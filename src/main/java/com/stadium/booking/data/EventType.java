package com.stadium.booking.data;

/**
 * Whether an event is a game or a concert.
 *
 * <p>An enum: a fixed list of allowed values. It decides which wording and which
 * coloured badge an event is shown with, and whether the details screen talks
 * about two teams or an artist.
 */
public enum EventType {
    GAME("Game"),
    CONCERT("Concert");

    private final String label;

    EventType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
