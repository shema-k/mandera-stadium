package com.stadium.booking;

import com.stadium.booking.booking.Booking;
import com.stadium.booking.booking.BookingService;
import com.stadium.booking.data.Stadium;
import com.stadium.booking.text.Messages;

import static com.stadium.booking.TestRunner.assertEquals;
import static com.stadium.booking.TestRunner.assertFalse;
import static com.stadium.booking.TestRunner.assertTrue;
import static com.stadium.booking.TestRunner.suite;
import static com.stadium.booking.TestRunner.test;

/** The interface can run in English, Luganda or Swahili. */
final class LocaleTest {
    private LocaleTest() {
    }

    static void register() {
        suite("Languages");

        test("English is the default", () -> {
            Messages.setLanguage(Messages.Language.ENGLISH);
            assertEquals("Mandela National Stadium", Messages.get("directory.title"), "stadium title");
            assertEquals("Confirm booked seats", Messages.get("booking.confirm"), "confirm button");
        });

        test("Luganda translates the main wording", () -> {
            Messages.setLanguage(Messages.Language.LUGANDA);
            assertEquals("Ekizimbe kya Mandela", Messages.get("directory.title"),
                    "stadium title");
            assertEquals("Wekakise ebizitansa", Messages.get("booking.confirm"), "confirm button");
            assertEquals("Wakibwa", Messages.get("legend.booked"), "booked legend");
        });

        test("Swahili translates the main wording", () -> {
            Messages.setLanguage(Messages.Language.SWAHILI);
            assertEquals("Uwanja wa Taifa wa Mandela", Messages.get("directory.title"), "stadium title");
            assertEquals("Thibitisha viti", Messages.get("booking.confirm"), "confirm button");
            assertEquals("Vimeuzwa", Messages.get("legend.booked"), "booked legend");
        });

        test("the app's own name follows the language too", () -> {
            // The header carries the application name. It used to be left in
            // English on the theory that a product name is not translated, but
            // the whole interface is expected to follow the language, so the
            // name is translated with everything else and checked here.
            Messages.setLanguage(Messages.Language.SWAHILI);
            assertFalse("NAMBOOLE SEAT BOOKING".equals(Messages.get("app.tagline")),
                    "the app name must follow the selected language");
            assertFalse(Messages.get("app.tagline").isBlank(), "the app name must not be blank");
            Messages.setLanguage(Messages.Language.ENGLISH);
            assertEquals("NAMBOOLE SEAT BOOKING", Messages.get("app.tagline"),
                    "English still shows the English name");
        });

        test("an unknown key is shown rather than blank", () -> {
            assertEquals("no.such.key", Messages.get("no.such.key"),
                    "an unknown key is visible so the gap is obvious");
        });

        test("placeholders are substituted", () -> {
            Messages.setLanguage(Messages.Language.ENGLISH);
            assertEquals("3 venues available", Messages.get("directory.venuesAvailable", 3),
                    "number substituted");
        });

        test("money stays in shillings whatever the language", () -> {
            Messages.setLanguage(Messages.Language.LUGANDA);
            assertEquals("UGX 15,000", Messages.money(15_000), "shillings are not translated");
        });

        test("every language defines the same core keys", () -> {
            for (String key : new String[]{"directory.title", "booking.confirm", "legend.booked",
                    "bookings.refresh", "nav.occupancy"}) {
                for (Messages.Language language : Messages.Language.values()) {
                    Messages.setLanguage(language);
                    assertFalse(Messages.get(key).isBlank(),
                            "missing wording for " + key + " in " + language);
                }
            }
            Messages.setLanguage(Messages.Language.ENGLISH);
        });

        test("no wording carries stray characters from another script", () -> {
            // Guards against mojibake slipping into a translation: Luganda and
            // Swahili are Latin-script, so a Han or Cyrillic run is always a bug.
            String[] keys = {"directory.title", "directory.subtitle",
                    "directory.hero.title", "directory.hero.subtitle", "directory.open",
                    "directory.findVenue", "directory.search.hint", "stadium.upcoming",
                    "stadium.search.hint", "stadium.notices", "stadium.specialRequest",
                    "booking.title", "booking.confirm", "booking.clearSeats", "booking.selectSeat",
                    "booking.seatsHeld", "booking.viewBooked", "booking.priceOutline",
                    "booking.priceHint", "booking.feeHint", "booking.limitHint",
                    "booking.priceSubtitle", "booking.emptyPrice", "booking.yourDetails",
                    "booking.threeDetails", "booking.yourSelection", "booking.noSeats",
                    "legend.vacant", "legend.selected", "legend.held", "legend.booked",
                    "legend.closed", "bookings.title", "bookings.refresh", "bookings.export",
                    "bookings.cancelSelected", "bookings.hint", "seats.title", "seats.subtitle",
                    "nav.back", "nav.venues", "nav.bookings", "nav.bookedSeats", "nav.occupancy",
                    "nav.liveSchedules",
                    "common.backToStadiums", "common.backToSchedule", "common.allVenues",
                    "occupancy.title", "occupancy.subtitle", "occupancy.export",
                    "payment.title", "payment.question", "common.totalDue",
                    "seatMap.hint", "seatMap.priceGuide"};
            int checked = 0;
            for (String key : keys) {
                for (Messages.Language language : Messages.Language.values()) {
                    Messages.setLanguage(language);
                    // Rendered, not raw: some wording takes a number, such as the
                    // seat limit, and what the customer reads has no placeholder
                    // in it. Checking the template would either forbid the number
                    // being passed in or wave through a broken one.
                    String wording = Messages.get(key, 7, 7, 7, 7, 7, 7, 7, 7);
                    checked++;
                    assertFalse(wording.matches(".*[\\u2E80-\\u9FFF\\u0400-\\u04FF].*"),
                            "stray characters in \"" + key + "\" for " + language
                                    + ": " + wording);
                    assertFalse(wording.contains("?{") || wording.contains("}"),
                            "unsubstituted placeholder in \"" + key + "\": " + wording);
                }
            }
            Messages.setLanguage(Messages.Language.ENGLISH);
            assertEquals(keys.length * Messages.Language.values().length, checked,
                    "every core key in every language was checked");
        });

        test("the seat limit on screen matches the limit the code enforces", () -> {
            // The hint on the seat screen is a promise about the cap. If the two
            // ever drift apart the screen is either lying or under-stating, so
            // the wording is checked against the constant rather than eyeballed.
            Messages.setLanguage(Messages.Language.ENGLISH);
            String hint = Messages.get("booking.limitHint",
                    BookingService.MAX_SEATS_PER_BOOKING);
            assertTrue(hint.contains(String.valueOf(BookingService.MAX_SEATS_PER_BOOKING)),
                    "the hint must quote the real limit (" + BookingService.MAX_SEATS_PER_BOOKING
                            + "), said: " + hint);
            assertFalse(hint.contains(" 6 "), "must not still say six: " + hint);
            assertFalse(hint.contains("{0}"),
                    "the number must be substituted, not left as a placeholder: " + hint);
        });

        test("the seat limit on screen survives the limit being changed", () -> {
            // The wording used to carry a typed-in "20". Raising the constant then
            // left the screen promising the old cap while the code enforced the
            // new one, and nothing caught it except this test. Now the number
            // comes from the constant, so there is nothing left to fall out of
            // step — checked here by confirming no language carries its own copy.
            for (Messages.Language language : Messages.Language.values()) {
                Messages.setLanguage(language);
                String raw = Messages.get("booking.limitHint");
                assertTrue(raw.contains("{0}"),
                        language + " must take the limit from the constant rather than "
                                + "wording its own, said: " + raw);
                String filled = Messages.get("booking.limitHint",
                        BookingService.MAX_SEATS_PER_BOOKING);
                assertFalse(filled.contains("{0}"),
                        language + " must show the real number, said: " + filled);
                assertTrue(filled.contains(String.valueOf(BookingService.MAX_SEATS_PER_BOOKING)),
                        language + " must quote " + BookingService.MAX_SEATS_PER_BOOKING
                                + ", said: " + filled);
            }
            Messages.setLanguage(Messages.Language.ENGLISH);
        });

        test("every key is translated in every language", () -> {
            // The requirement this enforces: switching language must translate
            // the whole interface. A key that exists only in English falls back
            // silently, which is how parts of the screen used to stay English.
            // There are no exceptions: even the application's own name follows
            // the language, so every key must carry all three translations.
            java.util.List<String> missing = new java.util.ArrayList<>();
            for (String key : Messages.keys()) {
                for (Messages.Language language : Messages.Language.values()) {
                    if (!Messages.has(language, key)) {
                        missing.add(language.getCode() + ":" + key);
                    }
                }
            }
            assertTrue(missing.isEmpty(),
                    "these keys would fall back to English: " + missing);
        });

        test("switching language back to English restores the wording", () -> {
            Messages.setLanguage(Messages.Language.SWAHILI);
            Messages.setLanguage(Messages.Language.ENGLISH);
            assertEquals("Booking history", Messages.get("bookings.title"), "restored");
        });
    }
}
