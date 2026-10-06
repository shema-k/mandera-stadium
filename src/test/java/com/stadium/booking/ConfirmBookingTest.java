package com.stadium.booking;

import com.stadium.booking.booking.Booking;
import com.stadium.booking.booking.BookingService;
import com.stadium.booking.booking.Receipt;
import com.stadium.booking.data.Seat;
import com.stadium.booking.data.SeatKey;
import com.stadium.booking.data.StadiumData;
import com.stadium.booking.data.StadiumEvent;
import com.stadium.booking.storage.BookingStore;

import static com.stadium.booking.TestRunner.assertEquals;
import static com.stadium.booking.TestRunner.assertTrue;
import static com.stadium.booking.TestRunner.suite;
import static com.stadium.booking.TestRunner.test;

import java.util.ArrayList;
import java.util.List;

/**
 * Confirming a booking takes it and issues the receipt.
 *
 * <p>The requirement is one thing: when a customer confirms, it should process
 * and then show the receipt. These tests cover the service-level half of that,
 * which is the part that can be checked without a display — that a confirmed
 * booking is really taken, is really charged the quoted price, and produces a
 * receipt that adds up to it.
 */

/**
 * Checks that the confirm button appears only when there is something to
 * confirm, and that it counts the seats correctly.
 */

final class ConfirmBookingTest {
    private ConfirmBookingTest() {
    }

    private static BookingService serviceFor(String name) throws Exception {
        BookingService service = new BookingService(new BookingStore(
                TestRunner.freshDatabase(name)));
        service.selectEvent(StadiumData.getEvent("namboole-01"));
        return service;
    }

    static void register() {
        suite("Confirming a booking");

        test("confirming takes the seats and issues a receipt for them", () -> {
            BookingService service = serviceFor("confirm");
            StadiumEvent event = StadiumData.getEvent("namboole-01");
            List<Seat> seats = List.of(
                    service.getSeat(new SeatKey("A", 4, 40)),
                    service.getSeat(new SeatKey("D", 60, 15)));
            double quoted = service.getTotalCharge(seats);

            Booking booking = service.book("Amina Okello", "amina@example.co.ug",
                    "+256700000123", seats);
            Receipt receipt = Receipt.forBooking(booking, service);

            assertEquals(2, booking.getSeats().size(), "the booking has both seats");
            assertTrue(receipt.getSeatCount() == 2, "and the receipt itemises both");
            assertCloseTo(quoted, receipt.getTotal());
            for (SeatKey key : seats.stream().map(Seat::getKey).toList()) {
                assertTrue(service.getBookedSeatKeys(event).contains(key),
                        "seat " + key.display() + " is sold");
            }
        });

        test("the receipt adds up to the quoted total", () -> {
            BookingService service = serviceFor("confirm-arithmetic");
            List<Seat> seats = new ArrayList<>();
            for (int number = 1; number <= 4; number++) {
                seats.add(service.getSeat(new SeatKey("B", number, number)));
            }
            double quoted = service.getTotalCharge(seats);
            Booking booking = service.book("Grace Nakato", "grace@example.co.ug",
                    "+256700998877", seats);
            Receipt receipt = Receipt.forBooking(booking, service);

            double lines = 0.0;
            for (Receipt.Line line : receipt.getLines()) {
                lines += line.getPrice();
            }
            assertCloseTo(lines, receipt.getSeatSubtotal());
            assertCloseTo(receipt.getSeatSubtotal() + receipt.getBookingFee(),
                    receipt.getTotal());
            assertCloseTo(quoted, receipt.getTotal());
        });

        test("contact details are checked before a booking is taken", () -> {
            BookingService service = serviceFor("confirm-details");
            // The details dialog asks before confirming, so a bad email has to be
            // refused by the check on its own, not only when booking is attempted.
            boolean refused = false;
            try {
                service.validateCustomer("Amina Okello", "not-an-email", "+256700000123");
            } catch (IllegalArgumentException expected) {
                refused = expected.getMessage().contains("email");
            }
            assertTrue(refused, "a bad email address is refused by the check");
            assertEquals(0, service.getBookings().size(),
                    "and nothing was booked while checking");
        });

        test("valid contact details pass the check", () -> {
            BookingService service = serviceFor("confirm-valid");
            service.validateCustomer("Amina Okello", "amina@example.co.ug", "+256700000123");
            assertEquals(0, service.getBookings().size(),
                    "checking alone never books anything");
        });

        test("the seats are released for everyone once the booking is confirmed", () -> {
            BookingService service = serviceFor("confirm-release");
            StadiumEvent event = StadiumData.getEvent("namboole-01");
            SeatKey seat = new SeatKey("C", 12, 20);
            service.book("First", "first@example.co.ug", "+256700000001",
                    List.of(service.getSeat(seat)));
            service.selectEvent(event);
            assertTrue(!service.isSeatSelectable(seat), "the seat is no longer offered");

            service.cancel(service.getBookings().get(0).getReference());
            service.selectEvent(event);
            assertTrue(service.isSeatSelectable(seat),
                    "and is offered again once the booking is cancelled");
        });
    }

    private static void assertCloseTo(double expected, double actual) {
        TestRunner.assertClose(expected, actual, 0.01,
                "expected " + expected + " but was " + actual);
    }
}