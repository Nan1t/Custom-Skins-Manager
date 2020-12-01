package ru.csm.api;

import java.net.MalformedURLException;
import java.net.URL;

public class Dependency {

    private final String name;
    private final String url;

    public Dependency(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public URL getUrl() {
        try {
            return new URL(this.url);
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
        return null;
    }

    public String getName() {
        return name;
    }
}
