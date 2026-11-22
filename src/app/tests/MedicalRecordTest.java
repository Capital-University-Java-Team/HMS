package app.tests;

import app.domain.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static app.tests.TestSupport.*;

public class MedicalRecordTest {
    public static void main(String[] args) {
        TestSupport suite = new TestSupport();
        System.out.println("=== Running MedicalRecord Unit Tests ===\n");

        suite.runTest("Create MedicalRecord validates required fields and trims strings", () -> {
            MedicalRecord rec = new MedicalRecord(
                UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now(), "  High fever  ", "  Viral infection  "
            );
            assertTrue(rec.getId() != null, "ID generated");
            assertEquals("High fever", rec.getSymptoms(), "Symptoms trimmed");
            assertEquals("Viral infection", rec.getDiagnosis(), "Diagnosis trimmed");

            assertThrows(IllegalArgumentException.class, () ->
                new MedicalRecord(UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now(), "  ", "Diagnosis")
            );
            assertThrows(IllegalArgumentException.class, () ->
                new MedicalRecord(UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now(), "Symptoms", null)
            );
        });

        suite.runTest("Only the owning Doctor can update consultation details", () -> {
            UUID ownerDoctorId = UUID.randomUUID();
            UUID otherDoctorId = UUID.randomUUID();
            MedicalRecord rec = new MedicalRecord(UUID.randomUUID(), ownerDoctorId, LocalDateTime.now().minusDays(1), "Cough", "Bronchitis");

            // Another doctor cannot edit
            assertThrows(SecurityException.class, () ->
                rec.updateConsultation(otherDoctorId, "Updated", "Updated", LocalDateTime.now())
            );
            assertEquals("Cough", rec.getSymptoms(), "Symptoms must not change on unauthorized edit");

            // Owning doctor can edit
            LocalDateTime updatedVisit = LocalDateTime.now();
            rec.updateConsultation(ownerDoctorId, "  Mild cough  ", "  Recovered  ", updatedVisit);
            assertEquals("Mild cough", rec.getSymptoms(), "Symptoms updated");
            assertEquals("Recovered", rec.getDiagnosis(), "Diagnosis updated");
            assertEquals(updatedVisit, rec.getVisitDate(), "Visit date updated");
        });

        suite.printSummary("MedicalRecordTest");
    }
}

