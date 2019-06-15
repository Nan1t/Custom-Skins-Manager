package ru.csm.api.utils;

import java.util.UUID;

public class UuidUtil {

    public static UUID getUUID(String str){
        if(str.contains("-")){
            return UUID.fromString(str);
        }

        return UUID.fromString(str.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
    }

}
