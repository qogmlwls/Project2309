package com.example.project2309.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project2309.R;
import com.example.project2309.data.Data;
import com.example.project2309.network.NetworkManager;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;

public class UpdateCommentActivity extends AppCompatActivity {



    ImageButton 닫기;
    EditText 댓글;
    TextView 타이틀;

    Button 수정;


    int comment_id;
    int parent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_comment);

        comment_id = getIntent().getIntExtra("comment_id",-1);
        if(comment_id == -1){
            Toast.makeText(this, "문제 발생.", Toast.LENGTH_SHORT).show();
        }

        String content = getIntent().getStringExtra("content");

        parent = getIntent().getIntExtra("parent",0);
//        intent.putExtra("comment_id", item.comment_id);
//        intent.putExtra("content", item.content);
//        intent.putExtra("parent", item.parent);

        닫기 = findViewById(R.id.imageButton13);

        타이틀 = findViewById(R.id.textView45);

        if(parent == -1){
            타이틀.setText("댓글 수정");
            
        }
        else{
            타이틀.setText("답글 수정");
        }

        댓글 = findViewById(R.id.editTextTextMultiLine2);

        수정 = findViewById(R.id.button38);

        닫기.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        수정.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(댓글.getText().toString().equals("")){
                    Toast.makeText(UpdateCommentActivity.this, "댓글을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                NetworkManager networkManager = new NetworkManager(getApplicationContext());


                networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
                    @Override
                    public void onResponseSuccess(Data data) {

//                (data.result.get("result").getAsString().equals(Data.SUCCESS)) {
                        if(data.result.get("result").getAsString().equals(Data.SUCCESS)) {

                            // 리사이클러뷰 상단에 내가 작성한 글 보여지도록.
                            Intent resultIntent = new Intent();

                            resultIntent.putExtra("comment_id",comment_id);
                            resultIntent.putExtra("content",댓글.getText().toString());
                            resultIntent.putExtra("parent",parent);
                            resultIntent.putExtra("update_date",data.result.get("update_date").getAsString());

                            setResult(RESULT_OK, resultIntent);
                            finish();  // Activity 종료하고 결과 반환


                        }
                    }

                    @Override
                    public void onResponseFail(ResponseBody ErrorBody) {
                        Toast.makeText(UpdateCommentActivity.this, "onResponseFail", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNetworkError(Throwable t) {
                        Toast.makeText(UpdateCommentActivity.this, "onNetworkError", Toast.LENGTH_SHORT).show();

                    }
                });


                JsonObject body = new JsonObject();
                body.addProperty("comment_id", comment_id);
                body.addProperty("content", 댓글.getText().toString());

//        body.addProperty("content", 댓글에디터.getText().toString());

//                update_date
                networkManager.post2Request("comment", "update", body);

            }
        });


        댓글.setText(content);


    }



}