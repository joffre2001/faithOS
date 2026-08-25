package com.obysoft.faithOS.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

class SensitiveDataServiceTest {
    private final SensitiveDataService service = new SensitiveDataService(
            "security-test-key-that-is-longer-than-thirty-two-characters");

    @Test
    void encryptsWithRandomAuthenticatedCiphertext() {
        String first = service.encrypt("sensitive value");
        String second = service.encrypt("sensitive value");
        assertThat(first).startsWith("enc:v1:").isNotEqualTo(second);
        assertThat(service.decrypt(first)).isEqualTo("sensitive value");
    }

    @Test
    void rejectsTamperedCiphertext() {
        String encrypted = service.encrypt("sensitive value");
        String tampered = encrypted.substring(0, encrypted.length() - 2) + "AA";
        assertThatThrownBy(() -> service.decrypt(tampered))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void createsStableKeyedHashes() {
        assertThat(service.hash("12345678909")).isEqualTo(service.hash("12345678909"));
        assertThat(service.hash("12345678909")).isNotEqualTo(service.hash("98765432100"));
    }
}
