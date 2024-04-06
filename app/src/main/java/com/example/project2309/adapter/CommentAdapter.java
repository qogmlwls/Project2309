package com.example.project2309.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project2309.R;
import com.example.project2309.data.Comment;
import com.example.project2309.data.Post;
import com.example.project2309.ui.MyPageActivity;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private List<Comment> itemList;
    Context context;


    public interface OnItemClickListener {
//        void onItemClick(int position);
        void Edit(Comment item);
        void delete(Comment item);

        void ReplySetVisibility(Comment comment);

        void MoreReply(Comment comment);

        void WriteReply(Comment comment);



//        void CommentButtonClick(int post_id);

    }

    private CommentAdapter.OnItemClickListener listener;

    public CommentAdapter(List<Comment> itemList, Context context, CommentAdapter.OnItemClickListener listener) {
        this.itemList = itemList;
        this.context = context;
        this.listener = listener;

    }
//    public CommentAdapter(List<Comment> itemList, Context context) {
//        this.itemList = itemList;
//        this.context = context;
//
//    }
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentAdapter.ViewHolder holder, int position) {


        Comment comment = itemList.get(position);

//        holder.imageView.
        holder.닉네임.setText(comment.user_name);
        holder.댓글내용.setText(comment.content);


        holder.등록날짜.setText("등록  " + comment.create_date);

        if(comment.update_date == null){
            holder.수정날짜.setVisibility(View.GONE);
        }
        else{
            holder.수정날짜.setText("수정  "+comment.update_date);
            holder.수정날짜.setVisibility(View.VISIBLE);
        }



        if(comment.parent == -1 ){
            holder.답글달기.setVisibility(View.VISIBLE);

        }
        else{
            holder.답글달기.setVisibility(View.GONE);
        }



        if(comment.isMyComment){

            holder.더보기.setVisibility(View.VISIBLE);
        }
        else{
            holder.더보기.setVisibility(View.GONE);
        }


        if(comment.user_profile.length() != 0 ){
            String url = "http://49.247.30.164"+comment.user_profile;
            Glide.with(context).load(url).into(holder.imageView);
        }
        else{
            holder.imageView.setImageDrawable(context.getDrawable(R.drawable.baseline_person_24));
        }





        if(comment.parent == -1  && comment.ReplyViewVisibility){
            holder.답글.setVisibility(View.VISIBLE);
//            holder.Replylist = itemList;

//            adapter = new CommentAdapter(itemList, context);
            holder.답글.setLayoutManager(new LinearLayoutManager(context));
            holder.답글.setAdapter(new CommentAdapter(comment.replies, context,listener));
//            holder.adapter.notifyDataSetChanged();
        }
        else{
            holder.답글.setVisibility(View.GONE);
        }



        if(comment.Reply_comment_cnt > 0){

            holder.답글보기.setVisibility(View.VISIBLE);
            if(holder.답글.getVisibility() == View.VISIBLE){
                holder.답글보기.setText("답글 숨기기");
            }
            else{
                holder.답글보기.setText("답글 "+Integer.toString(comment.Reply_comment_cnt)+" 개 보기");
            }


        }
        else{
            holder.답글보기.setVisibility(View.GONE);
//            holder.답글.setVisibility(View.GONE);


        }
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        adapter = new CommentAdapter(itemList, this, this);
//        recyclerView.setAdapter(adapter);




        if(comment.parent == -1 ){


            if(comment.more_replyComment && holder.답글.getVisibility() == View.VISIBLE){
                holder.답글더보기.setVisibility(View.VISIBLE);

            }
            else{
                holder.답글더보기.setVisibility(View.GONE);

            }

        }
        else{
            holder.답글더보기.setVisibility(View.GONE);

        }




    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder {
//        TextView textView;

        ImageView imageView;
        TextView 닉네임;
        TextView 댓글내용;

        TextView 등록날짜, 수정날짜;

        Button 답글달기, 답글보기;

        ImageButton 더보기;

        RecyclerView 답글;


        Button 답글더보기;
//        List<Comment> Replylist;


//        boolean ReplyViewVisibility;
//        CommentAdapter adapter;


        public ViewHolder(View view) {
            super(view);

//            Replylist = new ArrayList<>();

            imageView = view.findViewById(R.id.imageView8);

            닉네임 = view.findViewById(R.id.textView35);

            댓글내용 = view.findViewById(R.id.textView37);

            답글달기 = view.findViewById(R.id.button33);

            답글보기 = view.findViewById(R.id.button34);

            등록날짜 = view.findViewById(R.id.textView36);
            수정날짜 = view.findViewById(R.id.textView43);

            더보기 = view.findViewById(R.id.imageButton12);


            답글 = view.findViewById(R.id.reply_recycler_view);


            답글더보기 = view.findViewById(R.id.button39);


            답글달기.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (listener != null) {
                        // 서버에게 요청
                        // post.isMyLike 가 true이면 좋아요 제거 요청
                        // post.isMyLike 가 false 이면 좋아요 요청
                        // 결과 성공시, 좋아요수와 아이콘 형태 변경.
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.WriteReply(itemList.get(position));
                        }
                    }


                }
            });


//            ReplyViewVisibility = false;

            더보기.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showOptionsDialog("");
                }
            });


            답글더보기.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        // 서버에게 요청
                        // post.isMyLike 가 true이면 좋아요 제거 요청
                        // post.isMyLike 가 false 이면 좋아요 요청
                        // 결과 성공시, 좋아요수와 아이콘 형태 변경.
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.MoreReply(itemList.get(position));
                        }
                    }
                }
            });


            답글보기.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (listener != null) {
                        // 서버에게 요청
                        // post.isMyLike 가 true이면 좋아요 제거 요청
                        // post.isMyLike 가 false 이면 좋아요 요청
                        // 결과 성공시, 좋아요수와 아이콘 형태 변경.
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.ReplySetVisibility(itemList.get(position));
                        }
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
            });


            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Comment comment = itemList.get(position);

                    Intent intent = new Intent(context, MyPageActivity.class);

                    intent.putExtra("IsMyPage",comment.isMyComment);
                    intent.putExtra("UserPk",comment.user_id);

                    context.startActivity(intent);

                }
            });


        }

        // 게시글 수정, 삭제 선택하는 대화창
        private void showOptionsDialog(String selectedItem) {


//            new AlertDialog.Builder(con)
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("")
                    .setItems(new CharSequence[]{"수정", "삭제"}, (dialog, which) -> {
                        switch (which) {
                            case 0:
                                // "Edit" 선택 시 동작
                                editItem(selectedItem);
                                break;
                            case 1:
                                // "Delete" 선택 시 동작
                                deleteItem(selectedItem);
                                break;
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        // 게시글 수정
        private void editItem(String selectedItem) {
            // 수정 로직을 여기에 구현
//            Toast.makeText(context, "Editing item: " + selectedItem, Toast.LENGTH_SHORT).show();
            if (listener != null) {
                // 서버에게 요청
                // post.isMyLike 가 true이면 좋아요 제거 요청
                // post.isMyLike 가 false 이면 좋아요 요청
                // 결과 성공시, 좋아요수와 아이콘 형태 변경.
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.Edit(itemList.get(position));
                }
            }

        }

        // 게시글 삭제
        private void deleteItem(String selectedItem) {
            // 삭제 로직을 여기에 구현
//            itemList.remove(selectedItem);
//            notifyDataSetChanged();
            if (listener != null) {
                // 서버에게 요청
                // post.isMyLike 가 true이면 좋아요 제거 요청
                // post.isMyLike 가 false 이면 좋아요 요청
                // 결과 성공시, 좋아요수와 아이콘 형태 변경.
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.delete(itemList.get(position));
                }
            }

        }
    }

}
