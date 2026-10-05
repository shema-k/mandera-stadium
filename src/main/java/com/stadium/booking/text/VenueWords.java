package com.stadium.booking.text;

import com.stadium.booking.data.StadiumShape;

import java.util.Locale;
/**
 * The words for the things the venue data is made of: the four ends of the
 * stand, the kinds of event, and the shape of the ground.
 *
 * <p>These live in the data rather than in {@link Messages} because they are
 * facts about a place rather than wording the interface supplies. That is also
 * why they need translating: a customer choosing a seat has to be able to tell
 * the VIP box from the Terrace in their own language, and an event label reading
 * "GAME" beside a screen in Luganda is English leaking through.
 *
 * <p>Every lookup reads the language at the moment it is asked, not when the data
 * is built, so switching language takes effect without rebuilding the venue.
 */
public final class VenueWords {

    private VenueWords() {
    }

    /**
     * The name of one end of the stand, in the current language.
     *
     * @param sectionId the end, {@code A} to {@code D}
     * @param english   the name as the venue writes it, used for any end this
     *                  does not know, so a new stand is never shown blank
     */
    public static String sectionName(String sectionId, String english) {
        String key = "section." + (sectionId == null ? "" : sectionId.trim().toUpperCase(Locale.ENGLISH));
        if (Messages.isDefined(Messages.getLanguage(), key)) {
            return Messages.get(key);
        }
        return english;
    }

    /**
     * The kind of event, in the current language.
     *
     * @param name    the label as written in the data, which decides the key
     * @param upper   whether the caller wants it in capitals for a heading
     * @return the translated name, or the one given if it is not known
     */
    public static String eventType(String name, boolean upper) {
        String value = name == null ? "" : name.trim();
        String key = "eventType." + value.toUpperCase(Locale.ENGLISH);
        String wording = Messages.isDefined(Messages.getLanguage(), key)
                ? Messages.get(key) : value;
        return upper ? wording.toUpperCase(Locale.ENGLISH) : wording;
    }

    /** The kind of event, in the case the caller wants. */
    public static String eventType(String name) {
        return eventType(name, false);
    }

    /**
     * The kind of notice, in the current language.
     *
     * <p>Separate from the event kinds because the wording is not shared: an event
     * is a "Game", a notice about one is a "cancellation". Both come from data
     * classes and both have to follow the language.
     */
    public static String noticeType(String name, boolean upper) {
        String value = name == null ? "" : name.trim();
        String key = "noticeType." + value.toUpperCase(Locale.ENGLISH);
        String wording = Messages.isDefined(Messages.getLanguage(), key)
                ? Messages.get(key) : value;
        return upper ? wording.toUpperCase(Locale.ENGLISH) : wording;
    }

    /** The kind of notice, in the case the caller wants. */
    public static String noticeType(String name) {
        return noticeType(name, false);
    }

    /**
     * The shape of the ground, in the current language.
     *
     * <p>The venue writes the shape with the word "stadium" attached, which is
     * English left inside the data. Stripping that word here and building the
     * sentence from the table is what keeps "stadium" out of a Luganda screen.
     *
     * @param shape   the shape from the venue data, which decides the wording
     * @param english the label the venue writes, used only if the table has
     *                nothing for this shape
     * @return the shape on its own, in the current language
     */
    public static String shape(StadiumShape shape, String english) {
        String key = shape == StadiumShape.OVAL ? "shape.oval" : "shape.box";
        if (Messages.isDefined(Messages.getLanguage(), key)) {
            return Messages.get(key);
        }
        return english == null ? "" : english.replace(" stadium", "").trim();
    }

    /**
     * A venue written with the word for stadium in the current language.
     *
     * <p>Where the shape is shown beside the name, the two have to agree about
     * what the place is called, rather than the name being followed by an English
     * "stadium".
     */
    public static String stadiumWord() {
        return Messages.isDefined(Messages.getLanguage(), "shape.stadium")
                ? Messages.get("shape.stadium") : "stadium";
    }
}
