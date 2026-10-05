package com.stadium.booking;

import com.stadium.booking.booking.BookingService;
import com.stadium.booking.data.Seat;
import com.stadium.booking.data.Stadium;
import com.stadium.booking.data.StadiumData;
import com.stadium.booking.data.StadiumEvent;
import com.stadium.booking.ui.SeatMapPanel;
import com.stadium.booking.ui.StadiumBookingApp;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

/**
 * Renders the changed screens to PNG files so the layout can be looked at.
 * Run by hand when the layout needs checking; not part of the test suite.
 *
 * <p>Usage: java -cp "build/test-classes:lib/*" com.stadium.booking.ScreenShots [outDir]
 */
public final class ScreenShots {

    private ScreenShots() {
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.ENGLISH);
        File outDir = new File(args.length > 0 ? args[0] : "build/shots");
        outDir.mkdirs();
        // capture() already runs on the event thread, so it must not be wrapped in
        // another invokeAndWait: that call is illegal from the dispatcher.
        capture(outDir);
    }

    private static void capture(File outDir) throws Exception {
        StadiumBookingApp app = new StadiumBookingApp();
        // The window is never shown, so it needs a size and a native peer before
        // its content will lay out at a realistic width.
        app.setSize(1400, 940);
        app.addNotify();
        app.setVisible(true);

        Stadium stadium = StadiumData.getStadium("namboole");
        invoke(app, "showStadiumDetails", new Class<?>[]{Stadium.class}, stadium);
        shoot(app, outDir, "venue-details");

        BookingService service = (BookingService) field(app, "bookingService");
        StadiumEvent event = null;
        for (StadiumEvent candidate : service.getEvents(stadium.getId())) {
            if (service.isBookingOpen(candidate)) {
                event = candidate;
                break;
            }
        }
        if (event == null) {
            System.out.println("no bookable event, so the event page was not captured");
            app.dispose();
            return;
        }

        invoke(app, "showEventDetails", new Class<?>[]{StadiumEvent.class}, event);
        shoot(app, outDir, "event-details-empty");

        // With one seat chosen, so the review button shows its enabled state.
        SeatMapPanel picker = find(app, SeatMapPanel.class);
        for (Seat seat : service.getSeats()) {
            if (service.isSeatSelectable(seat.getKey())) {
                picker.setSelectedKeys(List.of(seat.getKey()));
                break;
            }
        }
        invoke(app, "updateBookingSummary", new Class<?>[0]);
        shoot(app, outDir, "event-details-seat-picked");

        invoke(app, "openEvent", new Class<?>[]{StadiumEvent.class, boolean.class},
                event, true);
        shoot(app, outDir, "review-step");
        app.dispose();
    }

    /** Lays out, paints the content pane and writes it out as a PNG. */
    private static void shoot(StadiumBookingApp app, File outDir, String name) throws Exception {
        app.validate();
        app.getContentPane().setSize(app.getContentPane().getPreferredSize());
        Thread.sleep(400);
        BufferedImage image = new BufferedImage(app.getContentPane().getWidth(),
                app.getContentPane().getHeight(), BufferedImage.TYPE_INT_RGB);
        app.getContentPane().paint(image.getGraphics());
        File file = new File(outDir, name + ".png");
        ImageIO.write(image, "png", file);
        System.out.println("wrote " + file + "  (" + image.getWidth() + "x" + image.getHeight() + ")");
    }

    private static <T> T find(StadiumBookingApp app, Class<T> type) throws Exception {
        Deque<java.awt.Component> queue = new ArrayDeque<>();
        queue.add(app.getContentPane());
        while (!queue.isEmpty()) {
            java.awt.Component component = queue.poll();
            if (type.isInstance(component)) {
                return type.cast(component);
            }
            if (component instanceof java.awt.Container) {
                java.awt.Container container = (java.awt.Container) component;
                queue.addAll(new ArrayList<>(List.of(container.getComponents())));
            }
        }
        throw new IllegalStateException("no " + type.getSimpleName() + " on screen");
    }

    private static void invoke(Object target, String name, Class<?>[] types, Object... args)
            throws Exception {
        Method method = target.getClass().getDeclaredMethod(name, types);
        method.setAccessible(true);
        try {
            method.invoke(target, args);
        } catch (java.lang.reflect.InvocationTargetException error) {
            throw new IllegalStateException(name + " failed", error.getCause());
        }
    }

    private static Object field(Object target, String name) throws Exception {
        Field found = target.getClass().getDeclaredField(name);
        found.setAccessible(true);
        return found.get(target);
    }
}
