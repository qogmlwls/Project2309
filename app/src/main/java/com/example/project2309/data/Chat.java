package com.example.project2309.data;

import android.net.Uri;

public class Chat {


    public boolean isLastChat;
    public static int MYCHAT = 0;
    public static int OTHERCHAT = 1;


    public static String TEXT = "text";
    public static String IMAGE = "image";

    public static String VIDEO = "video";


    public int type;

    public String mediaType;
    public String imagePath;

    public String ThumbnailPath;
    public String videoPath;

    public Uri imageUri;

    public Uri videoUri;


    public int chattingId;


    public int requestId;
    public String profile;
    public String name;
    //    String yearmonth;
    public String date;
    public String message;

    public Boolean UnReadChat;
    public Boolean result;

    public Chat(){
        isLastChat = false;

    }

}
