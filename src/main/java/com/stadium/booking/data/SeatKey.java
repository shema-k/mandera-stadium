package com.stadium.booking.data;

import java.io.Serializable;
import java.util.Objects;

/**
 * A seat's address, in the smallest form that still identifies it: which end,
 * which row, which number.
 *
 * <p>This is used everywhere a seat has to be put in a list, a set or a map.
 * There are 45,202 seats in the stadium, and the program has to answer
 * questions like "is this seat free?" thousands of times. Holding a tiny object
 * that means exactly one seat makes that fast.
 *
 * <p>It is created like this:
 *
 * <pre>
 *     SeatKey key = new SeatKey("B", 25, 40);   // end B, row 25, seat 40
 * </pre>
 *
 * <p>The four methods that look unusual — compareTo, equals, hashCode and
 * toString — are explained where they are.
 */
public final class SeatKey implements Comparable<SeatKey>, Serializable {

    /**
     * A fixed number that Java asks for when saving an object to a file.
     *
     * <p>It does not affect anything about how the program behaves. It tells
     * Java "this is the version of this class that was in use when you wrote
     * the file", so it can complain if it ever has to read a file written by a
     * much older version. Every serializable class has to have one.
     */
    private static final long serialVersionUID = 1L;

    private final String section;
    private final int row;
    private final int number;

    /**
     * Makes a seat address.
     *
     * <p>The two checks are there because a seat of "row 0" or "section ''"
     * would be a mistake that is very hard to track down later. Better to stop
     * straight away with a clear message.
     *
     * <p>The section is tidied up as it is stored: spaces removed and made
     * upper case. So new SeatKey("b", 25, 40) and new SeatKey("B", 25, 40) end
     * up as the same seat, which matters because the program looks seats up by
     * this object.
     */
    public SeatKey(String section, int row, int number) {
        if (section == null || section.trim().isEmpty()) {
            throw new IllegalArgumentException("Section is required");
        }
        if (row < 1 || number < 1) {
            throw new IllegalArgumentException("Row and seat number must be positive");
        }
        this.section = section.trim().toUpperCase();
        this.row = row;
        this.number = number;
    }

    /** Which end of the stand: "A", "B", "C" or "D". */
    public String getSection() {
        return section;
    }

    /** Which row, counted from the front. */
    public int getRow() {
        return row;
    }

    /** Which seat in that row, counted from the left. */
    public int getNumber() {
        return number;
    }

    /**
     * The seat written the way a customer would say it: "B25-40".
     *
     * <p>The "%02d" is worth reading twice. It means "print this number using
     * two digits, adding a zero in front if it is only one digit". So seat 3
     * prints as 03, which makes a row of seat numbers line up:
     *
     * <pre>
     *     B25-01  B25-02  B25-03
     * </pre>
     *
     * <p>Without the zero it would read B25-1, B25-2, B25-3 and would look
     * untidy on a receipt.
     */
    public String display() {
        return String.format("%s%d-%02d", section, row, number);
    }

    /**
     * Puts two seats in order: end A before end B, then row 2 before row 25.
     *
     * <p>Comparable means Java is allowed to sort these for us, which is how
     * seats end up in a tidy order on a receipt.
     *
     * <p>The method returns a number, and the rule is: return a negative number
     * if this seat comes first, a positive number if it comes second, and 0 if
     * they are the same seat. The exact value does not matter, only the sign.
     *
     * <p>It compares three things in turn, stopping as soon as one settles it.
     * Ends first, then rows, then seat numbers. Only if all three match are the
     * two keys the same seat.
     */
    @Override
    public int compareTo(SeatKey other) {
        int sectionComparison = section.compareTo(other.section);
        if (sectionComparison != 0) {
            return sectionComparison;
        }

        int rowComparison = Integer.compare(row, other.row);
        if (rowComparison != 0) {
            return rowComparison;
        }

        return Integer.compare(number, other.number);
    }

    /**
     * Are these two objects the same seat?
     *
     * <p>Java needs this before it can put anything in a HashSet or use it as a
     * key in a HashMap, which the program does constantly to hold the seats that
     * are already sold. Without a correct equals, the same seat booked twice
     * would look like two different seats.
     *
     * <p>"this == other" asks whether they are literally the same object in
     * memory. That is the quick yes, checked first.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SeatKey)) {
            return false;
        }

        // The cast is safe: we have just checked it really is a SeatKey.
        SeatKey that = (SeatKey) other;

        return row == that.row && number == that.number && section.equals(that.section);
    }

    /**
     * A number that stands for this seat, used by HashSet and HashMap.
     *
     * <p>The rule Java insists on is that two objects which are equal must
     * return the same number from this method. Objects.hash does exactly that:
     * it combines the three values into one. If this is wrong, a HashSet
     * silently loses seats, which is one of the hardest kinds of bug to find.
     */
    @Override
    public int hashCode() {
        return Objects.hash(section, row, number);
    }

    /**
     * What this looks like when printed or put in a message.
     *
     * <p>Delegating to display() means a seat in an error message reads
     * "B25-40" rather than "com.stadium.booking.data.SeatKey@1f2a3b".
     */
    @Override
    public String toString() {
        return display();
    }
}