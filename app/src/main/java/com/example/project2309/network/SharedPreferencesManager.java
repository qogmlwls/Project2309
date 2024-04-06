package com.example.project2309.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;


public class SharedPreferencesManager {

    String TAG = "SharedPreferencesManager";
    private static final String PREF_NAME = "MyAppPrefs";
    private static final String KEY_SESSION_COOKIE = "sessionCookie";

    private static final String KEY_SESSION_PK = "sessionPk";
    private static final String KEY_SESSION_RoomPk = "sessionRoomPk";

    private static final String KEY_SESSION_Chatting = "sessionChatting";


    private static final String KEY_SESSION_Post = "sessionPost";
    private static final String KEY_SESSION_Reply = "sessionReply";
    private static final String KEY_SESSION_Rereply = "sessionRereply";
    private static final String KEY_SESSION_Follow = "sessionFollow";
    private static final String KEY_SESSION_Favorite = "sessionFavorite";



    private static final String KEY_SESSION_Video = "sessionVedio";



    private SharedPreferences sharedPreferences;

    public SharedPreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveSessionCookie(String cookieValue) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SESSION_COOKIE, cookieValue);
        editor.apply();
    }

    public String getSessionCookie() {

        return sharedPreferences.getString(KEY_SESSION_COOKIE, null);
    }


    public void savePk(String data){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SESSION_PK, data);
        editor.apply();

    }

    public void removePk(){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_SESSION_PK);
//        editor.
//        editor.putString(KEY_SESSION_PK, data);
        editor.apply();

    }

    public String getSessionPk() {
        return sharedPreferences.getString(KEY_SESSION_PK, null);

//        sharedPreferences.getString(KEY_SESSION_PK,null);
    }



    // noti on시 true,
    // noti off 시 false
    public void setRoomNoti(boolean state,int roomPk){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_SESSION_RoomPk+Integer.toString(roomPk), state);

        editor.apply();

    }

    // noti on시 true,
    // noti off 시 false
    public boolean getRoomNotiOn(int roomPk) {

        return sharedPreferences.getBoolean(KEY_SESSION_RoomPk+Integer.toString(roomPk), true);

    }


    // noti on시 true,
    // noti off 시 false
    public void setChattingNoti(boolean state){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_SESSION_Chatting, state);

        editor.apply();

    }

    // noti on시 true,
    // noti off 시 false
    public boolean getIsChattingNotiOn() {

        return sharedPreferences.getBoolean(KEY_SESSION_Chatting, true);

    }


    // KEY_SESSION_Video

    public void setVideo(String uri, int Id){

//        String strUri = uri.toString();
//        Log.i("setVideo",strUri);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SESSION_Video+Integer.toString(Id), uri);
        editor.apply();

    }

    // noti on시 true,
    // noti off 시 false
    public Uri getVideo(int Id) {

        String uri = sharedPreferences.getString(KEY_SESSION_Video+Integer.toString(Id),"");


        if(uri.equals("")){
            return null;
        }
        else{
            return Uri.parse(uri);
        }

    }


    // noti on시 true,
    // noti off 시 false
    public void setPostNoti(boolean state){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_SESSION_Post, state);

        editor.apply();

    }

    // noti on시 true,
    // noti off 시 false
    public boolean getIsPostNotiOn() {

        return sharedPreferences.getBoolean(KEY_SESSION_Post, true);

    }
    // noti on시 true,
    // noti off 시 false
    public void setNoti(String type, boolean state){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(type.equals("Follow")){
            editor.putBoolean(KEY_SESSION_Follow, state);
        }
        else if(type.equals("Reply")){
            editor.putBoolean(KEY_SESSION_Reply, state);
        }
        else if(type.equals("Rereply")){
            editor.putBoolean(KEY_SESSION_Rereply, state);
        }
        else if(type.equals("Post")){
            editor.putBoolean(KEY_SESSION_Post, state);
        }
        else if(type.equals("Favorite")){
            editor.putBoolean(KEY_SESSION_Favorite, state);
        }
        else{
            Log.i(TAG, "값 미변경.");
        }

        editor.apply();

    }

    // noti on시 true,
    // noti off 시 false
    public boolean getIsNotiOn(String type) {


        boolean result = false;
        if(type.equals("Follow")){
            result = sharedPreferences.getBoolean(KEY_SESSION_Follow, true);

        }
        else if(type.equals("Reply")){
            result = sharedPreferences.getBoolean(KEY_SESSION_Reply, true);

        }
        else if(type.equals("Rereply")){
            result = sharedPreferences.getBoolean(KEY_SESSION_Rereply, true);

        }
        else if(type.equals("Post")){
            result = sharedPreferences.getBoolean(KEY_SESSION_Post, true);
        }
        else if(type.equals("Favorite")){
            result = sharedPreferences.getBoolean(KEY_SESSION_Favorite, true);
        }
        else {
            Log.i(TAG, "값 미변경.");

        }
        return result;
    }
}
