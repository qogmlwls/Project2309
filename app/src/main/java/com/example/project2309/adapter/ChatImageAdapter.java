package com.example.project2309.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2309.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ChatImageAdapter extends RecyclerView.Adapter<ChatImageAdapter.ViewHolder> {


    List<Uri> list;
    Context context;

    public ChatImageAdapter(List<Uri> list, Context context){
        this.list = list;

        this.context = context;

    }

    private Bitmap getBitmapFromUri(Uri uri) {
        Bitmap bitmap = null;
        try {
            InputStream imageStream = context.getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(imageStream);
            imageStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }





    @NonNull
    @Override
    public ChatImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cr_post_item, parent, false);
        return new ChatImageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatImageAdapter.ViewHolder holder, int position) {

        Uri uri = list.get(position);

        holder.이미지.setImageBitmap(getBitmapFromUri(uri));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView 이미지;
        ImageButton 삭제버튼;

        public ViewHolder(View view) {
            super(view);
            이미지 = view.findViewById(R.id.imageView3);
            삭제버튼 = view.findViewById(R.id.imageButton10);
            삭제버튼.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        list.remove(position);

                        // 이동된 아이템의 위치를 갱신
                        if(position == list.size()){
                            notifyItemRangeChanged(list.size()-1, list.size());
                        }
                        notifyItemRemoved(position);
                    }
//                    list.remove(position);
//                    notifyItemRemoved(position);
//                    // 이동된 아이템의 위치를 갱신
//
//                    notifyItemRangeChanged(0, list.size());
                }
            });

        }


    }

}
