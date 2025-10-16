package org.example.common;

import org.junit.jupiter.api.Test;
import org.example.common.util.JwtUtil;
import org.example.common.util.CommonUtil;

import static org.junit.jupiter.api.Assertions.*;

class CommonApplicationTests {

    @Test
    void testJwtUtil() {
        JwtUtil jwtUtil = new JwtUtil("test-secret", 86400000L);
        assertNotNull(jwtUtil);
    }
    
    @Test
    void testCommonUtil() {
        String email = "test@example.com";
        assertTrue(CommonUtil.isValidEmail(email));
        
        String invalidEmail = "invalid-email";
        assertFalse(CommonUtil.isValidEmail(invalidEmail));
        
        String code = CommonUtil.generateVerificationCode();
        assertNotNull(code);
        assertEquals(6, code.length());
    }

}
