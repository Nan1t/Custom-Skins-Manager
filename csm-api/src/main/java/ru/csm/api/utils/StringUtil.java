package ru.csm.api.utils;

import ru.csm.api.logging.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class StringUtil {

    private StringUtil(){ }

    public static String streamToString(InputStream stream){
        try {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = stream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString(StandardCharsets.UTF_8.name());
        } catch (IOException e){
            Logger.severe("Cannot read string from input stream: ", e);
            return null;
        }
    }

}
