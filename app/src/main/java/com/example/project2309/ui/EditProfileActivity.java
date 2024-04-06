package com.example.project2309.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project2309.data.Data;
import com.example.project2309.network.ApiService;
import com.example.project2309.network.CookieInterceptor;
import com.example.project2309.network.NetworkManager;
import com.example.project2309.R;
import com.example.project2309.common.Message;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditProfileActivity extends AppCompatActivity {



    ImageButton 나가기버튼,편집하기버튼;

    ImageView 프로필이미지;

    EditText 닉네임에디터, 소개글에디터;

    Button 프로필이미지추가수정, 기본이미지로수정버튼;

    TextView 닉네임상태메세지;
    byte[] 이미지;

    NetworkManager networkManager;




    NetworkManager.CallbackFunc<Data> 로그인이후내정보요청후콜백함수;
    NetworkManager.CallbackFunc<ResponseBody> 이미지받아오기;

    NetworkManager.CallbackFunc<Data> 프로필편집요청후콜백함수;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        나가기버튼 = findViewById(R.id.imageButton2);

        편집하기버튼 = findViewById(R.id.imageButton3);


        프로필이미지 = findViewById(R.id.imageView4);

        닉네임에디터 = findViewById(R.id.editTextText5);
        소개글에디터 = findViewById(R.id.editTextText6);


        프로필이미지추가수정 = findViewById(R.id.button21);
        기본이미지로수정버튼 = findViewById(R.id.button22);

        닉네임상태메세지 = findViewById(R.id.textView27);
        
        
        로그인이후내정보요청후콜백함수 = new NetworkManager.CallbackFunc<Data>() {
            @Override
            public void onResponseSuccess(Data data) {
                JsonObject responseResult = data.result;
                String result;

                // Data.RESULT 값이 없으면 예외 처리
                if (responseResult.has(Data.RESULT)) {
                    result = responseResult.get(Data.RESULT).getAsString();
                    // 여기에 결과 처리 코드 추가
                } else {
                    // Data.RESULT가 없는 경우에 대한 예외 처리
                    // 예: 로그를 남기거나, 적절한 조치를 취함
                    Toast(Message.ResponseParsingException);
                    return;
                }


                if(result.equals(Data.SUCCESS)){

                    String nickName = responseResult.get("name").getAsString();
                    String introText = responseResult.get("introText").getAsString();
                    String profileURL = responseResult.get("profile").getAsString();


                    닉네임에디터.setText(nickName);
                    소개글에디터.setText(introText);

                    if(profileURL.length()!=0){
                        networkManager.setCallback(이미지받아오기);
                        networkManager.GETImage(profileURL);

                        프로필이미지추가수정.setText("프로필 이미지 변경");
                        기본이미지로수정버튼.setEnabled(true);
                    }
                    else{
                        프로필이미지추가수정.setText("프로필 이미지 추가");
                        기본이미지로수정버튼.setEnabled(false);
                    }

                }
                else if(result.equals(Data.Fail) || result.equals(Data.ERROR)){

                    String errorCode = responseResult.get(Data.ERRCODE).getAsString();
                    String errorDescription = responseResult.get(Data.ERRDESCRIPTION).getAsString();
                    Toast(errorCode + " : "+ errorDescription);

                }
                // 예외.
                else{
                    Toast(Message.ResponseException);
                }
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
        이미지받아오기 = new NetworkManager.CallbackFunc<ResponseBody>() {
            @Override
            public void onResponseSuccess(ResponseBody data) {

                Bitmap image = BitmapFactory.decodeStream(data.byteStream());
                프로필이미지.setImageBitmap(image);
                이미지 = convertBitmapToByteArray(image);
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
        프로필편집요청후콜백함수 = new NetworkManager.CallbackFunc<Data>() {
            @Override
            public void onResponseSuccess(Data data) {


                Log.i("프로필 편집요청후 콜백함수",data.toString());
                JsonObject response = data.result;

                String result = response.get("result").getAsString();

                if(result.equals(Data.SUCCESS)){

                    String name = response.get("name").getAsString();
                    String introText = response.get("introText").getAsString();
                    String profileUrl = response.get("profileUrl").getAsString();

                    sendResultToCallingActivity(name,introText,profileUrl);

                }
                else{
                    Toast("프로필 편집 실패.");
                    Toast(response.get("errorCode").getAsString());
                    Toast(response.get(Data.ERRDESCRIPTION).getAsString());

                }


            }

            @Override
            public void onResponseFail(ResponseBody ErrorBody) {
                Toast.makeText(EditProfileActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNetworkError(Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Network error", Toast.LENGTH_SHORT).show();

            }
        };






        networkManager = new NetworkManager(getApplicationContext());
        networkManager.setCallback(로그인이후내정보요청후콜백함수);

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("isMyPage",true);
        jsonObject.addProperty("userPk",0);

//        networkManager.GETRequest("User","getInfo");

        networkManager.GETRequest3("User","getInfo",jsonObject);

        나가기버튼.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });



        프로필이미지추가수정.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 갤러리에서 이미지 가져오기.
                pickImageFromGallery();

            }
        });

        기본이미지로수정버튼.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                이미지 = null;

                프로필이미지.setImageResource(R.drawable.baseline_person_24);

                프로필이미지추가수정.setText("프로필 이미지 추가");
                기본이미지로수정버튼.setEnabled(false);

            }
        });

        편집하기버튼.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                networkManager.setCallback(프로필편집요청후콜백함수);


                // body 생성
                String name = 닉네임에디터.getText().toString();
                String introText = 소개글에디터.getText().toString();
                String includeFile = Boolean.toString(false);


                MultipartBody.Part imageTypeBodyData = null;
                if(이미지 != null){
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), 이미지);
                    imageTypeBodyData = MultipartBody.Part.createFormData("imgFile", "imgFile", requestFile);
                    includeFile = Boolean.toString(true);
                }

                Log.i(""," 프로필 편집 요청.");

                // 프로필 편집 요청.
                networkManager.postRequest(imageTypeBodyData,createTextTypeBodyData(name),createTextTypeBodyData(introText),createTextTypeBodyData(includeFile));


            }
        });

    }

    public void sendResultToCallingActivity(String nickname, String introText, String imageUrl) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("profileImage", imageUrl);
        resultIntent.putExtra("name", nickname);
        resultIntent.putExtra("introText", introText);

        setResult(RESULT_OK, resultIntent);
        finish();  // Activity 종료하고 결과 반환
    }




    RequestBody createTextTypeBodyData(String text){

        return RequestBody.create(MediaType.parse("text/plain"), text);

    }



    private static final int PICK_IMAGE_REQUEST = 1;
    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();


            Bitmap bitmap = getBitmapFromUri(imageUri);
            프로필이미지.setImageBitmap(bitmap);


            이미지 = convertBitmapToByteArray(bitmap);
            프로필이미지추가수정.setText("프로필 이미지 변경");

            기본이미지로수정버튼.setEnabled(true);



        }
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        Bitmap bitmap = null;
        try {
            InputStream imageStream = getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(imageStream);
            imageStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    void Toast(String message){
        Toast.makeText(EditProfileActivity.this, message, Toast.LENGTH_SHORT).show();

    }



}