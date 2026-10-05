package com.stadium.booking;

import com.stadium.booking.data.AnnouncementType;
import com.stadium.booking.data.SeatSection;
import com.stadium.booking.data.Stadium;
import com.stadium.booking.data.StadiumAnnouncement;
import com.stadium.booking.data.StadiumData;
import com.stadium.booking.data.StadiumEvent;

import static com.stadium.booking.TestRunner.assertEquals;
import static com.stadium.booking.TestRunner.assertTrue;
import static com.stadium.booking.TestRunner.suite;
import static com.stadium.booking.TestRunner.test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Checks the Ugandan venue, club and artist data stays internally consistent. */
final class StadiumDataTest {
    private StadiumDataTest() {
    }

    static void register() {
        suite("Venue and event data");

        test("the one venue is in Uganda, with four sections", () -> {
            List<Stadium> stadiums = StadiumData.getStadiums();
            assertEquals(1, stadiums.size(), "this system books a single stadium");
            for (Stadium stadium : stadiums) {
                assertEquals("Uganda", stadium.getCountry(),
                        "every venue must be in Uganda: " + stadium.getName());
                assertTrue(stadium.getSections().size() == 4,
                        "every venue needs four sections: " + stadium.getName());
            }
        });

        test("the venue is Mandela National Stadium", () -> {
            Stadium stadium = StadiumData.getStadium("namboole");
            assertTrue(stadium != null, "Namboole must be the venue");
            assertEquals("Mandela National Stadium (Namboole)", stadium.getName(), "venue name");
            assertEquals("Kampala", stadium.getCity(), "venue city");
            assertEquals(45202, stadium.getCapacity(), "published capacity");
            assertEquals(stadium.getId(), StadiumData.getStadiums().get(0).getId(),
                    "Namboole is the only venue in the directory");
        });

        test("the four ends are priced from premium to value", () -> {
            Stadium stadium = StadiumData.getStadium("namboole");
            assertEquals("A", stadium.getSections().get(0).getId(), "first end is A");
            assertEquals("D", stadium.getSections().get(3).getId(), "last end is D");
            SeatSection premium = stadium.getSections().get(0);
            SeatSection value = stadium.getSections().get(3);
            assertTrue(premium.getBasePrice() > value.getBasePrice(),
                    "the VIP end must cost more than the value end");
        });

        test("declared capacity equals the seat sections", () -> {
            for (Stadium stadium : StadiumData.getStadiums()) {
                assertEquals(stadium.getCapacity(), stadium.getSeatCount(),
                        "capacity must match the grid for " + stadium.getName());
            }
        });

        test("prices fall from the front to the back", () -> {
            for (Stadium stadium : StadiumData.getStadiums()) {
                SeatSection front = stadium.getSections().get(0);
                SeatSection back = stadium.getSections().get(3);
                assertTrue(front.getBasePrice() > back.getBasePrice(),
                        "VIP section must cost more than the back: " + stadium.getName());
            }
        });

        test("every venue has at least one event", () -> {
            for (Stadium stadium : StadiumData.getStadiums()) {
                assertTrue(!StadiumData.getEvents(stadium.getId()).isEmpty(),
                        "no events for " + stadium.getName());
            }
        });

        test("event ids are unique", () -> {
            Set<String> ids = new HashSet<>();
            for (StadiumEvent event : StadiumData.getEvents()) {
                assertTrue(ids.add(event.getId()), "duplicate event id " + event.getId());
            }
        });

        test("events are in the future", () -> {
            for (StadiumEvent event : StadiumData.getEvents()) {
                assertTrue(!event.getDate().isBefore(LocalDate.now()),
                        "past event in the data: " + event.getId());
            }
        });

        test("announcements point at real venues and events", () -> {
            for (StadiumAnnouncement announcement : StadiumData.getAnnouncements()) {
                assertTrue(StadiumData.getStadium(announcement.getStadiumId()) != null,
                        "unknown venue " + announcement.getStadiumId());
                if (announcement.getEventId() != null && !announcement.getEventId().isEmpty()) {
                    assertTrue(StadiumData.getEvent(announcement.getEventId()) != null,
                            "unknown event " + announcement.getEventId());
                }
            }
        });

        test("cancelled and emergency notices block booking", () -> {
            int blocking = 0;
            for (StadiumAnnouncement announcement : StadiumData.getAnnouncements()) {
                if (announcement.getType() == AnnouncementType.CANCELLATION
                        || announcement.getType() == AnnouncementType.EMERGENCY) {
                    blocking++;
                    StadiumEvent event = StadiumData.getEvent(announcement.getEventId());
                    assertTrue(event != null, "blocking notice without an event");
                }
            }
            assertTrue(blocking > 0, "at least one blocking notice is expected");
        });

        test("venue badges ignore punctuation", () -> {
            Stadium namboole = StadiumData.getStadium("namboole");
            assertEquals("MN", namboole.initials(), "Namboole badge");
        });
    }
}
