package com.stadium.booking;

import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

/**
 * Checks the venue-details and event-details screens by driving the app's own
 * navigation methods and inspecting the component tree that results.
 *
 * <p>Clicking through the interface cannot verify this reliably: a synthesised
 * click on a venue card opens the details page whether it landed on the card or
 * missed the button, so a screenshot cannot prove which screen is on show.
 * Calling the same methods the buttons call removes that doubt.
 */
public final class DetailsFlowTest {

    private DetailsFlowTest() {
    }

    public static void register() {
        TestRunner.suite("Details and booking flow");

        // One app for the whole group. Building it touches the database, so it is
        // done once and shared, and every navigation call has to happen on the
        // event thread.
        StadiumBookingApp[] app = new StadiumBookingApp[1];
        TestRunner.test("the app can be built for screen inspection", () -> {
            if (GraphicsEnvironment.isHeadless()) {
                return;
            }
            app[0] = build();
        });

        TestRunner.test("the venue page drops the plan and offers Choose seats", () -> {
            StadiumBookingApp instance = require(app);
            Stadium stadium = StadiumData.getStadium("namboole");
            TestRunner.assertTrue(stadium != null, "the reference venue exists");
            run(instance, () -> showStadiumDetails(instance, stadium));
            List<Component> tree = flatten(contentOf(instance));
            TestRunner.assertEquals(0, countOfType(tree, StadiumPhotoPanel.class),
                    "the venue page no longer shows the drawn plan");
            TestRunner.assertTrue(buttonLabelled(tree, "Choose seats") != null,
                    "the venue page offers a Choose seats button");
        });

        TestRunner.test("the event page replaces the plan with the seat picker", () -> {
            StadiumBookingApp instance = require(app);
            Stadium stadium = StadiumData.getStadium("namboole");
            StadiumEvent event = firstOpenEvent(instance, stadium);
            TestRunner.assertTrue(event != null, "the venue has a bookable event");
            run(instance, () -> showEventDetails(instance, event));
            List<Component> tree = flatten(contentOf(instance));
            TestRunner.assertEquals(0, countOfType(tree, StadiumPhotoPanel.class),
                    "the event page no longer shows the drawn plan");
            TestRunner.assertEquals(1, countOfType(tree, SeatMapPanel.class),
                    "the event page carries the seat picker");
        });

        TestRunner.test("no confirm button until a seat is chosen", () -> {
            StadiumBookingApp instance = require(app);
            StadiumEvent event = firstOpenEvent(instance, StadiumData.getStadium("namboole"));
            run(instance, () -> showEventDetails(instance, event));
            JButton book = findConfirmButton(flatten(contentOf(instance)));
            TestRunner.assertTrue(book == null,
                    "the event page offers no confirm button with nothing chosen");
            JLabel hint = labelContaining(flatten(contentOf(instance)),
                    "Choose one or more seats, then confirm here");
            TestRunner.assertTrue(hint != null,
                    "and says plainly that a seat has to be chosen first");
        });

        TestRunner.test("choosing a seat puts a booking button on the event page", () -> {
            StadiumBookingApp instance = require(app);
            StadiumEvent event = firstOpenEvent(instance, StadiumData.getStadium("namboole"));
            run(instance, () -> showEventDetails(instance, event));
            SeatMapPanel picker = findIn(contentOf(instance), SeatMapPanel.class);
            TestRunner.assertTrue(picker != null, "the picker is on the event page");
            SeatKey seat = firstSelectableKey(instance);
            TestRunner.assertTrue(seat != null, "there is a seat that can be chosen");
            int restored = picker.setSelectedKeys(new ArrayList<>(List.of(seat)));
            TestRunner.assertEquals(1, restored, "a vacant seat can be chosen");
            run(instance, () -> updateBookingSummary(instance));
            JButton book = findConfirmButton(flatten(contentOf(instance)));
            TestRunner.assertTrue(book != null,
                    "choosing a seat puts a confirm button on the page");
            TestRunner.assertTrue(book.isEnabled(), "and it is ready to press");
            TestRunner.assertEquals("Confirm 1 seat", book.getText(),
                    "it says how many seats it will book");
        });

        TestRunner.test("the booking button counts the seats chosen", () -> {
            StadiumBookingApp instance = require(app);
            StadiumEvent event = firstOpenEvent(instance, StadiumData.getStadium("namboole"));
            run(instance, () -> showEventDetails(instance, event));
            SeatMapPanel picker = findIn(contentOf(instance), SeatMapPanel.class);
            List<SeatKey> seats = new ArrayList<>();
            for (Seat seat : bookingService(instance).getSeats()) {
                if (seats.size() >= 3) {
                    break;
                }
                if (bookingService(instance).isSeatSelectable(seat.getKey())) {
                    seats.add(seat.getKey());
                }
            }
            picker.setSelectedKeys(new ArrayList<>(seats));
            run(instance, () -> updateBookingSummary(instance));
            JButton book = findConfirmButton(flatten(contentOf(instance)));
            TestRunner.assertTrue(book != null, "the confirm button is there");
            TestRunner.assertEquals("Confirm 3 seats", book.getText(),
                    "and counts the three seats chosen, said: " + book.getText());
        });

        TestRunner.test("a single seat is counted in the singular", () -> {
            StadiumBookingApp instance = require(app);
            StadiumEvent event = firstOpenEvent(instance, StadiumData.getStadium("namboole"));
            run(instance, () -> showEventDetails(instance, event));
            SeatMapPanel picker = findIn(contentOf(instance), SeatMapPanel.class);
            picker.setSelectedKeys(new ArrayList<>(List.of(firstSelectableKey(instance))));
            run(instance, () -> updateBookingSummary(instance));
            // "Book 1 seats" on the control that charges the customer would be a
            // real slip, so the singular is asserted rather than assumed.
            JButton book = findConfirmButton(flatten(contentOf(instance)));
            TestRunner.assertEquals("Confirm 1 seat", book.getText(),
                    "one seat is not pluralised, said: " + book.getText());
            run(instance, () -> openEvent(instance, event, true));
            JButton confirm = buttonLabelled(flatten(contentOf(instance)), "Confirm 1 seat");
            TestRunner.assertTrue(confirm != null,
                    "and the confirm button on the review step agrees");
        });

        TestRunner.test("clearing the seats takes the booking button away", () -> {
            StadiumBookingApp instance = require(app);
            StadiumEvent event = firstOpenEvent(instance, StadiumData.getStadium("namboole"));
            run(instance, () -> showEventDetails(instance, event));
            SeatMapPanel picker = findIn(contentOf(instance), SeatMapPanel.class);
            picker.setSelectedKeys(new ArrayList<>(List.of(firstSelectableKey(instance))));
            run(instance, () -> updateBookingSummary(instance));
            TestRunner.assertTrue(findConfirmButton(flatten(contentOf(instance))) != null,
                    "the button is there with a seat chosen");
            picker.clearSelection();
            run(instance, () -> updateBookingSummary(instance));
            TestRunner.assertTrue(findConfirmButton(flatten(contentOf(instance))) == null,
                    "and gone once the seats are cleared");
        });

        TestRunner.test("the review step keeps the seats chosen on the event page", () -> {
            StadiumBookingApp instance = require(app);
            StadiumEvent event = firstOpenEvent(instance, StadiumData.getStadium("namboole"));
            run(instance, () -> showEventDetails(instance, event));
            SeatMapPanel picker = findIn(contentOf(instance), SeatMapPanel.class);
            SeatKey seat = firstSelectableKey(instance);
            TestRunner.assertTrue(picker != null && seat != null, "a seat can be chosen");
            int chosen = picker.setSelectedKeys(new ArrayList<>(List.of(seat)));
            run(instance, () -> openEvent(instance, event, true));
            List<Component> tree = flatten(contentOf(instance));
            TestRunner.assertEquals(1, countOfType(tree, SeatMapPanel.class),
                    "the booking screen still carries the picker");
            TestRunner.assertEquals(chosen, picker.getSelectedSeats().size(),
                    "the review step keeps the chosen seats");
            JButton confirm = findConfirmButton(tree);
            TestRunner.assertTrue(confirm != null && confirm.isEnabled(),
                    "the confirm button is ready once seats are carried over");
        });

        TestRunner.test("the confirm button counts the seats on the review step", () -> {
            StadiumBookingApp instance = require(app);
            StadiumEvent event = firstOpenEvent(instance, StadiumData.getStadium("namboole"));
            run(instance, () -> showEventDetails(instance, event));
            SeatMapPanel picker = findIn(contentOf(instance), SeatMapPanel.class);
            List<SeatKey> seats = new ArrayList<>();
            for (Seat seat : bookingService(instance).getSeats()) {
                if (seats.size() >= 2) {
                    break;
                }
                if (bookingService(instance).isSeatSelectable(seat.getKey())) {
                    seats.add(seat.getKey());
                }
            }
            picker.setSelectedKeys(new ArrayList<>(seats));
            run(instance, () -> openEvent(instance, event, true));
            JButton confirm = findConfirmButton(flatten(contentOf(instance)));
            TestRunner.assertTrue(confirm != null, "the confirm button is on show");
            TestRunner.assertEquals("Confirm 2 seats", confirm.getText(),
                    "and says how many seats it will book, said: " + confirm.getText());
        });

        TestRunner.test("Back from the review step returns to the seat picker", () -> {
            StadiumBookingApp instance = require(app);
            StadiumEvent event = firstOpenEvent(instance, StadiumData.getStadium("namboole"));
            run(instance, () -> showEventDetails(instance, event));
            SeatMapPanel picker = findIn(contentOf(instance), SeatMapPanel.class);
            SeatKey seat = firstSelectableKey(instance);
            int chosen = picker.setSelectedKeys(new ArrayList<>(List.of(seat)));
            run(instance, () -> openEvent(instance, event, true));
            run(instance, () -> goBack(instance));
            TestRunner.assertEquals("event-details", screenOf(instance),
                    "Back returns to the event page");
            TestRunner.assertEquals(1, countOfType(flatten(contentOf(instance)), SeatMapPanel.class),
                    "the picker is still on show after coming back");
            TestRunner.assertEquals(chosen, picker.getSelectedSeats().size(),
                    "the seats are still chosen after coming back");
        });

        TestRunner.test("a fresh booking does not inherit the last selection", () -> {
            StadiumBookingApp instance = require(app);
            StadiumEvent event = firstOpenEvent(instance, StadiumData.getStadium("namboole"));
            run(instance, () -> showEventDetails(instance, event));
            SeatMapPanel picker = findIn(contentOf(instance), SeatMapPanel.class);
            picker.setSelectedKeys(new ArrayList<>(List.of(firstSelectableKey(instance))));
            run(instance, () -> openEvent(instance, event, false));
            TestRunner.assertTrue(picker.getSelectedSeats().isEmpty(),
                    "opening an event afresh clears any earlier selection");
        });

        TestRunner.test("the event page quotes the total beside the booking button", () -> {
            StadiumBookingApp instance = require(app);
            StadiumEvent event = firstOpenEvent(instance, StadiumData.getStadium("namboole"));
            run(instance, () -> showEventDetails(instance, event));
            SeatMapPanel picker = findIn(contentOf(instance), SeatMapPanel.class);
            List<SeatKey> seats = new ArrayList<>(List.of(firstSelectableKey(instance)));
            picker.setSelectedKeys(new ArrayList<>(seats));
            run(instance, () -> updateBookingSummary(instance));
            String total = currency(bookingService(instance).getTotalCharge(
                    bookingService(instance).getSeats().stream()
                            .filter(seat -> seats.contains(seat.getKey()))
                            .collect(java.util.stream.Collectors.toList())));
            JLabel quoted = labelContaining(flatten(contentOf(instance)), total);
            TestRunner.assertTrue(quoted != null,
                    "the amount the button will charge is on show, looked for " + total);
        });

        TestRunner.test("the confirm button sits at the top of the event page", () -> {
            StadiumBookingApp instance = require(app);
            StadiumEvent event = firstOpenEvent(instance, StadiumData.getStadium("namboole"));
            run(instance, () -> showEventDetails(instance, event));
            java.awt.Component host = namedComponent(instance, "eventActionsHost");
            TestRunner.assertTrue(host != null, "the confirm row is on the event page");
            java.awt.Container parent = host.getParent();
            TestRunner.assertTrue(parent instanceof javax.swing.JPanel,
                    "and is attached to a panel");
            // It used to be the page's SOUTH component, so the seat map and the
            // notices panel pushed it off the bottom of the window: present in
            // the layout, invisible to the customer. NORTH is what guarantees it
            // is on show whatever height the rest of the page needs.
            TestRunner.assertEquals(java.awt.BorderLayout.NORTH,
                    constraintOf(parent, host),
                    "the confirm row is placed along the north edge, not the south");
        });

        TestRunner.test("every screen builds without a sign-in", () -> {
            StadiumBookingApp instance = require(app);
            // These used to sit behind a staff PIN. Each is opened directly and
            // the resulting screen is inspected, so a gate that crept back in,
            // or a screen that throws while building, both fail here.
            run(instance, () -> showBookings(instance));
            TestRunner.assertEquals("bookings", screenOf(instance),
                    "My bookings opens without signing in");
            TestRunner.assertTrue(contentOf(instance) != null,
                    "and draws its booking history");

            run(instance, () -> showOccupancyReport(instance));
            TestRunner.assertEquals("occupancy", screenOf(instance),
                    "the occupancy report opens without signing in");
            TestRunner.assertTrue(contentOf(instance) != null,
                    "and draws its table");

            run(instance, () -> showSeatLedger(instance));
            TestRunner.assertEquals("seats", screenOf(instance),
                    "the booked seats list opens without signing in");
            TestRunner.assertTrue(contentOf(instance) != null,
                    "and draws its ledger");
        });

        TestRunner.test("the occupancy table has a row for every event", () -> {
            StadiumBookingApp instance = require(app);
            run(instance, () -> showOccupancyReport(instance));
            List<JTable> tables = tablesIn(contentOf(instance));
            TestRunner.assertEquals(1, tables.size(), "the occupancy screen has one table");
            JTable table = tables.get(0);
            // One row per event, plus the total.
            TestRunner.assertEquals(StadiumData.getEvents().size() + 1,
                    table.getRowCount(), "a row per event and a total");
            String header = table.getColumnName(0);
            TestRunner.assertTrue(header.contains("Event"),
                    "the first column names the event, not the venue: " + header);
        });

        TestRunner.test("the available-seat figures change as seats are chosen", () -> {
            StadiumBookingApp instance = require(app);
            StadiumEvent event = firstOpenEvent(instance, StadiumData.getStadium("namboole"));
            run(instance, () -> showEventDetails(instance, event));
            SeatMapPanel picker = findIn(contentOf(instance), SeatMapPanel.class);
            // The group shares one app, so an earlier test's seats are still
            // held. Start from nothing or the figure never appears to move.
            picker.clearSelection();
            run(instance, () -> updateBookingSummary(instance));

            List<JLabel> availability = labelsWithText(contentOf(instance), "seats still available");
            TestRunner.assertEquals(1, availability.size(),
                    "the event page shows one live availability figure");
            String before = availability.get(0).getText();

            picker.setSelectedKeys(new ArrayList<>(List.of(firstSelectableKey(instance))));
            run(instance, () -> updateBookingSummary(instance));
            String afterOne = availability.get(0).getText();
            TestRunner.assertFalse(before.equals(afterOne),
                    "choosing one seat changes the availability figure");

            // A second seat must move it again, or it is only reacting to being
            // touched rather than to how many seats are held.
            List<SeatKey> more = new ArrayList<>();
            int taken = 0;
            for (Seat seat : bookingService(instance).getSeats()) {
                if (taken >= 2) {
                    break;
                }
                if (bookingService(instance).isSeatSelectable(seat.getKey())) {
                    more.add(seat.getKey());
                    taken++;
                }
            }
            picker.setSelectedKeys(more);
            run(instance, () -> updateBookingSummary(instance));
            String afterTwo = availability.get(0).getText();
            TestRunner.assertFalse(afterOne.equals(afterTwo),
                    "a second seat changes the figure again");
        });

        TestRunner.test("the selection line reports the seats and their total", () -> {
            StadiumBookingApp instance = require(app);
            StadiumEvent event = firstOpenEvent(instance, StadiumData.getStadium("namboole"));
            run(instance, () -> showEventDetails(instance, event));
            findIn(contentOf(instance), SeatMapPanel.class).clearSelection();
            run(instance, () -> updateBookingSummary(instance));
            List<JLabel> selection = labelsWithText(contentOf(instance), "No seats chosen yet");
            TestRunner.assertEquals(1, selection.size(),
                    "the event page says plainly when nothing is chosen");

            SeatMapPanel picker = findIn(contentOf(instance), SeatMapPanel.class);
            SeatKey seat = firstSelectableKey(instance);
            picker.setSelectedKeys(new ArrayList<>(List.of(seat)));
            run(instance, () -> updateBookingSummary(instance));
            List<JLabel> updated = labelsWithText(contentOf(instance), "seat chosen");
            TestRunner.assertEquals(1, updated.size(),
                    "choosing a seat replaces the empty message");
            TestRunner.assertTrue(updated.get(0).getText().contains("UGX"),
                    "the selection line shows what the seats cost");
        });

        TestRunner.test("the picker survives a theme change on the event page", () -> {
            StadiumBookingApp instance = require(app);
            StadiumEvent event = firstOpenEvent(instance, StadiumData.getStadium("namboole"));
            run(instance, () -> showEventDetails(instance, event));
            // The picker is one live component shared by two screens, so a rebuild
            // has to re-attach it rather than leave it on the screen just closed.
            run(instance, () -> rebuildCurrentScreen(instance));
            TestRunner.assertEquals(1,
                    countOfType(flatten(contentOf(instance)), SeatMapPanel.class),
                    "exactly one picker is attached after a theme change");
        });
    }

    private static StadiumBookingApp require(StadiumBookingApp[] app) {
        if (app[0] == null) {
            throw new AssertionError("no app was built, so the screen cannot be inspected");
        }
        return app[0];
    }

    private static StadiumBookingApp build() throws Exception {
        final StadiumBookingApp[] built = new StadiumBookingApp[1];
        SwingUtilities.invokeAndWait(() -> built[0] = new StadiumBookingApp());
        return built[0];
    }

    /** Navigation touches Swing, so it is marshalled onto the event thread. */
    private static void run(StadiumBookingApp app, Body body) throws Exception {
        final Exception[] failure = new Exception[1];
        SwingUtilities.invokeAndWait(() -> {
            try {
                body.run();
            } catch (Exception error) {
                failure[0] = error;
            }
        });
        if (failure[0] != null) {
            throw failure[0];
        }
    }

    private interface Body {
        void run() throws Exception;
    }

    // ------------------------------------------------------------------
    // Driving the app the way its buttons do
    // ------------------------------------------------------------------

    private static void showStadiumDetails(StadiumBookingApp app, Stadium stadium)
            throws Exception {
        invoke(app, "showStadiumDetails", new Class<?>[]{Stadium.class}, stadium);
    }

    private static void showEventDetails(StadiumBookingApp app, StadiumEvent event)
            throws Exception {
        invoke(app, "showEventDetails", new Class<?>[]{StadiumEvent.class}, event);
    }

    private static void openEvent(StadiumBookingApp app, StadiumEvent event, boolean keep)
            throws Exception {
        invoke(app, "openEvent", new Class<?>[]{StadiumEvent.class, boolean.class},
                event, keep);
    }

    private static void showBookings(StadiumBookingApp app) throws Exception {
        invoke(app, "showBookings", new Class<?>[0]);
    }

    private static void showOccupancyReport(StadiumBookingApp app) throws Exception {
        invoke(app, "showOccupancyReport", new Class<?>[0]);
    }

    private static void showSeatLedger(StadiumBookingApp app) throws Exception {
        invoke(app, "showSeatLedger", new Class<?>[0]);
    }

    private static void goBack(StadiumBookingApp app) throws Exception {
        invoke(app, "goBack", new Class<?>[0]);
    }

    private static void updateBookingSummary(StadiumBookingApp app) throws Exception {
        invoke(app, "updateBookingSummary", new Class<?>[0]);
    }

    private static void rebuildCurrentScreen(StadiumBookingApp app) throws Exception {
        invoke(app, "rebuildCurrentScreen", new Class<?>[0]);
    }

    private static StadiumEvent firstOpenEvent(StadiumBookingApp app, Stadium stadium)
            throws Exception {
        BookingService service = bookingService(app);
        for (StadiumEvent event : service.getEvents(stadium.getId())) {
            if (service.isBookingOpen(event)) {
                return event;
            }
        }
        return null;
    }

    private static SeatKey firstSelectableKey(StadiumBookingApp app) throws Exception {
        BookingService service = bookingService(app);
        for (Seat seat : service.getSeats()) {
            if (service.isSeatSelectable(seat.getKey())) {
                return seat.getKey();
            }
        }
        return null;
    }

    private static BookingService bookingService(StadiumBookingApp app) throws Exception {
        return (BookingService) field(app, "bookingService");
    }

    /** Every label on the page whose text contains the given phrase. */
    private static List<JLabel> labelsWithText(Container root, String phrase) {
        List<JLabel> found = new ArrayList<>();
        for (Component component : flatten(root)) {
            if (component instanceof JLabel) {
                String text = ((JLabel) component).getText();
                if (text != null && text.contains(phrase)) {
                    found.add((JLabel) component);
                }
            }
        }
        return found;
    }

    // ------------------------------------------------------------------
    // Inspecting the tree
    // ------------------------------------------------------------------

    private static Container contentOf(StadiumBookingApp app) throws Exception {
        return (Container) field(app, "contentHost");
    }

    private static String screenOf(StadiumBookingApp app) throws Exception {
        return (String) field(app, "currentScreen");
    }

    private static List<Component> flatten(Container root) {
        List<Component> all = new ArrayList<>();
        collect(root, all);
        return all;
    }

    /** A field of the app, so a test can inspect how a panel is laid out. */
    private static Component namedComponent(StadiumBookingApp app, String name)
            throws Exception {
        java.lang.reflect.Field field = StadiumBookingApp.class.getDeclaredField(name);
        field.setAccessible(true);
        return (Component) field.get(app);
    }

    /** Which BorderLayout edge a child sits against. */
    private static String constraintOf(Container parent, Component child) {
        java.awt.BorderLayout layout = (java.awt.BorderLayout) parent.getLayout();
        Object edge = layout.getConstraints(child);
        return edge == null ? "none" : edge.toString();
    }

    /**
     * The confirm button, labelled by the number of seats it will book rather
     * than carrying a fixed name.
     */
    private static JButton findConfirmButton(List<Component> tree) {
        for (Component component : tree) {
            if (!(component instanceof JButton button)) {
                continue;
            }
            String text = button.getText();
            if (text != null && text.startsWith("Confirm ")
                    && (text.endsWith("seat") || text.endsWith("seats"))) {
                return button;
            }
        }
        return null;
    }

    private static JLabel labelContaining(List<Component> tree, String fragment) {
        for (Component component : tree) {
            if (component instanceof JLabel label) {
                String text = label.getText();
                if (text != null && text.contains(fragment)) {
                    return label;
                }
            }
        }
        return null;
    }

    private static JLabel labelWithText(List<Component> tree, String text) {
        for (Component component : tree) {
            if (component instanceof JLabel label && text.equals(label.getText())) {
                return label;
            }
        }
        return null;
    }

    private static String currency(double amount) {
        return BookingService.formatMoney(amount);
    }

    /** Every table in a component tree, for inspecting a report's columns. */
    private static List<JTable> tablesIn(Container root) {
        List<JTable> tables = new ArrayList<>();
        for (Component component : flatten(root)) {
            if (component instanceof JTable table) {
                tables.add(table);
            }
        }
        return tables;
    }

    private static void collect(Component component, List<Component> into) {
        into.add(component);
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                collect(child, into);
            }
        }
    }

    private static int countOfType(List<Component> tree, Class<?> type) {
        int found = 0;
        for (Component component : tree) {
            if (type.isInstance(component)) {
                found++;
            }
        }
        return found;
    }

    private static <T> T findIn(Container root, Class<T> type) {
        for (Component component : flatten(root)) {
            if (type.isInstance(component)) {
                return type.cast(component);
            }
        }
        return null;
    }

    /** Finds a button by its visible label, ignoring case and stray spaces. */
    private static JButton buttonLabelled(List<Component> tree, String label) {
        for (Component component : tree) {
            if (component instanceof JButton) {
                String text = ((JButton) component).getText();
                if (text != null && text.trim().equalsIgnoreCase(label)) {
                    return (JButton) component;
                }
            }
        }
        return null;
    }

    // ------------------------------------------------------------------
    // Reflection helpers
    // ------------------------------------------------------------------

    private static void invoke(Object target, String name, Class<?>[] types, Object... args)
            throws Exception {
        Method method = findMethod(target.getClass(), name, types);
        method.setAccessible(true);
        try {
            method.invoke(target, args);
        } catch (java.lang.reflect.InvocationTargetException error) {
            throw new IllegalStateException(name + " failed: " + error.getCause(), error.getCause());
        }
    }

    private static Method findMethod(Class<?> type, String name, Class<?>[] types)
            throws NoSuchMethodException {
        Class<?> walk = type;
        while (walk != null) {
            try {
                return walk.getDeclaredMethod(name, types);
            } catch (NoSuchMethodException ignored) {
                walk = walk.getSuperclass();
            }
        }
        throw new NoSuchMethodException(name + " not found on " + type.getName());
    }

    private static Object field(Object target, String name) throws Exception {
        Class<?> walk = target.getClass();
        while (walk != null) {
            try {
                Field found = walk.getDeclaredField(name);
                found.setAccessible(true);
                return found.get(target);
            } catch (NoSuchFieldException ignored) {
                walk = walk.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name + " not found on " + target.getClass().getName());
    }
}
