package app.tests;

import app.domain.Appointment;
import app.domain.AppointmentStatus;
import java.time.LocalDateTime;
import java.util.UUID;

import static app.tests.TestSupport.*;

public class AppointmentTest {
    public static void main(String[] args) {
        TestSupport suite = new TestSupport();
        System.out.println("=== Running Appointment Unit Tests ===\n");

        suite.runTest("New Appointment defaults to SCHEDULED and requires future dateTime", () -> {
            LocalDateTime future = LocalDateTime.now().plusHours(5);
            Appointment appt = new Appointment(UUID.randomUUID(), UUID.randomUUID(), future);

            assertTrue(appt.getId() != null, "ID generated");
            assertEquals(AppointmentStatus.SCHEDULED, appt.getStatus(), "Initial status must be SCHEDULED");
            assertEquals(future, appt.getDateTime(), "DateTime preserved");

            // Past dateTime rejected for new bookings
            assertThrows(IllegalArgumentException.class, () ->
                new Appointment(UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now().minusMinutes(10))
            );
        });

        suite.runTest("Load existing Appointment allows past timestamps from DB", () -> {
            LocalDateTime past = LocalDateTime.now().minusDays(10);
            Appointment appt = new Appointment(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), past, AppointmentStatus.COMPLETED);
            assertEquals(AppointmentStatus.COMPLETED, appt.getStatus(), "Loaded status preserved");
            assertEquals(past, appt.getDateTime(), "Past timestamp allowed on DB reconstitution");
        });

        suite.runTest("Reschedule works only while SCHEDULED and requires future time", () -> {
            Appointment appt = new Appointment(UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now().plusDays(1));
            LocalDateTime newSlot = LocalDateTime.now().plusDays(2);

            appt.reschedule(newSlot);
            assertEquals(newSlot, appt.getDateTime(), "DateTime updated");

            // Reject past reschedule
            assertThrows(IllegalArgumentException.class, () ->
                appt.reschedule(LocalDateTime.now().minusHours(1))
            );
        });

        suite.runTest("Complete and Cancel transitions lock further modifications", () -> {
            Appointment appt1 = new Appointment(UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now().plusDays(1));
            appt1.complete();
            assertEquals(AppointmentStatus.COMPLETED, appt1.getStatus(), "Status becomes COMPLETED");
            assertThrows(IllegalStateException.class, appt1::cancel);
            assertThrows(IllegalStateException.class, appt1::complete);
            assertThrows(IllegalStateException.class, () -> appt1.reschedule(LocalDateTime.now().plusDays(3)));

            Appointment appt2 = new Appointment(UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now().plusDays(1));
            appt2.cancel();
            assertEquals(AppointmentStatus.CANCELED, appt2.getStatus(), "Status becomes CANCELED");
            assertThrows(IllegalStateException.class, appt2::complete);
            assertThrows(IllegalStateException.class, () -> appt2.reschedule(LocalDateTime.now().plusDays(3)));
        });

        suite.printSummary("AppointmentTest");
    }
}

