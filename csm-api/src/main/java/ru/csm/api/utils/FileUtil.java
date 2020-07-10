/*
 * Custom Skins Manager
 * Copyright (C) 2020  Nanit
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.csm.api.utils;

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
