package com.stadium.booking.data;
/** The physical footprint used to present a stadium's seating bowl. */
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
