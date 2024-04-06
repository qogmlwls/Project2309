package com.example.project2309.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.project2309.R;

public class VideoActivity extends AppCompatActivity {


    VideoView 동영상;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);


        동영상 = findViewById(R.id.videoView3);


        String uriString = getIntent().getStringExtra("video");
        Uri uri = Uri.parse(uriString);



        Log.i("uri",uriString);

        MediaController mediaController = new MediaController(this);

//            // VideoView를 사용하여 동영상을 재생합니다.
        동영상.setVideoURI(uri);


        동영상.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                동영상.start();
//                mediaController.show(0);
////                    동영상.
//                동영상.pause();
                동영상.setMediaController(mediaController);
            }

        });



    }
}