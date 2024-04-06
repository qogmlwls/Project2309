package com.example.project2309.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.project2309.R;
import com.example.project2309.adapter.PostAdapter;
import com.example.project2309.data.Data;
import com.example.project2309.data.Post;
import com.example.project2309.network.NetworkManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import com.google.gson.JsonParser;


public class FeedActivity extends AppCompatActivity {


    RecyclerView recyclerView;

    ImageButton 닫기;

    PostAdapter adapter;
    private List<Post> itemList;

    private boolean isLoading = false;

    NetworkManager networkManager;

    int userId;

    String UP="up",DOWN="down";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        recyclerView = findViewById(R.id.recycler_view5);
        닫기 = findViewById(R.id.imageButton23);


        networkManager = new NetworkManager(getApplicationContext());

        // 페이징, 스크롤처리, 서버에게 초기에 받는것까지
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemList = new ArrayList<>();

        adapter = new PostAdapter(itemList, this);
        recyclerView.setAdapter(adapter);

        닫기.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        // 스크롤 리스너 설정
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                visibleItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();
                if (dy < 0) {
                    if (!isLoading && !recyclerView.canScrollVertically(-1)){
                    //pastVisibleItems == 0) {

                        // 위로 스크롤하고 첫 번째 아이템이 보이는 경우
    //                            Toast.makeText(ChattingActivity.this, "getChats", Toast.LENGTH_SHORT).show();
    //                        Log.i(TAG, "리사이클러뷰 최상단 아이템 보임.");
                        getPosts(UP,itemList.get(0).post_id);
//                        Toast.makeText(FeedActivity.this, "up", Toast.LENGTH_SHORT).show();
                    }
//                    if(firstVisibleItemPosition == 0){
//                        Toast.makeText(FeedActivity.this, "first", Toast.LENGTH_SHORT).show();
//
//                    }
//                    if (recyclerView.computeVerticalScrollOffset() == 0) {            // is top of scroll.
//                        Toast.makeText(FeedActivity.this, "top", Toast.LENGTH_SHORT).show();
//
//
//
//                    }
                }


                if (!isLoading) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        // 다음 페이지 로드
//                        Toast.makeText(MainActivity.this, "getPosts", Toast.LENGTH_SHORT).show();
                        getPosts(DOWN,itemList.get(itemList.size()-1).post_id);
                    }


                }
            }
        });
        userId = getIntent().getIntExtra("userId",-1);

        int postId;
        postId = getIntent().getIntExtra("postId",-1);
        position = getIntent().getIntExtra("position",-1);
        if(position > 0){
            int previousPostId = getIntent().getIntExtra("previousPostId",-1);
            getPosts("down",previousPostId+1);

        }
        else{
            getPosts("down",postId+1);

        }


    }
    int position;

    private void getPosts(String type,int postId) {

        isLoading = true;
        networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
            @Override
            public void onResponseSuccess(Data data) {
                if (data.result.get("result").getAsString().equals(Data.SUCCESS)) {


                    String type = data.result.get("type").getAsString();

                    boolean setting = itemList.size()==0?true:false;

                    if(type.equals(DOWN)){
                        List<Post> postList = getPostList(data.result.getAsJsonArray("posts"));
                        for (int i = 0; i < postList.size(); i++) {
                            itemList.add(postList.get(i));
                            adapter.notifyItemInserted(itemList.size()-1);
                        }

                    }
                    else{
                        List<Post> postList = getPostList(data.result.getAsJsonArray("posts"));
                        for (int i = 0; i < postList.size(); i++) {
                            itemList.add(0,postList.get(i));
                            adapter.notifyItemInserted(0);
                        }
                    }

                    if(setting && position > 0){
                        recyclerView.scrollToPosition(1);
                    }
//                    recyclerView.scrollToPosition(1);
//                    adapter.notifyDataSetChanged();
                    isLoading = false;

                }
            }

            @Override
            public void onResponseFail(ResponseBody ErrorBody) {
                Toast.makeText(FeedActivity.this, "onResponseFail", Toast.LENGTH_SHORT).show();
                Log.i("e", ErrorBody.toString());


            }

            @Override
            public void onNetworkError(Throwable t) {
                Toast.makeText(FeedActivity.this, "onNetworkError", Toast.LENGTH_SHORT).show();
                Log.i("err", t.toString());
            }
        });


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("startPostId",postId);
        jsonObject.addProperty("userId",userId);
        jsonObject.addProperty("type",type);

        networkManager.GET2Request("posts", "getUserPost", jsonObject.toString());

    }


    List<Post> getPostList(JsonArray array) {

        List<Post> data = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {

            Post post = new Post();
            JsonObject object = array.get(i).getAsJsonObject();

            Log.i("Post JsonObject", object.toString());

            post.post_id = object.get("post_id").getAsInt();
            post.user_id = object.get("user_id").getAsInt();
            post.user_name = object.get("name").getAsString();
            post.comment = object.get("comment").getAsString();
            post.create_date = object.get("create_date").getAsString();

            if (object.has("update_date")) {
                post.update_date = object.get("update_date").getAsString();

            }


            post.isMyPost = object.get("isMyPost").getAsBoolean();
            post.isMyLike = object.get("isMyLike").getAsBoolean();
            post.comment_cnt = object.get("comment_cnt").getAsInt();
            post.like_cnt = object.get("like_cnt").getAsInt();
            post.user_profile = object.get("profile").getAsString();
            post.image = new ArrayList<>();


            JsonArray array1 = object.getAsJsonArray("post_image");

            Log.i("image result", object.getAsJsonArray("post_image").toString());


            for (int j = 0; j < array1.size(); j++) {
                post.image.add(array1.get(j).getAsString());
                Log.i("image", array1.get(j).getAsString());
            }


            data.add(post);
        }


        return data;

    }
    private static final int REQUEST_CODE2 = 201;  // 어떤 상수든 사용 가능

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PostAdapter.CommentActivityCode) {
            adapter.click = true;
        }

     if(requestCode == REQUEST_CODE2 && resultCode == RESULT_OK){


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
        else if(requestCode == PostAdapter.CommentActivityCode && resultCode == RESULT_OK){


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

}