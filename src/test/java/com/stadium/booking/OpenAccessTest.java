package com.stadium.booking;

import com.stadium.booking.booking.Booking;
import com.stadium.booking.booking.BookingService;
import com.stadium.booking.booking.Receipt;
import com.stadium.booking.data.SeatKey;
import com.stadium.booking.data.StadiumData;
import com.stadium.booking.data.StadiumEvent;
import com.stadium.booking.storage.BookingStore;
import com.stadium.booking.storage.Database;

import static com.stadium.booking.TestRunner.assertEquals;
import static com.stadium.booking.TestRunner.assertFalse;
import static com.stadium.booking.TestRunner.assertTrue;
import static com.stadium.booking.TestRunner.freshDatabase;
import static com.stadium.booking.TestRunner.suite;
import static com.stadium.booking.TestRunner.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

/**
 * The system is open to the people who book.
 *
 * <p>There are no staff accounts, no PINs and no roles, and every screen is
 * reachable without signing in. These tests guard that, because an access gate
 * creeping back in would lock customers out of their own bookings, and because
 * the database has to open on its own rather than waiting for a password that
 * nobody is there to type.
 */

/**
 * Checks that every screen opens with no sign-in, because the system is
 * open to anyone who books.
 */

final class OpenAccessTest {
    private OpenAccessTest() {
    }

    private static BookingService service() throws Exception {
        return new BookingService(new BookingStore(freshDatabase("open")));
    }

    static void register() {
        suite("Open to bookers");

        test("the database opens on its own, with no password", () -> {
            Path file = freshDatabase("open-db");
            Database database = new Database(file);
            // No password is ever asked for or supplied.
            assertFalse(database.isProtected(), "the database is not password protected");
            try (java.sql.Connection connection = database.open()) {
                assertTrue(connection.isValid(3), "the database opens without a prompt");
            }
        });

        test("opening the database leaves no password marker behind", () -> {
            Path file = freshDatabase("open-marker");
            Database database = new Database(file);
            try (java.sql.Connection connection = database.open()) {
                assertTrue(connection.isValid(3), "opened");
            }
            assertFalse(Files.exists(Database.legacyMarkerFor(file)),
                    "no marker file is written beside the database");
            assertFalse(Database.requiresPassword(file),
                    "and nothing claims this database needs a password");
        });

        test("bookings are readable straight after they are written", () -> {
            Path file = freshDatabase("open-write");
            Booking saved;
            {
                BookingService first = new BookingService(new BookingStore(file));
                first.selectEvent(StadiumData.getEvent("namboole-01"));
                saved = first.book("Open User", "open@example.co.ug", "+256700000000",
                        List.of(first.getSeat(new SeatKey("A", 1, 1))));
            }
            // Reopened with no sign-in step of any kind.
            BookingService reopened = new BookingService(new BookingStore(file));
            assertEquals(1, reopened.getBookings().size(), "the booking is there to read");
            assertEquals(saved.getReference(), reopened.getBookings().get(0).getReference(),
                    "and it is the same booking");
        });

        test("every booking is listed, with no viewer signed in", () -> {
            BookingService service = service();
            StadiumEvent event = StadiumData.getEvent("namboole-01");
            service.selectEvent(event);
            service.book("One", "one@example.co.ug", "+256700000001",
                    List.of(service.getSeat(new SeatKey("A", 1, 1))));
            service.book("Two", "two@example.co.ug", "+256700000002",
                    List.of(service.getSeat(new SeatKey("A", 1, 2))));
            service.book("Three", "three@example.co.ug", "+256700000003",
                    List.of(service.getSeat(new SeatKey("A", 1, 3))));

            // The service exposes the whole list; there is no filter to pass.
            assertEquals(3, service.getBookings().size(),
                    "every booking is available to the screen without a sign-in");
            for (Booking booking : service.getBookings()) {
                assertFalse(booking.getCustomerName().isBlank(),
                        "and each carries who it is for");
                assertFalse(booking.getEmail().isBlank(), "and how to reach them");
            }
        });

        test("customer contact details are kept in full on the booking", () -> {
            BookingService service = service();
            service.selectEvent(StadiumData.getEvent("namboole-01"));
            Booking booking = service.book("Amina Okello", "amina@example.co.ug",
                    "+256700123456", List.of(service.getSeat(new SeatKey("A", 1, 1))));

            // With no staff roles, nothing is withheld from the booking record.
            assertEquals("Amina Okello", booking.getCustomerName(), "the name is in full");
            assertEquals("amina@example.co.ug", booking.getEmail(), "the email is in full");
            assertEquals("+256700123456", booking.getPhone(), "the phone is in full");
        });

        test("a booking can be looked up by the details on it", () -> {
            BookingService service = service();
            service.selectEvent(StadiumData.getEvent("namboole-01"));
            service.book("Amina Okello", "amina@example.co.ug", "+256700000001",
                    List.of(service.getSeat(new SeatKey("A", 1, 1))));
            assertTrue(service.hasBookingFor("amina@example.co.ug"),
                    "the customer finds their booking by email");
            assertTrue(service.hasBookingFor("+256700000001"),
                    "and by phone");
        });

        test("no gate stands between a customer and their receipt", () -> {
            BookingService service = service();
            service.selectEvent(StadiumData.getEvent("namboole-01"));
            Booking booking = service.book("Amina Okello", "amina@example.co.ug",
                    "+256700000001", List.of(service.getSeat(new SeatKey("A", 1, 1))));

            // A receipt is rebuilt from the booking alone, with no identity to
            // check and nothing that has to be unlocked first.
            Receipt receipt = Receipt.forBooking(booking, service);
            assertEquals(booking.getReference(), receipt.getReference(),
                    "the receipt is built straight from the booking");
            assertTrue(receipt.toText().contains(booking.getCustomerName()),
                    "and carries who it is for");
        });

        test("cancelling a booking needs no sign-in either", () -> {
            BookingService service = service();
            StadiumEvent event = StadiumData.getEvent("namboole-01");
            service.selectEvent(event);
            Booking booking = service.book("Cancel User", "cancel@example.co.ug",
                    "+256700000001", List.of(service.getSeat(new SeatKey("C", 5, 5))));
            assertTrue(service.cancel(booking.getReference()), "cancelled");
            service.selectEvent(event);
            assertTrue(service.isSeatSelectable(new SeatKey("C", 5, 5)),
                    "and the seat is free again");
        });

        test("the data file is left where the next run will find it", () -> {
            // The application opens its file directly from the working directory,
            // so nothing about it depends on a credential written elsewhere.
            Database database = new Database(new java.io.File("stadium-bookings.dat").toPath());
            assertTrue(database.getFile().endsWith("stadium-bookings.dat"),
                    "the database is the plain bookings file: " + database.getFile());
            database.close();
        });

        test("an old password-protected file is no longer asked for a password", () -> {
            // A database written by an earlier build may still have its sidecar
            // marker beside it. Nothing prompts for the password any more, so the
            // marker must not stop the file being used.
            Path file = freshDatabase("legacy-marker");
            Path marker = Database.legacyMarkerFor(file);
            Files.writeString(marker, "");
            assertTrue(Files.exists(marker), "the leftover marker is really there");
            assertFalse(Database.requiresPassword(file),
                    "and it no longer means the file needs a password");
            assertFalse(new Database(file).isProtected(),
                    "so the file opens as an ordinary database");
        });
    }
}
