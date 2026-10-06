package com.stadium.booking.booking;

/**
 * Whether a booking is confirmed or has been cancelled.
 *
 * <p>An enum: a fixed list of allowed values.
 *
 * <p>Cancelling does not delete a booking. It sets the status, and doing that
 * releases the seats back into the pool for that event while leaving the record
 * in place. That is deliberate: somebody paid money, and a record of the sale is
 * part of what they were given.
 */
public enum BookingStatus {
    CONFIRMED,
    CANCELLED
}
