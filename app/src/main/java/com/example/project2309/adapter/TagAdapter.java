package com.example.project2309.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2309.R;
import com.example.project2309.data.TagPost;

import java.text.DecimalFormat;
import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder> {

    List<TagPost> itemList;
    Context context;

    EditText editText;
    String TAG = "TagAdapter";
    public TagAdapter(List<TagPost> itemList, Context context, EditText editText) {
        this.itemList = itemList;
        this.context = context;
        this.editText = editText;
    }

    @NonNull
    @Override
    public TagAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "--------------------------------------------------");
        Log.i(TAG, "onCreateViewHolder() 실행.");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tagpost_item, parent, false);

        Log.i(TAG, "onCreateViewHolder() 끝.");
        Log.i(TAG, "--------------------------------------------------");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.i(TAG, "--------------------------------------------------");
        Log.i(TAG, "onBindViewHolder ");
        Log.i(TAG, "position : " + Integer.toString(position));

        TagPost tagPost = itemList.get(position);

        holder.게시글갯수.setText(tagPost.postCount);
        holder.태그이름.setText(tagPost.tagName);


        // 마지막 아이템이 보이면 공백하단이 보이게 된다.
        if(itemList.size()-1 == position){
            holder.하단공백.setVisibility(View.VISIBLE);
        }
        else{
            holder.하단공백.setVisibility(View.GONE);
        }


        Log.i(TAG, "--------------------------------------------------");
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView 태그이름, 게시글갯수;
        View 하단공백;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            태그이름 = itemView.findViewById(R.id.textView76);
            게시글갯수 = itemView.findViewById(R.id.textView77);
            하단공백 = itemView.findViewById(R.id.view21);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();
                    TagPost tagPost = itemList.get(position);

//                    MainActivity2 activity2 = (MainActivity2) context;
//                    activity2.setEditText(tagPost.tagName);

                    String comment = editText.getText().toString();
                    int index = comment.lastIndexOf("#");
                    if(index == -1){
                        // #가 없음.
                    }
                    String text = comment.substring(0,index);
                    editText.setText(text + tagPost.tagName+" ");
                    editText.setSelection(editText.getText().length());
//                    tagPost.tagName

                }
            });

        }

    }

}
