package com.example.project2309.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project2309.R;
import com.example.project2309.adapter.ChatAdapter;
import com.example.project2309.adapter.SearchAdapter;
import com.example.project2309.data.Data;
import com.example.project2309.data.User;
import com.example.project2309.network.NetworkManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

public class SearchActivity extends AppCompatActivity {

    Button 홈, 마이페이지, 채팅;

    EditText 검색입력창;
    ImageButton 검색버튼;
    NetworkManager networkManager;
    String TAG = "SearchActivity";

    int totalCnt = 0;


    String type;
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

                    TextView textView = findViewById(R.id.textView70);
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

    View 하단공간;

    ProgressBar 하단로딩바,검색로딩바;
    List<User> itemList;
    boolean isLoading = false;

    SearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter("1"));


        RecyclerView recyclerView;
        recyclerView = findViewById(R.id.recyclerView6);



        // 데이터 준비
        itemList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SearchAdapter(itemList,this);
        recyclerView.setAdapter(adapter);

        하단공간 = findViewById(R.id.view24);
        검색로딩바 = findViewById(R.id.progressBar6);
        하단로딩바 = findViewById(R.id.progressBar7);

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

                        하단로딩바.setVisibility(View.VISIBLE);
                        하단공간.setVisibility(View.VISIBLE);

                        if(type.equals("user")){
                            UserMoreData(검색입력창.getText().toString());
                        }
                        else if(type.equals("hashTag")){
                            UserMoreData(검색입력창.getText().toString());

                        }
                        else{
                            Log.i(TAG,"리사이클러뷰 type : "+type);

                        }

                    }
                }
            }
        });




        홈 = findViewById(R.id.button49);
        홈.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        마이페이지 = findViewById(R.id.button52);
        마이페이지.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this,MyPageActivity.class);
                startActivity(intent);
                finish();
            }
        });


        채팅 = findViewById(R.id.button51);

        채팅.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, RoomActivity.class);
                startActivity(intent);
                finish();
            }
        });

        검색입력창 = findViewById(R.id.editTextText10);
        검색버튼 = findViewById(R.id.imageButton17);
        networkManager = new NetworkManager(getApplicationContext());

        검색버튼.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String searchText = 검색입력창.getText().toString();

                TextView 검색결과가없습니다텍스트뷰 = findViewById(R.id.textView58);
                검색결과가없습니다텍스트뷰.setVisibility(View.GONE);

                if(searchText.length() == 0){
                    Toast.makeText(SearchActivity.this, "검색어를 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                isLoading = true;
                itemList.clear();
                검색로딩바.setVisibility(View.VISIBLE);


                networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
                    @Override
                    public void onResponseSuccess(Data data) {

                        Log.i(TAG,data.result.toString());


                        if( data.result.get("result").getAsString().equals(Data.SUCCESS)) {

//                            Toast.makeText(SearchActivity.this, "검색", Toast.LENGTH_SHORT).show();

                            JsonArray jsonArray;

                            if(type.equals("user")) {
                                jsonArray = data.result.getAsJsonArray("users");

                            }
                            else if(type.equals("hashTag")) {
                                jsonArray = data.result.getAsJsonArray("hashTags");

                            }
                            else{
                                Log.i(TAG,"type : "+type);
                                return;
                            }


                            if(jsonArray.size()==0){
                                TextView 검색결과가없습니다텍스트뷰 = findViewById(R.id.textView58);
                                검색결과가없습니다텍스트뷰.setVisibility(View.VISIBLE);
                            }

                            for(int i=0;i<jsonArray.size();i++){

                                JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                                User user = new User();

                                if(type.equals("user")){
                                    user.type = User.USER;

                                    user.name = jsonObject.get("name").getAsString();
                                    user.profile = jsonObject.get("profile").getAsString();
                                    user.introText = jsonObject.get("introText").getAsString();

                                    user.isMyData = jsonObject.get("isMyData").getAsBoolean();
                                    user.userId = jsonObject.get("userId").getAsInt();
                                }
                                else if(type.equals("hashTag")){
                                    user.type = User.HASHTAG;
                                    user.userId = jsonObject.get("userId").getAsInt();
                                    user.name = jsonObject.get("name").getAsString();
                                    user.count =  jsonObject.get("count").getAsInt();
                                }

                                itemList.add(user);
                            }


                            adapter.notifyDataSetChanged();


                        }

                        검색로딩바.setVisibility(View.GONE);
                        isLoading = false;

                    }

                    @Override
                    public void onResponseFail(ResponseBody ErrorBody) {
                        Toast.makeText(SearchActivity.this, "onResponseFail", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, ErrorBody.toString());
                    }

                    @Override
                    public void onNetworkError(Throwable t) {
                        Toast.makeText(SearchActivity.this, "onNetworkError", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, t.toString());
                    }
                });

                JsonObject jsonObject = new JsonObject();

                jsonObject.addProperty("searchText",searchText);
                jsonObject.addProperty("type",type);
                networkManager.post2Request("search","search",jsonObject);


            }
        });
        type = "user";

        RadioGroup radioGroup = findViewById(R.id.radioGroup);

// 선택된 RadioButton 변경 시 이벤트 처리
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // 선택된 RadioButton ID 확인
                switch (checkedId) {
                    case R.id.radioButton2:
                        // 옵션 1 선택 시 처리
                        type = "user";
                        break;
                    case R.id.radioButton:
                        // 옵션 2 선택 시 처리
                        type = "hashTag";
                        break;
                    // 추가적인 RadioButton들에 대한 처리
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        getUnReadChat();
    }


    public void getUnReadChat(){

        NetworkManager networkManager1 = new NetworkManager(getApplicationContext());
        networkManager1.setCallback(new NetworkManager.CallbackFunc<Data>() {
            @Override
            public void onResponseSuccess(Data data) {
                if (data.result.get("result").getAsString().equals(Data.SUCCESS)) {


                    Log.i(TAG, data.result.toString());

                    totalCnt = data.result.get("total").getAsInt();
                    Log.i(TAG, Integer.toString(totalCnt));


                    TextView textView = findViewById(R.id.textView70);
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


    void UserMoreData(String searchText){
        isLoading = true;

        networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
            @Override
            public void onResponseSuccess(Data data) {

                Log.i(TAG,data.result.toString());


                if( data.result.get("result").getAsString().equals(Data.SUCCESS)) {

//                            Toast.makeText(SearchActivity.this, "검색", Toast.LENGTH_SHORT).show();

                    JsonArray jsonArray;

                    if(type.equals("user")) {
                        jsonArray = data.result.getAsJsonArray("users");

                    }
                    else if(type.equals("hashTag")) {
                        jsonArray = data.result.getAsJsonArray("hashTags");

                    }
                    else{
                        Log.i(TAG,"type : "+type);
                        return;
                    }

                        for(int i=0;i<jsonArray.size();i++){

                        JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();

                        User user = new User();
                            if(type.equals("user")){
                                user.type = User.USER;

                                user.name = jsonObject.get("name").getAsString();
                                user.profile = jsonObject.get("profile").getAsString();
                                user.introText = jsonObject.get("introText").getAsString();

                                user.isMyData = jsonObject.get("isMyData").getAsBoolean();
                                user.userId = jsonObject.get("userId").getAsInt();
                            }
                            else if(type.equals("hashTag")){
                                user.type = User.HASHTAG;
                                user.userId = jsonObject.get("userId").getAsInt();
                                user.name = jsonObject.get("name").getAsString();
                                user.count =  jsonObject.get("count").getAsInt();
                            }

                        itemList.add(user);
                    }


                    adapter.notifyDataSetChanged();


                }

                검색로딩바.setVisibility(View.GONE);
                하단로딩바.setVisibility(View.GONE);
                하단공간.setVisibility(View.GONE);
                isLoading = false;
            }

            @Override
            public void onResponseFail(ResponseBody ErrorBody) {
                Toast.makeText(SearchActivity.this, "onResponseFail", Toast.LENGTH_SHORT).show();
                Log.i(TAG, ErrorBody.toString());
            }

            @Override
            public void onNetworkError(Throwable t) {
                Toast.makeText(SearchActivity.this, "onNetworkError", Toast.LENGTH_SHORT).show();
                Log.i(TAG, t.toString());
            }
        });

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("searchText",searchText);
        jsonObject.addProperty("lastId",itemList.get(itemList.size()-1).userId);
        jsonObject.addProperty("type",type);

        networkManager.post2Request("search","search",jsonObject);

    }


}