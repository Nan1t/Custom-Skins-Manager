package ru.csm.api.http.entity.http.http.entity.http;

import ru.csm.api.http.entity.http.http.entity.http.entity.HttpEntity;
import ru.csm.api.http.entity.http.http.entity.http.entity.HttpResponse;

import java.io.IOException;
import java.net.URL;

public abstract class HttpRequest {

    private URL url;
    private int timeout;
    private String entity;

    HttpRequest(String url) throws IOException {
        this.url = new URL(url);
    }

    public URL getUrl(){
        return url;
    }

    public int getTimeout(){
        return timeout;
    }

    public void setTimeout(int timeout){
        this.timeout = timeout;
    }

    public abstract void setEntity(HttpEntity entity);

    public abstract void addHeader(String key, String value);

    public abstract HttpResponse execute() throws IOException;

}
