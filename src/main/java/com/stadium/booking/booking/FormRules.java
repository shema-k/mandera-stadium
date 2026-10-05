package com.stadium.booking.booking;
/**
 * The rules for the short pieces of free text a customer types into a form.
 *
 * <p>Separate from {@link CustomerDetails}, which is only about the contact
 * details, because these are about the wording the customer chose rather than
 * about who they are.
 *
 * <p>The reason these exist as rules rather than being checked where they are
 * saved is the label one. A label over the limit used to be quietly shortened
 * when it was saved, so the customer typed a name, saw it accepted, and found
 * later that the first part of it was gone. A refusal the customer can see and
 * correct is better than a silent edit they cannot.
 */
public final class FormRules {
    /** The longest a saved selection's label may be. */
    public static final int MAX_LABEL_LENGTH = 120;

    /** The fewest characters a special request may have. */
    public static final int MIN_REQUEST_LENGTH = 5;

    private FormRules() {
    }

    /**
     * Checks a label for a saved selection.
     *
     * @return what is wrong with it, or null when it is usable
     */
    public static String labelProblem(String label) {
        String value = label == null ? "" : label.trim();
        if (value.isEmpty()) {
            // Not a fault. A selection with no name of its own is saved as
            // "My seats", which is what an unnamed one should be called.
            return null;
        }
        if (value.length() > MAX_LABEL_LENGTH) {
            // Said with the number, because "too long" on its own leaves the
            // customer guessing how much to cut.
            return "That label is " + value.length() + " characters. Please keep it to "
                    + MAX_LABEL_LENGTH + " or fewer";
        }
        return null;
    }

    /**
     * Shortens a label that is over the limit, for the store to fall back on.
     *
     * <p>The form refuses an over-long label rather than shortening it, so this
     * is only reached by a caller that skipped the form. It exists so the value
     * saved is never silently and unexpectedly clipped further than the stated
     * limit.
     */
    public static String trimLabel(String label) {
        String value = label == null || label.isBlank() ? "My seats" : label.trim();
        if (value.length() > MAX_LABEL_LENGTH) {
            value = value.substring(0, MAX_LABEL_LENGTH);
        }
        return value;
    }

    /**
     * Checks the wording of a special request.
     *
     * @return what is wrong with it, or null when it is usable
     */
    public static String requestProblem(String message) {
        String value = message == null ? "" : message.trim();
        if (value.length() < MIN_REQUEST_LENGTH) {
            return "Please describe the request in a little more detail, at least "
                    + MIN_REQUEST_LENGTH + " characters";
        }
        return null;
    }
}
