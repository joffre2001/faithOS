package com.obysoft.faithOS.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.obysoft.faithOS.dto.SuperAdminAssignAdminRequest;
import com.obysoft.faithOS.dto.SuperAdminAuditResponse;
import com.obysoft.faithOS.dto.SuperAdminChurchResponse;
import com.obysoft.faithOS.dto.SuperAdminChurchStatusRequest;
import com.obysoft.faithOS.dto.SuperAdminOverviewResponse;
import com.obysoft.faithOS.dto.SuperAdminUserSummary;
import com.obysoft.faithOS.entity.AdminAuditLog;
import com.obysoft.faithOS.entity.Church;
import com.obysoft.faithOS.entity.Role;
import com.obysoft.faithOS.entity.User;
import com.obysoft.faithOS.exception.ResourceNotFoundException;
import com.obysoft.faithOS.repository.AdminAuditLogRepository;
import com.obysoft.faithOS.repository.ChurchRepository;
import com.obysoft.faithOS.repository.MinistryRepository;
import com.obysoft.faithOS.repository.UserRepository;

@Service
public class SuperAdminService {
    private final ChurchRepository churches;
    private final UserRepository users;
    private final MinistryRepository ministries;
    private final AdminAuditLogRepository auditLogs;

    public SuperAdminService(ChurchRepository churches, UserRepository users,
            MinistryRepository ministries, AdminAuditLogRepository auditLogs) {
        this.churches = churches;
        this.users = users;
        this.ministries = ministries;
        this.auditLogs = auditLogs;
    }

    @Transactional(readOnly = true)
    public SuperAdminOverviewResponse overview() {
        return new SuperAdminOverviewResponse(
                churches.count(), churches.countByActiveTrue(), users.count(),
                users.countByActiveTrue(), ministries.count(), auditLogs.count());
    }

    @Transactional(readOnly = true)
    public List<SuperAdminChurchResponse> churchList() {
        return churches.findAll().stream()
                .sorted(Comparator.comparing(Church::getName, String.CASE_INSENSITIVE_ORDER))
                .map(this::churchResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SuperAdminUserSummary> churchUsers(Long churchId) {
        requireChurch(churchId);
        return users.findAllByChurchIdOrderByFirstNameAsc(churchId).stream()
                .filter(user -> user.getRole() != Role.SUPER_ADMIN)
                .map(user -> new SuperAdminUserSummary(user.getId(),
                        user.getFirstName() + " " + user.getLastName(), user.getEmail(),
                        user.getRole(), Boolean.TRUE.equals(user.getActive())))
                .toList();
    }

    @Transactional
    public SuperAdminChurchResponse updateChurchStatus(Long churchId,
            SuperAdminChurchStatusRequest request) {
        Church church = requireChurch(churchId);
        church.setActive(request.active());
        churches.save(church);
        audit(request.active() ? "CHURCH_REACTIVATED" : "CHURCH_SUSPENDED",
                "CHURCH", churchId, request.reason());
        return churchResponse(church);
    }

    @Transactional
    public SuperAdminChurchResponse assignAdministrator(Long churchId,
            SuperAdminAssignAdminRequest request) {
        Church church = requireChurch(churchId);
        User replacement = users.findByIdAndChurchId(request.userId(), churchId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found in this church."));
        if (!Boolean.TRUE.equals(replacement.getActive())) {
            throw new IllegalArgumentException("Activate this user before assigning administrator access.");
        }
        if (replacement.getRole() == Role.SUPER_ADMIN) {
            throw new AccessDeniedException("A platform administrator cannot be assigned to a church role.");
        }
        users.findAllByChurchIdOrderByFirstNameAsc(churchId).stream()
                .filter(user -> user.getRole() == Role.CHURCH_ADMIN)
                .filter(user -> !user.getId().equals(replacement.getId()))
                .forEach(user -> user.setRole(Role.MEMBER));
        replacement.setRole(Role.CHURCH_ADMIN);
        users.save(replacement);
        audit("CHURCH_ADMIN_ASSIGNED", "CHURCH", churchId, request.reason());
        return churchResponse(church);
    }

    @Transactional(readOnly = true)
    public List<SuperAdminAuditResponse> auditLog() {
        return auditLogs.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 100)).stream()
                .map(log -> new SuperAdminAuditResponse(log.getId(), log.getActorEmail(),
                        log.getAction(), log.getTargetType(), log.getTargetId(),
                        log.getReason(), log.getCreatedAt()))
                .toList();
    }

    private Church requireChurch(Long id) {
        return churches.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Church not found."));
    }

    private SuperAdminChurchResponse churchResponse(Church church) {
        User admin = users.findAllByChurchIdOrderByFirstNameAsc(church.getId()).stream()
                .filter(user -> user.getRole() == Role.CHURCH_ADMIN)
                .findFirst().orElse(null);
        return new SuperAdminChurchResponse(church.getId(), church.getName(), church.getEmail(),
                church.getPhone(), church.getAddress(), church.getCnpj(), church.getPrincipalPastor(),
                Boolean.TRUE.equals(church.getActive()), users.countByChurchId(church.getId()),
                admin == null ? null : admin.getId(),
                admin == null ? null : admin.getFirstName() + " " + admin.getLastName(),
                admin == null ? null : admin.getEmail());
    }

    private void audit(String action, String targetType, Long targetId, String reason) {
        User actor = currentActor();
        AdminAuditLog log = new AdminAuditLog();
        log.setActorId(actor.getId());
        log.setActorEmail(actor.getEmail());
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setReason(reason.trim());
        auditLogs.save(log);
    }

    private User currentActor() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User actor = users.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found."));
        if (actor.getRole() != Role.SUPER_ADMIN) throw new AccessDeniedException("Super administrator access required.");
        return actor;
    }
}
