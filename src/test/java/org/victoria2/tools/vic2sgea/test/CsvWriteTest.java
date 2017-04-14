package org.victoria2.tools.vic2sgea.test;

import org.victoria2.tools.vic2sgea.watcher.CsvExporter;
import org.victoria2.tools.vic2sgea.watcher.Watcher;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * By Anton Krylov (anthony.kryloff@gmail.com)
 * Date: 4/14/17 2:49 PM
 */
public class CsvWriteTest {

    public static void main(String[] args) throws IOException {
        Watcher watcher = new Watcher(
                Paths.get("/media/anth/Win/Documents and Settings/anth/Documents/sample1.json"),
                Paths.get("/media/anth/Win/Documents and Settings/anth/Documents")
        );

        String tag = "CHI";
        Path csv = Paths.get("/home/anth/csv1.csv");

        CsvExporter.exportCountry(watcher.getWatch(), tag, csv);
    }
}
