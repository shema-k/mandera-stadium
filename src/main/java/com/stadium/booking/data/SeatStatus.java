package com.stadium.booking.data;

/**
 * Whether a seat is free, already sold, or blocked from being booked.
 *
 * <p>An enum: a fixed list of allowed values. The seat map paints a different
 * colour for each, and BookingService decides which one applies before letting
 * anybody click.
 *
 * <p>Only three states live here. The two extra colours you see on the map —
 * "selected" and "held" — are not states of the seat in the world. They are
 * states of one customer's session, so they are held by SeatMapPanel and never
 * written to the database.
 */
public enum SeatStatus {
    AVAILABLE,
    BOOKED,
    BLOCKED
}
