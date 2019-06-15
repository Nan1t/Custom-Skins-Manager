package ru.csm.api;

public class WhiteListElement {

    private String permission;
    private String value;

    public WhiteListElement(String value){
        this.value = value;
    }

    public WhiteListElement(String value, String permission){
        this.value = value;
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }

    public String getValue() {
        return value;
    }

    public boolean hasPermission(){
        return permission != null;
    }
}
