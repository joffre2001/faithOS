package com.obysoft.faithOS.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.obysoft.faithOS.dto.UserUpdateRequest;
import com.obysoft.faithOS.entity.Church;
import com.obysoft.faithOS.entity.Role;
import com.obysoft.faithOS.entity.User;
import com.obysoft.faithOS.repository.ChurchRepository;
import com.obysoft.faithOS.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTenantTest {
    @Mock UserRepository users;
    @Mock ChurchRepository churches;
    @Mock PasswordEncoder passwordEncoder;
    @Mock PasswordResetService passwordResetService;
    private UserService service;

    @BeforeEach
    void setUp() {
        service = new UserService(users, churches, passwordEncoder, passwordResetService);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin@church.test", "ignored"));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void churchAdministratorOnlyListsUsersFromTheirChurch() {
        Church church = church(10L);
        User admin = user(1L, "admin@church.test", Role.CHURCH_ADMIN, church);
        User member = user(2L, "member@church.test", Role.MEMBER, church);
        PageRequest pageable = PageRequest.of(0, 10);
        when(users.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
        when(users.findAllByChurchId(church.getId(), pageable))
                .thenReturn(new PageImpl<>(List.of(member), pageable, 1));

        var result = service.findAll(pageable, "");

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).extracting("email").containsExactly(member.getEmail());
        verify(users, never()).findAll(pageable);
    }

    @Test
    void churchAdministratorCannotUpdateAUserFromAnotherChurch() {
        Church ownChurch = church(10L);
        Church otherChurch = church(20L);
        User admin = user(1L, "admin@church.test", Role.CHURCH_ADMIN, ownChurch);
        User otherUser = user(2L, "member@other.test", Role.MEMBER, otherChurch);
        when(users.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
        when(users.findById(otherUser.getId())).thenReturn(Optional.of(otherUser));

        assertThatThrownBy(() -> service.updateUser(otherUser.getId(), new UserUpdateRequest()))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("another church");
        verify(users, never()).save(otherUser);
    }

    @Test
    void churchAdministratorCannotInviteAUserFromAnotherChurch() {
        Church ownChurch = church(10L);
        Church otherChurch = church(20L);
        User admin = user(1L, "admin@church.test", Role.CHURCH_ADMIN, ownChurch);
        User otherUser = user(2L, "member@other.test", Role.MEMBER, otherChurch);
        when(users.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
        when(users.findById(otherUser.getId())).thenReturn(Optional.of(otherUser));

        assertThatThrownBy(() -> service.sendInvitation(otherUser.getId()))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("another church");
        verify(passwordResetService, never()).invite(otherUser.getEmail());
    }

    @Test
    void churchAdministratorCanInviteAnActiveUserFromTheirChurch() {
        Church church = church(10L);
        User admin = user(1L, "admin@church.test", Role.CHURCH_ADMIN, church);
        User member = user(2L, "member@church.test", Role.MEMBER, church);
        when(users.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
        when(users.findById(member.getId())).thenReturn(Optional.of(member));

        service.sendInvitation(member.getId());

        verify(passwordResetService).invite(member.getEmail());
    }

    private Church church(Long id) {
        Church church = mock(Church.class);
        when(church.getId()).thenReturn(id);
        return church;
    }

    private User user(Long id, String email, Role role, Church church) {
        User user = new User();
        user.setId(id);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail(email);
        user.setRole(role);
        user.setChurch(church);
        user.setActive(true);
        return user;
    }
}
