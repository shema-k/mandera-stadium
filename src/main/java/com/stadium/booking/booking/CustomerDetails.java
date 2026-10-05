package com.stadium.booking.booking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/**
 * Checks the details a customer types in, and says what is wrong with each one.
 *
 * <p>This exists because a single {@code throws} on the first bad field is a dead
 * end for the person typing. They press the button, the form closes, a warning
 * appears, and when they dismiss it the form is gone and everything they typed
 * has been lost — so they have to start again and remember what went wrong.
 *
 * <p>Instead, every field is checked and each problem is reported against the
 * field it belongs to, with an example of the format that would be accepted. The
 * form can then stay open, mark the fields that need fixing, and hand focus to
 * the first one.
 *
 * <p>No Swing here, so the rules can be tested without a display.
 */
public final class CustomerDetails {
    /** Which field a problem belongs to. */
    public enum Field {
        NAME,
        EMAIL,
        PHONE
    }

    /**
     * One field's problem: what is wrong, and an example of what is accepted.
     *
     * @param field   the field to mark
     * @param message what is wrong, said in terms of what to type
     * @param example a value that would be accepted, so the format is unambiguous
     */
    public static final class Problem {
        private final Field field;
        private final String message;
        private final String example;

        Problem(Field field, String message, String example) {
            this.field = field;
            this.message = message;
            this.example = example;
        }

        public Field getField() {
            return field;
        }

        public String getMessage() {
            return message;
        }

        /** An accepted example, for instance {@code "amina@example.co.ug"}. */
        public String getExample() {
            return example;
        }

        /** The message and the example together, for a one-line summary. */
        public String describe() {
            if (example == null || example.isBlank()) {
                return message;
            }
            // Bracketed rather than appended with "Example:", so the reason
            // reads first and the format sits at the end of the same line.
            return message + "  (for example " + example + ")";
        }
    }

    private static final String NAME_EXAMPLE = "Amina Okello";
    private static final String EMAIL_EXAMPLE = "amina@example.co.ug";
    private static final String PHONE_EXAMPLE = "+256 700 123 456";

    private CustomerDetails() {
    }

    /**
     * Checks all three fields and reports every problem, not just the first.
     *
     * @return the problems, keyed by field and in a fixed order so the form can
     *         work down them; empty when everything is acceptable
     */
    public static Map<Field, Problem> check(String name, String email, String phone) {
        Map<Field, Problem> problems = new LinkedHashMap<>();
        checkName(name, problems);
        checkEmail(email, problems);
        checkPhone(phone, problems);
        return Collections.unmodifiableMap(problems);
    }

    private static void checkName(String name, Map<Field, Problem> problems) {
        String value = clean(name);
        if (value.isEmpty()) {
            problems.put(Field.NAME, new Problem(Field.NAME,
                    "Please enter your name so the booking is yours", null));
            return;
        }
        if (value.length() < 2) {
            problems.put(Field.NAME, new Problem(Field.NAME,
                    "That name looks too short", NAME_EXAMPLE));
            return;
        }
        // A name with no letters at all is almost always a typo rather than a
        // real name, and is worth catching before the booking is taken.
        if (!value.matches(".*[\\p{L}].*")) {
            problems.put(Field.NAME, new Problem(Field.NAME,
                    "A name needs at least one letter", NAME_EXAMPLE));
        }
    }

    private static void checkEmail(String email, Map<Field, Problem> problems) {
        String value = clean(email);
        if (value.isEmpty()) {
            problems.put(Field.EMAIL, new Problem(Field.EMAIL,
                    "Please enter an email address for your receipt", null));
            return;
        }
        if (value.startsWith("@") || value.endsWith("@")) {
            problems.put(Field.EMAIL, new Problem(Field.EMAIL,
                    "An email address cannot start or end with @", EMAIL_EXAMPLE));
            return;
        }
        if (value.contains(" ")) {
            problems.put(Field.EMAIL, new Problem(Field.EMAIL,
                    "An email address cannot contain a space", EMAIL_EXAMPLE));
            return;
        }
        if (!value.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            problems.put(Field.EMAIL, new Problem(Field.EMAIL,
                    "That does not look like an email address. It needs an @ and a "
                            + "domain such as .com or .co.ug", EMAIL_EXAMPLE));
        }
    }

    private static void checkPhone(String phone, Map<Field, Problem> problems) {
        String value = clean(phone);
        if (value.isEmpty()) {
            problems.put(Field.PHONE, new Problem(Field.PHONE,
                    "Please enter a phone number the venue can reach you on", null));
            return;
        }
        // Letters are the mistake people actually make: a name typed into the
        // phone box, or a pasted word. Said plainly, because "invalid" is not.
        if (value.matches(".*[\\p{L}].*")) {
            problems.put(Field.PHONE, new Problem(Field.PHONE,
                    "A phone number is digits only. Remove any letters", PHONE_EXAMPLE));
            return;
        }
        String digits = value.replaceAll("\\D", "");
        if (digits.length() < 7) {
            problems.put(Field.PHONE, new Problem(Field.PHONE,
                    "That number is too short. A full number has at least 7 digits",
                    PHONE_EXAMPLE));
            return;
        }
        if (digits.length() > 15) {
            // 15 is the longest a number is anywhere in the E.164 standard, so
            // beyond that it is a mistake rather than a long number.
            problems.put(Field.PHONE, new Problem(Field.PHONE,
                    "That number is too long. Check for an extra digit", PHONE_EXAMPLE));
            return;
        }
        if (!value.matches("^[0-9+() .-]+$")) {
            problems.put(Field.PHONE, new Problem(Field.PHONE,
                    "A phone number may use digits, spaces and + ( ) - only", PHONE_EXAMPLE));
        }
    }

    private static String clean(String value) {
        return value == null ? "" : value.trim();
    }

    /** True when nothing is wrong with any field. */
    public static boolean isValid(String name, String email, String phone) {
        return check(name, email, phone).isEmpty();
    }

    /**
     * Whether the customer still has to be asked for their details.
     *
     * <p>Asked when anything is missing <em>or</em> wrong. The distinction
     * matters: treating "all three boxes have something in them" as good enough
     * waved through a wrong email address, and the booking was then refused with
     * a warning the customer could not act on. It is its own named method so
     * there is one decision to get right, and one to test.
     *
     * @return true when the details form should be shown
     */
    public static boolean needsAsking(String name, String email, String phone) {
        return !check(name, email, phone).isEmpty();
    }

    /** The problems as a list, in the order the fields appear in the form. */
    public static List<Problem> problemsInOrder(Map<Field, Problem> problems) {
        return new ArrayList<>(problems.values());
    }

    /**
     * A one-line summary of every problem, for a status line where there is no
     * room beside each field.
     */
    public static String summarise(Map<Field, Problem> problems) {
        if (problems.isEmpty()) {
            return "";
        }
        StringBuilder text = new StringBuilder();
        for (Problem problem : problems.values()) {
            if (text.length() > 0) {
                text.append(' ');
            }
            text.append(problem.describe());
        }
        return text.toString();
    }
}
