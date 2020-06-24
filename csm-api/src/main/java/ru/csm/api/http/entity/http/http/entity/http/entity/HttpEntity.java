package ru.csm.api.http.entity.http.http.entity.http.entity;

import java.util.HashMap;
import java.util.Map;

public class HttpEntity {

    private Map<String, String> urlEncodeParams = new HashMap<>();
    private String simpleLine;

    public HttpEntity(){}

    public HttpEntity(String line){
        this.simpleLine = line;
    }

    public void addParam(String key, String value){
        urlEncodeParams.put(key, value);
    }

    public String build(){
        if(simpleLine != null){
            return simpleLine;
        }

        simpleLine = "";

        for(Map.Entry<String, String> entry : urlEncodeParams.entrySet()){
            simpleLine += entry.getKey() + "=" + entry.getValue() + "&";
        }

        simpleLine = simpleLine.substring(0, simpleLine.length()-1);

        return simpleLine;
    }

}
