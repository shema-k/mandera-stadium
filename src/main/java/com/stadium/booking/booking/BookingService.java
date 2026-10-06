package com.stadium.booking.booking;

import com.stadium.booking.data.AnnouncementType;
import com.stadium.booking.data.EventType;
import com.stadium.booking.data.Seat;
import com.stadium.booking.data.SeatKey;
import com.stadium.booking.data.SeatSection;
import com.stadium.booking.data.SeatStatus;
import com.stadium.booking.data.Stadium;
import com.stadium.booking.data.StadiumAnnouncement;
import com.stadium.booking.data.StadiumData;
import com.stadium.booking.data.StadiumEvent;
import com.stadium.booking.data.StadiumShape;
import com.stadium.booking.storage.BookingStore;
import com.stadium.booking.storage.Database;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
/**
 * Contains the venue directory, event schedules, seat inventory and booking
 * rules. The service has no Swing dependency, so the rules can be tested and
 * reused independently of the desktop interface.
 */
@SuppressWarnings("this-escape")
public class BookingService {
    /**
     * Largest number of seats one reservation may hold. High enough for a family or
     * a club party to book together, low enough that the price outline and the
     * receipt stay readable in one screen.
     */
    public static final int MAX_SEATS_PER_BOOKING = 50;
    /** Ticketing fee charged once per reservation, in Ugandan shillings. */
    public static final double BOOKING_FEE = 5000.0;

    private final Map<SeatKey, Seat> seatInventory = new LinkedHashMap<>();
    private final List<Booking> bookings = new ArrayList<>();
    private final List<StadiumAnnouncement> submittedRequests = new ArrayList<>();
    private final BookingStore store;
    private StadiumEvent activeEvent;
    private Stadium stadiumOverride;
    private int nextReferenceNumber = 1;

    public BookingService() {
        this(new BookingStore(new File("stadium-bookings.dat").toPath()));
    }

    /** Uses a caller-supplied database, so the file can be chosen by the caller. */
    public BookingService(Database database) {
        this(new BookingStore(new File("stadium-bookings.dat").toPath(), database));
    }

    public BookingService(BookingStore store) {
        this.store = store;
        activeEvent = StadiumData.getEvents().get(0);
        createInventory();
        bookings.addAll(store == null ? Collections.<Booking>emptyList() : store.read());
        updateNextReferenceNumber();
    }

    /** Compatibility constructor for the original compact single-event demo. */
    public BookingService(String legacyEvent, BookingStore store) {
        this(store);
        if (legacyEvent != null && !legacyEvent.trim().isEmpty()) {
            stadiumOverride = createLegacyStadium();
            activeEvent = new StadiumEvent("legacy-event", stadiumOverride.getId(), EventType.GAME,
                    legacyEvent.trim(), "General event", "Home", "Away", null,
                    "Legacy event schedule", java.time.LocalDate.now().plusDays(1),
                    java.time.LocalTime.of(19, 0), java.time.LocalTime.of(18, 0), 1.0);
            createInventory();
        }
    }

    private Stadium createLegacyStadium() {
        List<SeatSection> sections = new ArrayList<>();
        sections.add(new SeatSection("A", "North end", 6, 10, 45));
        sections.add(new SeatSection("B", "East side", 6, 10, 40));
        sections.add(new SeatSection("C", "South end", 6, 10, 35));
        sections.add(new SeatSection("D", "West side", 6, 10, 30));
        return new Stadium("legacy-stadium", "Legacy Arena", "Demo", "Demo",
                "Demo venue", "A compact legacy venue for compatibility testing.",
                "Demo address", 240, "#2563eb", StadiumShape.BOX, sections);
    }

    private void createInventory() {
        seatInventory.clear();
        Stadium stadium = getActiveStadium();
        if (stadium == null || activeEvent == null) {
            return;
        }
        for (SeatSection section : stadium.getSections()) {
            for (int row = 1; row <= section.getRows(); row++) {
                for (int number = 1; number <= section.getSeatsPerRow(); number++) {
                    SeatKey key = new SeatKey(section.getId(), row, number);
                    seatInventory.put(key, new Seat(key, getSeatPrice(section, row)));
                }
            }
        }
    }

    /** Price decreases from the front rows toward the back rows. */
    public double getSeatPrice(SeatSection section, int row) {
        if (section == null) {
            return 0.0;
        }
        int safeRow = Math.max(1, Math.min(row, section.getRows()));
        double rowMultiplier = getRowPriceMultiplier(safeRow, section.getRows());
        double eventMultiplier = activeEvent == null ? 1.0 : activeEvent.getPriceFactor();
        return roundMoney(section.getBasePrice() * rowMultiplier * eventMultiplier);
    }

    public double getSeatPrice(SeatKey key) {
        Seat seat = seatInventory.get(key);
        return seat == null ? 0.0 : seat.getPrice();
    }

    /**
     * Price of a seat for any event, without changing the currently selected
     * event. Used by the booked-seats view so browsing another event's ledger
     * never disturbs the seat map the user is booking on.
     */
    public double getSeatPrice(StadiumEvent event, SeatKey key) {
        if (event == null || key == null) {
            return 0.0;
        }
        SeatSection section = stadiumFor(event).getSection(key.getSection());
        if (section == null) {
            return 0.0;
        }
        return roundMoney(section.getBasePrice()
                * getRowPriceMultiplier(key.getRow(), section.getRows())
                * event.getPriceFactor());
    }

    private Stadium stadiumFor(StadiumEvent event) {
        if (stadiumOverride != null && "legacy-event".equals(event.getId())) {
            return stadiumOverride;
        }
        Stadium stadium = StadiumData.getStadium(event.getStadiumId());
        return stadium == null ? StadiumData.getStadium("namboole") : stadium;
    }

    public static double getRowPriceMultiplier(int row, int rows) {
        if (rows <= 1) {
            return 1.0;
        }
        int safeRow = Math.max(1, Math.min(row, rows));
        double position = (safeRow - 1.0) / (rows - 1.0);
        if (position <= 0.20) {
            return 1.40;
        }
        if (position <= 0.50) {
            return 1.20;
        }
        if (position <= 0.80) {
            return 0.95;
        }
        return 0.70;
    }

    /** Human-readable name of the price tier a row falls into. */
    public String getRowTierName(int row, int rows) {
        if (rows <= 1) {
            return "Front rows";
        }
        double position = (Math.max(1, Math.min(row, rows)) - 1.0) / (rows - 1.0);
        if (position <= 0.20) {
            return "Front rows";
        }
        if (position <= 0.50) {
            return "Upper rows";
        }
        if (position <= 0.80) {
            return "Middle rows";
        }
        return "Back rows";
    }

    /**
     * Rounds a charge to the nearest 500 shillings, which is how Ugandan
     * ticketing prices are normally rounded for cash sales.
     */
    public static double roundMoney(double value) {
        return Math.round(value / 500.0) * 500.0;
    }

    /**
     * A reference that is unique across the whole database, so two people booking
     * at the same time never collide. Falls back to the in-memory counter when the
     * application runs without a store.
     */
    private String nextReference() {
        if (store != null) {
            try {
                String reference = store.allocateReference();
                while (reference.startsWith("ST-")
                        && reference.substring(3).matches("\\d+")
                        && Integer.parseInt(reference.substring(3)) >= nextReferenceNumber) {
                    nextReferenceNumber = Integer.parseInt(reference.substring(3)) + 1;
                }
                return reference;
            } catch (IOException exception) {
                // Fall through to the counter so booking can still be attempted.
            }
        }
        return "ST-" + nextReferenceNumber++;
    }

    private void updateNextReferenceNumber() {
        int highest = 0;
        for (Booking booking : bookings) {
            try {
                String numberPart = booking.getReference().replaceFirst("^ST-", "");
                highest = Math.max(highest, Integer.parseInt(numberPart));
            } catch (NumberFormatException ignored) {
                // Keep generating a valid reference for old/custom records.
            }
        }
        nextReferenceNumber = highest + 1;
    }

    public List<Stadium> getStadiums() {
        return StadiumData.getStadiums();
    }

    public List<StadiumEvent> getEvents(String stadiumId) {
        return StadiumData.getEvents(stadiumId);
    }

    public List<StadiumEvent> getEventsForStadium(Stadium stadium) {
        return stadium == null ? new ArrayList<>() : getEvents(stadium.getId());
    }

    public List<StadiumAnnouncement> getAnnouncements(String stadiumId) {
        List<StadiumAnnouncement> announcements = new ArrayList<>(
                StadiumData.getAnnouncements(stadiumId));
        for (StadiumAnnouncement request : submittedRequests) {
            if (request.getStadiumId().equals(stadiumId)) {
                announcements.add(request);
            }
        }
        announcements.sort((left, right) -> right.getCreatedAt().compareTo(left.getCreatedAt()));
        return announcements;
    }

    public List<StadiumAnnouncement> getAnnouncements() {
        List<StadiumAnnouncement> announcements = new ArrayList<>(StadiumData.getAnnouncements());
        announcements.addAll(submittedRequests);
        announcements.sort((left, right) -> right.getCreatedAt().compareTo(left.getCreatedAt()));
        return announcements;
    }

    public StadiumAnnouncement addSpecialRequest(String stadiumId, String category, String message) {
        Stadium stadium = StadiumData.getStadium(stadiumId);
        if (stadium == null) {
            throw new IllegalArgumentException("Choose a valid stadium");
        }
        String normalizedCategory = category == null || category.trim().isEmpty()
                ? "General request" : category.trim();
        String requestProblem = FormRules.requestProblem(message);
        if (requestProblem != null) {
            throw new IllegalArgumentException(requestProblem);
        }
        String normalizedMessage = message.trim();
        LocalDateTime now = LocalDateTime.now();
        StadiumAnnouncement request = new StadiumAnnouncement(
                "request-" + System.currentTimeMillis(), stadiumId, "", AnnouncementType.SPECIAL_REQUEST,
                normalizedCategory, normalizedMessage, now, now.plusDays(30));
        submittedRequests.add(request);
        return request;
    }

    public StadiumAnnouncement getBlockingAnnouncement(StadiumEvent event) {
        if (event == null) {
            return null;
        }
        for (StadiumAnnouncement announcement : getAnnouncements(event.getStadiumId())) {
            if (announcement.isBlocking() && announcement.appliesToEvent(event.getId())) {
                return announcement;
            }
        }
        return null;
    }

    public StadiumEvent getActiveEvent() {
        return activeEvent;
    }

    public Stadium getActiveStadium() {
        if (stadiumOverride != null && activeEvent != null
                && stadiumOverride.getId().equals(activeEvent.getStadiumId())) {
            return stadiumOverride;
        }
        return activeEvent == null ? null : StadiumData.getStadium(activeEvent.getStadiumId());
    }

    public StadiumShape getActiveShape() {
        Stadium stadium = getActiveStadium();
        return stadium == null ? StadiumShape.BOX : stadium.getShape();
    }

    public void selectEvent(StadiumEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("An event must be selected before booking");
        }
        if (!"legacy-event".equals(event.getId())) {
            stadiumOverride = null;
        }
        activeEvent = event;
        createInventory();
    }

    public String getEvent() {
        return activeEvent == null ? "" : activeEvent.getHeadline();
    }

    public List<Seat> getSeats() {
        return new ArrayList<>(seatInventory.values());
    }

    /**
     * All the seats in one section, such as every seat in end B.
     *
     * <p>seatInventory is a map, which is a bit like a dictionary: it holds
     * things under a key, so we can look one up without searching. The values()
     * part gives us just the seats, in no particular order.
     *
     * @return the seats in that section, or an empty list if no section was named
     */
    public List<Seat> getSeats(String sectionId) {
        List<Seat> found = new ArrayList<>();

        if (sectionId == null) {
            return found;
        }

        for (Seat seat : seatInventory.values()) {
            // equalsIgnoreCase means "A" matches "a", so the caller does not
            // have to know which way round somebody typed the letter.
            if (seat.getKey().getSection().equalsIgnoreCase(sectionId)) {
                found.add(seat);
            }
        }

        return found;
    }

    public Seat getSeat(SeatKey key) {
        return seatInventory.get(key);
    }

    public SeatStatus getStatus(SeatKey key) {
        return isBooked(key) ? SeatStatus.BOOKED : SeatStatus.AVAILABLE;
    }

    public boolean isSeatSelectable(SeatKey key) {
        return getStatus(key) == SeatStatus.AVAILABLE && isBookingOpen(activeEvent);
    }

    /**
     * Why a seat cannot be booked right now, in words meant for the customer.
     * A seat can be unavailable because it is sold, because it does not exist on
     * this venue's plan, or because booking is closed for the whole event, and
     * the right response differs in each case.
     */
    public String unavailableReason(SeatKey key) {
        String seat = key == null ? "That seat" : "Seat " + key.display();
        if (key == null || !seatInventory.containsKey(key)) {
            return seat + " is not a seat at this stadium";
        }
        if (isBooked(key)) {
            return seat + " has already been booked. Please choose another seat.";
        }
        if (activeEvent != null && !isBookingOpen(activeEvent)) {
            return seat + " cannot be booked because "
                    + getBookingRestrictionMessage(activeEvent);
        }
        return seat + " is not available. Please choose another seat.";
    }

    public boolean isBooked(SeatKey key) {
        if (key == null || activeEvent == null) {
            return false;
        }
        return getBookedSeatKeys(activeEvent).contains(key);
    }

    public Set<SeatKey> getBookedSeatKeys(StadiumEvent event) {
        Set<SeatKey> booked = new LinkedHashSet<>();
        for (Booking booking : bookings) {
            if (booking.isConfirmed() && belongsToEvent(booking, event)) {
                booked.addAll(booking.getSeats());
            }
        }
        return booked;
    }

    private boolean belongsToEvent(Booking booking, StadiumEvent event) {
        if (event == null) {
            return false;
        }
        String eventId = booking.getEventId();
        if (eventId != null && !eventId.isEmpty()) {
            return event.getId().equals(eventId);
        }
        return event.getHeadline().equals(booking.getEvent());
    }

    public int getTotalSeatCount(StadiumEvent event) {
        if (event == null) {
            return 0;
        }
        Stadium stadium = stadiumOverride != null && event.getId().equals("legacy-event")
                ? stadiumOverride : StadiumData.getStadium(event.getStadiumId());
        return stadium == null ? 0 : stadium.getSeatCount();
    }

    public int getAvailableSeatCount(StadiumEvent event) {
        int total = getTotalSeatCount(event);
        if (total == 0) {
            return 0;
        }
        return Math.max(0, total - getBookedSeatKeys(event).size());
    }

    public int getAvailableSeatCount() {
        return getAvailableSeatCount(activeEvent);
    }

    public int getBookedSeatCount() {
        return getBookedSeatKeys(activeEvent).size();
    }

    public double getVacancyPercentage(StadiumEvent event) {
        int total = getTotalSeatCount(event);
        if (total == 0) {
            return 0.0;
        }
        double percentage = getAvailableSeatCount(event) * 100.0 / total;
        return Math.round(percentage * 1000.0) / 1000.0;
    }

    public double getVacancyPercentage() {
        return getVacancyPercentage(activeEvent);
    }

    public boolean isBookingOpen(StadiumEvent event) {
        return event != null && event.isBookingOpen() && getBlockingAnnouncement(event) == null;
    }

    public String getBookingRestrictionMessage(StadiumEvent event) {
        StadiumAnnouncement blocking = getBlockingAnnouncement(event);
        if (blocking != null) {
            return blocking.getTitle() + " — " + blocking.getMessage();
        }
        if (event != null && !event.isBookingOpen()) {
            return "Bookings closed at " + event.getBookingDeadlineLabel();
        }
        return null;
    }

    public boolean isBookingOpen() {
        return isBookingOpen(activeEvent);
    }

    public String getBookingDeadlineLabel(StadiumEvent event) {
        return event == null ? "—" : event.getBookingDeadlineLabel();
    }

    public String getBookingDeadlineLabel() {
        return getBookingDeadlineLabel(activeEvent);
    }

    public List<SeatSection> getSectionDefinitions() {
        Stadium stadium = getActiveStadium();
        return stadium == null ? new ArrayList<>() : stadium.getSections();
    }

    public List<String> getSections() {
        List<String> sections = new ArrayList<>();
        for (SeatSection section : getSectionDefinitions()) {
            sections.add(section.getId());
        }
        return sections;
    }

    public int getRows(String sectionId) {
        SeatSection section = findSection(sectionId);
        return section == null ? 0 : section.getRows();
    }

    public int getSeatsPerRow(String sectionId) {
        SeatSection section = findSection(sectionId);
        return section == null ? 0 : section.getSeatsPerRow();
    }

    public double getSectionPrice(String sectionId) {
        SeatSection section = findSection(sectionId);
        return section == null ? 0.0 : getSeatPrice(section, 1);
    }

    private SeatSection findSection(String sectionId) {
        Stadium stadium = getActiveStadium();
        return stadium == null ? null : stadium.getSection(sectionId);
    }

    public List<Booking> getBookings() {
        return Collections.unmodifiableList(new ArrayList<>(bookings));
    }

    /**
     * The bookings made for one venue. A null means "every venue".
     */
    public List<Booking> getBookingsForStadium(String stadiumId) {
        List<Booking> found = new ArrayList<>();

        for (Booking booking : bookings) {
            if (stadiumId == null || stadiumId.equals(booking.getStadiumId())) {
                found.add(booking);
            }
        }

        return found;
    }

        /**
     * Whether any booking carries the given email address or phone number.
     *
     * <p>A quick yes or no for the search box. There is no sign-in, so this is
     * how somebody finds their own booking: they type the email address or phone
     * number they booked with.
     *
     * @return true if at least one booking matches
     */
    public boolean hasBookingFor(String contact) {
        return findBookingsFor(contact).size() > 0;
    }

    /**
     * The bookings made with a given email address or phone number, newest
     * first.
     *
     * <p>Three steps.
     *
     * <p><b>Step 1: tidy up what was typed.</b> Extra spaces are removed and
     * everything is made lower case, so "  AMINA@Example.CO.UG " still matches
     * "amina@example.co.ug".
     *
     * <p><b>Step 2: keep the ones that match.</b> A booking matches when the
     * typed text is the same as the email on the booking, or the same as the
     * phone number. Either one is enough.
     *
     * <p><b>Step 3: put the newest at the top.</b> Sorting by date is not quite
     * enough, because two bookings can be made on the same millisecond and would
     * then swap places each time the list is rebuilt. So the reference breaks
     * the tie. References are given out in order, which keeps the list steady.
     *
     * <p>Anything shorter than three characters is refused, because a single
     * letter would match half the bookings on the list.
     */
    public List<Booking> findBookingsFor(String contact) {
        List<Booking> found = new ArrayList<>();

        if (contact == null) {
            return found;
        }

        String wanted = contact.trim().toLowerCase(Locale.ENGLISH);
        if (wanted.length() < 3) {
            return found;
        }

        // Step 2: keep the bookings whose email or phone matches.
        for (Booking booking : bookings) {
            String email = clean(booking.getEmail());
            String phone = clean(booking.getPhone());

            if (email.equals(wanted) || phone.equals(wanted)) {
                found.add(booking);
            }
        }

        // Step 3: newest first. reversed() because a bigger reference and a
        // later date should both come first.
        Collections.sort(found, Comparator
                .comparing(Booking::getCreatedAt)
                .thenComparing(Booking::getReference)
                .reversed());

        return found;
    }

    /**
     * Turns text into the form we compare against: no nulls, no spaces at the
     * ends, all lower case.
     *
     * <p>Kept as one small method because the same tidying is needed for the
     * typed text, the email and the phone number, and doing it three different
     * ways is how they quietly stop matching each other.
     */
    private String clean(String text) {
        if (text == null) {
            return "";
        }
        return text.trim().toLowerCase(Locale.ENGLISH);
    }

    /** The bookings made for one event. */
    public List<Booking> getBookingsForEvent(StadiumEvent event) {
        List<Booking> found = new ArrayList<>();

        if (event == null) {
            return found;
        }

        for (Booking booking : bookings) {
            if (belongsToEvent(booking, event)) {
                found.add(booking);
            }
        }

        return found;
    }

    public double totalFor(List<Seat> selectedSeats) {
        if (selectedSeats == null) {
            return 0.0;
        }
        double total = 0.0;
        for (Seat seat : selectedSeats) {
            Seat inventorySeat = seatInventory.get(seat.getKey());
            if (inventorySeat != null) {
                total += inventorySeat.getPrice();
            }
        }
        return roundMoney(total);
    }

    public double getBookingFee() {
        return BOOKING_FEE;
    }

    /**
     * Formats an amount as Ugandan shillings, for example {@code UGX 89,500}.
     * Shared by every screen that shows a price so the format cannot drift.
     */
    public static String formatMoney(double amount) {
        return "UGX " + java.text.NumberFormat
                .getIntegerInstance(Locale.US)
                .format(Math.round(amount));
    }

    public double getTotalCharge(List<Seat> selectedSeats) {
        if (selectedSeats == null || selectedSeats.isEmpty()) {
            return 0.0;
        }
        return roundMoney(totalFor(selectedSeats) + BOOKING_FEE);
    }

    public String getRowTier(SeatKey key) {
        if (key == null) {
            return "Seat";
        }
        int rows = getRows(key.getSection());
        if (rows <= 1) {
            return "Standard seat";
        }
        double position = (key.getRow() - 1.0) / (rows - 1.0);
        if (position <= 0.20) {
            return "Front premium";
        }
        if (position <= 0.50) {
            return "Middle standard";
        }
        if (position <= 0.80) {
            return "Lower value";
        }
        return "Back value";
    }

    /**
     * Checks the contact details on their own, without booking anything.
     *
     * <p>Used when the details are collected in a dialog before the booking is
     * confirmed, so a missing email is reported on that dialog rather than after
     * the customer has already agreed to the purchase.
     *
     * <p>This reports only the FIRST problem it finds, which is fine because it
     * is a safety net. The form on screen checks every field at once with
     * CustomerDetails and marks each bad one, so nobody gets sent here one
     * problem at a time.
     *
     * @throws IllegalArgumentException if the details cannot be used
     */
    public void validateCustomer(String customerName, String email, String phone) {
        String name = customerName == null ? "" : customerName.trim();
        String address = email == null ? "" : email.trim();
        String number = phone == null ? "" : phone.trim();
        // Reports the first problem, and only ever as a last resort: the form
        // checks every field up front with CustomerDetails, so a customer is not
        // sent here one problem at a time. This stays as the guarantee that no
        // booking is ever taken with details the form would have refused.
        if (name.length() < 2) {
            throw new IllegalArgumentException("Please enter your name");
        }
        java.util.Map<CustomerDetails.Field, CustomerDetails.Problem> problems =
                CustomerDetails.check(name, address, number);
        if (!problems.isEmpty()) {
            CustomerDetails.Problem first =
                    CustomerDetails.problemsInOrder(problems).get(0);
            throw new IllegalArgumentException(first.getMessage());
        }
    }

    public synchronized Booking book(String customerName,
                                      String email,
                                      String phone,
                                      List<Seat> selectedSeats) {
        if (activeEvent == null) {
            throw new IllegalArgumentException("Choose an event before booking");
        }
        if (!isBookingOpen(activeEvent)) {
            throw new IllegalArgumentException(getBookingRestrictionMessage(activeEvent));
        }

        String normalizedName = customerName == null ? "" : customerName.trim();
        String normalizedEmail = email == null ? "" : email.trim();
        String normalizedPhone = phone == null ? "" : phone.trim();

        // Same rules as validateCustomer, which is called before this when the details
        // are collected in a dialog. Checked again here because this method is
        // public and can be called without that dialog.
        validateCustomer(customerName, email, phone);
        if (selectedSeats == null || selectedSeats.isEmpty()) {
            throw new IllegalArgumentException("Select at least one seat");
        }

        Set<SeatKey> selectedKeys = new LinkedHashSet<>();
        for (Seat seat : selectedSeats) {
            if (seat == null || !seatInventory.containsKey(seat.getKey())) {
                throw new IllegalArgumentException("One of the selected seats is not available");
            }
            if (!isSeatSelectable(seat.getKey())) {
                // Say why, so the customer knows whether to pick a different seat
                // or to wait. "Not selectable" on its own told them nothing.
                throw new IllegalArgumentException(unavailableReason(seat.getKey()));
            }
            selectedKeys.add(seat.getKey());
        }
        if (selectedKeys.size() > MAX_SEATS_PER_BOOKING) {
            throw new IllegalArgumentException("A reservation can contain up to "
                    + MAX_SEATS_PER_BOOKING + " seats");
        }
        for (SeatKey key : selectedKeys) {
            if (isBooked(key)) {
                throw new IllegalArgumentException("Seat " + key.display() + " has just been booked");
            }
        }

        // Customer identity is stored for the receipt and history only. There is intentionally
        // no limit on how many reservations one person may create.
        // Put the seats in order before saving, so the receipt always reads
        // "A1, A2, B4" rather than whichever order they were clicked in.
        List<SeatKey> keys = new ArrayList<>(selectedKeys);
        Collections.sort(keys);

        // Turn the seat addresses back into Seat objects, which is what holds
        // the prices, and then work out what they come to in total.
        List<Seat> chosenSeats = new ArrayList<>();
        for (SeatKey key : keys) {
            chosenSeats.add(seatInventory.get(key));
        }
        double total = getTotalCharge(chosenSeats);
        String reference = nextReference();
        Booking booking = new Booking(reference, activeEvent.getStadiumId(), activeEvent.getId(),
                activeEvent.getHeadline(), normalizedName, normalizedEmail, normalizedPhone,
                keys, total, Instant.now(), activeEvent.getDate(), activeEvent.getStartTime());
        bookings.add(booking);
        if (store != null) {
            try {
                store.save(booking);
            } catch (IOException | RuntimeException exception) {
                // The reservation never happened, so it must not linger in memory
                // and must never be reported to the customer as confirmed.
                bookings.remove(booking);
                updateNextReferenceNumber();
                if (exception instanceof BookingStore.SeatAlreadyBookedException) {
                    throw new IllegalArgumentException(
                            "One of those seats has just been booked by someone else. "
                                    + "Please pick again.");
                }
                throw new IllegalStateException(
                        "The booking could not be saved, so it has been cancelled. "
                                + "Please try again.", exception);
            }
        }
        return booking;
    }

    /**
     * Saves the seats somebody has chosen so they can come back to them later.
     *
     * <p>This is not a booking and the seats are not held, so the screen says so.
     * Without it there is no way to note down a choice and finish later.
     *
     * @param label a name the customer will recognise it by
     * @return the saved selection
     */
    public BookingStore.SavedSelection saveSelection(String label, List<Seat> seats) {
        if (activeEvent == null) {
            throw new IllegalArgumentException("Choose an event before saving a selection");
        }
        if (seats == null || seats.isEmpty()) {
            throw new IllegalArgumentException("Choose at least one seat to save");
        }
        // An over-long label is refused rather than shortened. It used to be cut
        // to 120 characters here without saying so, so a customer could type a
        // name, watch it save, and find part of it had gone. The form checks
        // this first and asks for a shorter one, so this is the guarantee that
        // nothing over the limit is ever stored.
        String labelProblem = FormRules.labelProblem(label);
        if (labelProblem != null) {
            throw new IllegalArgumentException(labelProblem);
        }
        String clean = FormRules.trimLabel(label);
        List<SeatKey> keys = new ArrayList<>();
        for (Seat seat : seats) {
            keys.add(seat.getKey());
        }
        String id = "SS-" + UUID.randomUUID().toString().replace("-", "")
                .substring(0, 8).toUpperCase(Locale.ENGLISH);
        BookingStore.SavedSelection selection = new BookingStore.SavedSelection(id, clean,
                activeEvent.getStadiumId(), activeEvent.getId(), activeEvent.getHeadline(),
                keys, getTotalCharge(seats), Instant.now());
        if (store != null) {
            try {
                store.saveSelection(selection);
            } catch (IOException exception) {
                throw new IllegalStateException(
                        "That selection could not be saved. Please try again.", exception);
            }
        }
        return selection;
    }

    /** Every saved selection, newest first. */
    public List<BookingStore.SavedSelection> getSavedSelections() {
        return store == null ? new ArrayList<>() : store.readSelections();
    }

    /**
     * Whether a saved selection can still be booked: the event must still be open
     * and every seat still on sale.
     */
    public String whySelectionCannotBeUsed(BookingStore.SavedSelection selection) {
        if (selection == null) {
            return "That selection is no longer saved";
        }
        StadiumEvent event = StadiumData.getEvent(selection.getEventId());
        if (event == null) {
            return "The event is no longer on the schedule";
        }
        if (!isBookingOpen(event)) {
            return getBookingRestrictionMessage(event);
        }
        List<String> taken = new ArrayList<>();
        for (SeatKey key : selection.getSeats()) {
            if (!isSeatSelectable(key)) {
                taken.add(key.display());
            }
        }
        if (!taken.isEmpty()) {
            return "Since you saved these, " + String.join(", ", taken)
                    + (taken.size() == 1 ? " has" : " have") + " been booked";
        }
        return null;
    }

    /** Forgets a saved selection. */
    public boolean deleteSavedSelection(String id) {
        return store != null && store.deleteSelection(id);
    }

    /** Cancels a reservation and returns its seats to that event's inventory. */
    public synchronized boolean cancel(String reference) {
        if (reference == null) {
            return false;
        }
        for (Booking booking : bookings) {
            if (booking.getReference().equals(reference) && booking.isConfirmed()) {
                booking.cancel();
                if (store != null) {
                    try {
                        // Saving the cancelled row also releases its seat rows, so the
                        // seats return to the pool for everyone.
                        store.save(booking);
                    } catch (IOException | RuntimeException exception) {
                        booking.restoreConfirmed();
                        throw new IllegalStateException(
                                "The cancellation could not be saved. Please try again.", exception);
                    }
                }
                return true;
            }
        }
        return false;
    }

}
