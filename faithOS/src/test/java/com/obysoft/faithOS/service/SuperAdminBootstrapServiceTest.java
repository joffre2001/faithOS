package com.obysoft.faithOS.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;

import com.obysoft.faithOS.entity.Role;
import com.obysoft.faithOS.entity.User;
import com.obysoft.faithOS.repository.UserRepository;

class SuperAdminBootstrapServiceTest {
    private final UserRepository users = mock(UserRepository.class);

    @Test
    void promotesExistingActiveAccountOnlyWhenNoSuperAdminExists() throws Exception {
        User user = new User();
        user.setActive(true);
        user.setRole(Role.CHURCH_ADMIN);
        when(users.existsByRole(Role.SUPER_ADMIN)).thenReturn(false);
        when(users.findByEmailIgnoreCase("admin@example.org")).thenReturn(Optional.of(user));

        new SuperAdminBootstrapService(users, " ADMIN@example.org ")
                .run(new DefaultApplicationArguments(new String[0]));

        assertEquals(Role.SUPER_ADMIN, user.getRole());
        verify(users).save(user);
    }

    @Test
    void doesNothingWhenSuperAdminAlreadyExists() throws Exception {
        when(users.existsByRole(Role.SUPER_ADMIN)).thenReturn(true);

        new SuperAdminBootstrapService(users, "admin@example.org")
                .run(new DefaultApplicationArguments(new String[0]));

        verify(users, never()).findByEmailIgnoreCase("admin@example.org");
        verify(users, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void failsClosedWhenConfiguredAccountDoesNotExist() {
        when(users.existsByRole(Role.SUPER_ADMIN)).thenReturn(false);
        when(users.findByEmailIgnoreCase("missing@example.org")).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () ->
                new SuperAdminBootstrapService(users, "missing@example.org")
                        .run(new DefaultApplicationArguments(new String[0])));
    }
}
