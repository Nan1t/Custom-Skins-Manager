package ru.csm.api.http.entity;

public enum RequestMethod {

    GET("GET"),
    POST("POST"),
    PUT("PUT");

    private String method;

    RequestMethod(String method){
        this.method = method;
    }

    public String getMethod(){
        return method;
    }

}
