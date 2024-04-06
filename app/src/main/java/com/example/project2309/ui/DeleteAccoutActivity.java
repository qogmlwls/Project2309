package com.example.project2309.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.project2309.MyApplication;
import com.example.project2309.R;
import com.example.project2309.data.Data;
import com.example.project2309.network.NetworkManager;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;

public class DeleteAccoutActivity extends AppCompatActivity {


    String TAG = "DeleteAccoutActivity";
    NetworkManager networkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_accout);

        CheckBox 유의사항확인체크박스 = findViewById(R.id.checkBox6);

        Button 계정삭제 = findViewById(R.id.button57);
        ImageButton 닫기버튼 = findViewById(R.id.imageButton25);
        networkManager = new NetworkManager(getApplicationContext());


        계정삭제.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(유의사항확인체크박스.isChecked()){
                    showOptionsDeleteDialog();
                }
                else{
                    Toast.makeText(DeleteAccoutActivity.this, "유의사항을 확인한 뒤, 체크박스에 체크를 해주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        닫기버튼.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    private void showOptionsDeleteDialog() {

        Log.i(TAG, "--------------------------------------------------");

        AlertDialog.Builder builder = new AlertDialog.Builder(DeleteAccoutActivity.this);
        builder.setTitle("회원 탈퇴를 합니다.");
//                .setMessage("");

        builder.setNegativeButton("확인", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
//                Intent intent = new Intent(DeleteAccoutActivity.this,SettingActivity.class);
//                startActivity(intent);
                DeleteAccountOut();
            }
        });

        builder.setNeutralButton("취소", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Log.i(TAG, "--------------------------------------------------");

    }



    public void DeleteAccountOut(){


        networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
            @Override
            public void onResponseSuccess(Data data) {

                Log.i(TAG, data.result.toString());


                if (data.result.get("result").getAsString().equals(Data.SUCCESS)) {

                    // 서비스 종료, 자원정리
                    // 채팅서버와 연결 끊기.
                    MyApplication application = (MyApplication) getApplicationContext();
                    application.removePk();
                    application.serviceStop();

                    ActivityCompat.finishAffinity(DeleteAccoutActivity.this);
                    Intent intent = new Intent(DeleteAccoutActivity.this, LoginActivity.class);
//                            출처: https://mparchive.tistory.com/81 [My Program Archive:티스토리]
                    startActivity(intent);

                }

            }

            @Override
            public void onResponseFail(ResponseBody ErrorBody) {
                Toast.makeText(DeleteAccoutActivity.this, "onResponseFail", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNetworkError(Throwable t) {
                Toast.makeText(DeleteAccoutActivity.this, "onNetworkError", Toast.LENGTH_SHORT).show();

            }
        });

        JsonObject jsonObject = new JsonObject();

        networkManager.post2Request("account","delete",jsonObject);


    }

}