package com.voici.client;

import android.util.Log;
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
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
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
        HttpConnectionParams.setConnectionTimeout(params, 60000);
        HttpConnectionParams.setSoTimeout(params, 60000);

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

        ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
        httpClient = new DefaultHttpClient(cm, params);
    }

    public InputStream executeEnroll(String url, String apiKey, String[] filePaths) {
        InputStream resp = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            MultipartEntity mpEntity = new MultipartEntity();
            mpEntity.addPart("apikey", new StringBody(apiKey));

            for (int i = 0; i < filePaths.length; i++) {
                ContentBody cbFile = new FileBody(new File(filePaths[i]), "audio/wav");  // ??? audio/x-wav
                mpEntity.addPart("file" + i, cbFile);
            }


            httpPost.setEntity(mpEntity);
            Log.d("voici", "executing request " + httpPost.getRequestLine());

            HttpResponse response = httpClient.execute(httpPost);
            int code = response.getStatusLine().getStatusCode();
            Log.d("voici", "response code = " + code);
            if (code == HttpURLConnection.HTTP_OK) {

                HttpEntity resEntity = response.getEntity();
                System.out.println(response.getStatusLine());
                if (resEntity != null) {
                    //resp = EntityUtils.toString(resEntity);
                    //System.out.println(resp);
                    resp = resEntity.getContent();

                }
//                if (resEntity != null) {
//                    resEntity.consumeContent();
//                }
            }else{
                 resp = new ByteArrayInputStream(("<?xml version=\"1.0\" encoding=\"UTF-8\"?><EnrollVerify Status=\"ERROR\">Voice upload failed</EnrollVerify>").getBytes("UTF-8"));
            }

        } catch (IOException e) {
            Log.d("voici", "Enroll Error " + e.getMessage());
            resp = new ByteArrayInputStream(("<?xml version=\"1.0\" encoding=\"UTF-8\"?><EnrollVerify Status=\"ERROR\">Voice upload failed</EnrollVerify>").getBytes());
        }
//        } finally {
//            httpClient.getConnectionManager().shutdown();
//        }

        return resp;
    }

    public InputStream executeEnrollVerify(String url, String apiKey, String id, String filePath) {
        InputStream resp = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            MultipartEntity mpEntity = new MultipartEntity();
            mpEntity.addPart("apikey", new StringBody(apiKey));
            mpEntity.addPart("ID", new StringBody(id));
            mpEntity.addPart("file1" , new FileBody(new File(filePath), "audio/wav"));

            httpPost.setEntity(mpEntity);
            Log.d("voici", "executing request " + httpPost.getRequestLine());

            HttpResponse response = httpClient.execute(httpPost);
            int code = response.getStatusLine().getStatusCode();
            Log.d("voici", "response code = " + code);
            if (code == HttpURLConnection.HTTP_OK) {

                HttpEntity resEntity = response.getEntity();
                System.out.println(response.getStatusLine());
                if (resEntity != null) {
                    resp = resEntity.getContent();

                }
            }else{
                resp = new ByteArrayInputStream(("<?xml version=\"1.0\" encoding=\"UTF-8\"?><EnrollVerify Status=\"ERROR\">Voice verification failed</EnrollVerify>").getBytes("UTF-8"));
            }

        } catch (IOException e) {
            Log.d("voici", "Enroll Verify Error " + e.getMessage());
            resp = new ByteArrayInputStream(("<?xml version=\"1.0\" encoding=\"UTF-8\"?><EnrollVerify Status=\"ERROR\">Voice verification failed</EnrollVerify>").getBytes());
        }
        return resp;
    }

}
