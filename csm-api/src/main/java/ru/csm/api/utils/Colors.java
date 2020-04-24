package ru.csm.api.utils;

import java.util.ArrayList;
import java.util.List;

public class Colors {

    public static String of(String text){
        return text == null ? null : text.replace("&", "§");
    }

    public static List<String> ofArr(String... text){
        List<String> list = new ArrayList<>();
        for(String str : text){
            list.add(of(str));
        }
        return list;
    }

    public static List<String> ofArr(Iterable<String> text){
        List<String> list = new ArrayList<>();
        for(String str : text){
            list.add(of(str));
        }
        return list;
    }

}
