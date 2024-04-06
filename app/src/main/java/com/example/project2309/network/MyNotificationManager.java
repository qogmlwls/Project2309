package com.example.project2309.network;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.project2309.R;
import com.example.project2309.ui.ChattingActivity;

public class MyNotificationManager {

    private static final int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "my_notification_channel";

    private Context context;
    private NotificationManager notificationManager;

    public MyNotificationManager(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    String TAG = "MyNotificationManager";
    int i =1;

    public void showNotification(String title, String content,int roomId, int fromPk ){

        Log.i(TAG, "--------------------------------------------------");

        String channelId = "MyChannelID2";
        createNotificationChannel(channelId,0);

        Log.i(TAG, "fromPk : "+Integer.toString(fromPk));
        Log.i(TAG, "roomId : "+Integer.toString(roomId));

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.baseline_flutter_dash_24);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.baseline_flutter_dash_24)
                .setLargeIcon(largeIcon) // 오른쪽 이미지
                .setContentTitle(title)
                .setContentText(content)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true);


        Log.i(TAG, "fromPk : "+Integer.toString(fromPk));
        Log.i(TAG, "roomId : "+Integer.toString(roomId));
        Log.i(TAG, "ID : "+Integer.toString(i));


        Intent notificationIntent = new Intent(context, ChattingActivity.class);
        notificationIntent.putExtra("fromPk",fromPk);
        notificationIntent.putExtra("roomId",roomId);
        notificationIntent.putExtra("ID",i);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

//        PendingIntent.getActivity(context,"l",notificationIntent,PendingIntent.FLAG_ONE_SHOT);

        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent contentIntent
                = PendingIntent.getActivity(context, roomId, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(contentIntent);

//        Toast.makeText(context, "noti : "+Integer.toString(i), Toast.LENGTH_SHORT).show();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(/* Notification ID */ roomId, builder.build());

        Log.i(TAG, "notify 생성.");

    }

    private void createNotificationChannel(String channelId,int num) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "My Channel"+Integer.toString(num);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription("Notification from Mascot");

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        Log.i(TAG, "createNotificationChannel() 끝.");

        Log.i(TAG, "--------------------------------------------------");


    }


    int PostNotiId = 0,ReplyNotiId = 0,RereplyNotiId = 0,FollowNotiId = 0,FavoriteNotiId = 0;

    public void showPostNotification(String name,String type ){

        Log.i(TAG, "--------------------------------------------------");

        String channelId,content,title;
        if(type.equals("Post")){
            channelId = "MyChannelID3";
            createNotificationChannel(channelId,1);

            content = "["+name+"]님이 새 게시글을 공유했습니다.";
            title = "게시글 알림";
        }
        else if(type.equals("Reply")){
            channelId = "MyChannelID4";
            createNotificationChannel(channelId,2);

            content = "["+name+"]님이 댓글을 남겼습니다.";
            title = "댓글 알림";
        }
        else if(type.equals("Rereply")){
            channelId = "MyChannelID5";
            createNotificationChannel(channelId,3);

            content = "["+name+"]님이 답글을 남겼습니다.";
            title = "답글 알림";
        }
        else if(type.equals("Follow")){
            channelId = "MyChannelID6";
            createNotificationChannel(channelId,4);

            content = "["+name+"]님이 팔로우를 시작했습니다.";
            title = "팔로우 알림";
        }
        else if(type.equals("Favorite")){
            channelId = "MyChannelID7";
            createNotificationChannel(channelId,5);

            content = "["+name+"]님이 게시글을 좋아합니다.";
            title = "좋아요 알림";
        }
        else{

            Log.i(TAG, "정해지지 않은 type");
            Log.i(TAG, "--------------------------------------------------");

            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.baseline_flutter_dash_24)
                .setContentTitle(title)
                .setContentText(content)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true);


        Log.i(TAG, "name : "+name);


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int num = 0;
        int PostNotiId = 0,ReplyNotiId = 0,RereplyNotiId = 0,FollowNotiId = 0,FavoriteNotiId = 0;

        if(type.equals("Post")){
            num = PostNotiId++;
        }
        else if(type.equals("Reply")){
            num = ReplyNotiId++;
        }
        else if(type.equals("Rereply")){
            num = RereplyNotiId++;
        }
        else if(type.equals("Follow")){
            num = FollowNotiId++;
        }
        else if(type.equals("Favorite")){
            num = FavoriteNotiId++;
        }


        notificationManager.notify(/* Notification ID */ num, builder.build());

        Log.i(TAG, "notify 생성.");
        Log.i(TAG, "--------------------------------------------------");

    }


}




//        PendingIntent contentIntent
//                = PendingIntent.getActivity(context, i, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(contentIntent);



//        builder.



//-----------------
////FLAG_UPDATE_CURRENT
////        @SuppressLint("UnspecifiedImmutableFlag")
////        PendingIntent contentIntent
////                = PendingIntent.getActivity(context, roomId, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
////        builder.setContentIntent(contentIntent);
//
////        PendingIntent contentIntent
////                = PendingIntent.getActivity(context, roomId, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
////        builder.setContentIntent(contentIntent);
//
//    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
////        notificationManager.notify(/* Notification ID */ roomId, builder.build());
//        notificationManager.notify(/* Notification ID */ i++, builder.build());
