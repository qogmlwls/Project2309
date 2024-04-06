package com.example.project2309.ui;

import static com.esafirm.imagepicker.features.ImagePickerLauncherKt.createImagePickerIntent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project2309.MyApplication;
import com.example.project2309.adapter.PostAdapter;
import com.example.project2309.R;
import com.example.project2309.data.Data;
import com.example.project2309.data.Post;
import com.example.project2309.network.NetworkManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;

import com.esafirm.imagepicker.features.*;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {


    String TAG = "MainActivity";
    Button 내정보페이지로이동버튼;

    ImageButton 게시글추가버튼;


    private RecyclerView recyclerView;
    private List<Post> itemList;
    private PostAdapter adapter;

    List<Uri> imageUris = new ArrayList<>();
    NetworkManager networkManager;

    Button 채팅페이지로이동버튼;



    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            Log.i(TAG, "onReceive 실행");
            String data = intent.getStringExtra("message");
            Log.i(TAG, "받은 데이터 " + data);

            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(data);

                String type = jsonObject.getString("type");

                if (type.equals("ReceiveChatting")) {

                    totalCnt++;

                    TextView textView = findViewById(R.id.textView68);
                    if(totalCnt == 0){
                        textView.setVisibility(View.GONE);
                    }
                    else{
                        if(totalCnt<1000){
                            textView.setText(Integer.toString(totalCnt));
                        }
                        else{
                            textView.setText("+999");
                        }
                        textView.setVisibility(View.VISIBLE);
                    }

                }

            } catch (
            JSONException e) {
                throw new RuntimeException(e);
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter("1"));


        Intent i = new Intent();
        String packageName = getPackageName();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        if(pm.isIgnoringBatteryOptimizations(packageName)){
            i.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
        } else {
            i.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            i.setData(Uri.parse("package:" + packageName));
            startActivity(i);
        }

        Button 검색;
        검색 = findViewById(R.id.button14);

        검색.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
                finish();
            }
        });



        채팅페이지로이동버튼 = findViewById(R.id.button15);


        내정보페이지로이동버튼 = findViewById(R.id.button16);
        게시글추가버튼 = findViewById(R.id.imageButton4);
        recyclerView = findViewById(R.id.recycler_view);

        내정보페이지로이동버튼.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MyPageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        게시글추가버튼.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 갤러리에서 이미지 가져오기.
                ImagePickerConfig imagePickerConfig = launchImagePicker();

                Intent intent = createImagePickerIntent(MainActivity.this, imagePickerConfig);
                startActivityForResult(intent, IpCons.RC_IMAGE_PICKER);
//                Toast.makeText(MainActivity.this, "click", Toast.LENGTH_SHORT).show();
            }
        });

        채팅페이지로이동버튼.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RoomActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 데이터 준비
        itemList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostAdapter(itemList,this);
        recyclerView.setAdapter(adapter);

        networkManager = new NetworkManager(getApplicationContext());

        // 스크롤 리스너 설정
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
//                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
//                        && firstVisibleItemPosition >= 0) {
//                    // 다음 페이지 로드
//                    getPosts();
//                }
                if (!isLoading) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        // 다음 페이지 로드
//                        Toast.makeText(MainActivity.this, "getPosts", Toast.LENGTH_SHORT).show();
                        getPosts();
                    }
                }
            }
        });

        getPosts();

    }

    @Override
    protected void onStart() {
        super.onStart();

        getUnReadChat();

    }

    private void getPosts(){

        isLoading = true;
        networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
            @Override
            public void onResponseSuccess(Data data) {

                Log.i(TAG,data.result.toString());
                if(data.result.get("result").getAsString().equals(Data.SUCCESS)){


                    List<Post> postList = getPostList(data.result.getAsJsonArray("posts"));
                    for(int i=0;i<postList.size();i++){
                        itemList.add(postList.get(i));
                    }

                    adapter.notifyDataSetChanged();
                    isLoading = false;

                }
            }

            @Override
            public void onResponseFail(ResponseBody ErrorBody) {
                Toast.makeText(MainActivity.this, "onResponseFail", Toast.LENGTH_SHORT).show();
                Log.i("e",ErrorBody.toString());


            }

            @Override
            public void onNetworkError(Throwable t) {
                Toast.makeText(MainActivity.this, "onNetworkError", Toast.LENGTH_SHORT).show();
                Log.i("err",t.toString());
            }
        });


        if(itemList.size() == 0)
            networkManager.GETRequest("test","posts");
        else
            networkManager.GETRequest("test","posts",Integer.toString(itemList.get(itemList.size()-1).post_id));

    }

//    private int currentPage = 0;
//    private int pageSize = 10; // 페이지당 아이템 수
//    private int last_post_id = 0;
    private boolean isLoading = false;
//    private void loadNextPage() {
//
//
//        isLoading = true;
//
//        getPosts();
//
//        // Simulate loading data from a network or database
//        new Handler(Looper.getMainLooper()).postDelayed(() -> {
//            for (int i = 0; i < pageSize; i++) {
//                itemList.add(itemList.get(0));
//
//
//            }
//            adapter.notifyDataSetChanged();
//            isLoading = false;
//            currentPage++;
//            Log.d("Paging", "Page loaded: " + currentPage);
//        }, 1000); // Simulate delay for loading data
//
//
//    }
//


    private static final int REQUEST_CODE = 200;  // 어떤 상수든 사용 가능
    private static final int REQUEST_CODE2 = 201;  // 어떤 상수든 사용 가능

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CommentActivityCode ){
            adapter.click = true;
        }


        if (requestCode == IpCons.RC_IMAGE_PICKER && resultCode == RESULT_OK && data != null) {


            // Uri 배열 가져오기.
            List<Image> images = ImagePicker.INSTANCE.getImages(data);


            Intent intent = new Intent(MainActivity.this, CreatePostActivity.class);

            // 먼저 Uri 배열을 생성하고 데이터를 추가합니다.
            Uri[] uriArray = new Uri[images.size()]; // 예시로 Uri 배열의 크기를 2로 설정
            // Display selected images
            for (int i = 0; i < images.size(); i++) {
                uriArray[i] = images.get(i).getUri();

            }

// Convert Uri array to ArrayList (ParcelableArrayList)
            ArrayList<Uri> uriArrayList = new ArrayList<>(Arrays.asList(uriArray));

            intent.putExtra("uris", uriArrayList); // "uris"라는 키로 Uri 배열 추가


//            Intent intent = new Intent(MyPageActivity.this, EditProfileActivity.class);
            startActivityForResult(intent, REQUEST_CODE);

//            startActivity(intent);



        }
        else if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
//



            String result = data.getStringExtra("result");

            // 문자열 parsing

            JsonParser parser = new JsonParser();
            JsonObject keys = (JsonObject) parser.parse(result);

            int postId = keys.get("post_id").getAsInt();
            String userName = keys.get("name").getAsString();


            JSONObject jsonObject1 = new JSONObject();
            try {
                jsonObject1.put("type","RequestNewPostNoti");
                jsonObject1.put("postId",postId);
                jsonObject1.put("postUserName",userName);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            MyApplication application = (MyApplication)getApplication();
            application.SendMessage(jsonObject1);



            Log.i("result",keys.toString());

            Post post = getPost(keys);
            itemList.add(0,post);

            adapter.notifyDataSetChanged();



        }
        else if(requestCode == REQUEST_CODE2 && resultCode == RESULT_OK){


            int post_id = data.getIntExtra("post_id",-1);
            String update_date = data.getStringExtra("update_date");
            String comment = data.getStringExtra("comment");


//            Toast.makeText(this, update_date, Toast.LENGTH_SHORT).show();
            List<String> post_image = new ArrayList<>();

            ArrayList<String> receivedUris = data.getStringArrayListExtra("post_image");

//            Uri[] receivedUris = (Uri[]) intent.getParcelableArrayExtra("uris");

            if (receivedUris != null) {
                // Uri 배열에서 Uri를 추출하여 사용합니다.
                for (String uri : receivedUris) {
                    // TODO: Uri에 대한 작업 수행
                    post_image.add(uri);


                }
            }

//            Toast.makeText(this, post_image.get(2), Toast.LENGTH_SHORT).show();




            for(int i=0;i<itemList.size();i++){
                if(itemList.get(i).post_id == post_id){

                    Post post = itemList.get(i);

                    post.image = post_image;
                    post.comment = comment;
                    post.update_date = update_date;

//                    adapter.notify();
                    adapter.notifyDataSetChanged();
//                    Toast.makeText(this, "수정 완료.", Toast.LENGTH_SHORT).show();


                }
            }



//


        }
        else if(requestCode == CommentActivityCode && resultCode == RESULT_OK){


            int post_id = data.getIntExtra("post_id",-1);
            int count = data.getIntExtra("Comment_count",-1);


            for(int i =0;i<itemList.size();i++){
                Post post = itemList.get(i);

                if(post.post_id == post_id){

                    post.comment_cnt = count;
                }

                adapter.notifyDataSetChanged();

            }
//            Toast.makeText(this, Integer.toString(count), Toast.LENGTH_SHORT).show();



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

    private ImagePickerConfig launchImagePicker() {


//        registerImagePicker
        ImagePickerConfig imagePickerConfig = new ImagePickerConfig();

        imagePickerConfig.setLimit(5);

        return imagePickerConfig;

    }

//    @Override
//    public void onItemClick(int position) {
//
//        // 서버에게 요청
//        // post.isMyLike 가 true이면 좋아요 제거 요청
//        // post.isMyLike 가 false 이면 좋아요 요청
//        // 결과 성공시, 좋아요수와 아이콘 형태 변경.
//
//
//        networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
//            @Override
//            public void onResponseSuccess(Data data) {
//
//
//                if(data.result.get("result").getAsString().equals(Data.SUCCESS)){
//
//
//                    int post_id = data.result.get("post_id").getAsInt();
//                    int like_cnt = data.result.get("like_cnt").getAsInt();
//                    boolean isMyLike = data.result.get("isMyLike").getAsBoolean();
//
//
//                    for(int i=0;i<itemList.size();i++){
//                        Post post = itemList.get(i);
//                        if(post.post_id == post_id){
//                            post.isMyLike = isMyLike;
//                            post.like_cnt = like_cnt;
//
//
//                        }
//                    }
//
//
//                    adapter.notifyDataSetChanged();
//
//                }
//            }
//
//
//            @Override
//            public void onResponseFail(ResponseBody ErrorBody) {
//                Toast.makeText(MainActivity.this, "onResponseFail", Toast.LENGTH_SHORT).show();
//                Log.i("e",ErrorBody.toString());
//
//
//            }
//
//            @Override
//            public void onNetworkError(Throwable t) {
//                Toast.makeText(MainActivity.this, "onNetworkError", Toast.LENGTH_SHORT).show();
//                Log.i("err",t.toString());
//            }
//
//        });
//        JsonObject body = new JsonObject();
//        body.addProperty("post_id",itemList.get(position).post_id);
//        networkManager.postRequest("test","Like",body);
//
//
//
//
//
//
//    }


//    @Override
//    public void Edit(Post item) {
//
//        Intent intent = new Intent(MainActivity.this, UpdatePostActivity.class);
////
////        intent.putExtra("uris", uriArrayList); // "uris"라는 키로 Uri 배열 추가
////
//        intent.putExtra("post_id",item.post_id);
//        intent.putExtra("comment",item.comment);
//
//
//        // Convert Uri array to ArrayList (ParcelableArrayList)
//        ArrayList<String> ArrayList = new ArrayList<>(item.image);
//
//        intent.putExtra("images", ArrayList); // "uris"라는 키로 Uri 배열 추가
//
////            Intent intent = new Intent(MyPageActivity.this, EditProfileActivity.class);
//        startActivityForResult(intent, REQUEST_CODE2);
//
//    }

//    @Override
//    public void delete(int post_id) {
//
//        networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
//            @Override
//            public void onResponseSuccess(Data data) {
//
//                if(data.result.get("result").getAsString().equals(Data.SUCCESS)){
//
//
//                    int post_id = data.result.get("post_id").getAsInt();
//
//                    for(int i=0;i<itemList.size();i++){
//                        if(itemList.get(i).post_id == post_id){
//                            itemList.remove(itemList.get(i));
//                            adapter.notifyItemRemoved(i);
//
//                        }
//                    }
//
//
////                     삭제.
////                    itemList.remove();
////                    Toast.makeText(MainActivity.this, "삭제 성공", Toast.LENGTH_SHORT).show();
//
//                }
//            }
//
//            @Override
//            public void onResponseFail(ResponseBody ErrorBody) {
//                Toast.makeText(MainActivity.this, "onResponseFail", Toast.LENGTH_SHORT).show();
//                Log.i("e",ErrorBody.toString());
//
//
//            }
//
//            @Override
//            public void onNetworkError(Throwable t) {
//                Toast.makeText(MainActivity.this, "onNetworkError", Toast.LENGTH_SHORT).show();
//                Log.i("err",t.toString());
//            }
//        });
//
//        networkManager.deleteRequest("posts","deletePost",Integer.toString(post_id));
//
//
//    }

    int CommentActivityCode =  112;

//    public boolean click = true;

    @Override
    protected void onPause() {
        super.onPause();

    }
//
//    @Override
//    public void CommentButtonClick(Post post) {
//
//
//        if(click){
//            click=false;
//            Intent intent = new Intent(MainActivity.this,CommentActivity.class);
//            intent.putExtra("post_id",post.post_id);
//            intent.putExtra("comment_cnt",post.comment_cnt);
//            startActivityForResult(intent,CommentActivityCode);
//        }
//
//    }

    Post getPost(JsonObject object){
        Post post = new Post();
//        JsonObject object = array.get(i).getAsJsonObject();

        Log.i("Post JsonObject",object.toString());

        post.post_id = object.get("post_id").getAsInt();
        post.user_id = object.get("user_id").getAsInt();
        post.user_name = object.get("name").getAsString();
        post.comment = object.get("comment").getAsString();
        post.create_date = object.get("create_date").getAsString();
        post.isMyPost = object.get("isMyPost").getAsBoolean();
        post.isMyLike = object.get("isMyLike").getAsBoolean();

        post.like_cnt = object.get("like_cnt").getAsInt();



        Log.i(" object.has(\"comment_cnt\")",Boolean.toString( object.has("comment_cnt")));


        post.comment_cnt = object.get("comment_cnt").getAsInt();
        post.user_profile = object.get("profile").getAsString();
        post.image = new ArrayList<>();


        JsonArray array1 = object.getAsJsonArray("post_image");

        Log.i("image result",object.getAsJsonArray("post_image").toString());


        for(int j=0;j<array1.size();j++){
            post.image.add(array1.get(j).getAsString());
            Log.i("image",array1.get(j).getAsString());
        }
        return post;
    }

    List<Post> getPostList(JsonArray array){

        List<Post> data = new ArrayList<>();

        for(int i=0;i<array.size();i++){

            Post post = new Post();
            JsonObject object = array.get(i).getAsJsonObject();

            Log.i("Post JsonObject",object.toString());

            post.post_id = object.get("post_id").getAsInt();
            post.user_id = object.get("user_id").getAsInt();
            post.user_name = object.get("name").getAsString();
            post.comment = object.get("comment").getAsString();
            post.create_date = object.get("create_date").getAsString();

            if(object.has("update_date")){
                post.update_date = object.get("update_date").getAsString();

            }


            post.isMyPost = object.get("isMyPost").getAsBoolean();
            post.isMyLike = object.get("isMyLike").getAsBoolean();
            post.comment_cnt = object.get("comment_cnt").getAsInt();
            post.like_cnt = object.get("like_cnt").getAsInt();
            post.user_profile = object.get("profile").getAsString();
            post.image = new ArrayList<>();


            JsonArray array1 = object.getAsJsonArray("post_image");

            Log.i("image result",object.getAsJsonArray("post_image").toString());


            for(int j=0;j<array1.size();j++){
                post.image.add(array1.get(j).getAsString());
                Log.i("image",array1.get(j).getAsString());
            }


            data.add(post);
        }


        return data;

    }
    int totalCnt = 0;

    public void getUnReadChat(){

        NetworkManager networkManager1 = new NetworkManager(getApplicationContext());
        networkManager1.setCallback(new NetworkManager.CallbackFunc<Data>() {
            @Override
            public void onResponseSuccess(Data data) {
                if (data.result.get("result").getAsString().equals(Data.SUCCESS)) {

                    Log.i(TAG, data.result.toString());

                    totalCnt = data.result.get("total").getAsInt();
                    Log.i(TAG, Integer.toString(totalCnt));


                    TextView textView = findViewById(R.id.textView68);
                    if(totalCnt == 0){
                        textView.setVisibility(View.GONE);
                    }
                    else{
                        if(totalCnt<1000){
                            textView.setText(Integer.toString(totalCnt));
                        }
                        else{
                            textView.setText("+999");
                        }
                        textView.setVisibility(View.VISIBLE);
                    }


                }
            }
            @Override
            public void onResponseFail(ResponseBody ErrorBody) {

            }

            @Override
            public void onNetworkError(Throwable t) {

            }
        });
        networkManager1.GET2Request("chat","totalUnread","");


    }


}