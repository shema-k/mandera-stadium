package com.stadium.booking.data;

import com.stadium.booking.booking.Booking;
import com.stadium.booking.booking.BookingCountdown;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

/**
 * One game or concert that is going to happen at a stadium.
 *
 * <p>It holds when the event is, who is playing or performing, and how much
 * prices should be multiplied by. A big match is worth more than a small one, so
 * a concert has a factor of 1.00 while a CECAFA final has 1.25 — meaning every
 * seat costs a quarter more.
 *
 * <p>It does not hold any prices itself. The price of a seat is worked out in
 * BookingService, which combines this event's factor with the seat's section and
 * row. Keeping the two apart means changing a price rule does not mean touching
 * every event.
 */
public final class StadiumEvent {

    /**
     * The three ways a date or time is written on screen.
     *
     * <p>These are patterns, like a template. DATE_FORMATTER turns a date into
     * "Wed, 07 Oct 2026". TIME_FORMATTER turns a time into "19:30", using the
     * 24-hour clock.
     *
     * <p>Every part of the pattern is fixed width, which is why the times and
     * dates line up neatly down the schedule no matter how long the words are.
     */
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("EEE, dd MMM yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);
    private static final DateTimeFormatter DEADLINE_FORMATTER =
            DateTimeFormatter.ofPattern("EEE, dd MMM • HH:mm", Locale.ENGLISH);

    private final String id;
    private final String stadiumId;
    private final EventType type;
    private final String title;
    private final String sport;
    private final String teamOne;
    private final String teamTwo;
    private final String artist;
    private final String description;
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime doorsTime;
    private final LocalDateTime bookingDeadline;
    private final double priceFactor;

    public StadiumEvent(String id,
                        String stadiumId,
                        EventType type,
                        String title,
                        String sport,
                        String teamOne,
                        String teamTwo,
                        String artist,
                        String description,
                        LocalDate date,
                        LocalTime startTime,
                        LocalTime doorsTime,
                        double priceFactor) {
        this(id, stadiumId, type, title, sport, teamOne, teamTwo, artist, description,
                date, startTime, doorsTime, priceFactor,
                date.atTime(startTime).minusHours(2));
    }

    public StadiumEvent(String id,
                        String stadiumId,
                        EventType type,
                        String title,
                        String sport,
                        String teamOne,
                        String teamTwo,
                        String artist,
                        String description,
                        LocalDate date,
                        LocalTime startTime,
                        LocalTime doorsTime,
                        double priceFactor,
                        LocalDateTime bookingDeadline) {
        this.id = Objects.requireNonNull(id, "id");
        this.stadiumId = Objects.requireNonNull(stadiumId, "stadiumId");
        this.type = Objects.requireNonNull(type, "type");
        this.title = Objects.requireNonNull(title, "title");
        this.sport = sport;
        this.teamOne = teamOne;
        this.teamTwo = teamTwo;
        this.artist = artist;
        this.description = Objects.requireNonNull(description, "description");
        this.date = Objects.requireNonNull(date, "date");
        this.startTime = Objects.requireNonNull(startTime, "startTime");
        this.doorsTime = doorsTime;
        this.bookingDeadline = Objects.requireNonNull(bookingDeadline, "bookingDeadline");
        if (priceFactor <= 0) {
            throw new IllegalArgumentException("Event price factor must be positive");
        }
        this.priceFactor = priceFactor;
    }

    public String getId() {
        return id;
    }

    public String getStadiumId() {
        return stadiumId;
    }

    public EventType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getSport() {
        return sport;
    }

    public String getTeamOne() {
        return teamOne;
    }

    public String getTeamTwo() {
        return teamTwo;
    }

    public String getArtist() {
        return artist;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getDoorsTime() {
        return doorsTime;
    }

    public LocalDateTime getBookingDeadline() {
        return bookingDeadline;
    }

    public double getPriceFactor() {
        return priceFactor;
    }

    public boolean isGame() {
        return type == EventType.GAME;
    }

    public boolean isConcert() {
        return type == EventType.CONCERT;
    }

    /**
     * Can somebody still book this event?
     *
     * <p>True while the clock is before the deadline. Once the deadline passes
     * this turns false on its own, with nobody having to remember to close
     * anything.
     */
    public boolean isBookingOpen() {
        return LocalDateTime.now().isBefore(bookingDeadline);
    }

    public String getBookingDeadlineLabel() {
        return DEADLINE_FORMATTER.format(bookingDeadline);
    }

    public String getCountdownLabel() {
        return BookingCountdown.format(bookingDeadline);
    }

    /** "Booking open" or "Booking closed". */
    public String getBookingStateLabel() {
        return isBookingOpen() ? "Booking open" : "Booking closed";
    }

    /**
     * The name of the event, as a heading.
     *
     * <p>A game is shown as the two teams, so "Uganda Cranes vs Kenya Harambee
     * Stars", whatever the title was written as. A concert just uses its title.
     */
    public String getHeadline() {
        if (isGame()) {
            return teamOne + " vs " + teamTwo;
        }
        return title;
    }

    /**
     * A longer description, for the details screen: the sport and the teams for
     * a game, or the artist for a concert.
     */
    public String getEventDetails() {
        if (isGame()) {
            return sport + "  •  " + teamOne + " vs " + teamTwo;
        }
        return artist + "  •  Live concert";
    }

    public String getDateLabel() {
        return DATE_FORMATTER.format(date);
    }

    public String getTimeLabel() {
        return TIME_FORMATTER.format(startTime);
    }

    public String getDoorsLabel() {
        return doorsTime == null ? "" : TIME_FORMATTER.format(doorsTime);
    }

    /** The date and the time together: "Wed, 07 Oct 2026  •  19:30". */
    public String getWhenLabel() {
        return getDateLabel() + "  •  " + getTimeLabel();
    }

    /**
     * Every word about this event as one lower-case line, for the search box.
     *
     * <p>All of it goes into one string so that searching is a single question:
     * does what somebody typed appear anywhere in here? That way "cranes",
     * "qualifier" and "07 Oct" all find the same match without each needing its
     * own rule.
     */
    public String searchableText() {
        return String.join(" ",
                id, stadiumId, type.getLabel(), title,
                sport == null ? "" : sport,
                teamOne == null ? "" : teamOne,
                teamTwo == null ? "" : teamTwo,
                artist == null ? "" : artist,
                description, getDateLabel(), getTimeLabel(), getBookingDeadlineLabel(), date.toString())
                .toLowerCase(Locale.ENGLISH);
    }

    @Override
    public String toString() {
        return getHeadline();
    }
}
