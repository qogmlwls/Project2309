package com.example.project2309.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project2309.MyApplication;
import com.example.project2309.adapter.UserPostAdapter;
import com.example.project2309.common.util;
import com.example.project2309.data.Data;
import com.example.project2309.data.FollowUser;
import com.example.project2309.data.Post;
import com.example.project2309.data.UserFeed;
import com.example.project2309.network.NetworkManager;
import com.example.project2309.R;
import com.example.project2309.common.Message;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;

public class MyPageActivity extends AppCompatActivity {

    // Toast() -> showToast()로 함수 이름 변경 추천함
    // getData 함수에서 Toast를 띄우지 말고, return 값 받아서 Toast 처리하는걸 추천함.

    private static final int REQUEST_CODE = 100;  // 어떤 상수든 사용 가능

    Button 프로필편집페이지로이동버튼, 홈으로이동버튼, 채팅방목록으로이동;
    ImageButton 설정페이지로이동버튼;


    NetworkManager.CallbackFunc<Data> 로그인이후내정보요청후콜백함수;
    NetworkManager.CallbackFunc<ResponseBody> 이미지받아오기;


    ImageView 프로필이미지;
    TextView 소개글, 닉네임;
    NetworkManager networkManager;

    Boolean IsMyPage;

    // IsMyPage가 false면 어떤 유저의 정보를 보여주는지, 정보에 해당하는 유저의 식별자.
    int UserPk;

    private boolean isLoading = false;


    private RecyclerView recyclerView;
    private List<UserFeed> itemList;
    private UserPostAdapter adapter;


    int postCnt, followeeCnt, followerCnt;
    // follower :내가 follow하는 유저들
    // followee : 나를 팔로우하는 유저들


    Button 팔로워, 팔로잉,게시글;

    Boolean isMeFollowState;

    int FollowerActivity = 101,FollowingActivity = 102;



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

                    TextView textView = findViewById(R.id.textView72);
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
        setContentView(R.layout.activity_my_page);

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter("1"));



        Button 검색;
        검색 = findViewById(R.id.button18);

        검색.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyPageActivity.this, SearchActivity.class);
                startActivity(intent);
                finish();
            }
        });


        프로필편집페이지로이동버튼 = findViewById(R.id.button29);
        홈으로이동버튼 = findViewById(R.id.button17);
        설정페이지로이동버튼 = findViewById(R.id.imageButton);
        채팅방목록으로이동 = findViewById(R.id.button19);

        닉네임 = findViewById(R.id.textView24);
//        채팅창으로이동 = findViewById(R.id.button45);


        프로필이미지 = findViewById(R.id.imageView2);
        소개글 = findViewById(R.id.textView20);


        팔로워 = findViewById(R.id.button27);
        팔로잉 = findViewById(R.id.button28);
        게시글 = findViewById(R.id.button26);


        팔로워.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyPageActivity.this, FollowListActivity.class);

                intent.putExtra("isMyPage",IsMyPage);
                intent.putExtra("userPk",UserPk);
                intent.putExtra("type", FollowUser.Follower);

                startActivityForResult(intent,FollowerActivity);

            }
        });


        팔로잉.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyPageActivity.this, FollowListActivity.class);

                intent.putExtra("isMyPage",IsMyPage);
                intent.putExtra("userPk",UserPk);
                intent.putExtra("type",FollowUser.Following);

                startActivityForResult(intent,FollowingActivity);

            }
        });

        networkManager = new NetworkManager(getApplicationContext());

        IsMyPage = getIntent().getBooleanExtra("IsMyPage", true);
        UserPk = getIntent().getIntExtra("UserPk",-1);

        Log.i("MyPageActivity","IsMyPage : " + Boolean.toString(IsMyPage));
        Log.i("MyPageActivity","UserPk : " + Integer.toString(UserPk));



        if(!IsMyPage && UserPk == -1){
            Toast.makeText(this, "UserPk 문제 발생", Toast.LENGTH_SHORT).show();
        }


        Button 채팅 = findViewById(R.id.button45);
        Button 팔로우 = findViewById(R.id.button46);

        if(IsMyPage){
            채팅.setVisibility(View.GONE);
            팔로우.setVisibility(View.GONE);
            프로필편집페이지로이동버튼.setVisibility(View.VISIBLE);

            설정페이지로이동버튼.setVisibility(View.VISIBLE);


        }
        else{
            채팅.setVisibility(View.VISIBLE);
            팔로우.setVisibility(View.VISIBLE);
            설정페이지로이동버튼.setVisibility(View.GONE);

            프로필편집페이지로이동버튼.setVisibility(View.GONE);

             채팅.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {

//                     Toast.makeText(MyPageActivity.this, "클릭", Toast.LENGTH_SHORT).show();
                        networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
                            @Override
                            public void onResponseSuccess(Data data) {

                                Log.i("MyPageActivity__",data.result.toString());

                                JsonObject responseResult = data.result;
                                String result = getData(responseResult,Data.RESULT);
                                if(result == null){
                                    return;
                                }

                                if(result.equals(Data.SUCCESS)) {

                                    // 방이 없으면  0을 반환.
                                    int roomId = responseResult.get("roomId").getAsInt();
                                    Intent intent = new Intent(MyPageActivity.this, ChattingActivity.class);
                                    intent.putExtra("roomId",roomId);
                                    intent.putExtra("fromPk",UserPk);

                                    startActivity(intent);

                                }
                                else{

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
                        });

                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("userPk",UserPk);
                        networkManager.GETRequest3("User","getRoomId",jsonObject);
                        //             채팅 아이디 요청

                 }
             });


             팔로우.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {


                     networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
                         @Override
                         public void onResponseSuccess(Data data) {

                             Log.i("MyPageActivity",data.result.toString());


                             if (data.result.get("result").getAsString().equals(Data.SUCCESS)) {

                                 JsonObject jsonObject = data.result.getAsJsonObject("isFollowMe");
                                 isMeFollowState = jsonObject.get("state").getAsBoolean();
                                 String text = jsonObject.get("btnText").getAsString();
                                 팔로우.setText(text);

                                 if(isMeFollowState){

                                     followeeCnt++;
                                     팔로워.setText(Integer.toString(followeeCnt)+"\n팔로워");


                                     JSONObject jsonObject1 = new JSONObject();
                                     try {
                                         jsonObject1.put("type","RequestFollowNoti");
                                         jsonObject1.put("FollowUserId",UserPk);
                                         jsonObject1.put("UserName",data.result.get("myName").getAsString());

                                     } catch (JSONException e) {
                                         throw new RuntimeException(e);
                                     }

//                                     MyApplication
                                     MyApplication application = (MyApplication)getApplication();
                                     application.SendMessage(jsonObject1);

                                 }
                                 else{
                                     followeeCnt--;
                                     팔로워.setText(Integer.toString(followeeCnt)+"\n팔로워");
                                 }

                             }
//                             'result' =>'success'
//                                     , 'isFollowMe' => $isFollowMe
//                             // ,'comments' => $comments
//                             $isFollowMe = [
//                             'state' => false,
//                                     'btnText' => '팔로우'
//                    ];
                         }

                         @Override
                         public void onResponseFail(ResponseBody ErrorBody) {
                             Toast.makeText(MyPageActivity.this, "onResponseFail", Toast.LENGTH_SHORT).show();
                             Log.i("MyPageActivity", ErrorBody.toString());
                         }

                         @Override
                         public void onNetworkError(Throwable t) {
                             Toast.makeText(MyPageActivity.this, "onNetworkError", Toast.LENGTH_SHORT).show();
                             Log.i("MyPageActivity", t.toString());
                         }
                     });


                     JsonObject jsonObject = new JsonObject();

                     jsonObject.addProperty("pk",UserPk);

                     networkManager.post2Request("follow","setFollowing",jsonObject);


                 }
             });



        }

        이미지받아오기 = new NetworkManager.CallbackFunc<ResponseBody>() {
            @Override
            public void onResponseSuccess(ResponseBody data) {

                Bitmap image = BitmapFactory.decodeStream(data.byteStream());
                프로필이미지.setImageBitmap(image);

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
        로그인이후내정보요청후콜백함수 = new NetworkManager.CallbackFunc<Data>() {
            @Override
            public void onResponseSuccess(Data data) {

                // 값이 정상적으로 왔는지 확인
                // 각 값마다(5개) 작업 처리, 예외 처리.

                Log.i("MyPageActivity",data.result.toString());
                JsonObject responseResult = data.result;
                String result = getData(responseResult,Data.RESULT);
                if(result == null){
                    return;
                }

                if(result.equals(Data.SUCCESS)){

                    String nickName = getData(responseResult,"name");
                    String introText = getData(responseResult,"introText");
                    String profileURL = getData(responseResult, "profile");



                    if(nickName == null || introText == null || profileURL == null){
                        return;
                    }

                    닉네임.setText(nickName);
                    소개글.setText(introText);

                    // 내정보 요청.
                    if(profileURL.length()!=0){
                        networkManager.setCallback(이미지받아오기);
                        networkManager.GETImage(profileURL);

                    }
                    else{
                        프로필이미지.setImageResource(R.drawable.baseline_person_24);
                    }



                    postCnt = responseResult.get("postCnt").getAsInt();
                    followeeCnt = responseResult.get("followeeCnt").getAsInt();
                    followerCnt = responseResult.get("followerCnt").getAsInt();


                    게시글.setText(Integer.toString(postCnt)+"\n게시글");
                    팔로워.setText(Integer.toString(followeeCnt)+"\n팔로워");
                    팔로잉.setText(Integer.toString(followerCnt)+"\n팔로잉");

                    // follower :내가 follow하는 유저들
                    // followee : 나를 팔로우하는 유저들

//                    IsMyPage
                    if(!IsMyPage){

                        JsonObject jsonObject = responseResult.getAsJsonObject("isFollowMe");
                        isMeFollowState = jsonObject.get("state").getAsBoolean();
                        String text = jsonObject.get("btnText").getAsString();

                        팔로우.setText(text);
//                        $isFollowMe = [
//                        'state' => false,
//                                'btnText' => '맞팔로우'
//                    ];
                    }
                }
                else if(result.equals(Data.Fail)){

                    String reasonCode = getData(responseResult,Data.REASONCODE);
                    String reasonDescription = getData(responseResult,Data.REASONDESCRIPTION);

                    if(reasonCode.equals("503")){
                        Toast("세션 만료. 다시 로그인을 해주세요.");
                        startActivity(LoginActivity.class);
                        finish();
                    }
                    else{
                        Toast(reasonCode + " : "+reasonDescription);
                    }

                }
                else if(result.equals(Data.ERROR)){

                    String errorCode = getData(responseResult,Data.ERRCODE);
                    String errorDescription = getData(responseResult,Data.ERRDESCRIPTION);

                    if(errorCode == null || errorDescription == null ){
                        return;
                    }

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


        if(!IsMyPage){
            Button button = findViewById(R.id.button20);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(MyPageActivity.class);
                    finish();
                }
            });
        }


        프로필편집페이지로이동버튼.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MyPageActivity.this, EditProfileActivity.class);
                startActivityForResult(intent, REQUEST_CODE);

            }
        });

        홈으로이동버튼.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(MainActivity.class);
                finish();
            }
        });

        설정페이지로이동버튼.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(SettingActivity.class);

            }
        });

        채팅방목록으로이동.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(RoomActivity.class);

            }
        });

//        채팅창으로이동.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(ChattingActivity.class);
//
//            }
//        });



        // 데이터 준비
        itemList = new ArrayList<>();

//        for(int i=0;i<10;i++){
//            UserFeed userFeed = new UserFeed();
//
//            userFeed.imagePath="";
//            userFeed.postId = 2;
//            itemList.add(userFeed);
//        }


        // 리사이클러뷰 설정
        recyclerView = findViewById(R.id.recycler_view2);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        adapter = new UserPostAdapter(itemList,this);
        recyclerView.setAdapter(adapter);

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
////                        Toast.makeText(MainActivity.this, "getPosts", Toast.LENGTH_SHORT).show();
//                        int postId;
//                        if(itemList.size() == 0){
//                            postId = -1;
//                        }
//                        else{
//                            postId = itemList.get(itemList.size()-1).postId;
//                        }
                        getPosts();
                    }
                }
            }
        });



        게시글.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(itemList.size() != 0){
                    Intent intent = new Intent(MyPageActivity.this, FeedActivity.class);
                    intent.putExtra("position", 0);
                    intent.putExtra("postId", itemList.get(0).postId);
                    intent.putExtra("userId", itemList.get(0).userId);
                    startActivity(intent);
                }


            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        networkManager.setCallback(로그인이후내정보요청후콜백함수);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("isMyPage",IsMyPage);
        jsonObject.addProperty("userPk",UserPk);
        networkManager.GETRequest3("User","getInfo",jsonObject);

        itemList.clear();
        getPosts();

        getUnReadChat();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // 프로필이미지 url, 닉네임, 소개글
                // 결과를 받아옴
                String profileURL = data.getStringExtra("profileImage");
                String nickName = data.getStringExtra("name");
                String introText = data.getStringExtra("introText");

                // 결과 사용
                // 내정보 요청 및 값 반영.
                if(profileURL.length()!=0){
                    networkManager.setCallback(이미지받아오기);
                    networkManager.GETImage(profileURL);

                }
                else{
                    프로필이미지.setImageResource(R.drawable.baseline_person_24);
                }
                닉네임.setText(nickName);
                소개글.setText(introText);

            }
        }

        else if(requestCode == FollowerActivity){
            if (resultCode == RESULT_OK) {
//                resultIntent.putExtra("deleteCnt",deleteCnt);
                int deleteCnt = data.getIntExtra("deleteCnt",0);
//                int postCnt, followeeCnt, followerCnt;
//                // follower :내가 follow하는 유저들
//                // followee : 나를 팔로우하는 유저들
                followeeCnt  = followeeCnt-deleteCnt;
                팔로워.setText(Integer.toString(followeeCnt)+"\n팔로워");

            }
        }
        else if(requestCode == FollowingActivity){
            if (resultCode == RESULT_OK) {
                int deleteCnt = data.getIntExtra("deleteCnt",0);
                followerCnt  = followerCnt-deleteCnt;
                팔로잉.setText(Integer.toString(followerCnt)+"\n팔로잉");
            }
        }


    }

    String getData(JsonObject object, String data){

        // Data.RESULT 값이 없으면 예외 처리
        if (object.has(data)) {
            // 여기에 결과 처리 코드 추가
            return  object.get(data).getAsString();
        } else {
            // Data.RESULT가 없는 경우에 대한 예외 처리
            // 예: 로그를 남기거나, 적절한 조치를 취함
            Toast(Message.ResponseParsingException);
            return null;
        }

    }


    void Toast(String message){
        Toast.makeText(MyPageActivity.this, message, Toast.LENGTH_SHORT).show();

    }

    <T> void startActivity(Class<T> activityClass){
        util.startMyActivity(MyPageActivity.this,activityClass);
    }


    private void getPosts() {

        isLoading = true;

        NetworkManager networkManager = new NetworkManager(getApplicationContext());
        networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
            @Override
            public void onResponseSuccess(Data data) {
                if (data.result.get("result").getAsString().equals(Data.SUCCESS)) {

                    Log.i(TAG,data.result.toString());
                    List<UserFeed> postList = getFeedList(data.result.getAsJsonArray("posts"));
                    for (int i = 0; i < postList.size(); i++) {
                        itemList.add(postList.get(i));
                    }

//                    recyclerView.ScrollToPosition(5);


                    adapter.notifyDataSetChanged();
                    isLoading = false;
//                    recyclerView.smoothScrollToPosition(2);
                }
            }

            @Override
            public void onResponseFail(ResponseBody ErrorBody) {
                Toast.makeText(MyPageActivity.this, "onResponseFail", Toast.LENGTH_SHORT).show();
                Log.i("e", ErrorBody.toString());


            }

            @Override
            public void onNetworkError(Throwable t) {
                Toast.makeText(MyPageActivity.this, "onNetworkError", Toast.LENGTH_SHORT).show();
                Log.i("err", t.toString());
            }
        });


//        if (itemList.size() == 0)
//            networkManager.GETRequest("test", "posts");
//        else
//            networkManager.GETRequest("test", "posts", Integer.toString(itemList.get(itemList.size() - 1).postId));
//
        // $postId =  $decoded_object->postId;
        // $lastLikeId =  $decoded_object->lastLikeId;
        // $keyword =  $decoded_object->keyword;
//        $postId = $decoded_object->startPostId;
//        // $last_post_id = $decoded_object->last_post_id;
//        $userId = $decoded_object->userId;
//        $type = $decoded_object->type;

        JsonObject jsonObject = new JsonObject();
        if(itemList.size() != 0)
            jsonObject.addProperty("startPostId",itemList.get(itemList.size()-1).postId);
        jsonObject.addProperty("userId",UserPk);
        jsonObject.addProperty("type","down");

        networkManager.GET2Request("posts", "getUserPost", jsonObject.toString());


    }

    String TAG = "MyPageActivity";

    List<UserFeed> getFeedList(JsonArray array) {

        List<UserFeed> data = new ArrayList<>();

//        Log.i(TAG,array.;
        for (int i = 0; i < array.size(); i++) {

            UserFeed userFeed = new UserFeed();
            JsonObject object = array.get(i).getAsJsonObject();

            Log.i("Post JsonObject", object.toString());

            userFeed.postId = object.get("post_id").getAsInt();
            userFeed.userId = UserPk;
//            post.user_id = object.get("user_id").getAsInt();
//            post.user_name = object.get("name").getAsString();
//            post.comment = object.get("comment").getAsString();
//            post.create_date = object.get("create_date").getAsString();

//            if (object.has("update_date")) {
//                post.update_date = object.get("update_date").getAsString();
//
//            }


//            post.isMyPost = object.get("isMyPost").getAsBoolean();
//            post.isMyLike = object.get("isMyLike").getAsBoolean();
//            post.comment_cnt = object.get("comment_cnt").getAsInt();
//            post.like_cnt = object.get("like_cnt").getAsInt();
//            post.user_profile = object.get("profile").getAsString();
//            post.image = new ArrayList<>();


            JsonArray array1 = object.getAsJsonArray("post_image");

            Log.i("image result", object.getAsJsonArray("post_image").toString());


//            for (int j = 0; j < array1.size(); j++) {
//                post.image.add(array1.get(j).getAsString());
//                Log.i("image", array1.get(j).getAsString());
//            }

            userFeed.imagePath = array1.get(0).getAsString();

            data.add(userFeed);
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

                    TextView textView = findViewById(R.id.textView72);
                    textView.bringToFront();

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