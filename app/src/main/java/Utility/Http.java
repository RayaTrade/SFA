package Utility;

import android.app.Activity;
import android.os.Looper;
import android.util.Log;




import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.params.HttpConnectionParams;
import cz.msebera.android.httpclient.protocol.HTTP;


public class Http {
    public interface RequestCallback {
        void onError(Throwable exception);
        void onResponseReceived(HttpResponse httpResponse, String response);


    }




    public static void httpPost(final String url,
                                final String json,
                                final RequestCallback callback)
    {
        Log.i("doPost a url: " + url, "- JSON: " + json);

        final HttpClient httpClient = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10000);
        final String CONTENT_TYPE_JSON="application/json";
        Thread thread = new Thread()
        {
            public void run()
            {   Looper.prepare();
                HttpPost httpPost = new HttpPost(url);
                httpPost.addHeader("Accept", CONTENT_TYPE_JSON);
                httpPost.addHeader("Content-Type", CONTENT_TYPE_JSON);
                try
                {
                    StringEntity entity = new StringEntity(json,
                            HTTP.UTF_8);

                    entity.setContentType(CONTENT_TYPE_JSON);
                    httpPost.setEntity(entity);
                    // execute is a blocking call, it's best to call this code
                    // in a thread separate from the ui's
                    HttpResponse response = httpClient.execute(httpPost);

                    callback.onResponseReceived(response,response.toString());




                }
                catch (Exception ex)
                {
                    callback.onError(ex);
                }
                Looper.loop();
            }
        };
        thread.start();
    }


    public static void httpGet(final String url, final Activity activity,
                               final RequestCallback callback)
    {
        Log.i("doGet", " - url: " + url);

        final HttpClient httpClient = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10000);
        try {


            final Thread thread = new Thread() {
                public void run() {
                    try {

                        Looper.prepare();

                        HttpGet httpget = new HttpGet(url);
                        HttpResponse response;

                        response = httpClient.execute(httpget);
                        callback.onResponseReceived(response, response.toString());


                        Looper.loop();


                    } catch (Exception ex) {
                        callback.onError(ex);


                    }
                }
            };
            thread.start();
        }catch (Exception EX){}
    }

    public static String ReadResponse(HttpResponse response)
    {
        try{
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent(); // Create an InputStream with the response
            // BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) // Read line by line
                sb.append(line + "\n");

            return  (sb.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
