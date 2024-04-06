package com.example.project2309.network;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.project2309.MyApplication;
import com.example.project2309.R;
import com.example.project2309.ui.ChattingActivity;
import com.example.project2309.ui.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class BackgroundSocketService extends Service {

    private static final String TAG = "BackgroundSocketService";
//    private static final String SERVER_IP = "192.168.0.61";
    private static final String SERVER_IP = "172.30.1.15";



    private static final int SERVER_PORT = 4444;

    private Socket socket;
    DataOutputStream out = null; // 이 변수는 사용자가 입력한 데이터를 출력할 때 사용합니다.
    DataInputStream in = null;

    LocalBroadcastManager localBroadcastManager;


    public static final String ACTION_MESSAGE_RECEIVED = "com.example.ACTION_MESSAGE_RECEIVED";


    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        public BackgroundSocketService getService() {
            return BackgroundSocketService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    boolean isRunning;

    List<JSONObject> messagelist;

    // 서비스의 메서드
    public boolean sendData(JSONObject object) {


        // TODO: 데이터 활용
        Log.i(TAG, "--------------------------------------------------");
        Log.i(TAG,"sendData 함수 실행.");
        Log.i(TAG,"보낼 메세지 : " + object.toString());

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "--------------------------------------------------");
                Log.i(TAG,"sendData()의 Thread 시작.");


                if(socket != null && !socket.isClosed()){

                    if(out == null){
                        Log.i(TAG,"outStream is null.");

                    }

                    try {
                        out.writeUTF(object.toString()); // 닉네임을 UTF-8로 변경 후 출력스트림에 넣습니다.
//                        if(object.has("media_type")) {
//                            out.writeUTF(object.toString()); // 닉네임을 UTF-8로 변경 후 출력스트림에 넣습니다.
//                            out.writeUTF(object.toString()); // 닉네임을 UTF-8로 변경 후 출력스트림에 넣습니다.
//                            out.writeUTF(object.toString()); // 닉네임을 UTF-8로 변경 후 출력스트림에 넣습니다.
//                            out.writeUTF(object.toString()); // 닉네임을 UTF-8로 변경 후 출력스트림에 넣습니다.
//                            out.writeUTF(object.toString()); // 닉네임을 UTF-8로 변경 후 출력스트림에 넣습니다.
//                            out.writeUTF(object.toString()); // 닉네임을 UTF-8로 변경 후 출력스트림에 넣습니다.
//                            out.writeUTF(object.toString()); // 닉네임을 UTF-8로 변경 후 출력스트림에 넣습니다.
//                            out.writeUTF(object.toString()); // 닉네임을 UTF-8로 변경 후 출력스트림에 넣습니다.
//                            out.writeUTF(object.toString()); // 닉네임을 UTF-8로 변경 후 출력스트림에 넣습니다.
//                            out.writeUTF(object.toString()); // 닉네임을 UTF-8로 변경 후 출력스트림에 넣습니다.
//                            out.writeUTF(object.toString()); // 닉네임을 UTF-8로 변경 후 출력스트림에 넣습니다.
//
//                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Log.i(TAG,"메세지 전송.");


                }
                else{
                    Log.i(TAG,"연결 안됨 상태라 메세지 안보냄.");

                }

                Log.i(TAG,"sendData()의 Thread 끝");
                Log.i(TAG, "--------------------------------------------------");

            }

        }).start();
        Log.i(TAG,"sendData() 끝");
        Log.i(TAG, "--------------------------------------------------");
        if(socket != null && !socket.isClosed()) {

            if (out == null ) {
                Log.i(TAG, "outStream is null.");
                messagelist.add(object);
                return false;

            }
            else{
                return true;
            }
        }
        else{
            messagelist.add(object);
            return false;
        }



    }

    MyNotificationManager notificationManager;
    private SharedPreferencesManager sharedPreferencesManager;
    public void setPk(String pk){
        sharedPreferencesManager.savePk(pk);

    }

    public String getPk(){
        return sharedPreferencesManager.getSessionPk();
//        sharedPreferencesManager.savePk(pk);

    }




    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG,"onUnbind");
        return super.onUnbind(intent);


    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
        Log.i(TAG,"unbindService");
    }
    Thread SocketThread;


    ConnectivityManager.NetworkCallback callback;
    String pk;
    ConnectivityManager manager;
    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "--------------------------------------------------");

        // Initialize socket connection in a separate thread
        Log.d(TAG, "onCreate");

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        notificationManager = new MyNotificationManager(getApplicationContext());
        sharedPreferencesManager = new SharedPreferencesManager(getApplicationContext());
        messagelist = new ArrayList<>();

        isRunning = true;

//        Thread
//         SocketThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Log.i(TAG, "--------------------------------------------------");
//                Log.i(TAG,"onCreate()의 SocketThread 시작.");
//
//                try {
//                    Log.d(TAG, "Thread Running Start.");
//
//
//                    socket = new Socket(SERVER_IP, SERVER_PORT);
//                    Log.d(TAG, "Socket connect");
//
//                    in = new DataInputStream(socket.getInputStream());
//                    out = new DataOutputStream(socket.getOutputStream());
//
//                    Log.d(TAG, "Stream 연결");
//
//                    // Handle incoming data (read from reader) here
//                    String message;
//
//                    while ((message = in.readUTF()) != null) {
//
//                        // 서비스에서 메시지를 전달할 때 사용할 브로드캐스트 액션
//                        // 메시지를 전달하는 부분
//                        Log.d(TAG,"서버에게 메세지 받음 : " + message);
//
//                        Intent intent = new Intent("1");
//                        intent.putExtra("message", message);
//                        localBroadcastManager.sendBroadcast(intent);
//
////                        Log.d(TAG, " : " + message);
//                        // Process the received message as needed
//
//                    }
//
//
//                } catch (IOException e) {
//
//                    Log.e(TAG, "onCreate Thread IOException Error: " + e.getMessage());
////                    throw new RuntimeException(e);
//
//                    e.printStackTrace();
//
//                }
//                catch (Exception exception){
////                    Log.e(TAG,exception.toString());
//
//                    exception.printStackTrace();
//                    Log.e(TAG, "onCreate Thread Exception Error: " + exception.getMessage());
//
//                }
//
//                Log.i(TAG,"onCreate()의 SocketThread 끝.");
//                Log.i(TAG, "--------------------------------------------------");
//
//            }
//
//        });
//
//        SocketThread.start();


        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        sharedPreferencesManager = new SharedPreferencesManager(getApplicationContext());

        callback = new ConnectivityManager.NetworkCallback(){
            @Override
            public void onAvailable(@NonNull Network network) {
                // 네트워크를 사용할 준비가 되었을 때

                Log.i(TAG, "--------------------------------------------------");

                Log.i(TAG, "네트워크를 사용할 준비가 되었을 때");
                if(getPk() == null){
                    return;
                    
                }
                if(socket == null || socket.isClosed()){
                    SocketThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "--------------------------------------------------");
                            Log.i(TAG,"onCreate()의 SocketThread 시작.");

                            try {
                                Log.d(TAG, "Thread Running Start.");


                                socket = new Socket(SERVER_IP, SERVER_PORT);
                                Log.d(TAG, "Socket connect");

//                                new Handler().post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(getApplicationContext(), "소켓 연결 성공", Toast.LENGTH_SHORT).show();
//
//                                    }
//                                });
                                in = new DataInputStream(socket.getInputStream());
                                out = new DataOutputStream(socket.getOutputStream());




                                Log.d(TAG, "Stream 연결");

                                // Handle incoming data (read from reader) here
                                String message;

                                while ((message = in.readUTF()) != null) {

                                    // 서비스에서 메시지를 전달할 때 사용할 브로드캐스트 액션
                                    // 메시지를 전달하는 부분
                                    Log.d(TAG,"서버에게 메세지 받음 : " + message);

                                    Intent intent = new Intent("1");
                                    intent.putExtra("message", message);

                                    localBroadcastManager.sendBroadcast(intent);

                                    JSONObject jsonObject;
                                    try {
                                        jsonObject = new JSONObject(message);
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }

                                    String type;
                                    try {
                                        type = jsonObject.getString("type");

                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }

                                    Log.i(TAG, "type : "+type);

                                    if(type.equals("connect")){
                                        Log.i(TAG, "서버가 연결을 수락.");

                                        init();

                                        for(int i=0;i<messagelist.size();i++){
                                            sendData(messagelist.get(i));
                                        }
                                        messagelist.clear();
                                    }

                                }


                            } catch (IOException e) {

                                Log.e(TAG, "onCreate Thread IOException Error: " + e.getMessage());
//                    throw new RuntimeException(e);
//                                Toast.makeText(getApplicationContext(), "소켓 연결 끊김.", Toast.LENGTH_SHORT).show();

//                                new Handler().post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(getApplicationContext(), "소켓 연결 끊김", Toast.LENGTH_SHORT).show();
//
//                                    }
//                                });
                                e.printStackTrace();

                            }
//                            catch (Exception exception){
////                    Log.e(TAG,exception.toString());
//
//                                exception.printStackTrace();
//                                Log.e(TAG, "onCreate Thread Exception Error: " + exception.getMessage());
//
//                            }

                            Log.i(TAG,"onCreate()의 SocketThread 끝.");
                            Log.i(TAG, "--------------------------------------------------");

                        }

                    });

                    SocketThread.start();
                    Log.i(TAG, "--------------------------------------------------");

                }
            }

            @Override
            public void onLost(@NonNull Network network) {
                // 네트워크가 끊겼을 때
                Log.i(TAG, "--------------------------------------------------");

                Log.i(TAG, "네트워크가 끊겼을 때");

                try {
                    if (socket != null && !socket.isClosed()) {
                        socket.close();
                        socket = null;
                        Log.i(TAG, "socket 닫음.");

                    }
                    if(out != null){
                        out.close();
                        out = null;
                    }


                } catch (IOException e) {
                    Log.e(TAG, "Error closing socket: " + e.getMessage());
                }

//                Log.i(TAG, "socket 닫음.");


                if(SocketThread!=null && SocketThread.isAlive()){
                    SocketThread.interrupt();
                    Log.i(TAG,"SocketThread 종료.");

                }

//                Log.i(TAG,"SocketThread 종료.");




                Log.i(TAG, "--------------------------------------------------");

            }
        };
        manager.registerNetworkCallback(builder.build(), callback);


        Log.i(TAG,"onCreate() 끝");
        Log.i(TAG, "--------------------------------------------------");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Return START_STICKY to ensure the service restarts if it's killed by the system
//        startForegroundService();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        Log.i(TAG, "--------------------------------------------------");

        Log.i(TAG,"onDestroy 시작");
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

        } catch (IOException e) {
            Log.e(TAG, "Error closing socket: " + e.getMessage());
        }

//        while ((message = in.readUTF()) != null) {
//            라서 소켓 연결끊기면 null 을 받나? 그럼 아래 코드 필요 없음. 확인해보기

//         쓰레드가 종료되지 않고, 무한대기할 수도 있어서, 종료를 해주기.
        if(SocketThread!=null && SocketThread.isAlive()){
            SocketThread.interrupt();
            Log.i(TAG,"SocketThread 종료.");
        }
        if(out != null){
            try {
                out.close();
                out = null;
            } catch (IOException e) {
                Log.e(TAG, "Error closing socket: " + e.getMessage());

//                throw new RuntimeException(e);
            }
        }

        manager.unregisterNetworkCallback(callback);

//        if (SocketThread != null && SocketThread.isAlive()) {
//            SocketThread.interrupt();
//        }
//

//        stopForeground(true);



//        Intent restartIntent = new Intent(getApplicationContext(), BackgroundSocketService.class);
//        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(),
//                0, restartIntent, PendingIntent.FLAG_ONE_SHOT );
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        if (alarmManager != null) {
//            alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 1000,
//                    pendingIntent);
//        }



//        stopForeground(true);


        Log.i(TAG,"onDestroy 끝");
        Log.i(TAG, "--------------------------------------------------");

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i(TAG, "--------------------------------------------------");



        Log.i(TAG,"onTaskRemoved");

        if(isRunning){
            Intent restartIntent = new Intent(getApplicationContext(), BackgroundSocketService.class);
            PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(),
                    0, restartIntent, PendingIntent.FLAG_ONE_SHOT );
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 1000,
                        pendingIntent);
            }
        }

        Log.i(TAG, "--------------------------------------------------");

    }



    @Override
    public void onLowMemory() {

        super.onLowMemory();

        Log.i(TAG, "--------------------------------------------------");

        Log.i(TAG,"onLowMemory");
        Log.i(TAG, "--------------------------------------------------");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

        Log.i(TAG, "--------------------------------------------------");

        Log.i(TAG,"onTrimMemory");

//        if(level == Application.)
//        stopForeground( builder.build());
//        stopSelf();
//        stopForeground(true);

        Log.i(TAG, "--------------------------------------------------");

    }

    public void init(){

        Log.i(TAG, "--------------------------------------------------");
        Log.i(TAG,"init() 실행 시작.");

        String pk = sharedPreferencesManager.getSessionPk();

        if(pk == null){
            isRunning = false;
            stopSelf();
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            pk = sharedPreferencesManager.getSessionPk();
            jsonObject.put("type","connect");
            jsonObject.put("pk",pk);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        Log.i(TAG,"메세지 : "+jsonObject.toString());

        // 서비스에 데이터 전달
        sendData(jsonObject);

        Log.i(TAG,"init() 끝");
        Log.i(TAG, "--------------------------------------------------");

    }
    NotificationCompat.Builder builder;
    private void startForegroundService(){
        builder = new NotificationCompat.Builder(this, "default");//오레오 부터 channelId가 반드시 필요하다.
        builder.setSmallIcon(R.drawable.baseline_flutter_dash_24);
        builder.setContentTitle("Img Share");
        builder.setContentText("");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT );
        builder.setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//오레오 이상부터 이 코드가 동작한다.
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }
        startForeground(1, builder.build());//id를 0으로 하면안된다.


    }


    public void setRunning(boolean running) {
        isRunning = running;
    }
}