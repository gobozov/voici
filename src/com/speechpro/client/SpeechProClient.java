package com.speechpro.client;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 16.01.13
 * Time: 1:43
 * To change this template use File | Settings | File Templates.
 */
public class SpeechProClient {


    private DefaultHttpClient httpClient = null;

    public SpeechProClient() {
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, 30000);
        HttpConnectionParams.setSoTimeout(params, 30000);

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

        ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
        httpClient = new DefaultHttpClient(cm, params);
    }

    private String execute(String url, String[] filePaths) {
//        setCredentials(login, password);
//        HttpGet get = new HttpGet(url.replaceAll("\\s", "+"));
//        try {
//            Log.d("MyShows", "Request = " + get.getRequestLine().toString());
//            HttpResponse response = httpClient.execute(get);
//            int code = response.getStatusLine().getStatusCode();
//            if (code == HttpURLConnection.HTTP_OK) {
//                InputStream stream = response.getEntity().getContent();
//                if (stream == null)
//                    Log.d("MyShows", "Response = null");
//                else
//                    Log.d("MyShows", "Response = " +  stream.available());
//                return stream;
//            } else {
//                Log.e("MyShows", "Wrong response code " + code + " for request " + get.getRequestLine().toString());
//                return null;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
        String resp = "";
        try {
            HttpPost httpPost = new HttpPost(url);
            MultipartEntity mpEntity = new MultipartEntity();
            for (int i = 0; i < filePaths.length; i++) {

                ContentBody cbFile = new FileBody(new File(filePaths[i]), "audio/wav");  // ??? audio/x-wav
                mpEntity.addPart("file" + i, cbFile);
            }


            httpPost.setEntity(mpEntity);
            System.out.println("executing request " + httpPost.getRequestLine());

            HttpResponse response = httpClient.execute(httpPost);
            int code = response.getStatusLine().getStatusCode();

            if (code == HttpURLConnection.HTTP_OK) {

                HttpEntity resEntity = response.getEntity();
                System.out.println(response.getStatusLine());
                if (resEntity != null) {
                    resp = EntityUtils.toString(resEntity);
                    System.out.println(resp);
                }
                if (resEntity != null) {
                    resEntity.consumeContent();
                }

            }else {


                return null;

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        return resp;


    }


}
