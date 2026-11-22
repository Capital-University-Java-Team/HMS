package app.tests;

import app.domain.Department;
import java.util.UUID;

import static app.tests.TestSupport.*;

public class DepartmentTest {
    public static void main(String[] args) {
        TestSupport suite = new TestSupport();
        System.out.println("=== Running Department Unit Tests ===\n");

        suite.runTest("Create Department generates UUID and trims fields", () -> {
            Department dept = new Department("  Cardiology  ", "  Cardiologist  ");
            assertTrue(dept.getId() != null, "ID generated");
            assertEquals("Cardiology", dept.getName(), "Name trimmed");
            assertEquals("Cardiologist", dept.getRequiredSpecialty(), "Specialty trimmed");
        });

        suite.runTest("Load existing Department preserves UUID", () -> {
            UUID id = UUID.randomUUID();
            Department dept = new Department(id, "Neurology", "Neurologist");
            assertEquals(id, dept.getId(), "ID preserved");
        });

        suite.runTest("Reject blank or null name and specialty", () -> {
            assertThrows(IllegalArgumentException.class, () -> new Department("  ", "Cardiologist"));
            assertThrows(IllegalArgumentException.class, () -> new Department("Cardiology", null));
        });

        suite.runTest("Update details atomically", () -> {
            Department dept = new Department("Old Dept", "Old Spec");

            assertThrows(IllegalArgumentException.class, () -> dept.updateDetails("New Dept", "  "));
            assertEquals("Old Dept", dept.getName(), "Name must not mutate on failure");

            dept.updateDetails("  Pediatrics  ", "  Pediatrician  ");
            assertEquals("Pediatrics", dept.getName(), "Name updated");
            assertEquals("Pediatrician", dept.getRequiredSpecialty(), "Specialty updated");
        });

        suite.runTest("matchesSpecialty compares case-insensitively and ignores extra spaces", () -> {
            Department dept = new Department("Dermatology", "Dermatologist");

            assertTrue(dept.matchesSpecialty("dermatologist"), "Lowercase should match");
            assertTrue(dept.matchesSpecialty("  DERMATOLOGIST  "), "Uppercase with spaces should match");
            assertTrue(!dept.matchesSpecialty("Cardiologist"), "Different specialty should not match");
            assertTrue(!dept.matchesSpecialty(null), "Null should return false");
            assertTrue(!dept.matchesSpecialty("   "), "Blank should return false");
        });

        suite.printSummary("DepartmentTest");
    }
}

