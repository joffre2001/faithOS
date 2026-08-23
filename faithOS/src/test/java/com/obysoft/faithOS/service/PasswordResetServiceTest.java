package com.obysoft.faithOS.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.obysoft.faithOS.entity.PasswordResetToken;
import com.obysoft.faithOS.entity.User;
import com.obysoft.faithOS.exception.InvalidCredentialsException;
import com.obysoft.faithOS.repository.PasswordResetTokenRepository;
import com.obysoft.faithOS.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {
    @Mock UserRepository users;
    @Mock PasswordResetTokenRepository tokens;
    @Mock PasswordEncoder encoder;
    @Mock ApplicationContext applicationContext;
    private PasswordResetService service;

    @BeforeEach
    void setUp() {
        service = new PasswordResetService(users, tokens, encoder, applicationContext,
                "no-reply@faithos.test", "");
    }

    @Test
    void requestStoresOnlyAHashAndReplacesPreviousTokens() {
        User user = activeUser();
        when(users.findByEmail("member@church.test")).thenReturn(Optional.of(user));

        service.request(" MEMBER@church.test ");

        verify(tokens).deleteAllByUser(user);
        ArgumentCaptor<PasswordResetToken> captor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokens).save(captor.capture());
        PasswordResetToken saved = captor.getValue();
        assertThat(saved.getTokenHash()).hasSize(64).matches("[0-9a-f]{64}");
        assertThat(saved.getExpiresAt()).isAfter(LocalDateTime.now().plusMinutes(29));
        assertThat(saved.getUser()).isSameAs(user);
    }

    @Test
    void confirmChangesPasswordAndConsumesAllUserTokens() throws Exception {
        String raw = "valid-one-time-code";
        User user = activeUser();
        PasswordResetToken reset = new PasswordResetToken();
        reset.setUser(user);
        reset.setTokenHash(hash(raw));
        reset.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        when(tokens.findByTokenHash(hash(raw))).thenReturn(Optional.of(reset));
        when(encoder.encode("a-new-secure-password")).thenReturn("encoded-password");

        service.confirm(raw, "a-new-secure-password");

        assertThat(user.getPassword()).isEqualTo("encoded-password");
        assertThat(user.getMustChangePassword()).isFalse();
        verify(users).save(user);
        verify(tokens).deleteAllByUser(user);
    }

    @Test
    void expiredTokenCannotResetPassword() throws Exception {
        String raw = "expired-code";
        PasswordResetToken reset = new PasswordResetToken();
        reset.setUser(activeUser());
        reset.setTokenHash(hash(raw));
        reset.setExpiresAt(LocalDateTime.now().minusSeconds(1));
        when(tokens.findByTokenHash(hash(raw))).thenReturn(Optional.of(reset));

        assertThatThrownBy(() -> service.confirm(raw, "a-new-secure-password"))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("invalid or expired");
        verify(users, org.mockito.Mockito.never()).save(any());
    }

    private User activeUser() {
        User user = new User();
        user.setEmail("member@church.test");
        user.setActive(true);
        user.setMustChangePassword(true);
        return user;
    }

    private String hash(String value) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                .digest(value.getBytes(StandardCharsets.UTF_8)));
    }
}
