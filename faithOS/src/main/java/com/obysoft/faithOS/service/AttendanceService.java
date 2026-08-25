package com.obysoft.faithOS.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.obysoft.faithOS.dto.AttendanceRequest;
import com.obysoft.faithOS.dto.AttendanceReportResponse;
import com.obysoft.faithOS.dto.AttendanceResponse;
import com.obysoft.faithOS.dto.AttendanceCheckInResponse;
import com.obysoft.faithOS.dto.MinistryMemberResponse;
import com.obysoft.faithOS.entity.AttendanceSession;
import com.obysoft.faithOS.entity.AttendanceCheckIn;
import com.obysoft.faithOS.entity.AttendanceStatus;
import com.obysoft.faithOS.entity.CheckInSource;
import com.obysoft.faithOS.entity.AttendanceType;
import com.obysoft.faithOS.entity.Church;
import com.obysoft.faithOS.entity.User;
import com.obysoft.faithOS.entity.Role;
import com.obysoft.faithOS.exception.ResourceNotFoundException;
import com.obysoft.faithOS.repository.AttendanceSessionRepository;
import com.obysoft.faithOS.repository.AttendanceCheckInRepository;
import com.obysoft.faithOS.repository.UserRepository;

@Service
public class AttendanceService {
    private final AttendanceSessionRepository repository;
    private final UserRepository users;
    private final CurrentChurchService current;
    private final AttendanceCheckInRepository checkIns;

    public AttendanceService(
            AttendanceSessionRepository repository,
            UserRepository users,
            CurrentChurchService current, AttendanceCheckInRepository checkIns) {
        this.repository = repository;
        this.users = users;
        this.current = current;
        this.checkIns = checkIns;
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> all() {
        User viewer = current.user();
        boolean detailed = viewer.getRole() != Role.MEMBER;
        return repository.findAllByChurchIdOrderBySessionDateDesc(viewer.getChurch().getId())
                .stream().map(session -> response(session, viewer, detailed)).toList();
    }

    @Transactional(readOnly = true)
    public AttendanceReportResponse report(LocalDate from, LocalDate to) {
        LocalDate safeTo = to == null ? LocalDate.now() : to;
        LocalDate safeFrom = from == null ? safeTo.minusDays(89) : from;
        if (safeFrom.isAfter(safeTo)) {
            throw new IllegalArgumentException("Report start date must not be after the end date.");
        }

        List<AttendanceSession> sessions = repository
                .findAllByChurchIdAndSessionDateBetweenOrderBySessionDateDesc(
                        current.church().getId(), safeFrom, safeTo);
        var sessionsByType = new EnumMap<AttendanceType, Integer>(AttendanceType.class);
        var checkInsByType = new EnumMap<AttendanceType, Integer>(AttendanceType.class);
        var uniqueAttendeeIds = new HashSet<Long>();
        int totalCheckIns = 0;

        for (AttendanceType type : AttendanceType.values()) {
            sessionsByType.put(type, 0);
            checkInsByType.put(type, 0);
        }
        for (AttendanceSession session : sessions) {
            int checkIns = session.getAttendees().size();
            sessionsByType.merge(session.getType(), 1, Integer::sum);
            checkInsByType.merge(session.getType(), checkIns, Integer::sum);
            totalCheckIns += checkIns;
            session.getAttendees().forEach(user -> uniqueAttendeeIds.add(user.getId()));
        }

        double average = sessions.isEmpty() ? 0 : (double) totalCheckIns / sessions.size();
        return new AttendanceReportResponse(
                safeFrom, safeTo, sessions.size(), totalCheckIns, uniqueAttendeeIds.size(),
                Math.round(average * 10.0) / 10.0, sessionsByType, checkInsByType);
    }

    @Transactional
    public AttendanceResponse create(AttendanceRequest request) {
        Church church = current.church();
        AttendanceSession session = new AttendanceSession();
        session.setChurch(church);
        apply(session, request, church.getId());
        return response(repository.save(session));
    }

    @Transactional
    public AttendanceResponse update(Long id, AttendanceRequest request) {
        Long churchId = current.church().getId();
        AttendanceSession session = find(id, churchId);
        apply(session, request, churchId);
        return response(repository.save(session));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(find(id, current.church().getId()));
    }

    private void apply(AttendanceSession session, AttendanceRequest request, Long churchId) {
        session.setTitle(request.title().trim());
        session.setType(request.type());
        session.setSessionDate(request.sessionDate());
        LocalTime opensAt = request.opensAt() == null ? LocalTime.of(7, 0) : request.opensAt();
        LocalTime onTimeUntil = request.onTimeUntil() == null ? LocalTime.of(8, 40) : request.onTimeUntil();
        if (onTimeUntil.isBefore(opensAt)) throw new IllegalArgumentException("On-time cutoff must be after opening time.");
        if (request.closesAt() != null && request.closesAt().isBefore(onTimeUntil)) throw new IllegalArgumentException("Closing time must be after the on-time cutoff.");
        session.setOpensAt(opensAt);
        session.setOnTimeUntil(onTimeUntil);
        session.setClosesAt(request.closesAt());
        session.setAttendees(resolveAttendees(request.attendeeIds(), churchId));
    }

    @Transactional
    public AttendanceCheckInResponse checkIn(User user, LocalDateTime occurredAt, CheckInSource source) {
        LocalDateTime at = occurredAt == null ? LocalDateTime.now() : occurredAt;
        List<AttendanceSession> candidates = repository.findAllByChurchIdAndSessionDateOrderByOpensAtDesc(user.getChurch().getId(), at.toLocalDate());
        AttendanceSession session = candidates.stream().filter(item -> {
            LocalTime opens = item.getOpensAt() == null ? LocalTime.of(7, 0) : item.getOpensAt();
            return !at.toLocalTime().isBefore(opens) && (item.getClosesAt() == null || !at.toLocalTime().isAfter(item.getClosesAt()));
        }).findFirst().orElseThrow(() -> new ResourceNotFoundException("No open attendance session was found for this time."));
        AttendanceCheckIn record = checkIns.findBySessionIdAndUserId(session.getId(), user.getId()).orElseGet(AttendanceCheckIn::new);
        if (record.getId() != null) return checkInResponse(record);
        record.setSession(session); record.setUser(user); record.setCheckedInAt(at); record.setSource(source);
        LocalTime cutoff = session.getOnTimeUntil() == null ? LocalTime.of(8, 40) : session.getOnTimeUntil();
        record.setStatus(at.toLocalTime().isAfter(cutoff) ? AttendanceStatus.LATE : AttendanceStatus.ON_TIME);
        session.getAttendees().add(user);
        repository.save(session);
        return checkInResponse(checkIns.save(record));
    }

    @Transactional
    public AttendanceCheckInResponse memberCheckIn() { return checkIn(current.user(), LocalDateTime.now(), CheckInSource.MEMBER); }

    private Set<User> resolveAttendees(Set<Long> ids, Long churchId) {
        if (ids == null || ids.isEmpty()) return new LinkedHashSet<>();
        List<User> found = users.findAllByIdInAndChurchId(ids, churchId);
        if (found.size() != ids.size()) {
            throw new ResourceNotFoundException("One or more attendees were not found in your church.");
        }
        return new LinkedHashSet<>(found);
    }

    private AttendanceSession find(Long id, Long churchId) {
        return repository.findByIdAndChurchId(id, churchId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance session not found."));
    }

    private AttendanceResponse response(AttendanceSession session) {
        return response(session, current.user(), true);
    }

    private AttendanceResponse response(AttendanceSession session, User viewer, boolean detailed) {
        List<MinistryMemberResponse> attendees = session.getAttendees().stream()
                .filter(user -> detailed || user.getId().equals(viewer.getId()))
                .sorted((a, b) -> (a.getFirstName() + a.getLastName())
                        .compareToIgnoreCase(b.getFirstName() + b.getLastName()))
                .map(user -> new MinistryMemberResponse(
                        user.getId(), user.getFirstName(), user.getLastName(), user.getEmail()))
                .toList();
        List<AttendanceCheckInResponse> records = checkIns.findAllBySessionIdOrderByCheckedInAt(session.getId()).stream()
                .filter(record -> detailed || record.getUser().getId().equals(viewer.getId()))
                .map(this::checkInResponse).toList();
        return new AttendanceResponse(session.getId(), session.getTitle(), session.getType(), session.getSessionDate(),
                session.getOpensAt(), session.getOnTimeUntil(), session.getClosesAt(), attendees, records);
    }

    private AttendanceCheckInResponse checkInResponse(AttendanceCheckIn record) {
        User user = record.getUser();
        return new AttendanceCheckInResponse(user.getId(), user.getMemberCode(), user.getFirstName()+" "+user.getLastName(), record.getCheckedInAt(), record.getStatus(), record.getSource());
    }
}
