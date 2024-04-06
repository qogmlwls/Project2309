package com.example.project2309.ui;

import static com.esafirm.imagepicker.features.ImagePickerLauncherKt.createImagePickerIntent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ImagePickerConfig;
import com.esafirm.imagepicker.features.IpCons;
import com.esafirm.imagepicker.model.Image;
import com.example.project2309.CustomItemTouchHelperCallback;
import com.example.project2309.adapter.CR_PostAdapter;
import com.example.project2309.R;
import com.example.project2309.common.Message;
import com.example.project2309.data.Data;
import com.example.project2309.network.NetworkManager;
import com.google.gson.JsonArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class UpdatePostActivity extends AppCompatActivity {


    String TAG = "UpdatePostActivity";
    ImageButton imageButton;
    RecyclerView recyclerView;
    EditText editText;
    Button button;
    List<String> images = new ArrayList<>();
    List<Bitmap> bitmaplist = new ArrayList<>();
    CR_PostAdapter adapter;

    NetworkManager networkManager;

    int post_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_post);

        post_id = getIntent().getIntExtra("post_id",-1);
        String comment = getIntent().getStringExtra("comment");

        networkManager = new NetworkManager(getApplicationContext());

        ArrayList<String> receivedUris = getIntent().getStringArrayListExtra("images");

//            Uri[] receivedUris = (Uri[]) intent.getParcelableArrayExtra("uris");

        if (receivedUris != null) {
            // Uri 배열에서 Uri를 추출하여 사용합니다.
            for (String uri : receivedUris) {
                // TODO: Uri에 대한 작업 수행
                images.add(uri);


            }
        }

        if(images.size() == 0){
            Toast.makeText(this, "이미지 정보 받아오지 못했습니다.", Toast.LENGTH_SHORT).show();
        }
        else{
            getImage(0);
        }


        recyclerView = findViewById(R.id.recycler_view3);

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));

        CustomItemTouchHelperCallback callback = new CustomItemTouchHelperCallback(new CustomItemTouchHelperCallback.ItemTouchHelperAdapter() {
            @Override
            public void onItemMove(int fromPosition, int toPosition) {
                // 아이템 이동이 발생했을 때의 동작을 정의합니다.
                // 여기서는 fromPosition과 toPosition을 사용하여 데이터 순서를 변경할 수 있습니다.
                Log.i(TAG,"onItemMove");
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
//                        Collections.swap(images,i,i+1);
                        Collections.swap(bitmaplist,i,i+1);

                    }
                }else{
                    for (int i = fromPosition; i > toPosition; i--) {
//                        Collections.swap(images,i,i-1);
                        Collections.swap(bitmaplist,i,i-1);

                    }
                }
                adapter.notifyItemMoved(fromPosition, toPosition);

            }
        });

        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);



        adapter = new CR_PostAdapter(bitmaplist);
        recyclerView.setAdapter(adapter);


        editText = findViewById(R.id.editTextText8);
        editText.setText(comment);



        imageButton = findViewById(R.id.imageButton11);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        button = findViewById(R.id.button36);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // 서버에게 수정 요청
                if(bitmaplist.size() == 0){
                    Toast.makeText(UpdatePostActivity.this, "이미지를 하나 이상 넣어주세요.", Toast.LENGTH_SHORT).show();
                    return;
                    //                    finish();
                }

                // body 생성
                List<MultipartBody.Part> imageParts = new ArrayList<>();

                for(int i=0;i<bitmaplist.size();i++){

//                    ImageView  ci;
////                    ci.get
//                    Bitmap bitmap = list.get(i);
                    byte[] 이미지 = convertBitmapToByteArray(bitmaplist.get(i));

                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), 이미지);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("post_image"+Integer.toString(i),"image"+Integer.toString(i), requestFile);

                    imageParts.add(body);

                }



                RequestBody postId = RequestBody.create(MediaType.parse("text/plain"), Integer.toString(post_id));

                RequestBody comment = RequestBody.create(MediaType.parse("text/plain"), editText.getText().toString());
                RequestBody file_cnt = RequestBody.create(MediaType.parse("text/plain"),Integer.toString(bitmaplist.size()) );

                networkManager = new NetworkManager(getApplicationContext());
                networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {


                    @Override
                    public void onResponseSuccess(Data data) {
                        if(data.result.get("result").getAsString().equals(Data.SUCCESS)){


                            // 리사이클러뷰 상단에 내가 작성한 글 보여지도록.
                            Intent resultIntent = new Intent();


//                            JsonObject jsonObject = new JsonObject();


//                            jsonObject.addProperty("post_id",data.result.get("post_id").getAsInt());
//                            jsonObject.addProperty("comment",data.result.get("comment").getAsString());
//                            jsonObject.addProperty("update_date",data.result.get("update_date").getAsString());




                            JsonArray array = data.result.get("post_image").getAsJsonArray();

                            List<String> imagelist = new ArrayList<>();

                            for(int i=0;i<array.size();i++){
                                imagelist.add(array.get(i).getAsString());
//                                Toast.makeText(UpdatePostActivity.this, array.get(i).getAsString(), Toast.LENGTH_SHORT).show();

                            }


                            ArrayList<String> uriArrayList = new ArrayList<>(imagelist);

//                            Toast.makeText(UpdatePostActivity.this, Integer.toString(uriArrayList.size()), Toast.LENGTH_SHORT).show();

                            resultIntent.putStringArrayListExtra("post_image",uriArrayList);
                            resultIntent.putExtra("post_id",data.result.get("post_id").getAsInt());
                            resultIntent.putExtra("comment",data.result.get("comment").getAsString());
                            resultIntent.putExtra("update_date",data.result.get("update_date").getAsString());


//                            JsonObject jsonObject = data.result.get("post").getAsJsonObject();
//                            resultIntent.putExtra("result",jsonObject.toString());



                            setResult(RESULT_OK, resultIntent);
                            finish();  // Activity 종료하고 결과 반환

                        }
                    }

                    @Override
                    public void onResponseFail(ResponseBody ErrorBody) {
                        Toast.makeText(UpdatePostActivity.this, "onResponseFail", Toast.LENGTH_SHORT).show();
                        Log.i("e",ErrorBody.toString());


                    }

                    @Override
                    public void onNetworkError(Throwable t) {
                        Toast.makeText(UpdatePostActivity.this, "onNetworkError", Toast.LENGTH_SHORT).show();
                        Log.i("err",t.toString());
                    }
                });

                networkManager.postRequest("posts", "updatePost",imageParts,postId, comment,file_cnt);


            }
        });






        Button 이미지변경;
        이미지변경 = findViewById(R.id.button37);

        이미지변경.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 이미지 변경하기.
                // 갤러리에서 이미지 가져오기.
                ImagePickerConfig imagePickerConfig = launchImagePicker();
                if(imagePickerConfig == null){
                    return;
                }

                Intent intent = createImagePickerIntent(UpdatePostActivity.this, imagePickerConfig);
                startActivityForResult(intent, IpCons.RC_IMAGE_PICKER);
//                Toast.makeText(MainActivity.this, "click", Toast.LENGTH_SHORT).show();

            }
        });


    }

    void getImage(int index){


        String path = images.get(index);
//        String url= "http://49.247.30.164/"+path;

//        String url= "http://49.247.30.164/Image/file.png";

//        Toast.makeText(UpdatePostActivity.this, "시작."+Integer.toString(images.size()), Toast.LENGTH_SHORT).show();
        NetworkManager.CallbackFunc<ResponseBody> 이미지받아오기;

        이미지받아오기 = new NetworkManager.CallbackFunc<ResponseBody>() {
            @Override
            public void onResponseSuccess(ResponseBody data) {

                Bitmap image = BitmapFactory.decodeStream(data.byteStream());
//                프로필이미지.setImageBitmap(image);
                if(index + 1 >= images.size()){
//                    Toast.makeText(UpdatePostActivity.this, "끝", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();

                }
                else{
//                    Toast.makeText(UpdatePostActivity.this, "more.", Toast.LENGTH_SHORT).show();
                    getImage(index + 1);
                }
                bitmaplist.add(image);

            }

            @Override
            public void onResponseFail(ResponseBody ErrorBody) {
                Toast.makeText(UpdatePostActivity.this, Message.RetrofitResponseSuccAndResponseFail, Toast.LENGTH_SHORT).show();
//                Toast(Message.RetrofitResponseSuccAndResponseFail);
//
            }

            @Override
            public void onNetworkError(Throwable t) {
                Toast.makeText(UpdatePostActivity.this, Message.RetrofitResponseFail, Toast.LENGTH_SHORT).show();

//                Toast(Message.RetrofitResponseFail);

            }
        };



        networkManager.setCallback(이미지받아오기);
        networkManager.GETImage(path);




//        Set 'android:enableOnBackInvokedCallback="true"' in the application manifest.
//
//        Glide.with(this).asBitmap().load(url).listener(new RequestListener<Bitmap>() {
//            @Override
//            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
//
//                Toast.makeText(UpdatePostActivity.this, "fail", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//            @Override
//            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
//
//                if(index + 1 < images.size()){
//                    Toast.makeText(UpdatePostActivity.this, "끝", Toast.LENGTH_SHORT).show();
//                    adapter.notifyDataSetChanged();
//
//                }
//                else{
//                    Toast.makeText(UpdatePostActivity.this, "more.", Toast.LENGTH_SHORT).show();
//                    getImage(index + 1);
//                }
//                bitmaplist.add(resource);
//
//                return false;
//            }
//        }).submit();





    }


    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }



    private ImagePickerConfig launchImagePicker() {



        int size = 5;
        if(bitmaplist.size() == size){
            Toast.makeText(this, "이미지는 최대 "+Integer.toString(size)+"개 선택할 수 있습니다.", Toast.LENGTH_SHORT).show();
            return null;
        }
//        registerImagePicker
        ImagePickerConfig imagePickerConfig = new ImagePickerConfig();

        imagePickerConfig.setLimit(size-bitmaplist.size());
        return imagePickerConfig;

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IpCons.RC_IMAGE_PICKER && resultCode == RESULT_OK && data != null) {


            // Uri 배열 가져오기.
            List<Image> images = ImagePicker.INSTANCE.getImages(data);

            // 먼저 Uri 배열을 생성하고 데이터를 추가합니다.
            Uri[] uriArray = new Uri[images.size()]; // 예시로 Uri 배열의 크기를 2로 설정
            // Display selected images
            for (int i = 0; i < images.size(); i++) {
                uriArray[i] = images.get(i).getUri();
                Bitmap bitmap = getBitmapFromUri(uriArray[i]);
                bitmaplist.add(bitmap);

//                this.images.add()
            }

            adapter.notifyDataSetChanged();

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

}