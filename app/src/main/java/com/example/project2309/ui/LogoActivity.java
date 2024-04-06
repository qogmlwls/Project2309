package com.example.project2309.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.project2309.MyApplication;
import com.example.project2309.R;
import com.example.project2309.data.Data;
import com.example.project2309.network.NetworkManager;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;

public class LogoActivity extends AppCompatActivity {


    NetworkManager networkManager;

    Handler handler;

    String TAG = "LogoActivity";
//        Log.i(TAG,"");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        Log.i(TAG, "--------------------------------------------------");
        Log.i(TAG,"onCreate");

        handler = new Handler();

        // Handler handler = new Handler();
        // 로그인 유지 확인 요청
        // 로그인 유지된 상태라면 메인 페이지로 이동
        // 로그인 유지 안된거라면 로그인 페이지로 이동
        networkManager = new NetworkManager(getApplicationContext());
        networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
            @Override
            public void onResponseSuccess(Data data) {

                Log.i(TAG, "--------------------------------------------------");
                Log.i(TAG,"로그인 유지 확인 응답 받음");
                JsonObject responseResult = data.result;


                Log.i(TAG+" : 로그인 유지 확인","응답 데이터 : "+responseResult.toString());


                String result = responseResult.get("result").getAsString();
                if(result.equals(Data.SUCCESS)) {

                    boolean isLogin = responseResult.get("isLogin").getAsBoolean();

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "--------------------------------------------------");
                            if(isLogin){

                                String pk = Integer.toString(responseResult.get("pk").getAsInt());
                                MyApplication application = (MyApplication)getApplicationContext();

                                if(!pk.equals(application.getPk())){
                                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                    notificationManager.cancelAll();
                                }
                                // 소켓 실행.
                                application.setPk(pk);
                                application.startSocketService(pk);


                                Intent intent = new Intent(LogoActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                            }
                            else{

                                Intent intent = new Intent(LogoActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();

                            }
                            Log.i(TAG, "--------------------------------------------------");
                        }
                    },1000);


                }
                else{


                    Toast.makeText(LogoActivity.this, "result : fail", Toast.LENGTH_SHORT).show();


                }
                Log.i(TAG, "--------------------------------------------------");
            }

            @Override
            public void onResponseFail(ResponseBody ErrorBody) {
                Log.i(TAG, "--------------------------------------------------");
                Log.i(TAG,"로그인 유지 확인 응답 못받음");
                Toast.makeText(LogoActivity.this, "onResponseFail", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "--------------------------------------------------");
            }

            @Override
            public void onNetworkError(Throwable t) {
                Log.i(TAG, "--------------------------------------------------");
                Log.i(TAG,"로그인 유지 확인 응답 못받음");
                Toast.makeText(LogoActivity.this, "onNetworkError", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "--------------------------------------------------");
            }

        });


        networkManager.GET2Request("Login","isLogin","");

        Log.i(TAG,"서버에 로그인 유지 확인 요청");

        Log.i(TAG,"onCreate 끝");
        Log.i(TAG, "--------------------------------------------------");


    }
}