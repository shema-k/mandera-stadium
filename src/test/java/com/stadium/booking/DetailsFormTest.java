package com.stadium.booking;

import com.stadium.booking.booking.CustomerDetails;
import com.stadium.booking.ui.DetailsFormPanel;

import static com.stadium.booking.TestRunner.assertEquals;
import static com.stadium.booking.TestRunner.assertFalse;
import static com.stadium.booking.TestRunner.assertTrue;
import static com.stadium.booking.TestRunner.suite;
import static com.stadium.booking.TestRunner.test;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * The details form: what a customer actually meets when their typing is refused.
 *
 * <p>These are here because two faults got through the rule tests untouched. The
 * rules were fine both times; the form was not. It cleared its messages before
 * showing the dialog again, so it came back with the same mistakes and no
 * explanation of them — the exact dead end the form exists to remove. And its
 * rows were stepped wrongly, drawing the fields on top of each other and the
 * heading over the first one.
 *
 * <p>Building the form for real and looking at it is the only way to catch
 * either, so that is what these do.
 */

/**
 * Checks that the contact form marks the wrong field, keeps what was
 * typed, and puts the cursor where it needs fixing.
 */

final class DetailsFormTest {
    private DetailsFormTest() {
    }

    private static final String NAME = "Amina Okello";
    private static final String EMAIL = "amina@example.co.ug";
    private static final String PHONE = "+256 700 123 456";

    /** Swing needs a display; without one these have nothing to inspect. */
    private static boolean canBuild() {
        return !GraphicsEnvironment.isHeadless();
    }

    private static JTextField[] boxes() {
        return new JTextField[]{new JTextField(), new JTextField(), new JTextField()};
    }

    /**
     * Where a part of the form ended up, in the form's own coordinates.
     *
     * <p>Each field sits inside a row panel, so its own bounds are relative to
     * that panel and say nothing about where it appears. Measured from the form
     * itself, the positions can be compared.
     */
    private static Rectangle place(JPanel panel, java.awt.Component part) {
        return javax.swing.SwingUtilities.convertRectangle(
                part.getParent(), part.getBounds(), panel);
    }

    private static DetailsFormPanel form() {
        JTextField[] fields = boxes();
        return new DetailsFormPanel(fields[0], fields[1], fields[2]);
    }

    private static DetailsFormPanel formFilled() {
        JTextField[] fields = boxes();
        DetailsFormPanel form = new DetailsFormPanel(fields[0], fields[1], fields[2]);
        form.fill(NAME, EMAIL, PHONE);
        return form;
    }

    static void register() {
        suite("The details form");

        test("a correct form greets the customer with no complaint", () -> {
            if (!canBuild()) {
                return;
            }
            DetailsFormPanel form = formFilled();
            assertTrue(form.check().isEmpty(), "the details are usable");
            assertEquals("", form.messageFor(CustomerDetails.Field.NAME),
                    "nothing is said against the name");
            assertEquals("", form.messageFor(CustomerDetails.Field.EMAIL),
                    "nothing is said against the email");
            assertEquals("", form.messageFor(CustomerDetails.Field.PHONE),
                    "nothing is said against the phone");
        });

        test("every wrong field is marked with a message of its own", () -> {
            if (!canBuild()) {
                return;
            }
            DetailsFormPanel form = form();
            form.fill("A", "amina example co ug", "0772abc123");
            form.showProblems(form.check());
            for (CustomerDetails.Field field : CustomerDetails.Field.values()) {
                assertFalse(form.messageFor(field).isEmpty(),
                        field + " must be marked when it is wrong");
                assertTrue(form.messageFor(field).length() > 15,
                        field + " must explain itself, said: "
                                + form.messageFor(field));
            }
        });

        test("a corrected field stops carrying its old complaint", () -> {
            if (!canBuild()) {
                return;
            }
            // The case that used to leave a customer staring at a message about
            // a mistake they had already fixed, with no way to tell it was stale.
            DetailsFormPanel form = form();
            form.fill("", "bad", "abc");
            form.showProblems(form.check());
            assertFalse(form.messageFor(CustomerDetails.Field.NAME).isEmpty(),
                    "the empty name is marked to begin with");
            form.fill(NAME, "bad", "abc");
            form.showProblems(form.check());
            assertEquals("", form.messageFor(CustomerDetails.Field.NAME),
                    "and stops being marked once it is filled in");
            assertFalse(form.messageFor(CustomerDetails.Field.EMAIL).isEmpty(),
                    "while the email is still wrong");
        });

        test("what the customer typed survives being told they were wrong", () -> {
            if (!canBuild()) {
                return;
            }
            // The whole reason the form stays open. A refused entry used to cost
            // the customer everything they had written.
            DetailsFormPanel form = form();
            form.fill("A", "amina example co ug", "0772abc123");
            form.showProblems(form.check());
            form.showProblems(form.check());
            form.showProblems(form.check());
            assertEquals("A", form.values().get(0), "the name is still there");
            assertEquals("amina example co ug", form.values().get(1),
                    "the email is still there, exactly as typed");
            assertEquals("0772abc123", form.values().get(2), "and the phone");
        });

        test("the cursor is sent to the first field that needs fixing", () -> {
            if (!canBuild()) {
                return;
            }
            DetailsFormPanel form = formFilled();
            // Correct name, two wrong fields: focus must land on the email, the
            // first of them in reading order, so they can just start typing.
            form.fill(NAME, "amina example co ug", "0772abc123");
            JTextField target = form.showProblems(form.check());
            assertTrue(target == form.fieldFor(CustomerDetails.Field.EMAIL),
                    "focus goes to the email, the first field still wrong");
        });

        test("a problem with nothing wrong to point at is still shown", () -> {
            if (!canBuild()) {
                return;
            }
            // The safety net for the booking rules refusing something the form
            // accepted, which should be impossible but must not be a dead end.
            DetailsFormPanel form = formFilled();
            JTextField target = form.markAll("Those details cannot be used");
            assertTrue(target != null, "there is somewhere to put the cursor");
            for (CustomerDetails.Field field : CustomerDetails.Field.values()) {
                assertEquals("Those details cannot be used", form.messageFor(field),
                        field + " must not be left unmarked, said: "
                                + form.messageFor(field));
            }
            form.clearProblems();
            for (CustomerDetails.Field field : CustomerDetails.Field.values()) {
                assertEquals("", form.messageFor(field),
                        field + " must be clearable, said: " + form.messageFor(field));
            }
        });

        test("a form opened for details that are already wrong marks them at once", () -> {
            if (!canBuild()) {
                return;
            }
            // The case the customer meets after typing a bad email straight onto
            // the booking screen: the boxes are not empty, but they are not usable
            // either. The form opens for them and must immediately say what is
            // wrong. Opening it and waiting to be pressed again would leave the
            // customer with a full form, a wrong entry and no explanation.
            DetailsFormPanel form = form();
            form.fill(NAME, "amina example co ug", "0772abc123");
            form.showProblems(form.check());
            assertFalse(form.messageFor(CustomerDetails.Field.EMAIL).isEmpty(),
                    "the email must be marked on the first showing of the form, said: "
                            + form.messageFor(CustomerDetails.Field.EMAIL));
            assertFalse(form.messageFor(CustomerDetails.Field.PHONE).isEmpty(),
                    "and so must the phone, said: "
                            + form.messageFor(CustomerDetails.Field.PHONE));
            assertEquals("", form.messageFor(CustomerDetails.Field.NAME),
                    "while the name, which is right, must not be complained about");
        });

        test("a message is not shown before there is something to say", () -> {
            if (!canBuild()) {
                return;
            }
            // Reserved space, empty text. Marking a field must not make the form
            // jump about while the customer is reading what went wrong.
            DetailsFormPanel form = formFilled();
            JPanel panel = form.panel();
            int before = panel.getPreferredSize().height;
            form.showProblems(CustomerDetails.check("", "", ""));
            assertEquals(before, panel.getPreferredSize().height,
                    "the form must not change height when fields are marked");
        });

        test("the heading and the three fields stack without overlapping", () -> {
            if (!canBuild()) {
                return;
            }
            // The rows were stepped wrongly, which drew the heading over the name
            // box. Every position is measured in the form's own coordinates:
            // each box sits inside a row panel, so its own bounds say nothing
            // about where it ended up.
            DetailsFormPanel form = formFilled();
            JPanel panel = form.panel();
            panel.setSize(new Dimension(460, 320));
            panel.doLayout();
            Rectangle title = place(panel, form.heading());
            Rectangle name = place(panel, form.fieldFor(CustomerDetails.Field.NAME));
            Rectangle email = place(panel, form.fieldFor(CustomerDetails.Field.EMAIL));
            Rectangle phone = place(panel, form.fieldFor(CustomerDetails.Field.PHONE));
            assertTrue(title.y + title.height <= name.y,
                    "the heading must finish above the name box: " + title
                            + " then " + name);
            assertTrue(name.y + name.height <= email.y,
                    "the name must finish above the email: " + name + " then " + email);
            assertTrue(email.y + email.height <= phone.y,
                    "the email must finish above the phone: " + email + " then " + phone);
            assertTrue(panel.getComponentCount() == 4,
                    "a heading and three field rows, found "
                            + panel.getComponentCount());
        });

        test("no two parts of the form cover each other", () -> {
            if (!canBuild()) {
                return;
            }
            // The general form of the fault: two things drawn on one row. Checked
            // across every part rather than by name, so a field added later is
            // covered without anyone remembering to add it here.
            DetailsFormPanel form = formFilled();
            JPanel panel = form.panel();
            panel.setSize(new Dimension(460, 320));
            panel.doLayout();
            List<Rectangle> placed = new ArrayList<>();
            List<String> names = new ArrayList<>();
            placed.add(place(panel, form.heading()));
            names.add("heading");
            for (CustomerDetails.Field field : CustomerDetails.Field.values()) {
                placed.add(place(panel, form.fieldFor(field)));
                names.add(field.name());
            }
            for (int i = 0; i < placed.size(); i++) {
                for (int j = i + 1; j < placed.size(); j++) {
                    assertFalse(placed.get(i).intersects(placed.get(j)),
                            names.get(i) + " covers " + names.get(j) + ": "
                                    + placed.get(i) + " and " + placed.get(j));
                }
            }
        });

        test("each field has room for a full message", () -> {
            if (!canBuild()) {
                return;
            }
            // A message clipped mid-word is worse than none, because the example
            // at the end of it is the part that tells the customer what to do.
            DetailsFormPanel form = formFilled();
            Map<CustomerDetails.Field, String> longest = new EnumMap<>(
                    CustomerDetails.Field.class);
            for (CustomerDetails.Problem problem
                    : CustomerDetails.check("a", "amina example co ug", "0772abc")
                    .values()) {
                longest.put(problem.getField(), problem.describe());
            }
            form.showProblems(CustomerDetails.check("a", "amina example co ug",
                    "0772abc"));
            for (Map.Entry<CustomerDetails.Field, String> entry : longest.entrySet()) {
                JLabel area = null;
                for (java.awt.Component child : form.panel().getComponents()) {
                    if (child instanceof JPanel row) {
                        for (java.awt.Component part : row.getComponents()) {
                            if (part instanceof JLabel label
                                    && entry.getValue().equals(label.getText())) {
                                area = label;
                            }
                        }
                    }
                }
                assertTrue(area != null, entry.getKey() + " shows a message");
                assertTrue(area.getPreferredSize().width >= 420,
                        entry.getKey() + " message area is wide enough for "
                                + entry.getValue().length() + " characters, width was "
                                + area.getPreferredSize().width);
            }
        });

        test("what the form reads back is what is in the boxes", () -> {
            if (!canBuild()) {
                return;
            }
            DetailsFormPanel form = form();
            form.fill(NAME, EMAIL, PHONE);
            assertEquals(NAME, form.values().get(0), "the name");
            assertEquals(EMAIL, form.values().get(1), "the email");
            assertEquals(PHONE, form.values().get(2), "the phone");
            assertTrue(form.isFilled(), "and it knows all three are filled in");
            form.fill(NAME, EMAIL, "");
            assertFalse(form.isFilled(), "an empty box means it is not filled in");
        });

        test("the boxes the form uses are the ones the booking uses", () -> {
            if (!canBuild()) {
                return;
            }
            // If the form kept its own copy, what was confirmed in it would not
            // be what got booked, and the receipt would carry different details.
            JTextField[] shared = boxes();
            DetailsFormPanel form = new DetailsFormPanel(shared[0], shared[1], shared[2]);
            form.fill(NAME, EMAIL, PHONE);
            assertEquals(NAME, shared[0].getText(),
                    "the name reaches the box the booking reads");
            assertEquals(EMAIL, shared[1].getText(), "and so does the email");
            assertEquals(PHONE, shared[2].getText(), "and the phone");
        });

        test("the form is told what each box is for", () -> {
            if (!canBuild()) {
                return;
            }
            // Without this a screen reader announces three unlabelled boxes and
            // the customer cannot tell which is which.
            DetailsFormPanel form = formFilled();
            for (CustomerDetails.Field field : CustomerDetails.Field.values()) {
                JTextField box = form.fieldFor(field);
                assertTrue(box.getAccessibleContext().getAccessibleName() != null,
                        field + " must have a name a screen reader can read");
                assertTrue(String.valueOf(
                        box.getAccessibleContext().getAccessibleDescription())
                        .length() > 10,
                        field + " must be described, so its format is known before "
                                + "a mistake is made");
            }
        });
    }
}
