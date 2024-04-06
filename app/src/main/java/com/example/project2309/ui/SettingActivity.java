package com.example.project2309.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.project2309.MyApplication;
import com.example.project2309.R;
import com.example.project2309.data.Data;
import com.example.project2309.network.NetworkManager;
import com.example.project2309.network.SharedPreferencesManager;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;

public class SettingActivity extends AppCompatActivity {

    SharedPreferencesManager sharedPreferencesManager;

    NetworkManager networkManager;
    Button 알람설정,계정삭제,비밀번호변경버튼;

    String TAG = "SettingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        sharedPreferencesManager = new SharedPreferencesManager(getApplicationContext());
        networkManager = new NetworkManager(getApplicationContext());

        알람설정 = findViewById(R.id.button56);

        계정삭제 = findViewById(R.id.button30);

        알람설정.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                boolean isNotiOn = sharedPreferencesManager.getIsChattingNotiOn();
                showOptionsDialog();

            }
        });


        Button 로그아웃;
        로그아웃 = findViewById(R.id.button25);

        로그아웃.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
                    @Override
                    public void onResponseSuccess(Data data) {

                        Log.i(TAG, data.result.toString());


                        if (data.result.get("result").getAsString().equals(Data.SUCCESS)) {

                            // 방 삭제된거 이전 엑티비티에 반영되어야 함.
//                            finish();


                            // 서비스 종료, 자원정리
                            // 채팅서버와 연결 끊기.
                            MyApplication application = (MyApplication) getApplicationContext();
                            application.removePk();
                            application.serviceStop();

                            ActivityCompat.finishAffinity(SettingActivity.this);
                            Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
//                            출처: https://mparchive.tistory.com/81 [My Program Archive:티스토리]
                            startActivity(intent);


                        }

                    }

                    @Override
                    public void onResponseFail(ResponseBody ErrorBody) {
                        Toast.makeText(SettingActivity.this, "onResponseFail", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNetworkError(Throwable t) {
                        Toast.makeText(SettingActivity.this, "onNetworkError", Toast.LENGTH_SHORT).show();

                    }
                });

                JsonObject jsonObject = new JsonObject();
                networkManager.post2Request("Login","logout",jsonObject);

            }
        });

        계정삭제.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, DeleteAccoutActivity.class);
                startActivity(intent);
            }
        });

        비밀번호변경버튼 = findViewById(R.id.button24);

        비밀번호변경버튼.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, EditPasswordActivity.class);
                startActivity(intent);
            }
        });
    }


    // 게시글 수정, 삭제 선택하는 대화창
    private void showOptionsDialog() {


        boolean isNotiOn = sharedPreferencesManager.getIsChattingNotiOn();
        String message = "채팅 알림 ";
        if(isNotiOn){
            message += "끄기";
        }
        else{
            message += "켜기";
        }

        boolean isPostState = sharedPreferencesManager.getIsPostNotiOn();

        String Post = "게시글 알림 ";
        if(isPostState){
            Post += "끄기";
        }
        else{
            Post += "켜기";
        }


        boolean isReplyState = sharedPreferencesManager.getIsNotiOn("Reply");

        String Reply = "댓글 알림 ";
        if(isReplyState){
            Reply += "끄기";
        }
        else{
            Reply += "켜기";
        }

        boolean isRereplyState = sharedPreferencesManager.getIsNotiOn("Rereply");

        String Rereply = "답글 알림 ";
        if(isRereplyState){
            Rereply += "끄기";
        }
        else{
            Rereply += "켜기";
        }

        boolean isFollowState = sharedPreferencesManager.getIsNotiOn("Follow");

        String Follow = "팔로우 알림 ";
        if(isFollowState){
            Follow += "끄기";
        }
        else{
            Follow += "켜기";
        }

        boolean isFavoriteState = sharedPreferencesManager.getIsNotiOn("Favorite");

        String Favorite = "좋아요 알림 ";
        if(isFavoriteState){
            Favorite += "끄기";
        }
        else{
            Favorite += "켜기";
        }



        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("")
                .setItems(new CharSequence[]{message,Post,Reply,Rereply,Follow,Favorite}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            sharedPreferencesManager.setChattingNoti(!isNotiOn);
                            break;
                        case 1:
                            sharedPreferencesManager.setPostNoti(!isPostState);
                            break;
                        case 2:
                            sharedPreferencesManager.setNoti("Reply",!isReplyState);
                            break;
                        case 3:
                            sharedPreferencesManager.setNoti("Rereply",!isRereplyState);
                            break;
                        case 4:
                            sharedPreferencesManager.setNoti("Follow",!isFollowState);
                            break;
                        case 5:
                            sharedPreferencesManager.setNoti("Favorite",!isFavoriteState);
                            break;
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}