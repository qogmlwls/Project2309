package com.example.project2309.adapter;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import com.example.project2309.data.Data;
import com.example.project2309.data.FollowUser;
import com.example.project2309.data.Like;
import com.example.project2309.data.User;
import com.example.project2309.network.NetworkManager;
import com.example.project2309.ui.FollowListActivity;
import com.example.project2309.ui.MyPageActivity;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;

public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.ViewHolder> {

    List<Like> itemList;
    Context context;

    String TAG = "LikeAdapter";
    public LikeAdapter(List<Like> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public LikeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_like_item, parent, false);
        return new LikeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LikeAdapter.ViewHolder holder, int position) {

        Like like = itemList.get(position);


        holder.닉네임.setText(like.name);
        holder.소개글.setText(like.introText);

        if(like.isFollowing){
            holder.팔로우버튼.setText("팔로잉");

            holder.팔로우버튼.setBackgroundColor(Color.LTGRAY);
            holder.팔로우버튼.setTextColor(Color.BLACK);

//            holder.팔로우버튼.setBackgroundColor(Color.);


        }
        else{
            holder.팔로우버튼.setText("팔로우");
            holder.팔로우버튼.setBackgroundColor(Color.parseColor("#FF6200EE"));
            holder.팔로우버튼.setTextColor(Color.WHITE);

        }


        if(like.isMyLike){
            holder.팔로우버튼.setVisibility(View.GONE);

        }
        else{
            holder.팔로우버튼.setVisibility(View.VISIBLE);

        }


        if(like.profile.length() != 0 ){
            String url = "http://49.247.30.164" + like.profile;
            Glide.with(context).load(url).into(holder.프로필이미지);

        }
        else{
            Drawable drawable;
            drawable = context.getResources().getDrawable(R.drawable.baseline_person_24);
            holder.프로필이미지.setImageDrawable(drawable);
        }


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView 프로필이미지;
        TextView 닉네임, 소개글;

        Button 팔로우버튼;

        public ViewHolder(View view) {
            super(view);

            프로필이미지 = view.findViewById(R.id.imageView20);
            닉네임 = view.findViewById(R.id.textView63);
            소개글 = view.findViewById(R.id.textView64);
            팔로우버튼 = view.findViewById(R.id.button54);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Like like = itemList.get(position);

                    Intent intent = new Intent(context, MyPageActivity.class);
                    intent.putExtra("IsMyPage",like.isMyLike);
                    intent.putExtra("UserPk",like.userId);
                    context.startActivity(intent);

                }
            });


            팔로우버튼.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    int position = getAdapterPosition();
                    Like like = itemList.get(position);

                    NetworkManager networkManager = new NetworkManager(context.getApplicationContext());
                    networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
                        @Override
                        public void onResponseSuccess(Data data) {
                            Log.i(TAG,data.result.toString());

                            if (data.result.get("result").getAsString().equals(Data.SUCCESS)) {

                                JsonObject jsonObject = data.result.get("isFollowMe").getAsJsonObject();

                                Boolean state = jsonObject.get("state").getAsBoolean();
                                like.isFollowing = state;

                                notifyDataSetChanged();


                                if(state){

                                    JSONObject jsonObject1 = new JSONObject();
                                    try {
                                        jsonObject1.put("type","RequestFollowNoti");
                                        jsonObject1.put("FollowUserId",like.userId);
                                        jsonObject1.put("UserName",data.result.get("myName").getAsString());

                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }

//                                     MyApplication
                                    Activity activity = (Activity)context;
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

                    jsonObject.addProperty("pk",like.userId);
//                    jsonObject.addProperty("followId",like.userId);

                    networkManager.post2Request("follow","setFollowing",jsonObject);


                }
            });




        }

    }

}
