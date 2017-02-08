package org.victoria2.tools.vic2sgea.main;

/**
 * By Anton Krylov (anthony.kryloff@gmail.com)
 * Date: 2/8/17 12:37 PM
 */
public abstract class Helpers {

    private static Runtime runtime = Runtime.getRuntime();

    public static long getMemoryUsage() {
        return runtime.totalMemory() - runtime.freeMemory();
    }
}
