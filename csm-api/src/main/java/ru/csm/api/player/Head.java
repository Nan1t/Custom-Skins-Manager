package ru.csm.api.player;

public class Head {

    private final String owner;
    private final String url;

    public Head(String owner, String url) {
        this.owner = owner;
        this.url = url;
    }

    public String getOwner() {
        return owner;
    }

    public String getUrl() {
        return url;
    }
}
