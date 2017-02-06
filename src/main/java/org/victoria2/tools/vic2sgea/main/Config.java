package org.victoria2.tools.vic2sgea.main;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * By Anton Krylov (anthony.kryloff@gmail.com)
 * Date: 2/6/17 4:15 PM
 */
public class Config extends java.util.Properties {

    private static final String FILE_PATH = "./path.txt";

    public void load() throws IOException {
        InputStream in = new FileInputStream(FILE_PATH);
        load(in);
    }

    public Config() throws IOException {
        load();
    }

    public void save() throws IOException {
        FileOutputStream out = new FileOutputStream(FILE_PATH);
        store(out, null);
    }
}
