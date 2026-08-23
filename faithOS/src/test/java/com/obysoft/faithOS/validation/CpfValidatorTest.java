package com.obysoft.faithOS.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CpfValidatorTest {
    private final CpfValidator validator = new CpfValidator();

    @Test void acceptsValidFormattedAndPlainCpf() {
        assertTrue(validator.isValid("529.982.247-25", null));
        assertTrue(validator.isValid("52998224725", null));
    }

    @Test void rejectsInvalidChecksumAndRepeatedDigits() {
        assertFalse(validator.isValid("529.982.247-24", null));
        assertFalse(validator.isValid("111.111.111-11", null));
    }
}
