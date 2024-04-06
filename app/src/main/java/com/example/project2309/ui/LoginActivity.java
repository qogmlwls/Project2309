package com.example.project2309.ui;

import androidx.appcompat.app.AppCompatActivity;


import com.example.project2309.MyApplication;
import com.example.project2309.data.Data;
import com.example.project2309.data.HTTPData;
import com.example.project2309.network.NetworkManager;
import com.example.project2309.R;
import com.example.project2309.common.Message;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.project2309.common.TextViewStyleManager;
import com.google.gson.JsonObject;
import okhttp3.ResponseBody;

public class LoginActivity extends AppCompatActivity {


    Button loginTojoin,loginToFindPw,로그인버튼;
    EditText 아이디에디터, 비밀번호에디터;

    TextView 로그인상태메세지;

    NetworkManager networkManager;

    // 상수 정의 (HTTPData 클래스에서 관리하는 것으로 가정)
    private static final String CATEGORY_LOGIN_INFO = HTTPData.로그인정보.catagory;
    private static final String PATH_LOGIN_INFO = HTTPData.로그인정보.path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        로그인버튼 = findViewById(R.id.button2);
        아이디에디터 = findViewById(R.id.editTextText);
        비밀번호에디터 = findViewById(R.id.editTextTextPassword);

        로그인상태메세지 = findViewById(R.id.textView21);
        loginTojoin = (Button) findViewById(R.id.loginTojoin);
        loginToFindPw = (Button) findViewById(R.id.button3);

        networkManager = new NetworkManager(getApplicationContext());

        로그인버튼.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 아이디 공백인지 확인
                // 비밀번호 공백인지 확인
                // 서버에게 로그인 요청.

                String id = 아이디에디터.getText().toString();
                String pw = 비밀번호에디터.getText().toString();
                
                if(id.length() == 0){
                    setErrorStyledText(Message.로그인_아이디미입력);
                }
                else if(pw.length() == 0){
                    setErrorStyledText(Message.로그인_비밀번호미입력);

                }
                else{


                    // 서버에게 로그인 요청.

                    // 검사 후, 형식검사 통과 시 서버에 요청.
                    // 이메일 정보 JSON형식의 변수에 넣어서 body안에 넣어 보내기.
                    JsonObject body = new JsonObject();
                    body.addProperty("id",id);
                    body.addProperty("pw", pw);


                    networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
                        @Override
                        public void onResponseSuccess(Data data) {
                            JsonObject responseResult = data.result;


                            Log.i("LoginActivity",data.result.toString());
                            String result;

                            // Data.RESULT 값이 없으면 예외 처리
                            if (responseResult.has(Data.RESULT)) {
                                result = responseResult.get(Data.RESULT).getAsString();
                                // 여기에 결과 처리 코드 추가
                            } else {
                                // Data.RESULT가 없는 경우에 대한 예외 처리
                                // 예: 로그를 남기거나, 적절한 조치를 취함
                                setErrorStyledText(Message.ResponseParsingException);

//                                Log.e("LoginActivity", "Data.RESULT is missing");
                                return;
                            }

                            if(result.equals(Data.SUCCESS)){

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                                String pk;
                                pk = responseResult.get("pk").getAsString();
                                MyApplication application = (MyApplication)getApplicationContext();

                                if(!pk.equals(application.getPk())){
                                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                    notificationManager.cancelAll();
                                }

                                // 소켓 실행.
                                application.setPk(pk);
                                application.startSocketService(pk);

                            }
                            else if(result.equals(Data.Fail)){
                                setErrorStyledText(Message.로그인_로그인실패);
                            }
                            else if(result.equals(Data.ERROR)){

                                String errorCode = responseResult.get(Data.ERRCODE).getAsString();
                                String errorDescription = responseResult.get(Data.ERRDESCRIPTION).getAsString();
                                setErrorStyledText(errorCode + " : "+ errorDescription);
                            }
                            // 예외.
                            else{
                                setErrorStyledText(Message.ResponseException );
                            }
                        }

                        @Override
                        public void onResponseFail(ResponseBody ErrorBody) {
                            setErrorStyledText(Message.RetrofitResponseSuccAndResponseFail);

                        }

                        @Override
                        public void onNetworkError(Throwable t) {
                            setErrorStyledText(Message.RetrofitResponseFail);

                        }
                    });


                    networkManager.postRequest(CATEGORY_LOGIN_INFO, PATH_LOGIN_INFO, body);


                }

            }
        });


        loginTojoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, JoinAgreeActivity.class);
                startActivity(intent);
            }
        });


        loginToFindPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, FindPasswordActivity.class);
                startActivity(intent);
            }
        });


    }

    void setErrorStyledText(String message){
        TextViewStyleManager.setErrorStyledText(로그인상태메세지,message);
    }

}