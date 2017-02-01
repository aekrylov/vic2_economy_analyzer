/**
 *
 */
package main;

import java.util.Locale;

/**
 * @author minetron
 */
public class Wrapper {

    static public String toKMG(long inline) {
        //, int maxlenght
        if (inline < 1000) return String.valueOf(inline);
        int exp = (int) (Math.log(inline) / Math.log(1000));
        String pre = String.valueOf("KMGTPE".charAt(exp - 1));
        return String.format(Locale.US, "%.3f %s", inline / Math.pow(1000, exp), pre);
    }

    static public String toKMG(float inline) {
        //, int maxlenght
        if (inline < 1000) return String.format(Locale.US, "%.3f", inline);
        int exp = (int) (Math.log(inline) / Math.log(1000));
        String pre = String.valueOf("KMGTPE".charAt(exp - 1));
        return String.format(Locale.US, "%.3f %s", inline / Math.pow(1000, exp), pre);
        //.replace(',', '.')
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
}
