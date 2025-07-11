package com.moviemood.dao;

import com.moviemood.bean.User;
import com.moviemood.exceptions.UserAlreadyExistsException;
import com.moviemood.util.TestDatabaseHelper;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Tests for dao classes using H2 in-memory database.
 */
public class UserDaoTest {

    private BasicDataSource testDataSource;
    private UserDao userDao;

    /**
     * Sets up fresh H2 database before each test.
     */
    @Before
    public void setUp() throws SQLException {
        testDataSource = TestDatabaseHelper.createTestDataSource();
        TestDatabaseHelper.validateTestDatabase(testDataSource);

        // Create users table
        TestDatabaseHelper.createUsersTable(testDataSource);
        userDao = new UserDao(testDataSource);
    }

    /**
     * Cleans up database after each test.
     */
    @After
    public void tearDown() throws SQLException {
        if (testDataSource != null) {
            TestDatabaseHelper.dropAllTables(testDataSource);
            testDataSource.close();
        }
    }

    /**
     * Helper method to insert a test user directly into database.
     */
    private void insertTestUserDirectly(String username, String email, String passwordHash) throws SQLException {
//        TestDatabaseHelper.clearUsersTable(testDataSource);
        String sql = "INSERT INTO users (username, email, password_hash, is_verified) VALUES (?, ?, ?, ?)";
        try (var conn = testDataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, passwordHash);
            stmt.setBoolean(4, false);
            stmt.executeUpdate();
        }
    }

    /**
     * Test that we can insert and retrieve a user.
     */
    @Test
    public void testBasicUserOperations() throws UserAlreadyExistsException {
        userDao.insertUser("testuser", "test@gmail.com", "hashedpassword123");
        User user = userDao.getUserByEmail("test@gmail.com");

        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("test@gmail.com", user.getEmail());
        assertEquals( "hashedpassword123", user.getHashedPassword());
        assertFalse( user.isVerified());
    }

    @Test
    public void testGetUserByEmail1_Basic() throws SQLException {
        insertTestUserDirectly("testuser", "test@gmail.com", "hashedpassword123");
        User user = userDao.getUserByEmail("test@gmail.com");

        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("test@gmail.com", user.getEmail());
        assertEquals("hashedpassword123", user.getHashedPassword());
        assertFalse(user.isVerified());
        assertNull(user.getVerificationCode());
        assertNull(user.getToken());

        // Test non existing user
        User user2 = userDao.getUserByEmail("ragaca@gmail.com");
        assertNull("Should return null", user2);

    }


    @Test
    public void testGetUserByEmail2_VerifiedUser() throws SQLException {
        String sql = "INSERT INTO users (username, email, password_hash, is_verified, verification_code) VALUES (?, ?, ?, ?, ?)";
        try (var conn = testDataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "gooduser");
            stmt.setString(2, "gooduser@gmail.com");
            stmt.setString(3, "hashedpassword456");
            stmt.setBoolean(4, true);
            stmt.setString(5, "123456");
            stmt.executeUpdate();
        }

        User user = userDao.getUserByEmail("gooduser@gmail.com");

        assertNotNull(user);
        assertEquals("gooduser", user.getUsername());
        assertTrue(user.isVerified());
        assertEquals("123456", user.getVerificationCode());
    }

    @Test
    public void testGetUserByEmail3_NullAndEmpty() {
        User user1 = userDao.getUserByEmail(null);
        assertNull("Should handle null email", user1);

        User user2 = userDao.getUserByEmail("");
        assertNull("Should handle empty email", user2);
    }

    @Test
    public void testGetUserByUsername() throws SQLException {
        insertTestUserDirectly("testuser", "test@gmail.com", "hashedpassword123");
        User user = userDao.getUserByUsername("testuser");

        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("test@gmail.com", user.getEmail());
        assertEquals("hashedpassword123", user.getHashedPassword());

        // Test non-existing users
        User user2 = userDao.getUserByUsername("nonexistentuser");
        assertNull(user2);
    }


    @Test
    public void testInsertUser1_Success() throws UserAlreadyExistsException {
        userDao.insertUser("newuser", "new@hi.com", "something");
        User insertedUser = userDao.getUserByEmail("new@hi.com");

        assertNotNull(insertedUser);
        assertEquals("newuser", insertedUser.getUsername());
        assertEquals("new@hi.com", insertedUser.getEmail());
        assertEquals("something", insertedUser.getHashedPassword());
        assertFalse(insertedUser.isVerified());
        assertNull(insertedUser.getToken());
        assertNull(insertedUser.getVerificationCode());
        assertNull(insertedUser.getVerificationCodeExpiry());
    }

    @Test(expected = UserAlreadyExistsException.class)
    public void testInsertUser2_DuplicateUsername() throws SQLException, UserAlreadyExistsException {
        insertTestUserDirectly("duplicateuser", "first@first.com", "hash1");
        userDao.insertUser("duplicateuser", "second@first.com", "hash2");
    }

    @Test
    public void testInsertUser3_ValidatesUniqueConstraints() throws UserAlreadyExistsException {
        userDao.insertUser("user1", "user1@gmail.com", "paroli1");
        userDao.insertUser("user2", "user2@gmail.com", "paroli2");

        User user1 = userDao.getUserByEmail("user1@gmail.com");
        User user2 = userDao.getUserByEmail("user2@gmail.com");

        assertNotNull(user1);
        assertNotNull(user2);
        assertNotEquals(user1.getId(), user2.getId());
    }


    @Test
    public void testInsertUser4_WithVerification_Success() throws UserAlreadyExistsException {
        Timestamp expiry = new Timestamp(System.currentTimeMillis() + 3600000);
        userDao.insertUser("verifyuser", "verify@kai.com", "kai", "123456", expiry);
        User insertedUser = userDao.getUserByEmail("verify@kai.com");

        assertNotNull(insertedUser);
        assertEquals( "verifyuser", insertedUser.getUsername());
        assertEquals("verify@kai.com", insertedUser.getEmail());
        assertEquals( "kai", insertedUser.getHashedPassword());
        assertFalse( insertedUser.isVerified());
        assertEquals("123456", insertedUser.getVerificationCode());
        assertEquals(expiry, insertedUser.getVerificationCodeExpiry());
    }


    @Test
    public void testInsertUser5_WithVerification_PastExpiry() throws UserAlreadyExistsException {
        Timestamp pastExpiry = new Timestamp(System.currentTimeMillis() - 3600000);
        userDao.insertUser("vigaca", "vigca@gmail.com", "kargi", "789012", pastExpiry);

        User user = userDao.getUserByEmail("vigca@gmail.com");
        assertNotNull(user);
        assertEquals("789012", user.getVerificationCode());
        assertEquals(pastExpiry, user.getVerificationCodeExpiry());
    }

    @Test(expected = UserAlreadyExistsException.class)
    public void testInsertUser6_DuplicateEmail() throws SQLException, UserAlreadyExistsException {
        insertTestUserDirectly("user1", "duplicate@test.com", "l");
        userDao.insertUser("user2", "duplicate@test.com", "k");
    }

    @Test(expected = UserAlreadyExistsException.class)
    public void testInsertUser7_DuplicateEmailVerified() throws SQLException, UserAlreadyExistsException {
        insertTestUserDirectly("existing", "existing@test.com", "1");
        Timestamp expiry = new Timestamp(System.currentTimeMillis() + 3600000);
        userDao.insertUser("newuser", "existing@test.com", "t", "123456", expiry);
    }

    @Test(expected = UserAlreadyExistsException.class)
    public void testInsertUser7_DuplicateUsername() throws SQLException, UserAlreadyExistsException {
        insertTestUserDirectly("existing", "existing@test.com", "hash");
        Timestamp expiry = new Timestamp(System.currentTimeMillis() + 3600000);
        userDao.insertUser("existing", "new@test.com", "hash2", "123456", expiry);
    }




    @Test
    public void testUpdateRememberToken1() throws SQLException {
        insertTestUserDirectly("gaga", "gaga@gmail.com", "haha");
        userDao.updateRememberToken("gaga", "abc123token456");

        User user = userDao.getUserByUsername("gaga");
        assertNotNull(user);
        assertEquals("abc123token456", user.getToken());

        User userByEmail = userDao.getUserByEmail("gaga@gmail.com");
        assertEquals("abc123token456", userByEmail.getToken());
    }

    @Test
    public void testUpdateRememberToken2_UpdateExistingToken() throws SQLException {
        insertTestUserDirectly("chika", "chika@chika", "chika");
        userDao.updateRememberToken("chika", "start");
        userDao.updateRememberToken("chika", "new");
        User user = userDao.getUserByUsername("chika");
        assertEquals("new", user.getToken());
    }

    @Test(expected = RuntimeException.class)
    public void testUpdateRememberToken3_NonExistentUser() {
        userDao.updateRememberToken("nonexistent", "token");
    }


    @Test
    public void testGetUserByToken1() throws SQLException {
        insertTestUserDirectly("levani", "levani@levani", "hashedpassword");
        userDao.updateRememberToken("levani", "searchtoken123");
        User user = userDao.getUserByToken("searchtoken123");

        assertNotNull(user);
        assertEquals("levani", user.getUsername());
        assertEquals("levani@levani", user.getEmail());
        assertEquals("searchtoken123", user.getToken());
    }

    @Test
    public void testGetUserByToken2_Edge() {
        User user = userDao.getUserByToken("nonexistenttoken");
        assertNull(user);

        User user2 = userDao.getUserByToken(null);
        assertNull(user2);

        User user3 = userDao.getUserByToken("");
        assertNull("Should return null for empty token", user3);
    }


    @Test
    public void testVerifyUser1() throws SQLException, UserAlreadyExistsException {
        Timestamp expiry = new Timestamp(System.currentTimeMillis() + 3600000);
        userDao.insertUser("verifytest", "verify@test.com", "hashedpassword", "123456", expiry);
        User user = userDao.getUserByEmail("verify@test.com");
        int userId = user.getId();

        assertFalse(user.isVerified());
        assertEquals("123456", user.getVerificationCode());
        assertNotNull(user.getVerificationCodeExpiry());

        boolean result = userDao.verifyUser(userId);
        assertTrue(result);
        User verifiedUser = userDao.getUserByEmail("verify@test.com");
        assertTrue(verifiedUser.isVerified());
        assertNull(verifiedUser.getVerificationCode());
        assertNull(verifiedUser.getVerificationCodeExpiry());
    }

    @Test
    public void testVerifyUser2_NonExistentUser() {
        boolean result = userDao.verifyUser(99999);
        assertFalse(result);
    }

    @Test
    public void testVerifyUser3_AlreadyVerified() throws SQLException, UserAlreadyExistsException {
        Timestamp expiry = new Timestamp(System.currentTimeMillis() + 3600000);
        userDao.insertUser("alreadyverified", "already@test.com", "alreadyy", "123456", expiry);
        User user = userDao.getUserByEmail("already@test.com");
        userDao.verifyUser(user.getId());
        boolean result = userDao.verifyUser(user.getId());

        assertTrue(result);

        User stillVerified = userDao.getUserByEmail("already@test.com");
        assertTrue(stillVerified.isVerified());
    }

    @Test
    public void testUpdateVerificationCode1() throws SQLException {
        insertTestUserDirectly("kargiuseri", "kai@kai", "hashedpassword");
        Timestamp newExpiry = new Timestamp(System.currentTimeMillis() + 7200000);

        boolean result = userDao.updateVerificationCode("kai@kai", "654321", newExpiry);

        assertTrue(result);

        User user = userDao.getUserByEmail("kai@kai");
        assertEquals("654321", user.getVerificationCode());
        assertEquals( newExpiry, user.getVerificationCodeExpiry());
    }

    @Test
    public void testUpdateVerificationCode2() {
        Timestamp expiry = new Timestamp(System.currentTimeMillis() + 3600000);
        boolean result = userDao.updateVerificationCode("bolo@bolo", "paroli", expiry);
        assertFalse(result);
    }

    @Test
    public void testWithClosedDataSource() throws SQLException {
        testDataSource.close();

        try {
            userDao.getUserByEmail("test@test.com");
            fail("Should throw RuntimeException");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Failed to make connection"));
        }
    }


    @Test
    public void testSearchUserByQuery1() throws SQLException {
        insertTestUserDirectly("gio", "gio@gio.com", "ra");
        insertTestUserDirectly("lali", "lali@lali.com", "ra2");
        insertTestUserDirectly("nata", "nata@nata.com", "ra3");

        List<User> results = userDao.searchUserByQuery("gio");

        assertEquals(1, results.size());
        assertEquals( "gio", results.get(0).getUsername());
    }

    @Test
    public void testSearchUserByQuery2_PartialMatch() throws SQLException {
        insertTestUserDirectly("johnson", "johnson@gmail.com", "ra");
        insertTestUserDirectly("johnny", "johnny@yahoo.com", "ra2");
        insertTestUserDirectly("john", "john@tahoo.com", "ra3");

        List<User> results = userDao.searchUserByQuery("john");

        assertEquals(3, results.size());

        for (User user : results) {
            assertTrue(user.getUsername().toLowerCase().contains("john"));
        }
    }


    @Test
    public void testSearchUserByQuery3_EmptyQuery() throws SQLException {
        insertTestUserDirectly("user1", "user1@someth.com", "la");
        insertTestUserDirectly("user2", "user2@someth.com", "bla");

        List<User> results = userDao.searchUserByQuery("");

        assertEquals(2, results.size());
    }



    @Test
    public void testSearchUserByQuery4_Verification() throws SQLException, UserAlreadyExistsException {
        Timestamp expiry = new Timestamp(System.currentTimeMillis() + 3600000);
        userDao.insertUser("verifieduser", "verified@gmail.com", "ka", "123456", expiry);

        List<User> results = userDao.searchUserByQuery("verified");

        assertEquals(1, results.size());
        User user = results.get(0);
        assertEquals("123456", user.getVerificationCode());
        assertEquals(expiry, user.getVerificationCodeExpiry());
        assertFalse(user.isVerified());
    }



    @Test
    public void testUpdateUsername1() throws SQLException, UserAlreadyExistsException {
        insertTestUserDirectly("oldname", "vigac@cigac.com", "bla");
        User user = userDao.getUserByEmail("vigac@cigac.com");

        boolean result = userDao.updateUsername(user.getId(), "newname");

        assertTrue(result);

        User updatedUser = userDao.getUserByEmail("vigac@cigac.com");
        assertEquals( "newname", updatedUser.getUsername());

        User userByNewName = userDao.getUserByUsername("newname");
        assertNotNull(userByNewName);

        User userByOldName = userDao.getUserByUsername("oldname");
        assertNull(userByOldName);
    }

    @Test(expected = UserAlreadyExistsException.class)
    public void testUpdateUsername2_DuplicateUsername() throws SQLException, UserAlreadyExistsException {
        insertTestUserDirectly("gio", "gio@gio", "bla");
        insertTestUserDirectly("gio2", "gio2@gio2", "blu");
        User user1 = userDao.getUserByEmail("gio@gio");

        userDao.updateUsername(user1.getId(), "gio2");
    }


    @Test
    public void testUpdatePassword1() throws SQLException {
        insertTestUserDirectly("testuser", "test@test.com", "hey");
        User user = userDao.getUserByEmail("test@test.com");

        boolean result = userDao.updatePassword(user.getId(), "new");

        assertTrue(result);

        User updatedUser = userDao.getUserByEmail("test@test.com");
        assertEquals("new", updatedUser.getHashedPassword());
    }

    @Test
    public void testUpdateProfilePicture1() throws SQLException {
        insertTestUserDirectly("picturee", "pic@pic.com", "hash");
        User user = userDao.getUserByEmail("pic@pic.com");

        boolean result = userDao.updateProfilePicture(user.getId(), "/path/to/picture.jpg");

        assertTrue(result);

        User updatedUser = userDao.getUserByEmail("pic@pic.com");
        assertEquals("/path/to/picture.jpg", updatedUser.getProfilePicture());
    }

    @Test
    public void testUpdateProfilePicture2_Null() throws SQLException {
        insertTestUserDirectly("nulll", "nullable@null.com", "bla");
        User user = userDao.getUserByEmail("nullable@null.com");

        boolean result = userDao.updateProfilePicture(user.getId(), null);

        assertTrue(result);

        User updatedUser = userDao.getUserByEmail("nullable@null.com");
        assertNull(updatedUser.getProfilePicture());
    }


}