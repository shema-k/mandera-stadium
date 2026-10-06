package com.stadium.booking.booking;

import com.stadium.booking.data.Seat;
import com.stadium.booking.data.SeatKey;
import com.stadium.booking.data.StadiumEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * Temporary holds on seats while a customer completes their details.
 *
 * <p><b>Why this exists.</b> Without a hold, two people can open the same seat
 * and both start filling in the form. They only find out who got there first
 * when they press Confirm, and by then one of them has typed their details for
 * nothing. A hold reserves the seat for five minutes, so the second person is
 * turned away the moment they click it.
 *
 * <p>A hold is <b>not</b> a booking. Nothing is charged and nothing is sold. If
 * the customer walks away, the hold simply runs out and the seat goes back on
 * sale. That is why the seat map says "held" in a different colour from "booked".
 *
 * <p><b>Everything here is synchronized</b>, which means only one thread can be
 * inside a method at a time. That matters because two people booking at once are
 * handled by two threads, and without it they could both pass the same check
 * before either had written down its answer.
 */
public final class SeatHoldService {
    /** How long a seat stays held before it is released automatically. */
    public static final Duration HOLD_DURATION = Duration.ofMinutes(5);

    /**
     * One held seat.
     *
     * <p>"owner" is whoever took the hold, so the program can tell "this is my
     * own hold, keep showing it greyed out" from "somebody else has this, turn
     * them away".
     *
     * <p>The class is private because nothing outside this file needs to know a
     * hold exists. The rest of the program only ever asks the questions asked
     * below.
     */
    private static final class Hold {
        /** Which seat this is a hold on. */
        final SeatKey key;

        /** Which event. A seat can be free for one match and sold for another. */
        final StadiumEvent event;

        /** Who took the hold. */
        final String owner;

        /** When the hold gives up and the seat goes back on sale. */
        final Instant expiresAt;

        Hold(SeatKey key, StadiumEvent event, String owner, Instant expiresAt) {
            this.key = key;
            this.event = event;
            this.owner = owner;
            this.expiresAt = expiresAt;
        }

        boolean covers(StadiumEvent candidate) {
            return event != null && event.getId().equals(candidate.getId());
        }
    }

    private final Map<SeatKey, Hold> holds = new LinkedHashMap<>();
    private final BookingService bookingService;

    public SeatHoldService(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Throws away every hold whose time is up.
     *
     * <p>This is called at the start of nearly every other method, so an expired
     * seat is treated as free the moment somebody looks at it, rather than
     * waiting for a timer nobody started.
     */
    public synchronized void purgeExpired() {
        Instant now = Instant.now();

        // removeIf deletes every entry the test says yes to. Here that means
        // "every hold that ran out some time before now".
        holds.entrySet().removeIf(entry -> entry.getValue().expiresAt.isBefore(now));
    }

    public synchronized void releaseAll() {
        holds.clear();
    }

    public synchronized void releaseAllFor(String owner) {
        holds.entrySet().removeIf(entry -> entry.getValue().owner.equals(owner));
    }

    public synchronized void release(SeatKey key) {
        holds.remove(key);
    }

    /**
     * Is this seat taken by somebody else, so this customer cannot have it?
     *
     * <p>Three steps. First throw away expired holds, so a seat whose five
     * minutes are up counts as free. Then look the seat up — if there is no hold
     * at all, or the hold is for a different event, the answer is no. Otherwise
     * the answer is yes if the hold belongs to somebody else.
     */
    public synchronized boolean isHeldByAnother(SeatKey key, StadiumEvent event, String owner) {
        purgeExpired();

        Hold hold = holds.get(key);
        if (hold == null || !hold.covers(event)) {
            return false;
        }

        return !hold.owner.equals(owner);
    }

    /**
     * Is this seat one of the seats this customer is holding?
     *
     * <p>Used to keep the customer's own choices greyed out, so they can see
     * what they picked without being able to pick it twice.
     */
    public synchronized boolean isHeldBy(SeatKey key, String owner) {
        purgeExpired();

        Hold hold = holds.get(key);
        return hold != null && hold.owner.equals(owner);
    }

    /**
     * Places or renews a hold.
     *
     * @return null when the hold succeeded, or a reason the seat cannot be held
     */
    public synchronized String hold(SeatKey key, StadiumEvent event, String owner) {
        // Step 1: is the seat even real and free? This is the booking rules'
        // answer, so the holds do not have to know anything about sold seats.
        if (bookingService != null && !bookingService.isSeatSelectable(key)) {
            return "Seat " + key.display() + " is not available";
        }

        // Step 2: throw away anything that ran out, or it would block forever.
        purgeExpired();

        // Step 3: if somebody else already holds this seat for this event, say
        // no and give a reason the customer can act on.
        Hold existing = holds.get(key);
        if (existing != null && !existing.owner.equals(owner) && existing.covers(event)) {
            return "Seat " + key.display() + " is being held by another customer";
        }

        // Step 4: take the hold, or renew it if it was already ours. Five
        // minutes from now is when it runs out.
        holds.put(key, new Hold(key, event, owner, Instant.now().plus(HOLD_DURATION)));

        // null means "no problem, the hold is yours".
        return null;
    }

    public synchronized Instant expiryFor(SeatKey key) {
        Hold hold = holds.get(key);
        return hold == null ? null : hold.expiresAt;
    }

    /**
     * How many seconds are left on a hold, for the countdown under the total.
     *
     * <p>Math.max stops a seat showing "-1 seconds left" in the moment between
     * running out and the next purge.
     */
    public synchronized long secondsRemaining(SeatKey key) {
        Hold hold = holds.get(key);
        if (hold == null) {
            return 0;
        }
        long seconds = Duration.between(Instant.now(), hold.expiresAt).getSeconds();
        return Math.max(0, seconds);
    }

    public synchronized List<SeatKey> heldBy(String owner, StadiumEvent event) {
        purgeExpired();
        List<SeatKey> keys = new ArrayList<>();
        for (Hold hold : holds.values()) {
            if (hold.owner.equals(owner) && hold.covers(event)) {
                keys.add(hold.key);
            }
        }
        return keys;
    }

    public synchronized Set<SeatKey> allHeldKeys(StadiumEvent event) {
        purgeExpired();
        Set<SeatKey> keys = new LinkedHashSet<>();
        for (Hold hold : holds.values()) {
            if (hold.covers(event)) {
                keys.add(hold.key);
            }
        }
        return keys;
    }
}
