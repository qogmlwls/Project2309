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
import android.widget.TextView;
import android.widget.Toast;

import com.example.project2309.R;
import com.example.project2309.adapter.FollowListAdapter;
import com.example.project2309.adapter.RoomAdapter;
import com.example.project2309.data.Data;
import com.example.project2309.data.FollowUser;
import com.example.project2309.data.Like;
import com.example.project2309.data.Room;
import com.example.project2309.network.NetworkManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

public class FollowListActivity extends AppCompatActivity {

    NetworkManager networkManager;
    List<FollowUser> itemList;
    FollowListAdapter adapter;

    RecyclerView recyclerView;

    TextView textView, 총갯수;
    private boolean isLoading = false;

    String TAG = "FollowListActivity";

    int type,userPk;

    Boolean isMyPage;

    int totalCnt;


    public void setDeleteTotalCnt(){

        Log.i(TAG,Integer.toString(totalCnt));


        this.totalCnt--;
        총갯수.setText(Integer.toString(totalCnt) + " 명");

    }
    public void setInsertTotalCnt(){

        Log.i(TAG,Integer.toString(totalCnt));


        this.totalCnt++;
        총갯수.setText(Integer.toString(totalCnt) + " 명");

    }


    EditText 검색창;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_list);


        type = getIntent().getIntExtra("type",-1);
        userPk = getIntent().getIntExtra("userPk",-1);
        isMyPage = getIntent().getBooleanExtra("isMyPage",true);

//        intent.putExtra("isMyPage",IsMyPage);
//        intent.putExtra("userPk",UserPk);
//        intent.putExtra("type","Follower");
//        intent.putExtra("type","Following");

        textView = findViewById(R.id.textView50);

        if(type == FollowUser.Follower){
            textView.setText("팔로워");
        }
        else if(type == FollowUser.Following){
            textView.setText("팔로잉");
        }



        총갯수 = findViewById(R.id.textView53);

        recyclerView = findViewById(R.id.recyclerView3);


        // 데이터 준비
        itemList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FollowListAdapter(itemList,this);
        recyclerView.setAdapter(adapter);



        검색창 = findViewById(R.id.editTextText12);


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
//                        Toast.makeText(FollowListActivity.this, "getFollowLists", Toast.LENGTH_SHORT).show();
                        getLikes(false, 검색창.getText().toString());
//
//                        if(검색창.getText().toString().equals("")){
//                            getFollowData();                            getLikes(false, 검색창.getText().toString());
//
//                        }
//                        else{
//                            getLikes(false, 검색창.getText().toString());
//                        }


                    }
                }
            }
        });

        getFollowData();


    }


//    private void getFollowData(){
//
//    }



    private void getLikes(boolean state, String keyword){

        if(isLoading){return;}

        isLoading = true;
        networkManager = new NetworkManager(getApplicationContext());

        networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
            @Override
            public void onResponseSuccess(Data data) {

                if (data.result.get("result").getAsString().equals(Data.SUCCESS)) {


                    Log.i("log",data.result.toString());
                    totalCnt = data.result.get("totalCnt").getAsInt();
                    총갯수.setText(Integer.toString(totalCnt) + " 명");

                    List<FollowUser> FollowUserList = getList(data.result.getAsJsonArray("followDatas"));
//                    for(int i=0;i<FollowUserList.size();i++){
//                        FollowUser followUser = FollowUserList.get(i);
//                        itemList.add(followUser);
//                        adapter.notifyItemInserted(itemList.size()-1);
//                    }

//                    adapter.notifyDataSetChanged();

                    if(state && FollowUserList.size() ==0){

                        TextView textView = findViewById(R.id.textView28);
                        boolean isSearch = data.result.get("isSearch").getAsBoolean();

//                        else{
//                            textView.setText("사용자를 찾을 수 없음.");
//                        }

                        if(isSearch){
                            textView.setText("사용자를 찾을 수 없음.");
                            textView.setVisibility(View.VISIBLE);
                        }
                        else{
                            textView.setVisibility(View.GONE);
                        }

                    }
                    else{

                        TextView textView = findViewById(R.id.textView28);

//                        TextView textView = findViewById(R.id.textView65);
                        textView.setVisibility(View.GONE);
                    }

                    if(state){
                        itemList.clear();
                        adapter.notifyDataSetChanged();
                    }

                    for (int i = 0; i < FollowUserList.size(); i++) {
                        itemList.add(FollowUserList.get(i));
                        adapter.notifyItemInserted(itemList.size()-1);

                    }

//                    adapter.notifyDataSetChanged();

                    isLoading = false;
                }

            }

            @Override
            public void onResponseFail(ResponseBody ErrorBody) {
                Toast.makeText(FollowListActivity.this, "onResponseFail", Toast.LENGTH_SHORT).show();
                Log.i("e", ErrorBody.toString());
            }

            @Override
            public void onNetworkError(Throwable t) {
                Toast.makeText(FollowListActivity.this, "onNetworkError", Toast.LENGTH_SHORT).show();
                Log.i("err", t.toString());
            }


        });

        //-------

        JsonObject jsonObject = new JsonObject();



        if(type == FollowUser.Following){
            jsonObject.addProperty("type","following");

        }
        else if(type == FollowUser.Follower){
            jsonObject.addProperty("type","follower");

        }


        if(!keyword.equals("")){
            jsonObject.addProperty("keyword",keyword);
        }


        jsonObject.addProperty("isMyInfoRequest",isMyPage);
        jsonObject.addProperty("pk",userPk);


        if(!state&& itemList.size() != 0){
            jsonObject.addProperty("lastFollowDataId",itemList.get(itemList.size()-1).followId);

        }


        Log.i(TAG,jsonObject.toString());

        networkManager.GET2Request("follow","getinfo",jsonObject.toString());

    }

    private void getFollowData(){


        if(isLoading){return;}

        isLoading = true;


        networkManager = new NetworkManager(getApplicationContext());
        networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
            @Override
            public void onResponseSuccess(Data data) {

                Log.i(TAG,data.result.toString());
                if (data.result.get("result").getAsString().equals(Data.SUCCESS)) {

//                    {"result":"success","totalCnt":1,"followDatas":[{"name":"teamnova1","profile":"../Image/2023-10-06 14:57:16.904","userId":"20","myInfo":null}]}

                    totalCnt = data.result.get("totalCnt").getAsInt();
                    총갯수.setText(Integer.toString(totalCnt) + " 명");

                    List<FollowUser> FollowUserList = getList(data.result.getAsJsonArray("followDatas"));
                    for(int i=0;i<FollowUserList.size();i++){
                        FollowUser followUser = FollowUserList.get(i);
                        itemList.add(followUser);
                    }

                    adapter.notifyDataSetChanged();

                    isLoading = false;

                }

            }

            @Override
            public void onResponseFail(ResponseBody ErrorBody) {
                Toast.makeText(FollowListActivity.this, "onResponseFail", Toast.LENGTH_SHORT).show();
                Log.i("e", ErrorBody.toString());
            }

            @Override
            public void onNetworkError(Throwable t) {
                Toast.makeText(FollowListActivity.this, "onNetworkError", Toast.LENGTH_SHORT).show();
                Log.i("err", t.toString());
            }


        });

        JsonObject jsonObject = new JsonObject();



         if(type == FollowUser.Following){
             jsonObject.addProperty("type","following");

         }
         else if(type == FollowUser.Follower){
             jsonObject.addProperty("type","follower");

         }

         jsonObject.addProperty("isMyInfoRequest",isMyPage);
         jsonObject.addProperty("pk",userPk);

         if (itemList.size() != 0) {
             jsonObject.addProperty("lastFollowDataId",itemList.get(itemList.size()-1).followId);

         }

        Log.i(TAG,jsonObject.toString());

        networkManager.GET2Request("follow","getinfo",jsonObject.toString());


    }


    private List<FollowUser> getList(JsonArray array){

        List<FollowUser> list = new ArrayList<>();

//  ,"followDatas":[{"name":"teamnova1","profile":"../Image/2023-10-06 14:57:16.904","userId":"20","myInfo":null}]}


        for(int i=0;i<array.size();i++){
            JsonObject jsonObject = array.get(i).getAsJsonObject();
//            array.getAsJsonObject()
            String name = jsonObject.get("name").getAsString();
            String profile  = jsonObject.get("profile").getAsString();
            String followId = jsonObject.get("followId").getAsString();
            Boolean myInfo = jsonObject.get("myInfo").getAsBoolean();

            Boolean isFollowing = jsonObject.get("isFollowing").getAsBoolean();
            Boolean isMe = jsonObject.get("isMe").getAsBoolean();

            int userId = jsonObject.get("userId").getAsInt();



            FollowUser user = new FollowUser();

            user.type = type;

            user.isMyFollowData = myInfo;
            user.followId = Integer.parseInt(followId);
            user.name = name;
            user.profile = profile;

            user.userId = userId;
            user.isFollowing = isFollowing;

            user.isMe = isMe;

            list.add(user);

        }


        return list;

    }


}