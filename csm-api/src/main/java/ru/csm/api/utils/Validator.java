package ru.csm.api.utils;

import java.util.regex.Pattern;

public class Validator {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._]{3,16}$");

    public static boolean validateURL(String url){
        return (url.startsWith("http://") || url.startsWith("https://"));
    }

    public static boolean validateName(String name){
        return NAME_PATTERN.matcher(name).matches();
    }

}
