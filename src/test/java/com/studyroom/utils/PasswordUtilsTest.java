package com.studyroom.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilsTest {
    @Test
    void shouldEncodeAndMatchPassword() {
        String encoded = PasswordUtils.encode("123456");
        assertEquals(64, encoded.length());
        assertTrue(PasswordUtils.matches("123456", encoded));
        assertFalse(PasswordUtils.matches("wrong", encoded));
    }
}
