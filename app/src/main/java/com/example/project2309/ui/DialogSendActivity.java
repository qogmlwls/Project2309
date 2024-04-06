package com.example.project2309.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.project2309.R;
import com.example.project2309.adapter.CR_PostAdapter;
import com.example.project2309.adapter.ChatImageAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DialogSendActivity extends AppCompatActivity {


    List<Uri> list;

    ChatImageAdapter adapter;



    RecyclerView 이미지리사이클러뷰;

    Button 전송;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_send);

        list = new ArrayList<>();
        이미지리사이클러뷰 = findViewById(R.id.recyclerView5);

        전송 = findViewById(R.id.button48);


        Intent intent = getIntent();
        if (intent != null) {
            // Uri 배열을 받아옵니다.


            ArrayList<Uri> receivedUris = intent.getParcelableArrayListExtra("uris");

//            Uri[] receivedUris = (Uri[]) intent.getParcelableArrayExtra("uris");

            if (receivedUris != null) {
                // Uri 배열에서 Uri를 추출하여 사용합니다.
                for (Uri uri : receivedUris) {
                    // TODO: Uri에 대한 작업 수행
//                    Bitmap bitmap = getBitmapFromUri(uri);
                    list.add(uri);

                }
            }
        }

        adapter = new ChatImageAdapter(list,this);

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(이미지리사이클러뷰);
        이미지리사이클러뷰.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));
        이미지리사이클러뷰.setAdapter(adapter);



        전송.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent resultIntent = new Intent();

                // 먼저 Uri 배열을 생성하고 데이터를 추가합니다.
                Uri[] uriArray = new Uri[list.size()]; // 예시로 Uri 배열의 크기를 2로 설정
                // Display selected images
                for (int i = 0; i < list.size(); i++) {
                    uriArray[i] = list.get(i);

                }

// Convert Uri array to ArrayList (ParcelableArrayList)
                ArrayList<Uri> uriArrayList = new ArrayList<>(Arrays.asList(uriArray));

                resultIntent.putExtra("uris", uriArrayList); // "uris"라는 키로 Uri 배열 추가



                setResult(RESULT_OK,resultIntent);


                finish();
            }
        });


    }





}