package ru.csm.api;

import ru.csm.api.logging.Logger;

import java.net.MalformedURLException;
import java.net.URL;

public enum Dependency {

    DBCP(
            "commons-dbcp2-2.8.0.jar",
            "https://repo1.maven.org/maven2/org/apache/commons/commons-dbcp2/2.8.0/commons-dbcp2-2.8.0.jar"
    ),
    H2(
            "h2-1.4.200.jar",
            "https://repo1.maven.org/maven2/com/h2database/h2/1.4.200/h2-1.4.200.jar"
    );

    private final String name;
    private final String url;

    Dependency(String name, String url){
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public URL getUrl() {
        try {
            return new URL(this.url);
        } catch (MalformedURLException e){
            Logger.severe("Cannot convert string to URL: " + e.getMessage());
        }
        return null;
    }
}
