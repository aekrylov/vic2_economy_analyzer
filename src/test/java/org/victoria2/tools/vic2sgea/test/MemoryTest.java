package org.victoria2.tools.vic2sgea.test;

import org.victoria2.tools.vic2sgea.main.Report;

/**
 * By Anton Krylov (anthony.kryloff@gmail.com)
 * Date: 2/7/17 8:02 PM
 */
public class MemoryTest {

    public static void main(String[] args) {
        String saveName = "/media/anth/Win/Users/anth/Documents/Paradox Interactive/Victoria II/save games/CNGreat6.2.v2";

        Runtime runtime = Runtime.getRuntime();

        long memoryUsage = 0;
        int TRIES = 10;

        long timeFilter = 0;
        System.out.println("Testing with filter");
        for (int i = 0; i < TRIES; i++) {
            runtime.gc();
            long startTime = System.nanoTime();
            Report report = new Report(saveName, null, null, true);
            memoryUsage += runtime.totalMemory() - runtime.freeMemory();
            timeFilter += System.nanoTime() - startTime;
        }

        long memUsageFilter = memoryUsage / TRIES;

        memoryUsage = 0;
        long timeNoFilter = 0;
        System.out.println("Testing without filter");
        for (int i = 0; i < TRIES; i++) {
            runtime.gc();
            long startTime = System.nanoTime();
            Report report = new Report(saveName, null, null, false);
            memoryUsage += runtime.totalMemory() - runtime.freeMemory();
            timeNoFilter += System.nanoTime() - startTime;
        }
        long memUsageNoFilter = memoryUsage / TRIES;

        System.err.println();
        System.err.println("Avg memory usage with filter: " + memUsageFilter);
        System.out.println("timeFilter = " + timeFilter);
        System.err.println("Avg mem usage without filter: " + memUsageNoFilter);
        System.out.println("timeNoFilter = " + timeNoFilter);
    }
}
