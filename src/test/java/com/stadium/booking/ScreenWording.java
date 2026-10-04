package com.stadium.booking;

import static com.stadium.booking.TestRunner.assertTrue;
import static com.stadium.booking.TestRunner.suite;
import static com.stadium.booking.TestRunner.test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads the screen sources, so the translation checks have something to look at.
 *
 * <p>Two kinds of fact can only be had by reading the code, and neither is
 * visible from the running application:
 *
 * <ul>
 *   <li>Which keys the screens ask for. Asking the running application would
 *       find the keys on the screen the tests happened to reach.
 *   <li>Whether a screen is redrawn when the language changes, and whether a
 *       long-lived widget is renamed. That is a property of the code path, not of
 *       any wording.
 * </ul>
 *
 * <p>Kept apart from the tests themselves so the tests read as assertions about
 * behaviour rather than as a pile of string handling.
 */
final class ScreenWording {
    private ScreenWording() {
    }

    /** The screens that make up the application, by the name they record. */
    private static final String[] SCREENS = {
            "stadium", "stadium-details", "schedules", "event-details",
            "booking", "bookings", "seats", "occupancy", "saved-seats"};

    /** The files a customer can read wording out of. */
    private static final List<String> SOURCES = List.of(
            "StadiumBookingApp.java", "SeatMapPanel.java", "DetailsFormPanel.java",
            "Receipt.java", "TicketBuilder.java", "StadiumPhotoPanel.java",
            "OccupancyReport.java", "BookingService.java", "VenueWords.java");

    /** A call to the wording table, capturing the key and what follows it. */
    private static final Pattern CALL = Pattern.compile(
            "(?:Messages\\.get|text)\\(\\s*\"([a-zA-Z0-9_.]+)\"([^)]*)\\)");

    private static String source(String file) {
        String path = "src/main/java/com/stadium/booking/" + file;
        try {
            return new String(java.nio.file.Files.readAllBytes(
                    java.nio.file.Paths.get(path)), java.nio.charset.StandardCharsets.UTF_8);
        } catch (java.io.IOException problem) {
            throw new AssertionError("could not read " + path + ": " + problem);
        }
    }

    /** Every key the screens ask for, in the order first seen. */
    static List<String> keysInMainSources() {
        return allKeyUses(false);
    }

    /**
     * Keys read without an argument even though their wording has a {0} in it.
     *
     * <p>This is the fault that matters for placeholders. A key whose wording is
     * "up to {0} seats" is correct, and reading it with the count gives the right
     * sentence. Read without one, the customer is shown the braces. Finding this
     * needs the call, not the key, so the argument list is matched too.
     */
    static List<String> keysReadWithoutTheirArgument() {
        return allKeyUses(true);
    }

    /** Keys, or keys-with-no-argument when {@code bareCallsOnly} is set. */
    private static List<String> allKeyUses(boolean bareCallsOnly) {
        List<String> keys = new ArrayList<>();
        for (String file : SOURCES) {
            Matcher matcher = CALL.matcher(source(file));
            while (matcher.find()) {
                boolean bare = matcher.group(2).trim().isEmpty();
                if (bareCallsOnly == bare) {
                    keys.add(matcher.group(1));
                }
            }
        }
        return keys;
    }

    /**
     * Bare calls to keys that actually hold a placeholder.
     *
     * <p>Filtering by the table rather than by the call alone matters: most calls
     * pass nothing, and nearly all of them are correct because their wording has
     * no {0} in it. Only the calls that would really show braces are reported.
     */
    static List<String> bareCallsThatWouldShowBraces() {
        List<String> offenders = new ArrayList<>();
        for (String key : allKeyUses(true)) {
            for (Messages.Language language : Messages.Language.values()) {
                if (!Messages.isDefined(language, key)) {
                    continue;
                }
                String wording = Messages.allWording().get(key).get(language);
                if (wording != null && wording.contains("{0}")) {
                    offenders.add(language + "/" + key);
                }
            }
        }
        return offenders;
    }

    /** The screens {@code applyLanguage} rebuilds. */
    static java.util.Set<String> screensRedrawnOnLanguageChange() {
        java.util.Set<String> found = new java.util.LinkedHashSet<>();
        String code = source("StadiumBookingApp.java");
        int start = code.indexOf("private void redrawCurrentScreen()");
        if (start < 0) {
            return found;
        }
        int end = code.indexOf("\n    private ", start + 10);
        String body = end < 0 ? code.substring(start) : code.substring(start, end);
        Matcher matcher = Pattern.compile("case \"([a-z-]+)\"").matcher(body);
        while (matcher.find()) {
            found.add(matcher.group(1));
        }
        return found;
    }

    /**
     * Widgets that a language switch has to rename by hand.
     *
     * <p>Long-lived components are built once, when the application opens, so the
     * redraw of a screen cannot reach them. Three kinds were missed: the search
     * boxes and their placeholder text, the status line, and the seat map's tabs.
     * The tabs are the interesting one, because their names come from the venue
     * data and are not keys at all — so nothing in the wording table could have
     * told anyone they needed renaming.
     *
     * <p>Read from the body of {@code applyLanguage} and {@code retranslate}, so
     * removing the renaming fails here rather than only on screen.
     */
    static List<String> widgetsRenamedOnLanguageChange() {
        List<String> renamed = new ArrayList<>();
        String app = source("StadiumBookingApp.java");
        int start = app.indexOf("private void applyLanguage()");
        int end = app.indexOf("\n    private ", start + 10);
        String body = end < 0 ? app.substring(start) : app.substring(start, end);
        Matcher matcher = Pattern.compile("(\\w+)\\.(setHint|setText|setTitle)\\(")
                .matcher(body);
        while (matcher.find()) {
            renamed.add(matcher.group(1));
        }
        String map = source("SeatMapPanel.java");
        int mapStart = map.indexOf("void retranslate()");
        int mapEnd = map.indexOf("\n    private ", mapStart + 10);
        String mapBody = mapEnd < 0 ? map.substring(mapStart)
                : map.substring(mapStart, mapEnd);
        if (mapBody.contains("setTitleAt")) {
            renamed.add("sectionTabs");
        }
        return renamed;
    }

    /**
     * Widgets built once, named from the venue data, that no switch renames.
     *
     * <p>The seat map tabs are the case. They are named from the end names in the
     * data, so they are not keys, and they are built once, so a switch does not
     * rebuild them. Both faults together leave "VIP Box" in English above a
     * screen in Luganda.
     */
    static List<String> dataNamedTabsWithoutRenaming() {
        List<String> missing = new ArrayList<>();
        String map = source("SeatMapPanel.java");
        boolean tabsAreDataNamed = map.contains("addTab(")
                && map.contains("section.getLabel()");
        boolean renamedOnSwitch = widgetsRenamedOnLanguageChange().contains("sectionTabs");
        if (tabsAreDataNamed && !renamedOnSwitch) {
            missing.add("sectionTabs");
        }
        return missing;
    }

    /** Screen keys that some language does not define. */
    static List<String> keysMissingFromAnyLanguage(List<String> keys) {
        List<String> gaps = new ArrayList<>();
        for (String key : keys) {
            for (Messages.Language language : Messages.Language.values()) {
                if (!Messages.isDefined(language, key)) {
                    gaps.add(language + "/" + key);
                }
            }
        }
        return gaps;
    }

    /** Every key in the table that some language does not define. */
    static List<String> allKeysMissingFromAnyLanguage() {
        List<String> gaps = new ArrayList<>();
        for (java.util.Map.Entry<String, java.util.Map<Messages.Language, String>> row
                : Messages.allWording().entrySet()) {
            for (Messages.Language language : Messages.Language.values()) {
                if (!row.getValue().containsKey(language)) {
                    gaps.add(language + "/" + row.getKey());
                }
            }
        }
        return gaps;
    }

    /**
     * Keys that read exactly the same in all three languages.
     *
     * <p>Not automatically a fault — "Email" and "Status" are the same word in
     * Luganda and Swahili — but each one is listed so it can be looked at, and a
     * whole sentence appearing in three languages is almost certainly a
     * translation that was never done.
     */
    static List<String> keysIdenticalInAllLanguages() {
        List<String> identical = new ArrayList<>();
        for (java.util.Map.Entry<String, java.util.Map<Messages.Language, String>> row
                : Messages.allWording().entrySet()) {
            java.util.Map<Messages.Language, String> byLanguage = row.getValue();
            Messages.Language first = null;
            String value = null;
            boolean same = true;
            for (Messages.Language language : Messages.Language.values()) {
                String wording = byLanguage.get(language);
                if (wording == null) {
                    same = false;
                    break;
                }
                if (first == null) {
                    first = language;
                    value = wording;
                } else if (!value.equals(wording)) {
                    same = false;
                    break;
                }
            }
            // A single word repeated across languages is normal; a sentence is not.
            if (same && value != null && value.trim().indexOf(' ') >= 0) {
                identical.add(row.getKey());
            }
        }
        return identical;
    }

    /** Translations that are a byte-for-byte copy of the English. */
    static java.util.Map<String, String> translationsCopiedFromEnglish() {
        java.util.Map<String, String> copied = new java.util.TreeMap<>();
        for (java.util.Map.Entry<String, java.util.Map<Messages.Language, String>> row
                : Messages.allWording().entrySet()) {
            String english = row.getValue().get(Messages.Language.ENGLISH);
            if (english == null) {
                continue;
            }
            for (Messages.Language language : Messages.Language.values()) {
                if (language == Messages.Language.ENGLISH) {
                    continue;
                }
                String wording = row.getValue().get(language);
                // A single word such as "Email" or "Status" is genuinely the same.
                if (wording != null && wording.equals(english)
                        && wording.trim().indexOf(' ') >= 0) {
                    copied.put(language + "/" + row.getKey(), wording);
                }
            }
        }
        return copied;
    }
}
