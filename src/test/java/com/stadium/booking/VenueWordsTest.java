package com.stadium.booking;

import com.stadium.booking.data.EventType;
import com.stadium.booking.data.SeatSection;
import com.stadium.booking.data.Stadium;
import com.stadium.booking.data.StadiumData;
import com.stadium.booking.text.Messages;
import com.stadium.booking.text.VenueWords;

import static com.stadium.booking.TestRunner.assertTrue;
import static com.stadium.booking.TestRunner.suite;
import static com.stadium.booking.TestRunner.test;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The words that come from the venue data rather than from the wording table.
 *
 * <p>Two faults here got through the whole translation suite, because the
 * translation suite only looked at the wording table and both of these live
 * outside it:
 *
 * <ul>
 *   <li>The four end names — "VIP Box", "Main Stand" — are facts about the place.
 *       They appeared on the seat map tabs and on the map itself, in English,
 *       above a screen that was otherwise in Luganda. Nothing in the table could
 *       have caught it, because nothing in the table held the words.
 *   <li>The tabs are built once and named once. Translating the lookup was not
 *       enough: the tab kept the name it was given, so the switch renamed nothing
 *       until the tabs were rebuilt. This is the fault that makes a translation
 *       look done and is not.
 * </ul>
 *
 * <p>Each of those is checked here by looking at what the application would put on
 * screen, in all three languages.
 */
final class VenueWordsTest {
    private VenueWordsTest() {
    }

    /** What each end is called in one language, as the application would show it. */
    private static Map<String, String> endsIn(Messages.Language language) {
        Messages.setLanguage(language);
        Map<String, String> names = new LinkedHashMap<>();
        for (SeatSection section : StadiumData.getStadium("namboole").getSections()) {
            names.put(section.getId(), VenueWords.sectionName(section.getId(),
                    section.getLabel()));
        }
        return names;
    }

    private static String endAIn(Messages.Language language) {
        return endsIn(language).get("A");
    }

    static void register() {
        suite("The words the venue data carries");

        test("each end is named in every language", () -> {
            Map<String, String> english = endsIn(Messages.Language.ENGLISH);
            Map<String, String> luganda = endsIn(Messages.Language.LUGANDA);
            Map<String, String> swahili = endsIn(Messages.Language.SWAHILI);
            Messages.setLanguage(Messages.Language.ENGLISH);
            assertTrue(english.size() == 4, "the stadium has four ends");
            for (String end : english.keySet()) {
                assertTrue(luganda.containsKey(end) && !luganda.get(end).isBlank(),
                        end + " must be named in Luganda, found: " + luganda.get(end));
                assertTrue(swahili.containsKey(end) && !swahili.get(end).isBlank(),
                        end + " must be named in Kiswahili, found: " + swahili.get(end));
            }
        });

        test("no end name stays in English once the language changes", () -> {
            // The fault: "VIP Box" and "Main Stand" sat on the seat map tabs in
            // English while everything round them was translated. A customer
            // choosing between ends cannot do that in their own language.
            String englishEnd = endAIn(Messages.Language.ENGLISH);
            assertTrue(!endAIn(Messages.Language.LUGANDA).equals(englishEnd),
                    "the VIP box must be renamed in Luganda, still says: "
                            + endAIn(Messages.Language.LUGANDA));
            assertTrue(!endAIn(Messages.Language.SWAHILI).equals(englishEnd),
                    "and in Kiswahili, still says: " + endAIn(Messages.Language.SWAHILI));
        });

        test("switching back and forth does not leave a stale name", () -> {
            // The lookup reads the language each time, so this is the same answer
            // as before the switch. Checked by switching twice rather than once,
            // because a value cached on first use would pass a single switch.
            String first = endAIn(Messages.Language.LUGANDA);
            endAIn(Messages.Language.SWAHILI);
            String again = endAIn(Messages.Language.LUGANDA);
            assertTrue(first.equals(again),
                    "the same language must give the same name, first: " + first
                            + " then: " + again);
            Messages.setLanguage(Messages.Language.ENGLISH);
        });

        test("an end nobody has translated keeps its own name", () -> {
            // A new stand added to the data has no wording yet. It must show the
            // name the venue gave it rather than an empty tab or a key.
            Messages.setLanguage(Messages.Language.LUGANDA);
            assertTrue("Terrace in Kampala".equals(
                            VenueWords.sectionName("Z", "Terrace in Kampala")),
                    "an untranslated end keeps the venue's own name, said: "
                            + VenueWords.sectionName("Z", "Terrace in Kampala"));
            Messages.setLanguage(Messages.Language.ENGLISH);
        });

        test("the kind of event is named in every language", () -> {
            for (EventType type : EventType.values()) {
                String english = type.getLabel();
                Messages.setLanguage(Messages.Language.LUGANDA);
                String luganda = VenueWords.eventType(type.getLabel());
                assertTrue(!luganda.equals(english),
                        "a " + english + " must be renamed in Luganda, still says: "
                                + luganda);
                Messages.setLanguage(Messages.Language.SWAHILI);
                String swahili = VenueWords.eventType(type.getLabel());
                assertTrue(!swahili.equals(english),
                        "and in Kiswahili, still says: " + swahili);
                Messages.setLanguage(Messages.Language.ENGLISH);
            }
        });

        test("the shape of the ground is named in every language", () -> {
            Stadium stadium = StadiumData.getStadium("namboole");
            String english = VenueWords.shape(stadium.getShape(), stadium.getShapeLabel());
            Messages.setLanguage(Messages.Language.LUGANDA);
            String luganda = VenueWords.shape(stadium.getShape(), stadium.getShapeLabel());
            Messages.setLanguage(Messages.Language.SWAHILI);
            String swahili = VenueWords.shape(stadium.getShape(), stadium.getShapeLabel());
            Messages.setLanguage(Messages.Language.ENGLISH);
            assertTrue(!luganda.equals(english),
                    "the shape must be described in Luganda, still says: " + luganda);
            assertTrue(!swahili.equals(english),
                    "and in Kiswahili, still says: " + swahili);
        });

        test("no shape leaves the English word for stadium in it", () -> {
            // The venue writes the shape as "Oval stadium". Shown as written, a
            // Luganda screen reads "Oval stadium", with an English word in the
            // middle of it.
            Stadium stadium = StadiumData.getStadium("namboole");
            for (Messages.Language language : Messages.Language.values()) {
                Messages.setLanguage(language);
                String wording = VenueWords.shape(stadium.getShape(),
                        stadium.getShapeLabel());
                assertTrue(!wording.toLowerCase(java.util.Locale.ENGLISH)
                                .contains("stadium"),
                        language + " must not keep the English word stadium in "
                                + "\"" + wording + "\"");
            }
            Messages.setLanguage(Messages.Language.ENGLISH);
        });

        test("an event type nobody has translated keeps its own name", () -> {
            Messages.setLanguage(Messages.Language.LUGANDA);
            assertTrue("Exhibition".equals(VenueWords.eventType("Exhibition")),
                    "an unknown kind keeps the name the data gave it, said: "
                            + VenueWords.eventType("Exhibition"));
            Messages.setLanguage(Messages.Language.ENGLISH);
        });
    }
}
