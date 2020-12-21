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

package ru.csm.api.http.entity.http.http.entity.http;

import ru.csm.api.http.entity.http.http.entity.http.entity.HttpEntity;
import ru.csm.api.http.entity.http.http.entity.http.entity.HttpResponse;

import java.io.IOException;
import java.net.URL;

public abstract class HttpRequest {

    private URL url;
    private int timeout;
    private String entity;

    HttpRequest(String url) throws IOException {
        this.url = new URL(url);
    }

    public URL getUrl(){
        return url;
    }

    public int getTimeout(){
        return timeout;
    }

    public void setTimeout(int timeout){
        this.timeout = timeout;
    }

    public abstract void setEntity(HttpEntity entity);

    public abstract void addHeader(String key, String value);

    public abstract HttpResponse execute() throws IOException;

}
