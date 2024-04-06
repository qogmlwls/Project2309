package com.example.project2309.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project2309.MyApplication;
import com.example.project2309.adapter.CommentAdapter;
import com.example.project2309.R;
import com.example.project2309.common.Message;
import com.example.project2309.common.util;
import com.example.project2309.data.Comment;
import com.example.project2309.data.Data;
import com.example.project2309.network.NetworkManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

public class CommentActivity extends AppCompatActivity  implements CommentAdapter.OnItemClickListener {


    private RecyclerView recyclerView;

    private List<Comment> itemList;
    private CommentAdapter adapter;

    private ImageView 프로필이미지;

    NetworkManager networkManager;

    private boolean isLoading = false;

    int post_id,comment_cnt;

    NetworkManager.CallbackFunc<Data> 로그인이후내정보요청후콜백함수;
    NetworkManager.CallbackFunc<ResponseBody> 이미지받아오기;


    EditText 댓글에디터;

    Button 작성;


    // 답글 작성시 부모 댓글 id
    // 댓글 작성시 -1
    int WriteComment_id;

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);



        WriteComment_id = -1;


        TextView textView = findViewById(R.id.textView46);
        TextView textView1 = findViewById(R.id.textView47);
        ImageView imageView = findViewById(R.id.imageView10);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setVisibility(View.GONE);
                textView1.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
                WriteComment_id = -1;

            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        프로필이미지 = findViewById(R.id.imageView9);

        댓글에디터 = findViewById(R.id.editTextText7);
        작성 = findViewById(R.id.button35);


        post_id = getIntent().getIntExtra("post_id", -1);
        comment_cnt = getIntent().getIntExtra("comment_cnt", -1);

        ImageButton imageButton = findViewById(R.id.imageButton7);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // 리사이클러뷰 상단에 내가 작성한 글 보여지도록.
                Intent resultIntent = new Intent();

                resultIntent.putExtra("post_id",post_id);
                resultIntent.putExtra("Comment_count",comment_cnt);

                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });


        // 내 프로필 정보 요청하기.
        // 프로필 이미지 가져와서, 이미지 뷰에 넣기.

        networkManager = new NetworkManager(getApplicationContext());

        itemList = new ArrayList<>();


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommentAdapter(itemList, this, this);
        recyclerView.setAdapter(adapter);

//        networkManager = new NetworkManager(getApplicationContext());


        // 스크롤 리스너 설정
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        getComments();
                    }
                }
            }
        });


        getComments();


        작성.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(댓글에디터.getText().toString().equals("")){
                    Toast.makeText(CommentActivity.this, "댓글을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                NetworkManager manager = new NetworkManager(getApplicationContext());
                // 서버에 요청
                manager.setCallback(new NetworkManager.CallbackFunc<Data>() {
                    @Override
                    public void onResponseSuccess(Data data) {

//                        Toast(data.result.toString());

                        if (data.result.get("result").getAsString().equals(Data.SUCCESS)) {

                            JsonObject jsonObject = data.result.get("comment").getAsJsonObject();
//                            JsonObject jsonObject = data.result.get("comment").getAsJsonObject();

                            String type;

                            if(WriteComment_id == -1){

                                type = "Reply";
                                Comment comment = new Comment();

                                comment.user_id = jsonObject.get("user_id").getAsInt();
                                comment.comment_id = jsonObject.get("comment_id").getAsInt();
                                comment.create_date = jsonObject.get("create_date").getAsString();
                                comment.user_name = jsonObject.get("name").getAsString();
                                comment.user_profile = jsonObject.get("profile").getAsString();


//                comment.update_date = "수정날짜";
                                comment.isMyComment = true;
                                comment.content = 댓글에디터.getText().toString();
                                comment.Reply_comment_cnt = 0;

                                itemList.add(0, comment);

                                comment_cnt = comment_cnt + 1;

//                                Toast("Result WriteComment_id -1");
                            }
                            else{


                                type = "Rereply";
                                Comment comment1 = new Comment();

                                comment1.user_id = jsonObject.get("user_id").getAsInt();
                                comment1.comment_id = jsonObject.get("comment_id").getAsInt();
                                comment1.create_date = jsonObject.get("create_date").getAsString();
                                comment1.user_name = jsonObject.get("name").getAsString();
                                comment1.user_profile = jsonObject.get("profile").getAsString();


//                comment.update_date = "수정날짜";
                                comment1.isMyComment = true;
                                comment1.content = 댓글에디터.getText().toString();
                                comment1.Reply_comment_cnt = 0;

//                                comment1.content = 댓글에디터.getText().toString();



                                for(int i=0;i<itemList.size();i++){

                                    Comment comment = itemList.get(i);

                                    if(comment.comment_id == WriteComment_id){

                                        comment1.parent = comment.comment_id;

                                        comment.replies.add(0,comment1);

                                        comment.Reply_comment_cnt = comment.Reply_comment_cnt+1;

                                    }

                                }
//                                Toast("Result WriteComment_id 11111111");

                            }


                            textView.setVisibility(View.GONE);
                            textView1.setVisibility(View.GONE);
                            imageView.setVisibility(View.GONE);
                            WriteComment_id = -1;
                            댓글에디터.setText("");

                            adapter.notifyDataSetChanged();

                            // 리사이클러뷰 상단에 내가 작성한 글 보여지도록.
                            Intent resultIntent = new Intent();

                            resultIntent.putExtra("post_id",post_id);
                            resultIntent.putExtra("Comment_count",comment_cnt);

                            setResult(RESULT_OK, resultIntent);


                            JSONObject jsonObject1 = new JSONObject();
                            try {
                                if(type.equals("Reply"))
                                    jsonObject1.put("type","RequestReplyNoti");
                                else if(type.equals("Rereply"))
                                    jsonObject1.put("type","RequestRereplyNoti");


                                jsonObject1.put("postId",post_id);
                                jsonObject1.put("postUserName", jsonObject.get("name").getAsString());
                                jsonObject1.put("PostUserId",jsonObject.get("writeUserId").getAsInt());

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                            MyApplication application = (MyApplication)getApplication();
                            application.SendMessage(jsonObject1);


                        }
                    }

                    @Override
                    public void onResponseFail(ResponseBody ErrorBody) {


                        Toast("onResponseFail");
                    }

                    @Override
                    public void onNetworkError(Throwable t) {
                        Toast("onNetworkError");

                    }
                });


                if(WriteComment_id == -1){
                    JsonObject body = new JsonObject();
                    body.addProperty("post_id", post_id);
                    body.addProperty("content", 댓글에디터.getText().toString());


                    manager.post2Request("comment", "createComment", body);


//                    Toast("WriteComment_id == -1");
                }

                else{
                    JsonObject body = new JsonObject();
                    body.addProperty("post_id", post_id);
                    body.addProperty("content", 댓글에디터.getText().toString());
                    body.addProperty("parent", WriteComment_id);


                    manager.post2Request("reply", "create", body);
//                    Toast("WriteComment_id 1111");

                }





            }
        });

    }


    private void getComments() {


        isLoading = true;
        networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
            @Override
            public void onResponseSuccess(Data data) {

                if (data.result.get("result").getAsString().equals(Data.SUCCESS)) {

//                    Toast.makeText(CommentActivity.this, data.result.toString(), Toast.LENGTH_SHORT).show();

                    Log.i("log",data.result.toString());
                    List<Comment> commentList = getCommentList(data.result.getAsJsonArray("comments"));

                    for (int i = 0; i < commentList.size(); i++) {
                        itemList.add(commentList.get(i));
                    }

                    adapter.notifyDataSetChanged();
                    isLoading = false;


                }

            }

            @Override
            public void onResponseFail(ResponseBody ErrorBody) {
                Toast.makeText(CommentActivity.this, "onResponseFail", Toast.LENGTH_SHORT).show();
                Log.i("e", ErrorBody.toString());
            }

            @Override
            public void onNetworkError(Throwable t) {
                Toast.makeText(CommentActivity.this, "onNetworkError", Toast.LENGTH_SHORT).show();
                Log.i("err", t.toString());
            }


        });

//        manager.GETRequest("comment","getinfo",post_id,last_comment_id);
        JsonObject jsonObject = new JsonObject();

        if (itemList.size() == 0) {

            jsonObject.addProperty("post_id", post_id);
        } else {

//            Toast.makeText(this, Integer.toString(itemList.get(itemList.size()-1).comment_id), Toast.LENGTH_SHORT).show();

            jsonObject.addProperty("post_id", post_id);
            jsonObject.addProperty("last_comment_id", itemList.get(itemList.size() - 1).comment_id);


        }
        networkManager.GET2Request("comment", "getinfo", jsonObject.toString());


    }


    List<Comment> getCommentList(JsonArray array) {


        List<Comment> list;
        list = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {

            JsonObject jsonObject = array.get(i).getAsJsonObject();


            Comment comment = new Comment();


            comment.comment_id = jsonObject.get("comment_id").getAsInt();
            comment.user_id = jsonObject.get("user_id").getAsInt();


            comment.user_profile = jsonObject.get("profile").getAsString();
            comment.user_name = jsonObject.get("name").getAsString();
            comment.create_date = jsonObject.get("create_date").getAsString();

            if (jsonObject.has("update_date")) {
//                Toast.makeText(this, "update_date", Toast.LENGTH_SHORT).show();
                comment.update_date = jsonObject.get("update_date").getAsString();
            }
            comment.content = jsonObject.get("content").getAsString();



            if(jsonObject.has("Reply_comment_cnt"))
                comment.Reply_comment_cnt = jsonObject.get("Reply_comment_cnt").getAsInt();

            comment.isMyComment = jsonObject.get("isMyComment").getAsBoolean();



            list.add(comment);


        }

        return list;

    }


    String getData(JsonObject object, String data) {

        // Data.RESULT 값이 없으면 예외 처리
        if (object.has(data)) {
            // 여기에 결과 처리 코드 추가
            return object.get(data).getAsString();
        } else {
            // Data.RESULT가 없는 경우에 대한 예외 처리
            // 예: 로그를 남기거나, 적절한 조치를 취함
            Toast(Message.ResponseParsingException);
            return null;
        }

    }

    void Toast(String message) {
        Toast.makeText(CommentActivity.this, message, Toast.LENGTH_SHORT).show();

    }

    <T> void startActivity(Class<T> activityClass) {
        util.startMyActivity(CommentActivity.this, activityClass);
    }


    int Code = 102;

    @Override
    public void Edit(Comment item) {
//        Toast("Edit");

        Intent intent = new Intent(CommentActivity.this, UpdateCommentActivity.class);
        intent.putExtra("comment_id", item.comment_id);
        intent.putExtra("content", item.content);
        intent.putExtra("parent", item.parent);


        startActivityForResult(intent, Code);

    }

    @Override
    public void delete(Comment item) {
//        Toast("delete");


        networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
            @Override
            public void onResponseSuccess(Data data) {

//                (data.result.get("result").getAsString().equals(Data.SUCCESS)) {
                if(data.result.get("result").getAsString().equals(Data.SUCCESS)) {
                    if(item.parent == -1){
                        itemList.remove(item);
                        adapter.notifyDataSetChanged();

                        comment_cnt = comment_cnt - 1;
                    }
                    else{

                        for(int i =0;i<itemList.size();i++){


                            if(itemList.get(i).comment_id == item.parent){
                                itemList.get(i).replies.remove(item);
                                itemList.get(i).Reply_comment_cnt = itemList.get(i).Reply_comment_cnt - 1;
//                                itemList.get(i).
                                if(itemList.get(i).Reply_comment_cnt == 0){
                                    itemList.get(i).ReplyViewVisibility = false;
                                }
                                adapter.notifyDataSetChanged();

                            }

                        }


                    }


                    // 리사이클러뷰 상단에 내가 작성한 글 보여지도록.
                    Intent resultIntent = new Intent();

                    resultIntent.putExtra("post_id",post_id);
                    resultIntent.putExtra("Comment_count",comment_cnt);

                    setResult(RESULT_OK, resultIntent);
                }
            }

            @Override
            public void onResponseFail(ResponseBody ErrorBody) {
                Toast("onResponseFail");
            }

            @Override
            public void onNetworkError(Throwable t) {
                Toast("onNetworkError");

            }
        });


        JsonObject body = new JsonObject();
        body.addProperty("comment_id", item.comment_id);
//        body.addProperty("content", 댓글에디터.getText().toString());


        networkManager.post2Request("comment", "delete", body);


//        adapter.notifyItemRemoved();

    }

    @Override
    public void ReplySetVisibility(Comment comment) {
//        Toast("ReplySetVisibility");


        comment.ReplyViewVisibility = !comment.ReplyViewVisibility;
//        if(comment.)
        if(comment.Reply_comment_cnt!=0 && comment.replies.size() == 0){
            //서버에 요청
            networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
                @Override
                public void onResponseSuccess(Data data) {


//                    Toast(data.result.toString());
                    if (data.result.get("result").getAsString().equals(Data.SUCCESS)) {



                        List<Comment> commentList = getCommentList(data.result.getAsJsonArray("comments"));

                        for (int i = 0; i < commentList.size(); i++) {

                            commentList.get(i).parent = comment.comment_id;
                            comment.replies.add(commentList.get(i));

                        }



                        if(data.result.has("more_replyComment"))
                            comment.more_replyComment = data.result.get("more_replyComment").getAsBoolean();



                        adapter.notifyDataSetChanged();

                    }


                }

                @Override
                public void onResponseFail(ResponseBody ErrorBody) {
                    Toast("onResponseFail");

                }

                @Override
                public void onNetworkError(Throwable t) {
                    Toast("onNetworkError");

                }
            });

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("post_id", post_id);
            jsonObject.addProperty("parent_id", comment.comment_id);
//            jsonObject.addProperty("last_comment_id", itemList.get(itemList.size() - 1).comment_id);

            networkManager.GET2Request("reply","getinfo", jsonObject.toString());

        }

        else{
            adapter.notifyDataSetChanged();
        }



//                    if(답글.getVisibility() == View.VISIBLE){
//                        답글.setVisibility(View.GONE);
//                        ReplyViewVisibility = false;
//                        notifyDataSetChanged();
//                    }
//                    else{
//                        답글.setVisibility(View.VISIBLE);
//                        ReplyViewVisibility = true;
//                    }
    }

    @Override
    public void MoreReply(Comment comment) {
        // 댓글 더보기.

        networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
            @Override
            public void onResponseSuccess(Data data) {


//                Toast(data.result.toString());
                if (data.result.get("result").getAsString().equals(Data.SUCCESS)) {



                    List<Comment> commentList = getCommentList(data.result.getAsJsonArray("comments"));

                    for (int i = 0; i < commentList.size(); i++) {
                        commentList.get(i).parent = comment.comment_id;

                        comment.replies.add(commentList.get(i));
                    }


                    if(data.result.has("more_replyComment"))
                        comment.more_replyComment = data.result.get("more_replyComment").getAsBoolean();


                    adapter.notifyDataSetChanged();


                }


            }

            @Override
            public void onResponseFail(ResponseBody ErrorBody) {
                Toast("onResponseFail");

            }

            @Override
            public void onNetworkError(Throwable t) {
                Toast("onNetworkError");

            }
        });

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("post_id", post_id);
        jsonObject.addProperty("parent_id", comment.comment_id);

        if(comment.replies.size() != 0)
            jsonObject.addProperty("last_replyComment_id", comment.replies.get(comment.replies.size() - 1).comment_id);

        networkManager.GET2Request("reply","getinfo", jsonObject.toString());

    }



    @Override
    public void WriteReply(Comment comment) {


        TextView textView = findViewById(R.id.textView46);
        TextView textView1 = findViewById(R.id.textView47);
        ImageView imageView = findViewById(R.id.imageView10);

        textView.setVisibility(View.VISIBLE);
        textView1.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);

        textView.setText(comment.content);

        WriteComment_id = comment.comment_id;


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Code && resultCode == RESULT_OK && data != null) {


            int comment_id = data.getIntExtra("comment_id",-1);
            int parent = data.getIntExtra("parent",0);
            String content = data.getStringExtra("content");
            String update_date = data.getStringExtra("update_date");

            if(parent == -1){
                for (int i=0;i<itemList.size();i++){

                    Comment comment = itemList.get(i);
                    if(comment.comment_id == comment_id){


                        comment.update_date = update_date;
                        comment.content = content;

                    }

                }
            }
            else{

                for (int i=0;i<itemList.size();i++){

                    Comment comment = itemList.get(i);
                    if(comment.comment_id == parent){

                        for (int j=0;j<comment.replies.size();j++) {

                            Comment comment1 = comment.replies.get(j);
                            if(comment1.comment_id == comment_id) {

                                comment1.update_date = update_date;
                                comment1.content = content;
                            }
                        }


                    }

                }


            }





            adapter.notifyDataSetChanged();


        }

    }




}