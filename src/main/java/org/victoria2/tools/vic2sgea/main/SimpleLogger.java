package org.victoria2.tools.vic2sgea.main;

/**
 * By Anton Krylov (anthony.kryloff@gmail.com)
 * Date: 2/8/17 12:35 PM
 */
public class SimpleLogger {

    public void log(String msg) {
        System.out.println(msg);
    }

    public void logMemoryUsage() {
        System.out.println("Memory usage: " + Wrapper.toKMG(Helpers.getMemoryUsage()));
    }
}
