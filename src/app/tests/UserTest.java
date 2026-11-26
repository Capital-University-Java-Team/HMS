package app.tests;

import app.domain.DomainException;
import app.domain.Role;
import app.domain.User;
import java.util.Objects;

public class UserTest {

    private static class TestUser extends User {
        public TestUser(String id, String name, String phone, String password, Role role) {
            super(id, name, phone, password, role);
        }
    }

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== Running User Entity Tests ===\n");

        testCreateUser();
        testOptionalPhone();
        testRejectNullRole();
        testRejectBlankRequiredFields();
        testUpdateProfile();
        testUpdateProfileAtomicFailure();
        testCheckPassword();

        System.out.println("\n=== Summary: " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }

    // --- Test Cases ---

    private static void testCreateUser() {
        runTest("Create user stores and trims fields properly", () -> {
            User user = new TestUser("  U-101  ", "  John Doe  ", "  01012345678  ", "pass123", Role.ADMIN);

            assertEquals("U-101", user.getId(), "ID should be trimmed");
            assertEquals("John Doe", user.getName(), "Name should be trimmed");
            assertEquals("01012345678", user.getPhone(), "Phone should be trimmed");
            assertEquals("pass123", user.getPassword(), "Password mismatch");
            assertEquals(Role.ADMIN, user.getRole(), "Role mismatch");
        });
    }

    private static void testOptionalPhone() {
        runTest("Optional phone normalizes null or blank values to null", () -> {
            User u1 = new TestUser("U-101", "John Doe", null, "pass123", Role.PATIENT);
            User u2 = new TestUser("U-102", "Jane Doe", "   ", "pass123", Role.PATIENT);

            assertEquals(null, u1.getPhone(), "Null phone should stay null");
            assertEquals(null, u2.getPhone(), "Blank phone should normalize to null");
        });
    }

    private static void testRejectNullRole() {
        runTest("Reject null Role with DomainException", () -> {
            assertThrows(DomainException.class, () ->
                new TestUser("U-101", "Name", "0100000", "pass", null)
            );
        });
    }

    private static void testRejectBlankRequiredFields() {
        runTest("Reject null or blank id, name, and password with DomainException", () -> {
            String[] badInputs = {null, "", "   ", "\t\n"};
            for (String bad : badInputs) {
                assertThrows(DomainException.class, () -> new TestUser(bad, "Name", "010", "pass", Role.PATIENT));
                assertThrows(DomainException.class, () -> new TestUser("U-101", bad, "010", "pass", Role.PATIENT));
                assertThrows(DomainException.class, () -> new TestUser("U-101", "Name", "010", bad, Role.PATIENT));
            }
        });
    }

    private static void testUpdateProfile() {
        runTest("Update profile updates name, optional phone, and password", () -> {
            User user = new TestUser("U-101", "Old Name", "0101111", "old_pass", Role.PATIENT);
            user.updateProfile("  New Name  ", "  0122222  ", "new_pass");

            assertEquals("New Name", user.getName(), "Name should be updated");
            assertEquals("0122222", user.getPhone(), "Phone should be updated");
            assertEquals("new_pass", user.getPassword(), "Password should be updated");

            // Clearing optional phone
            user.updateProfile("New Name", "   ", "new_pass");
            assertEquals(null, user.getPhone(), "Blank phone on update should normalize to null");
        });
    }

    private static void testUpdateProfileAtomicFailure() {
        runTest("Update profile fails atomically with DomainException if name or password is blank", () -> {
            User user = new TestUser("U-101", "Original Name", "0101111", "orig_pass", Role.PATIENT);

            assertThrows(DomainException.class, () -> user.updateProfile("   ", "0122222", "new_pass"));
            assertThrows(DomainException.class, () -> user.updateProfile("Valid Name", "0122222", null));

            assertEquals("Original Name", user.getName(), "Name must remain unchanged on failure");
            assertEquals("0101111", user.getPhone(), "Phone must remain unchanged on failure");
            assertEquals("orig_pass", user.getPassword(), "Password must remain unchanged on failure");
        });
    }

    private static void testCheckPassword() {
        runTest("checkPassword returns true only for exact password match", () -> {
            User user = new TestUser("U-101", "Name", "0101111", "secret123", Role.DOCTOR);

            assertTrue(user.checkPassword("secret123"), "Exact password should match");
            assertTrue(!user.checkPassword("wrong"), "Wrong password should not match");
            assertTrue(!user.checkPassword(null), "Null password should not match");
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

