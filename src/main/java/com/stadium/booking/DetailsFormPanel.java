package com.stadium.booking;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * The contact details form, with a place beside every field to say what is
 * wrong with it.
 *
 * <p>The point of this class is that a mistake does not cost the customer what
 * they have already typed. The form is built once and shown again and again
 * while the entry is wrong, so anything typed survives every attempt, and each
 * field that needs fixing is marked with a message naming a format that would
 * be accepted. Before this, the form closed on the first mistake and explained
 * the problem afterwards, which left an empty form and a warning.
 *
 * <p>Kept apart from the dialog that shows it so the behaviour can be checked
 * without a modal window: building the form, marking fields and reading the
 * values back are all ordinary calls here.
 */
final class DetailsFormPanel {
    /** Where the longest message has to fit without being cut off. */
    private static final int MESSAGE_WIDTH = 430;
    private static final int MESSAGE_HEIGHT = 14;

    private final JPanel panel = new JPanel(new GridBagLayout());
    private final JLabel heading;
    private final JTextField nameField;
    private final JTextField emailField;
    private final JTextField phoneField;
    private final Map<CustomerDetails.Field, JLabel> messages =
            new EnumMap<>(CustomerDetails.Field.class);

    /**
     * Builds the form around the given fields.
     *
     * <p>The fields are passed in rather than created here because they are the
     * application's own contact fields, shared with the booking screen, so what
     * is confirmed in this form is the same thing that is booked.
     *
     * @param nameField  the box for the customer's name
     * @param emailField the box for the address the receipt goes to
     * @param phoneField the box for the number the venue can reach them on
     */
    DetailsFormPanel(JTextField nameField, JTextField emailField, JTextField phoneField) {
        this.nameField = nameField;
        this.emailField = emailField;
        this.phoneField = phoneField;
        panel.setOpaque(false);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(3, 0, 3, 10);
        constraints.gridwidth = 2;
        this.heading = new JLabel(Messages.get("booking.detailsIntro"));
        heading.setForeground(Theme.current().text());
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 13f));
        constraints.gridy = 0;
        panel.add(heading, constraints);
        // The first field goes on the row after the heading. Sharing one
        // constraints object across the calls means each one has to advance the
        // row itself, and getting that wrong stacked the fields on top of each
        // other and drew the heading over the first one.
        constraints.gridy = 1;

        addRow(constraints, CustomerDetails.Field.NAME, Messages.get("form.name"), nameField,
                Messages.get("form.nameHint"), Messages.get("form.nameExample"));
        addRow(constraints, CustomerDetails.Field.EMAIL, Messages.get("bookings.field.email"), emailField,
                Messages.get("form.emailHint"), Messages.get("form.emailExample"));
        addRow(constraints, CustomerDetails.Field.PHONE, Messages.get("bookings.field.phone"), phoneField,
                Messages.get("form.phoneHint"), Messages.get("form.phoneExample"));
    }

    /**
     * Adds one field: its name above it, a message area below it, each in the
     * same row so the message sits directly under the box it is about.
     *
     * @param field   which field this row is, so a problem can find its message
     * @param hint    what the field is for, announced to a screen reader
     * @param example the format that would be accepted
     */
    private void addRow(GridBagConstraints constraints, CustomerDetails.Field field,
                        String labelText, JTextField box, String hint, String example) {
        JPanel fieldBox = new JPanel(new BorderLayout(0, 2));
        fieldBox.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setForeground(Theme.current().muted());
        label.setFont(label.getFont().deriveFont(Font.BOLD, 10f));
        fieldBox.add(label, BorderLayout.NORTH);

        styleTextField(box);
        box.getAccessibleContext().setAccessibleName(labelText);
        box.getAccessibleContext().setAccessibleDescription(hint + ". " + example);
        fieldBox.add(box, BorderLayout.CENTER);

        JLabel message = new JLabel();
        message.setForeground(Theme.current().danger());
        message.setFont(message.getFont().deriveFont(Font.PLAIN, 10f));
        message.setText("");
        // Reserved whether or not there is anything to say, so marking a field
        // does not shift the fields below it while the customer is reading it.
        message.setPreferredSize(new Dimension(MESSAGE_WIDTH, MESSAGE_HEIGHT));
        fieldBox.add(message, BorderLayout.SOUTH);
        messages.put(field, message);

        constraints.gridx = 0;
        constraints.gridwidth = 2;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(fieldBox, constraints);
        constraints.gridy = constraints.gridy + 1;
    }

    private void styleTextField(JTextField field) {
        field.setFont(field.getFont().deriveFont(Font.PLAIN, 13f));
        field.setForeground(Theme.current().text());
        field.setBackground(Theme.current().field());
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.current().border()),
                new EmptyBorder(8, 10, 8, 10)));
        field.setPreferredSize(new Dimension(180, 37));
    }

    /** The form itself, for handing to a dialog. */
    JPanel panel() {
        return panel;
    }

    /** The heading above the fields. */
    JLabel heading() {
        return heading;
    }

    /**
     * Marks every field that needs fixing and sends the cursor to the first one.
     *
     * <p>Focus is the part that matters most in practice. Without it the cursor
     * lands wherever it happened to be, so a customer told to fix their email
     * has to find the box again before they can type into it.
     *
     * <p>All messages are cleared first, so a field corrected since the last
     * attempt stops carrying a complaint the customer has already acted on.
     *
     * @return the first field that needs attention, or null when nothing is wrong
     */
    JTextField showProblems(Map<CustomerDetails.Field, CustomerDetails.Problem> problems) {
        clearProblems();
        JTextField firstBad = null;
        for (CustomerDetails.Problem problem : problems.values()) {
            JLabel message = messages.get(problem.getField());
            if (message != null) {
                message.setText(problem.describe());
            }
            JTextField box = fieldFor(problem.getField());
            if (box != null && firstBad == null) {
                firstBad = box;
            }
        }
        JTextField target = firstBad;
        if (target != null) {
            SwingUtilities.invokeLater(() -> {
                target.requestFocusInWindow();
                target.selectAll();
            });
        }
        return firstBad;
    }

    /** Takes every message away, for a form that is being shown afresh. */
    void clearProblems() {
        for (JLabel message : messages.values()) {
            message.setText("");
        }
    }

    /**
     * Shows one plain message against every field.
     *
     * <p>For a problem that is not about one particular box, so it cannot be
     * pointed at a single field. A test asserts the form and the booking rules
     * always agree, so this is a safety net rather than a normal path.
     *
     * @return the first field, for focus
     */
    JTextField markAll(String message) {
        clearProblems();
        for (JLabel area : messages.values()) {
            area.setText(message);
        }
        JTextField target = nameField;
        SwingUtilities.invokeLater(() -> {
            target.requestFocusInWindow();
            target.selectAll();
        });
        return target;
    }

    /** The message currently shown against a field, or "" when it is clean. */
    String messageFor(CustomerDetails.Field field) {
        JLabel message = messages.get(field);
        return message == null ? "" : String.valueOf(message.getText());
    }

    /** What is in the three boxes, as the rules see it. */
    List<String> values() {
        return List.of(nameField.getText(), emailField.getText(), phoneField.getText());
    }

    /** The box for a field, so a caller can read it without reaching for Swing. */
    JTextField fieldFor(CustomerDetails.Field field) {
        switch (field) {
            case NAME:
                return nameField;
            case EMAIL:
                return emailField;
            case PHONE:
                return phoneField;
            default:
                return null;
        }
    }

    /** Fills the boxes, as if the customer had typed them. */
    void fill(String name, String email, String phone) {
        nameField.setText(name);
        emailField.setText(email);
        phoneField.setText(phone);
    }

    /** Checks what is in the boxes and returns the problems with it. */
    Map<CustomerDetails.Field, CustomerDetails.Problem> check() {
        return CustomerDetails.check(nameField.getText(), emailField.getText(),
                phoneField.getText());
    }

    /** True when all three boxes have something in them. */
    boolean isFilled() {
        for (String value : values()) {
            if (value == null || value.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /** The message colour, so a screen can match it if it needs to. */
    static Color problemColour() {
        return Theme.current().danger();
    }
}
