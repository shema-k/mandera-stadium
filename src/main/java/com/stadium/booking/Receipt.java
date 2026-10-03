package com.stadium.booking;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * An itemised bill for one booking: what each seat cost, what the fee was, and
 * what the customer paid in total.
 *
 * <p>This is the receipt a customer is entitled to after booking. It is built
 * from the booking itself, so it cannot disagree with what was charged: every
 * line's price is looked up from the same pricing curve the seat map charged,
 * and the total is the booking's own recorded total rather than a fresh sum.
 *
 * <p>One caveat worth stating plainly. The per-seat prices are recomputed from
 * the event and the seat positions, so a receipt shown for a past booking
 * depends on the event's price factor still being the one that applied at the
 * time. That is true for this build, where prices are fixed per event, but a
 * venue that reprices between sales would need the price stored on the booking.
 */
public final class Receipt {
    private static final DateTimeFormatter STAMP =
            DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm", Locale.ENGLISH)
                    .withZone(ZoneId.systemDefault());
    private static final DateTimeFormatter DATE =
            DateTimeFormatter.ofPattern("EEE, d MMM yyyy", Locale.ENGLISH);

    /** One line on the bill: a single seat and what it cost. */
    public static final class Line {
        private final SeatKey key;
        private final String sectionLabel;
        private final String tier;
        private final double price;

        Line(SeatKey key, String sectionLabel, String tier, double price) {
            this.key = key;
            this.sectionLabel = sectionLabel;
            this.tier = tier;
            this.price = price;
        }

        public SeatKey getKey() {
            return key;
        }

        public String getSeatLabel() {
            return key.display();
        }

        public String getSectionLabel() {
            return sectionLabel;
        }

        public String getTier() {
            return tier;
        }

        public double getPrice() {
            return price;
        }
    }

    /** A group of seats in one section, totalled. A subheading on the bill. */
    public static final class SectionTotal {
        private final String sectionId;
        private final String sectionLabel;
        private final int count;
        private final double total;

        SectionTotal(String sectionId, String sectionLabel, int count, double total) {
            this.sectionId = sectionId;
            this.sectionLabel = sectionLabel;
            this.count = count;
            this.total = total;
        }

        public String getSectionId() {
            return sectionId;
        }

        public String getSectionLabel() {
            return sectionLabel;
        }

        public int getCount() {
            return count;
        }

        public double getTotal() {
            return total;
        }
    }

    private final Booking booking;
    private final StadiumEvent event;
    private final Stadium stadium;
    private final List<Line> lines;
    private final List<SectionTotal> sectionTotals;
    private final double seatSubtotal;
    private final double bookingFee;

    private Receipt(Booking booking, StadiumEvent event, Stadium stadium,
                    List<Line> lines, List<SectionTotal> sectionTotals,
                    double seatSubtotal, double bookingFee) {
        this.booking = booking;
        this.event = event;
        this.stadium = stadium;
        this.lines = lines;
        this.sectionTotals = sectionTotals;
        this.seatSubtotal = seatSubtotal;
        this.bookingFee = bookingFee;
    }

    /**
     * Builds the receipt for a booking.
     *
     * @param service the service holding the pricing curve, so the prices here
     *                are the same ones the seat map charged
     */
    public static Receipt forBooking(Booking booking, BookingService service) {
        if (booking == null) {
            throw new IllegalArgumentException("A receipt needs a booking");
        }
        StadiumEvent event = StadiumData.getEvent(booking.getEventId());
        Stadium stadium = StadiumData.getStadium(booking.getStadiumId());
        if (stadium == null && event != null) {
            stadium = StadiumData.getStadium(event.getStadiumId());
        }

        List<Line> lines = new ArrayList<>();
        Map<String, SectionTotal> bySection = new LinkedHashMap<>();
        Map<String, String> labels = new LinkedHashMap<>();
        Map<String, Integer> counts = new LinkedHashMap<>();

        for (SeatKey key : booking.getSeats()) {
            StadiumEvent pricedFor = event == null ? null : event;
            double price = service != null && pricedFor != null
                    ? service.getSeatPrice(pricedFor, key)
                    : 0.0;

            SeatSection section = stadium == null ? null : stadium.getSection(key.getSection());
            String sectionLabel = section == null ? Messages.get("receipt.sectionLine") + " " + key.getSection() : section.getLabel();
            String tier = service != null
                    ? service.getRowTierName(key.getRow(), section == null ? 1 : section.getRows())
                    : "";

            lines.add(new Line(key, sectionLabel, tier, price));

            String id = key.getSection();
            labels.putIfAbsent(id, sectionLabel);
            counts.merge(id, 1, Integer::sum);
            bySection.compute(id, (ignoredKey, existing) ->
                    new SectionTotal(id, sectionLabel, counts.getOrDefault(id, 1),
                            existing == null ? price : existing.getTotal() + price));
        }

        List<SectionTotal> totals = new ArrayList<>(bySection.values());
        double seatSubtotal = 0.0;
        for (Line line : lines) {
            seatSubtotal += line.getPrice();
        }
        seatSubtotal = round(seatSubtotal);

        double fee = Math.max(0.0, round(booking.getTotal() - seatSubtotal));
        return new Receipt(booking, event, stadium, lines, totals, seatSubtotal, fee);
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public Booking getBooking() {
        return booking;
    }

    public StadiumEvent getEvent() {
        return event;
    }

    public Stadium getStadium() {
        return stadium;
    }

    public List<Line> getLines() {
        return lines;
    }

    public List<SectionTotal> getSectionTotals() {
        return sectionTotals;
    }

    public int getSeatCount() {
        return lines.size();
    }

    /** The seats' own prices, before the ticketing fee. */
    public double getSeatSubtotal() {
        return seatSubtotal;
    }

    public double getBookingFee() {
        return bookingFee;
    }

    /** What the customer paid in total. Taken from the booking, not recomputed. */
    public double getTotal() {
        return booking.getTotal();
    }

    public String getReference() {
        return booking.getReference();
    }

    public String getPaidAt() {
        return STAMP.format(booking.getCreatedAt());
    }

    public String getEventWhen() {
        if (event == null) {
            if (booking.getEventDate() == null) {
                return booking.getEvent();
            }
            return booking.getEvent() + "  •  " + booking.getEventDate().format(DATE)
                    + "  •  " + booking.getEventStartTime();
        }
        return event.getDate().format(DATE) + "  •  " + event.getStartTime()
                + "  •  doors " + event.getDoorsLabel();
    }

    private static String money(double value) {
        return BookingService.formatMoney(value);
    }

    /**
     * The receipt as fixed-width text, so it lines up in a terminal, in a text
     * file and on a printed page without needing a rendering engine.
     */
    public String toText() {
        StringBuilder out = new StringBuilder();
        String rule = "=".repeat(66);
        String thin = "-".repeat(66);

        out.append(rule).append('\n');
        out.append("  NAMBOOLE STADIUM  -  BOOKING RECEIPT").append('\n');
        out.append("  Mandela National Stadium, Kampala, Uganda").append('\n');
        out.append(rule).append('\n');
        out.append('\n');
        out.append(String.format("  Receipt no.   : %s%n", getReference()));
        out.append(String.format("  Issued        : %s%n", getPaidAt()));
        out.append(String.format("  Status        : %s%n", booking.getStatus().name()));
        out.append('\n');

        out.append("  " + Messages.get("receipt.eventHeading")).append('\n');
        out.append(String.format("    %s%n", booking.getEvent()));
        out.append(String.format("    %s%n", getEventWhen()));
        if (event != null && event.getType() != null) {
            out.append(String.format("    Type         : %s%n", event.getType().getLabel()));
        }
        out.append('\n');

        out.append(String.format("  SEATS BOOKED  (%d)%n", getSeatCount()));
        out.append(thin).append('\n');
        out.append(String.format("  %-10s  %-18s  %14s  %12s%n",
                Messages.get("receipt.colSeat"), Messages.get("receipt.colSection"), Messages.get("receipt.colTier"), Messages.get("receipt.colAmount")));
        for (Line line : lines) {
            out.append(String.format("  %-10s  %-18s  %-14s  %12s%n",
                    line.getSeatLabel(),
                    line.getSectionLabel(),
                    line.getTier(),
                    money(line.getPrice())));
        }
        out.append(thin).append('\n');

        out.append('\n');
        out.append("  " + Messages.get("receipt.chargesHeading")).append('\n');
        out.append(String.format("    %-34s %16s%n", Messages.get("receipt.seatsLine") + " (" + getSeatCount() + ")",
                money(seatSubtotal)));
        // Per-end subtotals, each showing what one seat in that end cost, so the
        // figures can be checked against the seat lines above rather than taken
        // on trust.
        for (SectionTotal total : sectionTotals) {
            double each = total.getCount() == 0
                    ? 0.0 : total.getTotal() / total.getCount();
            out.append(String.format("      %d x %-24s @ %12s %16s%n",
                    total.getCount(), total.getSectionLabel(),
                    money(each), money(total.getTotal())));
        }
        out.append(String.format("    %-34s %16s%n", Messages.get("receipt.feeLine"), money(bookingFee)));
        out.append(rule).append('\n');
        out.append(String.format("  %-34s %16s%n", Messages.get("receipt.totalPaid"), money(getTotal())));
        out.append(rule).append('\n');
        out.append('\n');
        out.append("  " + Messages.get("receipt.billedTo")).append('\n');
        out.append(String.format("    %s%n", booking.getCustomerName()));
        out.append(String.format("    %s%n", booking.getEmail()));
        out.append(String.format("    %s%n", booking.getPhone()));
        out.append('\n');
        out.append(thin).append('\n');
        out.append("  Keep this receipt. Quote the receipt number at the gate.").append('\n');
        out.append("  Seats are held under this number only.").append('\n');
        out.append(rule).append('\n');
        return out.toString();
    }
}
