package com.stadium.booking.booking;

import com.stadium.booking.data.SeatKey;
import com.stadium.booking.data.Stadium;
import com.stadium.booking.data.StadiumData;
import com.stadium.booking.data.StadiumEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
/**
 * How full the ground is for each event, worked out from the database rather than
 * from the screen.
 *
 * <p>Occupancy is a question about one night: how full will this match or this
 * concert be. So there is a row per event, measured against the stadium's seats on
 * sale for that event. Summing seats sold across a whole season and dividing by a
 * season's worth of seats would report a nearly empty ground however busy each
 * individual night was, which is why the totals are kept separate from the rows.
 */
public final class OccupancyReport {
    /** One event's figures. */
    public static final class Row {
        private final StadiumEvent event;
        private final int seatsOnSale;
        private final int seatsBooked;
        private final int[] seatsByEnd;

        Row(StadiumEvent event, int seatsOnSale, int seatsBooked, int[] seatsByEnd) {
            this.event = event;
            this.seatsOnSale = seatsOnSale;
            this.seatsBooked = seatsBooked;
            this.seatsByEnd = seatsByEnd;
        }

        public StadiumEvent getEvent() {
            return event;
        }

        public Stadium getStadium() {
            return StadiumData.getStadium(event.getStadiumId());
        }

        /** Seats on sale for this event, which is the stadium's seat count. */
        public int getSeatsOnSale() {
            return seatsOnSale;
        }

        public int getSeatsBooked() {
            return seatsBooked;
        }

        public int getSeatsAvailable() {
            return Math.max(0, seatsOnSale - seatsBooked);
        }

        /** Percentage of seats still on sale, between 0 and 100. */
        public double getVacancyPercentage() {
            if (seatsOnSale <= 0) {
                return 100.0;
            }
            return getSeatsAvailable() * 100.0 / seatsOnSale;
        }

        public int getSeatsInEnd(String endId) {
            int index = "ABCD".indexOf(endId);
            return index < 0 ? 0 : seatsByEnd[index];
        }
    }

    private final Stadium stadium;
    private final List<Row> rows = new ArrayList<>();
    private final int seasonSeatsOnSale;
    private final int seasonSeatsBooked;

    private OccupancyReport(Stadium stadium, List<Row> rows,
                            int seasonSeatsOnSale, int seasonSeatsBooked) {
        this.stadium = stadium;
        this.rows.addAll(rows);
        this.seasonSeatsOnSale = seasonSeatsOnSale;
        this.seasonSeatsBooked = seasonSeatsBooked;
    }

    /**
     * Builds the report from confirmed bookings.
     *
     * @param bookingService the service holding the reservations; may be null,
     *                       in which case every event simply reads as empty
     */
    public static OccupancyReport compute(BookingService bookingService) {
        return compute(bookingService, StadiumData.getStadiums().get(0));
    }

    /** The report for one stadium. */
    public static OccupancyReport compute(BookingService bookingService, Stadium stadium) {
        List<Row> rows = new ArrayList<>();
        int seasonOnSale = 0;
        int seasonBooked = 0;
        List<StadiumEvent> events = StadiumData.getEvents(stadium.getId());
        for (StadiumEvent event : events) {
            Set<SeatKey> taken = bookingService == null
                    ? Collections.<SeatKey>emptySet() : bookingService.getBookedSeatKeys(event);
            int[] byEnd = new int[4];
            for (SeatKey key : taken) {
                int index = "ABCD".indexOf(key.getSection());
                if (index >= 0) {
                    byEnd[index]++;
                }
            }
            int onSale = stadium.getSeatCount();
            rows.add(new Row(event, onSale, taken.size(), byEnd));
            seasonOnSale += onSale;
            seasonBooked += taken.size();
        }
        return new OccupancyReport(stadium, rows, seasonOnSale, seasonBooked);
    }

    public Stadium getStadium() {
        return stadium;
    }

    /** One row per event, soonest first. */
    public List<Row> getRows() {
        return rows;
    }

    /** Seats on sale across the whole season, being the seat count times events. */
    public int getSeasonSeatsOnSale() {
        return seasonSeatsOnSale;
    }

    public int getSeasonSeatsBooked() {
        return seasonSeatsBooked;
    }

    public int getEventCount() {
        return rows.size();
    }

    public double getSeasonVacancyPercentage() {
        if (seasonSeatsOnSale <= 0) {
            return 100.0;
        }
        return (seasonSeatsOnSale - seasonSeatsBooked) * 100.0 / seasonSeatsOnSale;
    }
}
