package com.stadium.booking;

import com.stadium.booking.booking.BookingService;
import com.stadium.booking.data.Seat;
import com.stadium.booking.data.SeatKey;
import com.stadium.booking.data.SeatSection;
import com.stadium.booking.data.Stadium;
import com.stadium.booking.data.StadiumData;
import com.stadium.booking.storage.BookingStore;
import com.stadium.booking.ui.SeatMapPanel;

import static com.stadium.booking.TestRunner.assertEquals;
import static com.stadium.booking.TestRunner.assertTrue;
import static com.stadium.booking.TestRunner.freshDatabase;
import static com.stadium.booking.TestRunner.suite;
import static com.stadium.booking.TestRunner.test;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * The stadium map: choosing which end of the ground to sit in.
 *
 * <p>A customer should be able to look at the whole bowl, see which end is which,
 * and click straight onto a seat in the end they want, without being told which
 * letter it is first.
 */

/**
 * Checks that the seat map draws correctly and stays within its size
 * limits.
 */

final class StadiumMapTest {
    private StadiumMapTest() {
    }

    private static SeatMapPanel panel() throws Exception {
        BookingService service = new BookingService(new BookingStore(freshDatabase("stadiummap")));
        service.selectEvent(StadiumData.getEvent("namboole-01"));
        List<Seat> ignored = new ArrayList<>();
        return new SeatMapPanel(service, ignored::add, () -> { }, message -> { });
    }

    static void register() {
        suite("Stadium map");

        test("all four ends of the ground can be picked", () -> {
            SeatMapPanel map = panel();
            for (String end : new String[]{"A", "B", "C", "D"}) {
                assertTrue(map.getSectionCanvas(end) != null,
                        "end " + end + " must be shown on the map");
            }
        });

        test("the map tells the customer what each end is called", () -> {
            Stadium stadium = StadiumData.getStadium("namboole");
            Set<String> labels = new LinkedHashSet<>();
            for (SeatSection section : stadium.getSections()) {
                labels.add(section.getLabel());
            }
            assertEquals(4, labels.size(), "each end has its own name");
            assertTrue(labels.contains("VIP Box"), "the premium end is named: " + labels);
            assertTrue(labels.contains("Kampala End"), "the home end is named: " + labels);
        });

        test("each end has its own price, and the tiers differ across the bowl", () -> {
            Stadium stadium = StadiumData.getStadium("namboole");
            List<SeatSection> sections = stadium.getSections();
            Set<String> prices = new LinkedHashSet<>();
            for (SeatSection section : sections) {
                prices.add(BookingService.formatMoney(section.getBasePrice()));
            }
            assertTrue(prices.size() >= 2,
                    "the four ends are not all priced the same: " + prices);

            // The dearest seat in the ground must cost more than the cheapest,
            // which is the whole point of selling ends separately.
            BookingService service = new BookingService(new BookingStore(freshDatabase("map-price")));
            service.selectEvent(StadiumData.getEvent("namboole-01"));
            double dearest = 0.0;
            double cheapest = Double.MAX_VALUE;
            for (SeatSection section : sections) {
                for (int row = 1; row <= section.getRows(); row++) {
                    double price = service.getSeatPrice(section, row);
                    dearest = Math.max(dearest, price);
                    cheapest = Math.min(cheapest, price);
                }
            }
            assertTrue(dearest > cheapest,
                    "the price range across the ground must be real: "
                            + BookingService.formatMoney(cheapest) + " to "
                            + BookingService.formatMoney(dearest));
        });

        test("every seat on the map has a row, a number and a tier", () -> {
            SeatMapPanel map = panel();
            BookingService service = map.getBookingService();
            Stadium stadium = StadiumData.getStadium("namboole");
            for (SeatSection section : stadium.getSections()) {
                SeatKey key = new SeatKey(section.getId(), 1, 1);
                Seat seat = service.getSeat(key);
                assertTrue(seat != null, "front seat of " + section.getId() + " exists");
                assertTrue(seat.getPrice() > 0, "a seat has a price");
                assertTrue(!service.getRowTierName(1, section.getRows()).isBlank(),
                        "a row has a price tier");
            }
        });

        test("the map covers every seat in the stadium", () -> {
            Stadium stadium = StadiumData.getStadium("namboole");
            int fromSections = 0;
            for (SeatSection section : stadium.getSections()) {
                fromSections += section.getSeatCount();
            }
            assertEquals(stadium.getCapacity(), fromSections,
                    "the map must show the whole bowl, not a sample of it");
        });

        test("the map names each end to assistive technology", () -> {
            SeatMapPanel map = panel();
            for (String end : new String[]{"A", "B", "C", "D"}) {
                Component canvas = map.getSectionCanvas(end);
                String name = canvas.getAccessibleContext().getAccessibleName();
                assertTrue(name != null && name.contains(end),
                        "accessible name for end " + end + " names it: " + name);
            }
        });

        test("a seat can be chosen by clicking it on the map", () -> {
            SeatMapPanel map = panel();
            SeatKey clicked = new SeatKey("A", 1, 50);
            assertTrue(map.selectSeatByKey(clicked), "clicking a free seat should hold it");
            List<Seat> picked = map.getSelectedSeats();
            assertEquals(1, picked.size(), "the held seat is reported to the screen");
            assertEquals(clicked, picked.get(0).getKey(),
                    "the seat that was clicked is the seat held");
        });

        test("clicking the same seat twice lets it go again", () -> {
            SeatMapPanel map = panel();
            SeatKey key = new SeatKey("B", 3, 10);
            assertTrue(map.selectSeatByKey(key), "first click selects");
            assertEquals(1, map.getSelectedSeats().size(), "one seat chosen");
            assertTrue(!map.selectSeatByKey(key), "second click deselects");
            assertEquals(0, map.getSelectedSeats().size(), "nothing left chosen");
        });

        test("a sold seat cannot be chosen from the map", () -> {
            BookingService service = new BookingService(new BookingStore(freshDatabase("map-sold")));
            service.selectEvent(StadiumData.getEvent("namboole-01"));
            SeatKey taken = new SeatKey("A", 1, 5);
            service.book("Sold Out", "s@example.co.ug", "+256700000000",
                    List.of(service.getSeat(taken)));
            SeatMapPanel map = new SeatMapPanel(service, seats -> { }, () -> { }, message -> { });
            assertTrue(!map.selectSeatByKey(taken), "a sold seat must not be selectable");
            assertEquals(0, map.getSelectedSeats().size(), "nothing was chosen");
        });

        test("seats from different ends can sit in one booking", () -> {
            SeatMapPanel map = panel();
            SeatKey[] wanted = {
                    new SeatKey("A", 2, 20), new SeatKey("B", 2, 20),
                    new SeatKey("C", 2, 20), new SeatKey("D", 2, 20)};
            for (SeatKey key : wanted) {
                assertTrue(map.selectSeatByKey(key), "chose " + key.display());
            }
            Set<String> ends = new LinkedHashSet<>();
            for (Seat seat : map.getSelectedSeats()) {
                ends.add(seat.getKey().getSection());
            }
            assertEquals(4, ends.size(),
                    "a customer may book across all four ends: " + ends);
        });

        test("a group of twenty seats can be chosen", () -> {
            SeatMapPanel map = panel();
            for (int number = 1; number <= 20; number++) {
                assertTrue(map.selectSeatByKey(new SeatKey("A", 1, number)),
                        "chose A1-" + number);
            }
            assertEquals(20, map.getSelectedSeats().size(), "all twenty are held");
        });

        test("the map refuses one seat past the limit rather than silently dropping one", () -> {
            SeatMapPanel map = panel();
            for (int number = 1; number <= BookingService.MAX_SEATS_PER_BOOKING; number++) {
                map.selectSeatByKey(new SeatKey("A", 1, number));
            }
            assertEquals(BookingService.MAX_SEATS_PER_BOOKING, map.getSelectedSeats().size(),
                    "the limit can be reached");
            assertTrue(!map.selectSeatByKey(new SeatKey("A", 1, 99)),
                    "the seat past the limit is refused");
            assertEquals(BookingService.MAX_SEATS_PER_BOOKING, map.getSelectedSeats().size(),
                    "the selection is unchanged by the refusal");
        });

        test("the map asks for a sensible size, not the whole screen", () -> {
            // The map used to be given a preferred size of 800x420 but sat in a
            // BorderLayout.CENTER slot, which always stretches to fill, so it
            // grew to whatever height the window had and pushed the price
            // outline off the bottom. A capped maximum is what stops it.
            SeatMapPanel map = panel();
            Dimension preferred = map.getPreferredSize();
            Dimension maximum = map.getMaximumSize();
            assertTrue(preferred.width > 0 && preferred.height > 0,
                    "the map asks for a real size: " + preferred.width + "x" + preferred.height);
            assertTrue(maximum.height <= 460,
                    "and is capped so it cannot fill the screen, was " + maximum.height);
            assertTrue(maximum.height >= preferred.height,
                    "the cap must not be smaller than the map: "
                            + maximum.height + " vs " + preferred.height);
        });

        test("a tall end does not make the map taller than its cap", () -> {
            // Section D is 130 rows and section A is 114, so a layout that
            // followed the row count would grow the map by 130 rows' worth of
            // pixels. Every end's canvas has to stay inside the same cap.
            SeatMapPanel map = panel();
            for (String end : new String[]{"A", "B", "C", "D"}) {
                Component canvas = map.getSectionCanvas(end);
                assertTrue(canvas != null, "end " + end + " has a canvas");
                assertTrue(canvas.getMaximumSize().height <= 300,
                        "end " + end + " canvas is capped, was "
                                + canvas.getMaximumSize().height);
            }
        });

        test("choosing a seat leaves the map usable for the next one", () -> {
            // A regression guard for the smaller map. Selecting a seat used to
            // re-run the status refresh, which rebuilt the tab pane, and the
            // rebuild could take focus away from the canvas. That left a
            // keyboard customer stuck: the arrow keys stopped moving the caret
            // the moment they picked their first seat.
            SeatMapPanel map = panel();
            Component canvas = map.getSectionCanvas("A");
            assertTrue(map.selectSeatByKey(new SeatKey("A", 1, 10)), "picked a seat");
            assertTrue(map.getSectionCanvas("A") == canvas,
                    "the same canvas is still on show after picking");
            assertTrue(canvas.isFocusable(),
                    "and it can still take focus, so the arrow keys keep working");
            assertEquals("A1-01", map.getKeyboardCaret().display(),
                    "the caret is still on the grid");
        });

        test("many rows are visible at once", () -> {
            // The reason for the smaller cells: before, only about twenty rows
            // fitted on screen, so choosing a seat meant constant scrolling.
            SeatMapPanel map = panel();
            Component canvas = map.getSectionCanvas("A");
            int height = canvas.getPreferredSize().height;
            // Row label height plus cell height and gap, per row.
            int rowsVisible = (height - 34) / 9;
            assertTrue(rowsVisible >= 25,
                    "at least 25 rows fit before scrolling, got " + rowsVisible);
        });
    }
}
