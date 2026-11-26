package app.tests;

import app.domain.Appointment;
import app.domain.AppointmentStatus;
import app.domain.DomainException;
import java.time.LocalDateTime;

import static app.tests.TestSupport.*;

public class AppointmentTest {
    public static void main(String[] args) {
        TestSupport suite = new TestSupport();
        System.out.println("=== Running Appointment Unit Tests ===\n");

        suite.runTest("New Appointment generates 8-char ID and defaults to SCHEDULED", () -> {
            LocalDateTime slot = LocalDateTime.now().plusHours(5);
            Appointment appt = new Appointment("  DOC-1  ", "  PAT-1  ", slot);

            assertTrue(appt.getId() != null && appt.getId().length() == 8, "8-char ID generated");
            assertEquals("DOC-1", appt.getDoctorId(), "Doctor ID trimmed");
            assertEquals("PAT-1", appt.getPatientId(), "Patient ID trimmed");
            assertEquals(AppointmentStatus.SCHEDULED, appt.getStatus(), "Initial status must be SCHEDULED");
            assertEquals(slot, appt.getDate(), "DateTime preserved");
        });

        suite.runTest("Load existing Appointment preserves ID, past timestamps, and status", () -> {
            LocalDateTime past = LocalDateTime.now().minusDays(10);
            Appointment appt = new Appointment("APT-101", "DOC-1", "PAT-1", past, AppointmentStatus.COMPLETED);

            assertEquals("APT-101", appt.getId(), "Loaded ID preserved");
            assertEquals(AppointmentStatus.COMPLETED, appt.getStatus(), "Loaded status preserved");
            assertEquals(past, appt.getDate(), "Past timestamp preserved");
        });

        suite.runTest("Reject null or blank required fields with DomainException", () -> {
            LocalDateTime now = LocalDateTime.now();
            assertThrows(DomainException.class, () -> new Appointment("  ", "DOC-1", "PAT-1", now, AppointmentStatus.SCHEDULED));
            assertThrows(DomainException.class, () -> new Appointment("   ", "PAT-1", now));
            assertThrows(DomainException.class, () -> new Appointment("DOC-1", null, now));
            assertThrows(DomainException.class, () -> new Appointment("DOC-1", "PAT-1", null));
            assertThrows(DomainException.class, () -> new Appointment("APT-1", "DOC-1", "PAT-1", now, null));
        });

        suite.runTest("Reschedule updates date only while SCHEDULED and rejects null", () -> {
            Appointment appt = new Appointment("DOC-1", "PAT-1", LocalDateTime.now().plusDays(1));
            LocalDateTime newSlot = LocalDateTime.now().plusDays(2);

            appt.reschedule(newSlot);
            assertEquals(newSlot, appt.getDate(), "DateTime updated");

            assertThrows(DomainException.class, () -> appt.reschedule(null));
        });

        suite.runTest("Complete and Cancel transitions lock further modifications with DomainException", () -> {
            Appointment appt1 = new Appointment("DOC-1", "PAT-1", LocalDateTime.now().plusDays(1));
            appt1.complete();
            assertEquals(AppointmentStatus.COMPLETED, appt1.getStatus(), "Status becomes COMPLETED");
            assertThrows(DomainException.class, appt1::cancel);
            assertThrows(DomainException.class, appt1::complete);
            assertThrows(DomainException.class, () -> appt1.reschedule(LocalDateTime.now().plusDays(3)));

            Appointment appt2 = new Appointment("DOC-1", "PAT-1", LocalDateTime.now().plusDays(1));
            appt2.cancel();
            assertEquals(AppointmentStatus.CANCELED, appt2.getStatus(), "Status becomes CANCELED");
            assertThrows(DomainException.class, appt2::complete);
            assertThrows(DomainException.class, appt2::cancel);
            assertThrows(DomainException.class, () -> appt2.reschedule(LocalDateTime.now().plusDays(3)));
        });

        suite.printSummary("AppointmentTest");
    }
}

