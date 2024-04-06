package com.example.project2309.network;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {

    String TAG = "BootReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {


            Log.i(TAG,"ACTION_BOOT_COMPLETED");
            // ㅖㅏ
            SharedPreferencesManager sharedPreferencesManager;

            sharedPreferencesManager = new SharedPreferencesManager(context.getApplicationContext());

            if(sharedPreferencesManager.getSessionPk()!= null){
                // 백그라운드 서비스가 실행되고 있지 않습니다.
                Log.i(TAG,"서비스 실행.");

                Intent i = new Intent(context, BackgroundSocketService.class);
                context.startService(i);

//                // 서비스 미생성시, 생성하고 bind하기.
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    context.startForegroundService(i);
////                    startForegroundService(intent);
//                } else {
////                    startService(intent);
//                    context.startService(i);
//                }
//            Intent i = new Intent(context, BackgroundSocketService.class);
//            context.startService(i);
            }



        }
    }

}


