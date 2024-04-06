package com.example.project2309.adapter;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project2309.MyApplication;
import com.example.project2309.R;
import com.example.project2309.data.Chat;
import com.example.project2309.data.Data;
import com.example.project2309.data.FollowUser;
import com.example.project2309.data.Like;
import com.example.project2309.data.Room;
import com.example.project2309.data.User;
import com.example.project2309.network.NetworkManager;
import com.example.project2309.ui.FollowListActivity;
import com.example.project2309.ui.MyPageActivity;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;

public class FollowListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    String TAG = "FollowListAdapter";
    List<FollowUser> itemList;

    Context context;

    int deleteCnt;

    public FollowListAdapter(List<FollowUser> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;

        deleteCnt = 0;
    }




    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == FollowUser.Follower){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.follower_item, parent, false);
            return new FollowListAdapter.Follower(view);
        }
        else if(viewType == FollowUser.Following){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.following_item, parent, false);

            return new FollowListAdapter.Following(view);
        }
        else{
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        FollowUser user = itemList.get(position);

        if(FollowUser.Follower == user.type){

            FollowListAdapter.Follower holder1 = (FollowListAdapter.Follower) holder;
            holder1.닉네임.setText(user.name);

            if(user.isMyFollowData){
                holder1.삭제.setVisibility(View.VISIBLE);
            }
            else{


//                if(user.)
//                holder1.삭제.setVisibility(View.GONE);
                if(user.isFollowing){
                    holder1.삭제.setText("팔로잉");
                    holder1.삭제.setBackgroundColor(Color.LTGRAY);
                    holder1.삭제.setTextColor(Color.BLACK);
                }
                else{
                    holder1.삭제.setText("팔로우");
                    holder1.삭제.setBackgroundColor(Color.parseColor("#FF6200EE"));
                    holder1.삭제.setTextColor(Color.WHITE);
                }
            }

            if(user.profile.length() != 0 ){
                String url = "http://49.247.30.164"+user.profile;
                Glide.with(context).load(url).into(holder1.프로필이미지);
            }
            else{
                Drawable drawable;
                drawable = context.getResources().getDrawable(R.drawable.baseline_person_24);
                holder1.프로필이미지.setImageDrawable(drawable);
            }

            if(user.isMe){
                holder1.삭제.setVisibility(View.GONE);
            }
            else{
                holder1.삭제.setVisibility(View.VISIBLE);
            }

        }
        else if(FollowUser.Following == user.type){
//            TextView 시간, 닉네임, 메세지, 안읽음;


            FollowListAdapter.Following holder1 = (FollowListAdapter.Following) holder;
            holder1.닉네임.setText(user.name);


            if(user.profile.length() != 0 ){
                String url = "http://49.247.30.164"+user.profile;
                Glide.with(context).load(url).into(holder1.프로필이미지);
            }
            else{
                Drawable drawable;
                drawable = context.getResources().getDrawable(R.drawable.baseline_person_24);
                holder1.프로필이미지.setImageDrawable(drawable);
            }


            if(user.isFollowing){
                holder1.팔로우.setText("팔로잉");
                holder1.팔로우.setBackgroundColor(Color.LTGRAY);
                holder1.팔로우.setTextColor(Color.BLACK);
            }
            else{
                holder1.팔로우.setText("팔로우");
                holder1.팔로우.setBackgroundColor(Color.parseColor("#FF6200EE"));
                holder1.팔로우.setTextColor(Color.WHITE);
            }


            if(user.isMe){
                holder1.팔로우.setVisibility(View.GONE);
            }
            else{
                holder1.팔로우.setVisibility(View.VISIBLE);
            }

        }
        else {




        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
    @Override
    public int getItemViewType(int position) {
        return itemList.get(position).type;
    }

    public class Following extends RecyclerView.ViewHolder {
        ImageView 프로필이미지;
        TextView 닉네임;
        // , 안읽음;
        Button 팔로우;


        public Following(View view) {
            super(view);

            프로필이미지 = view.findViewById(R.id.imageView12);
            닉네임 = view.findViewById(R.id.textView51);

            팔로우 = view.findViewById(R.id.button55);


            팔로우.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    FollowUser user = itemList.get(position);

                    NetworkManager networkManager = new NetworkManager(context.getApplicationContext());
                    networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
                        @Override
                        public void onResponseSuccess(Data data) {
                            Log.i(TAG,data.result.toString());

                            if (data.result.get("result").getAsString().equals(Data.SUCCESS)) {

                                JsonObject jsonObject = data.result.get("isFollowMe").getAsJsonObject();

                                Boolean state = jsonObject.get("state").getAsBoolean();
                                user.isFollowing = state;

                                notifyDataSetChanged();

                                if(!state){

                                    FollowListActivity activity = (FollowListActivity)context;
                                    activity.setDeleteTotalCnt();


                                }
                                else{
                                    FollowListActivity activity = (FollowListActivity)context;
                                    activity.setInsertTotalCnt();




                                    JSONObject jsonObject1 = new JSONObject();
                                    try {
                                        jsonObject1.put("type","RequestFollowNoti");
                                        jsonObject1.put("FollowUserId",user.userId);
                                        jsonObject1.put("UserName",data.result.get("myName").getAsString());

                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }

//                                     MyApplication
                                    MyApplication application = (MyApplication)activity.getApplication();
                                    application.SendMessage(jsonObject1);
                                }
                            }
                        }

                        @Override
                        public void onResponseFail(ResponseBody ErrorBody) {
                            Toast.makeText(context, "onResponseFail", Toast.LENGTH_SHORT).show();
                            Log.i("e", ErrorBody.toString());
                        }

                        @Override
                        public void onNetworkError(Throwable t) {
                            Toast.makeText(context, "onNetworkError", Toast.LENGTH_SHORT).show();
                            Log.i("err", t.toString());
                        }
                    });


                    JsonObject jsonObject = new JsonObject();

                    jsonObject.addProperty("pk",user.userId);
//                    jsonObject.addProperty("followId",like.userId);

                    networkManager.post2Request("follow","setFollowing",jsonObject);


                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = getAdapterPosition();
                    FollowUser user = itemList.get(position);

                    Intent intent = new Intent(context, MyPageActivity.class);

                    intent.putExtra("IsMyPage",user.isMe);
                    intent.putExtra("UserPk",user.userId);
                    context.startActivity(intent);


                }
            });


        }
    }
    public class Follower extends RecyclerView.ViewHolder {

        ImageView 프로필이미지;
        TextView 닉네임;

        Button 삭제;


        public Follower(View view) {
            super(view);

            프로필이미지 = view.findViewById(R.id.imageView13);
            닉네임 = view.findViewById(R.id.textView52);

            // 삭제는 나의 팔로워를 삭제.
            // 나를 Follow하는 유저 와 Follow 끊기.

            삭제 = view.findViewById(R.id.button47);

            if(itemList.size() != 0 && itemList.get(0).isMyFollowData){
                삭제.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        int position = getAdapterPosition();
                        FollowUser user = itemList.get(position);

                        NetworkManager networkManager = new NetworkManager(context.getApplicationContext());
                        networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
                            @Override
                            public void onResponseSuccess(Data data) {
                                Log.i("FollowListAdapter",data.result.toString());
                                if (data.result.get("result").getAsString().equals(Data.SUCCESS)) {

                                    itemList.remove(user);
//                                no
                                    notifyDataSetChanged();

                                    FollowListActivity activity = (FollowListActivity)context;
                                    activity.setDeleteTotalCnt();
                                    deleteCnt++;

                                    Intent resultIntent = new Intent();
                                    resultIntent.putExtra("deleteCnt",deleteCnt);

                                    activity.setResult(RESULT_OK,resultIntent);

                                }
                            }

                            @Override
                            public void onResponseFail(ResponseBody ErrorBody) {
                                Toast.makeText(context, "onResponseFail", Toast.LENGTH_SHORT).show();
                                Log.i("e", ErrorBody.toString());
                            }

                            @Override
                            public void onNetworkError(Throwable t) {
                                Toast.makeText(context, "onNetworkError", Toast.LENGTH_SHORT).show();
                                Log.i("err", t.toString());
                            }
                        });


                        JsonObject jsonObject = new JsonObject();

                        jsonObject.addProperty("followId",user.followId);

                        networkManager.post2Request("follow","deleteFollow",jsonObject);


                    }
                });


            }
            else if(itemList.size() != 0 && itemList.get(0).isMyFollowData == false){
                삭제.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        FollowUser user = itemList.get(position);

                        NetworkManager networkManager = new NetworkManager(context.getApplicationContext());
                        networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
                            @Override
                            public void onResponseSuccess(Data data) {
                                Log.i(TAG,data.result.toString());

                                if (data.result.get("result").getAsString().equals(Data.SUCCESS)) {

                                    JsonObject jsonObject = data.result.get("isFollowMe").getAsJsonObject();

                                    Boolean state = jsonObject.get("state").getAsBoolean();
                                    user.isFollowing = state;



                                    notifyDataSetChanged();

                                    if(state){

                                        JSONObject jsonObject1 = new JSONObject();
                                        try {
                                            jsonObject1.put("type","RequestFollowNoti");
                                            jsonObject1.put("FollowUserId",user.userId);
                                            jsonObject1.put("UserName",data.result.get("myName").getAsString());

                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                        FollowListActivity activity = (FollowListActivity)context;

//                                     MyApplication
                                        MyApplication application = (MyApplication)activity.getApplication();
                                        application.SendMessage(jsonObject1);
                                    }


                                }
                            }

                            @Override
                            public void onResponseFail(ResponseBody ErrorBody) {
                                Toast.makeText(context, "onResponseFail", Toast.LENGTH_SHORT).show();
                                Log.i("e", ErrorBody.toString());
                            }

                            @Override
                            public void onNetworkError(Throwable t) {
                                Toast.makeText(context, "onNetworkError", Toast.LENGTH_SHORT).show();
                                Log.i("err", t.toString());
                            }
                        });


                        JsonObject jsonObject = new JsonObject();

                        jsonObject.addProperty("pk",user.userId);
//                    jsonObject.addProperty("followId",like.userId);

                        networkManager.post2Request("follow","setFollowing",jsonObject);


                    }
                });

            }


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = getAdapterPosition();
                    FollowUser user = itemList.get(position);

                    Intent intent = new Intent(context, MyPageActivity.class);

                    intent.putExtra("IsMyPage",user.isMe);
                    intent.putExtra("UserPk",user.userId);
                    context.startActivity(intent);


                }
            });
        }

    }
}
