package ru.csm.api.storage.database;

import java.util.HashMap;
import java.util.Map;

public class Row {

    private Map<String, Object> fields = new HashMap<>();

    public void addField(String column, Object value) {
        fields.put(column.toLowerCase(), value);
    }

    public Object getField(String name) {
        return fields.get(name);
    }

    public Map<String, Object> getAllFields(){
        return fields;
    }

}
