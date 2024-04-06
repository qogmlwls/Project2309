package com.example.project2309.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project2309.common.Message;
import com.example.project2309.data.Data;
import com.example.project2309.data.HTTPData;
import com.example.project2309.R;
import com.example.project2309.network.NetworkManager;
import com.google.gson.JsonObject;

import java.util.regex.Pattern;

import okhttp3.ResponseBody;


public class JoinBeginMainActivity extends AppCompatActivity {


    TextView 가입이메일텍스트뷰;
    EditText 비밀번호에디터, 비밀번호확인에디터, 닉네임에디터;
    Button 가입하기버튼;

    TextView 비밀번호상태메세지, 비밀번호확인상태메세지, 닉네임상태메세지;


    boolean 비밀번호유효성, 비밀번호확인유효성, 닉네임유효성;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_begin_main);


        가입이메일텍스트뷰 = findViewById(R.id.textView5);

        비밀번호에디터 = findViewById(R.id.editTextTextPassword2);
        비밀번호확인에디터 = findViewById(R.id.editTextTextPassword3);
        닉네임에디터 = findViewById(R.id.editTextText3);

        비밀번호상태메세지 = findViewById(R.id.textView17);
        비밀번호확인상태메세지 = findViewById(R.id.textView18);
        닉네임상태메세지 = findViewById(R.id.textView19);


        가입하기버튼 = findViewById(R.id.button9);

        비밀번호유효성 = false;
        비밀번호확인유효성 = false;
        닉네임유효성 = false;


        // 가입아이디 정보 설정
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        가입이메일텍스트뷰.setText(email);


        비밀번호에디터.addTextChangedListener(createTextWatcher(비밀번호에디터));
        비밀번호확인에디터.addTextChangedListener(createTextWatcher(비밀번호확인에디터));
        닉네임에디터.addTextChangedListener(createTextWatcher(닉네임에디터));

        가입하기버튼.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                checkPassword(비밀번호에디터.getText().toString());
                checkpassword확인(비밀번호확인에디터.getText().toString());
                checkName(닉네임에디터.getText().toString());

                if(가입조건충족하는가()){

                    // 서버에 가입 요청하기.

                    // 검사 후, 형식검사 통과 시 서버에 요청.
                    // 이메일 정보 JSON형식의 변수에 넣어서 body안에 넣어 보내기.
                    JsonObject body = new JsonObject();
                    body.addProperty("email", 가입이메일텍스트뷰.getText().toString());
                    body.addProperty("password", 비밀번호에디터.getText().toString());
                    body.addProperty("nickname", 닉네임에디터.getText().toString());

                    NetworkManager networkManager = new NetworkManager(getApplicationContext());


                    networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {


                        @Override
                        public void onResponseSuccess(Data data) {
                            JsonObject responseResult = data.result;
                            String result = responseResult.get(Data.RESULT).getAsString();


                            if(result.equals(Data.SUCCESS)){
                                Toast.makeText(JoinBeginMainActivity.this, "회원가입이 되었습니다.", Toast.LENGTH_LONG).show();
                                finish();

                            }
                            else if(result.equals(Data.Fail) || result.equals(Data.ERROR)){

                                String errorCode = responseResult.get(Data.ERRCODE).getAsString();
                                String errorDescription = responseResult.get(Data.ERRDESCRIPTION).getAsString();
                                Toast.makeText(JoinBeginMainActivity.this, errorCode + " : "+ errorDescription, Toast.LENGTH_LONG).show();


                            }
                            // 예외.
                            else{
                                Toast.makeText(JoinBeginMainActivity.this, "예외 발생. 지정되지 않은 "+Data.RESULT+"값.", Toast.LENGTH_LONG).show();

                            }
                        }

                        @Override
                        public void onResponseFail(ResponseBody ErrorBody) {
                            Toast.makeText(JoinBeginMainActivity.this, Message.RetrofitResponseSuccAndResponseFail, Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onNetworkError(Throwable t) {
                            Toast.makeText(JoinBeginMainActivity.this, Message.RetrofitResponseFail, Toast.LENGTH_SHORT).show();

                        }
                    });


                    networkManager.postRequest("Join", "SignUp", body);


                }

                else{
                    가입하기버튼.setEnabled(false);

                 }

            }
        });
    }

    private boolean 가입조건충족하는가(){

        return 비밀번호유효성&&비밀번호확인유효성&&닉네임유효성;
    }

    // 비밀번호 유효성 검사 함수
    private boolean isPasswordValid(String password) {
        // 최소 8자 이상, 최대 16문자 이하
        // 최소한 하나의 대문자, 소문자, 숫자, 특수 문자를 포함해야 함
        String pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,16}$";
        return Pattern.compile(pattern).matcher(password).matches();
    }

    // 예시로 비밀번호를 검사하는 함수 호출
    private void checkPassword(String password) {

        비밀번호유효성 = false;
        if (TextUtils.isEmpty(password)) {
            // 비밀번호가 비어있음
            // TODO: 처리 로직 추가
            setTextViewErrorMsg(비밀번호상태메세지,"비밀번호를 입력해주세요.");
            return;
        }

        if (!isPasswordValid(password)) {
            // 비밀번호가 유효하지 않음
            setTextViewErrorMsg(비밀번호상태메세지,"비밀번호 형식이 올바르지 않습니다.\n 최소 8자 이상, 최대 16자 이하이며, \n대문자, 소문자, 숫자, 특수 문자를 \n최소한 1개씩 포함해야 합니다.");

            // TODO: 처리 로직 추가
            return;
        }

        // 비밀번호가 유효함
        setTextViewSuccessMsg(비밀번호상태메세지, "사용 가능한 비밀번호입니다.");
        비밀번호유효성 = true;
        // TODO: 처리 로직 추가
    }

    // 예시로 비밀번호를 검사하는 함수 호출
    private void checkName(String nickname) {

        닉네임유효성 = false;
        if (TextUtils.isEmpty(nickname)) {
            // 닉네임이 비어있음
            // TODO: 처리 로직 추가
            setTextViewErrorMsg(닉네임상태메세지,"닉네임을 입력해주세요.");
            return;
        }

     
        // 닉네임이 유효함
        닉네임유효성 = true;
        setTextViewSuccessMsg(닉네임상태메세지, "사용 가능한 닉네임입니다.");
        // TODO: 처리 로직 추가
    }
    // 예시로 비밀번호를 검사하는 함수 호출
    private void checkpassword확인(String 비밀번호확인) {

        비밀번호확인유효성 = false;
        if (TextUtils.isEmpty(비밀번호확인)) {
            // 닉네임이 비어있음
            // TODO: 처리 로직 추가
            setTextViewErrorMsg(비밀번호확인상태메세지,"비밀번호 확인을 입력해주세요.");
            return;
        }

        if(!비밀번호에디터.getText().toString().equals(비밀번호확인)){

            setTextViewErrorMsg(비밀번호확인상태메세지,"비밀번호와 일치하지 않습니다.");

            return;
        }


        // 닉네임이 유효함
        비밀번호확인유효성 = true;
        setTextViewSuccessMsg(비밀번호확인상태메세지, "비밀번호와 일치합니다.");
        // TODO: 처리 로직 추가
    }


    // 이제 이 함수를 사용하여 비밀번호를 검사할 수 있습니다.
    // checkPassword("비밀번호");
    void setTextViewSuccessMsg(TextView view, String msg){
        view.setText(msg);
        view.setTextColor(Color.GREEN);
        view.setVisibility(View.VISIBLE);
    }
    void setTextViewErrorMsg(TextView view, String msg){
        view.setText(msg);
        view.setTextColor(Color.RED);
        view.setVisibility(View.VISIBLE);
    }

    private TextWatcher createTextWatcher(final EditText editText) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // 입력전
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editText == 비밀번호에디터) {
                    checkPassword(비밀번호에디터.getText().toString());
                } else if (editText == 비밀번호확인에디터) {
                    checkpassword확인(비밀번호확인에디터.getText().toString());
                } else if (editText == 닉네임에디터) {
                    checkName(닉네임에디터.getAccessibilityClassName().toString());
                }

                가입하기버튼.setEnabled(가입조건충족하는가());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 입력후
                if (editText == 비밀번호에디터) {
                    checkPassword(비밀번호에디터.getText().toString());
                } else if (editText == 비밀번호확인에디터) {
                    checkpassword확인(비밀번호확인에디터.getText().toString());
                } else if (editText == 닉네임에디터) {
                    checkName(닉네임에디터.getAccessibilityClassName().toString());
                }

                가입하기버튼.setEnabled(가입조건충족하는가());
            }
        };
    }



    void setTextViewInit(TextView view){
        view.setText("");
        view.setTextColor(Color.TRANSPARENT);
        view.setVisibility(View.GONE);
    }


}