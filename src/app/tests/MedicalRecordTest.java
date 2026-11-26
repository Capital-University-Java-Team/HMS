package app.tests;

import app.domain.DomainException;
import app.domain.MedicalRecord;
import java.time.LocalDate;

import static app.tests.TestSupport.*;

public class MedicalRecordTest {
    public static void main(String[] args) {
        TestSupport suite = new TestSupport();
        System.out.println("=== Running MedicalRecord Unit Tests ===\n");

        suite.runTest("New MedicalRecord generates 8-char ID and trims fields", () -> {
            LocalDate date = LocalDate.of(2026, 11, 26);
            MedicalRecord record = new MedicalRecord("  APT-1  ", "  DOC-1  ", "  PAT-1  ", date, "  Fever, cough  ", "  Flu  ");

            assertTrue(record.getId() != null && record.getId().length() == 8, "8-char ID generated");
            assertEquals("APT-1", record.getAppointmentId(), "Appointment ID trimmed");
            assertEquals("DOC-1", record.getDoctorId(), "Doctor ID trimmed");
            assertEquals("PAT-1", record.getPatientId(), "Patient ID trimmed");
            assertEquals(date, record.getVisitDate(), "Visit date preserved");
            assertEquals("Fever, cough", record.getSymptoms(), "Symptoms trimmed");
            assertEquals("Flu", record.getDiagnosis(), "Diagnosis trimmed");
        });

        suite.runTest("Load existing MedicalRecord preserves ID and fields", () -> {
            LocalDate date = LocalDate.of(2026, 10, 15);
            MedicalRecord record = new MedicalRecord("REC-101", "APT-1", "DOC-1", "PAT-1", date, "Headache", "Migraine");

            assertEquals("REC-101", record.getId(), "Loaded ID preserved");
            assertEquals("Headache", record.getSymptoms(), "Symptoms preserved");
            assertEquals("Migraine", record.getDiagnosis(), "Diagnosis preserved");
            assertEquals(date, record.getVisitDate(), "Visit date preserved");
        });

        suite.runTest("Reject null or blank required fields with DomainException", () -> {
            LocalDate date = LocalDate.now();
            assertThrows(DomainException.class, () -> new MedicalRecord("  ", "APT-1", "DOC-1", "PAT-1", date, "Symptoms", "Diagnosis"));
            assertThrows(DomainException.class, () -> new MedicalRecord("  ", "DOC-1", "PAT-1", date, "Symptoms", "Diagnosis"));
            assertThrows(DomainException.class, () -> new MedicalRecord("APT-1", null, "PAT-1", date, "Symptoms", "Diagnosis"));
            assertThrows(DomainException.class, () -> new MedicalRecord("APT-1", "DOC-1", "   ", date, "Symptoms", "Diagnosis"));
            assertThrows(DomainException.class, () -> new MedicalRecord("APT-1", "DOC-1", "PAT-1", null, "Symptoms", "Diagnosis"));
            assertThrows(DomainException.class, () -> new MedicalRecord("APT-1", "DOC-1", "PAT-1", date, "", "Diagnosis"));
            assertThrows(DomainException.class, () -> new MedicalRecord("APT-1", "DOC-1", "PAT-1", date, "Symptoms", null));
        });

        suite.runTest("Update consultation details updates fields and validates inputs", () -> {
            LocalDate oldDate = LocalDate.of(2026, 11, 1);
            LocalDate newDate = LocalDate.of(2026, 11, 26);
            MedicalRecord record = new MedicalRecord("REC-101", "APT-1", "DOC-1", "PAT-1", oldDate, "Old Symptoms", "Old Diagnosis");

            assertThrows(DomainException.class, () -> record.updateConsultationDetails("   ", "New Diagnosis", newDate));
            assertThrows(DomainException.class, () -> record.updateConsultationDetails("New Symptoms", null, newDate));
            assertThrows(DomainException.class, () -> record.updateConsultationDetails("New Symptoms", "New Diagnosis", null));

            record.updateConsultationDetails("  Updated Symptoms  ", "  Updated Diagnosis  ", newDate);
            assertEquals("Updated Symptoms", record.getSymptoms(), "Symptoms updated and trimmed");
            assertEquals("Updated Diagnosis", record.getDiagnosis(), "Diagnosis updated and trimmed");
            assertEquals(newDate, record.getVisitDate(), "Visit date updated");
        });

        suite.runTest("toString formats medical record details properly", () -> {
            LocalDate date = LocalDate.of(2026, 11, 26);
            MedicalRecord record = new MedicalRecord("REC-101", "APT-1", "DOC-1", "PAT-1", date, "Fever", "Cold");

            String expected = "[REC-101] Date: 2026-11-26 | Patient: PAT-1 | Doctor: DOC-1 | Symptoms: Fever | Diagnosis: Cold";
            assertEquals(expected, record.toString(), "toString format mismatch");
        });

        suite.printSummary("MedicalRecordTest");
    }
}

