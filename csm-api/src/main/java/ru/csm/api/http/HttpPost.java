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

package ru.csm.api.http;

import ru.csm.api.http.entity.HttpEntity;
import ru.csm.api.http.entity.HttpResponse;
import ru.csm.api.http.entity.RequestMethod;
import ru.csm.api.utils.StringUtil;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.io.IOException;

public class HttpPost extends HttpRequest {

    private final HttpsURLConnection connection;
    private HttpEntity entity;

    public HttpPost(String url) throws IOException {
        super(url);
        connection = (HttpsURLConnection) getUrl().openConnection();
        connection.setRequestMethod(RequestMethod.POST.getMethod());
    }

    @Override
    public void addHeader(String key, String value){
        connection.setRequestProperty(key, value);
    }

    @Override
    public void setEntity(HttpEntity entity){
        this.entity = entity;
    }

    @Override
    public HttpResponse execute() throws IOException {
        connection.setConnectTimeout(getTimeout());
        connection.setReadTimeout(getTimeout());
        connection.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(entity.build());
        wr.flush();
        wr.close();

        int responseCode = connection.getResponseCode();
        String line = null;

        if(connection.getInputStream() != null){
            line = StringUtil.streamToString(connection.getInputStream());
        }

        return new HttpResponse(responseCode, line);
    }
}
