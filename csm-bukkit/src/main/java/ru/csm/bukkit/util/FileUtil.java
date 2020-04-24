package ru.csm.bukkit.util;

import java.io.InputStream;
import java.util.Scanner;

public final class FileUtil {

    private FileUtil(){}

    public static String readResourceContent(String sqlFile){
        InputStream in = FileUtil.class.getResourceAsStream(sqlFile);
        Scanner scanner = new Scanner(in);
        StringBuilder builder = new StringBuilder();

        while (scanner.hasNextLine()){
            builder.append(scanner.nextLine());
        }

        return builder.toString();
    }


}
