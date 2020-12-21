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

import java.util.HashMap;
import java.util.Map;

public class Row {

    private final Map<String, Object> fields = new HashMap<>();

    public void addField(String column, Object value) {
        fields.put(column.toLowerCase(), value);
    }

    public Object getField(String name) {
        return fields.get(name);
    }

    public Map<String, Object> getAllFields(){
        return fields;
    }

    public boolean hasField(String key){
        return getField(key) != null;
    }

}
