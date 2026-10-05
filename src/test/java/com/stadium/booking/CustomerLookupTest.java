package com.stadium.booking;

import com.stadium.booking.booking.Booking;
import com.stadium.booking.booking.BookingService;
import com.stadium.booking.data.SeatKey;
import com.stadium.booking.data.StadiumData;
import com.stadium.booking.data.StadiumEvent;
import com.stadium.booking.storage.BookingStore;

import static com.stadium.booking.TestRunner.assertEquals;
import static com.stadium.booking.TestRunner.assertFalse;
import static com.stadium.booking.TestRunner.assertTrue;
import static com.stadium.booking.TestRunner.freshDatabase;
import static com.stadium.booking.TestRunner.suite;
import static com.stadium.booking.TestRunner.test;

import java.util.List;

/**
 * Finding a booking by the contact details on it.
 *
 * <p>The booking history is open, so this is not a gate in front of it: it is the
 * search a customer uses to pick their own booking out of the list, by the email
 * address or phone number they booked with.
 */
final class CustomerLookupTest {
    private CustomerLookupTest() {
    }

    private static BookingService serviceWithCustomers() throws Exception {
        BookingService service = new BookingService(new BookingStore(freshDatabase("lookup")));
        StadiumEvent cranes = StadiumData.getEvent("namboole-01");
        StadiumEvent concert = StadiumData.getEvent("namboole-03");
        service.selectEvent(cranes);
        service.book("Amina Okello", "amina@example.co.ug", "+256700000001",
                List.of(service.getSeat(new SeatKey("A", 1, 1))));
        service.book("Amina Okello", "amina@example.co.ug", "+256700000001",
                List.of(service.getSeat(new SeatKey("A", 1, 2))));
        service.selectEvent(concert);
        service.book("Bob Mugisha", "bob@example.co.ug", "+256700000002",
                List.of(service.getSeat(new SeatKey("B", 2, 2))));
        return service;
    }

    static void register() {
        suite("Finding a booking");

        test("a customer finds their bookings by email", () -> {
            BookingService service = serviceWithCustomers();
            List<Booking> found = service.findBookingsFor("amina@example.co.ug");
            assertEquals(2, found.size(), "both of Amina's bookings are found");
            for (Booking booking : found) {
                assertEquals("Amina Okello", booking.getCustomerName(),
                        "and nobody else's booking is included");
            }
        });

        test("a customer finds their bookings by phone", () -> {
            BookingService service = serviceWithCustomers();
            assertEquals(1, service.findBookingsFor("+256700000002").size(),
                    "Bob's booking is found by phone");
            assertEquals(2, service.findBookingsFor("+256700000001").size(),
                    "Amina's two bookings are found by phone");
        });

        test("the lookup ignores case and surrounding spaces", () -> {
            BookingService service = serviceWithCustomers();
            assertEquals(2, service.findBookingsFor("  AMINA@Example.co.UG  ").size(),
                    "a customer typing their email in capitals still finds it");
        });

        test("a different customer's details find only their own bookings", () -> {
            BookingService service = serviceWithCustomers();
            List<Booking> stranger = service.findBookingsFor("someone.else@example.co.ug");
            assertTrue(stranger.isEmpty(),
                    "details that match no booking find nothing");
            assertFalse(service.hasBookingFor("stranger@example.co.ug"),
                    "and the yes/no check agrees");
        });

        test("a guess that is too short is refused before it searches", () -> {
            BookingService service = serviceWithCustomers();
            assertTrue(service.findBookingsFor("a").isEmpty(), "one character finds nothing");
            assertTrue(service.findBookingsFor("").isEmpty(), "empty finds nothing");
            assertTrue(service.findBookingsFor(null).isEmpty(), "null finds nothing");
        });

        test("the newest booking is listed first", () -> {
            BookingService service = serviceWithCustomers();
            List<Booking> found = service.findBookingsFor("amina@example.co.ug");
            for (int index = 1; index < found.size(); index++) {
                // Newest first means each row is at or before the one above it.
                assertTrue(!found.get(index - 1).getCreatedAt()
                                .isBefore(found.get(index).getCreatedAt()),
                        "bookings are ordered newest first, but row " + (index - 1)
                                + " (" + found.get(index - 1).getCreatedAt()
                                + ") is older than row " + index
                                + " (" + found.get(index).getCreatedAt() + ")");
            }
        });

        test("a customer's booking is found whichever event it is for", () -> {
            BookingService service = serviceWithCustomers();
            List<Booking> found = service.findBookingsFor("bob@example.co.ug");
            assertEquals(1, found.size(), "one booking");
            assertEquals("namboole-03", found.get(0).getEventId(),
                    "found against the concert it was for");
        });

        test("the lookup finds a cancelled booking too", () -> {
            BookingService service = serviceWithCustomers();
            Booking bob = service.findBookingsFor("bob@example.co.ug").get(0);
            service.cancel(bob.getReference());
            List<Booking> afterCancel = service.findBookingsFor("bob@example.co.ug");
            assertEquals(1, afterCancel.size(), "still found after cancelling");
            assertEquals("CANCELLED", afterCancel.get(0).getStatus().name(),
                    "and it says it was cancelled");
        });
    }
}
