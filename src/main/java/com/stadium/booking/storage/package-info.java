package com.stadium.booking.storage;

/**
 * Saving to disk.
 *
 * How a booking survives the application being closed. Everything lives in one
 * H2 file in the same folder as the program.
 *
 * <p>BookingStore owns the three tables and all the SQL. The table that keeps one
 * row per seat is the important one: its primary key is what stops the same seat
 * being sold twice.
 *
 * <p>The files here: {@code BookingStore}, {@code Database}.
 */
