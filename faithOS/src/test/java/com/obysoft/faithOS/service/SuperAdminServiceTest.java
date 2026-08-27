package com.obysoft.faithOS.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.obysoft.faithOS.dto.SuperAdminAssignAdminRequest;
import com.obysoft.faithOS.dto.SuperAdminChurchStatusRequest;
import com.obysoft.faithOS.entity.AdminAuditLog;
import com.obysoft.faithOS.entity.Church;
import com.obysoft.faithOS.entity.Role;
import com.obysoft.faithOS.entity.User;
import com.obysoft.faithOS.repository.AdminAuditLogRepository;
import com.obysoft.faithOS.repository.ChurchRepository;
import com.obysoft.faithOS.repository.MinistryRepository;
import com.obysoft.faithOS.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class SuperAdminServiceTest {
    @Mock ChurchRepository churches;
    @Mock UserRepository users;
    @Mock MinistryRepository ministries;
    @Mock AdminAuditLogRepository auditLogs;
    private SuperAdminService service;

    @BeforeEach
    void setUp() {
        service = new SuperAdminService(churches, users, ministries, auditLogs);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("platform@example.org", "ignored"));
        User actor = user(1L, Role.SUPER_ADMIN);
        actor.setEmail("platform@example.org");
        when(users.findByEmailIgnoreCase("platform@example.org")).thenReturn(Optional.of(actor));
    }

    @AfterEach
    void clearContext() { SecurityContextHolder.clearContext(); }

    @Test
    void suspensionIsPersistedAndAuditedWithReason() {
        Church church = church(20L);
        when(churches.findById(20L)).thenReturn(Optional.of(church));
        when(users.findAllByChurchIdOrderByFirstNameAsc(20L)).thenReturn(List.of());

        service.updateChurchStatus(20L,
                new SuperAdminChurchStatusRequest(false, "Repeated policy violations"));

        verify(church).setActive(false);
        verify(churches).save(church);
        ArgumentCaptor<AdminAuditLog> log = ArgumentCaptor.forClass(AdminAuditLog.class);
        verify(auditLogs).save(log.capture());
        assertThat(log.getValue().getAction()).isEqualTo("CHURCH_SUSPENDED");
        assertThat(log.getValue().getReason()).isEqualTo("Repeated policy violations");
    }

    @Test
    void assigningAdministratorDemotesPreviousAdministrator() {
        Church church = church(20L);
        User previous = user(2L, Role.CHURCH_ADMIN);
        User replacement = user(3L, Role.PASTOR);
        when(churches.findById(20L)).thenReturn(Optional.of(church));
        when(users.findByIdAndChurchId(3L, 20L)).thenReturn(Optional.of(replacement));
        when(users.findAllByChurchIdOrderByFirstNameAsc(20L))
                .thenReturn(List.of(previous, replacement));
        when(users.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.assignAdministrator(20L,
                new SuperAdminAssignAdminRequest(3L, "Leadership transition approved"));

        assertThat(previous.getRole()).isEqualTo(Role.MEMBER);
        assertThat(replacement.getRole()).isEqualTo(Role.CHURCH_ADMIN);
        verify(users).save(replacement);
        verify(auditLogs).save(any(AdminAuditLog.class));
    }

    private Church church(Long id) {
        Church church = mock(Church.class);
        when(church.getId()).thenReturn(id);
        when(church.getName()).thenReturn("Test Church");
        when(church.getActive()).thenReturn(true);
        return church;
    }

    private User user(Long id, Role role) {
        User user = new User();
        user.setId(id);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("user" + id + "@example.org");
        user.setRole(role);
        user.setActive(true);
        return user;
    }
}
