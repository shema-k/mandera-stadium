package com.stadium.booking.data;

import java.util.Objects;

/**
 * One seat, and what it costs.
 *
 * <p>A seat is just two things: where it is, and its price. There are 45,202 of
 * them in the stadium, so they are kept in a map in BookingService rather than
 * written into a file — the file only holds the seats people have actually
 * bought.
 *
 * <p>"Immutable" means the seat is never changed once it has been made. A price
 * that quietly changed halfway through a booking would be very confusing, so
 * there is deliberately no way to do it.
 */
public final class Seat {
    private final SeatKey key;
    private final double price;

    public Seat(SeatKey key, double price) {
        this.key = Objects.requireNonNull(key, "key");
        if (price < 0) {
            throw new IllegalArgumentException("Seat price cannot be negative");
        }
        this.price = price;
    }

    public SeatKey getKey() {
        return key;
    }

    public double getPrice() {
        return price;
    }

    public String display() {
        return key.display();
    }

    @Override
    public String toString() {
        return display();
    }
}
