package com.stadium.booking.booking;

import com.stadium.booking.data.SeatKey;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * One confirmed booking: who booked, which event, which seats, and for how much.
 *
 * <p>A booking is a record of something that already happened. Nothing in this
 * class changes a booking — cancelling one is the only thing that alters it, and
 * even that only sets the status. That is deliberate: once money is involved, the
 * booking should read the same tomorrow as it does today.
 *
 * <p>"Serializable" means the booking can be written to a file and read back
 * later, which is how it survives the application being closed.
 */
public final class Booking implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String reference;
    private final String stadiumId;
    private final String eventId;
    private final String event;
    private final String customerName;
    private final String email;
    private final String phone;
    private final ArrayList<SeatKey> seats;
    private final double total;
    private final Instant createdAt;
    private final LocalDate eventDate;
    private final LocalTime eventStartTime;
    private BookingStatus status;

    /** Compatibility constructor for bookings created by the original demo. */
    public Booking(String reference,
                   String event,
                   String customerName,
                   String email,
                   String phone,
                   List<SeatKey> seats,
                   double total,
                   Instant createdAt) {
        this(reference, "", "", event, customerName, email, phone, seats, total,
                createdAt, null, null);
    }

    public Booking(String reference,
                   String stadiumId,
                   String eventId,
                   String event,
                   String customerName,
                   String email,
                   String phone,
                   List<SeatKey> seats,
                   double total,
                   Instant createdAt) {
        this(reference, stadiumId, eventId, event, customerName, email, phone, seats, total,
                createdAt, null, null);
    }

    public Booking(String reference,
                   String stadiumId,
                   String eventId,
                   String event,
                   String customerName,
                   String email,
                   String phone,
                   List<SeatKey> seats,
                   double total,
                   Instant createdAt,
                   LocalDate eventDate,
                   LocalTime eventStartTime) {
        this.reference = Objects.requireNonNull(reference, "reference");
        this.stadiumId = stadiumId == null ? "" : stadiumId;
        this.eventId = eventId == null ? "" : eventId;
        this.event = Objects.requireNonNull(event, "event");
        this.customerName = Objects.requireNonNull(customerName, "customerName");
        this.email = Objects.requireNonNull(email, "email");
        this.phone = Objects.requireNonNull(phone, "phone");
        this.seats = new ArrayList<>(Objects.requireNonNull(seats, "seats"));
        this.total = total;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.eventDate = eventDate;
        this.eventStartTime = eventStartTime;
        this.status = BookingStatus.CONFIRMED;
    }

    public String getReference() {
        return reference;
    }

    public String getStadiumId() {
        return stadiumId == null ? "" : stadiumId;
    }

    public String getEventId() {
        return eventId == null ? "" : eventId;
    }

    public String getEvent() {
        return event;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    /**
     * The seats on this booking.
     *
     * <p>unmodifiableList wraps the real list so that nobody can add or remove a
     * seat from a booking after it has been made. The list is still readable, it
     * just cannot be changed.
     */
    public List<SeatKey> getSeats() {
        return Collections.unmodifiableList(seats);
    }

    /**
     * The seats as one line of text, for the receipt and the bookings list.
     *
     * <p>"B4-03, B4-04, C1-11"
     *
     * <p>The seats are sorted first so the line always comes out the same way. A
     * customer who booked A1 then B2 would otherwise see "B2, A1" on the receipt
     * and "A1, B2" on the ticket.
     */
    public String getSeatDisplay() {
        List<SeatKey> sorted = new ArrayList<>(seats);
        Collections.sort(sorted);

        StringBuilder text = new StringBuilder();

        for (SeatKey seat : sorted) {
            // Put a comma after every seat except the last one.
            if (text.length() > 0) {
                text.append(", ");
            }
            text.append(seat.display());
        }

        return text.toString();
    }

    public double getTotal() {
        return total;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public LocalTime getEventStartTime() {
        return eventStartTime;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public boolean isConfirmed() {
        return status == BookingStatus.CONFIRMED;
    }

    /** Undoes a cancel whose database save failed, so the booking stays confirmed. */
    public void restoreConfirmed() {
        status = BookingStatus.CONFIRMED;
    }

    public void cancel() {
        status = BookingStatus.CANCELLED;
    }

    @Override
    public String toString() {
        return reference + " - " + getSeatDisplay();
    }
}
