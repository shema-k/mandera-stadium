package com.stadium.booking;

import com.stadium.booking.booking.Booking;
import com.stadium.booking.booking.BookingService;
import com.stadium.booking.data.Seat;
import com.stadium.booking.data.SeatKey;
import com.stadium.booking.data.Stadium;
import com.stadium.booking.data.StadiumData;
import com.stadium.booking.data.StadiumEvent;
import com.stadium.booking.storage.BookingStore;

import static com.stadium.booking.TestRunner.assertClose;
import static com.stadium.booking.TestRunner.assertEquals;
import static com.stadium.booking.TestRunner.assertThrows;
import static com.stadium.booking.TestRunner.assertTrue;
import static com.stadium.booking.TestRunner.freshDatabase;
import static com.stadium.booking.TestRunner.suite;
import static com.stadium.booking.TestRunner.test;

import java.util.List;

/** Pricing, per-reservation limits and customer detail validation. */
final class BookingPricingTest {
    private BookingPricingTest() {
    }

    private static BookingService service() throws Exception {
        return new BookingService(new BookingStore(freshDatabase("pricing")));
    }

    static void register() {
        suite("Pricing and booking rules");

        test("shillings are rounded to the nearest 500", () -> {
            BookingService service = service();
            service.selectEvent(StadiumData.getEvent("namboole-01"));
            double price = service.getSeatPrice(new SeatKey("A", 1, 1));
            assertEquals(0.0, price % 500.0, "seat price must be a multiple of 500");
            assertEquals(0.0, service.getBookingFee() % 500.0, "fee must be a multiple of 500");
        });

        test("a front row seat costs more than a back row seat", () -> {
            BookingService service = service();
            StadiumEvent event = StadiumData.getEvent("namboole-01");
            service.selectEvent(event);
            Stadium stadium = StadiumData.getStadium("namboole");
            SeatKey front = new SeatKey("A", 1, 1);
            SeatKey back = new SeatKey("D", stadium.getSection("D").getRows(), 1);
            assertTrue(service.getSeatPrice(front) > service.getSeatPrice(back),
                    "front must be dearer than back");
        });

        test("the total adds the seat prices and one booking fee", () -> {
            BookingService service = service();
            service.selectEvent(StadiumData.getEvent("namboole-01"));
            List<Seat> seats = List.of(
                    service.getSeat(new SeatKey("A", 1, 1)),
                    service.getSeat(new SeatKey("A", 1, 2)));
            double expected = service.totalFor(seats) + service.getBookingFee();
            assertClose(expected, service.getTotalCharge(seats), 0.01, "total charge");
        });

        test("at most twenty seats per reservation", () -> {
            BookingService service = service();
            service.selectEvent(StadiumData.getEvent("namboole-01"));
            java.util.List<Seat> twenty = new java.util.ArrayList<>();
            for (int n = 1; n <= 20; n++) {
                twenty.add(service.getSeat(new SeatKey("A", 1, n)));
            }
            Booking booking = service.book("Group User", "a@example.co.ug", "+256700000000", twenty);
            assertEquals(20, booking.getSeats().size(), "a group of twenty is allowed");

            // Twenty-one is one too many, and must be refused rather than trimmed
            // silently, or the customer would pay for seats they never got.
            java.util.List<Seat> twentyOne = new java.util.ArrayList<>(twenty);
            twentyOne.add(service.getSeat(new SeatKey("A", 1, 21)));
            assertThrows("a twenty-first seat must be refused",
                    () -> service.book("Too Many", "b@example.co.ug", "+256700000001", twentyOne));
        });

        test("there is no limit on the number of bookings per person", () -> {
            BookingService service = service();
            service.selectEvent(StadiumData.getEvent("namboole-01"));
            for (int n = 1; n <= 8; n++) {
                service.book("Repeat User", "repeat@example.co.ug", "+256700000000",
                        List.of(service.getSeat(new SeatKey("A", n, 1))));
            }
            assertEquals(8, service.getBookings().size(), "repeat bookings should all be kept");
        });

        test("customer details are validated", () -> {
            BookingService service = service();
            service.selectEvent(StadiumData.getEvent("namboole-01"));
            Seat seat = service.getSeat(new SeatKey("A", 1, 1));
            assertThrows("a short name must be refused",
                    () -> service.book("A", "a@example.co.ug", "+256700000000", List.of(seat)));
            assertThrows("an invalid email must be refused",
                    () -> service.book("Valid Name", "not-an-email", "+256700000000", List.of(seat)));
            assertThrows("an invalid phone must be refused",
                    () -> service.book("Valid Name", "a@example.co.ug", "abc", List.of(seat)));
        });

        test("money is formatted in Ugandan shillings", () -> {
            assertEquals("UGX 1,500", BookingService.formatMoney(1500), "formatting");
            assertEquals("UGX 12,500", BookingService.formatMoney(12500.4), "rounds to whole shillings");
        });
    }
}
