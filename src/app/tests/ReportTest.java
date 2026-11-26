package app.tests;

import app.domain.DomainException;
import app.domain.Report;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static app.tests.TestSupport.*;

public class ReportTest {
    public static void main(String[] args) {
        TestSupport suite = new TestSupport();
        System.out.println("=== Running Report Unit Tests ===\n");

        suite.runTest("New Report generates 8-char ID and trims fields", () -> {
            LocalDateTime now = LocalDateTime.of(2026, 11, 26, 13, 30);
            List<String> lines = List.of("Line 1", "Line 2");
            Report report = new Report(Report.Type.APPOINTMENTS, "  ADM-1  ", now, "  Daily Summary  ", lines);

            assertTrue(report.getId() != null && report.getId().length() == 8, "8-char ID generated");
            assertEquals(Report.Type.APPOINTMENTS, report.getType(), "Type preserved");
            assertEquals("ADM-1", report.getGeneratedByAdminId(), "Admin ID trimmed");
            assertEquals(now, report.getGeneratedAt(), "Timestamp preserved");
            assertEquals("Daily Summary", report.getSummary(), "Summary trimmed");
            assertEquals(lines, report.getLines(), "Lines preserved");
        });

        suite.runTest("Load existing Report preserves ID and enforces defensive copy / immutability", () -> {
            LocalDateTime now = LocalDateTime.of(2026, 11, 26, 13, 30);
            List<String> mutableLines = new ArrayList<>(List.of("Entry A", "Entry B"));

            Report report = new Report("REP-101", Report.Type.DOCTOR_WORKLOAD, "ADM-1", now, "Workload", mutableLines);
            assertEquals("REP-101", report.getId(), "Loaded ID preserved");

            // Mutating source list should not affect report
            mutableLines.add("Entry C");
            assertEquals(2, report.getLines().size(), "Report lines should be defensively copied");

            // Returned list must be unmodifiable
            assertThrows(UnsupportedOperationException.class, () -> report.getLines().add("Entry D"));
        });

        suite.runTest("Reject null or blank required fields with DomainException", () -> {
            LocalDateTime now = LocalDateTime.now();
            List<String> lines = List.of("Item 1");

            assertThrows(DomainException.class, () -> new Report("  ", Report.Type.PATIENT_RECORDS, "ADM-1", now, "Summary", lines));
            assertThrows(DomainException.class, () -> new Report(null, "ADM-1", now, "Summary", lines));
            assertThrows(DomainException.class, () -> new Report(Report.Type.PATIENT_RECORDS, "   ", now, "Summary", lines));
            assertThrows(DomainException.class, () -> new Report(Report.Type.PATIENT_RECORDS, "ADM-1", null, "Summary", lines));
            assertThrows(DomainException.class, () -> new Report(Report.Type.PATIENT_RECORDS, "ADM-1", now, "", lines));
            assertThrows(DomainException.class, () -> new Report(Report.Type.PATIENT_RECORDS, "ADM-1", now, "Summary", null));
        });

        suite.runTest("formatText and toString format report output properly", () -> {
            LocalDateTime now = LocalDateTime.of(2026, 11, 26, 10, 0);
            Report report = new Report(
                "REP-101",
                Report.Type.APPOINTMENTS,
                "ADM-1",
                now,
                "Total: 2",
                List.of("APT-1: Scheduled", "APT-2: Completed")
            );

            String expectedText =
                "[REP-101] APPOINTMENTS | Generated: 2026-11-26T10:00 | By Admin: ADM-1\n" +
                "Summary: Total: 2\n" +
                " - APT-1: Scheduled\n" +
                " - APT-2: Completed";

            assertEquals(expectedText, report.formatText(), "formatText output mismatch");
            assertEquals("[REP-101] APPOINTMENTS (2 entries) - Total: 2", report.toString(), "toString format mismatch");
        });

        suite.printSummary("ReportTest");
    }
}

