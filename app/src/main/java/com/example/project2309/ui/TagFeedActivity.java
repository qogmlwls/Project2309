package com.example.project2309.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project2309.R;
import com.example.project2309.adapter.PostAdapter;
import com.example.project2309.data.Data;
import com.example.project2309.data.Post;
import com.example.project2309.data.UserFeed;
import com.example.project2309.network.NetworkManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

public class TagFeedActivity extends AppCompatActivity {


    RecyclerView recyclerView;

    ImageButton 닫기;
    private List<Post> itemList;
    private PostAdapter adapter;


    private boolean isLoading = false;

    NetworkManager networkManager;


    TextView 해쉬태그이름, 게시글총수;

    ProgressBar 로딩바;

    View 하단공간;

    String TAG = "TagFeedActivity";
    String Hashtag_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_feed);


        닫기 = findViewById(R.id.imageButton27);
        해쉬태그이름 = findViewById(R.id.textView78);
        게시글총수 = findViewById(R.id.textView79);

        recyclerView = findViewById(R.id.recyclerView8);
        로딩바 = findViewById(R.id.progressBar5);
        하단공간 = findViewById(R.id.view23);
        // 데이터 준비
        itemList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostAdapter(itemList,this);
        recyclerView.setAdapter(adapter);


        Hashtag_name = getIntent().getStringExtra("Hashtag_name");
        해쉬태그이름.setText("#"+Hashtag_name);

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

                if (!isLoading && totalItemCount > 0) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && !recyclerView.canScrollVertically(1)) {

                        // 리스트가 스크롤 가능한지 여부를 확인하여 맨 아래로 스크롤되었을 때만 호출
                        로딩바.setVisibility(View.VISIBLE);
                        하단공간.setVisibility(View.VISIBLE);
                        getPosts();
                    }
                }
            }
        });

//        // 스크롤 리스너 설정
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                int visibleItemCount = layoutManager.getChildCount();
//                int totalItemCount = layoutManager.getItemCount();
//                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
//
//                if (!isLoading) {
//
//                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
//                            && firstVisibleItemPosition >= 0) {
//
//                        로딩바.setVisibility(View.VISIBLE);
//                        하단공간.setVisibility(View.VISIBLE);
//                        getPosts();
//
//
//                    }
//                }
//            }
//        });

        networkManager = new NetworkManager(getApplicationContext());

        // 서버에 게시글 데이터와 게시글 총수 요청
        getPosts();


    }


    private void getPosts() {

        isLoading = true;

//        NetworkManager networkManager = new NetworkManager(getApplicationContext());
        networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
            @Override
            public void onResponseSuccess(Data data) {

                if (data.result.get("result").getAsString().equals(Data.SUCCESS)) {

                    Log.i(TAG,data.result.toString());

                    String total = data.result.get("total_posts").getAsString();
                    게시글총수.setText("게시물 총 "+total + "개");

                    long time;
                    if(itemList.size() <2){
                        time = 0;
                    }
                    else{
                        time = 1000;
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            List<Post> postList = getPostList(data.result.getAsJsonArray("posts"));
                            for (int i = 0; i < postList.size(); i++) {
                                itemList.add(postList.get(i));
                            }

                            adapter.notifyDataSetChanged();
                            로딩바.setVisibility(View.GONE);
                            하단공간.setVisibility(View.GONE);
                            isLoading = false;

                        }
                    },time);

                }
                else{
                    로딩바.setVisibility(View.GONE);
                    하단공간.setVisibility(View.GONE);
                    isLoading = false;
                }



            }

            @Override
            public void onResponseFail(ResponseBody ErrorBody) {
                Toast.makeText(TagFeedActivity.this, "onResponseFail", Toast.LENGTH_SHORT).show();
                Log.i("e", ErrorBody.toString());


            }

            @Override
            public void onNetworkError(Throwable t) {
                Toast.makeText(TagFeedActivity.this, "onNetworkError", Toast.LENGTH_SHORT).show();
                Log.i("err", t.toString());
            }
        });

        JsonObject jsonObject = new JsonObject();
        if(itemList.size() != 0){

            jsonObject.addProperty("startPostId",itemList.get(itemList.size()-1).post_id);
            jsonObject.addProperty("like_cnt",itemList.get(itemList.size()-1).like_cnt);

        }
//        jsonObject.addProperty("userId",UserPk);
        jsonObject.addProperty("type","down");
        jsonObject.addProperty("Hashtag_name",Hashtag_name);

        networkManager.GET2Request("posts", "getTagPost", jsonObject.toString());
//        getUserPost

    }
//    // $keyword =  $decoded_object->keyword;
//    $postId = $decoded_object->startPostId;
//    // $last_post_id = $decoded_object->last_post_id;
//    $Hashtag_name = $decoded_object->Hashtag_name;
//    $like_cnt = $decoded_object->like_cnt;
//
//    // $userId = $decoded_object->userId;
//    $type = $decoded_object->type;




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


    // *  userFeed.userId = -2;
    List<UserFeed> getFeedList(JsonArray array) {

        List<UserFeed> data = new ArrayList<>();

//        Log.i(TAG,array.;
        for (int i = 0; i < array.size(); i++) {

            UserFeed userFeed = new UserFeed();
            JsonObject object = array.get(i).getAsJsonObject();

            Log.i("Post JsonObject", object.toString());

            userFeed.postId = object.get("post_id").getAsInt();
            userFeed.userId = -2;
            userFeed.like_cnt = object.get("like_cnt").getAsInt();
            JsonArray array1 = object.getAsJsonArray("post_image");

            Log.i("image result", object.getAsJsonArray("post_image").toString());
            userFeed.imagePath = array1.get(0).getAsString();

            data.add(userFeed);
        }

        return data;

    }
}