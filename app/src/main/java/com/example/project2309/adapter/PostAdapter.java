package com.example.project2309.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project2309.MyApplication;
import com.example.project2309.ui.TagFeedActivity;
import com.example.project2309.data.Data;
import com.example.project2309.network.NetworkManager;
import com.example.project2309.ui.CommentActivity;
import com.example.project2309.ui.LikeActivity;
import com.example.project2309.R;
import com.example.project2309.data.Post;
import com.example.project2309.ui.MyPageActivity;
import com.example.project2309.ui.UpdatePostActivity;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    public boolean click;


    public static int CommentActivityCode =  112;
    private static final int REQUEST_CODE2 = 201;  // 어떤 상수든 사용 가능

    private List<Post> itemList;
//    private OnItemClickListener listener;

    Context context;
//    public interface OnItemClickListener {
//        void onItemClick(int position);
//        void Edit(Post item);
//        void delete(int post_id);
//        void CommentButtonClick(Post post);
//
//    }


    public PostAdapter(List<Post> itemList, Context context) {
        this.itemList = itemList;
//        this.listener = listener;
        this.context = context;
        click = true;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        
        Post item = itemList.get(position);

         holder.닉네임.setText(item.user_name);
        holder.등록날짜.setText(item.create_date);
//        holder.문구.setText(item.comment);

//        String textWithHashtags = "This is a sample #text with #hashtags.";

        SpannableString spannableString = new SpannableString(item.comment);

        Pattern pattern = Pattern.compile("#\\w+");
        Matcher matcher = pattern.matcher(item.comment);

        while (matcher.find()) {
            final String hashtag = matcher.group();
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View view) {
                    // 해시태그 클릭 시 수행할 작업 추가

                    // 해쉬태그.

                    String clickedHashtag = hashtag.substring(1); // "#" 제거

                    Intent intent = new Intent(context, TagFeedActivity.class);
                    intent.putExtra("Hashtag_name",clickedHashtag);
                    context.startActivity(intent);

//                    Toast.makeText(context.getApplicationContext(), "Clicked hashtag: " + clickedHashtag, Toast.LENGTH_SHORT).show();
                    // 클릭된 해시태그에 대한 작업을 수행할 수 있도록 코드를 추가하세요.
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false); // 클릭 가능한 텍스트의 밑줄 제거
                    ds.bgColor = Color.WHITE; // 클릭 했을 때 배경색 변경을 방지하기 위해 텍스트 배경색을 흰색으로 설정
                }
            };

            int startIndex = item.comment.indexOf(hashtag);
            int endIndex = startIndex + hashtag.length();

            spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.blue)),
                    startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        holder.문구.setText(spannableString);
        holder.문구.setMovementMethod(LinkMovementMethod.getInstance());

        
        holder.좋아요수.setText(Integer.toString(item.like_cnt));
        holder.댓글수.setText(Integer.toString(item.comment_cnt));


//
        if(item.isMyPost)
            holder.게시글더보기버튼.setVisibility(View.VISIBLE);
        else
            holder.게시글더보기버튼.setVisibility(View.GONE);

        Drawable favoriteDrawable;
        if(item.isMyLike){
            favoriteDrawable = context.getResources().getDrawable(R.drawable.baseline_favorite_24);
        }
        else {
            favoriteDrawable = context.getResources().getDrawable(R.drawable.baseline_favorite_border_24);
        }
        holder.좋아요버튼.setImageDrawable(favoriteDrawable);


        if(item.user_profile.length() != 0 ){
            String url = "http://49.247.30.164"+item.user_profile;
            Glide.with(context).load(url).into(holder.프로필이미지);
        }
        else{
            Drawable drawable;
            drawable = context.getResources().getDrawable(R.drawable.baseline_person_24);
            holder.프로필이미지.setImageDrawable(drawable);
        }


        List<String> imagePath = item.image;
//        Log.i("image",imagePath.t)

        holder.게시글이미지리사이클러뷰.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false));
        holder.게시글이미지리사이클러뷰.setAdapter(new PostInnerAdapter(imagePath,context));


        if(item.update_date != null){
            holder.수정날짜.setText("수정  "+item.update_date);
            holder.수정날짜.setVisibility(View.VISIBLE);

            //        holder.textView.setText(item);
        }
        else{
            holder.수정날짜.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
//        TextView textView;

        ImageView 프로필이미지;
        
        TextView 닉네임, 등록날짜, 문구, 좋아요수, 댓글수;
        
        ImageButton 게시글더보기버튼, 좋아요버튼;
        
        RecyclerView 게시글이미지리사이클러뷰;

        TextView 수정날짜;
        
        
        
        public ViewHolder(View view) {
            super(view);
//            textView = view.findViewById(R.id.text_view);


            프로필이미지 = view.findViewById(R.id.imageView5);
            닉네임 = view.findViewById(R.id.textView29);
            등록날짜 = view.findViewById(R.id.textView33);
            문구 = view.findViewById(R.id.textView32);
            좋아요수 = view.findViewById(R.id.textView38);
            댓글수 = view.findViewById(R.id.textView44);
            게시글더보기버튼 = view.findViewById(R.id.imageButton6);
            좋아요버튼 = view.findViewById(R.id.imageButton8);
            게시글이미지리사이클러뷰 = view.findViewById(R.id.recycler_view2);

            수정날짜 = view.findViewById(R.id.textView42);

            게시글더보기버튼.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showOptionsDialog("");
                }
            });
            PagerSnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(게시글이미지리사이클러뷰);

            ImageButton btn = itemView.findViewById(R.id.imageButton9);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    AppCompatActivity activity = (AppCompatActivity) context;

                    if(click){
                        click=false;
                        int position = getAdapterPosition();
                        Post post = itemList.get(position);

                        Intent intent = new Intent(context, CommentActivity.class);
                        intent.putExtra("post_id",post.post_id);
                        intent.putExtra("comment_cnt",post.comment_cnt);

                        activity.startActivityForResult(intent,CommentActivityCode);
                    }


                }
            });

            TextView 댓글수2 = view.findViewById(R.id.textView34);
            댓글수2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppCompatActivity activity = (AppCompatActivity) context;

                    if(click){
                        click=false;
                        int position = getAdapterPosition();
                        Post post = itemList.get(position);

                        Intent intent = new Intent(context, CommentActivity.class);
                        intent.putExtra("post_id",post.post_id);
                        intent.putExtra("comment_cnt",post.comment_cnt);

                        activity.startActivityForResult(intent,CommentActivityCode);
                    }

                }
            });

            좋아요수.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Post post = itemList.get(position);

                    Intent intent = new Intent(context, LikeActivity.class);
                    intent.putExtra("postId", post.post_id);
                    context.startActivity(intent);
                }
            });


            댓글수.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppCompatActivity activity = (AppCompatActivity) context;

                    if(click){
                        click=false;
                        int position = getAdapterPosition();
                        Post post = itemList.get(position);

                        Intent intent = new Intent(context, CommentActivity.class);
                        intent.putExtra("post_id",post.post_id);
                        intent.putExtra("comment_cnt",post.comment_cnt);

                        activity.startActivityForResult(intent,CommentActivityCode);
                    }


                }
            });

            TextView 좋아요수2 = view.findViewById(R.id.textView31);
            좋아요수2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Post post = itemList.get(position);

                    Intent intent = new Intent(context, LikeActivity.class);
                    intent.putExtra("postId", post.post_id);
                    context.startActivity(intent);
                }
            });


            프로필이미지.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = getAdapterPosition();
                    Post post = itemList.get(position);

                    Intent intent = new Intent(context, MyPageActivity.class);

                    intent.putExtra("IsMyPage",post.isMyPost);
                    intent.putExtra("UserPk",post.user_id);

                    context.startActivity(intent);

                }
            });

            좋아요버튼.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


//                    if (listener != null) {
//                        // 서버에게 요청
//                        // post.isMyLike 가 true이면 좋아요 제거 요청
//                        // post.isMyLike 가 false 이면 좋아요 요청
//                        // 결과 성공시, 좋아요수와 아이콘 형태 변경.
//                        int position = getAdapterPosition();
//                        if (position != RecyclerView.NO_POSITION) {
//                            listener.onItemClick(position);
//                        }
//                    }


                    int position = getAdapterPosition();
                    Post post = itemList.get(position);


                    NetworkManager networkManager = new NetworkManager(context.getApplicationContext());
                    networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
                        @Override
                        public void onResponseSuccess(Data data) {


                            if(data.result.get("result").getAsString().equals(Data.SUCCESS)){


                                int post_id = data.result.get("post_id").getAsInt();
                                int like_cnt = data.result.get("like_cnt").getAsInt();
                                boolean isMyLike = data.result.get("isMyLike").getAsBoolean();


                                for(int i=0;i<itemList.size();i++){
                                    Post post = itemList.get(i);
                                    if(post.post_id == post_id){
                                        post.isMyLike = isMyLike;

                                        if(post.like_cnt < like_cnt){
                                            JSONObject jsonObject1 = new JSONObject();
                                            try {

                                                jsonObject1.put("type","RequestFavoriteNoti");
                                                jsonObject1.put("postId",post_id);
                                                jsonObject1.put("postUserName",  post.user_name);
                                                jsonObject1.put("PostUserId", post.user_id);

                                            } catch (JSONException e) {
                                                throw new RuntimeException(e);
                                            }

                                            Activity activity = (Activity)context;
                                            MyApplication application = (MyApplication)activity.getApplication();
                                            application.SendMessage(jsonObject1);
                                        }

                                        post.like_cnt = like_cnt;


                                    }
                                }


                                notifyDataSetChanged();





                            }
                        }


                        @Override
                        public void onResponseFail(ResponseBody ErrorBody) {
                            Toast.makeText(context, "onResponseFail", Toast.LENGTH_SHORT).show();
                            Log.i("e",ErrorBody.toString());


                        }

                        @Override
                        public void onNetworkError(Throwable t) {
                            Toast.makeText(context, "onNetworkError", Toast.LENGTH_SHORT).show();
                            Log.i("err",t.toString());
                        }

                    });
                    JsonObject body = new JsonObject();
                    body.addProperty("post_id",itemList.get(position).post_id);
                    networkManager.postRequest("test","Like",body);




//                    int position = getAdapterPosition();
//                    Post post = itemList.get(position);
//                    Drawable favoriteDrawable;
//                    if(post.isMyLike){
//                        favoriteDrawable = context.getResources().getDrawable(R.drawable.baseline_favorite_24);
//                        좋아요수.setText(Integer.toString(post.like_cnt+1));
//
//                    }
//                    else {
//                        favoriteDrawable = context.getResources().getDrawable(R.drawable.baseline_favorite_border_24);
//                        좋아요수.setText(Integer.toString(post.like_cnt-1));
//
//                    }
//
//                    좋아요버튼.setImageDrawable(favoriteDrawable);
//

                }
            });


            문구.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

        }

        // 게시글 수정, 삭제 선택하는 대화창
        private void showOptionsDialog(String selectedItem) {
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
////            Toast.makeText(context, "Editing item: " + selectedItem, Toast.LENGTH_SHORT).show();
//            if (listener != null) {
//                // 서버에게 요청
//                // post.isMyLike 가 true이면 좋아요 제거 요청
//                // post.isMyLike 가 false 이면 좋아요 요청
//                // 결과 성공시, 좋아요수와 아이콘 형태 변경.
//                int position = getAdapterPosition();
//                if (position != RecyclerView.NO_POSITION) {
//                    listener.Edit(itemList.get(position));
//                }
//            }

            int position = getAdapterPosition();

            Post item = itemList.get(position);

            Intent intent = new Intent(context, UpdatePostActivity.class);
//
//        intent.putExtra("uris", uriArrayList); // "uris"라는 키로 Uri 배열 추가
//
            intent.putExtra("post_id",item.post_id);
            intent.putExtra("comment",item.comment);


            // Convert Uri array to ArrayList (ParcelableArrayList)
            ArrayList<String> ArrayList = new ArrayList<>(item.image);

            intent.putExtra("images", ArrayList); // "uris"라는 키로 Uri 배열 추가
            AppCompatActivity activity = (AppCompatActivity) context;

//            Intent intent = new Intent(MyPageActivity.this, EditProfileActivity.class);
//            startActivityForResult(intent, REQUEST_CODE2);
            activity.startActivityForResult(intent,REQUEST_CODE2);


        }

        // 게시글 삭제
        private void deleteItem(String selectedItem) {
            // 삭제 로직을 여기에 구현
//            itemList.remove(selectedItem);
//            notifyDataSetChanged();
//            if (listener != null) {
//                // 서버에게 요청
//                // post.isMyLike 가 true이면 좋아요 제거 요청
//                // post.isMyLike 가 false 이면 좋아요 요청
//                // 결과 성공시, 좋아요수와 아이콘 형태 변경.
//                int position = getAdapterPosition();
//                if (position != RecyclerView.NO_POSITION) {
//                    listener.delete(itemList.get(position).post_id);
//                }
//            }

            int position = getAdapterPosition();
            int post_id = itemList.get(position).post_id;

            NetworkManager networkManager = new NetworkManager(context.getApplicationContext());
            networkManager.setCallback(new NetworkManager.CallbackFunc<Data>() {
                @Override
                public void onResponseSuccess(Data data) {

                    Log.i("PostAdapter",data.result.toString());
                    if(data.result.get("result").getAsString().equals(Data.SUCCESS)){


                        int post_id = data.result.get("post_id").getAsInt();

                        for(int i=0;i<itemList.size();i++){
                            if(itemList.get(i).post_id == post_id){
                                itemList.remove(itemList.get(i));
                                notifyItemRemoved(i);

                            }
                        }


//                     삭제.
//                    itemList.remove();
//                    Toast.makeText(MainActivity.this, "삭제 성공", Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onResponseFail(ResponseBody ErrorBody) {
                    Toast.makeText(context, "onResponseFail", Toast.LENGTH_SHORT).show();
                    Log.i("e",ErrorBody.toString());


                }

                @Override
                public void onNetworkError(Throwable t) {
                    Toast.makeText(context, "onNetworkError", Toast.LENGTH_SHORT).show();
                    Log.i("err",t.toString());
                }
            });

            networkManager.deleteRequest("posts","deletePost",Integer.toString(post_id));






        }
    }




}
