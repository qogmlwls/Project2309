package com.example.project2309.ui;

import static com.esafirm.imagepicker.features.ImagePickerLauncherKt.createImagePickerIntent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ImagePickerConfig;
import com.esafirm.imagepicker.features.IpCons;
import com.esafirm.imagepicker.model.Image;
import com.example.project2309.adapter.ChatAdapter;
import com.example.project2309.MyApplication;
import com.example.project2309.R;
import com.example.project2309.data.Chat;
import com.example.project2309.data.Data;
import com.example.project2309.data.Post;
import com.example.project2309.network.NetworkManager;
import com.example.project2309.network.SharedPreferencesManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class ChattingActivity extends AppCompatActivity {

//    BackgroundSocketService.LocalBinder binder;

    private static final int REQUEST_CODE = 200,REQUEST_VIDEO_PICKER = 201;  // 어떤 상수든 사용 가능

    SharedPreferencesManager sharedPreferencesManager;

    private boolean isLoading = false;
    private int pastVisibleItems, visibleItemCount, totalItemCount;

    ImageButton 나가기버튼;
    NetworkManager networkManager;

    int roomId,fromPk;

    List<Chat> itemList;
    ChatAdapter adapter;
    RecyclerView recyclerView;
    String TAG = "ChattingActivity";

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.i(TAG, "--------------------------------------------------");

            Log.i(TAG, "BroadcastReceiver onReceive 실행");
            Log.i(TAG, "BackSocketService에게 메세지 받음.");
            String data = intent.getStringExtra("message");
            Log.i(TAG, "--------------------------------------------------");

            Log.i(TAG, "받은 메세지 "+ data);


            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(data);

                String type = jsonObject.getString("type");
                Log.i(TAG, "type : "+ type);

                if(type.equals("ReceiveChatting")){

                    String m_roomId = jsonObject.getString("roomId");
                    Log.i(TAG, "roomId : "+ m_roomId);
                    MyApplication application = (MyApplication) getApplicationContext();
                    application.setRoomId(Integer.parseInt(m_roomId));


                    if(roomId == Integer.parseInt(m_roomId)){

                        Chat chat = new Chat();

                        String chattingId = jsonObject.getString("chattingId");
                        Log.i(TAG, "chattingId : "+ chattingId);

                        String content,imagePath;
//                        content = jsonObject.getString("message");
                        String mediaType = jsonObject.getString("media_type");
                        chat.mediaType = mediaType;
                        Log.i(TAG, "mediaType : "+ mediaType);

                        if(Chat.TEXT.equals(mediaType)){
                            content = jsonObject.getString("message");

                            chat.message = content;
                            Log.i(TAG, "message : "+ content);

                        }
                        else if(Chat.IMAGE.equals(mediaType)){
                            imagePath = jsonObject.getString("imagePath");
//                            chat.mediaType = Chat.IMAGE;
                            chat.imagePath = imagePath;
                            Log.i(TAG, "imagePath : "+ imagePath);

                        }
                        else if(Chat.VIDEO.equals(mediaType)){
                            String ThumbnailPath = jsonObject.getString("Thumbnail");
                            String videoPath = jsonObject.getString("videoPath");

//                            chat.mediaType = Chat.IMAGE;
                            chat.ThumbnailPath = ThumbnailPath;
                            chat.videoPath = videoPath;

                            Log.i(TAG, "ThumbnailPath : "+ ThumbnailPath);
                            Log.i(TAG, "videoPath : "+ videoPath);


                        }



                        String name = jsonObject.getString("name");
                        String profile = jsonObject.getString("profile");
                        String date = jsonObject.getString("date");

                        // other chat 생성
                        Log.i(TAG, "name : "+ name);
                        Log.i(TAG, "profile : "+ profile);
                        Log.i(TAG, "date : "+ date);

    //                    chat.chattingId = jsonObject.get("chattingId").getAsInt();
                        chat.chattingId = Integer.parseInt(chattingId);
                        chat.profile = profile;
                        chat.name = name;
                        chat.date = date;
                        chat.UnReadChat = false;
                        chat.type = Chat.OTHERCHAT;

                        itemList.add(chat);
                        Log.i(TAG, "itemList.add");

                        adapter.notifyDataSetChanged();
                        Log.i(TAG, "notifyDataSetChanged().");


                        recyclerView.smoothScrollToPosition(itemList.size()-1);
                        Log.i(TAG, "date : "+ date);


                        application.SendReadChat(Integer.toString(roomId),Integer.toString(fromPk));


    //                        ReadChat(itemList.get(itemList.size()-1).chattingId);
                    }


    //                    String content = jsonObject.getString("message");
    //                    String roomId = jsonObject.getString("roomId");

                    Log.i(TAG, "--------------------------------------------------");

                }
                else if(type.equals("ReceiveReadChat")){

                    String m_roomId = jsonObject.getString("roomId");

                    if(roomId == Integer.parseInt(m_roomId)){
//                        String lastChattingId = jsonObject.getString("lastChattingId");
//    //                        for(int i=0;)
//                        // 읽음 처리하기.
//
//                        int i;
//    //                        int lastId;
//                        for(i=itemList.size()-1;i>=0;i--){
//                            Chat chat = itemList.get(i);
//                            if(chat.chattingId == Integer.parseInt(lastChattingId)){
//                                break;
//                            }
//                        }
//                        for(;i>=0;i--){
//                            Chat chat = itemList.get(i);
//                            chat.UnReadChat =  false;
//                        }

                        for(int i=itemList.size()-1;i>=0;i--){
                            Chat chat = itemList.get(i);
                            if(chat.type == Chat.MYCHAT){
                                chat.UnReadChat = false;
                            }

                        }

                        adapter.notifyDataSetChanged();
                    }



                }
                else if(type.equals("ResultSendChatting")){
                    Log.i(TAG, "--------------------------------------------------");

                    Log.i(TAG, "서버에게 채팅 전송 결과 받음.");

                    // 채팅 전송 결과
                    String result = jsonObject.getString("result");
//                    String requestId = jsonObject.getString("requestId");

                    if(result.equals("success")){
                        Log.i(TAG, "채팅 전송 성공.");

                        String date = jsonObject.getString("date");
                        Log.i(TAG, "date : "+date);

                        int requestId = Integer.parseInt(jsonObject.getString("requestId"));
                        int chattingId = Integer.parseInt(jsonObject.getString("chattingId"));
                        int m_roomId = Integer.parseInt(jsonObject.getString("roomId"));


                        Log.i(TAG, "requestId : "+Integer.toString(requestId));
                        Log.i(TAG, "requestId chattingId : "+Integer.toString(chattingId));
                        Log.i(TAG, "requestId room Id : "+Integer.toString(m_roomId));
                        Log.i(TAG, "roomId : "+Integer.toString(roomId));
                        MyApplication application = (MyApplication)getApplication();
                        application.setRoomId(roomId);
                        roomId = m_roomId;
//                        if(roomId == 0 ){
//                            roomId = m_roomId;
//                        }

                        for(int i=itemList.size()-1;i>=0;i--){
                            Chat chat = itemList.get(i);

                            if(chat.requestId == requestId){
                                chat.date = date;
                                chat.chattingId = chattingId;
                                chat.result = true;

                                Log.i(TAG, "채팅 날짜 서버가 준 날짜로 변경.");
                                Log.i(TAG, "변경 itemList index : "+Integer.toString(i));

                                if(chat.mediaType.equals(Chat.VIDEO)){
//                                    sharedPreferencesManager.savePk();

                                    String uri = getRealPathFromURI(context,chat.videoUri);
                                    Log.i(TAG,uri);
                                    sharedPreferencesManager.setVideo(uri,chattingId);

                                }

                                break;

                            }
                        }

                        adapter.notifyDataSetChanged();

//                        String chattingId = jsonObject.getString("chattingId");
//                        String message = jsonObject.getString("message");
//    //                        String result = jsonObject.getString("result");
//
//                        Chat chat = new Chat();
//                        chat.chattingId = Integer.parseInt(chattingId);
//    //                        chat.
//                        chat.type = Chat.MYCHAT;
//                        chat.message = message;
//                        chat.date = date;
//                        chat.UnReadChat = true;
//                        itemList.add(chat);
//
//                        adapter.notifyDataSetChanged();
//                        recyclerView.smoothScrollToPosition(itemList.size()-1);



                    }
                    Log.i(TAG, "--------------------------------------------------");

                }
//                else if(type.equals("ReceiveReadChat")){
//
//                }


            } catch (JSONException e) {
                throw new RuntimeException(e);
            }


            Log.i(TAG, "BroadcastReceiver onReceive 끝.");
            Log.i(TAG, "--------------------------------------------------");


        }
    };



    @Override
    public void onBackPressed() {

        Log.i(TAG, "--------------------------------------------------");
        Log.i(TAG, "onBackPressed() 시작.");

        // 메인 페이지를 시작하는 인텐트 생성
        Intent intent = new Intent(this, RoomActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 기존의 액티비티 스택을 비워줍니다.
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // 액티비티 시작
        startActivity(intent);
        Log.i(TAG, "RoomActivity 시작.");

        finish(); // 현재 액티비티를 종료

        Log.i(TAG, "onBackPressed() 끝.");
        Log.i(TAG, "--------------------------------------------------");

    }


    public void checkPermission(String[] permissions){

        Log.i(TAG, "--------------------------------------------------");
        Log.i(TAG, "checkPermission() 시작.");

        ArrayList<String> targetList = new ArrayList<String>();
        ActivityCompat.requestPermissions(this,permissions,101);

        Log.i(TAG, "checkPermission() 끝.");
        Log.i(TAG, "--------------------------------------------------");


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG, "--------------------------------------------------");

        Log.i(TAG,"onNewIntent");

        Log.i(TAG, "--------------------------------------------------");

    }

    public Bitmap createThumbnail(Context activity, String path){


        Log.i(TAG, "--------------------------------------------------");
        Log.i(TAG, "createThumbnail() 시작.");


        MediaMetadataRetriever retriever = null;
        Bitmap bitmap = null;

        try{
            retriever = new MediaMetadataRetriever();
            retriever.setDataSource(activity,Uri.parse(path));
            bitmap = retriever.getFrameAtTime(1000000,MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(retriever != null) {
                try {
                    retriever.release();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        Log.i(TAG, "createThumbnail() 끝.");
        Log.i(TAG, "--------------------------------------------------");


        if( bitmap == null ){
            Log.i(TAG, "bitmap is null.");
        }

        return  bitmap;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        Log.i(TAG, "--------------------------------------------------");
        Log.i(TAG, "onCreate() 시작.");

        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        checkPermission(permissions);

        int id = getIntent().getIntExtra("ID",-1);
//        Toast.makeText(this, "Noti Id : " +  Integer.toString(id), Toast.LENGTH_SHORT).show();

//        if(id != -1){
//            Toast.makeText(this, "Noti Id : " +  Integer.toString(id), Toast.LENGTH_SHORT).show();
//        }
        Log.i("ChattingActivity onCreate ","ID : "+Integer.toString(getIntent().getIntExtra("ID",-1)));
        if( getIntent().hasExtra("ID") && id==-1)
            Log.i("ChattingActivity onCreate ","String ID : "+getIntent().getStringExtra("ID"));


        roomId = getIntent().getIntExtra("roomId",-1);
        Log.i("ChattingActivity onCreate ","roomId : "+Integer.toString(getIntent().getIntExtra("roomId",-1)));

        Log.i("ChattingActivity onCreate ","roomId : "+Integer.toString(roomId));
//        if(roomId == -1){
//            Toast.makeText(this, "roomId 문제 있음.", Toast.LENGTH_SHORT).show();
//        }


//        TextView textView = findViewById(R.id.textView59);
//        textView.setText(Integer.toString(roomId));

        fromPk = getIntent().getIntExtra("fromPk",-1);
        Log.i("ChattingActivity onCreate ","fromPk : "+Integer.toString(getIntent().getIntExtra("fromPk",-1)));
        Log.i("ChattingActivity onCreate ","fromPk : "+Integer.toString(fromPk));

        if(fromPk == -1){

            Toast.makeText(this, "fromPk 문제 있음.", Toast.LENGTH_SHORT).show();
//            Log.i("ChattingActivity onCreate ","fromPk : "+getIntent().getStringExtra("fromPk"));

        }


        EditText editText;
        editText = findViewById(R.id.editTextText9);


        // 전송
        Button button;
        button = findViewById(R.id.button40);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "--------------------------------------------------");
                
                
                String 전송msg = editText.getText().toString();


                if(전송msg.length() == 0){

                    Toast.makeText(ChattingActivity.this, "메세지를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;

                }

                if(roomId == 0){

                    Log.i(TAG, "방이 없다. 서버에게 채팅방 생성 요청.");

                    // 방이 없었다.
                    // 서버에게 생성 요청.
                    networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
                        @Override
                        public void onResponseSuccess(Data data) {

                            if (data.result.get("result").getAsString().equals(Data.SUCCESS)) {
                                Log.i(TAG, "채팅방 생성 성공");

                                roomId = data.result.get("roomId").getAsInt();

//                                JsonArray array = new JsonArray();
                                int requestId = chatRequestId++;
                                JSONArray array1 = new JSONArray();
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("message",editText.getText().toString());
                                    jsonObject.put("requestId",requestId);

                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }

                                array1.put(jsonObject);
//                                array.add(jsonObject);



//                                int requestId = chatRequestId++;
                                MyApplication application = (MyApplication) getApplicationContext();
                                boolean sendResult = application.SendChatting(Integer.toString(roomId), Integer.toString(requestId),Integer.toString(fromPk),Chat.TEXT,jsonObject.toString());



                                Chat chat = new Chat();
//                                chat.chattingId = Integer.parseInt(chattingId);
                                //                        chat.
                                chat.type = Chat.MYCHAT;
                                chat.mediaType = Chat.TEXT;
                                chat.message =  editText.getText().toString();
                                chat.date = getTime();
                                chat.UnReadChat = true;
                                chat.requestId = requestId;
                                chat.result = sendResult;
                                if(!sendResult){
                                    chat.result = false;
                                }


                                itemList.add(chat);

                                adapter.notifyDataSetChanged();
                                recyclerView.smoothScrollToPosition(itemList.size()-1);


                                // 초기화
                                editText.setText("");
                            }

                        }

                        @Override
                        public void onResponseFail(ResponseBody ErrorBody) {
                            Toast.makeText(ChattingActivity.this, "onResponseFail", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNetworkError(Throwable t) {
                            Toast.makeText(ChattingActivity.this, "onNetworkError", Toast.LENGTH_SHORT).show();

                        }
                    });

                    JsonObject jsonObject = new JsonObject();

                    jsonObject.addProperty("toPk",fromPk);

                    networkManager.post2Request("room","create",jsonObject);
                    Log.i(TAG, "방이 없다. 서버에게 채팅방 생성 요청.");

                    Log.i(TAG, "--------------------------------------------------");


                }
                else{

                    String strRoomId = Integer.toString(roomId);
                    String strContent = editText.getText().toString();
                    String strFromPk = Integer.toString(fromPk);
                    String strType = Chat.TEXT;
                    int requestId = chatRequestId++;

                    JSONArray array = new JSONArray();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("message",strContent);
                        jsonObject.put("requestId",requestId);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    array.put(jsonObject);
                    Log.i(TAG, "MyApplication에게 서버에게 채팅 보내기 요청.");
                    Log.i(TAG, "전송 메세지");
                    Log.i(TAG, strRoomId);
                    Log.i(TAG, strContent);
                    Log.i(TAG, strFromPk);
                    Log.i(TAG, strType);
                    Log.i(TAG, jsonObject.toString());
                    Log.i(TAG,"requestId : "+Integer.toString(requestId));



                    MyApplication application = (MyApplication) getApplicationContext();
                    boolean sendResult = application.SendChatting(strRoomId, Integer.toString(requestId),strFromPk,strType,jsonObject.toString());



                    Chat chat = new Chat();
//                                chat.chattingId = Integer.parseInt(chattingId);
                    //                        chat.
                    chat.type = Chat.MYCHAT;
                    chat.mediaType = Chat.TEXT;
                    chat.message = strContent;
                    chat.date = getTime();
                    chat.UnReadChat = true;
                    chat.requestId = requestId;
                    chat.result = sendResult;
                    if(!sendResult){
                        chat.result = false;
                    }

                    itemList.add(chat);
                    Log.i(TAG, "itemList.add ");

                    adapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(itemList.size()-1);

                    // 초기화
                    editText.setText("");
                    Log.i(TAG, "--------------------------------------------------");

                }


            }
        });

        sharedPreferencesManager = new SharedPreferencesManager(getApplicationContext());
        recyclerView = findViewById(R.id.recycler_view2);

        // 데이터 준비
        itemList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ChatAdapter(itemList,this);
        recyclerView.setAdapter(adapter);
        networkManager = new NetworkManager(getApplicationContext());


        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter("1"));


        나가기버튼 = findViewById(R.id.imageButton15);
        나가기버튼.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


//                notifyDetection();
//                showNotification("제목","내용",1,2);
                showOptionsDialog();


            }
        });



        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                visibleItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                if (dy < 0) {
                    if (!isLoading && pastVisibleItems == 0) {
                        // 위로 스크롤하고 첫 번째 아이템이 보이는 경우
//                            Toast.makeText(ChattingActivity.this, "getChats", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "리사이클러뷰 최상단 아이템 보임.");
                        getChats(false);
                    }
                }
            }
        });

        if(roomId>0){
            getChats(true);


        }


        click = true;

        ImageButton 이미지전송;
        이미지전송 = findViewById(R.id.imageButton16);

        이미지전송.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(click){
                    click = false;
                    // 갤러리에서 이미지 가져오기.
                    ImagePickerConfig imagePickerConfig = launchImagePicker();

                    Intent intent = createImagePickerIntent(ChattingActivity.this, imagePickerConfig);
                    startActivityForResult(intent, IpCons.RC_IMAGE_PICKER);
                    Log.i(TAG, "이미지 가져오기 시작.");

                }


//                Toast.makeText(MainActivity.this, "click", Toast.LENGTH_SHORT).show();
//
//
//                Intent intent = new Intent(ChattingActivity.this, DialogSendActivity.class);
//
//                startActivity(intent);

            }
        });

        ImageButton 동영상전송;
        동영상전송 = findViewById(R.id.imageButton18);

        동영상전송.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(click){

                    click = false;
                    // 동영상 선택 Intent를 만듭니다.
                    Intent videoPickerIntent = new Intent(Intent.ACTION_PICK);
                    videoPickerIntent.setType("video/*");

// startActivityForResult를 사용하여 동영상 선택 액티비티를 시작합니다.
                    startActivityForResult(videoPickerIntent, REQUEST_VIDEO_PICKER);
                    Log.i(TAG, "동영상 가져오기 시작.");

                }

            }
        });


        Log.i(TAG, "onCreate() 끝.");
        Log.i(TAG, "--------------------------------------------------");


    }

    Boolean click;

    private ImagePickerConfig launchImagePicker() {

        Log.i(TAG, "--------------------------------------------------");
        Log.i(TAG, "launchImagePicker() 시작");

//        registerImagePicker
        ImagePickerConfig imagePickerConfig = new ImagePickerConfig();

        imagePickerConfig.setLimit(5);

        if(imagePickerConfig == null){
            Log.i(TAG, "ImagePickerConfig is NULL.");
        }

        Log.i(TAG, "launchImagePicker() 끝");
        Log.i(TAG, "--------------------------------------------------");

        return imagePickerConfig;

    }

    public void getChats(boolean state){

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


                    Log.i(TAG, "채팅 가져오기 성공.");
                    List<Chat> ChatList = getChatList(data.result.getAsJsonArray("chats"));
                    Log.i(TAG, "가져온 채팅 갯수 : "+Integer.toString(ChatList.size()));


                    if(ChatList.size() == 0&&itemList.size()!=0){

                        itemList.get(0).isLastChat = true;
                        Log.i(TAG, "더이상 채팅 없음.");
                        adapter.notifyDataSetChanged();
                    }


                    for (int i = 0; i < ChatList.size(); i++) {

                        Chat chat = ChatList.get(i);
                        Log.i(TAG, "chattingId : " + chat.chattingId);
                        Log.i(TAG, "name : " + chat.name);
                        Log.i(TAG, "data : " + chat.date);
                        Log.i(TAG, "mediaType : "+chat.mediaType);
//                        Log.i(TAG, "mediaType : "+chat.);


                        itemList.add(0,chat);

                        adapter.notifyItemInserted(0);
                    }


//                    adapter.notifyDataSetChanged();

                    if(state && itemList.size() >0)
                        recyclerView.smoothScrollToPosition(itemList.size()-1);

                    isLoading = false;

                }
                else if(data.result.get("result").getAsString().equals(Data.Fail)){

                    String reasonCode = data.result.get("reasonCode").getAsString();
                    if(reasonCode.equals("505")){
                        Toast.makeText(ChattingActivity.this, "세션 만료. 다시 로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ChattingActivity.this,LoginActivity.class);
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
                Toast.makeText(ChattingActivity.this, "onResponseFail", Toast.LENGTH_SHORT).show();
                Log.i("e", ErrorBody.toString());
            }

            @Override
            public void onNetworkError(Throwable t) {
                Toast.makeText(ChattingActivity.this, "onNetworkError", Toast.LENGTH_SHORT).show();
                Log.i("err", t.toString());
            }


        });


//        manager.GETRequest("comment","getinfo",post_id,last_comment_id);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("roomId",roomId);
        Log.i(TAG, "서버에게 전송할 Body : ");
        Log.i(TAG, "roomId : "+jsonObject.get("roomId").getAsString());

        if(itemList.size()!=0){
            jsonObject.addProperty("lastChattingId",itemList.get(0).chattingId);
            Log.i(TAG, "lastChattingId : "+jsonObject.get("lastChattingId").getAsString());

        }

        networkManager.GET2Request("chat", "getinfo", jsonObject.toString());
        Log.i(TAG, "서버에게 채팅 데이터 요청하기.");
        Log.i(TAG, "--------------------------------------------------");

    }

    // 게시글 수정, 삭제 선택하는 대화창
    private void showOptionsDialog() {

        Log.i(TAG, "--------------------------------------------------");

        String 채팅알림메세지 = "채팅 알림 ";

        boolean state = sharedPreferencesManager.getRoomNotiOn(roomId);
        if(state){
            채팅알림메세지 += "끄기";
        }
        else{
            채팅알림메세지 += "켜기";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(ChattingActivity.this);
        builder.setTitle("")
            .setItems(new CharSequence[]{채팅알림메세지, "채팅방 나가기"}, (dialog, which) -> {
                switch (which) {
                    case 0:
                        // 알림메세지.
                        sharedPreferencesManager.setRoomNoti(!state,roomId);
                        break;
                    case 1:
                        // 나가기
                        // 다이얼로그
                        showOptionsRoomOutDialog();
//                        roomOut(roomId);
                        break;
                }
            });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Log.i(TAG, "--------------------------------------------------");

    }

    private void showOptionsRoomOutDialog() {

        Log.i(TAG, "--------------------------------------------------");

        AlertDialog.Builder builder = new AlertDialog.Builder(ChattingActivity.this);
        builder.setTitle("채팅방 나가기")
                .setMessage("나가기를 하면 대화내용이 모두 삭제되고 채팅목록에서도 삭제됩니다.");
//                .setItems(new CharSequence[]{"취소", "나가기"}, (dialog, which) -> {
//                    switch (which) {
//                        case 0:
//                            // 알림메세지.
//                            break;
//                        case 1:
//                            // 나가기
//                            // 다이얼로그
//                            roomOut(roomId);
//                            break;
//                    }
//                });

        builder.setNegativeButton("나가기", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                roomOut(roomId);
            }
        });

        builder.setNeutralButton("취소", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Log.i(TAG, "--------------------------------------------------");

    }
    @Override
    protected void onResume() {
        super.onResume();


        Log.i(TAG, "--------------------------------------------------");
        Log.i(TAG, "onResume");

        Log.i(TAG, "setRoomId "+Integer.toString(roomId)+"로 설정.");

        MyApplication application = (MyApplication) getApplicationContext();
        application.setRoomId(roomId);

        application.SendReadChat(Integer.toString(roomId),Integer.toString(fromPk));

        Log.i(TAG, "--------------------------------------------------");

    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.i(TAG, "--------------------------------------------------");
        Log.i(TAG, "onPause");
        Log.i(TAG, "setRoomId -1로 설정.");

        MyApplication application = (MyApplication) getApplicationContext();
        application.setRoomId(-1);

        Log.i(TAG, "--------------------------------------------------");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i(TAG, "--------------------------------------------------");
        Log.i(TAG, "onDestroy");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        Log.i(TAG, "--------------------------------------------------");

    }

    List<Chat> getChatList(JsonArray array) {

        Log.i(TAG, "--------------------------------------------------");
        Log.i(TAG, "getChatList() 시작.");
        Log.i(TAG, "JSONArray에서 JSONObject들 가져오기 시작.");

        List<Chat> list;
        list = new ArrayList<>();

        Log.i(TAG, "array.size() : " + Integer.toString(array.size()));


        for (int i = 0; i < array.size(); i++) {

            JsonObject jsonObject = array.get(i).getAsJsonObject();

            Log.i(TAG, "Chat data." );

            Chat chat = new Chat();

            chat.chattingId = jsonObject.get("chattingId").getAsInt();

            chat.profile = jsonObject.get("profile").getAsString();
            chat.name = jsonObject.get("name").getAsString();


            chat.mediaType = jsonObject.get("media_type").getAsString();

            Log.i(TAG, "chattingId : " + Integer.toString(chat.chattingId));
            Log.i(TAG, "profile : " + chat.profile);
            Log.i(TAG, "name : " + chat.name);
            Log.i(TAG, "mediaType : " + chat.mediaType);


            if(chat.mediaType.equals(Chat.VIDEO)){

               Uri uri = sharedPreferencesManager.getVideo(chat.chattingId);
                if(uri != null){
                    chat.videoUri = uri;
                }
            }

            if(Chat.TEXT.equals(chat.mediaType)){
                chat.message = jsonObject.get("message").getAsString();
                Log.i(TAG, "message : " + chat.message);
            }
            else if(Chat.IMAGE.equals(chat.mediaType)){
                chat.imagePath = jsonObject.get("image").getAsString();
                Log.i(TAG, "imagePath : " + chat.imagePath);
            }
            else if(Chat.VIDEO.equals(chat.mediaType)){

                chat.ThumbnailPath = jsonObject.get("thumbnail").getAsString();
                chat.videoPath = jsonObject.get("video").getAsString();
                Log.i(TAG, "ThumbnailPath : " + chat.ThumbnailPath);
                Log.i(TAG, "videoPath : " + chat.videoPath);
            }


            chat.date = jsonObject.get("date").getAsString();

            chat.UnReadChat = jsonObject.get("UnReadChat").getAsBoolean();
            Boolean isMyChat = jsonObject.get("isMyChat").getAsBoolean();

            if(isMyChat)
                chat.type = Chat.MYCHAT;
            else
                chat.type = Chat.OTHERCHAT;

            Log.i(TAG, "date : " + chat.date );
            Log.i(TAG, "UnReadChat : " + Boolean.toString(chat.UnReadChat));
            Log.i(TAG, "isMyChat : " + Boolean.toString(isMyChat));


            list.add(chat);

        }


        Log.i(TAG, "getChatList() 끝.");
        Log.i(TAG, "--------------------------------------------------");

        return list;

    }

    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String getTime(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }



    public void roomOut(int roomId){

        Toast.makeText(this, "roomOut", Toast.LENGTH_SHORT).show();
        if(itemList.size()==0 || roomId == 0){
            finish();
            return;
        }
        networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
            @Override
            public void onResponseSuccess(Data data) {

                Log.i("ChattingActivity", data.result.toString());


                if (data.result.get("result").getAsString().equals(Data.SUCCESS)) {

//                    Log.i("ChattingActivity", data.result.toString());

                    // 방 삭제된거 이전 엑티비티에 반영되어야 함.
                    finish();

                }

            }

            @Override
            public void onResponseFail(ResponseBody ErrorBody) {
                Toast.makeText(ChattingActivity.this, "onResponseFail", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNetworkError(Throwable t) {
                Toast.makeText(ChattingActivity.this, "onNetworkError", Toast.LENGTH_SHORT).show();

            }
        });

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("roomId",roomId);
        jsonObject.addProperty("lastChattingId",itemList.get(itemList.size()-1).chattingId);

        networkManager.post2Request("room","delete",jsonObject);

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG, "--------------------------------------------------");


        if(requestCode == IpCons.RC_IMAGE_PICKER ||requestCode == REQUEST_VIDEO_PICKER){
            click = true;
        }

        if (requestCode == IpCons.RC_IMAGE_PICKER && resultCode == RESULT_OK && data != null) {

            Log.i(TAG, "이미지 가져옴.");

            // Uri 배열 가져오기.
            List<Image> images = ImagePicker.INSTANCE.getImages(data);

            Intent intent = new Intent(ChattingActivity.this, DialogSendActivity.class);

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

            Log.i(TAG, "RC_IMAGE_PICKER 끝");
            Log.i(TAG, "--------------------------------------------------");

//            startActivity(intent);


            //                Intent intent = new Intent(ChattingActivity.this, DialogSendActivity.class);
//
//                startActivity(intent);


        }
        else if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
//
            List<Uri> m_list = new ArrayList<>();;

//            Intent intent = getIntent();
            if (data != null) {
                // Uri 배열을 받아옵니다.


                ArrayList<Uri> receivedUris = data.getParcelableArrayListExtra("uris");

//            Uri[] receivedUris = (Uri[]) intent.getParcelableArrayExtra("uris");

                if (receivedUris != null) {
                    // Uri 배열에서 Uri를 추출하여 사용합니다.
                    for (Uri uri : receivedUris) {
                        // TODO: Uri에 대한 작업 수행
//                    Bitmap bitmap = getBitmapFromUri(uri);
                        m_list.add(uri);

//                        String path = jsonArray.get(i).getAsString();
                        Chat chat = new Chat();
                        chat.type = Chat.MYCHAT;
                        chat.mediaType = Chat.IMAGE;
                        chat.imageUri = uri;
//                        chat.imagePath = path;
                        chat.date = getTime();
                        //                        chat.
                        itemList.add(chat);


                    }
                }
            }

            adapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(itemList.size()-1);
            imageUpload(m_list);

            Log.i(TAG, "이미지 전송 REQUEST_CODE 끝");

//            Toast.makeText(this, "이미지 전송 : "+Integer.toString(m_list.size()), Toast.LENGTH_SHORT).show();
            Log.i(TAG, "--------------------------------------------------");

        }
        else if(requestCode == REQUEST_VIDEO_PICKER && resultCode == RESULT_OK){

            Log.i(TAG, "비디오 가져옴");

            // 동영상 선택이 성공했을 때의 처리를 여기에 추가합니다.
            Uri selectedVideoUri = data.getData();
            String mimeType = getContentResolver().getType(selectedVideoUri);

            String fileName = getFilename(selectedVideoUri);
//                    getContentResolver().g

            Log.i(TAG, "Uri : "+selectedVideoUri.toString());
            Log.i(TAG, "mimeType : "+mimeType);
            Log.i(TAG, "fileName : "+fileName);
            Chat chat = new Chat();
            chat.type = Chat.MYCHAT;
            chat.mediaType = Chat.VIDEO;
            chat.videoUri = selectedVideoUri;



            chat.UnReadChat = true;
//            chat.requestId = chatRequestId;
            imageUpload(selectedVideoUri,mimeType,fileName,chat);

//                        String path = jsonArray.get(i).getAsString();

//                        chat.imagePath = path;
            chat.date = getTime();
            //                        chat.
            itemList.add(chat);


            adapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(itemList.size()-1);
            
            Log.i(TAG, "REQUEST_VIDEO_PICKER 끝");

            ////            // VideoView를 사용하여 동영상을 재생합니다.
//            동영상.setVideoURI(selectedVideoUri);
//
//
//            동영상.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    동영상.start();mediaController.show(0);
//
////                    동영상.
//                    동영상.pause();
////                    동영상.setMediaController(mediaController);
//                }
//
//            });
//
//            imageUpload(m_list);
//
//            Toast.makeText(this, "이미지 전송 : "+Integer.toString(m_list.size()), Toast.LENGTH_SHORT).show();
//
//
//






//            // VideoView를 사용하여 동영상을 재생합니다.
//            videoView.setVideoURI(videoUri);
//            videoView.start();

            // 선택한 동영상 Uri를 사용하여 필요한 작업을 수행합니다.
            // 예를 들어, 선택한 동영상을 업로드하거나 재생할 수 있습니다.
            Log.i(TAG, "--------------------------------------------------");

        }
        Log.i(TAG, "--------------------------------------------------");


//        else if(requestCode == REQUEST_CODE2 && resultCode == RESULT_OK){
//
//
//            int post_id = data.getIntExtra("post_id",-1);
//            String update_date = data.getStringExtra("update_date");
//            String comment = data.getStringExtra("comment");
//
//
////            Toast.makeText(this, update_date, Toast.LENGTH_SHORT).show();
//            List<String> post_image = new ArrayList<>();
//
//            ArrayList<String> receivedUris = data.getStringArrayListExtra("post_image");
//
////            Uri[] receivedUris = (Uri[]) intent.getParcelableArrayExtra("uris");
//
//            if (receivedUris != null) {
//                // Uri 배열에서 Uri를 추출하여 사용합니다.
//                for (String uri : receivedUris) {
//                    // TODO: Uri에 대한 작업 수행
//                    post_image.add(uri);
//
//
//                }
//            }
//
////            Toast.makeText(this, post_image.get(2), Toast.LENGTH_SHORT).show();
//
//
//
//
//            for(int i=0;i<itemList.size();i++){
//                if(itemList.get(i).post_id == post_id){
//
//                    Post post = itemList.get(i);
//
//                    post.image = post_image;
//                    post.comment = comment;
//                    post.update_date = update_date;
//
////                    adapter.notify();
//                    adapter.notifyDataSetChanged();
////                    Toast.makeText(this, "수정 완료.", Toast.LENGTH_SHORT).show();
//
//
//                }
//            }
//
//
//
////
//
//
//        }
//        else if(requestCode == CommentActivityCode && resultCode == RESULT_OK){
//
//
//            int post_id = data.getIntExtra("post_id",-1);
//            int count = data.getIntExtra("Comment_count",-1);
//
//
//            for(int i =0;i<itemList.size();i++){
//                Post post = itemList.get(i);
//
//                if(post.post_id == post_id){
//
//                    post.comment_cnt = count;
//                }
//
//                adapter.notifyDataSetChanged();
//
//            }
////            Toast.makeText(this, Integer.toString(count), Toast.LENGTH_SHORT).show();
//
//
//
//        }
    }


    public String getFilename(Uri uri){
//                ...
        /*
         * Get the file's content URI from the incoming Intent,
         * then query the server app to get the file's display name
         * and size.
         */
//        Uri returnUri = returnIntent.getData();

        Log.i(TAG, "--------------------------------------------------");
        Log.i(TAG, "getFilename() 시작");
        Log.i(TAG, "Uri 에서 FileName 가져오기.");

        Cursor returnCursor =
                getContentResolver().query(uri, null, null, null, null);
        /*
         * Get the column indexes of the data in the Cursor,
         * move to the first row in the Cursor, get the data,
         * and display it.
         */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();


        String filename = returnCursor.getString(nameIndex);

        Log.i(TAG, "filename : "+filename);
        Log.i(TAG, "getFilename() 끝");
        Log.i(TAG, "--------------------------------------------------");

        return filename;

    }

    public void imageUpload(Uri uri,String mimeType,String Filename,Chat chat){

        Log.i(TAG, "--------------------------------------------------");
        Log.i(TAG, "imageUpload() (비디오) 시작.");

        Log.i(TAG,"mimeType : " + mimeType);
        Log.i(TAG,"Filename : " + Filename);

        List<MultipartBody.Part> imageParts = new ArrayList<>();
        Bitmap bitmap = createThumbnail(this,uri.toString());
        byte[] 이미지 = convertBitmapToByteArray(bitmap);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), 이미지);
        MultipartBody.Part body = MultipartBody.Part.createFormData("post_image"+Integer.toString(0),"Thumbnail", requestFile);

        imageParts.add(body);

        Log.i(TAG,"썸네일 이미지 add" );

        // body 생성
        try {

            InputStream is = getContentResolver().openInputStream(uri);

            if (is != null) {

                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                requestFile = RequestBody.create(MediaType.parse(mimeType), buffer);
                body = MultipartBody.Part.createFormData("post_image"+Integer.toString(1),Filename, requestFile);

                imageParts.add(body);

                Log.i(TAG,"비디오 add" );

//                    File targetFile = new File(getFilesDir()+"/"+"video.mp4");
//                    OutputStream outStream = new FileOutputStream(targetFile);
//                    outStream.write(buffer);
//
//                    Log.e("tag", "bitmap: " + "fileName");

            }

            is.close();

        } catch (IOException e) {
            e.printStackTrace();
        }



        RequestBody comment = RequestBody.create(MediaType.parse("text/plain"), "");
        RequestBody file_cnt = RequestBody.create(MediaType.parse("text/plain"),Integer.toString(imageParts.size()) );

        Log.i(TAG,"서버에 전송하는 file count : "+Integer.toString(imageParts.size()) );

        NetworkManager networkManager = new NetworkManager(getApplicationContext());


        networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
            @Override
            public void onResponseSuccess(Data data) {
                Log.i(TAG, "--------------------------------------------------");

//                Toast.makeText(ChattingActivity.this, "전송 완료", Toast.LENGTH_SHORT).show();

                if (data.result.get("result").getAsString().equals(Data.SUCCESS)) {
                    Log.i(TAG, "비디오 업로드 성공");

//                    JsonArray jsonArray = data.result.getAsJsonArray("file_path");
                    JsonObject jsonObject = data.result.getAsJsonObject("file_path");

                    String videoPath = jsonObject.get("videoPath").getAsString();
                    String Thumbnail = jsonObject.get("Thumbnail").getAsString();

                    Log.i(TAG, "videoPath : "+videoPath);
                    Log.i(TAG, "Thumbnail : "+Thumbnail);


                    for(int j=0;j<itemList.size();j++){
                        Chat chat = itemList.get(j);
                        if(chat.videoUri != null && chat.videoPath == null && chat.mediaType.equals(Chat.VIDEO)){
//                            chat.imageUri = null;
                            chat.videoPath = jsonObject.get("videoPath").getAsString();
                            chat.ThumbnailPath = jsonObject.get("Thumbnail").getAsString();
                            Log.i(TAG, "채팅 객체에 video data 할당");

                            break;
                        }
                    }
                    Log.i(TAG, "--------------------------------------------------");


                    MyApplication application = (MyApplication) getApplicationContext();

                    JsonObject jsonObject1 = new JsonObject();
                    jsonObject1.addProperty("Thumbnail",Thumbnail);
                    jsonObject1.addProperty("videoPath",videoPath);

                    String strRoomId = Integer.toString(roomId);
//                    String strMessage = "";
                    String strFromPk = Integer.toString(fromPk);
                    String strVideo = Chat.VIDEO;

                    Log.i(TAG, "MyApplication에게 채팅 서버에게 비디오 채팅 전송하는 것을 요청");
                    Log.i(TAG, "전송 메세지");
                    Log.i(TAG, "roomId : "+strRoomId);
//                    Log.i(TAG, "message : " + strMessage);
                    Log.i(TAG, "fromPk : " + strFromPk);
                    Log.i(TAG, "Media Type : " + strVideo);
                    Log.i(TAG, "Thumbnail : " + jsonObject1.get("Thumbnail").getAsString());
                    Log.i(TAG, "videoPath : " +  jsonObject1.get("videoPath").getAsString());


                    int requestId = chatRequestId++;
                    Log.i(TAG, "chatRequestId : " + Integer.toString(requestId));

                    boolean sendResult = application.SendChatting(strRoomId, Integer.toString(requestId),strFromPk,strVideo,jsonObject1.toString());
                    if(!sendResult){
                        chat.result = false;
                    }

                    chat.requestId = requestId;

                    chat.result = sendResult;
                }
                Log.i(TAG, "--------------------------------------------------");


            }

            @Override
            public void onResponseFail(ResponseBody ErrorBody) {
                Toast.makeText(ChattingActivity.this, "onResponseFail", Toast.LENGTH_SHORT).show();
                Log.i("e", ErrorBody.toString());
            }

            @Override
            public void onNetworkError(Throwable t) {
                Toast.makeText(ChattingActivity.this, "onNetworkError", Toast.LENGTH_SHORT).show();
                Log.i("err", t.toString());
            }
        });


        Log.i(TAG, "채팅 비디오 업로드 요청.");


//        chat
//        uoloadImage.php
        networkManager.postRequest("chat","uploadVideo",imageParts,comment,file_cnt);

//        Toast.makeText(this, "동영상 전송", Toast.LENGTH_SHORT).show();
//        networkManager.postRequest(imageParts,comment,file_cnt);


        Log.i(TAG, "imageUpload() (비디오) 끝.");
        Log.i(TAG, "--------------------------------------------------");


    }


    public  String getRealPathFromURI(Context context, Uri uri) {
        String[] projection = {MediaStore.Images.ImageColumns.DATA};
        ContentResolver contentResolver = context.getContentResolver();

        Cursor cursor = contentResolver.query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }
        return null;
    }
    int chatRequestId = 0;

    public void imageUpload(List<Uri> m_list){

        Log.i(TAG, "--------------------------------------------------");
        Log.i(TAG, "imageUpload() (이미지) 함수 시작.");
        Log.i(TAG, "웹 서버에 이미지들 업로드하기. ");

        if(m_list.size() == 0){
            Toast.makeText(ChattingActivity.this, "이미지를 하나 이상 넣어주세요.", Toast.LENGTH_SHORT).show();
            return;
//                    finish();
        }

        // body 생성
        List<MultipartBody.Part> imageParts = new ArrayList<>();

        for(int i=0;i<m_list.size();i++){

            Bitmap bitmap = getBitmapFromUri(m_list.get(i));
            byte[] 이미지 = convertBitmapToByteArray(bitmap);

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), 이미지);
            MultipartBody.Part body = MultipartBody.Part.createFormData("post_image"+Integer.toString(i),"image"+Integer.toString(i), requestFile);

            imageParts.add(body);

        }
        RequestBody comment = RequestBody.create(MediaType.parse("text/plain"), "");
        RequestBody file_cnt = RequestBody.create(MediaType.parse("text/plain"),Integer.toString(m_list.size()) );

        Log.i(TAG, "이미지 파일 업로드 갯수 : "+Integer.toString(m_list.size()));


        networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
            @Override
            public void onResponseSuccess(Data data) {

                Log.i(TAG, "--------------------------------------------------");


                if (data.result.get("result").getAsString().equals(Data.SUCCESS)) {

                    Log.i(TAG, "이미지 업로드 성공.");
                    Log.i(TAG, "응답 : "+data.result.toString());

                    JsonArray jsonArray = data.result.getAsJsonArray("file_path");

                    Log.i(TAG, "이미지 업로드한  파일 갯수 : "+Integer.toString(jsonArray.size()));



                    for(int i=0;i<jsonArray.size();i++){

                        String path = jsonArray.get(i).getAsString();
                        Log.i(TAG, "Image Path : "+path);

                        for(int j=0;j<itemList.size();j++){
                            Chat chat = itemList.get(j);
                            if(chat.imageUri != null){
                                chat.imageUri = null;
                                chat.imagePath = path;
                                chat.requestId = chatRequestId;
                                Log.i(TAG, "채팅 객체에게 Image Uri null로 바꾸고, 서버에게 받은 Path 값을 할당한다.");


                                JSONObject mediaData = new JSONObject();
                                try {
                                    mediaData.put("imagePath",path);
//                                    mediaData.put("requestId",chatRequestId++);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }

                                Log.i(TAG, "MyApplication에게 채팅 서버에게 이미지 채팅을 전송하는 것을 요청한다.");

                                MyApplication application = (MyApplication) getApplicationContext();
                                boolean sendResult = application.SendChatting(Integer.toString(roomId), Integer.toString(chatRequestId++), Integer.toString(fromPk), Chat.IMAGE, mediaData.toString());


                                chat.result = sendResult;
                                if(!sendResult){
                                    chat.result = false;
                                }




                                //                                try {
//                                    Log.i(TAG, "0.01초 쉰다.");
//
//                                    Thread.sleep(100);
//                                } catch (InterruptedException e) {
//                                    throw new RuntimeException(e);
//                                }


                                break;
                            }

//                            Log.i(TAG, "imageUri : "+);
                        }

//                        String path = jsonArray.get(i).getAsString();
//                    Log.i("ChattingActivity", data.result.toString());
//                        Chat chat = new Chat();
//                        chat.type = Chat.MYCHAT;
//                        chat.mediaType = Chat.IMAGE;
//    //                    chat.imageUri = uri;
//                        chat.imagePath = path;
//                        chat.date = "";
//    //                        chat.
//                        itemList.add(chat);

                        Log.i(TAG, "--------------------------------------------------");

                    }
//                    for(int i=0;i<jsonArray.size();i++) {
//
//
//                    }
//                    adapter.notifyDataSetChanged();
//                    recyclerView.smoothScrollToPosition(itemList.size()-1);
                }

                Log.i(TAG, "--------------------------------------------------");

            }

            @Override
            public void onResponseFail(ResponseBody ErrorBody) {
                Toast.makeText(ChattingActivity.this, "onResponseFail", Toast.LENGTH_SHORT).show();
                Log.i("e", ErrorBody.toString());
            }

            @Override
            public void onNetworkError(Throwable t) {
                Toast.makeText(ChattingActivity.this, "onNetworkError", Toast.LENGTH_SHORT).show();
                Log.i("err", t.toString());
            }
        });

        Log.i(TAG, "채팅 이미지 업로드 요청.");

//        chat
//        uoloadImage.php
        networkManager.postRequest("chat","uoloadImage",imageParts,comment,file_cnt);

        //        networkManager.postRequest(imageParts,comment,file_cnt);
        Log.i(TAG, "imageUpload() 함수 끝.");
        Log.i(TAG, "--------------------------------------------------");

    }
//    //Uri -> Path(파일경로)
//    public static String uri2path(Context context, Uri contentUri) {
//
//
//        String[] proj = { MediaStore.Images.Media.DATA };
//
//        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
//        cursor.moveToNext();
//        @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
////        Uri uri = Uri.fromFile(new File(path));
//
//        cursor.close();
//        return path;
//    }
//    private String getRealPathFromURI(Uri contentUri) {
//        if (contentUri.getPath().startsWith("/storage")) {
//            return contentUri.getPath();
//        }
//
//        String id = DocumentsContract.getDocumentId(contentUri).split(":")[1];
//        String[] columns = { MediaStore.Files.FileColumns.DATA };
//        String selection = MediaStore.Files.FileColumns._ID + " = " + id;
//        Cursor cursor = getContentResolver().query(MediaStore.Files.getContentUri("external"), columns, selection, null, null);
//        try {
//            int columnIndex = cursor.getColumnIndex(columns[0]);
//            if (cursor.moveToFirst()) {
//                return cursor.getString(columnIndex);
//            }
//        } finally {
//            cursor.close();
//        }
//        return null;
//    }
//    public byte[] getByte(){
//        String filePath = "파일 경로";
//        byte[] byteFile = null;
//        Uri uri = null;
//
//        new File(uri);
//
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                byteFile = Files.readAllBytes(new File(filePath).toPath());
//            }
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }
//        return byteFile;
//    }


    private Bitmap getBitmapFromUri(Uri uri) {

        Log.i(TAG, "--------------------------------------------------");
        Log.i(TAG, "getBitmapFromUri() 시작.");
        Log.i(TAG, "Uri를 Bitmap으로 변환하기 시작");
        Log.i(TAG, "Uri : "+uri.toString());

        Bitmap bitmap = null;

        try {

            InputStream imageStream = getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(imageStream);
            imageStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "getBitmapFromUri() 끝.");
        Log.i(TAG, "--------------------------------------------------");

        return bitmap;

    }


    private byte[] convertBitmapToByteArray(Bitmap bitmap) {

        Log.i(TAG, "--------------------------------------------------");
        Log.i(TAG, "convertBitmapToByteArray() 시작");
        Log.i(TAG, "bitmap을 btye로 변환하기 시작");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] bytes = byteArrayOutputStream.toByteArray();

        Log.i(TAG, "변환된 bytes.size : "+Integer.toString(bytes.length));
        Log.i(TAG, "convertBitmapToByteArray() 끝.");
        Log.i(TAG, "--------------------------------------------------");

        return bytes;

    }

}