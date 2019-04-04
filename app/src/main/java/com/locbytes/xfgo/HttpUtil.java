package com.locbytes.xfgo;

import android.util.Log;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {

    public static String get(String url) {
        final OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder().url(url).build();
        String responseBody = null;
        try {
            Response response = okHttpClient.newCall(request).execute();
            responseBody = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseBody;
    }

    public static void post(String url, String requestBody) {
        final OkHttpClient okHttpClient = new OkHttpClient();
        MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, requestBody);
        final Request request = new Request.Builder().url(url).post(body).build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = okHttpClient.newCall(request).execute();
                    if (!response.isSuccessful()) {
                        Log.d("okhttp3 post error", response.code() + " " + response.message());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

}
