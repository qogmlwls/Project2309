package com.example.project2309.network;


import android.content.Context;
import android.util.Log;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class CookieInterceptor implements Interceptor {

    private SharedPreferencesManager sharedPreferencesManager;

    public CookieInterceptor(Context context) {
        this.sharedPreferencesManager = new SharedPreferencesManager(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder requestBuilder = chain.request().newBuilder();

        // 여기에서 저장된 쿠키를 가져와 설정
        String savedCookie = sharedPreferencesManager.getSessionCookie();
        if (savedCookie != null) {
            requestBuilder.addHeader("Cookie", savedCookie);
            Log.i("savedCookie",savedCookie);
        }
        else{
            Log.i("savedCookie","null");

        }

        return chain.proceed(requestBuilder.build());
    }
}