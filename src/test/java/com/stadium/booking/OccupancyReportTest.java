package com.stadium.booking;

import static com.stadium.booking.TestRunner.assertClose;
import static com.stadium.booking.TestRunner.assertEquals;
import static com.stadium.booking.TestRunner.assertTrue;
import static com.stadium.booking.TestRunner.freshDatabase;
import static com.stadium.booking.TestRunner.suite;
import static com.stadium.booking.TestRunner.test;

import java.util.List;

/**
 * The occupancy report.
 *
 * <p>Occupancy is a question about one night, so there is a row per event and each
 * row is measured against the seats on sale for that event. The season totals are
 * kept separately, because a season total that folded eight events together would
 * report a nearly empty ground however busy each individual night was.
 */
final class OccupancyReportTest {
    private OccupancyReportTest() {
    }

    private static BookingService serviceWithBookings() throws Exception {
        BookingService service = new BookingService(new BookingStore(freshDatabase("occupancy")));
        StadiumEvent cranes = StadiumData.getEvent("namboole-01");
        StadiumEvent derby = StadiumData.getEvent("namboole-02");
        service.selectEvent(cranes);
        service.book("Alice Ssali", "alice@example.co.ug", "+256700000001",
                List.of(
                        service.getSeat(new SeatKey("A", 1, 1)),
                        service.getSeat(new SeatKey("A", 1, 2))));
        service.selectEvent(derby);
        service.book("Bob Mugisha", "bob@example.co.ug", "+256700000002",
                List.of(service.getSeat(new SeatKey("B", 1, 1))));
        return service;
    }

    private static OccupancyReport.Row rowForEvent(OccupancyReport report, String eventId) {
        return report.getRows().stream()
                .filter(row -> row.getEvent().getId().equals(eventId))
                .findFirst()
                .orElseThrow(() -> new AssertionError("no row for " + eventId));
    }

    static void register() {
        suite("Occupancy report");

        test("every event appears, soonest first", () -> {
            OccupancyReport report = OccupancyReport.compute(null);
            assertEquals(StadiumData.getEvents().size(), report.getRows().size(),
                    "one row per event");
            java.time.LocalDate previous = null;
            for (OccupancyReport.Row row : report.getRows()) {
                if (previous != null) {
                    assertTrue(!row.getEvent().getDate().isBefore(previous),
                            "events must not go backwards in time");
                }
                previous = row.getEvent().getDate();
            }
        });

        test("each row is measured against the seats on sale for that night", () -> {
            OccupancyReport report = OccupancyReport.compute(null);
            Stadium namboole = StadiumData.getStadium("namboole");
            OccupancyReport.Row row = rowForEvent(report, "namboole-01");
            // One event, one ground: the seat count, not a season's worth of it.
            assertEquals(namboole.getSeatCount(), row.getSeatsOnSale(),
                    "seats on sale for one night");
        });

        test("the season totals compare like with like", () -> {
            OccupancyReport report = OccupancyReport.compute(null);
            Stadium namboole = StadiumData.getStadium("namboole");
            int events = StadiumData.getEvents("namboole").size();
            assertEquals(events, report.getEventCount(), "event count");
            assertEquals(namboole.getSeatCount() * events, report.getSeasonSeatsOnSale(),
                    "seats on sale across the season");
        });

        test("rows sum to the season totals", () -> {
            OccupancyReport report = OccupancyReport.compute(serviceWithBookings());
            int onSale = report.getRows().stream()
                    .mapToInt(OccupancyReport.Row::getSeatsOnSale).sum();
            int booked = report.getRows().stream()
                    .mapToInt(OccupancyReport.Row::getSeatsBooked).sum();
            assertEquals(report.getSeasonSeatsOnSale(), onSale,
                    "seats on sale total matches the rows");
            assertEquals(report.getSeasonSeatsBooked(), booked,
                    "booked total matches the rows");
            assertEquals(StadiumData.getEvents().size(), report.getEventCount(),
                    "every event is counted once");
        });

        test("a booking counts against its own event only", () -> {
            OccupancyReport report = OccupancyReport.compute(serviceWithBookings());
            // The two Cranes seats belong to the Cranes match, and the single seat
            // to the derby. Neither night should carry the other's sales.
            assertEquals(2, rowForEvent(report, "namboole-01").getSeatsBooked(),
                    "the Cranes match has its two seats");
            assertEquals(1, rowForEvent(report, "namboole-02").getSeatsBooked(),
                    "the derby has its one seat");
            assertEquals(3, report.getSeasonSeatsBooked(), "three seats across the season");
        });

        test("booked seats are counted, split across the ends", () -> {
            OccupancyReport report = OccupancyReport.compute(serviceWithBookings());
            OccupancyReport.Row row = rowForEvent(report, "namboole-01");
            assertEquals(2, row.getSeatsBooked(), "two seats on this event");
            int byEnd = row.getSeatsInEnd("A") + row.getSeatsInEnd("B")
                    + row.getSeatsInEnd("C") + row.getSeatsInEnd("D");
            assertEquals(row.getSeatsBooked(), byEnd, "end counts add up");
            assertEquals(2, row.getSeatsInEnd("A"), "both were in the VIP Box");
            assertTrue(row.getSeatsInEnd("D") == 0, "nothing was booked in the Kampala End");
        });

        test("cancelled bookings free their seats in the report", () -> {
            BookingService service = serviceWithBookings();
            OccupancyReport.Row before = rowForEvent(OccupancyReport.compute(service), "namboole-01");
            Booking booking = service.getBookings().get(0);
            service.cancel(booking.getReference());
            OccupancyReport.Row after = rowForEvent(OccupancyReport.compute(service), "namboole-01");
            assertEquals(before.getSeatsBooked() - booking.getSeats().size(),
                    after.getSeatsBooked(), "a cancellation reduces the count");
        });

        test("vacancy is seats on sale minus booked", () -> {
            OccupancyReport.Row row = rowForEvent(
                    OccupancyReport.compute(serviceWithBookings()), "namboole-01");
            assertEquals(row.getSeatsOnSale() - row.getSeatsBooked(), row.getSeatsAvailable(),
                    "available seats");
            assertClose(row.getSeatsAvailable() * 100.0 / row.getSeatsOnSale(),
                    row.getVacancyPercentage(), 0.0001, "vacancy percentage");
        });

        test("an empty database reads as fully vacant", () -> {
            BookingService service = new BookingService(new BookingStore(freshDatabase("empty")));
            OccupancyReport report = OccupancyReport.compute(service);
            assertEquals(0, report.getSeasonSeatsBooked(), "nothing booked");
            assertClose(100.0, report.getSeasonVacancyPercentage(), 0.0001, "fully vacant");
        });

        test("booked seats never exceed the seats on sale", () -> {
            OccupancyReport report = OccupancyReport.compute(serviceWithBookings());
            for (OccupancyReport.Row row : report.getRows()) {
                assertTrue(row.getSeatsBooked() <= row.getSeatsOnSale(),
                        "overbooked: " + row.getEvent().getHeadline());
                assertTrue(row.getSeatsAvailable() >= 0,
                        "negative vacancy at " + row.getEvent().getHeadline());
                assertTrue(row.getVacancyPercentage() >= 0.0
                                && row.getVacancyPercentage() <= 100.0,
                        "vacancy out of range at " + row.getEvent().getHeadline());
            }
        });

        test("one busy night is not hidden by the quiet ones", () -> {
            // The reason this report is per event. Filling a single night and
            // leaving the rest empty must show that night as nearly full, rather
            // than averaging to a fraction of a percent across the whole season.
            BookingService service = new BookingService(new BookingStore(freshDatabase("busy")));
            Stadium stadium = StadiumData.getStadium("namboole");
            StadiumEvent busy = StadiumData.getEvents("namboole").get(0);
            int seatsToBook = stadium.getSeatCount() / 2;
            List<Seat> seats = new java.util.ArrayList<>();
            for (Seat seat : service.getSeats()) {
                if (seats.size() >= seatsToBook) {
                    break;
                }
                if (service.isSeatSelectable(seat.getKey())) {
                    seats.add(seat);
                }
            }
            // Split across reservations, since one reservation is capped at 20.
            for (int start = 0; start < seats.size(); start += 20) {
                service.selectEvent(busy);
                List<Seat> slice = seats.subList(start, Math.min(start + 20, seats.size()));
                service.book("Busy Night", "busy@example.co.ug", "+256700000000",
                        new java.util.ArrayList<>(slice));
            }
            OccupancyReport.Row row = rowForEvent(OccupancyReport.compute(service), busy.getId());
            assertEquals(seatsToBook, row.getSeatsBooked(), "the busy night has its seats");
            assertTrue(row.getVacancyPercentage() < 50.5,
                    "a half-full night reads as about half full, got "
                            + row.getVacancyPercentage());
        });
    }
}
