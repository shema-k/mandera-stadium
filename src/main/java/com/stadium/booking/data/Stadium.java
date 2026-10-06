package com.stadium.booking.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * One venue: its name, where it is, how big it is, and how its seats are
 * divided up.
 *
 * <p>An object like this is called a "class". It holds facts about one thing and
 * gives you ways to read them. The fields at the top are the facts; the methods
 * below are the ways to read them.
 *
 * <p>Nothing here decides anything. The rules about prices and bookings live in
 * BookingService. This class only knows what is true about the stadium.
 */
public final class Stadium {
    private final String id;
    private final String name;
    private final String city;
    private final String country;
    private final String venueType;
    private final String description;
    private final String address;
    private final int capacity;
    private final String accentColor;
    private final StadiumShape shape;
    private final List<SeatSection> sections;

    /**
     * A shorter way to make a stadium, used only by the old compact demo.
     *
     * <p>It works out the shape and the seating sections itself. Everything else
     * should use the longer constructor below, where those two are given
     * explicitly.
     */
    public Stadium(String id,
                   String name,
                   String city,
                   String country,
                   String venueType,
                   String description,
                   String address,
                   int capacity,
                   String accentColor) {
        this(id, name, city, country, venueType, description, address, capacity, accentColor,
                StadiumShape.BOX, defaultSections(capacity));
    }

    /**
     * The main way to make a stadium.
     *
     * <p>Everything the stadium needs is passed in as an argument. There are two
     * things worth knowing about what happens next.
     *
     * <p><b>1. Nothing is allowed to be missing.</b> Objects.requireNonNull stops
     * the program immediately with a clear message if one of the text fields is
     * null, rather than letting it fail later in a confusing place.
     *
     * <p><b>2. The capacity has to agree with the sections.</b> The capacity is
     * the number printed on the tickets, and the sections are the rows of seats
     * we actually draw. If they disagree then one of them is a mistake, so we
     * refuse to start rather than sell a number of seats we cannot deliver. This
     * is why changing a section size in StadiumData without changing the
     * capacity will stop the program with a message.
     *
     * <p>The sections are copied into a new list that nobody else can change, so
     * nothing can alter the stadium after it has been made.
     */
    public Stadium(String id,
                   String name,
                   String city,
                   String country,
                   String venueType,
                   String description,
                   String address,
                   int capacity,
                   String accentColor,
                   StadiumShape shape,
                   List<SeatSection> sections) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
        this.city = Objects.requireNonNull(city, "city");
        this.country = Objects.requireNonNull(country, "country");
        this.venueType = Objects.requireNonNull(venueType, "venueType");
        this.description = Objects.requireNonNull(description, "description");
        this.address = Objects.requireNonNull(address, "address");
        this.capacity = capacity;
        this.accentColor = Objects.requireNonNull(accentColor, "accentColor");
        this.shape = Objects.requireNonNull(shape, "shape");
        this.sections = Collections.unmodifiableList(new ArrayList<>(Objects.requireNonNull(sections, "sections")));
        if (this.sections.isEmpty()) {
            throw new IllegalArgumentException("A stadium must have at least one section");
        }
        // Add up every seat in every section, then check it against the
        // capacity we were promised. Done with a loop rather than a shortcut
        // so it is easy to follow.
        int calculatedCapacity = 0;
        for (SeatSection section : this.sections) {
            calculatedCapacity = calculatedCapacity + section.getSeatCount();
        }

        if (calculatedCapacity != capacity) {
            throw new IllegalArgumentException("Stadium capacity does not match its seating sections");
        }
    }

    /**
     * Works out four seating sections that add up to exactly the capacity.
     *
     * <p>Used by the short constructor above. The real stadium is built with
     * its sections written out, so this is only here for the compact demo.
     *
     * <p>It works in three steps.
     *
     * <p><b>Step 1: share the capacity out equally.</b> Each section takes its
     * share of what is left. A capacity of 100 becomes 25, 25, 25, 25.
     *
     * <p><b>Step 2: deal with anything left over.</b> Dividing does not always
     * come out even, so whatever is left over is given to the last section. That
     * is why the total always comes out right.
     *
     * <p><b>Step 3: turn each number into rows and seats.</b> A section of 100
     * becomes 10 rows of 10. We try the square root first because 10 by 10 looks
     * right, then count down until the number divides evenly, so we never get
     * half a row.
     */
    private static List<SeatSection> defaultSections(int capacity) {
        if (capacity < 4) {
            throw new IllegalArgumentException("A stadium must have at least four seats");
        }

        // Step 1: share the capacity between the four sections.
        int[] targets = new int[4];
        int remaining = capacity;

        for (int index = 0; index < targets.length; index++) {
            int sectionsLeft = targets.length - index;
            targets[index] = Math.max(1, remaining / sectionsLeft);
            remaining = remaining - targets[index];
        }

        // Step 2: anything that did not divide evenly goes to the last section.
        targets[targets.length - 1] = targets[targets.length - 1] + remaining;

        // Step 3: turn each target into a number of rows and seats per row.
        List<SeatSection> sections = new ArrayList<>();
        String[] labels = {"North end", "East side", "South end", "West side"};

        for (int index = 0; index < targets.length; index++) {
            int target = targets[index];

            // Start at the square root, which gives the squarest shape.
            int rows = Math.max(1, (int) Math.sqrt(target));

            // If it does not divide evenly, step down until it does. Without
            // this we could end up with 10 rows and 9.5 seats in each, which
            // would not be a whole seat.
            while (rows > 1 && target % rows != 0) {
                rows = rows - 1;
            }

            int seatsPerRow = target / rows;

            // (char) ('A' + index) turns 0 into A, 1 into B, and so on.
            sections.add(new SeatSection(String.valueOf((char) ('A' + index)), labels[index],
                    rows, seatsPerRow, 40.0));
        }

        return sections;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getVenueType() {
        return venueType;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public int getCapacity() {
        return capacity;
    }

    /** The real seat count, worked out by adding up all four sections. */
    public int getSeatCount() {
        int total = 0;

        for (SeatSection section : sections) {
            total = total + section.getSeatCount();
        }

        return total;
    }

    public String getAccentColor() {
        return accentColor;
    }

    public StadiumShape getShape() {
        return shape;
    }

    public String getShapeLabel() {
        return shape.getLabel();
    }

    public List<SeatSection> getSections() {
        return sections;
    }

    public SeatSection getSection(String sectionId) {
        if (sectionId == null) {
            return null;
        }
        for (SeatSection section : sections) {
            if (section.getId().equalsIgnoreCase(sectionId)) {
                return section;
            }
        }
        return null;
    }

    public String getLocation() {
        return city + ", " + country;
    }

    /**
     * Everything about this stadium as one long lower-case line of text.
     *
     * <p>The search box uses this. When someone types "namboole" or "kampala" or
     * "vip", we compare what they typed against this line. Putting all the words
     * together is simpler than comparing against each field separately.
     *
     * <p>Lower case is important: someone typing "VIP" should still match the
     * text "VIP Box".
     */
    public String searchableText() {
        // Gather what the sections themselves want to be searched by.
        String sectionText = "";

        for (SeatSection section : sections) {
            if (sectionText.length() > 0) {
                sectionText = sectionText + " ";
            }
            sectionText = sectionText + section.searchableText();
        }

        return String.join(" ", id, name, city, country, venueType, description, address,
                shape.getLabel(), sectionText).toLowerCase(Locale.ENGLISH);
    }

    /**
     * The first letter of the first word and the last word, for the little
     * badge in the corner of the stadium card.
     *
     * <p>"Mandela National Stadium (Namboole)" becomes "MN".
     *
     * <p>Punctuation is removed first, so a name such as "Hamz Stadium
     * (Nakivubo)" still gives "HS" rather than something like "(N".
     */
    public String initials() {
        // Split the name into words, then take out anything that is not a
        // letter or a number, then throw away anything that is now empty.
        List<String> words = new ArrayList<>();
        String[] parts = name.trim().split("\\s+");

        for (String part : parts) {
            String cleaned = part.replaceAll("[^\\p{L}\\p{N}]", "");
            if (cleaned.length() > 0) {
                words.add(cleaned);
            }
        }

        if (words.isEmpty()) {
            return "UG";
        }
        if (words.size() == 1) {
            String only = words.get(0);
            return only.substring(0, Math.min(2, only.length())).toUpperCase(Locale.ENGLISH);
        }
        return (words.get(0).substring(0, 1) + words.get(words.size() - 1).substring(0, 1))
                .toUpperCase(Locale.ENGLISH);
    }
}
