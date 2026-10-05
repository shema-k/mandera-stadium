package com.stadium.booking;

import com.stadium.booking.booking.FormRules;

import static com.stadium.booking.TestRunner.assertEquals;
import static com.stadium.booking.TestRunner.assertTrue;
import static com.stadium.booking.TestRunner.suite;
import static com.stadium.booking.TestRunner.test;

/**
 * The rules for the short pieces of free text a customer types.
 *
 * <p>The label rule is here because of what it replaced. A label over the limit
 * used to be shortened without mention when it was saved, so a customer could
 * type a name, watch the save succeed, and find later that the start of it had
 * gone. Refusing it with a message that says by how much to cut is the change.
 */
final class FormRulesTest {
    private FormRulesTest() {
    }

    static void register() {
        suite("Checking the words a customer types");

        test("an ordinary label is accepted", () -> {
            assertEquals(null, FormRules.labelProblem("Kenya end, with friends"),
                    "a label someone will recognise later is fine");
            assertEquals(null, FormRules.labelProblem("  padded out  "),
                    "surrounding spaces do not make a label wrong");
        });

        test("no label at all is not a fault", () -> {
            // An unnamed selection is given a name of its own when saved. Calling
            // that a mistake would make the customer type something for no reason.
            assertEquals(null, FormRules.labelProblem(""), "an empty label is allowed");
            assertEquals(null, FormRules.labelProblem("   "), "and so is only spaces");
            assertEquals(null, FormRules.labelProblem(null), "and no label at all");
            assertEquals("My seats", FormRules.trimLabel(""), "which is saved as My seats");
        });

        test("a label over the limit is refused with both numbers in the message", () -> {
            String message = FormRules.labelProblem("x".repeat(400));
            assertTrue(message != null, "400 characters is over the limit");
            assertTrue(message.contains("400"),
                    "the message says how long the label was, so the customer can "
                            + "see how much there is to cut: " + message);
            assertTrue(message.contains(String.valueOf(FormRules.MAX_LABEL_LENGTH)),
                    "and how short it has to be: " + message);
        });

        test("the limit is the boundary in both directions", () -> {
            assertEquals(null, FormRules.labelProblem("x".repeat(FormRules.MAX_LABEL_LENGTH)),
                    "a label exactly on the limit is accepted");
            assertTrue(FormRules.labelProblem("x".repeat(FormRules.MAX_LABEL_LENGTH + 1))
                            != null,
                    "and one character more is refused");
        });

        test("the message names the real length, not a guess", () -> {
            // Spacing is trimmed before measuring, so the number in the message
            // has to be the trimmed one or it will not match what is counted.
            String padded = "   " + "x".repeat(400) + "   ";
            assertTrue(FormRules.labelProblem(padded).contains("400"),
                    "the length excludes the padding around it: "
                            + FormRules.labelProblem(padded));
        });

        test("nothing over the limit can reach the store", () -> {
            // The rule and the store have to agree. If the store shortened it
            // anyway the customer would still lose part of their label.
            assertEquals(FormRules.MAX_LABEL_LENGTH,
                    FormRules.trimLabel("x".repeat(400)).length(),
                    "even the fallback keeps to the stated limit");
        });

        test("a special request has to say something", () -> {
            assertTrue(FormRules.requestProblem("hi") != null, "two characters is not a request");
            assertTrue(FormRules.requestProblem("") != null, "nor is nothing");
            assertTrue(FormRules.requestProblem(null) != null, "nor is no text at all");
            assertTrue(FormRules.requestProblem("   ") != null, "nor is only spaces");
            assertTrue(FormRules.requestProblem("    a    ") != null,
                    "a single letter padded out is still too short");
            assertEquals(null, FormRules.requestProblem("wheelchair access please"),
                    "but a real request is accepted");
        });

        test("a request message says what length is needed", () -> {
            String message = FormRules.requestProblem("hi");
            assertTrue(message.contains(String.valueOf(FormRules.MIN_REQUEST_LENGTH)),
                    "so the customer knows what to write rather than being told "
                            + "only that it is too short: " + message);
        });

        test("a request exactly on the minimum is accepted", () -> {
            assertEquals(null, FormRules.requestProblem("x".repeat(FormRules.MIN_REQUEST_LENGTH)),
                    "the boundary, so tightening the rule does not start refusing "
                            + "requests that used to be fine");
            assertTrue(FormRules.requestProblem(
                    "x".repeat(FormRules.MIN_REQUEST_LENGTH - 1)) != null,
                    "and one character short is still refused");
        });

        test("both limits are stated as numbers, not only in the wording", () -> {
            // The screens put these numbers in front of the customer, so they are
            // part of what the customer is shown rather than an internal detail.
            assertTrue(FormRules.MAX_LABEL_LENGTH > 0, "the label limit is a real number");
            assertTrue(FormRules.MIN_REQUEST_LENGTH > 0, "the request minimum is too");
        });
    }
}
