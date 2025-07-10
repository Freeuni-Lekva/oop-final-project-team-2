package com.moviemood.service;

import com.moviemood.services.EmailService;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

/**
 * This is unit tests for EmailService class.
 *
 * Covers:
 * - Code generation
 * - Input validation
 * - Email format validation
 * - Valid input acceptance
 */

public class EmailServiceTest {

    private EmailService emailService;

    @Before
    public void setUp() {
        emailService = new EmailService();
        System.setProperty("test.mode", "true");
    }

    @After
    public void tearDown() {
        System.clearProperty("test.mode");
    }


    @Test
    public void testGenerateVerificationCode_SixDigits() {
        int code = emailService.generateVerificationCode();

        assertTrue("Code should be at least 100000", code >= 100000);
        assertTrue("Code should be at most 999999", code <= 999999);

        String codeStr = String.valueOf(code);
        assertEquals("Code should be exactly 6 digits", 6, codeStr.length());

        assertTrue("Code should contain only digits", codeStr.matches("\\d{6}"));
    }

    @Test
    public void testGenerateVerificationCode_Random() {
        int code1 = emailService.generateVerificationCode();
        int code2 = emailService.generateVerificationCode();
        int code3 = emailService.generateVerificationCode();

        assertNotEquals("Code1 and Code2 should be different", code1, code2);
        assertNotEquals("Code2 and Code3 should be different", code2, code3);
        assertNotEquals("Code1 and Code3 should be different", code1, code3);
    }

    @Test
    public void testSendVerificationEmail_RejectEmptyAndNullEmail() {
        assertFalse("Should reject null email",
                emailService.sendVerificationEmail(null, "123456", "user"));
        assertFalse("Should reject empty email",
                emailService.sendVerificationEmail("", "123456", "user"));
    }

    @Test
    public void testSendVerificationEmail_RejectInvalidEmailFormat() {
        assertFalse("Should reject email without @",
                emailService.sendVerificationEmail("invalidemail", "123456", "user"));
        assertFalse("Should reject email with space",
                emailService.sendVerificationEmail("invalid email@test.com", "123456", "user"));
        assertFalse("Should reject email without domain",
                emailService.sendVerificationEmail("test@", "123456", "user"));
    }

    @Test
    public void testSendVerificationEmail_RejectNullCode() {
        assertFalse("Should reject null code",
                emailService.sendVerificationEmail("test@test.com", null, "user"));
    }

    @Test
    public void testSendVerificationEmail_RejectEmptyCode() {
        assertFalse("Should reject empty code",
                emailService.sendVerificationEmail("test@test.com", "", "user"));
    }

    @Test
    public void testSendVerificationEmail_RejectNullUsername() {
        assertFalse("Should reject null username",
                emailService.sendVerificationEmail("test@test.com", "123456", null));
    }

    @Test
    public void testSendVerificationEmail_RejectEmptyUsername() {
        assertFalse("Should reject empty username",
                emailService.sendVerificationEmail("test@test.com", "123456", ""));
    }

    @Test
    public void testSendVerificationEmail_AcceptValidInputs() {
        boolean result = emailService.sendVerificationEmail("valid@test.com", "123456", "validUser");
        assertTrue("Should accept valid inputs", result);
    }

    @Test
    public void testSendVerificationEmail_AcceptEmailFormats() {
        String[] validEmails = {
                "user@example.com",
                "test.email@hey.ok.naxvamdis",
                "user+tag@gmail.com",
                "user123@test-d.com",
                "a@b.coo"
        };

        for (String email : validEmails) {
            boolean result = emailService.sendVerificationEmail(email, "123456", "testUser");
            assertTrue("Should accept valid email: " + email, result);
        }
    }

}