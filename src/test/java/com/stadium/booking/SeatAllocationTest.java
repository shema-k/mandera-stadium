package com.stadium.booking;

import com.stadium.booking.booking.Booking;
import com.stadium.booking.booking.BookingService;
import com.stadium.booking.data.Seat;
import com.stadium.booking.data.SeatKey;
import com.stadium.booking.data.StadiumData;
import com.stadium.booking.data.StadiumEvent;
import com.stadium.booking.storage.BookingStore;

import static com.stadium.booking.TestRunner.assertEquals;
import static com.stadium.booking.TestRunner.assertTrue;
import static com.stadium.booking.TestRunner.freshDatabase;
import static com.stadium.booking.TestRunner.suite;
import static com.stadium.booking.TestRunner.test;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * The rule that a booking contains exactly the seats the customer chose.
 *
 * <p>These tests replace an earlier rule that quietly moved a customer's seats
 * around so a booking would be spread across all four sections. That was wrong
 * for a seat-booking system: the customer picked a seat, was quoted a price for
 * it, and then got a different seat at the point of payment.
 */
final class SeatAllocationTest {
    private SeatAllocationTest() {
    }

    private static BookingService serviceFor(String name) throws Exception {
        BookingService service = new BookingService(new BookingStore(freshDatabase(name)));
        service.selectEvent(StadiumData.getEvent("namboole-01"));
        return service;
    }

    private static List<Seat> consecutive(BookingService service, String sectionId, int count) {
        List<Seat> seats = new ArrayList<>();
        for (int number = 1; number <= count; number++) {
            seats.add(service.getSeat(new SeatKey(sectionId, 1, number)));
        }
        return seats;
    }

    static void register() {
        suite("Exact seats");

        test("the seats chosen are the seats booked", () -> {
            BookingService service = serviceFor("exact");
            StadiumEvent event = StadiumData.getEvent("namboole-01");
            List<Seat> chosen = List.of(
                    service.getSeat(new SeatKey("A", 3, 12)),
                    service.getSeat(new SeatKey("C", 7, 40)),
                    service.getSeat(new SeatKey("D", 2, 5)));
            Booking booking = service.book("Exact User", "s@example.co.ug", "+256700000000", chosen);

            assertEquals(3, booking.getSeats().size(), "booked seat count");
            for (Seat seat : chosen) {
                assertTrue(booking.getSeats().contains(seat.getKey()),
                        "chosen seat not booked: " + seat.getKey().display());
                assertTrue(service.getBookedSeatKeys(event).contains(seat.getKey()),
                        "seat not marked taken: " + seat.getKey().display());
            }
        });

        test("a customer may keep every seat in one stand", () -> {
            BookingService service = serviceFor("single-stand");
            StadiumEvent event = StadiumData.getEvent("namboole-01");
            List<Seat> chosen = consecutive(service, "A", 4);
            Booking booking = service.book("One Stand", "s@example.co.ug", "+256700000000", chosen);

            assertEquals(4, booking.getSeats().size(), "booked seat count");
            for (Seat seat : chosen) {
                assertTrue(service.getBookedSeatKeys(event).contains(seat.getKey()),
                        "seat not marked taken: " + seat.getKey().display());
            }
        });

        test("the quoted total is charged for the seats chosen", () -> {
            BookingService service = serviceFor("total");
            List<Seat> chosen = List.of(
                    service.getSeat(new SeatKey("A", 1, 1)),
                    service.getSeat(new SeatKey("B", 40, 20)));
            double expected = service.getTotalCharge(chosen);
            Booking booking = service.book("Priced User", "s@example.co.ug", "+256700000000", chosen);
            assertEquals(expected, booking.getTotal(), "booked total");
        });

        test("no duplicate or unavailable seat is booked", () -> {
            BookingService service = serviceFor("clean");
            StadiumEvent event = StadiumData.getEvent("namboole-01");
            List<Seat> chosen = List.of(
                    service.getSeat(new SeatKey("A", 5, 5)),
                    service.getSeat(new SeatKey("A", 5, 6)),
                    service.getSeat(new SeatKey("D", 9, 60)));
            Booking booking = service.book("Clean User", "s@example.co.ug", "+256700000000", chosen);

            Set<SeatKey> seen = new LinkedHashSet<>();
            for (SeatKey key : booking.getSeats()) {
                assertTrue(seen.add(key), "duplicate seat " + key.display());
            }
            assertEquals(chosen.size(), booking.getSeats().size(), "no seat lost or added");
            for (Seat seat : chosen) {
                assertTrue(service.getBookedSeatKeys(event).contains(seat.getKey()),
                        "seat not marked taken: " + seat.getKey().display());
            }
        });

        test("seats already taken are refused, not swapped", () -> {
            BookingService service = serviceFor("taken");
            StadiumEvent event = StadiumData.getEvent("namboole-01");
            List<Seat> first = List.of(service.getSeat(new SeatKey("B", 6, 6)));
            service.book("First Customer", "first@example.co.ug", "+256700000001", first);

            service.selectEvent(event);
            List<Seat> contested = List.of(
                    service.getSeat(new SeatKey("B", 6, 6)),
                    service.getSeat(new SeatKey("D", 3, 3)));
            try {
                service.book("Second Customer", "second@example.co.ug", "+256700000002", contested);
                throw new AssertionError("booking a taken seat should have been refused");
            } catch (IllegalArgumentException expected) {
                assertTrue(expected.getMessage().contains("B6-06"),
                        "should name the taken seat, said: " + expected.getMessage());
                assertTrue(expected.getMessage().contains("already been booked"),
                        "should say it is sold, said: " + expected.getMessage());
            }
            // The second customer's good seat must still be free: refusing a
            // booking must not silently take the seats that were still on sale.
            assertTrue(service.isSeatSelectable(new SeatKey("D", 3, 3)),
                    "a refused booking must not consume the seats that were free");
        });

        test("a seat on another venue's plan is refused clearly", () -> {
            BookingService service = serviceFor("not-a-seat");
            // Namboole's section A has 99 seats per row, so 400 is not a real seat.
            SeatKey imaginary = new SeatKey("A", 1, 400);
            String reason = service.unavailableReason(imaginary);
            assertTrue(reason.contains("A1-400"), "names the seat: " + reason);
            assertTrue(reason.contains("not a seat at this stadium"),
                    "explains it is not a real seat: " + reason);
        });

        test("refusing a booking keeps the sold seat sold", () -> {
            BookingService service = serviceFor("still-sold");
            StadiumEvent event = StadiumData.getEvent("namboole-01");
            service.book("First Customer", "first@example.co.ug", "+256700000001",
                    List.of(service.getSeat(new SeatKey("C", 4, 4))));
            try {
                service.book("Second Customer", "second@example.co.ug", "+256700000002",
                        List.of(service.getSeat(new SeatKey("C", 4, 4)),
                                service.getSeat(new SeatKey("A", 9, 9))));
                throw new AssertionError("booking a taken seat should have been refused");
            } catch (IllegalArgumentException expected) {
                assertTrue(expected.getMessage().contains("C4-04"),
                        "names the sold seat: " + expected.getMessage());
            }
            service.selectEvent(event);
            assertEquals(1, service.getBookedSeatKeys(event).size(),
                    "only the first customer's seat is sold");
        });
    }
}
