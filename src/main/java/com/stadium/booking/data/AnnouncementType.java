package com.stadium.booking.data;

/**
 * The kinds of notice that can be put up about a venue or an event.
 *
 * <p>An enum: a fixed list of allowed values.
 *
 * <p>The important one is isBlocking(), further down. A CANCELLATION or an
 * EMERGENCY stops people booking; a NOTICE or a SCHEDULE CHANGE does not. That
 * single question decides whether the booking button is live or greyed out, so
 * it is answered in one place rather than repeated across the screens.
 */
public enum AnnouncementType {
    NOTICE("Notice", false),
    SCHEDULE_CHANGE("Schedule update", false),
    CANCELLATION("Event cancellation", true),
    EMERGENCY("Stadium emergency", true),
    SPECIAL_REQUEST("Special request", false);

    private final String label;
    private final boolean blocking;

    AnnouncementType(String label, boolean blocking) {
        this.label = label;
        this.blocking = blocking;
    }

    public String getLabel() {
        return label;
    }

    public boolean isBlocking() {
        return blocking;
    }
}
