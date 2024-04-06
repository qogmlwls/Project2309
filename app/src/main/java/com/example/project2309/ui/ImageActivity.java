package com.example.project2309.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.project2309.R;
import com.example.project2309.common.Message;
import com.example.project2309.network.NetworkManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.ResponseBody;

public class ImageActivity extends AppCompatActivity {

    ImageView 이미지;
    Button 다운로드;
    Bitmap image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);


        이미지 = findViewById(R.id.imageView17);

        다운로드 = findViewById(R.id.button53);

        NetworkManager.CallbackFunc<ResponseBody> 이미지받아오기;
        이미지받아오기 = new NetworkManager.CallbackFunc<ResponseBody>() {
            @Override
            public void onResponseSuccess(ResponseBody data) {


                data.byteStream();
                image = BitmapFactory.decodeStream(data.byteStream());
                이미지.setImageBitmap(image);

            }

            @Override
            public void onResponseFail(ResponseBody ErrorBody) {
                Toast(Message.RetrofitResponseSuccAndResponseFail);

            }

            @Override
            public void onNetworkError(Throwable t) {
                Toast(Message.RetrofitResponseFail);

            }
        };
        String path = getIntent().getStringExtra("image");
        NetworkManager networkManager = new NetworkManager(getApplicationContext());
        networkManager.setCallback(이미지받아오기);
        networkManager.GETImage(path);


//
//
//        String url = "http://49.247.30.164/"+path;
//        Glide.with(this).load(url).into(이미지);

        다운로드.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


//                Bitmap bitmap = BitmapFactory.decodeStream(input);

                // 내부 저장소에 이미지 저장
                File directory = Environment.getExternalStorageDirectory();
                File imageFile = new File(directory, getTime()+".png");
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(imageFile);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                Toast("다운로드 완료.");
            }
        });

    }

    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");



    private String getTime(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

    void Toast(String message){
        Toast.makeText(ImageActivity.this, message, Toast.LENGTH_SHORT).show();

    }
}