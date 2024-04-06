package com.example.project2309.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project2309.R;
import com.example.project2309.data.Chat;
import com.example.project2309.data.Room;
import com.example.project2309.data.TagPost;
import com.example.project2309.data.User;
import com.example.project2309.ui.ChattingActivity;
import com.example.project2309.ui.ImageActivity;
import com.example.project2309.ui.MyPageActivity;
import com.example.project2309.ui.TagFeedActivity;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    String TAG = "SearchAdapter";
    List<User> itemList;
    Context context;

    public SearchAdapter(List<User> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }
    public int getItemViewType(int position) {
        return itemList.get(position).type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == User.USER) {

            Log.i(TAG, "Type : MYCHAT");

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
            Log.i(TAG, "--------------------------------------------------");

            return new SearchAdapter.UserViewHolder(view);
        } else if (viewType == User.HASHTAG) {
            Log.i(TAG, "Type : OTHERCHAT");

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tagpost_item, parent, false);
            Log.i(TAG, "--------------------------------------------------");

            return new SearchAdapter.TagViewHolder(view);
        } else {

            Log.i(TAG, "Type : null");
            Log.i(TAG, "--------------------------------------------------");

            return null;
        }
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
//        return new SearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        User user = itemList.get(position);


        if (user.type == User.USER) {
            UserViewHolder holder1 = (UserViewHolder) holder;
            if (user.profile.length() != 0) {
                String url = "http://49.247.30.164" + user.profile;
                Glide.with(context).load(url).into(holder1.프로필이미지);
            } else {
                Drawable drawable;
                drawable = context.getResources().getDrawable(R.drawable.baseline_person_24);
                holder1.프로필이미지.setImageDrawable(drawable);
            }


            holder1.닉네임.setText(user.name);
            holder1.소개글.setText(user.introText);
        } else if (user.type == User.HASHTAG) {


            TagViewHolder holder1 = (TagViewHolder) holder;
            holder1.태그이름.setText("#"+user.name);
            holder1.게시글갯수.setText("게시글 총 "+user.count +"개");
            holder1.하단공백.setVisibility(View.GONE);

        }
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public class UserViewHolder extends RecyclerView.ViewHolder {

        ImageView 프로필이미지;
        TextView 닉네임, 소개글;


        public UserViewHolder(View view) {
            super(view);

            프로필이미지 = view.findViewById(R.id.imageView14);
            닉네임 = view.findViewById(R.id.textView56);
            소개글 = view.findViewById(R.id.textView57);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    User user = itemList.get(position);

                    Intent intent = new Intent(context, MyPageActivity.class);

                    intent.putExtra("IsMyPage",user.isMyData);
                    intent.putExtra("UserPk",user.userId);
                    context.startActivity(intent);

                }
            });
        }

    }

    public class TagViewHolder extends RecyclerView.ViewHolder {

        TextView 태그이름, 게시글갯수;
        View 하단공백;
        public TagViewHolder(@NonNull View itemView) {
            super(itemView);

            태그이름 = itemView.findViewById(R.id.textView76);
            게시글갯수 = itemView.findViewById(R.id.textView77);
            하단공백 = itemView.findViewById(R.id.view21);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();
                    User tagPost = itemList.get(position);

                    Intent intent = new Intent(context, TagFeedActivity.class);
                    intent.putExtra("Hashtag_name",tagPost.name);
                    context.startActivity(intent);



                }
            });

        }

    }

}
