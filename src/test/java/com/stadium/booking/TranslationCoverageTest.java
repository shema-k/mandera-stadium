package com.stadium.booking;

import static com.stadium.booking.TestRunner.assertEquals;
import static com.stadium.booking.TestRunner.assertFalse;
import static com.stadium.booking.TestRunner.assertTrue;
import static com.stadium.booking.TestRunner.suite;
import static com.stadium.booking.TestRunner.test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Everything a customer reads has to exist in all three languages.
 *
 * <p>Three separate things can go wrong when wording is moved into a table, and
 * each has happened here:
 *
 * <ul>
 *   <li>A key defined in English and not in Luganda or Swahili, so those screens
 *       quietly show English. {@link Messages#get} falls back to English
 *       rather than showing nothing, which hides the fault instead of reporting
 *       it — so the check is on the table, not on what a screen happens to show.
 *   <li>A number written into the wording instead of read from the constant, so
 *       changing the limit leaves the screen promising the old one.
 *   <li>A label the language switch does not reach, because the screen was never
 *       redrawn.
 * </ul>
 */
final class TranslationCoverageTest {
    private TranslationCoverageTest() {
    }

    /** Every language, so each check can insist on all three rather than two. */
    private static final List<Messages.Language> ALL =
            java.util.Arrays.asList(Messages.Language.values());

    /**
     * The wording table as it stands, read back through the class itself.
     *
     * <p>Read through {@link Messages#get} with the English fallback in place,
     * which is why a key missing from a translation cannot be spotted this way.
     * Only the English side is genuinely visible; the other languages need the
     * source, so they are checked against the file below.
     */
    private static String english(String key) {
        Messages.setLanguage(Messages.Language.ENGLISH);
        return Messages.get(key);
    }

    /** The keys the screens actually ask for. */
    private static Set<String> keysUsedInScreens() {
        Set<String> used = new LinkedHashSet<>();
        used.addAll(ScreenWording.keysInMainSources());
        return used;
    }

    static void register() {
        suite("Every screen in every language");

        test("no screen asks for wording that does not exist", () -> {
            // A key nobody defined shows the customer the key itself, in a row of
            // labels that read normally otherwise. It looks like a bug report
            // about the layout rather than about a missing word.
            List<String> unknown = new ArrayList<>();
            for (String key : keysUsedInScreens()) {
                for (Messages.Language language : ALL) {
                    Messages.setLanguage(language);
                    String shown = Messages.get(key);
                    if (shown != null && shown.equals(key)) {
                        unknown.add(language + "/" + key);
                    }
                }
            }
            Messages.setLanguage(Messages.Language.ENGLISH);
            assertTrue(unknown.isEmpty(),
                    "these keys are used but never defined: " + unknown);
        });

        test("a language never falls back to English for a screen label", () -> {
            // The fallback exists so a missing translation shows something
            // readable rather than a blank. That is right at runtime and wrong as
            // a test, because it makes an untranslated screen look fine. Every
            // key used on a screen therefore has to be present in all three.
            List<String> missing = ScreenWording.keysMissingFromAnyLanguage(
                    keysUsedInScreens());
            Messages.setLanguage(Messages.Language.ENGLISH);
            assertTrue(missing.isEmpty(),
                    "these screen keys are not carried by every language: "
                            + missing.subList(0, Math.min(12, missing.size())));
        });

        test("changing the language changes what the screens say", () -> {
            // The check that matters most, and the one that catches the common
            // fault: a key that happens to hold the same text in every language,
            // so selecting another language leaves that line in English. Some
            // words are genuinely the same in all three — "Email" — and are
            // listed rather than left to look like a miss.
            List<String> identical = ScreenWording.keysIdenticalInAllLanguages();
            assertTrue(identical.isEmpty(),
                    "these keys read the same in all three languages, so that line "
                            + "does not change when the language does: " + identical);
        });

        test("no screen reads a placeholder without filling it in", () -> {
            // A key whose wording contains {0} is correct on its own — it is what
            // keeps the seat limit following the code. Read without the argument
            // it shows the customer the braces. So the check is on the call, not
            // on the key: a key with a placeholder that no screen reads bare is
            // perfectly fine.
            List<String> bare = ScreenWording.bareCallsThatWouldShowBraces();
            Messages.setLanguage(Messages.Language.ENGLISH);
            assertTrue(bare.isEmpty(),
                    "these keys hold a {0} but are read without an argument, so the "
                            + "braces would reach the screen: " + bare);
        });

        test("the seat limit on screen comes from the code, not the wording", () -> {
            // The fault this guards: the number was typed into the sentence, so
            // raising the limit left the screen claiming the old one. Anything
            // quoting the limit must now take it from the constant.
            Messages.setLanguage(Messages.Language.ENGLISH);
            String hint = Messages.get("booking.limitHint",
                    BookingService.MAX_SEATS_PER_BOOKING);
            assertTrue(hint.contains(String.valueOf(BookingService.MAX_SEATS_PER_BOOKING)),
                    "the seat screen must quote " + BookingService.MAX_SEATS_PER_BOOKING
                            + ", said: " + hint);
            String closing = Messages.get("home.seatLimit",
                    BookingService.MAX_SEATS_PER_BOOKING);
            assertTrue(closing.contains(String.valueOf(BookingService.MAX_SEATS_PER_BOOKING)),
                    "the venue page must quote it too, said: " + closing);
            // The old wording said "Six seats per reservation" in the middle of a
            // sentence, which nothing could check because the number was part of
            // the prose. Now it is an argument, so a change to the limit is
            // followed by every screen that mentions it.
            assertFalse(closing.contains("Six"),
                    "must not still say six seats: " + closing);
        });

        test("switching language redraws whatever screen is showing", () -> {
            // applyLanguage used to redraw only the booking screen, so the
            // schedule, the bookings list, the occupancy report and the saved
            // seats carried on reading in the old language with a translated
            // header above them. Every screen it can rebuild is checked here.
            assertTrue(ScreenWording.screensRedrawnOnLanguageChange().size() >= 8,
                    "every screen must be redrawn when the language changes, found: "
                            + ScreenWording.screensRedrawnOnLanguageChange());
        });

        test("the wording table holds no English left in a translation", () -> {
            // A translation that is a copy of the English is the failure a
            // reviewer would catch by eye, and a test can catch by comparison.
            // Words that are legitimately the same are listed with a reason.
            Map<String, String> copied = ScreenWording.translationsCopiedFromEnglish();
            assertTrue(copied.isEmpty(),
                    "these translations repeat the English exactly: " + copied);
        });

        test("every language carries every key in the table", () -> {
            // The table as a whole, not just the keys the screens use, so a key
            // added for a screen still to be built does not arrive half finished.
            List<String> gaps = ScreenWording.allKeysMissingFromAnyLanguage();
            Messages.setLanguage(Messages.Language.ENGLISH);
            assertTrue(gaps.isEmpty(),
                    "keys not carried by every language: "
                            + gaps.subList(0, Math.min(12, gaps.size())));
        });
    }
}
