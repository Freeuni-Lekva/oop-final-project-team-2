package com.moviemood.bean;

import org.junit.Test;
import static org.junit.Assert.*;
import java.sql.Timestamp;

/**
 * Unit tests for User class.
 * This is a simple bean test that ensures all properties
 * can be set and retrieved correctly.
 */


public class UserTest {

    @Test
    public void testUserBean() {
        // Test constructor and getters
        User user = new User(1, "lali", "lali@lali.com", "bla", "token");

        assertEquals(1, user.getId());
        assertEquals("lali", user.getUsername());
        assertEquals("lali@lali.com", user.getEmail());
        assertEquals("bla", user.getHashedPassword());
        assertEquals("token", user.getToken());

        user.setUsername("newname");
        assertEquals("newname", user.getUsername());

        user.setHashedPassword("newpass");
        assertEquals("newpass", user.getHashedPassword());

        user.setProfilePicture("picturee.jpg");
        assertEquals("picturee.jpg", user.getProfilePicture());

        user.setVerified(true);
        assertTrue(user.isVerified());

        user.setVerificationCode("123456");
        assertEquals("123456", user.getVerificationCode());

        Timestamp expiry = new Timestamp(System.currentTimeMillis());
        user.setVerificationCodeExpiry(expiry);
        assertEquals(expiry, user.getVerificationCodeExpiry());

        assertNotNull(user.getProfilePicture());
    }
}