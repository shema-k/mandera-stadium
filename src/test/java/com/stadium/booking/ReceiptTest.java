package com.stadium.booking;

import com.stadium.booking.booking.Booking;
import com.stadium.booking.booking.BookingService;
import com.stadium.booking.booking.Receipt;
import com.stadium.booking.data.Seat;
import com.stadium.booking.data.SeatKey;
import com.stadium.booking.data.StadiumData;
import com.stadium.booking.storage.BookingStore;

import static com.stadium.booking.TestRunner.assertClose;
import static com.stadium.booking.TestRunner.assertEquals;
import static com.stadium.booking.TestRunner.assertTrue;
import static com.stadium.booking.TestRunner.freshDatabase;
import static com.stadium.booking.TestRunner.suite;
import static com.stadium.booking.TestRunner.test;

import java.util.ArrayList;
import java.util.List;

/** The itemised receipt a customer gets after booking. */
final class ReceiptTest {
    private ReceiptTest() {
    }

    private static BookingService serviceFor(String name) throws Exception {
        BookingService service = new BookingService(new BookingStore(freshDatabase(name)));
        service.selectEvent(StadiumData.getEvent("namboole-01"));
        return service;
    }

    /** Books the given seats and returns the booking. */
    private static Booking book(BookingService service, String who, String email, SeatKey... keys) {
        List<Seat> seats = new ArrayList<>();
        for (SeatKey key : keys) {
            seats.add(service.getSeat(key));
        }
        return service.book(who, email, "+256700000000", seats);
    }

    static void register() {
        suite("Receipts");

        test("every booked seat gets its own line", () -> {
            BookingService service = serviceFor("receipt-lines");
            Booking booking = book(service, "Receipt User", "r@example.co.ug",
                    new SeatKey("A", 1, 1), new SeatKey("C", 20, 5), new SeatKey("D", 60, 80));
            Receipt receipt = Receipt.forBooking(booking, service);

            assertEquals(3, receipt.getSeatCount(), "one line per seat");
            assertEquals(3, receipt.getLines().size(), "line count");
            assertEquals(3, receipt.getSectionTotals().size(), "one total per section used");
        });

        test("the lines add up to the total charged", () -> {
            BookingService service = serviceFor("receipt-total");
            Booking booking = book(service, "Arithmetic User", "r@example.co.ug",
                    new SeatKey("A", 2, 2), new SeatKey("B", 30, 9), new SeatKey("D", 100, 70));
            Receipt receipt = Receipt.forBooking(booking, service);

            double sum = 0.0;
            for (Receipt.Line line : receipt.getLines()) {
                sum += line.getPrice();
            }
            assertClose(sum, receipt.getSeatSubtotal(), 0.01, "lines sum to the subtotal");
            assertClose(receipt.getSeatSubtotal() + receipt.getBookingFee(),
                    receipt.getTotal(), 0.01, "subtotal plus fee is the total");
            assertClose(receipt.getTotal(), booking.getTotal(), 0.01,
                    "the receipt total is the booking's own total");
        });

        test("the section totals match the lines they group", () -> {
            BookingService service = serviceFor("receipt-sections");
            Booking booking = book(service, "Grouped User", "r@example.co.ug",
                    new SeatKey("A", 1, 1), new SeatKey("A", 1, 2), new SeatKey("B", 5, 5),
                    new SeatKey("B", 5, 6), new SeatKey("B", 5, 7));
            Receipt receipt = Receipt.forBooking(booking, service);

            assertEquals(2, receipt.getSectionTotals().size(), "two sections used");
            int counted = 0;
            double summed = 0.0;
            for (Receipt.SectionTotal total : receipt.getSectionTotals()) {
                counted += total.getCount();
                summed += total.getTotal();
            }
            assertEquals(5, counted, "every seat appears in exactly one section total");
            assertClose(summed, receipt.getSeatSubtotal(), 0.01,
                    "section totals sum to the subtotal");

            for (Receipt.SectionTotal total : receipt.getSectionTotals()) {
                if ("A".equals(total.getSectionId())) {
                    assertEquals(2, total.getCount(), "section A holds two seats");
                }
                if ("B".equals(total.getSectionId())) {
                    assertEquals(3, total.getCount(), "section B holds three seats");
                }
            }
        });

        test("a single seat still produces a full receipt", () -> {
            BookingService service = serviceFor("receipt-one");
            Booking booking = book(service, "Solo User", "r@example.co.ug", new SeatKey("A", 1, 1));
            Receipt receipt = Receipt.forBooking(booking, service);

            assertEquals(1, receipt.getSeatCount(), "one seat");
            assertEquals(1, receipt.getSectionTotals().size(), "one section total");
            assertClose(receipt.getSeatSubtotal() + BookingService.BOOKING_FEE,
                    receipt.getTotal(), 0.01, "fee still charged once");
        });

        test("the text names the booking, the seats and the money", () -> {
            BookingService service = serviceFor("receipt-text");
            Booking booking = book(service, "Text User", "r@example.co.ug",
                    new SeatKey("A", 1, 1), new SeatKey("D", 50, 50));
            String text = Receipt.forBooking(booking, service).toText();

            assertTrue(text.contains(booking.getReference()), "carries the reference");
            assertTrue(text.contains("A1-01"), "names the first seat");
            assertTrue(text.contains("D50-50"), "names the second seat");
            assertTrue(text.contains("TOTAL PAID"), "states the total");
            assertTrue(text.contains(BookingService.formatMoney(booking.getTotal())),
                    "prints the total in shillings");
            assertTrue(text.contains("Text User"), "names who was billed");
            assertTrue(text.contains("Ticketing fee"), "itemises the fee");
            assertTrue(text.contains(booking.getEmail()), "carries the billing contact");
        });

        test("each end's subtotal shows the price of one seat in it", () -> {
            BookingService service = serviceFor("receipt-each");
            // Two different ends, so the per-end lines are not all the same.
            Booking booking = book(service, "Each User", "r@example.co.ug",
                    new SeatKey("A", 1, 1), new SeatKey("A", 1, 2),
                    new SeatKey("D", 130, 87));
            Receipt receipt = Receipt.forBooking(booking, service);

            for (Receipt.SectionTotal total : receipt.getSectionTotals()) {
                double line = receipt.getLines().stream()
                        .filter(candidate -> candidate.getKey().getSection()
                                .equals(total.getSectionId()))
                        .mapToDouble(Receipt.Line::getPrice).findFirst().orElse(0.0);
                assertClose(total.getTotal() / total.getCount(), line, 0.01,
                        "section " + total.getSectionId()
                                + " quotes a unit price matching its seat lines");
            }
            assertTrue(Receipt.forBooking(booking, service).toText().contains("@"),
                    "the charge lines show the unit price they were worked out at");
        });

        test("the receipt works with no service to price against", () -> {
            BookingService service = serviceFor("receipt-noservice");
            Booking booking = book(service, "No Service User", "r@example.co.ug",
                    new SeatKey("A", 1, 1));
            Receipt receipt = Receipt.forBooking(booking, null);
            // Prices fall back to zero, but the total is still the booking's own,
            // so the receipt cannot silently understate what was charged.
            assertEquals(1, receipt.getSeatCount(), "the seat is still listed");
            assertClose(receipt.getTotal(), booking.getTotal(), 0.01,
                    "the recorded total survives without a pricing service");
            assertTrue(receipt.getBookingFee() >= 0.0, "the fee is never negative");
        });

        test("a cancelled booking receipts as cancelled", () -> {
            BookingService service = serviceFor("receipt-cancelled");
            Booking booking = book(service, "Cancel User", "r@example.co.ug",
                    new SeatKey("A", 1, 1));
            service.cancel(booking.getReference());
            String text = Receipt.forBooking(booking, service).toText();
            assertTrue(text.contains("CANCELLED"), "a cancelled booking says so");
        });

        test("a receipt needs a booking", () -> {
            try {
                Receipt.forBooking(null, null);
                throw new AssertionError("a receipt with no booking should be refused");
            } catch (IllegalArgumentException expected) {
                assertTrue(expected.getMessage().contains("needs a booking"),
                        "explains why: " + expected.getMessage());
            }
        });

        test("a group of twenty seats receipts correctly", () -> {
            BookingService service = serviceFor("receipt-group");
            List<SeatKey> keys = new ArrayList<>();
            for (int number = 1; number <= 20; number++) {
                keys.add(new SeatKey("A", 1, number));
            }
            Booking booking = book(service, "Coach Party", "r@example.co.ug",
                    keys.toArray(new SeatKey[0]));
            Receipt receipt = Receipt.forBooking(booking, service);

            assertEquals(20, receipt.getSeatCount(), "every seat itemised");
            assertEquals(1, receipt.getSectionTotals().size(), "one section total");
            assertEquals(20, receipt.getSectionTotals().get(0).getCount(),
                    "all twenty in one total");
            assertClose(receipt.getSeatSubtotal() + BookingService.BOOKING_FEE,
                    receipt.getTotal(), 0.01, "group total is right");
        });
    }
}
