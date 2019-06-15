package ru.csm.api.utils.text;

public class Validator {

    public static boolean validateURL(String url){
        return (url.startsWith("http://") || url.startsWith("https://"));
    }

    public static boolean validateName(String name){
        return name.matches("^[a-zA-Z0-9._]{3,16}$");
    }

}
