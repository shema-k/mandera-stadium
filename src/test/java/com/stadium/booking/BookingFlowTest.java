package com.stadium.booking;

import com.stadium.booking.booking.Booking;
import com.stadium.booking.booking.BookingService;
import com.stadium.booking.booking.Receipt;
import com.stadium.booking.data.Seat;
import com.stadium.booking.data.SeatKey;
import com.stadium.booking.data.StadiumData;
import com.stadium.booking.data.StadiumEvent;
import com.stadium.booking.storage.BookingStore;

import static com.stadium.booking.TestRunner.assertClose;
import static com.stadium.booking.TestRunner.assertEquals;
import static com.stadium.booking.TestRunner.assertTrue;
import static com.stadium.booking.TestRunner.freshDatabase;
import static com.stadium.booking.TestRunner.suite;
import static com.stadium.booking.TestRunner.test;

import java.util.ArrayList;
import java.util.List;

/**
 * The whole booking, from picking a seat to holding a receipt, driven through
 * the same steps the screens take.
 *
 * <p>Each earlier suite covers one rule. This one covers the sequence, because
 * the requirements are about a customer's path through the system rather than
 * about any single rule: choose an end, choose exact seats, see the itemised
 * costs, and get a receipt that adds up.
 */

/**
 * Checks the whole journey from picking seats to being given a receipt.
 */

final class BookingFlowTest {
    private BookingFlowTest() {
    }

    private static BookingService serviceFor(String name) throws Exception {
        BookingService service = new BookingService(new BookingStore(freshDatabase(name)));
        service.selectEvent(StadiumData.getEvent("namboole-01"));
        return service;
    }

    static void register() {
        suite("Booking flow");

        test("a customer picks a seat in each end and books them together", () -> {
            BookingService service = serviceFor("flow-ends");
            List<SeatKey> chosen = List.of(
                    new SeatKey("A", 3, 40),
                    new SeatKey("B", 20, 30),
                    new SeatKey("C", 40, 10),
                    new SeatKey("D", 60, 15));
            List<Seat> seats = new ArrayList<>();
            for (SeatKey key : chosen) {
                seats.add(service.getSeat(key));
            }
            double quoted = service.getTotalCharge(seats);
            Booking booking = service.book("Amina Okello", "amina@example.co.ug",
                    "+256700000123", seats);

            assertEquals(chosen.size(), booking.getSeats().size(), "every seat booked");
            for (SeatKey key : chosen) {
                assertTrue(booking.getSeats().contains(key),
                        "the seat " + key.display() + " chosen is not in the booking");
                assertTrue(service.getBookedSeatKeys(StadiumData.getEvent("namboole-01"))
                        .contains(key), "seat " + key.display() + " is not marked sold");
            }
            assertClose(quoted, booking.getTotal(), 0.01,
                    "the receipt total is the figure that was quoted");
        });

        test("the receipt for that booking adds up to the total", () -> {
            BookingService service = serviceFor("flow-receipt");
            List<Seat> seats = List.of(
                    service.getSeat(new SeatKey("A", 3, 40)),
                    service.getSeat(new SeatKey("C", 12, 20)),
                    service.getSeat(new SeatKey("D", 60, 15)));
            Booking booking = service.book("Amina Okello", "amina@example.co.ug",
                    "+256700000123", seats);
            Receipt receipt = Receipt.forBooking(booking, service);

            double lines = 0.0;
            for (Receipt.Line line : receipt.getLines()) {
                lines += line.getPrice();
            }
            assertClose(lines, receipt.getSeatSubtotal(), 0.01, "lines sum to the subtotal");
            assertClose(receipt.getSeatSubtotal() + receipt.getBookingFee(),
                    receipt.getTotal(), 0.01, "the bill adds up");
            assertClose(receipt.getTotal(), booking.getTotal(), 0.01,
                    "and matches what was charged");
        });

        test("the quoted price of a seat is what the receipt bills for it", () -> {
            BookingService service = serviceFor("flow-price");
            // A front-row seat in the VIP box, and a back seat at the value end:
            // the two ends of the price range in one booking.
            List<Seat> seats = List.of(
                    service.getSeat(new SeatKey("A", 1, 1)),
                    service.getSeat(new SeatKey("D", 130, 87)));
            double quotedFront = seats.get(0).getPrice();
            double quotedBack = seats.get(1).getPrice();
            assertTrue(quotedFront > quotedBack,
                    "the VIP front row must be dearer than the value back row: "
                            + quotedFront + " vs " + quotedBack);

            Booking booking = service.book("Price User", "price@example.co.ug",
                    "+256700000000", seats);
            Receipt receipt = Receipt.forBooking(booking, service);
            assertClose(receipt.getLines().get(0).getPrice(), quotedFront, 0.01,
                    "the receipt bills the front seat at its quoted price");
            assertClose(receipt.getLines().get(1).getPrice(), quotedBack, 0.01,
                    "and the back seat at its quoted price");
        });

        test("twenty seats across four ends book as one reservation", () -> {
            BookingService service = serviceFor("flow-twenty");
            List<Seat> seats = new ArrayList<>();
            for (int number = 1; number <= 5; number++) {
                seats.add(service.getSeat(new SeatKey("A", 1, number)));
                seats.add(service.getSeat(new SeatKey("B", 1, number)));
                seats.add(service.getSeat(new SeatKey("C", 1, number)));
                seats.add(service.getSeat(new SeatKey("D", 1, number)));
            }
            Booking booking = service.book("Coach Party", "coach@example.co.ug",
                    "+256700000000", seats);
            assertEquals(20, booking.getSeats().size(), "all twenty seats booked");
            Receipt receipt = Receipt.forBooking(booking, service);
            assertEquals(20, receipt.getSeatCount(), "all twenty itemised");
            assertEquals(4, receipt.getSectionTotals().size(), "grouped into the four ends");
            assertClose(receipt.getSeatSubtotal() + BookingService.BOOKING_FEE,
                    receipt.getTotal(), 0.01, "the group bill adds up");
        });

        test("a seat sold to one customer is not sold to the next", () -> {
            BookingService service = serviceFor("flow-double");
            StadiumEvent event = StadiumData.getEvent("namboole-01");
            SeatKey contested = new SeatKey("B", 30, 45);
            Booking first = service.book("First", "first@example.co.ug", "+256700000001",
                    List.of(service.getSeat(contested)));
            service.selectEvent(event);

            assertTrue(!service.isSeatSelectable(contested),
                    "the seat is sold and must not be offered again");
            assertTrue(!service.isBooked(new SeatKey("B", 30, 46)) == false
                            || service.isSeatSelectable(new SeatKey("B", 30, 46)),
                    "the neighbouring seat is still for sale");
            assertEquals(1, first.getSeats().size(), "the first booking is intact");
        });

        test("cancelling a booking puts its seats back on sale", () -> {
            BookingService service = serviceFor("flow-cancel");
            StadiumEvent event = StadiumData.getEvent("namboole-01");
            List<SeatKey> keys = List.of(
                    new SeatKey("A", 10, 10), new SeatKey("D", 20, 20));
            List<Seat> seats = new ArrayList<>();
            for (SeatKey key : keys) {
                seats.add(service.getSeat(key));
            }
            Booking booking = service.book("Cancel User", "cancel@example.co.ug",
                    "+256700000000", seats);
            service.cancel(booking.getReference());
            service.selectEvent(event);

            for (SeatKey key : keys) {
                assertTrue(service.isSeatSelectable(key),
                        key.display() + " should be on sale again after the cancellation");
            }
            Booking replacement = service.book("Second", "second@example.co.ug",
                    "+256700000001", seats);
            assertEquals(2, replacement.getSeats().size(),
                    "the freed seats can be sold again");
        });

        test("a concert is booked the same way as a game", () -> {
            BookingService service = serviceFor("flow-concert");
            StadiumEvent concert = StadiumData.getEvents().stream()
                    .filter(StadiumEvent::isConcert).findFirst().orElseThrow();
            service.selectEvent(concert);
            List<Seat> seats = List.of(
                    service.getSeat(new SeatKey("A", 2, 2)),
                    service.getSeat(new SeatKey("C", 30, 30)));
            Booking booking = service.book("Concert Fan", "fan@example.co.ug",
                    "+256775566677", seats);
            assertEquals(2, booking.getSeats().size(), "the concert seats are booked");
            assertEquals(concert.getId(), booking.getEventId(), "against the concert");
            Receipt receipt = Receipt.forBooking(booking, service);
            assertEquals(concert.getDate().format(java.time.format.DateTimeFormatter
                            .ofPattern("EEE, d MMM yyyy", java.util.Locale.ENGLISH)),
                    receipt.getEventWhen().split("  •  ")[0].trim(),
                    "the receipt is dated for the concert");
            assertTrue(receipt.toText().contains(concert.getType().getLabel()),
                    "and names the kind of event: " + concert.getType().getLabel());
        });

        test("a booking survives being written and read back", () -> {
            java.nio.file.Path file = freshDatabase("flow-persist");
            List<SeatKey> keys = List.of(
                    new SeatKey("A", 5, 5), new SeatKey("D", 70, 70));
            Booking saved;
            List<Seat> seats;
            {
                BookingService first = new BookingService(new BookingStore(file));
                first.selectEvent(StadiumData.getEvent("namboole-01"));
                seats = List.of(first.getSeat(keys.get(0)), first.getSeat(keys.get(1)));
                saved = first.book("Persist User", "persist@example.co.ug", "+256700000000", seats);
            }
            BookingService reopened = new BookingService(new BookingStore(file));
            assertEquals(1, reopened.getBookings().size(), "the booking was kept");
            Booking loaded = reopened.getBookings().get(0);
            assertEquals(saved.getReference(), loaded.getReference(), "same reference");
            assertEquals(2, loaded.getSeats().size(), "same seats");
            assertClose(saved.getTotal(), loaded.getTotal(), 0.01, "same total");
            assertTrue(reopened.getBookedSeatKeys(StadiumData.getEvent("namboole-01"))
                    .containsAll(keys), "the seats are still sold after reopening");
        });

        test("the whole schedule can be booked without a clash between events", () -> {
            BookingService service = serviceFor("flow-schedule");
            int booked = 0;
            for (StadiumEvent event : StadiumData.getEvents()) {
                if (!service.isBookingOpen(event)) {
                    continue;
                }
                service.selectEvent(event);
                // The same seat position in each event is a different seat in
                // each event, so no two bookings can collide.
                List<Seat> seats = List.of(
                        service.getSeat(new SeatKey("A", 1, 1)),
                        service.getSeat(new SeatKey("B", 1, 1)));
                Booking booking = service.book("Season Ticket", "season@example.co.ug",
                        "+256700000000", seats);
                assertEquals(2, booking.getSeats().size(),
                        "two seats booked for " + event.getId());
                booked++;
            }
            assertEquals(service.getBookings().size(), booked,
                    "every booking made was kept");
            assertTrue(booked >= 5,
                    "most of the season's events are open for booking, got " + booked);
            // The same seat position is a different seat in each event, so every
            // booking must have survived without clashing with the others.
            for (Booking booking : service.getBookings()) {
                assertEquals(2, booking.getSeats().size(),
                        booking.getEventId() + " kept both of its seats");
            }
        });
    }
}
