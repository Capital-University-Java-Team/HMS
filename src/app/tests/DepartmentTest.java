package app.tests;

import app.domain.Department;
import app.domain.DomainException;

import static app.tests.TestSupport.*;

public class DepartmentTest {
    public static void main(String[] args) {
        TestSupport suite = new TestSupport();
        System.out.println("=== Running Department Unit Tests ===\n");

        suite.runTest("Create Department stores and trims fields", () -> {
            Department dept = new Department("  D-101  ", "  Cardiology  ", "  Cardiologist  ");
            assertEquals("D-101", dept.getId(), "ID trimmed");
            assertEquals("Cardiology", dept.getName(), "Name trimmed");
            assertEquals("Cardiologist", dept.getRequiredSpeciality(), "Speciality trimmed");
        });

        suite.runTest("Reject blank or null id, name, and speciality with DomainException", () -> {
            assertThrows(DomainException.class, () -> new Department("  ", "Cardiology", "Cardiologist"));
            assertThrows(DomainException.class, () -> new Department("D-101", "  ", "Cardiologist"));
            assertThrows(DomainException.class, () -> new Department("D-101", "Cardiology", null));
        });

        suite.runTest("Update details atomically", () -> {
            Department dept = new Department("D-101", "Old Dept", "Old Spec");

            assertThrows(DomainException.class, () -> dept.update("New Dept", "  "));
            assertEquals("Old Dept", dept.getName(), "Name must not mutate on failure");
            assertEquals("Old Spec", dept.getRequiredSpeciality(), "Speciality must not mutate on failure");

            dept.update("  Pediatrics  ", "  Pediatrician  ");
            assertEquals("Pediatrics", dept.getName(), "Name updated");
            assertEquals("Pediatrician", dept.getRequiredSpeciality(), "Speciality updated");
        });

        suite.runTest("toString formats department details properly", () -> {
            Department dept = new Department("D-101", "Neurology", "Neurologist");
            assertEquals("D-101 - Neurology (Speciality: Neurologist)", dept.toString(), "toString format mismatch");
        });

        suite.printSummary("DepartmentTest");
    }
}

