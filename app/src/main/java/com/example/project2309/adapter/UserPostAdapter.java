

package com.example.project2309.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project2309.R;
import com.example.project2309.data.Chat;
import com.example.project2309.data.UserFeed;
import com.example.project2309.ui.FeedActivity;
import com.example.project2309.ui.ImageActivity;

import java.util.List;

public class UserPostAdapter extends RecyclerView.Adapter<UserPostAdapter.ViewHolder> {

    private List<UserFeed> itemList;
    Context context;
    String TAG = "UserPostAdapter";


    public UserPostAdapter(List<UserFeed> itemList,Context context) {

        this.itemList = itemList;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_post_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserFeed item = itemList.get(position);

        String url = "http://49.247.30.164" + item.imagePath;
        Glide.with(context).load(url).into(holder.이미지);

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
//        TextView textView;

        ImageView 이미지;
        public ViewHolder(View view) {
            super(view);


            이미지 = view.findViewById(R.id.imageView7);

            이미지.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = getAdapterPosition();
                    UserFeed userFeed = itemList.get(position);

                    Intent intent = new Intent(context, FeedActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("postId", userFeed.postId);

                    if(position != 0){
                        UserFeed userFeed2 = itemList.get(position-1);
                        intent.putExtra("previousPostId", userFeed2.postId);
                    }
                    intent.putExtra("userId", userFeed.userId);
                    context.startActivity(intent);


                }
            });
//            textView = view.findViewById(R.id.text_view);
        }
    }
}
