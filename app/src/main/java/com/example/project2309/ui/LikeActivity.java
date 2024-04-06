package com.example.project2309.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project2309.R;
import com.example.project2309.adapter.LikeAdapter;
import com.example.project2309.adapter.RoomAdapter;
import com.example.project2309.data.Data;
import com.example.project2309.data.Like;
import com.example.project2309.data.Room;
import com.example.project2309.network.NetworkManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

public class LikeActivity extends AppCompatActivity {


    ImageButton 닫기;
    RecyclerView recyclerView;
    private boolean isLoading = false;
    String TAG = "LikeActivity";
    List<Like> itemList;
    LikeAdapter adapter;

    NetworkManager networkManager;

    int postId;

    EditText 검색창;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like);

        닫기 = findViewById(R.id.imageButton24);
        recyclerView = findViewById(R.id.recycler_view4);

        검색창 = findViewById(R.id.editTextText11);
        // 데이터 준비
        itemList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new LikeAdapter(itemList,this);
        recyclerView.setAdapter(adapter);


        postId = getIntent().getIntExtra("postId",-1);

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
//                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
//                        && firstVisibleItemPosition >= 0) {
//                    // 다음 페이지 로드
//                    getPosts();
//                }
                if (!isLoading) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        // 다음 페이지 로드
//                        Toast.makeText(RoomActivity.this, "getRooms", Toast.LENGTH_SHORT).show();

                        getLikes(false,검색창.getText().toString());
                    }
                }
            }
        });


         TextWatcher textWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력하기 전에 조치



            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력난에 변화가 있을 시 조치


            }
            public void afterTextChanged(Editable s) {
                // 입력이 끝났을 때 조치
                if (!isLoading) {
                    getLikes(true, s.toString());
                }

            }
        };

        검색창.addTextChangedListener(textWatcher);

        getLikes(true,"");


    }




    private void getLikes(boolean state, String keyword){



        isLoading = true;

//        if(state){
//            itemList.clear();
//        }

        networkManager = new NetworkManager(getApplicationContext());


        networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
            @Override
            public void onResponseSuccess(Data data) {

                if (data.result.get("result").getAsString().equals(Data.SUCCESS)) {


                    Log.i("log",data.result.toString());

                    List<Like> likeList = getLikeList(data.result.getAsJsonArray("likes"));


                    if(state && likeList.size() ==0){

                        TextView textView = findViewById(R.id.textView65);
                        boolean isSearch = data.result.get("isSearch").getAsBoolean();
                        if(!isSearch){
                            textView.setText("아직 좋아요가 없습니다.");

                        }
                        else{
                            textView.setText("사용자를 찾을 수 없음.");
                        }
                        textView.setVisibility(View.VISIBLE);

                    }
                    else{
                        TextView textView = findViewById(R.id.textView65);
                        textView.setVisibility(View.GONE);
                    }

                    if(state){
                        itemList.clear();
                    }

                    for (int i = 0; i < likeList.size(); i++) {
                        itemList.add(likeList.get(i));
                    }

                    adapter.notifyDataSetChanged();

                    isLoading = false;
                }

            }

            @Override
            public void onResponseFail(ResponseBody ErrorBody) {
                Toast.makeText(LikeActivity.this, "onResponseFail", Toast.LENGTH_SHORT).show();
                Log.i("e", ErrorBody.toString());
            }

            @Override
            public void onNetworkError(Throwable t) {
                Toast.makeText(LikeActivity.this, "onNetworkError", Toast.LENGTH_SHORT).show();
                Log.i("err", t.toString());
            }


        });

//        manager.GETRequest("comment","getinfo",post_id,last_comment_id);
//        JsonObject jsonObject = new JsonObject();



//        networkManager.GET2Request("room", "getinfo", Integer.toString(itemList.get(itemList.size()-1).post_id));

//        $postId =  $decoded_object->postId;
//        $lastLikeId =  $decoded_object->lastLikeId;
//        $keyword =  $decoded_object->keyword;


        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("postId",postId);
        if(!keyword.equals("")){
            jsonObject.addProperty("keyword",keyword);

        }

        if (state) {

//            jsonObject.addProperty("post_id", post_id);
        } else {

//            Toast.makeText(this, Integer.toString(itemList.get(itemList.size()-1).comment_id), Toast.LENGTH_SHORT).show();

            jsonObject.addProperty("lastLikeId",itemList.get(itemList.size()-1).likeId);

//            jsonObject.addProperty("lastCreateDate",itemList.get(itemList.size()-1).last_chat_date);


        }

        Log.i(TAG,jsonObject.toString());


        networkManager.GET2Request("posts","getLikes",jsonObject.toString());


    }



    List<Like> getLikeList(JsonArray array) {


        List<Like> list;
        list = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {

            JsonObject jsonObject = array.get(i).getAsJsonObject();

            Like like = new Like();

            like.introText = jsonObject.get("userIntroText").getAsString();
            like.name = jsonObject.get("userName").getAsString();
            like.profile = jsonObject.get("userProfile").getAsString();
            like.userId = jsonObject.get("userId").getAsInt();
            like.likeId = jsonObject.get("likeId").getAsInt();
            like.isMyLike = jsonObject.get("isMyLike").getAsBoolean();
            like.isFollowing = jsonObject.get("isFollowing").getAsBoolean();

//            if(jsonObject.has("followId")){
//                like.followId = jsonObject.get("followId").getAsInt();
//            }

            list.add(like);

        }

        return list;

    }




}