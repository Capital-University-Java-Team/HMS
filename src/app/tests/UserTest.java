package app.tests;

import app.domain.Role;
import app.domain.User;
import java.util.Objects;
import java.util.UUID;

public class UserTest {

    private static class TestUser extends User {
        public TestUser(String username, String name, String phone, String passwordHash, Role role) {
            super(username, name, phone, passwordHash, role);
        }

        public TestUser(UUID id, String username, String name, String phone, String passwordHash, Role role) {
            super(id, username, name, phone, passwordHash, role);
        }
    }

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== Running Simplified User Entity Tests ===\n");

        testCreateNewUser();
        testLoadExistingUser();
        testRejectNullIdAndRole();
        testRejectBlankFields();
        testUpdateProfile();
        testUpdateProfileAtomicFailure();
        testChangePassword();
        testIdentityEquality();

        System.out.println("\n=== Summary: " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }

    // --- Test Cases ---

    private static void testCreateNewUser() {
        runTest("Create new user generates UUID and trims/normalizes fields", () -> {
            User user = new TestUser("  AdminUser  ", "  John Doe  ", "  01012345678  ", "hash123", Role.ADMIN);

            assertTrue(user.getId() != null, "ID should be generated");
            assertEquals("adminuser", user.getUsername(), "Username should be lowercased & trimmed");
            assertEquals("John Doe", user.getName(), "Name should be trimmed");
            assertEquals("01012345678", user.getPhone(), "Phone should be trimmed");
            assertEquals("hash123", user.getPasswordHash(), "Password hash mismatch");
            assertEquals(Role.ADMIN, user.getRole(), "Role mismatch");
        });
    }

    private static void testLoadExistingUser() {
        runTest("Load existing user keeps provided UUID", () -> {
            UUID id = UUID.randomUUID();
            User user = new TestUser(id, "dr.sara", "Sara Ali", "01198765432", "bcrypt_hash", Role.DOCTOR);

            assertEquals(id, user.getId(), "Existing ID should be preserved");
            assertEquals("dr.sara", user.getUsername(), "Username mismatch");
        });
    }

    private static void testRejectNullIdAndRole() {
        runTest("Reject null ID or null Role with NullPointerException", () -> {
            assertThrows(NullPointerException.class, () ->
                new TestUser(null, "user1", "Name", "0100000", "hash", Role.PATIENT)
            );
            assertThrows(NullPointerException.class, () ->
                new TestUser("user1", "Name", "0100000", "hash", null)
            );
        });
    }

    private static void testRejectBlankFields() {
        runTest("Reject null or blank username, name, phone, and passwordHash", () -> {
            String[] badInputs = {null, "", "   ", "\t\n"};
            for (String bad : badInputs) {
                assertThrows(IllegalArgumentException.class, () -> new TestUser(bad, "Name", "010", "hash", Role.PATIENT));
                assertThrows(IllegalArgumentException.class, () -> new TestUser("user", bad, "010", "hash", Role.PATIENT));
                assertThrows(IllegalArgumentException.class, () -> new TestUser("user", "Name", bad, "hash", Role.PATIENT));
                assertThrows(IllegalArgumentException.class, () -> new TestUser("user", "Name", "010", bad, Role.PATIENT));
            }
        });
    }

    private static void testUpdateProfile() {
        runTest("Update profile updates and trims name and phone", () -> {
            User user = new TestUser("user1", "Old Name", "0101111", "hash1", Role.PATIENT);
            user.updateProfile("  New Name  ", "  0122222  ");

            assertEquals("New Name", user.getName(), "Name should be updated");
            assertEquals("0122222", user.getPhone(), "Phone should be updated");
            assertEquals("hash1", user.getPasswordHash(), "Password hash should not change");
        });
    }

    private static void testUpdateProfileAtomicFailure() {
        runTest("Update profile fails atomically if either field is blank", () -> {
            User user = new TestUser("user1", "Original Name", "0101111", "hash1", Role.PATIENT);

            assertThrows(IllegalArgumentException.class, () -> user.updateProfile("   ", "0122222"));
            assertThrows(IllegalArgumentException.class, () -> user.updateProfile("Valid Name", null));

            assertEquals("Original Name", user.getName(), "Name must remain unchanged on failure");
            assertEquals("0101111", user.getPhone(), "Phone must remain unchanged on failure");
        });
    }

    private static void testChangePassword() {
        runTest("Change password updates passwordHash and rejects blank values", () -> {
            User user = new TestUser("user1", "Name", "0101111", "old_hash", Role.DOCTOR);
            user.changePassword("  new_hash  ");
            assertEquals("new_hash", user.getPasswordHash(), "Password hash should update");

            assertThrows(IllegalArgumentException.class, () -> user.changePassword("  "));
            assertEquals("new_hash", user.getPasswordHash(), "Password hash must remain unchanged on failure");
        });
    }

    private static void testIdentityEquality() {
        runTest("Users with same UUID are equal; different UUIDs or null/other types are not", () -> {
            UUID sharedId = UUID.randomUUID();
            User u1 = new TestUser(sharedId, "user1", "Name 1", "0101", "hash1", Role.ADMIN);
            User u2 = new TestUser(sharedId, "user2", "Name 2", "0102", "hash2", Role.ADMIN);
            User u3 = new TestUser(UUID.randomUUID(), "user1", "Name 1", "0101", "hash1", Role.ADMIN);

            assertTrue(u1.equals(u2), "Same UUID should be equal");
            assertEquals(u1.hashCode(), u2.hashCode(), "Same UUID should have same hashCode");
            assertTrue(!u1.equals(u3), "Different UUID should not be equal");
            assertTrue(!u1.equals(null), "Should not equal null");
            assertTrue(!u1.equals("string"), "Should not equal unrelated object");
        });
    }

    private static void runTest(String name, Runnable test) {
        try {
            test.run();
            passed++;
            System.out.println("[PASS] " + name);
        } catch (Throwable t) {
            failed++;
            System.out.println("[FAIL] " + name + " -> " + t.getMessage());
        }
    }

    private static void assertEquals(Object expected, Object actual, String msg) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(msg + " | Expected: <" + expected + ">, Actual: <" + actual + ">");
        }
    }

    private static void assertTrue(boolean condition, String msg) {
        if (!condition) throw new AssertionError(msg);
    }

    private static void assertThrows(Class<? extends Throwable> expectedType, Runnable action) {
        try {
            action.run();
        } catch (Throwable t) {
            if (expectedType.isInstance(t)) return;
            throw new AssertionError("Expected " + expectedType.getSimpleName() + ", got " + t.getClass().getSimpleName());
        }
        throw new AssertionError("Expected " + expectedType.getSimpleName() + " to be thrown, but nothing was thrown.");
    }
}

