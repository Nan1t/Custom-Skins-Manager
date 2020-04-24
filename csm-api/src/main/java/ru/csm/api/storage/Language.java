package ru.csm.api.storage;

import ru.csm.api.utils.Colors;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
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
