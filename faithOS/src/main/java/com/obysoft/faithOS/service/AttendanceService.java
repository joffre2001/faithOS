package com.obysoft.faithOS.service;

import java.time.LocalDate;
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
import com.obysoft.faithOS.dto.MinistryMemberResponse;
import com.obysoft.faithOS.entity.AttendanceSession;
import com.obysoft.faithOS.entity.AttendanceType;
import com.obysoft.faithOS.entity.Church;
import com.obysoft.faithOS.entity.User;
import com.obysoft.faithOS.exception.ResourceNotFoundException;
import com.obysoft.faithOS.repository.AttendanceSessionRepository;
import com.obysoft.faithOS.repository.UserRepository;

@Service
public class AttendanceService {
    private final AttendanceSessionRepository repository;
    private final UserRepository users;
    private final CurrentChurchService current;

    public AttendanceService(
            AttendanceSessionRepository repository,
            UserRepository users,
            CurrentChurchService current) {
        this.repository = repository;
        this.users = users;
        this.current = current;
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> all() {
        return repository.findAllByChurchIdOrderBySessionDateDesc(current.church().getId())
                .stream().map(this::response).toList();
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
        session.setAttendees(resolveAttendees(request.attendeeIds(), churchId));
    }

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
        List<MinistryMemberResponse> attendees = session.getAttendees().stream()
                .sorted((a, b) -> (a.getFirstName() + a.getLastName())
                        .compareToIgnoreCase(b.getFirstName() + b.getLastName()))
                .map(user -> new MinistryMemberResponse(
                        user.getId(), user.getFirstName(), user.getLastName(), user.getEmail()))
                .toList();
        return new AttendanceResponse(
                session.getId(), session.getTitle(), session.getType(), session.getSessionDate(), attendees);
    }
}
