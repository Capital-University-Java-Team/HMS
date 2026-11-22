package app.tests;

import java.util.Objects;

public class TestSupport {
    private int passed = 0;
    private int failed = 0;

    public void runTest(String name, Runnable test) {
        try {
            test.run();
            passed++;
            System.out.println("[PASS] " + name);
        } catch (Throwable t) {
            failed++;
            System.out.println("[FAIL] " + name + " -> " + t.getMessage());
        }
    }

    public static void assertEquals(Object expected, Object actual, String msg) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(msg + " | Expected: <" + expected + ">, Actual: <" + actual + ">");
        }
    }

    public static void assertTrue(boolean condition, String msg) {
        if (!condition) throw new AssertionError(msg);
    }

    public static void assertThrows(Class<? extends Throwable> expectedType, Runnable action) {
        try {
            action.run();
        } catch (Throwable t) {
            if (expectedType.isInstance(t)) return;
            throw new AssertionError("Expected " + expectedType.getSimpleName() + ", got " + t.getClass().getSimpleName());
        }
        throw new AssertionError("Expected " + expectedType.getSimpleName() + " to be thrown, but nothing was thrown.");
    }

    public void printSummary(String suiteName) {
        System.out.println("\n=== " + suiteName + " Summary: " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}

