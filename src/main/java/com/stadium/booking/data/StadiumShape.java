package com.stadium.booking.data;

/**
 * The shape of the ground, which decides how the seat map is drawn.
 *
 * <p>This is an "enum", short for enumeration: a fixed list of allowed values.
 * A stadium can only be one of these three, so writing the shape as a plain
 * piece of text would allow "hexagon" or an empty box, and every use of it would
 * have to cope with that. An enum makes those mistakes impossible to write.
 */
public enum StadiumShape {
    BOX("Box-shaped stadium"),
    OVAL("Oval stadium"),
    CIRCULAR("Circular stadium");

    private final String label;

    StadiumShape(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
