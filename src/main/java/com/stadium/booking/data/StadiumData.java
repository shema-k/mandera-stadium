package com.stadium.booking.data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
/**
 * Demo data for the venue directory and schedules. The dates are generated
 * relative to the current day so the directory always has upcoming events.
 */
public final class StadiumData {
    /**
     * Labels for the four independent seating sections (A to D). Section A is the
     * premium stand, and prices fall through to section D at the back. Declared
     * before the lists below because static initialisers run in declaration order.
     */
    private static final String[] SECTION_LABELS = {
            "VIP Box", "Main Stand", "Terrace", "Kampala End"};

    private static final LocalDate TODAY = LocalDate.now();
    private static final List<Stadium> STADIUMS = createStadiums();
    private static final List<StadiumEvent> EVENTS = createEvents();
    private static final List<StadiumAnnouncement> ANNOUNCEMENTS = createAnnouncements();

    private StadiumData() {
    }

    public static List<Stadium> getStadiums() {
        return STADIUMS;
    }

    public static Stadium getStadium(String id) {
        for (Stadium stadium : STADIUMS) {
            if (stadium.getId().equals(id)) {
                return stadium;
            }
        }
        return null;
    }

    public static List<StadiumEvent> getEvents() {
        return EVENTS;
    }

    public static List<StadiumEvent> getEvents(String stadiumId) {
        return EVENTS.stream()
                .filter(event -> event.getStadiumId().equals(stadiumId))
                .sorted(eventOrder())
                .collect(Collectors.toList());
    }

    public static StadiumEvent getEvent(String id) {
        for (StadiumEvent event : EVENTS) {
            if (event.getId().equals(id)) {
                return event;
            }
        }
        return null;
    }

    public static List<LocalDate> getDates(String stadiumId) {
        Set<LocalDate> dates = new LinkedHashSet<>();
        for (StadiumEvent event : getEvents(stadiumId)) {
            dates.add(event.getDate());
        }
        return new ArrayList<>(dates);
    }

    public static List<StadiumAnnouncement> getAnnouncements() {
        return ANNOUNCEMENTS;
    }

    public static List<StadiumAnnouncement> getAnnouncements(String stadiumId) {
        return ANNOUNCEMENTS.stream()
                .filter(announcement -> announcement.getStadiumId().equals(stadiumId))
                .collect(Collectors.toList());
    }

    private static List<Stadium> createStadiums() {
        List<Stadium> stadiums = new ArrayList<>();
        stadiums.add(new Stadium(
                "namboole",
                "Mandela National Stadium (Namboole)",
                "Kampala",
                "Uganda",
                "National multi-purpose stadium",
                "Uganda's flagship arena on Namboole Hill, opened in 1997 with Chinese government "
                        + "funding. Home of the Cranes national team and a regular host of Uganda "
                        + "Premier League derbies, CHAN matches and major concerts.",
                "Namboole Hill, Kira Municipality, Wakiso District",
                45202,
                "#dc2626",
                StadiumShape.OVAL,
                sections(114, 99, 250000, 127, 89, 190000, 127, 89, 140000, 130, 87, 95000)));
        return Collections.unmodifiableList(stadiums);
    }

    /**
     * Builds the four seating sections from explicit grids. Capacities are the real
     * published figures, so the grids are sized to total exactly the stated
     * capacity, which {@link Stadium} validates.
     */
    private static List<SeatSection> sections(int rowsA, int seatsA, double priceA,
                                              int rowsB, int seatsB, double priceB,
                                              int rowsC, int seatsC, double priceC,
                                              int rowsD, int seatsD, double priceD) {
        List<SeatSection> sections = new ArrayList<>();
        sections.add(new SeatSection("A", SECTION_LABELS[0], rowsA, seatsA, priceA));
        sections.add(new SeatSection("B", SECTION_LABELS[1], rowsB, seatsB, priceB));
        sections.add(new SeatSection("C", SECTION_LABELS[2], rowsC, seatsC, priceC));
        sections.add(new SeatSection("D", SECTION_LABELS[3], rowsD, seatsD, priceD));
        return sections;
    }

    private static List<StadiumEvent> createEvents() {
        List<StadiumEvent> events = new ArrayList<>();

        // --- Mandela National Stadium (Namboole), Kampala ---
        events.add(game("namboole-01", "namboole", "Football",
                "Uganda Cranes", "Kenya Harambee Stars", 1, 19, 30, 17, 30, 1.25,
                "A CECAFA qualifier at the national stadium, with the Cranes playing in front of a "
                        + "full Namboole crowd."));
        events.add(game("namboole-02", "namboole", "Football",
                "Vipers SC", "SC Villa", 3, 15, 0, 13, 0, 1.20,
                "The biggest club duel on the calendar, with the two most successful Kampala sides "
                        + "meeting on the big stage."));
        events.add(concert("namboole-03", "namboole", "Eddy Kenzo Live at Namboole",
                "Eddy Kenzo", 2, 20, 0, 18, 0, 1.10,
                "The Big Talent boss brings his dancehall catalogue to the national stadium for a full "
                        + "night of hits."));
        events.add(game("namboole-04", "namboole", "Football",
                "Uganda Cranes", "Tanzania Taifa Stars", 5, 20, 0, 18, 0, 1.20,
                "A regional derby in the east, with the Cranes seeking revenge from the last meeting."));
        events.add(concert("namboole-05", "namboole", "Bwatibhappu Festival Night",
                "Bobi Wine", 6, 20, 0, 18, 30, 1.15,
                "One night of the Bwatibhappu festival: a homecoming show with a full light and "
                        + "effects production."));
        events.add(game("namboole-06", "namboole", "Football",
                "KCCA FC", "Express FC", 8, 19, 0, 17, 0, 1.10,
                "A Kampala derby at national stadium scale, between the city club and the old guard."));
        events.add(game("namboole-07", "namboole", "Football",
                "URA FC", "Police FC", 10, 16, 0, 14, 0, 1.05,
                "Two of the country's oldest institution clubs, URA and Police, meeting on the big "
                        + "stage."));
        events.add(concert("namboole-08", "namboole", "Jose Chameleone: The Golden Voice",
                "Jose Chameleone", 12, 20, 0, 18, 0, 1.00,
                "The Leone Island boss returns to the national stadium with a career-spanning set."));

        return Collections.unmodifiableList(events);
    }

    private static List<StadiumAnnouncement> createAnnouncements() {
        LocalDateTime now = LocalDateTime.now();
        List<StadiumAnnouncement> announcements = new ArrayList<>();
        announcements.add(new StadiumAnnouncement(
                "namboole-notice-01", "namboole", "namboole-01", AnnouncementType.NOTICE,
                "Match-day entry and security screening",
                "Spectators should arrive early through the north gate. Accessible seating and "
                        + "wheelchair spaces are available from the VIP box entrance.",
                now.minusHours(2), now.plusDays(30)));
        announcements.add(new StadiumAnnouncement(
                "namboole-cancel-01", "namboole", "namboole-04", AnnouncementType.CANCELLATION,
                "Match postponed — heavy rain warning",
                "The Cranes vs Taifa Stars qualifier has been postponed while the venue team monitors "
                        + "a heavy rain warning for the Kampala area.",
                now.minusHours(1), now.plusDays(14)));
        announcements.add(new StadiumAnnouncement(
                "namboole-schedule-01", "namboole", "", AnnouncementType.SCHEDULE_CHANGE,
                "Concert gates may open earlier",
                "For concerts, gates may open up to 60 minutes before the published doors time to "
                        + "allow security screening.",
                now.minusDays(1), now.plusDays(45)));
        return Collections.unmodifiableList(announcements);
    }

    private static StadiumEvent game(String id,
                                     String stadiumId,
                                     String sport,
                                     String teamOne,
                                     String teamTwo,
                                     int dayOffset,
                                     int hour,
                                     int minute,
                                     int doorsHour,
                                     int doorsMinute,
                                     double priceFactor,
                                     String description) {
        String title = teamOne + " vs " + teamTwo;
        return new StadiumEvent(id, stadiumId, EventType.GAME, title, sport, teamOne, teamTwo,
                null, description, TODAY.plusDays(dayOffset), LocalTime.of(hour, minute),
                LocalTime.of(doorsHour, doorsMinute), priceFactor);
    }

    private static StadiumEvent concert(String id,
                                        String stadiumId,
                                        String title,
                                        String artist,
                                        int dayOffset,
                                        int hour,
                                        int minute,
                                        int doorsHour,
                                        int doorsMinute,
                                        double priceFactor,
                                        String description) {
        return new StadiumEvent(id, stadiumId, EventType.CONCERT, title, null, null, null,
                artist, description, TODAY.plusDays(dayOffset), LocalTime.of(hour, minute),
                LocalTime.of(doorsHour, doorsMinute), priceFactor);
    }

    private static Comparator<StadiumEvent> eventOrder() {
        return Comparator.comparing(StadiumEvent::getDate)
                .thenComparing(StadiumEvent::getStartTime)
                .thenComparing(StadiumEvent::getId);
    }
}
