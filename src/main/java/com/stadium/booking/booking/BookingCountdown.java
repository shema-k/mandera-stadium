package com.stadium.booking.booking;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
/** Formats the time remaining before an event's booking deadline. */
public final class BookingCountdown {
    private BookingCountdown() {
    }

    /**
     * Says how long is left before bookings close, in the biggest unit that
     * still reads sensibly.
     *
     * <p>Nobody wants to be told "1483 hours left". They want "2 months left",
     * then "3 days left", then "4 hours left" as the day approaches. So this
     * works in four steps, and stops at the first one that applies.
     *
     * <p><b>Step 1: is it already over?</b> If so there is nothing left to
     * count and we say so straight away.
     *
     * <p><b>Step 2: work out whole months, then whole days.</b> If there are
     * more than a month left, we say months, and add the odd days if there are
     * any — "2 months 3 days left" rather than just "2 months left".
     *
     * <p><b>Step 3: if less than a month, count whole days.</b>
     *
     * <p><b>Step 4: if it is today, count hours, then minutes.</b> The Math.max
     * at the end guarantees at least "1 minute left" even in the last few
     * seconds, so the line never reads "0 minutes left" while booking is
     * technically still open.
     *
     * <p>The word "left" changes to match the number, so it reads "1 day left"
     * and not "1 days left".
     */
    public static String format(LocalDateTime deadline) {
        // Step 1: has it already closed?
        if (deadline == null) {
            return "Deadline unavailable";
        }

        LocalDateTime now = LocalDateTime.now();
        if (!now.isBefore(deadline)) {
            return "Booking closed";
        }

        LocalDate today = now.toLocalDate();
        LocalDate endDate = deadline.toLocalDate();
        long days = ChronoUnit.DAYS.between(today, endDate);
        long months = ChronoUnit.MONTHS.between(today, endDate);

        // Step 2: more than a month left, so count months.
        if (months > 1) {
            long remainingDays = ChronoUnit.DAYS.between(today.plusMonths(months), endDate);
            if (remainingDays > 0) {
                return months + " months " + remainingDays
                        + (remainingDays == 1 ? " day left" : " days left");
            }
            return months + " months left";
        }

        if (months == 1) {
            long remainingDays = ChronoUnit.DAYS.between(today.plusMonths(1), endDate);
            if (remainingDays > 0) {
                return "1 month " + remainingDays + (remainingDays == 1 ? " day left" : " days left");
            }
            return "1 month left";
        }

        // Step 3: under a month, so count days.
        if (days > 0) {
            return days + (days == 1 ? " day left" : " days left");
        }

        // Step 4: it is today, so count down through hours to minutes.
        Duration remaining = Duration.between(now, deadline);
        long hours = remaining.toHours();
        if (hours > 0) {
            return hours + (hours == 1 ? " hour left" : " hours left");
        }

        long minutes = Math.max(1, remaining.toMinutes());
        return minutes + (minutes == 1 ? " minute left" : " minutes left");
    }
}
