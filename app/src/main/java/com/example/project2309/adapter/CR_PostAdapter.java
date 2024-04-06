package com.example.project2309.adapter;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2309.R;

import java.util.List;

public class CR_PostAdapter extends RecyclerView.Adapter<CR_PostAdapter.ViewHolder> {


    List<Bitmap> list;

    public CR_PostAdapter(List<Bitmap> list){
        this.list = list;
    }





    @NonNull
    @Override
    public CR_PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cr_post_item, parent, false);
        return new CR_PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CR_PostAdapter.ViewHolder holder, int position) {

        holder.이미지.setImageBitmap(list.get(position));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    String TAG = "CR_PostAdatper";

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


                    Log.i(TAG,"삭제");

                    int position = getAdapterPosition();
                    Log.i(TAG,"position : "+Integer.toString(position));
                    Log.i(TAG,"RecyclerView.NO_POSITION : "+Integer.toString(RecyclerView.NO_POSITION));


                    if (position != RecyclerView.NO_POSITION) {

                        list.remove(position);
                        Log.i(TAG,"아이템 삭제.");
                        Log.i(TAG,"list.size() : "+Integer.toString(list.size()));

                        // 이동된 아이템의 위치를 갱신
                        if(position == list.size()){
                            notifyItemRangeChanged(list.size()-1, list.size());

                            Log.i(TAG,"list.size() : "+Integer.toString(list.size()));
                            Log.i(TAG,"notifyItemRangeChanged.");

                        }

//                        notifyItemRangeRemoved();
//                        notifyItemRemoved(position);
                        notifyDataSetChanged();
//                        notifyItemRangeChanged(position,1);

                        Log.i(TAG,"notifyItemRemoved.");

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
