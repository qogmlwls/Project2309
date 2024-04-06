package com.example.project2309.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.example.project2309.R;
import com.example.project2309.data.TermData;

public class JoinAgreeActivity extends AppCompatActivity {

    Button 이메일인증페이지로이동, 이용약관동의보기, 개인정보취급약관동의보기;

    CheckBox 이용약관동의, 개인정보취급약관동의, 만14세동의, 모두동의;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_agree);


        이메일인증페이지로이동 = (Button) findViewById(R.id.button4);
        이메일인증페이지로이동.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JoinAgreeActivity.this, JoinEmailAuthActivity.class);
                startActivity(intent);
                finish();
            }
        });

        이용약관동의 = (CheckBox) findViewById(R.id.checkBox2);
        개인정보취급약관동의 = (CheckBox) findViewById(R.id.checkBox3);
        만14세동의 = (CheckBox) findViewById(R.id.checkBox);
        모두동의 = (CheckBox) findViewById(R.id.checkBox4);

        이용약관동의보기 = (Button) findViewById(R.id.button5);
        개인정보취급약관동의보기 = (Button) findViewById(R.id.button6);

        이용약관동의.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                모두동의상태설정();
                다음버튼상태설정();
            }
        });
        개인정보취급약관동의.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                모두동의상태설정();
                다음버튼상태설정();
            }
        });
        만14세동의.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                모두동의상태설정();
                다음버튼상태설정();
            }
        });
        모두동의.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 다른 check box의 상태를 모두동의 check box 상태와 같게하기.
                이용약관동의.setChecked(모두동의.isChecked());
                개인정보취급약관동의.setChecked(모두동의.isChecked());
                만14세동의.setChecked(모두동의.isChecked());

                다음버튼상태설정();
            }
        });

        이용약관동의보기.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JoinAgreeActivity.this, JoinTermActivity.class);
                intent.putExtra(TermData.TermIntentName,TermData.이용약관);
                startActivity(intent);
            }
        });
        개인정보취급약관동의보기.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JoinAgreeActivity.this, JoinTermActivity.class);
                intent.putExtra(TermData.TermIntentName,TermData.개인정보처리방침);
                startActivity(intent);
            }
        });

    }


    void 모두동의상태설정(){

        Log("모두동의상태설정 함수 실행.");
        // 모든 check box의 체크 상태를 확인하고
        // 모두 체크되었다면 모두동의도 체크, 하나라도 체크가 안되어있가면 미체크로 상태변경.
        Log("모두 체크되었는지 : "+Boolean.toString(이용약관동의.isChecked()&&개인정보취급약관동의.isChecked()&&만14세동의.isChecked()));
        모두동의.setChecked(이용약관동의.isChecked()&&개인정보취급약관동의.isChecked()&&만14세동의.isChecked());

    }


    void 다음버튼상태설정(){

        Log("다음버튼상태설정 함수 실행.");
        // 현재 check box의 체크 상태를 확인하고
        // 필수 부분이 모두 체크되었다면 활성화, 하나라도 체크가 안되었다면 비활성화한다.
        Log("필수부분이 모두 체크되었는지 : "+Boolean.toString(이용약관동의.isChecked()&&개인정보취급약관동의.isChecked()&&만14세동의.isChecked()));

        이메일인증페이지로이동.setEnabled(이용약관동의.isChecked()&&개인정보취급약관동의.isChecked()&&만14세동의.isChecked());

    }

    void Log(String msg){
        Log.i("회원가입, 약관동의 페이지",msg);
    }

}