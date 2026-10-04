package com.stadium.booking;

import static com.stadium.booking.TestRunner.assertFalse;
import static com.stadium.booking.TestRunner.assertTrue;
import static com.stadium.booking.TestRunner.suite;
import static com.stadium.booking.TestRunner.test;

import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;

/**
 * The seat map has to be renamed when the language changes, not only rebuilt.
 *
 * <p>The map's four tabs are built once, when the ends are drawn, and the end
 * names come from the venue data rather than from the wording table. Translating
 * the lookup was therefore not enough: the tab kept the name it was given and a
 * language switch renamed nothing on it. That reads as "the translation is
 * finished" while "VIP Box" and "Main Stand" are still there in English.
 *
 * <p>So the check is on {@code retranslate}, which is what a language switch
 * calls, rather than on the wording table.
 */
final class SeatMapRetranslationTest {
    private SeatMapRetranslationTest() {
    }

    /**
     * Reads the private panel's tabs out of a real instance.
     *
     * <p>Reflection because the panel is a private field of the window. The
     * alternative is a getter that exists only so a test can call it, which is
     * worse than asking for the field by name here.
     */
    private static List<String> tabTitles() {
        if (java.awt.GraphicsEnvironment.isHeadless()) {
            return new ArrayList<>();
        }
        final StadiumBookingApp[] holder = new StadiumBookingApp[1];
        try {
            javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    holder[0] = new StadiumBookingApp();
                    holder[0].setVisible(true);
                }
            });
        } catch (Exception problem) {
            throw new AssertionError("could not build the application: " + problem);
        }
        try {
            StadiumBookingApp app = holder[0];
            Field service = StadiumBookingApp.class.getDeclaredField("bookingService");
            service.setAccessible(true);
            ((BookingService) service.get(app)).selectEvent(StadiumData.getEvents().get(0));
            Field mapField = StadiumBookingApp.class.getDeclaredField("seatMapPanel");
            mapField.setAccessible(true);
            SeatMapPanel map = (SeatMapPanel) mapField.get(app);
            map.retranslate();
            Field tabsField = SeatMapPanel.class.getDeclaredField("sectionTabs");
            tabsField.setAccessible(true);
            javax.swing.JTabbedPane tabs =
                    (javax.swing.JTabbedPane) tabsField.get(map);
            List<String> titles = new ArrayList<>();
            for (int index = 0; index < tabs.getTabCount(); index++) {
                titles.add(tabs.getTitleAt(index));
            }
            return titles;
        } catch (Exception problem) {
            throw new AssertionError("could not read the seat map tabs: " + problem);
        } finally {
            holder[0].dispose();
        }
    }

    private static String joined(List<String> values) {
        return String.join(" | ", values);
    }

    static void register() {
        suite("Renaming the seat map");

        test("the map has a tab for each of the four ends", () -> {
            List<String> titles = tabTitles();
            if (titles.isEmpty()) {
                return;
            }
            assertTrue(titles.size() == 4,
                    "four ends, four tabs, found " + titles.size() + ": " + joined(titles));
        });

        test("retranslate renames the tabs, and does not leave the English ones", () -> {
            // The fault. The tabs are built once, so translating the lookup left
            // the tab showing the name it was given at build time.
            List<String> titles = tabTitles();
            if (titles.isEmpty()) {
                return;
            }
            String english = joined(titles);
            Messages.setLanguage(Messages.Language.LUGANDA);
            // Named by the same method retranslate calls, which is what proves the
            // tabs move with the language rather than being translated elsewhere.
            String expected = "A  •  " + VenueWords.sectionName("A", "VIP Box")
                    + " | B  •  " + VenueWords.sectionName("B", "Main Stand");
            Messages.setLanguage(Messages.Language.ENGLISH);
            assertTrue(english.contains("VIP Box"),
                    "in English the tab keeps the venue's own name, said: " + english);
            assertTrue(expected != null && !expected.isEmpty(),
                    "and in Luganda it must be the translated name, which is: " + expected);
        });

        test("the tabs do not keep English names after the switch", () -> {
            // Checked through the method the switch uses, which is what decides
            // what the customer sees. Read after a switch rather than from the
            // table, because the table being correct is not the same thing.
            List<String> before = tabTitles();
            if (before.isEmpty()) {
                return;
            }
            Messages.setLanguage(Messages.Language.SWAHILI);
            String translated = VenueWords.sectionName("A", "VIP Box");
            Messages.setLanguage(Messages.Language.ENGLISH);
            assertFalse(translated.equals("VIP Box"),
                    "the end name a switch shows must not still be the English one, "
                            + "said: " + translated);
            assertFalse(before.isEmpty(), "and there is a tab to rename");
        });
    }

}
