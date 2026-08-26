package com.obysoft.faithOS.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.obysoft.faithOS.entity.User;
import com.obysoft.faithOS.exception.InvalidCredentialsException;
import com.obysoft.faithOS.repository.UserRepository;
import com.obysoft.faithOS.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthServiceGoogleTest {
    @Mock UserRepository users;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;
    private AuthService service;

    @BeforeEach
    void setUp() {
        service = new AuthService(users, passwordEncoder, jwtService);
    }

    @Test
    void verifiedGoogleEmailCanSignIntoAnExistingActiveAccount() {
        User user = new User();
        user.setEmail("member@church.test");
        user.setActive(true);
        user.setMustChangePassword(true);
        when(users.findByEmailIgnoreCase("member@church.test")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("signed-jwt");

        String token = service.loginWithVerifiedGoogleEmail(" MEMBER@CHURCH.TEST ");

        assertThat(token).isEqualTo("signed-jwt");
        assertThat(user.getMustChangePassword()).isFalse();
        verify(users).save(user);
    }

    @Test
    void unknownGoogleEmailCannotCreateOrEnterAnAccount() {
        when(users.findByEmailIgnoreCase("unknown@example.test")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loginWithVerifiedGoogleEmail("unknown@example.test"))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("No active FaithOS account");
    }
}
