package org.victoria2.tools.vic2sgea.main;

import javafx.scene.paint.Color;

import java.util.Locale;

/**
 * @author minetron
 */
public class Wrapper {

    static public <T extends Number> String toKMG(T value) {
        double inline = value.doubleValue();
        if (inline < 1000) return String.format(Locale.US, "%.3f", inline);
        int exp = (int) (Math.log(inline) / Math.log(1000));
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format(Locale.US, "%.3f %s", inline / Math.pow(1000, exp), pre);
    }

    public static float fromKMG(String in) {
        if (in == null || in.isEmpty()) return Float.NaN;
        int size = in.length();
        char postfix = in.substring(size - 1, size).charAt(0);

        long multiplier = 1;
        switch (postfix) {
            case 'K':
                multiplier = 1000;
                break;
            case 'M':
                multiplier = 1000000;
                break;
            case 'G':
                multiplier = 1000000000;
                break;
            case 'T':
                multiplier = 1000000000000L;
                break;
            case 'P':
                multiplier = 1000000000000000L;
                break;
            case 'E':
                multiplier = 1000000000000000000L;
                break;
        }
        return Float.valueOf(in.replaceAll("[^0-9.]", "")) * multiplier;
    }

    public static String toPercentage(float in) {
        return String.format(Locale.US, "%.2f%s", in, "%");
    }

    public static float fromPercentage(String in) {
        if (in == null || in.isEmpty()) return Float.NaN;

        //replace any irrelevant characters
        return Float.valueOf(in.replaceAll("[^0-9.]", ""));
    }

    public static String toWebColor(Color color) {

        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}
