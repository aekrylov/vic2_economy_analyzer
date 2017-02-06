package org.victoria2.tools.vic2sgea.main;

import java.io.IOException;

/**
 * Created by anth on 05.02.2017.
 */
public class Properties {

    private java.util.Properties classPathProperties = new java.util.Properties();

    public Properties() {
        try {
            classPathProperties.load(getClass().getResourceAsStream("/build.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getVersion() {
        return classPathProperties.getProperty("build.version");
    }
}
