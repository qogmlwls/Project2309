package com.example.project2309.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project2309.MyApplication;
import com.example.project2309.R;
import com.example.project2309.data.Data;
import com.example.project2309.network.NetworkManager;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;

public class FindPasswordActivity extends AppCompatActivity {

    String TAG = "FindPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        EditText 이메일입력창 = findViewById(R.id.editTextText4);
        Button 임시비밀번호발송버튼 = findViewById(R.id.button10);

        TextView 이메일상태메세지 = findViewById(R.id.textView74);

        NetworkManager networkManager;
        networkManager = new NetworkManager(getApplicationContext());

        임시비밀번호발송버튼.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                이메일상태메세지.setVisibility(View.GONE);
                // 이메일 작성 안되어있으면
                // MSG 창에 이메일(아이디)를 입력해주세요. 뜨도록.
                String InputEmailValue = 이메일입력창.getText().toString();
                if (InputEmailValue.length() == 0) {
                    이메일상태메세지.setText("이메일을 입력해주세요.");
                    이메일상태메세지.setVisibility(View.VISIBLE);

                }
                // 나중에 유효성검사 추가.
                else{
                    //서버에 임시 비밀번호 발송 요청.

                    networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
                        @Override
                        public void onResponseSuccess(Data data) {

                            Log.i(TAG, data.result.toString());

                            // 가입한 이메일이 아닌 경우,
                            // 이메일 전송에 문제가 발생한 경우
                            // db 변경에 문제가 발생하거나 등 서버에서 작업하다가 문제가 발생한 경우

                            if (data.result.get("result").getAsString().equals(Data.SUCCESS)) {
//                                임시비밀번호를 [이메일] 로 발송하였습니다.
//                                임시 비밀번호로 로그인을 하신 후
//                                나의정보 > 설정 > 비밀번호 수정에서 비밀번호를 수정해 주세요.
                                showDialog("임시비밀번호를 [이메일] 로 발송하였습니다.\n임시 비밀번호로 로그인을 하신 후 나의정보 > 설정 > 비밀번호 수정에서 비밀번호를 수정해 주세요.");
                            }
                            else if (data.result.get("result").getAsString().equals(Data.Fail)) {

                                String reasonCode = data.result.get(Data.REASONCODE).getAsString();
                                String reasonDescription = data.result.get(Data.REASONDESCRIPTION).getAsString();
                                if(reasonCode.equals("401")){
                                    이메일상태메세지.setText("가입하지 않은 이메일입니다.");
                                    이메일상태메세지.setVisibility(View.VISIBLE);
                                }
                                else{
                                    이메일상태메세지.setText(reasonCode + " : "+ reasonDescription);
                                    이메일상태메세지.setVisibility(View.VISIBLE);
                                }



                            }
                            else{
                                String errorCode = data.result.get(Data.ERRCODE).getAsString();
                                String errorDescription = data.result.get(Data.ERRDESCRIPTION).getAsString();
                                이메일상태메세지.setText(errorCode + " : "+ errorDescription);
                                이메일상태메세지.setVisibility(View.VISIBLE);
                            }

                        }

                        @Override
                        public void onResponseFail(ResponseBody ErrorBody) {
                            Toast.makeText(FindPasswordActivity.this, "onResponseFail", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNetworkError(Throwable t) {
                            Toast.makeText(FindPasswordActivity.this, "onNetworkError", Toast.LENGTH_SHORT).show();

                        }
                    });

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("email", InputEmailValue);
                    networkManager.post2Request("account","temporaryPassword",jsonObject);
//                    temporaryPassword


                }

            }
        });

    }

    private void showDialog(String message) {

        Log.i(TAG, "--------------------------------------------------");
        AlertDialog.Builder builder = new AlertDialog.Builder(FindPasswordActivity.this);
        builder.setMessage(message);

        builder.setNegativeButton("확인", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                finish();
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();


        Log.i(TAG, "--------------------------------------------------");

    }

}