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

package ru.csm.api.storage;

import com.google.common.reflect.TypeToken;
import ninja.leaping.modded.configurate.objectmapping.ObjectMappingException;
import ru.csm.api.utils.Colors;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Language {

    private Configuration lang;

    public Configuration getLang(){
        return lang;
    }

    public Language(Object plugin, Path path, String code) throws IOException {
        lang = new Configuration(code + ".conf", path, plugin);
    }

    public String of(String key) {
        return Colors.of(lang.get().getNode(key).getString(key));
    }

    public List<String> ofList(String key) {
        try{
            List<String> list = lang.get().getNode(key).getList(TypeToken.of(String.class), Collections.singletonList(key));
            return Colors.ofArr(list);
        } catch (ObjectMappingException e){
            return Collections.singletonList(key);
        }
    }


    public String[] ofArray(String key){
        try{
            List<String> list = (ArrayList<String>)lang.get().getNode(key).getValue();
            String[] array = new String[list.size()];

            for(int i = 0; i < list.size(); i++){
                array[i] = Colors.of(list.get(i));
            }

            return array;
        } catch (Exception e){
            e.printStackTrace();
        }

        return new String[]{ key };
    }

}
