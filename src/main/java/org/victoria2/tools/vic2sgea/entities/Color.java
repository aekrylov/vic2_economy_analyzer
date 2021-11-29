package org.victoria2.tools.vic2sgea.entities;

/**
 * Duplicated a JavaFX class so that entities won't depend on JavaX
 *
 * By Anton Krylov (anthony.kryloff@gmail.com)
 * Date: 12/8/19 2:58 PM
 */
public class Color {

    private final double red;
    private final double green;
    private final double blue;

    public Color(double red, double green, double blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public double getRed() {
        return red;
    }

    public double getGreen() {
        return green;
    }

    public double getBlue() {
        return blue;
    }
}
