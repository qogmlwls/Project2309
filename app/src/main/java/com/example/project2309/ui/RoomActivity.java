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
import android.widget.TextView;
import android.widget.Toast;

import com.example.project2309.R;
import com.example.project2309.adapter.RoomAdapter;
import com.example.project2309.data.Chat;
import com.example.project2309.data.Data;
import com.example.project2309.data.Post;
import com.example.project2309.data.Room;
import com.example.project2309.network.NetworkManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

public class RoomActivity extends AppCompatActivity {

    List<Room> itemList;
    RoomAdapter adapter;

    NetworkManager networkManager;

    Button 홈,마이페이지;

    String TAG = "RoomActivity";

    int totalCnt = 0;
    private boolean isLoading = false;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            Log.i(TAG, "onReceive 실행");
            String data = intent.getStringExtra("message");
            Log.i(TAG, "받은 데이터 "+ data);

//            Toast.makeText(context, "RoomActivity Broadcast", Toast.LENGTH_SHORT).show();

            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(data);

                String type = jsonObject.getString("type");

                if(type.equals("ReceiveChatting")){

//                    String content = jsonObject.getString("message");
                    String fromPk = jsonObject.getString("fromPk");
                    String roomId = jsonObject.getString("roomId");

                    String name = jsonObject.getString("name");
                    String profile = jsonObject.getString("profile");
                    String date = jsonObject.getString("date");
//                    String UnreadChatCnt = jsonObject.getString("UnreadChatCnt");


                    String content,imagePath;

                    String mediaType = jsonObject.getString("media_type");




                    // other chat 생성

                    Boolean newRoom = true;
                    // 변경 룸 아이디가 있는지 확인.
                    // 없으면 새로 만들기.
                    for(int i=0;i<itemList.size();i++){

                        Room room = itemList.get(i);
                        if(Integer.toString(room.room_id).equals(roomId)){

//                            room.fromPk = Integer.parseInt(fromPk);

                            if("text".equals(mediaType)){
                                content = jsonObject.getString("message");
                                room.last_chat_message = content;
//                                chat.mediaType = Chat.TEXT;
//                                chat.message = content;

                            }
                            else if("image".equals(mediaType)){
                                imagePath = jsonObject.getString("imagePath");
//                                chat.mediaType = Chat.IMAGE;
//                                chat.imagePath = imagePath;
                                room.last_chat_message = "사진을 보냈습니다.";

                            }
                            else if("video".equals(mediaType)){
//                            imagePath = jsonObject.getString("imagePath");
//                                chat.mediaType = Chat.IMAGE;
//                                chat.imagePath = imagePath;
                                room.last_chat_message = "동영상을 보냈습니다.";
                            }


//                            room.fromName = name;
//                            room.fromProfile = profile;
                            room.last_chat_date = date;
                            room.Unread_chat_cnt = room.Unread_chat_cnt+1;

                            newRoom = false;

// 인덱스 1의 요소를 제거
                            Boolean removeResult = itemList.remove(room);
// 새 위치(인덱스 3)에 추가
                            itemList.add(0, room);

                        }

                    }


                    if(newRoom){

                        Room room = new Room();

                        room.room_id = Integer.parseInt(roomId);
                        room.fromPk = Integer.parseInt(fromPk);


                        if("text".equals(mediaType)){
                            content = jsonObject.getString("message");
                            room.last_chat_message = content;
//                                chat.mediaType = Chat.TEXT;
//                                chat.message = content;
                            room.last_chat_message = content;
                        }
                        else if("image".equals(mediaType)){
                            imagePath = jsonObject.getString("imagePath");
//                                chat.mediaType = Chat.IMAGE;
//                                chat.imagePath = imagePath;
                            room.last_chat_message = "사진을 보냈습니다.";
                        }
                        else if("video".equals(mediaType)){
//                            imagePath = jsonObject.getString("imagePath");
//                                chat.mediaType = Chat.IMAGE;
//                                chat.imagePath = imagePath;
                            room.last_chat_message = "동영상을 보냈습니다.";
                        }

                        room.fromName = name;
                        room.fromProfile = profile;
                        room.last_chat_date = date;
                        room.Unread_chat_cnt = 1;

                        itemList.add(0,room);

                    }

                    adapter.notifyDataSetChanged();

//                    String content = jsonObject.getString("message");
//                    String roomId = jsonObject.getString("roomId");


                    totalCnt++;

                    TextView textView = findViewById(R.id.textView69);
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


            } catch (JSONException e) {
                throw new RuntimeException(e);
            }


        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        홈 = findViewById(R.id.button41);
        홈.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RoomActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        마이페이지 = findViewById(R.id.button44);
        마이페이지.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RoomActivity.this,MyPageActivity.class);
                startActivity(intent);
                finish();
            }
        });




        Button 검색;
        검색 = findViewById(R.id.button42);

        검색.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RoomActivity.this, SearchActivity.class);
                startActivity(intent);
                finish();
            }
        });


        RecyclerView recyclerView;
        recyclerView = findViewById(R.id.recyclerView);

        // 데이터 준비
        itemList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RoomAdapter(itemList,this);
        recyclerView.setAdapter(adapter);




        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter("1"));


        // 웹 서버에게 방 정보 요청하기.

//        public String room_id;
//        public String last_chat_message;
//        public String last_chat_writer_name;
//        public String last_chat_writer_profile;
//        public String last_chat_date;
//        public String Unread_chat_cnt;


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
                        getRooms();
                    }
                }
            }
        });
//        getRooms();
    }


    private void getRooms(){

        isLoading = true;
        networkManager = new NetworkManager(getApplicationContext());
        networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
            @Override
            public void onResponseSuccess(Data data) {

                if (data.result.get("result").getAsString().equals(Data.SUCCESS)) {


                    Log.i("log",data.result.toString());

                    List<Room> roomList = getRoomList(data.result.getAsJsonArray("rooms"));

                    for (int i = 0; i < roomList.size(); i++) {
                        itemList.add(roomList.get(i));
                    }

                    adapter.notifyDataSetChanged();

                    isLoading = false;
                }

            }

            @Override
            public void onResponseFail(ResponseBody ErrorBody) {
                Toast.makeText(RoomActivity.this, "onResponseFail", Toast.LENGTH_SHORT).show();
                Log.i("e", ErrorBody.toString());
            }

            @Override
            public void onNetworkError(Throwable t) {
                Toast.makeText(RoomActivity.this, "onNetworkError", Toast.LENGTH_SHORT).show();
                Log.i("err", t.toString());
            }


        });

//        manager.GETRequest("comment","getinfo",post_id,last_comment_id);
//        JsonObject jsonObject = new JsonObject();



//        networkManager.GET2Request("room", "getinfo", Integer.toString(itemList.get(itemList.size()-1).post_id));


        JsonObject jsonObject = new JsonObject();
        if (itemList.size() == 0) {

//            jsonObject.addProperty("post_id", post_id);
        } else {

//            Toast.makeText(this, Integer.toString(itemList.get(itemList.size()-1).comment_id), Toast.LENGTH_SHORT).show();

            jsonObject.addProperty("lastRoomId",itemList.get(itemList.size()-1).room_id);
            jsonObject.addProperty("lastCreateDate",itemList.get(itemList.size()-1).last_chat_date);


        }

        Log.i(TAG,jsonObject.toString());

        networkManager.GET2Request("room","getinfo",jsonObject.toString());
//        if(itemList.size() == 0)
//            networkManager.GET2Request("room","getinfo",jsonObject);
//        else
//            networkManager.GET2Request("room","getinfo",Integer.toString(itemList.get(itemList.size()-1).room_id));
//
//


    }

    @Override
    protected void onStart() {
        super.onStart();

        itemList.clear();
        getRooms();

        getUnReadChat();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);

    }

    List<Room> getRoomList(JsonArray array) {


        List<Room> list;
        list = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {

            JsonObject jsonObject = array.get(i).getAsJsonObject();

            Room room = new Room();

            room.room_id = jsonObject.get("room_id").getAsInt();
            room.fromPk = jsonObject.get("fromPk").getAsInt();

//            room.last_chat_media_type = jsonObject.get("last_chat_media_type").getAsString();
//
//
//            if("text".equals(room.last_chat_media_type)){
//
//
//            }
//            else if("image".equals(room.last_chat_media_type)){
//                room.last_chat_image_path = jsonObject
//            }
            room.last_chat_message = jsonObject.get("last_chat_message").getAsString();
            room.last_chat_message = jsonObject.get("last_chat_message").getAsString();
            room.fromName = jsonObject.get("fromName").getAsString();
            room.fromProfile = jsonObject.get("fromProfile").getAsString();
            room.last_chat_date = jsonObject.get("last_chat_date").getAsString();
            int cnt = jsonObject.get("Unread_chat_cnt").getAsInt();
            room.Unread_chat_cnt = cnt;

//            room.Unread_chat_cnt = Integer.toString(jsonObject.get("last_chat_date").getAsInt());

            list.add(room);

        }

        return list;

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


                    TextView textView = findViewById(R.id.textView69);
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
