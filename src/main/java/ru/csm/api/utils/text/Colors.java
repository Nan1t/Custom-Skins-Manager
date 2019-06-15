package ru.csm.api.utils.text;

import java.util.ArrayList;
import java.util.List;

public class Colors {

    public static String of(String text){
        if(text == null){
            return " ";
        }

        return text.replace("&", "§");
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
