package com.example.project2309;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.project2309.data.Chat;
import com.example.project2309.network.BackgroundSocketService;
import com.example.project2309.network.MyNotificationManager;
import com.example.project2309.network.SharedPreferencesManager;
import com.example.project2309.ui.ChattingActivity;
import com.example.project2309.ui.MainActivity;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MyApplication extends Application {

//    String pk;

    private BackgroundSocketService service;
    private boolean isBound = false;
    String TAG = "Application";
    MyNotificationManager notificationManager;
    int roomId;

    private SharedPreferencesManager sharedPreferencesManager;

    public void serviceStop(){
        service.setRunning(false);

        unbindService(serviceConnection);

        Intent intent = new Intent(this, BackgroundSocketService.class);
        stopService(intent);

        list.clear();

//        unbindService();

    }

    public void setPk(String pk){
        sharedPreferencesManager.savePk(pk);

    }
    public void removePk(){
        sharedPreferencesManager.removePk();

    }

    public String getPk(){
        return sharedPreferencesManager.getSessionPk();
//        sharedPreferencesManager.savePk(pk);

    }
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onBindingDied(ComponentName name) {
            ServiceConnection.super.onBindingDied(name);
            Log.i("Application ServiceConnection","onBindingDied 실행.");

        }

        @Override
        public void onNullBinding(ComponentName name) {
            ServiceConnection.super.onNullBinding(name);
            Log.i("Application ServiceConnection","onNullBinding 실행.");


        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {

            Log.i("Application ServiceConnection","onServiceConnected 실행.");

            // service와 대화할 수 있는 binder객체 받아오기.
            BackgroundSocketService.LocalBinder localBinder = (BackgroundSocketService.LocalBinder) binder;
            Log.i("Application ServiceConnection","get binder : "+binder.toString() );

            service = localBinder.getService();
            Log.i("Application ServiceConnection","localBinder.getService() 실행 : service = "+service.toString());

            isBound = true;

            for(int i=0;i<list.size();i++){
                service.sendData(list.get(i));
            }
            list.clear();


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // 서비스가 예기치 못하게 종료, 중단될때 실행.
            // 클라이언트가 bind를 해제할 때는 미실행.
            //https://developer.android.com/guide/components/bound-services?hl=ko
            Log.i("Application ServiceConnection","onServiceDisconnected 실행.");

            Toast.makeText(getApplicationContext(), "서비스 연결 끊김.", Toast.LENGTH_SHORT).show();
            isBound = false;
        }
    };


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.i(TAG, "--------------------------------------------------");
            Log.i(TAG, "BroadcastReceiver 메세지 받음.");

            String data = intent.getStringExtra("message");
            Log.i(TAG, "받은 메세지 : "+ data);
            Log.i(TAG,"지금 있는 방의 id : "+Integer.toString(getRoomId()));


            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(data);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            String type;
            try {
                type = jsonObject.getString("type");

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            Log.i(TAG, "--------------------------------------------------");

            Log.i(TAG, "type : "+type);

            if(type.equals("connect")){
                Log.i(TAG, "서버가 연결을 수락.");

//                MyApplication application = (MyApplication) getApplicationContext();
//                application.init();

            }
            else if(type.equals("ReceiveChatting")){

                Log.i(TAG, "채팅을 받음.");

                try {

                    // 알림 생성
                    String content="";

                    String mediaType = jsonObject.getString("media_type");


                    if("text".equals(mediaType)){
                        content = jsonObject.getString("message");

                    }
                    else if("image".equals(mediaType)){
                        content = "사진을 보냈습니다.";

                    }
                    else if("video".equals(mediaType)){
                        content = "동영상을 보냈습니다.";

                    }


                    String name = jsonObject.getString("name");
                    String roomId = jsonObject.getString("roomId");
                    String fromPk = jsonObject.getString("fromPk");

                    Log.i(TAG, "채팅 내용 : ");
                    Log.i(TAG,"닉네임 : "+name);
                    Log.i(TAG,"roomId : "+roomId);
                    Log.i(TAG,"fromPk : "+fromPk);
                    Log.i(TAG,"content : "+content);
                    Log.i(TAG,"지금 있는 방의 id : "+Integer.toString(getRoomId()));



                    if(getRoomId() != Integer.parseInt(roomId) && sharedPreferencesManager.getRoomNotiOn(Integer.parseInt(roomId) )
                            && sharedPreferencesManager.getIsChattingNotiOn()){

                        Log.i(TAG,"알림 생성 : showNotification() 실행.");

                        notificationManager.showNotification(name , content,Integer.parseInt(roomId),Integer.parseInt(fromPk) );

                    }
                    else{
                        Log.i(TAG,"지금 있는 방의 채팅이 온거라서 알림 미생성.");

                    }
                    Log.i(TAG, "--------------------------------------------------");


                } catch (JSONException e) {
                    throw new RuntimeException(e);

                }

            }
            else if(type.equals("NewPostNoti")){

                Log.i(TAG, "NewPostNoti 채팅을 받음.");

                try {

                    int postId = Integer.parseInt(jsonObject.getString("postId"));
                    String postUserId = jsonObject.getString("postUserId");
                    String postUserName = jsonObject.getString("postUserName");

                    Log.i(TAG, "채팅 내용 : ");
                    Log.i(TAG,"postId : "+postId);
                    Log.i(TAG,"postUserId : "+postUserId);
                    Log.i(TAG,"postUserName : "+postUserName);


                    if(sharedPreferencesManager.getIsPostNotiOn()){
                        Log.i(TAG, "알림 생성 : NewPostNoti showNotification() 실행.");
                        notificationManager.showPostNotification( postUserName,"Post");
                    }
                    else{
                        Log.i(TAG, "게시글 알림 off 미생성.");

                    }
                    Log.i(TAG, "--------------------------------------------------");


                } catch (JSONException e) {
                    throw new RuntimeException(e);

                }

            }
            else if(type.equals("ReplyNoti")){

                Log.i(TAG, "ReplyNoti 채팅을 받음.");

                try {

                    int postId = Integer.parseInt(jsonObject.getString("postId"));
                    String postUserId = jsonObject.getString("postUserId");
                    String postUserName = jsonObject.getString("postUserName");

                    Log.i(TAG, "채팅 내용 : ");
                    Log.i(TAG,"postId : "+postId);
                    Log.i(TAG,"postUserId : "+postUserId);
                    Log.i(TAG,"postUserName : "+postUserName);


                    if(sharedPreferencesManager.getIsNotiOn("Reply")){
                        Log.i(TAG, "알림 생성 : NewPostNoti showNotification() 실행.");
                        notificationManager.showPostNotification( postUserName,"Reply");
                    }
                    else{
                        Log.i(TAG, "게시글 알림 off 미생성.");

                    }
                    Log.i(TAG, "--------------------------------------------------");


                } catch (JSONException e) {
                    throw new RuntimeException(e);

                }

            }
            else if(type.equals("RereplyNoti")){

                Log.i(TAG, "RereplyNoti 채팅을 받음.");

                try {

                    int postId = Integer.parseInt(jsonObject.getString("postId"));
                    String postUserId = jsonObject.getString("postUserId");
                    String postUserName = jsonObject.getString("postUserName");

                    Log.i(TAG, "채팅 내용 : ");
                    Log.i(TAG,"postId : "+postId);
                    Log.i(TAG,"postUserId : "+postUserId);
                    Log.i(TAG,"postUserName : "+postUserName);


                    if(sharedPreferencesManager.getIsNotiOn("Rereply")){
                        Log.i(TAG, "알림 생성 : NewPostNoti showNotification() 실행.");
                        notificationManager.showPostNotification( postUserName,"Rereply");
                    }
                    else{
                        Log.i(TAG, "게시글 알림 off 미생성.");

                    }
                    Log.i(TAG, "--------------------------------------------------");


                } catch (JSONException e) {
                    throw new RuntimeException(e);

                }

            }
            else if(type.equals("FavoriteNoti")){

                Log.i(TAG, "FavoriteNoti 채팅을 받음.");

                try {

                    int postId = Integer.parseInt(jsonObject.getString("postId"));
                    String postUserId = jsonObject.getString("postUserId");
                    String postUserName = jsonObject.getString("postUserName");

                    Log.i(TAG, "채팅 내용 : ");
                    Log.i(TAG,"postId : "+postId);
                    Log.i(TAG,"postUserId : "+postUserId);
                    Log.i(TAG,"postUserName : "+postUserName);


                    if(sharedPreferencesManager.getIsNotiOn("Favorite")){
                        Log.i(TAG, "알림 생성 : Favorite showNotification() 실행.");
                        notificationManager.showPostNotification( postUserName,"Favorite");
                    }
                    else{
                        Log.i(TAG, "좋아요 알림 off 미생성.");

                    }
                    Log.i(TAG, "--------------------------------------------------");


                } catch (JSONException e) {
                    throw new RuntimeException(e);

                }

            }
            else if(type.equals("FollowNoti")){

                Log.i(TAG, "FollowNoti 채팅을 받음.");

                try {

                    int UserId = Integer.parseInt(jsonObject.getString("UserId"));
                    String UserName = jsonObject.getString("UserName");

                    Log.i(TAG, "채팅 내용 : ");
                    Log.i(TAG,"UserId : "+UserId);
                    Log.i(TAG,"UserName : "+UserName);


                    if(sharedPreferencesManager.getIsNotiOn("Follow")){
                        Log.i(TAG, "알림 생성 : Follow showNotification() 실행.");
                        notificationManager.showPostNotification( UserName,"Follow");
                    }
                    else{
                        Log.i(TAG, "팔로우 알림 off 미생성.");

                    }
                    Log.i(TAG, "--------------------------------------------------");


                } catch (JSONException e) {
                    throw new RuntimeException(e);

                }

            }

        }
    };
    LocalBroadcastManager localBroadcastManager;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG,"onCreate");
        notificationManager = new MyNotificationManager(getApplicationContext());
        sharedPreferencesManager = new SharedPreferencesManager(getApplicationContext());

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter("1"));

        roomId = -1;

        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        // 백그라운드 서비스가 실행되고 있지 않습니다.
        Intent intent = new Intent(this, BackgroundSocketService.class);
        // 서비스 미생성시, 생성하고 bind하기.
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);


    }


    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;

//        if(roomId != -1){
////            getResultSet
//        }
    }




    public void startSocketService(String pk) {

//        boolean isRunning = ServiceUtils.isServiceRunning(this, BackgroundSocketService.class);


        Log.i(TAG, "--------------------------------------------------");
        Log.i(TAG, "startSocketService() 실행 시작.");

        boolean isRunning = isServiceRunning(this, BackgroundSocketService.class);
        if (isRunning|| service==null) {
            // 백그라운드 서비스가 실행 중입니다.
            Log.i(TAG,"BackgroundSocketService 가 Running중");

            if(service == null){
                Log.i(TAG,"service is null");
            }
            stopSocketService();
        }

//        if(service == null){
//            stopSocketService();
//        }

//        this.pk = pk;
        Log.i(TAG,"나의 pk : "+sharedPreferencesManager.getSessionPk());

        // 백그라운드 서비스가 실행되고 있지 않습니다.
        Intent intent = new Intent(this, BackgroundSocketService.class);
        startService(intent);

//오레오 이상부터 동작하는 코드
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(intent);
//        } else {
//            startService(intent);
//        }
//        startService(intent);
        // 서비스 미생성시, 생성하고 bind하기.
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);


        Log.i(TAG,"startSocketService() 끝.");
        Log.i(TAG, "--------------------------------------------------");


    }

    public void stopSocketService() {

        Log.i(TAG, "--------------------------------------------------");
        Log.i(TAG, "stopSocketService() 실행 시작.");

        // 서비스와 연결 해제
        if (isBound) {
            Log.i(TAG,"서비스와 bind 해제");

            // 연결된 곳이 없으면 서비스 종료.
            unbindService(serviceConnection);
            isBound = false;

        }
        else{
            Log.i(TAG,"stopSocketService isBound : "+Boolean.toString(isBound));

        }

        Log.i(TAG,"stopSocketService() 끝.");
        Log.i(TAG, "--------------------------------------------------");

    }


    // serviceClass 실행중이라면 true를 던지자.
    public boolean isServiceRunning(Context context, Class<?> serviceClass) {


        // 현재 엑티비티 관리자를 가져온다.
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        if (activityManager != null) {

            // 현재 실행중인 서비스의 정보들을 가져온다.
            // Integer.MAX_VALUE : 32-bit 정수 표현의 최대값
            // .getRunningServices() 매개변수 : 최대 서비스 수
            for (ActivityManager.RunningServiceInfo serviceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {

                // service와 이름이 같은 서비스가 실행중이라면
                if (serviceClass.getName().equals(serviceInfo.service.getClassName())) {
                    // true를 반환한다.
                    return true;
                }
            }
        }

        Log.i("MYAPPLICATION","서비스 NOT RUNNING.");

        return false;
    }




    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
//        Log.i("application","onTrimMemory");
        Log.i("application","onTrimMemory, level : "+Integer.toString(level));



    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.i("application","onLowMemory");
//        Application.TRIM_MEMORY_COMPLETE
//        Application.TRIM_MEMORY_UI_HIDDEN

    }



    @Override
    public void onTerminate() {

        super.onTerminate();
        Log.i("application","onTerminate");

    }




    public void init(){

        Log.i(TAG, "--------------------------------------------------");
        Log.i(TAG,"init() 실행 시작.");



        String pk = sharedPreferencesManager.getSessionPk();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type","connect");
            jsonObject.put("pk",pk);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        Log.i(TAG,"메세지 : "+jsonObject.toString());
        if(service == null){
            Log.i(TAG,"service is null ");
        }


        if(service == null){

            list.add(jsonObject);
            Log.i(TAG,"jsonObject : "+jsonObject.toString());


            // 백그라운드 서비스가 실행되고 있지 않습니다.
            Intent intent = new Intent(this, BackgroundSocketService.class);
            // 서비스 미생성시, 생성하고 bind하기.
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

            return;
        }


        // 서비스에 데이터 전달
        service.sendData(jsonObject);

        Log.i(TAG,"init() 끝");
        Log.i(TAG, "--------------------------------------------------");

    }


    public boolean SendChatting(String roomId, String requestId,String receivePk,String mediaType,String mediaData){

        Log.i(TAG, "--------------------------------------------------");
        Log.i(TAG,"SendChatting() 실행 시작.");

        boolean isRunning = isServiceRunning(this, BackgroundSocketService.class);

        //
        if(isRunning) {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("type", "SendChatting");
                jsonObject.put("roomId", roomId);
                jsonObject.put("ReceivePk", receivePk);
                jsonObject.put("requestId", requestId);
                jsonObject.put("media_type", mediaType);
                jsonObject.put("mediaData", mediaData);
                Log.i(TAG,"메세지 : "+jsonObject.toString());
                Log.i(TAG,"type : "+jsonObject.getString("type"));
                Log.i(TAG,"roomId : "+jsonObject.getString("roomId"));
                Log.i(TAG,"ReceivePk : "+jsonObject.getString("ReceivePk"));
                Log.i(TAG,"requestId : "+jsonObject.getString("requestId"));
                Log.i(TAG,"media_type : "+jsonObject.getString("media_type"));
                Log.i(TAG,"mediaData : "+jsonObject.getString("mediaData"));

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }




            if(service == null){
                Log.i(TAG,"service is null ");
                list.add(jsonObject);
                Log.i(TAG,"jsonObject : "+jsonObject.toString());

                // 백그라운드 서비스가 실행되고 있지 않습니다.
                Intent intent = new Intent(this, BackgroundSocketService.class);
                // 서비스 미생성시, 생성하고 bind하기.
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

                return true;
            }
            Log.i(TAG,"SendChatting() 끝");

            Log.i(TAG, "--------------------------------------------------");
            // 서비스에 데이터 전달
            return service.sendData(jsonObject);


        }
        else{
            Log.i(TAG,"SendChatting() 끝");

            Log.i(TAG, "--------------------------------------------------");
            return false;
        }


    }



    public boolean SendReadChat(String roomId,String receivePk){

        Log.i(TAG, "--------------------------------------------------");
        Log.i(TAG,"SendReadChat() 실행 시작.");

        boolean isRunning = isServiceRunning(this, BackgroundSocketService.class);

        //
        if(isRunning) {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("type", "ReadChat");
                jsonObject.put("roomId", roomId);
                jsonObject.put("receivePk", receivePk);

                Log.i(TAG,"메세지 : "+jsonObject.toString());
                Log.i(TAG,"type : "+jsonObject.getString("type"));
                Log.i(TAG,"roomId : "+jsonObject.getString("roomId"));
                Log.i(TAG,"receivePk : "+jsonObject.getString("receivePk"));

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            Log.i(TAG,"SendChatting() 끝");

            Log.i(TAG, "--------------------------------------------------");

            if(service == null){
                list.add(jsonObject);
                Log.i(TAG,"jsonObject : "+jsonObject.toString());

                // 백그라운드 서비스가 실행되고 있지 않습니다.
                Intent intent = new Intent(this, BackgroundSocketService.class);
                // 서비스 미생성시, 생성하고 bind하기.
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
                Log.i(TAG,"service is null ");
                return true;
            }
            else{
                // 서비스에 데이터 전달
                return service.sendData(jsonObject);

            }


        }
        else{
            Log.i(TAG,"SendReadChat() 끝");

            Log.i(TAG, "--------------------------------------------------");
            return false;
        }


    }

    List<JSONObject> list = new ArrayList<>();



    public boolean SendMessage(JSONObject jsonObject){

        Log.i(TAG, "--------------------------------------------------");
        Log.i(TAG,"SendReadChat() 실행 시작.");

        boolean isRunning = isServiceRunning(this, BackgroundSocketService.class);
        //
        if(isRunning) {

            if(service == null){
                Log.i(TAG,"service is null ");
                list.add(jsonObject);
                Log.i(TAG,"jsonObject : "+jsonObject.toString());


                // 백그라운드 서비스가 실행되고 있지 않습니다.
                Intent intent = new Intent(this, BackgroundSocketService.class);
                // 서비스 미생성시, 생성하고 bind하기.
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);


                return true;
            }
            else{
                Log.i(TAG,"SendChatting() 끝");

                Log.i(TAG, "--------------------------------------------------");
                // 서비스에 데이터 전달
                return service.sendData(jsonObject);
            }


        }
        else{
            Log.i(TAG,"SendReadChat() 끝");

            Log.i(TAG, "--------------------------------------------------");
            return false;
        }


    }
}
