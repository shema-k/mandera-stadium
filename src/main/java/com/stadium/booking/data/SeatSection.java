package com.stadium.booking.data;

/**
 * One end of the stand.
 *
 * <p>The stadium has four of these — A, B, C and D — and each one is a rectangle
 * of seats. This class describes the size of that rectangle and what a seat in
 * the front row costs.
 *
 * <pre>
 *     new SeatSection("A", "VIP Box", 114, 99, 250000)
 *                   ^      ^          ^    ^     ^
 *                   |      |          |    |     what a front row seat costs
 *                   |      |          |    how many seats in each row
 *                   |      |          how many rows
 *                   |      the name shown on screen
 *                   which end: A, B, C or D
 * </pre>
 *
 * <p>There are 114 rows of 99 seats, so 11,286 seats in this end. All four ends
 * together give the stadium's capacity of 45,202.
 *
 * <p>The actual price of a seat is worked out in BookingService, because it
 * depends on the row and on which event it is for. basePrice here is only the
 * starting figure — the price of a front row seat for an ordinary match.
 */
public final class SeatSection {

    /** Which end: "A", "B", "C" or "D". */
    private final String id;

    /** The name shown on screen, such as "VIP Box". */
    private final String label;

    /** How many rows deep this end is. */
    private final int rows;

    /** How many seats are in each row. */
    private final int seatsPerRow;

    /** What a front row seat costs, in shillings. */
    private final double basePrice;

    /**
     * Makes one end of the stand.
     *
     * <p>The three checks refuse values that could not possibly be right: an end
     * with no name, an end with no rows, or one that costs a negative amount. A
     * negative price would quietly make the program charge people money.
     *
     * <p>The id is made upper case, so "b" and "B" cannot end up as two
     * different ends.
     */
    public SeatSection(String id, String label, int rows, int seatsPerRow, double basePrice) {
        if (id == null || id.trim().isEmpty() || label == null || label.trim().isEmpty()) {
            throw new IllegalArgumentException("Section id and label are required");
        }
        if (rows < 1 || seatsPerRow < 1) {
            throw new IllegalArgumentException("Section rows and seats must be positive");
        }
        if (basePrice < 0) {
            throw new IllegalArgumentException("Section price cannot be negative");
        }

        this.id = id.trim().toUpperCase();
        this.label = label.trim();
        this.rows = rows;
        this.seatsPerRow = seatsPerRow;
        this.basePrice = basePrice;
    }

    /** Which end: "A", "B", "C" or "D". */
    public String getId() {
        return id;
    }

    /** The name shown on screen, such as "VIP Box". */
    public String getLabel() {
        return label;
    }

    /** How many rows deep this end is. */
    public int getRows() {
        return rows;
    }

    /** How many seats are in each row. */
    public int getSeatsPerRow() {
        return seatsPerRow;
    }

    /** What a front row seat costs, in shillings. */
    public double getBasePrice() {
        return basePrice;
    }

    /** How many seats are in this end altogether: rows multiplied by seats. */
    public int getSeatCount() {
        return rows * seatsPerRow;
    }

    /**
     * The words someone might type to search for this end, as one lower-case
     * string.
     *
     * <p>So searching for "vip" or for "b" both find something sensible. The
     * search box compares what was typed against this.
     */
    public String searchableText() {
        return (id + " " + label).toLowerCase();
    }

    /** How this end is written when printed or put in a message: "A - VIP Box". */
    @Override
    public String toString() {
        return id + " - " + label;
    }
}