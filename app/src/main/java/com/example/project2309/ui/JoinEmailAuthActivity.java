package com.example.project2309.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project2309.data.Data;
import com.example.project2309.data.HTTPData;
import com.example.project2309.R;
import com.example.project2309.network.NetworkManager;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;


public class JoinEmailAuthActivity extends AppCompatActivity {


    EditText 이메일입력창, 인증코드입력창;
    TextView 이메일상태메세지텍스트뷰, 인증코드상태메세지텍스트뷰;
    Button 계정정보입력페이지로이동버튼,이메일중복검사버튼, 인증번호요청버튼,인증코드확인버튼;
    ConstraintLayout 이메일입력부터인증번호요청까지의레이아웃, 인증코드입력레이아웃;



    // 다음 클릭시 -> 중복검사에 사용한 이메일과 현재 이메일 창의 값이 다르면,
    // 인증시 사용한 이메일과 현재 입력된 이메일 값이 다릅니다. 다시 인증해주세요.
    // 후 첫번째 단계로 이동. (중복검사부터)
    String AuthEmailValue;
    HTTPData.회원가입이메일인증정보 이메일인증정보;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_email_auth);

        AuthEmailValue = "";
        이메일인증정보 = new HTTPData.회원가입이메일인증정보();

        이메일입력부터인증번호요청까지의레이아웃 = findViewById(R.id.constraintLayout3);
        인증코드입력레이아웃 = findViewById(R.id.constraintLayout2);


        이메일입력창 = (EditText)findViewById(R.id.editTextTextEmailAddress);
        이메일중복검사버튼 = (Button)findViewById(R.id.button7);
        이메일상태메세지텍스트뷰 = (TextView)findViewById(R.id.textView15);
        인증번호요청버튼 = (Button)findViewById(R.id.button12);

        인증코드입력창 = (EditText) findViewById(R.id.editTextText2);
        인증코드확인버튼 = (Button)findViewById(R.id.button13);

        계정정보입력페이지로이동버튼 = (Button) findViewById(R.id.button8);

        인증코드상태메세지텍스트뷰 = (TextView) findViewById(R.id.textView16);


        이메일중복검사버튼.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(중복검사통과한후이메일변경했는지()){
                    이메일변경해서인증단계초기화();
                    Toast.makeText(JoinEmailAuthActivity.this, 이메일인증정보.인증도중이메일변경시출력되는메세지, Toast.LENGTH_SHORT).show();
                }



                // 이메일상태메세지텍스트뷰 상태 초기화.
                이메일상태메세지텍스트뷰.setText("");
                이메일상태메세지텍스트뷰.setTextColor(Color.TRANSPARENT);
                이메일상태메세지텍스트뷰.setVisibility(View.GONE);

                // 이메일 작성 안되어있으면
                // MSG 창에 이메일(아이디)를 입력해주세요. 뜨도록.
                String InputEmailValue = 이메일입력창.getText().toString();
                if (InputEmailValue.length() == 0) {
                    이메일상태메세지텍스트뷰.setText(이메일인증정보.이메일미입력상태메세지);
                    이메일상태메세지텍스트뷰.setTextColor(Color.RED);
                    이메일상태메세지텍스트뷰.setVisibility(View.VISIBLE);
                    return;
                }

                // 유효성 검사. (나중에)


                // 검사 후, 형식검사 통과 시 서버에 요청.
                // 이메일 정보 JSON형식의 변수에 넣어서 body안에 넣어 보내기.
                JsonObject body = new JsonObject();
                body.addProperty(이메일인증정보.중복검사bodykeyValue, InputEmailValue);

                NetworkManager networkManager = new NetworkManager(getApplicationContext());

                
                networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
                    @Override
                    public void onResponseSuccess(Data data) {

                        JsonObject responseResult = data.result;
                        String result = responseResult.get(Data.RESULT).getAsString();


                        if(result.equals(Data.PASS)){

                            인증번호요청버튼.setEnabled(true);
                            AuthEmailValue = 이메일입력창.getText().toString();
                            이메일상태메세지텍스트뷰.setText(이메일인증정보.중복검사통과이메일상태메세지);
                            int color = ContextCompat.getColor(getBaseContext(), R.color.green);
                            이메일상태메세지텍스트뷰.setTextColor(color);
                            이메일상태메세지텍스트뷰.setVisibility(View.VISIBLE);

                        }
                        else if(result.equals(Data.NOTPASS)){
                            이메일상태메세지텍스트뷰.setText(이메일인증정보.중복이메일상태메세지);
                            이메일상태메세지텍스트뷰.setTextColor(Color.RED);
                            이메일상태메세지텍스트뷰.setVisibility(View.VISIBLE);
                        }
                        else if(result.equals(Data.ERROR)){

                            String errorCode = responseResult.get(Data.ERRCODE).getAsString();
                            String errorDescription = responseResult.get(Data.ERRDESCRIPTION).getAsString();
                            이메일상태메세지텍스트뷰.setText(errorCode + " : "+ errorDescription);
                            이메일상태메세지텍스트뷰.setTextColor(Color.RED);
                            이메일상태메세지텍스트뷰.setVisibility(View.VISIBLE);

                        }
                        // 예외.
                        else{
                            이메일상태메세지텍스트뷰.setText("예외 발생. 지정되지 않은 "+Data.RESULT+"값.");
                            이메일상태메세지텍스트뷰.setTextColor(Color.RED);
                            이메일상태메세지텍스트뷰.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onResponseFail(ResponseBody ErrorBody) {
                        이메일상태메세지텍스트뷰.setText(HTTPData.응답실패에러발생);
                        이메일상태메세지텍스트뷰.setTextColor(Color.RED);
                        이메일상태메세지텍스트뷰.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNetworkError(Throwable t) {
                        이메일상태메세지텍스트뷰.setText(HTTPData.통신네트워크에러발생);
                        이메일상태메세지텍스트뷰.setTextColor(Color.RED);
                        이메일상태메세지텍스트뷰.setVisibility(View.VISIBLE);
                    }
                });


                // 요청 전송.
                networkManager.postRequest(이메일인증정보.catagory, 이메일인증정보.중복검사path, body);


            }
        });


        인증번호요청버튼.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //
                if (!중복검사통과한후이메일변경했는지()){


                    // 검사 후, 형식검사 통과 시 서버에 요청.
                    // 이메일 정보 JSON형식의 변수에 넣어서 body안에 넣어 보내기.
                    JsonObject body = new JsonObject();
                    body.addProperty(이메일인증정보.인증요청bodykeyValue, 이메일입력창.getText().toString());

                    NetworkManager networkManager = new NetworkManager(getApplicationContext());


                    networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
                        @Override
                        public void onResponseSuccess(Data data) {
                            JsonObject responseResult = data.result;
                            String result = responseResult.get(Data.RESULT).getAsString();


                            if(result.equals(Data.SUCCESS)){

                                인증코드입력레이아웃.setVisibility(View.VISIBLE);

                            }
                            else if(result.equals(Data.Fail) || result.equals(Data.ERROR)){

                                String errorCode = responseResult.get(Data.ERRCODE).getAsString();
                                String errorDescription = responseResult.get(Data.ERRDESCRIPTION).getAsString();
                                이메일상태메세지텍스트뷰.setText(errorCode + " : "+ errorDescription);
                                이메일상태메세지텍스트뷰.setTextColor(Color.RED);
                                이메일상태메세지텍스트뷰.setVisibility(View.VISIBLE);

                            }
                            // 예외.
                            else{
                                이메일상태메세지텍스트뷰.setText("예외 발생. 지정되지 않은 "+Data.RESULT+"값.");
                                이메일상태메세지텍스트뷰.setTextColor(Color.RED);
                                이메일상태메세지텍스트뷰.setVisibility(View.VISIBLE);
                            }

                        }

                        @Override
                        public void onResponseFail(ResponseBody ErrorBody) {
                            이메일상태메세지텍스트뷰.setText(HTTPData.응답실패에러발생);
                            이메일상태메세지텍스트뷰.setTextColor(Color.RED);
                            이메일상태메세지텍스트뷰.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onNetworkError(Throwable t) {
                            이메일상태메세지텍스트뷰.setText(HTTPData.통신네트워크에러발생);
                            이메일상태메세지텍스트뷰.setTextColor(Color.RED);
                            이메일상태메세지텍스트뷰.setVisibility(View.VISIBLE);
                        }
                    });


                    networkManager.postRequest(이메일인증정보.catagory, 이메일인증정보.인증요청path, body);


                }
                else{
                    이메일변경해서인증단계초기화();
                    Toast.makeText(JoinEmailAuthActivity.this, 이메일인증정보.인증도중이메일변경시출력되는메세지, Toast.LENGTH_SHORT).show();

                }



            }
        });


        인증코드확인버튼.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //
                if(!중복검사통과한후이메일변경했는지()) {

                    // 이메일상태메세지텍스트뷰 상태 초기화.
                    인증코드상태메세지텍스트뷰.setText("");
                    인증코드상태메세지텍스트뷰.setTextColor(Color.TRANSPARENT);
                    인증코드상태메세지텍스트뷰.setVisibility(View.GONE);


                    // 이메일 작성 안되어있으면
                    // MSG 창에 이메일(아이디)를 입력해주세요. 뜨도록.
                    String InputEmailValue = 인증코드입력창.getText().toString();
                    if (InputEmailValue.length() == 0) {
                        인증코드상태메세지텍스트뷰.setText(이메일인증정보.인증코드미입력상태메세지);
                        인증코드상태메세지텍스트뷰.setTextColor(Color.RED);
                        인증코드상태메세지텍스트뷰.setVisibility(View.VISIBLE);
                        return;
                    }


                    // 검사 후, 형식검사 통과 시 서버에 요청.
                    // 이메일 정보 JSON형식의 변수에 넣어서 body안에 넣어 보내기.
                    JsonObject body = new JsonObject();
                    body.addProperty(이메일인증정보.인증확인요청EmailBodyKeyValue, 이메일입력창.getText().toString());
                    body.addProperty(이메일인증정보.인증확인요청CodeBodyKeyValue, 인증코드입력창.getText().toString());

                    NetworkManager networkManager = new NetworkManager(getApplicationContext());


                    networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
                        @Override
                        public void onResponseSuccess(Data data) {
                            JsonObject responseResult = data.result;
                            String result = responseResult.get(Data.RESULT).getAsString();


                            if(result.equals(Data.PASS)){

                                인증코드상태메세지텍스트뷰.setText(이메일인증정보.인증코드일치성검사통과메세지);
                                int color = ContextCompat.getColor(getBaseContext(), R.color.green);

                                인증코드상태메세지텍스트뷰.setTextColor(color);
                                인증코드상태메세지텍스트뷰.setVisibility(View.VISIBLE);

                                계정정보입력페이지로이동버튼.setEnabled(true);


                            }
                            else if(result.equals(Data.NOTPASS)){

                                인증코드상태메세지텍스트뷰.setText(이메일인증정보.인증코드일치성검사미통과메세지);
                                인증코드상태메세지텍스트뷰.setTextColor(Color.RED);
                                인증코드상태메세지텍스트뷰.setVisibility(View.VISIBLE);

                            }
                            else if(result.equals(Data.ERROR)){

                                String errorCode = responseResult.get(Data.ERRCODE).getAsString();
                                String errorDescription = responseResult.get(Data.ERRDESCRIPTION).getAsString();

                                인증코드상태메세지텍스트뷰.setText(errorCode + " : "+ errorDescription);
                                인증코드상태메세지텍스트뷰.setTextColor(Color.RED);
                                인증코드상태메세지텍스트뷰.setVisibility(View.VISIBLE);


                            }
                            // 예외.
                            else{

                                인증코드상태메세지텍스트뷰.setText("예외 발생. 지정되지 않은 "+Data.RESULT+"값.");
                                인증코드상태메세지텍스트뷰.setTextColor(Color.RED);
                                인증코드상태메세지텍스트뷰.setVisibility(View.VISIBLE);

                            }
                        }

                        @Override
                        public void onResponseFail(ResponseBody ErrorBody) {
                            인증코드상태메세지텍스트뷰.setText(HTTPData.응답실패에러발생);
                            인증코드상태메세지텍스트뷰.setTextColor(Color.RED);
                            인증코드상태메세지텍스트뷰.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onNetworkError(Throwable t) {
                            인증코드상태메세지텍스트뷰.setText(HTTPData.통신네트워크에러발생);
                            인증코드상태메세지텍스트뷰.setTextColor(Color.RED);
                            인증코드상태메세지텍스트뷰.setVisibility(View.VISIBLE);
                        }
                    });


                    networkManager.postRequest(이메일인증정보.catagory, 이메일인증정보.인증확인요청path, body);

                }
                else{
                    이메일변경해서인증단계초기화();
                    Toast.makeText(JoinEmailAuthActivity.this, 이메일인증정보.인증도중이메일변경시출력되는메세지, Toast.LENGTH_SHORT).show();

                }



            }
        });


        계정정보입력페이지로이동버튼.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                // 가장 최근에 중복검사 통과한 이메일과 현재 입력된 이메일이 일치하는지 확인하고,
                // 인증시 사용한 이메일과 현재 입력된 이메일 값이 다릅니다. 다시 인증해주세요.
                // 후 첫번째 단계로 이동. (중복검사부터)
                if(!중복검사통과한후이메일변경했는지()){
                    Intent intent = new Intent(JoinEmailAuthActivity.this, JoinBeginMainActivity.class);

                    intent.putExtra("email",AuthEmailValue);

                    startActivity(intent);
                    finish();
                }
                else{
                    이메일변경해서인증단계초기화();
                    Toast.makeText(JoinEmailAuthActivity.this, 이메일인증정보.인증도중이메일변경시출력되는메세지, Toast.LENGTH_SHORT).show();

                }

            }
        });


    }

    void Log(String msg){
        Log.i("회원가입, 이메일인증 페이지",msg);
    }

    // AuthEmailValue(인증하고있는 이메일)이 ""(공백)이 아니고, 현재이메일입력창 값과 불일치한다면,
    // 인증 1번째 단계로 이동, 인증 초기화.
    // true : 변경함. false : 미변경.
    boolean 중복검사통과한후이메일변경했는지(){

        return (!AuthEmailValue.equals("")&&!AuthEmailValue.equals(이메일입력창.getText().toString()));


    }

    void 이메일변경해서인증단계초기화(){

        인증번호요청버튼.setEnabled(false);
        // 이메일상태메세지텍스트뷰 상태 초기화.
        이메일상태메세지텍스트뷰.setText("");
        이메일상태메세지텍스트뷰.setTextColor(Color.TRANSPARENT);
        이메일상태메세지텍스트뷰.setVisibility(View.GONE);

        인증코드입력창.setText("");
        // 인증코드상태메세지텍스트뷰 상태 초기화.
        인증코드상태메세지텍스트뷰.setText("");
        인증코드상태메세지텍스트뷰.setTextColor(Color.TRANSPARENT);
        인증코드상태메세지텍스트뷰.setVisibility(View.GONE);


        AuthEmailValue = "";

        인증코드입력레이아웃.setVisibility(View.GONE);
        계정정보입력페이지로이동버튼.setEnabled(false);



    }
    
}