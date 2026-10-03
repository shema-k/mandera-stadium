package com.stadium.booking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads the screen sources so the translation checks have something to look at.
 *
 * <p>Two kinds of fact can only be had by reading the code, and neither is
 * visible from the running application:
 *
 * <ul>
 *   <li>Which keys the screens ask for. Asking the running application would
 *       find the keys on the screen the tests happened to reach.
 *   <li>Whether a screen is redrawn when the language changes. That is a
 *       property of the code path, not of any wording.
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
    private static final List<String> SOURCES = Arrays.asList(
            "StadiumBookingApp.java", "SeatMapPanel.java", "DetailsFormPanel.java",
            "Receipt.java", "TicketBuilder.java", "StadiumPhotoPanel.java",
            "OccupancyReport.java", "BookingService.java");

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
    static Set<String> keysInMainSources() {
        return new LinkedHashSet<>(allKeyUses(false));
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
    static Set<String> screensRedrawnOnLanguageChange() {
        Set<String> found = new LinkedHashSet<>();
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

    /** Screen keys that some language does not define. */
    static List<String> keysMissingFromAnyLanguage(Set<String> keys) {
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
        for (Map.Entry<String, Map<Messages.Language, String>> row
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
        for (Map.Entry<String, Map<Messages.Language, String>> row
                : Messages.allWording().entrySet()) {
            Map<Messages.Language, String> byLanguage = row.getValue();
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
    static Map<String, String> translationsCopiedFromEnglish() {
        Map<String, String> copied = new java.util.TreeMap<>();
        for (Map.Entry<String, Map<Messages.Language, String>> row
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
