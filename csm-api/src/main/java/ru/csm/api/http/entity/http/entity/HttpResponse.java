package ru.csm.api.http.entity.http.entity;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private int code;
    private String response;
    private Map<String, String> headers = new HashMap<>();

    public HttpResponse(int code, String response){
        this.code = code;
        this.response = response;
    }

    public int getCode() {
        return code;
    }

    public String getResponse() {
        return response;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void addHeader(String key, String value){
        headers.put(key, value);
    }
}
