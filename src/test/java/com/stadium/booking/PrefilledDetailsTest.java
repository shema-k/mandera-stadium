package com.stadium.booking;

import com.stadium.booking.booking.CustomerDetails;
import com.stadium.booking.ui.DetailsFormPanel;

import static com.stadium.booking.TestRunner.assertEquals;
import static com.stadium.booking.TestRunner.assertFalse;
import static com.stadium.booking.TestRunner.assertTrue;
import static com.stadium.booking.TestRunner.suite;
import static com.stadium.booking.TestRunner.test;

/**
 * The details form when the three boxes already hold something.
 *
 * <p>There is a case the form tests do not reach on their own: the boxes are
 * shared with the booking screen, so they can arrive at the confirmation already
 * holding an unusable entry. The form must open for that too. Previously the
 * decision was "are they non-empty", so a wrong email was waved through and the
 * booking was refused afterwards, with the customer holding a warning and no way
 * to correct it.
 */
final class PrefilledDetailsTest {
    private PrefilledDetailsTest() {
    }

    private static final String NAME = "Amina Okello";
    private static final String EMAIL = "amina@example.co.ug";
    private static final String PHONE = "+256 700 123 456";

    /**
     * Whether the customer is asked for their details again.
     *
     * <p>The screen's own decision, so these tests break if it changes rather
     * than passing because a copy of the rule was left alone.
     */
    private static boolean wouldAskAgain(String name, String email, String phone) {
        return CustomerDetails.needsAsking(name, email, phone);
    }

    static void register() {
        suite("Details already entered");

        test("usable details are not asked for again", () -> {
            assertFalse(wouldAskAgain(NAME, EMAIL, PHONE),
                    "the customer has already given usable details");
            assertFalse(wouldAskAgain("  " + NAME, EMAIL + " ", PHONE),
                    "and still has not when they have spaces round them");
        });

        test("details that are there but unusable are asked for again", () -> {
            // The case that was wrong. Non-empty was treated as good enough, so
            // these were waved through and the booking was refused afterwards.
            // Every case below must be asked about, and must not be accepted by
            // the booking rules either, or asking would be pointless.
            String[][] unusable = {
                    {NAME, "amina example co ug", PHONE},
                    {NAME, "amina@example", PHONE},
                    {NAME, EMAIL, "0772abc123"},
                    {NAME, EMAIL, "12345"},
                    {"A", EMAIL, PHONE},
                    {"12345", EMAIL, PHONE},
                    {"A", "not-an-email", "abc"}};
            for (String[] values : unusable) {
                assertTrue(wouldAskAgain(values[0], values[1], values[2]),
                        "must ask again about " + values[0] + " / " + values[1]
                                + " / " + values[2]);
                assertFalse(CustomerDetails.isValid(values[0], values[1], values[2]),
                        "and the booking rules must refuse it too, or asking again "
                                + "changes nothing");
            }
        });

        test("the two rules cannot disagree about what is usable", () -> {
            // needsAsking and isValid are the same question asked twice, from
            // opposite sides. If they ever drift apart the customer is either
            // asked about details that were fine, or waved through details that
            // were not.
            String[][] cases = {
                    {NAME, EMAIL, PHONE},
                    {"  " + NAME + "  ", " " + EMAIL, PHONE + " "},
                    {NAME, "amina example co ug", PHONE},
                    {NAME, EMAIL, "0772abc123"},
                    {"A", EMAIL, PHONE},
                    {"", EMAIL, PHONE},
                    {NAME, "", PHONE},
                    {NAME, EMAIL, ""},
                    {"", "", ""},
                    {"12345", "amina@example.co.ug", "+256700123456"},
                    {"amina@example.co.ug", "x@y.ug", "12345678901234567890"}};
            for (String[] values : cases) {
                assertEquals(CustomerDetails.isValid(values[0], values[1], values[2]),
                        !wouldAskAgain(values[0], values[1], values[2]),
                        "usable and asked-about must always be opposites, for "
                                + values[0] + " / " + values[1] + " / " + values[2]);
            }
        });

        test("anything missing is asked for again", () -> {
            assertTrue(wouldAskAgain("", EMAIL, PHONE), "an empty name");
            assertTrue(wouldAskAgain(NAME, "", PHONE), "an empty email");
            assertTrue(wouldAskAgain(NAME, EMAIL, ""), "an empty phone");
            assertTrue(wouldAskAgain("", "", ""), "and an empty form");
        });

        test("a bad entry the customer already made is corrected, not lost", () -> {
            // What the customer typed has to reach the form, so the mistake they
            // made on the booking screen can be fixed rather than retyped.
            DetailsFormPanel form = new DetailsFormPanel(new javax.swing.JTextField(),
                    new javax.swing.JTextField(), new javax.swing.JTextField());
            form.fill(NAME, "amina example co ug", PHONE);
            assertTrue(wouldAskAgain(form.values().get(0), form.values().get(1),
                    form.values().get(2)),
                    "so the form opens");
            assertEquals("amina example co ug", form.values().get(1),
                    "and what they typed is still there to be corrected");
        });

        test("one wrong field does not lose the two that are right", () -> {
            DetailsFormPanel form = new DetailsFormPanel(new javax.swing.JTextField(),
                    new javax.swing.JTextField(), new javax.swing.JTextField());
            form.fill(NAME, "amina example co ug", PHONE);
            form.showProblems(form.check());
            form.fill(NAME, EMAIL, PHONE);
            assertFalse(wouldAskAgain(form.values().get(0), form.values().get(1),
                    form.values().get(2)),
                    "correcting the one field is enough");
            assertEquals(NAME, form.values().get(0), "and nothing else changed");
            assertEquals(PHONE, form.values().get(2), "the phone is untouched");
        });
    }
}
