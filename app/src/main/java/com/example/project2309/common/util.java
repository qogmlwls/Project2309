package com.example.project2309.common;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.widget.Toast;

import com.google.gson.JsonObject;

public class util {

    public static <T> void startMyActivity(Activity activity, Class<T> activityClass) {
        Intent intent = new Intent(activity, activityClass);
        activity.startActivity(intent);
    }

}
