package com.stadium.booking.data;

import com.stadium.booking.booking.BookingService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
/**
 * Everything known about one venue, worked out in one place.
 *
 * <p>The detail screen is meant to say all there is to say about a stadium, so
 * the facts are gathered and calculated here rather than in the Swing code. That
 * keeps the arithmetic out of the painting, and lets the tests check that the
 * page is telling the truth without a display.
 *
 * <p>Nothing here is invented. Every figure comes from the venue record, its
 * seating sections or its published events.
 */
public final class StadiumDetails {
    /** What is known about one of the four seating sections. */
    public static final class SectionFacts {
        private final SeatSection section;
        private final double frontPrice;
        private final double backPrice;

        SectionFacts(SeatSection section, double frontPrice, double backPrice) {
            this.section = section;
            this.frontPrice = frontPrice;
            this.backPrice = backPrice;
        }

        public SeatSection getSection() {
            return section;
        }

        public String getId() {
            return section.getId();
        }

        public String getLabel() {
            return section.getLabel();
        }

        public int getRows() {
            return section.getRows();
        }

        public int getSeatsPerRow() {
            return section.getSeatsPerRow();
        }

        public int getSeatCount() {
            return section.getSeatCount();
        }

        public double getFrontPrice() {
            return frontPrice;
        }

        public double getBackPrice() {
            return backPrice;
        }

        /** What a seat costs, from the front of the section to the back. */
        public String getPriceRange() {
            return frontPrice == backPrice
                    ? BookingService.formatMoney(frontPrice)
                    : BookingService.formatMoney(frontPrice) + " to "
                            + BookingService.formatMoney(backPrice);
        }
    }

    private final Stadium stadium;
    private final List<SectionFacts> sections = new ArrayList<>();
    private final List<StadiumEvent> events;
    private final List<StadiumAnnouncement> notices;
    private final int seatsOnSale;
    private final int seatsBooked;
    private final double cheapestSeat;
    private final double dearestSeat;
    private final Set<String> teams = new LinkedHashSet<>();
    private final Set<String> artists = new LinkedHashSet<>();
    private final BookingService booked;
    private final StadiumEvent priceReference;

    private StadiumDetails(Stadium stadium, List<StadiumEvent> events, BookingService service) {
        this.stadium = stadium;
        this.events = new ArrayList<>(events);
        this.booked = service;

        // Prices are taken from the cheapest upcoming event, so the range shown is
        // a price the customer can actually pay rather than a list price.
        this.priceReference = cheapestEvent();
        for (SeatSection section : stadium.getSections()) {
            double front = priceReference == null ? section.getBasePrice()
                    : priceFor(priceReference, section, 1);
            double back = priceReference == null ? section.getBasePrice()
                    : priceFor(priceReference, section, section.getRows());
            sections.add(new SectionFacts(section, front, back));
        }
        // The cheapest seat anywhere in the stadium, and the dearest. One loop
        // over the sections, keeping hold of the smallest and largest numbers
        // we come across. Plain numbers are used while we work, and only
        // assigned to the fields once, at the end.
        double lowest = 0;
        double highest = 0;

        for (int index = 0; index < sections.size(); index++) {
            SectionFacts facts = sections.get(index);
            if (index == 0 || facts.getBackPrice() < lowest) {
                lowest = facts.getBackPrice();
            }
            if (index == 0 || facts.getFrontPrice() > highest) {
                highest = facts.getFrontPrice();
            }
        }

        this.cheapestSeat = lowest;
        this.dearestSeat = highest;

        this.notices = StadiumData.getAnnouncements(stadium.getId());
        for (StadiumEvent event : this.events) {
            if (event.getTeamOne() != null && !event.getTeamOne().isBlank()) {
                teams.add(event.getTeamOne());
            }
            if (event.getTeamTwo() != null && !event.getTeamTwo().isBlank()) {
                teams.add(event.getTeamTwo());
            }
            if (event.getArtist() != null && !event.getArtist().isBlank()) {
                artists.add(event.getArtist());
            }
        }

        int onSale = 0;
        int taken = 0;
        for (StadiumEvent event : this.events) {
            onSale += stadium.getSeatCount();
            taken += seatsTakenOn(event);
        }
        this.seatsOnSale = onSale;
        this.seatsBooked = taken;
    }

    /** Collects everything known about a venue. */
    public static StadiumDetails of(Stadium stadium, BookingService service) {
        return new StadiumDetails(stadium, StadiumData.getEvents(stadium.getId()),
                service);
    }

    /** Seats already sold on one event, or none when no service was supplied. */
    private int seatsTakenOn(StadiumEvent event) {
        return booked == null ? 0 : booked.getBookedSeatKeys(event).size();
    }

    /**
     * The price of one seat, using the seat map's own row curve. An earlier version
     * carried a second, linear copy of that curve and quietly quoted prices the map
     * would never charge.
     */
    private double priceFor(StadiumEvent event, SeatSection section, int row) {
        return BookingService.roundMoney(section.getBasePrice()
                * BookingService.getRowPriceMultiplier(row, section.getRows())
                * event.getPriceFactor());
    }

    /** The upcoming event with the lowest prices, used as the reference for pricing. */
    private StadiumEvent cheapestEvent() {
        StadiumEvent cheapest = null;
        for (StadiumEvent event : events) {
            if (cheapest == null || event.getPriceFactor() < cheapest.getPriceFactor()) {
                cheapest = event;
            }
        }
        return cheapest;
    }

    public Stadium getStadium() {
        return stadium;
    }

    public List<SectionFacts> getSections() {
        return sections;
    }

    /**
     * The events at this venue, soonest first.
     *
     * <p>The sort compares three things in turn: the date, then the start time,
     * then the id. The id is only there as a last step so that two events with
     * the same date and time always come back in the same order.
     */
    public List<StadiumEvent> getEvents() {
        List<StadiumEvent> sorted = new ArrayList<>(events);

        Collections.sort(sorted, new Comparator<StadiumEvent>() {
            @Override
            public int compare(StadiumEvent one, StadiumEvent two) {
                int result = one.getDate().compareTo(two.getDate());
                if (result != 0) {
                    return result;
                }

                result = one.getStartTime().compareTo(two.getStartTime());
                if (result != 0) {
                    return result;
                }

                return one.getId().compareTo(two.getId());
            }
        });

        return sorted;
    }

    /**
     * The event the quoted prices come from: the cheapest one on sale, which may
     * not be the next one. Null when nothing is scheduled.
     */
    public StadiumEvent getPriceReferenceEvent() {
        return priceReference;
    }

    public StadiumEvent getNextEvent() {
        List<StadiumEvent> sorted = getEvents();
        return sorted.isEmpty() ? null : sorted.get(0);
    }

    public int getEventCount() {
        return events.size();
    }

    /** How many of the events are games rather than concerts. */
    public int getGameCount() {
        int count = 0;

        for (StadiumEvent event : events) {
            if (event.isGame()) {
                count = count + 1;
            }
        }

        return count;
    }

    public int getConcertCount() {
        return getEventCount() - getGameCount();
    }

    public List<StadiumAnnouncement> getNotices() {
        return notices;
    }

    public boolean hasNotices() {
        return !notices.isEmpty();
    }

    /** Teams seen at this venue, without repeats. */
    public List<String> getTeams() {
        return new ArrayList<>(teams);
    }

    /** Artists seen at this venue, without repeats. */
    public List<String> getArtists() {
        return new ArrayList<>(artists);
    }

    /** Events that cannot currently be booked, and why. */
    /**
     * The events that cannot be booked right now.
     *
     * <p>An event is blocked when it has been cancelled, when there is an
     * emergency notice, or when it is too close to its booking deadline.
     */
    public List<StadiumEvent> getBlockedEvents() {
        List<StadiumEvent> blocked = new ArrayList<>();

        // Without a booking service there is nothing to check against, so we
        // cannot say anything is blocked.
        if (booked == null) {
            return blocked;
        }

        for (StadiumEvent event : events) {
            if (!booked.isBookingOpen(event)) {
                blocked.add(event);
            }
        }

        return blocked;
    }

    public int getSeatsOnSale() {
        return seatsOnSale;
    }

    public int getSeatsBooked() {
        return seatsBooked;
    }

    public double getVacancyPercentage() {
        if (seatsOnSale <= 0) {
            return 100.0;
        }
        return (seatsOnSale - seatsBooked) * 100.0 / seatsOnSale;
    }

    public double getCheapestSeat() {
        return cheapestSeat;
    }

    public double getDearestSeat() {
        return dearestSeat;
    }

    public double getBookingFee() {
        return BookingService.BOOKING_FEE;
    }

    /** The full price span, cheapest seat to dearest. */
    public String getPriceSpan() {
        return BookingService.formatMoney(cheapestSeat) + " to "
                + BookingService.formatMoney(dearestSeat);
    }

    /** A one-line summary for a card or a header. */
    public String getSummary() {
        return stadium.getVenueType() + "  •  " + stadium.getSeatCount() + " seats  •  "
                + getEvents().size() + " upcoming";
    }
}
