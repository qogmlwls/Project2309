package com.example.project2309.network;


import com.example.project2309.data.Data;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;



public interface ApiService {

    @POST("/api/{category}/{path}.php")
    Call<Data> POST2(
            @Path("category") String category,
            @Path("path") String path,
            @Body JsonObject body
    );


    @POST("/{category}/{path}.php")
    Call<Data> POST(
            @Path("category") String category,
            @Path("path") String path,
            @Body JsonObject body
    );

    @GET("/{category}/{path}.php")
    Call<Data> GET(@Path("category") String category, @Path("path") String path);


    @GET("/{category}/{path}.php")
    Call<Data> GET3(@Path("category") String category, @Path("path") String path, @Query("data") JsonObject data);


    @GET("/{category}/{path}.php")
    Call<Data> GET(@Path("category") String category, @Path("path") String path, @Query("last_post_id") String data);

    @GET("/{category}/{path}.php")
    Call<Data> GET(@Path("category") String category, @Path("path") String path, @Query("data") JsonObject data);

//("/api/{category}/{path}.php")
//    @GET("/api/comment/{path}.php")

    @GET("/api/{category}/{path}.php")
    Call<Data> GET2(@Path("category") String category,@Path("path") String path, @Query("data") String data);
//


    @GET
    Call<ResponseBody> downloadImage(@Url String imageUrl);



    @Multipart
    @POST("/User/updateProfile.php")  // 이 부분에 서버의 PHP 엔드포인트 URL을 입력합니다.
    Call<Data> uploadImage(
            @Part MultipartBody.Part imgFile,
            @Part("name") RequestBody description,
            @Part("introText") RequestBody description2,
            @Part("includeFile") RequestBody description3
    );


    @Multipart
//    @POST("/api/createPost.php")  // Replace with your server's API endpoint for image upload
//    @POST("/api/posts/createPost.php")  // Replace with your server's API endpoint for image upload
    @POST("/api/{category}/{path}.php")  // Replace with your server's API endpoint for image upload
    Call<Data> uploadImages(
            @Path("category") String category,
            @Path("path") String path,
            @Part List<MultipartBody.Part> images,
            @Part("comment") RequestBody description,
            @Part("file_cnt") RequestBody description2
    );


    @Multipart
//    @POST("/api/createPost.php")  // Replace with your server's API endpoint for image upload
//    @POST("/api/posts/createPost.php")  // Replace with your server's API endpoint for image upload
    @POST("/api/{category}/{path}.php")  // Replace with your server's API endpoint for image upload
    Call<Data> uploadImages(
            @Path("category") String category,
            @Path("path") String path,
            @Part List<MultipartBody.Part> images,
            @Part("post_id") RequestBody description,
            @Part("comment") RequestBody description2,
            @Part("file_cnt") RequestBody description3
    );


    @DELETE("/api/{category}/{path}.php")
    Call<Data> DELETE(@Path("category") String category,
                      @Path("path") String path,
                      @Query("data") String data);



}

