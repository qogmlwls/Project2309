package com.example.project2309.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project2309.ui.VideoActivity;
import com.example.project2309.network.NetworkManager;
import com.example.project2309.ui.ImageActivity;
import com.example.project2309.R;
import com.example.project2309.data.Chat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import okio.BufferedSource;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    String TAG = "ChatAdapter";
    List<Chat> itemList;

    Context context;

    public ChatAdapter(List<Chat> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "--------------------------------------------------");

        Log.i(TAG, "onCreateViewHolder ");


        if (viewType == Chat.MYCHAT) {

            Log.i(TAG, "Type : MYCHAT");

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mychat_item, parent, false);
            Log.i(TAG, "--------------------------------------------------");

            return new MyViewHolder(view);
        } else if (viewType == Chat.OTHERCHAT) {
            Log.i(TAG, "Type : OTHERCHAT");

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_chat_item, parent, false);
            Log.i(TAG, "--------------------------------------------------");

            return new OtherViewHolder(view);
        } else {

            Log.i(TAG, "Type : null");
            Log.i(TAG, "--------------------------------------------------");

            return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Log.i(TAG, "--------------------------------------------------");
        Log.i(TAG, "onBindViewHolder ");
        Log.i(TAG, "position : " + Integer.toString(position));

        Chat chat = itemList.get(position);

        Log.i(TAG, "mediaType : " + chat.mediaType);
        Log.i(TAG, "date : " + chat.date);


        if (chat.type == Chat.MYCHAT) {
            Log.i(TAG, "Type : MYCHAT");

            MyViewHolder holder1 = (MyViewHolder) holder;

            //
            if(position + 1 <  itemList.size()
                    && getYMD(getDate(itemList.get(position).date)).equals(getYMD(getDate(itemList.get(position+1).date)))
                    && getHourMin(getDate(itemList.get(position).date)).equals(getHourMin(getDate(itemList.get(position+1).date)))
                    && Chat.MYCHAT == itemList.get(position+1).type ){

                holder1.시간.setVisibility(View.GONE);

            }
            else{
                holder1.시간.setVisibility(View.VISIBLE);
                holder1.시간.setText(getHourMin(getDate(chat.date)));

            }

            // 마지막 채팅이거나
            // 이전 채팅과 년도가 다르면 보여주기
            if( position > 0 && !getYMD(getDate(itemList.get(position).date)).equals(getYMD(getDate(itemList.get(position-1).date)))){
                holder1.날짜.setVisibility(View.VISIBLE);
                holder1.날짜.setText(getYMD(getDate(itemList.get(position).date)));
            }
            else if(chat.isLastChat){
                holder1.날짜.setVisibility(View.VISIBLE);
                holder1.날짜.setText(getYMD(getDate(itemList.get(position).date)));

            }
            else{
                holder1.날짜.setVisibility(View.GONE);
            }
//            if(position > 0
//                    && getYMD(getDate(itemList.get(position).date)).equals(getYMD(getDate(itemList.get(position-1).date)))){
//
//                holder1.날짜.setVisibility(View.GONE);
//
//            }
//            else{
//                holder1.날짜.setVisibility(View.VISIBLE);
//                holder1.날짜.setText(getYMD(getDate(itemList.get(position).date)));
//            }

            
            
            holder1.안읽음.setVisibility(View.GONE);

//            if(chat.UnReadChat){
//                holder1.안읽음.setVisibility(View.VISIBLE);
//            }
//            else{
//                holder1.안읽음.setVisibility(View.GONE);
//

            if (chat.mediaType.equals(Chat.TEXT)) {


                holder1.메세지.setText(chat.message);
                holder1.메세지.setVisibility(View.VISIBLE);
                holder1.전송이미지.setVisibility(View.GONE);
                holder1.동영상.setVisibility(View.GONE);
                holder1.썸네일.setVisibility(View.GONE);
                holder1.동영상재생.setVisibility(View.GONE);
//                setlayout_constraintEnd_toStartOf
//                holder1.시간.set

                Log.i(TAG, "message : " + chat.message);

            } else if (chat.mediaType.equals(Chat.IMAGE)) {
                holder1.메세지.setVisibility(View.GONE);
                holder1.동영상.setVisibility(View.GONE);
                holder1.썸네일.setVisibility(View.GONE);
                holder1.동영상재생.setVisibility(View.GONE);

//                if(chat.imageUri.)
                if (chat.imagePath != null && chat.imagePath.length() != 0) {

                    holder1.전송이미지.setVisibility(View.VISIBLE);
                    Log.i(TAG, "imagePath : " + chat.imagePath);

                    String url = "http://49.247.30.164" + chat.imagePath;
                    Glide.with(context).load(url).into(holder1.전송이미지);
                } else {

                    holder1.전송이미지.setVisibility(View.VISIBLE);
                    holder1.전송이미지.setImageBitmap(getBitmapFromUri(chat.imageUri));
                    Log.i(TAG, "imageUri : " + chat.imageUri.toString());

//                    holder1.전송이미지.setVisibility(View.GONE);
                }
            } else if (chat.mediaType.equals(Chat.VIDEO)) {


                holder1.메세지.setVisibility(View.GONE);

                if (chat.videoUri == null) {

                    Log.i(TAG,"My Video Uri null");
                    Uri uri = isExistFile(getFileName(chat.videoPath),chat.videoPath);
                    if(uri != null){
                        holder1.동영상.setVisibility(View.GONE);
                        holder1.동영상재생.setVisibility(View.VISIBLE);
                        chat.videoUri = uri;
                    }else{
                        holder1.동영상.setVisibility(View.VISIBLE);
                        holder1.동영상재생.setVisibility(View.GONE);
                    }

//                    holder1.동영상.setVisibility(View.VISIBLE);
//                    holder1.동영상재생.setVisibility(View.GONE);

                } else {


                    Log.i(TAG,"My Video Uri not null");

                    holder1.동영상재생.setVisibility(View.VISIBLE);
                    holder1.동영상.setVisibility(View.GONE);

                }


                holder1.전송이미지.setVisibility(View.GONE);
                holder1.썸네일.setVisibility(View.VISIBLE);


                if (chat.ThumbnailPath != null && chat.ThumbnailPath.length() != 0) {


                    String url = "http://49.247.30.164" + chat.ThumbnailPath;
                    Glide.with(context).load(url).into(holder1.썸네일);
                    Log.i(TAG, "ThumbnailPath : " + chat.ThumbnailPath);

//                    MediaController mediaController = new MediaController();
//                    mediaController.


//                    Log.i("tag",Uri.fromFile(targetFile).toString());
//                    동영상.setMediaController(new MediaController(context));

//                    동영상.start();





                } else {

                    holder1.썸네일.setImageBitmap(createThumbnail(context, chat.videoUri.toString()));
                    Log.i(TAG, "videoUri : " + chat.videoUri.toString());

                }


            }




            if(chat.UnReadChat){
                holder1.안읽음.setVisibility(View.VISIBLE);
            }
            else{
                holder1.안읽음.setVisibility(View.GONE);

            }


            if(chat.result != null && chat.result == false){

                Toast.makeText(context, "네트워크 상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
                holder1.시간.setVisibility(View.GONE);
//                holder1.시간.setVisibility(View.GONE);
                holder1.안읽음.setVisibility(View.GONE);
//                holder1.안읽음.setText("전송중...");

                /// 재전송 시도를 계속 한다.
                //
            }
//            else{
//
//                holder1.시간.setVisibility(View.VISIBLE);
//                holder1.안읽음.setVisibility(View.VISIBLE);
////                holder1.안읽음.setText("전송중...");
//            }

//            }


        } else if (chat.type == Chat.OTHERCHAT) {
//            TextView 시간, 닉네임, 메세지, 안읽음;
            Log.i(TAG, "Type : OTHERCHAT");




            OtherViewHolder holder1 = (OtherViewHolder) holder;




            // 이전에 보낸 채팅이 있고, 이전에 보낸 채팅이 내가 보낸 채팅이며, 지금 채팅과 시간이 같다면
            // 닉네임 제거, 프로필 안보이데/
            if (position > 0 && chat.type == itemList.get(position - 1).type
                    && getYMD(getDate(chat.date)).equals(getYMD(getDate(itemList.get(position-1).date)))
                    && getHourMin(getDate(chat.date)).equals(getHourMin(getDate(itemList.get(position-1).date)))) {
                holder1.닉네임.setVisibility(View.GONE);
                holder1.프로필이미지.setVisibility(View.GONE);
            }
            else{
                holder1.닉네임.setVisibility(View.VISIBLE);
                holder1.프로필이미지.setVisibility(View.VISIBLE);

                holder1.닉네임.setText(chat.name);
                Log.i(TAG, "name : " + chat.name);
                Log.i(TAG, "profile : " + chat.profile);

                if (chat.profile.length() != 0) {
                    String url = "http://49.247.30.164" + chat.profile;
                    Glide.with(context).load(url).into(holder1.프로필이미지);


                } else {
                    Drawable drawable;
                    drawable = context.getResources().getDrawable(R.drawable.baseline_person_24);
                    holder1.프로필이미지.setImageDrawable(drawable);
                }
            }

            // 이후 채팅이 있고, 내가 보냈고, 나와 시간이 같다면, 시간 삭제

            if(position+1 < itemList.size()
                    && itemList.get(position+1).type == Chat.OTHERCHAT
                    && getYMD(getDate(chat.date)).equals(getYMD(getDate(itemList.get(position+1).date)))
                    && getHourMin(getDate(chat.date)).equals(getHourMin(getDate(itemList.get(position+1).date)))) {
//                    && chat.date.equals(itemList.get(position+1).date)){
                holder1.시간.setVisibility(View.GONE);
            }
            else{
                holder1.시간.setVisibility(View.VISIBLE);
                holder1.시간.setText(getHourMin(getDate(chat.date)));
            }

//
//            if(position <  itemList.size()-1 && getYMD(getDate(itemList.get(position).date)).equals(getYMD(getDate(itemList.get(position+1).date))) && getHourMin(getDate(itemList.get(position).date)).equals(getHourMin(getDate(itemList.get(position+1).date))) && chat.type==itemList.get(position+1).type ){
//
//                holder1.시간.setVisibility(View.GONE);
//
//            }
//            else{
//                holder1.시간.setVisibility(View.VISIBLE);
//                holder1.시간.setText(getHourMin(getDate(chat.date)));
//
//            }

//            if(position > 0
//                    && getYMD(getDate(itemList.get(position).date)).equals(getYMD(getDate(itemList.get(position-1).date)))){
//
//                holder1.날짜.setVisibility(View.GONE);
//
//            }
//            else{
//                holder1.날짜.setVisibility(View.VISIBLE);
//                holder1.날짜.setText(getYMD(getDate(itemList.get(position).date)));
//            }

            // 마지막 채팅이거나
            // 이전 채팅과 년도가 다르면 보여주기
            if( position > 0 && !getYMD(getDate(itemList.get(position).date)).equals(getYMD(getDate(itemList.get(position-1).date)))){
                holder1.날짜.setVisibility(View.VISIBLE);
                holder1.날짜.setText(getYMD(getDate(itemList.get(position).date)));
            }
            else if(chat.isLastChat){
                holder1.날짜.setVisibility(View.VISIBLE);
                holder1.날짜.setText(getYMD(getDate(itemList.get(position).date)));

            }
            else{
                holder1.날짜.setVisibility(View.GONE);
            }


//            if(chat.result != null && chat.result == false){
//                holder1.날짜.setText("X");
//                holder1.날짜.setTextColor(Color.RED);
//                holder1.날짜.setVisibility(View.VISIBLE);
//            }
//            else{
//                holder1.날짜.setTextColor(Color.BLACK);
//            }

//            if(position > 0
//                    && getYMD(getDate(itemList.get(position).date)).equals(getYMD(getDate(itemList.get(position-1).date)))){
//
//                holder1.날짜.setVisibility(View.GONE);
//
//            }
//            else{
//                holder1.날짜.setVisibility(View.VISIBLE);
//                holder1.날짜.setText(getYMD(getDate(itemList.get(position).date)));
//            }


            if (chat.mediaType.equals(Chat.TEXT)) {
                holder1.메세지.setText(chat.message);
                holder1.메세지.setVisibility(View.VISIBLE);
                holder1.전송이미지.setVisibility(View.GONE);
//                holder1.동영상.setVisibility(View.GONE);
                holder1.동영상.setVisibility(View.GONE);
                holder1.썸네일.setVisibility(View.GONE);
                holder1.동영상재생.setVisibility(View.GONE);
                Log.i(TAG, "message : " + chat.message);

            } else if (chat.mediaType.equals(Chat.IMAGE)) {
                holder1.메세지.setVisibility(View.GONE);
                holder1.동영상.setVisibility(View.GONE);
                holder1.썸네일.setVisibility(View.GONE);
                holder1.동영상재생.setVisibility(View.GONE);
                Log.i(TAG, "imagePath : " + chat.imagePath);

                if (chat.imagePath.length() != 0) {

                    holder1.전송이미지.setVisibility(View.VISIBLE);

                    String url = "http://49.247.30.164" + chat.imagePath;
                    Glide.with(context).load(url).into(holder1.전송이미지);
                } else {
                    holder1.전송이미지.setVisibility(View.GONE);

                }
            }
//            else if(chat.mediaType.equals(Chat.VIDEO)){
//                holder1.전송이미지.setVisibility(View.GONE);
//                holder1.메세지.setVisibility(View.GONE);
//
//                holder1.동영상.setVisibility(View.VISIBLE);
//
//
//
//            }
            else if (chat.mediaType.equals(Chat.VIDEO)) {


                holder1.메세지.setVisibility(View.GONE);

                if (chat.videoUri == null) {

                    holder1.동영상.setVisibility(View.VISIBLE);
                    holder1.동영상재생.setVisibility(View.GONE);

                } else {

                    holder1.동영상재생.setVisibility(View.VISIBLE);
                    holder1.동영상.setVisibility(View.GONE);

                }


                holder1.전송이미지.setVisibility(View.GONE);
                holder1.썸네일.setVisibility(View.VISIBLE);

                Log.i(TAG, "ThumbnailPath : " + chat.ThumbnailPath);
                Log.i(TAG, "videoPath : " + chat.videoPath);

                if (chat.ThumbnailPath != null && chat.ThumbnailPath.length() != 0) {


                    String url = "http://49.247.30.164" + chat.ThumbnailPath;
                    Glide.with(context).load(url).into(holder1.썸네일);

//                    MediaController mediaController = new MediaController();
//                    mediaController.


//                    Log.i("tag",Uri.fromFile(targetFile).toString());
//                    동영상.setMediaController(new MediaController(context));

//                    동영상.start();


                }
//                else{
//
//                    holder1.썸네일.setImageBitmap(createThumbnail(context, chat.videoUri.toString()));
//
//                }


            }


//            holder1.시간.setText(chat.date);
//            holder1.메세지.setText(chat.message);


        } else {


        }
        Log.i(TAG, "--------------------------------------------------");

    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return itemList.get(position).type;
    }

    public class OtherViewHolder extends RecyclerView.ViewHolder {
        ImageView 프로필이미지, 전송이미지, 썸네일;
        TextView 시간, 닉네임, 메세지,날짜;
        // , 안읽음;

        ImageButton 동영상, 동영상재생;


        public OtherViewHolder(View view) {
            super(view);

            프로필이미지 = view.findViewById(R.id.imageView11);
            시간 = view.findViewById(R.id.textView7);
            날짜 = view.findViewById(R.id.textView60);

            메세지 = view.findViewById(R.id.textView6);
            닉네임 = view.findViewById(R.id.textView5);
            전송이미지 = view.findViewById(R.id.imageView15);

            동영상 = view.findViewById(R.id.imageButton21);
            동영상재생 = view.findViewById(R.id.imageButton22);


            썸네일 = view.findViewById(R.id.imageView19);


            전송이미지.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = getAdapterPosition();
                    Chat chat = itemList.get(position);

                    Intent intent = new Intent(context, ImageActivity.class);
                    intent.putExtra("image", chat.imagePath);
                    context.startActivity(intent);

                }
            });


            동영상.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Chat chat = itemList.get(position);
                    // 동영상 다운로드.
//                    동영상.setVisibility(View.GONE);
//                    동영상재생.setVisibility(View.VISIBLE);
                    이미지(chat.videoPath, getFileExtension(chat.videoPath), chat);
                }
            });
            동영상재생.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Chat chat = itemList.get(position);

                    Intent intent = new Intent(context, VideoActivity.class);
                    intent.putExtra("video", chat.videoUri.toString());

//                    Uri.parse()
                    context.startActivity(intent);
                }
            });


        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        ImageView 전송이미지, 썸네일;

        TextView 시간, 메세지, 안읽음,날짜;

        //        VideoView 동영상;
        ImageButton 동영상, 동영상재생;


        public MyViewHolder(View view) {
            super(view);

            날짜 = view.findViewById(R.id.textView9);
            시간 = view.findViewById(R.id.textView11);
            메세지 = view.findViewById(R.id.textView12);
            안읽음 = view.findViewById(R.id.textView67);

            전송이미지 = view.findViewById(R.id.imageView16);
            동영상 = view.findViewById(R.id.imageButton19);

            썸네일 = view.findViewById(R.id.imageView18);

            동영상재생 = view.findViewById(R.id.imageButton20);



            전송이미지.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = getAdapterPosition();
                    Chat chat = itemList.get(position);

                    Intent intent = new Intent(context, ImageActivity.class);
                    intent.putExtra("image", chat.imagePath);
                    context.startActivity(intent);

                }
            });

            // 동영상 다운로드 버튼
            동영상.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Chat chat = itemList.get(position);
                    // 동영상 다운로드.
//                    동영상.setVisibility(View.GONE);
//                    동영상재생.setVisibility(View.VISIBLE);
                    이미지(chat.videoPath, getFileExtension(chat.videoPath), chat);

                }
            });



            동영상재생.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = getAdapterPosition();
                    Chat chat = itemList.get(position);

                    Intent intent = new Intent(context, VideoActivity.class);
                    intent.putExtra("video", chat.videoUri.toString());

//                    Uri.parse()
                    context.startActivity(intent);
                }
            });

        }

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


    public Bitmap createThumbnail(Context activity, String path) {

        MediaMetadataRetriever retriever = null;
        Bitmap bitmap = null;

        try {
            retriever = new MediaMetadataRetriever();
            retriever.setDataSource(activity, Uri.parse(path));
            bitmap = retriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (retriever != null) {
                try {
                    retriever.release();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return bitmap;
    }

    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMddhhmmss");

    private String getTime() {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }


    public String getFileName(String path){

            return path.substring(7);
    }


    public Uri isExistFile(String fileName,String path){

        Log.i(TAG,"------------------------------------------------");

        Log.i(TAG,"isExistFile() 실행.");


        Log.i(TAG,"fileName : "+fileName);
        Log.i(TAG,"path : "+path);

          //        getExternalStoragePublicDirectory
        String outputFilePath = Environment.getExternalStoragePublicDirectory(
//            Environment.DIRECTORY_DOWNLOADS + "/저장할 폴더 이름") + "/저장할 파일 이름.txt";
                Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + fileName;
//        outputFilePath =
////    outputFilePath = "/document/raw:/storage/emulated/0/Download/ImaShare202311281214575151.mp4";
//
        Log.i(TAG,"outputFilePath : "+outputFilePath);
//                getExernalFilesDir
//                Environment.getEx
//        File targetFile = new File(Environment.getExernalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);


//        File targetFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),fileName);
        File targetFile = new File(outputFilePath);


        Uri uri1 = Uri.fromFile(targetFile);
        Log.i("uri : ",uri1.toString());
        Log.i("targetFile.canExecute() : ",Boolean.toString(targetFile.canExecute()));


        if(targetFile.exists()){

            Log.i(TAG,"파일있음");
            Log.i(TAG,"------------------------------------------------");

            Uri uri = Uri.fromFile(targetFile);
            return uri;
        }
        else{
            Log.i(TAG,"파일없음");
            Log.i(TAG,"------------------------------------------------");

            return null;
        }


    }


//    private String outputFilePath = Environment.getExternalStoragePublicDirectory(
////            Environment.DIRECTORY_DOWNLOADS + "/저장할 폴더 이름") + "/저장할 파일 이름.txt";
//            Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + getTime() + ".";

    public void 이미지(String path, String extension, Chat chat) {


//        String path = "/Image/chat2023-10-26 00:35:18.7150";
//        String path = "/Image/chat2023-10-26 01:44:22.6830";


//        String path = "/Image/chat4.mp4";
//        String extension = getFileExtension(path);


        String fileName = getFileName(path);


        String outputFilePath = Environment.getExternalStoragePublicDirectory(
//            Environment.DIRECTORY_DOWNLOADS + "/저장할 폴더 이름") + "/저장할 파일 이름.txt";
                Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + fileName;



        NetworkManager.CallbackFunc<ResponseBody> 이미지받아오기;
        이미지받아오기 = new NetworkManager.CallbackFunc<ResponseBody>() {
            @Override
            public void onResponseSuccess(ResponseBody data) {


//                MediaFormat mediaFormat = MediaFormat.createVideoFormat(MediaFormat.KEY_MIME, 320, 240);//                동영상.add
//                동영상.addSubtitleSource(data.byteStream(),mediaFormat);
//                data.contentLength()
//


                File targetFile;
                try {


                    BufferedSource bufferedSource = data.source();

                    if (bufferedSource != null) {

                        targetFile = new File(outputFilePath);

                        Log.i(TAG,"다운파일 : "+outputFilePath);
                        Log.i(TAG,"파일 있음? : "+Boolean.toString(targetFile.exists()));


                        //                        targetFile.
                        OutputStream outStream = new FileOutputStream(targetFile);

//                        int readLength = is.available();
//                        byte[] buffer = new byte[readLength];
                        byte[] buffer = new byte[1024]; // 고정 크기의 버퍼 설정
                        int read;
                        while ((read = bufferedSource.read(buffer)) != -1) {
//                            outStream.
                            outStream.write(buffer, 0, read);

                        }

                        outStream.close();
                        Uri uri = Uri.fromFile(targetFile);
                        chat.videoUri = uri;
//                        Toast.makeText(context, "성공", Toast.LENGTH_SHORT).show();

                        Log.i("uri", uri.toString());
                        notifyDataSetChanged();
                    }


                    bufferedSource.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onResponseFail(ResponseBody ErrorBody) {

                Toast.makeText(context, "onResponseFail(", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNetworkError(Throwable t) {

                Toast.makeText(context, "onNetworkError(", Toast.LENGTH_SHORT).show();


            }
        };


        NetworkManager networkManager = new NetworkManager(context.getApplicationContext());
        networkManager.setCallback(이미지받아오기);
        networkManager.GETImage(path);

    }

    public String getFileExtension(String path) {
        int lastDotIndex = path.lastIndexOf(".");
        if (lastDotIndex != -1) {
            return path.substring(lastDotIndex + 1);
        }
        return ""; // 확장자가 없을 경우 빈 문자열 반환
    }


    public String getHourMin(Date date) {

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String str = format.format(date);
//        System.out.println(str);

        return str;
    }


    public String getYMD(Date date) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String str = format.format(date);
//        System.out.println(str);

        return str;
    }

    public Date getDate(String str) {
//        String str = "2019-09-02 08:10:55";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
//        System.out.println(date);
        return date;
    }

}
