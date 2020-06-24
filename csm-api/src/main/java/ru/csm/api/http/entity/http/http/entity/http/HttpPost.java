package ru.csm.api.http.entity.http.http.entity.http;

import org.apache.commons.io.IOUtils;
import ru.csm.api.http.entity.http.http.entity.http.entity.HttpEntity;
import ru.csm.api.http.entity.http.http.entity.http.entity.HttpResponse;
import ru.csm.api.http.entity.http.http.entity.http.entity.RequestMethod;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.io.IOException;

public class HttpPost extends HttpRequest {

    private HttpsURLConnection connection;
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
            line = IOUtils.toString(connection.getInputStream(), "UTF-8");
        }

        return new HttpResponse(responseCode, line);
    }
}
