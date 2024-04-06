package com.example.project2309.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.project2309.R;
import com.example.project2309.data.Data;
import com.example.project2309.network.NetworkManager;
import com.google.gson.JsonObject;

import java.util.regex.Pattern;

import okhttp3.ResponseBody;

public class EditPasswordActivity extends AppCompatActivity {


    ImageButton 닫기버튼;

    EditText 현재비밀번호입력창, 새비밀번호입력창, 새비밀번호확인입력창;
    Button 변경버튼;
    NetworkManager networkManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        닫기버튼 = findViewById(R.id.imageButton26);
        현재비밀번호입력창 = findViewById(R.id.editTextTextPassword4);
        새비밀번호입력창 = findViewById(R.id.editTextTextPassword5);
        새비밀번호확인입력창 = findViewById(R.id.editTextTextPassword6);
        변경버튼 = findViewById(R.id.button58);

         networkManager = new NetworkManager(getApplicationContext());

        닫기버튼.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        변경버튼.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String currentPw = 현재비밀번호입력창.getText().toString();
                String newPw = 새비밀번호입력창.getText().toString();
                String newCheckPw = 새비밀번호확인입력창.getText().toString();

                if(currentPw.length() == 0){
                    다이얼로그("","현재 비밀번호를 입력해주세요.");
                }
                else if(newPw.length() == 0){
                    다이얼로그("","새 비밀번호를 입력해주세요.");
                }
                else if(newCheckPw.length() == 0){
                    다이얼로그("","새 비밀번호 확인을 입력해주세요.");
                }
                else if(!newPw.equals(newCheckPw)){
                    다이얼로그("","새 비밀번호와 새 비밀번호 확인이 일치하지 않습니다.");

                }
                else if(!isPasswordValid(newPw)){

                    다이얼로그("","비밀번호 형식이 올바르지 않습니다.\n 최소 8자 이상, 최대 16자 이하이며, \n대문자, 소문자, 숫자, 특수 문자를 \n최소한 1개씩 포함해야 합니다.");

                }
                else{
                    networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
                        @Override
                        public void onResponseSuccess(Data data) {

                            Log.i("",data.result.toString());
                            if (data.result.get("result").getAsString().equals(Data.SUCCESS)) {
                                다이얼로그("","비밀번호가 변경되었습니다.");
                                finish();
                            }
                            else if (data.result.get("result").getAsString().equals(Data.Fail)) {

                                String reasonCode = data.result.get("reasonCode").getAsString();
                                if(reasonCode.equals("400")){
                                    다이얼로그("","입력하신 현재 비밀번호를 다시 확인해주세요.");
                                }
                                else{
//                        Toast(reasonCode + " : "+reasonDescription);
                                }

                            }
                            else{

                            }
                        }

                        @Override
                        public void onResponseFail(ResponseBody ErrorBody) {

                            Toast.makeText(EditPasswordActivity.this, "onResponseFail", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNetworkError(Throwable t) {
                            Toast.makeText(EditPasswordActivity.this, "onNetworkError", Toast.LENGTH_SHORT).show();

                        }
                    });

                    JsonObject body = new JsonObject();
                    body.addProperty("currPassword", currentPw);
                    body.addProperty("updatePassword", newPw);
                    networkManager.post2Request("Login", "editpassword", body);
                }

            }
        });

    }

    private boolean isPasswordValid(String password) {
        // 최소 8자 이상, 최대 16문자 이하
        // 최소한 하나의 대문자, 소문자, 숫자, 특수 문자를 포함해야 함
        String pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,16}$";
        return Pattern.compile(pattern).matcher(password).matches();
    }

    void 다이얼로그(String title, String message){
        // 다이얼로그 빌더 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

// 다이얼로그 제목 설정 (선택 사항)
        builder.setTitle(title);

// 다이얼로그 메시지 설정 (선택 사항)
        builder.setMessage(message);

// '확인' 버튼 설정
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // '확인' 버튼 클릭 시 처리할 작업
            }
        });

//// '취소' 버튼 설정 (선택 사항)
//        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                // '취소' 버튼 클릭 시 처리할 작업
//                dialog.dismiss(); // 다이얼로그 닫기 (선택 사항)
//            }
//        });

// 다이얼로그 생성 및 표시
        AlertDialog dialog = builder.create();
        dialog.show();

    }
}