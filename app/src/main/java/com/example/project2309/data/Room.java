package com.example.project2309.data;

import android.app.Application;

import com.example.project2309.network.SharedPreferencesManager;

public class Room {

    public int room_id;

    public int fromPk;
    public String fromName;
    public String fromProfile;

//    public String last_chat_media_type;
//    public String last_chat_image_path;
//


    public String last_chat_message;
    public String last_chat_date;
    public int Unread_chat_cnt;


//    public boolean isNotiOn;

//    public Room(Application application){
//        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(application.getApplicationContext());
//        isNotiOn = sharedPreferencesManager.getRoomNotiOn(room_id);
//    }

}
