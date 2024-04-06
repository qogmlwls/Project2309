package com.example.project2309.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project2309.R;

import java.util.List;

public class PostInnerAdapter extends RecyclerView.Adapter<PostInnerAdapter.ViewHolder> {


    List<String> imagePath;

    Context context;


    PostInnerAdapter(List<String> imagePath, Context context) {

        this.imagePath = imagePath;
        this.context = context;

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;


        public ViewHolder(View view) {
            super(view);
//            textView = view.findViewById(R.id.text_view);
            textView = itemView.findViewById(R.id.textView39);
            imageView = itemView.findViewById(R.id.imageView6);


        }
    }
    @NonNull
    @Override
    public PostInnerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_inner_item, parent, false);
        return new PostInnerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostInnerAdapter.ViewHolder holder, int position) {
        holder.textView.setText(Integer.toString(position+1)+" / "+Integer.toString(getItemCount()));

        String path = imagePath.get(position);
        String url= "http://49.247.30.164"+path;
        Glide.with(context).load(url)
                .into(holder.imageView);



    }

    @Override
    public int getItemCount() {
        return imagePath.size();
    }



}

