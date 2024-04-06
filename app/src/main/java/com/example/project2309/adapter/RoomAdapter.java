package com.example.project2309.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.example.project2309.data.Room;
import com.example.project2309.network.SharedPreferencesManager;
import com.example.project2309.ui.ChattingActivity;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {

    List<Room> itemList;
    Context context;

    public RoomAdapter(List<Room> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public RoomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_item, parent, false);
        return new RoomAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Room room = itemList.get(position);

        holder.닉네임.setText(room.fromName);
        holder.메세지.setText(room.last_chat_message);
        holder.시간.setText(room.last_chat_date);
//        holder.안읽은채팅수.setVisibility(View.GONE);


        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context.getApplicationContext());
        boolean isNotiOn = sharedPreferencesManager.getRoomNotiOn(room.room_id);
        if(isNotiOn){
            holder.noti.setVisibility(View.GONE);
        }
        else{
            holder.noti.setVisibility(View.VISIBLE);
        }

        if(room.fromProfile.length() != 0 ){
            String url = "http://49.247.30.164"+room.fromProfile;
            Glide.with(context).load(url).into(holder.프로필이미지);
        }
        else{

            Drawable drawable;
            drawable = context.getResources().getDrawable(R.drawable.baseline_person_24);

            holder.프로필이미지.setImageDrawable(drawable);

        }

        if(room.Unread_chat_cnt == 0){
            holder.안읽은채팅수.setVisibility(View.GONE);
//            안읽은채팅
        }
        else{
            holder.안읽은채팅수.setVisibility(View.VISIBLE);

            if(room.Unread_chat_cnt < 1000){
                holder.안읽은채팅수.setText(Integer.toString(room.Unread_chat_cnt));
            }
            else{
                holder.안읽은채팅수.setText("+999");
            }

        }

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView 프로필이미지;
        TextView 시간, 안읽은채팅수, 닉네임, 메세지;

        ConstraintLayout 아이템;

        ImageView noti;

        public ViewHolder(View view) {
            super(view);

            프로필이미지 = view.findViewById(R.id.imageView);
            시간 = view.findViewById(R.id.textView);
            안읽은채팅수 = view.findViewById(R.id.textView2);
            닉네임 = view.findViewById(R.id.textView3);
            메세지 = view.findViewById(R.id.textView4);

            아이템 = view.findViewById(R.id.constraint);

            noti = view.findViewById(R.id.imageView21);

            아이템.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();
                    Room room = itemList.get(position);

                    Intent intent = new Intent(context, ChattingActivity.class);
                    intent.putExtra("roomId",room.room_id);
                    intent.putExtra("fromPk",room.fromPk);

                    context.startActivity(intent);

                    room.Unread_chat_cnt = 0;


                }
            });


            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    int position = getAdapterPosition();
                    Room room = itemList.get(position);
                    SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context.getApplicationContext());
                    boolean isNotiOn = sharedPreferencesManager.getRoomNotiOn(room.room_id);
                    showOptionsDialog(isNotiOn,room);

                    return false;
                }
            });


        }

    }


    // 게시글 수정, 삭제 선택하는 대화창
    private void showOptionsDialog(boolean state,Room room) {
        
        String message = "알림 ";
        if(state){
            message += "끄기";
        }
        else{
            message += "켜기";
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("")
                .setItems(new CharSequence[]{message}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            // "Edit" 선택 시 동작
                            setNoti(!state,room.room_id);
                            notifyDataSetChanged();
                            break;
//                        case 1:, "채팅방 나가기"
//                            // "Delete" 선택 시 동작
//                            deleteItem(selectedItem);
//                            break;
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    void setNoti(boolean state,int roomPk){

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context.getApplicationContext());
        sharedPreferencesManager.setRoomNoti(state,roomPk);

    }

}
