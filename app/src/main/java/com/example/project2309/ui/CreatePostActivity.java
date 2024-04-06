package com.example.project2309.ui;

import static com.esafirm.imagepicker.features.ImagePickerLauncherKt.createImagePickerIntent;

import androidx.annotation.NonNull;
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
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ImagePickerConfig;
import com.esafirm.imagepicker.features.IpCons;
import com.esafirm.imagepicker.model.Image;
import com.example.project2309.CustomItemTouchHelperCallback;
import com.example.project2309.MyApplication;
import com.example.project2309.adapter.CR_PostAdapter;
import com.example.project2309.R;
import com.example.project2309.adapter.TagAdapter;
import com.example.project2309.data.Chat;
import com.example.project2309.data.Data;
import com.example.project2309.data.TagPost;
import com.example.project2309.network.NetworkManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

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

public class CreatePostActivity extends AppCompatActivity {

    RecyclerView 해시태그리사이클러뷰;
    List<TagPost> itemList;

    TagAdapter tagadapter;
    boolean isLoading = false;
    ProgressBar 검색로딩바,하단로딩바;


    Button 이미지변경, 게시글공유;

    ImageButton 닫기;

    String TAG = "CreatePostActivity";

    RecyclerView 이미지리사이클러뷰;


    List<Bitmap> list;

    EditText 문구;

    CR_PostAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        닫기 = findViewById(R.id.imageButton5);
        게시글공유 = findViewById(R.id.button31);

        이미지리사이클러뷰 = findViewById(R.id.recyclerView2);
        해시태그리사이클러뷰 = findViewById(R.id.recyclerView7);
        Log.i(TAG,이미지리사이클러뷰.toString());

        list = new ArrayList<>();
        문구 = findViewById(R.id.editTextTextMultiLine);

        검색로딩바 = findViewById(R.id.progressBar);
        하단로딩바 = findViewById(R.id.progressBar2);

        itemList = new ArrayList<>();
        tagadapter = new TagAdapter(itemList,this, 문구);

        Log.i(TAG,해시태그리사이클러뷰.toString());


        이미지변경 = findViewById(R.id.button32);

        해시태그리사이클러뷰.setLayoutManager(new LinearLayoutManager(this));
        해시태그리사이클러뷰.setAdapter(tagadapter);

        Intent intent = getIntent();
        if (intent != null) {
            // Uri 배열을 받아옵니다.

            ArrayList<Uri> receivedUris = intent.getParcelableArrayListExtra("uris");

//            Uri[] receivedUris = (Uri[]) intent.getParcelableArrayExtra("uris");

            if (receivedUris != null) {
                // Uri 배열에서 Uri를 추출하여 사용합니다.
                for (Uri uri : receivedUris) {
                    // TODO: Uri에 대한 작업 수행
                    Bitmap bitmap = getBitmapFromUri(uri);
                    list.add(bitmap);

                }
            }
        }


        adapter = new CR_PostAdapter(list);


        이미지리사이클러뷰.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));//,LinearLayoutManager.HORIZONTAL, false
        이미지리사이클러뷰.setAdapter(adapter);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(이미지리사이클러뷰);
        CustomItemTouchHelperCallback callback = new CustomItemTouchHelperCallback(new CustomItemTouchHelperCallback.ItemTouchHelperAdapter() {
            @Override
            public void onItemMove(int fromPosition, int toPosition) {
                // 아이템 이동이 발생했을 때의 동작을 정의합니다.
                // 여기서는 fromPosition과 toPosition을 사용하여 데이터 순서를 변경할 수 있습니다.
                Log.i(TAG,"onItemMove");
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(list,i,i+1);
                    }
                }else{
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(list,i,i-1);
                    }
                }
                adapter.notifyItemMoved(fromPosition, toPosition);

            }
        });

        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(이미지리사이클러뷰);


        닫기.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        게시글공유.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(list.size() == 0){
                    Toast.makeText(CreatePostActivity.this, "이미지를 하나 이상 넣어주세요.", Toast.LENGTH_SHORT).show();
                    return;
                    //                    finish();
                }

                // body 생성
                List<MultipartBody.Part> imageParts = new ArrayList<>();

                for(int i=0;i<list.size();i++){

                    Bitmap bitmap = list.get(i);
                    byte[] 이미지 = convertBitmapToByteArray(bitmap);

                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), 이미지);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("post_image"+Integer.toString(i),"image"+Integer.toString(i), requestFile);

                    imageParts.add(body);

                }
                RequestBody comment = RequestBody.create(MediaType.parse("text/plain"), 문구.getText().toString());
                RequestBody file_cnt = RequestBody.create(MediaType.parse("text/plain"),Integer.toString(list.size()) );



                NetworkManager networkManager = new NetworkManager(getApplicationContext());
                networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {


                    @Override
                        public void onResponseSuccess(Data data) {
                            if(data.result.get("result").getAsString().equals(Data.SUCCESS)){


                                // 리사이클러뷰 상단에 내가 작성한 글 보여지도록.
                                Intent resultIntent = new Intent();

                                JsonObject jsonObject = data.result.get("post").getAsJsonObject();

//                                jsonObject.toString()

//                                JsonParser parser = new JsonParser();
//                                parser.
                                resultIntent.putExtra("result",jsonObject.toString());


//                                resultIntent.putExtra("post_id", data.result.get("").getAsInt());
//                                resultIntent.putExtra("user_id",data.result.get("").getAsInt());
//                                resultIntent.putExtra("name", data.result.get("").getAsString());
//                                resultIntent.putExtra("profile", data.result.get("").getAsString());
//
//
//                                JsonArray array = data.result.getAsJsonArray("post_image");
//
//                                ArrayList<String> image = new ArrayList<>();
//
//                                for(int i=0;i<array.size();i++){
//                                    image.add(image.get(i));
//
//                                }
//
//                                // JsonArray.
//                                resultIntent.putExtra("post_image", image);
//                                resultIntent.putExtra("comment", data.result.get("").getAsString());
//                                resultIntent.putExtra("like_cnt", data.result.get("").getAsInt());
//                                resultIntent.putExtra("isMyPost", data.result.get("").getAsBoolean());
//                                resultIntent.putExtra("create_date", data.result.get("").getAsString());
////                                resultIntent.putExtra("update_date", nickname);
//
//                                resultIntent.putExtra("isMyLike", data.result.get("").getAsBoolean());


                                setResult(RESULT_OK, resultIntent);
                                finish();  // Activity 종료하고 결과 반환

                            }
                    }

                    @Override
                    public void onResponseFail(ResponseBody ErrorBody) {
                        Toast.makeText(CreatePostActivity.this, "onResponseFail", Toast.LENGTH_SHORT).show();
                        Log.i("e",ErrorBody.toString());


                    }

                    @Override
                    public void onNetworkError(Throwable t) {
                        Toast.makeText(CreatePostActivity.this, "onNetworkError", Toast.LENGTH_SHORT).show();
                        Log.i("err",t.toString());
                    }
                });

                networkManager.postRequest(imageParts,comment,file_cnt);

            }
        });


        이미지변경.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 이미지 변경하기.
                // 갤러리에서 이미지 가져오기.
                ImagePickerConfig imagePickerConfig = launchImagePicker();

                if(imagePickerConfig == null){
                    return;
                }

                Intent intent = createImagePickerIntent(CreatePostActivity.this, imagePickerConfig);
                startActivityForResult(intent, IpCons.RC_IMAGE_PICKER);
//                Toast.makeText(MainActivity.this, "click", Toast.LENGTH_SHORT).show();

            }
        });


        // 스크롤 리스너 설정
        해시태그리사이클러뷰.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading) {

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {


                        // 다음 페이지 로드
                        String text = 문구.getText().toString();
                        String[] words = text.split("\\s+"); // 공백을 기준으로 문자열을 분리하여 배열로 저장

                        // 맘지막 글자 # 포함.
                        if (words[words.length - 1].contains("#")) {
                            // 해시태그 입력중.
                            // 방금 입력된 문자가 #이라면
                            if (words[words.length - 1].endsWith("#")) {
                                recyclerView.setVisibility(View.VISIBLE);
                            }

                            int index = words[words.length - 1].lastIndexOf("#");

                            if (index == -1) {
                                // #가 없음.

                            }

                            String keyword = words[words.length - 1].substring(index + 1);
                            getMoreData(keyword,0);
                            하단로딩바.setVisibility(View.VISIBLE);


                        }


                    }
                }
            }
        });

        문구.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // 입력 전
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // 입력 중
                String[] words = charSequence.toString().split("\\s+"); // 공백을 기준으로 문자열을 분리하여 배열로 저장

                if(words.length == 0 || charSequence.toString().endsWith(" ")){
                    itemList.clear();
                    tagadapter.notifyDataSetChanged();
                    해시태그리사이클러뷰.setVisibility(View.GONE);
                    검색로딩바.setVisibility(View.GONE);
                    하단로딩바.setVisibility(View.GONE);
                    return;
                }

                String text = words[words.length-1];
//                String[] words = text.split("\\s+"); // 공백을 기준으로 문자열을 분리하여 배열로 저장
                int index = text.lastIndexOf("#");

                if(index == -1){

                    itemList.clear();
                    tagadapter.notifyDataSetChanged();
                    해시태그리사이클러뷰.setVisibility(View.GONE);
                    return;
                }

                if(index == text.length()-1){

                    itemList.clear();
                    tagadapter.notifyDataSetChanged();
                    검색로딩바.setVisibility(View.GONE);

                    // 하단 로딩바 GONE 하는 이유
                    하단로딩바.setVisibility(View.GONE);
                    
                    return;
                }

                String keyword = text.substring(index+1);
                itemList.clear();
                tagadapter.notifyDataSetChanged();

                getData(keyword);
                검색로딩바.setVisibility(View.VISIBLE);
                하단로딩바.setVisibility(View.GONE);
                해시태그리사이클러뷰.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 입력 후
            }
        });
    }

    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }



    private ImagePickerConfig launchImagePicker() {

        

        int size = 5;
        if(list.size() == size){
            Toast.makeText(this, "이미지는 최대 "+Integer.toString(size)+"개 선택할 수 있습니다.", Toast.LENGTH_SHORT).show();
            return null;
        }
//        registerImagePicker
        ImagePickerConfig imagePickerConfig = new ImagePickerConfig();

        imagePickerConfig.setLimit(size-list.size());

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
                list.add(bitmap);

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
//    public void getData(String keyword){
//
//        isLoading = true;
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                int index = 문구.getText().toString().lastIndexOf("#");
//
//                if(index == -1){
//                    // #가 없음.
//
//                }
//
//                String tagName = 문구.getText().toString().substring(index+1);
//                Log.i(TAG,tagName);
//                Log.i(TAG,keyword);
//
//                //                for(int i=0;i<10;i++){
////                    TagPost tagPost = new TagPost();
////                    tagPost.setTagName("태그이름");
////                    tagPost.setCount(1000);
////                    itemList.add(tagPost);
////                }
////                검색로딩바.setVisibility(View.GONE);
////                adapter.notifyDataSetChanged();
////                isLoading = false;
//
//                // 10초 후에 실행될 작업을 여기에 추가
//                if(keyword.equals(tagName)){
//
//                    for(int i=0;i<10;i++){
//                        TagPost tagPost = new TagPost();
//                        tagPost.setTagName("태그이름");
//                        tagPost.setCount(1000);
//                        itemList.add(tagPost);
//                    }
//                    검색로딩바.setVisibility(View.GONE);
//                    tagadapter.notifyDataSetChanged();
//                    isLoading = false;
//
//                }
//
//            }
//        }, 1000); // 10초를 밀리초 단위로 표현한 값 (1초 = 1000밀리초)
//
//    }


    // 아래 추가.
//    public void getMoreData(String keyword, int lastId){
//
//
//        isLoading = true;
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                int index = 문구.getText().toString().lastIndexOf("#");
//
//                if(index == -1){
//                    // #가 없음.
//                }
//
//                String tagName = 문구.getText().toString().substring(index+1);
//                Log.i(TAG,tagName);
//                Log.i(TAG,keyword);
//
//                // 10초 후에 실행될 작업을 여기에 추가
//                // 10초 후에 실행될 작업을 여기에 추가
//                if(keyword.equals(tagName)){
//
//                    하단로딩바.setVisibility(View.GONE);
//                    for(int i=0;i<10;i++){
//                        TagPost tagPost = new TagPost();
//                        tagPost.setTagName("태그이름추가임");
//                        tagPost.setCount(1000);
//                        itemList.add(tagPost);
//                    }
//
//                    tagadapter.notifyDataSetChanged();
//                    isLoading = false;
//
//                }
//
//            }
//        }, 1000); // 10초를 밀리초 단위로 표현한 값 (1초 = 1000밀리초)
//
//    }

    public void getData(String keyword){

        Log.i(TAG, "--------------------------------------------------");
        Log.i(TAG, "채팅 가져오기.");

        isLoading = true;

        NetworkManager networkManager = new NetworkManager(getApplicationContext());
        networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
            @Override
            public void onResponseSuccess(Data data) {

                Log.i(TAG, "--------------------------------------------------");
                Log.i(TAG, "채팅 가져오기 끝 : "+data.result.toString());
                if (data.result.get("result").getAsString().equals(Data.SUCCESS)) {

                    int index = 문구.getText().toString().lastIndexOf("#");

                    if(index == -1){
                        // #가 없음.
                        Log.i(TAG,"#가 없음.");

                    }

                    String tagName = 문구.getText().toString().substring(index+1);
                    Log.i(TAG,tagName);
                    Log.i(TAG,keyword);

                    // 10초 후에 실행될 작업을 여기에 추가
                    if(keyword.equals(tagName)){

                        JsonArray array = data.result.get("hashTags").getAsJsonArray();

                        for(int i=0;i<array.size();i++){

                            JsonObject jsonObject = array.get(i).getAsJsonObject();

                            TagPost tagPost = new TagPost();
                            tagPost.setTagName(jsonObject.get("hashtag_name").getAsString());
                            tagPost.setCount(jsonObject.get("total_amount").getAsInt());
                            tagPost.tagPostId = jsonObject.get("hashtag_id").getAsInt();

                            itemList.add(tagPost);

                        }
                        검색로딩바.setVisibility(View.GONE);
                        tagadapter.notifyDataSetChanged();
                        isLoading = false;

                    }
                }
                else if(data.result.get("result").getAsString().equals(Data.Fail)){

                    String reasonCode = data.result.get("reasonCode").getAsString();
                    if(reasonCode.equals("505")){
                        Toast.makeText(CreatePostActivity.this, "세션 만료. 다시 로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CreatePostActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
//                        Toast(reasonCode + " : "+reasonDescription);
                    }

                }
                Log.i(TAG, "--------------------------------------------------");

            }

            @Override
            public void onResponseFail(ResponseBody ErrorBody) {
                Toast.makeText(CreatePostActivity.this, "onResponseFail", Toast.LENGTH_SHORT).show();
                Log.i("e", ErrorBody.toString());
            }

            @Override
            public void onNetworkError(Throwable t) {
                Toast.makeText(CreatePostActivity.this, "onNetworkError", Toast.LENGTH_SHORT).show();
                Log.i("err", t.toString());
            }

        });

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("keyword",keyword);

        Log.i(TAG, "서버에게 전송할 Body : ");
        Log.i(TAG, jsonObject.toString());

        networkManager.GET2Request("posts", "getHashTag", jsonObject.toString());
        Log.i(TAG, "서버에게 채팅 데이터 요청하기.");
        Log.i(TAG, "--------------------------------------------------");

    }

    public void getMoreData(String keyword, int lastId){

        Log.i(TAG, "--------------------------------------------------");
        Log.i(TAG, "채팅 가져오기.");

        isLoading = true;
        NetworkManager networkManager = new NetworkManager(getApplicationContext());
        networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
            @Override
            public void onResponseSuccess(Data data) {

                Log.i(TAG, "--------------------------------------------------");
                Log.i(TAG, "채팅 가져오기 끝 : "+data.result.toString());
                if (data.result.get("result").getAsString().equals(Data.SUCCESS)) {

                    int index = 문구.getText().toString().lastIndexOf("#");

                    if(index == -1){
                        // #가 없음.
                        Log.i(TAG,"#가 없음.");

                    }

                    String tagName = 문구.getText().toString().substring(index+1);
                    Log.i(TAG,tagName);
                    Log.i(TAG,keyword);

                    // 10초 후에 실행될 작업을 여기에 추가
                    if(keyword.equals(tagName)){

                        하단로딩바.setVisibility(View.GONE);

                        JsonArray array = data.result.get("hashTags").getAsJsonArray();


                        for(int i=0;i<array.size();i++){

                            JsonObject jsonObject = array.get(i).getAsJsonObject();

                            TagPost tagPost = new TagPost();
                            tagPost.setTagName(jsonObject.get("hashtag_name").getAsString());
                            tagPost.setCount(jsonObject.get("total_amount").getAsInt());
                            tagPost.tagPostId = jsonObject.get("hashtag_id").getAsInt();

                            itemList.add(tagPost);

                        }

                        tagadapter.notifyDataSetChanged();
                        isLoading = false;

                    }
                }
                else if(data.result.get("result").getAsString().equals(Data.Fail)){

                    String reasonCode = data.result.get("reasonCode").getAsString();
                    if(reasonCode.equals("505")){
                        Toast.makeText(CreatePostActivity.this, "세션 만료. 다시 로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CreatePostActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
//                        Toast(reasonCode + " : "+reasonDescription);
                    }

                }
                Log.i(TAG, "--------------------------------------------------");

            }

            @Override
            public void onResponseFail(ResponseBody ErrorBody) {
                Toast.makeText(CreatePostActivity.this, "onResponseFail", Toast.LENGTH_SHORT).show();
                Log.i("e", ErrorBody.toString());
            }

            @Override
            public void onNetworkError(Throwable t) {
                Toast.makeText(CreatePostActivity.this, "onNetworkError", Toast.LENGTH_SHORT).show();
                Log.i("err", t.toString());
            }

        });

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("keyword",keyword);

        TagPost tagPost = itemList.get(itemList.size()-1);


        jsonObject.addProperty("last_id",tagPost.tagPostId);
        jsonObject.addProperty("last_total",tagPost.getCount());
//        jsonObject.addProperty("last_post_id",);


        Log.i(TAG, "서버에게 전송할 Body : ");
        Log.i(TAG, jsonObject.toString());

        networkManager.GET2Request("posts", "getHashTag", jsonObject.toString());
        Log.i(TAG, "서버에게 채팅 데이터 요청하기.");
        Log.i(TAG, "--------------------------------------------------");

    }

}