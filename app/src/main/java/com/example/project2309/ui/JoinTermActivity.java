package com.example.project2309.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.project2309.R;
import com.example.project2309.data.TermData;

public class JoinTermActivity extends AppCompatActivity {

    TermData 약관정보;

    TextView 약관제목, 약관내용;


    Button 페이지닫기버튼;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_term);

        약관정보 = new TermData();

        약관제목 = (TextView) findViewById(R.id.textView13);
        약관내용 = (TextView) findViewById(R.id.textView14);
        페이지닫기버튼 = (Button) findViewById(R.id.button11);

        int TermType = getIntent().getIntExtra(TermData.TermIntentName,-1);

        약관제목.setText(약관정보.getTermTitle(TermType));
        약관내용.setText(약관정보.getTermContent(TermType));

        페이지닫기버튼.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}