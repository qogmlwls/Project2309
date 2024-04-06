package com.example.project2309.network;

import android.content.Context;

import com.example.project2309.data.Data;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkManager {



    private static final String BASE_URL = "http://49.247.30.164/";
//    private Retrofit retrofit;
    private SharedPreferencesManager sharedPreferencesManager;
    private ApiService apiService;



    // Callback.java
    public interface CallbackFunc<T> {
//        void onResponseSuccess(Data data);
        void onResponseSuccess(T data);

        void onResponseFail(ResponseBody ErrorBody);

        void onNetworkError(Throwable t);

    }


    private CallbackFunc callback;

    public void setCallback(CallbackFunc<?> callback) {
        this.callback = callback;
    }

    private static Retrofit retrofit = null;




    private Retrofit getRetrofitInstance(Context applicationContext) {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new CookieInterceptor(applicationContext))
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }


    private ApiService getApiServiceInstance(){
        if(apiService == null){
//            retrofit.create(ApiService<Data,P>)

//            retrofit.create(ApiService<Data,PostData>.class);
            apiService = retrofit.create(ApiService.class);

        }
        return apiService;
    }


    private <T> void enqueueCall(Call<T> call) {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful()) {
                    String sessionCookie = response.headers().get("Set-Cookie");
                    if (sessionCookie != null) {
                        sharedPreferencesManager.saveSessionCookie(sessionCookie);
                    }
                    callback.onResponseSuccess(response.body());
                } else {
                    callback.onResponseFail(response.errorBody());

//                    response.
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                callback.onNetworkError(t);
            }
        });
    }

    public NetworkManager(Context context){
        sharedPreferencesManager = new SharedPreferencesManager(context);
        retrofit = getRetrofitInstance(context);
        apiService = getApiServiceInstance();
    }


    public void deleteRequest(String category, String path, String body) {

        Call<Data> call = apiService.DELETE(category, path, body);
        enqueueCall(call);

    }

    public void postRequest(String category, String path, JsonObject body) {

        Call<Data> call = apiService.POST(category, path, body);
        enqueueCall(call);

    }
//


    public void post2Request(String category, String path, JsonObject body) {

        Call<Data> call = apiService.POST2(category, path, body);
        enqueueCall(call);

    }
    //
    public void GETRequest(String category, String path) {

        Call<Data> call = apiService.GET(category,path);

        enqueueCall(call);
    }

    public void GETRequest3(String category, String path, JsonObject data) {

        Call<Data> call = apiService.GET3(category,path,data);

        enqueueCall(call);
    }



    public void GETRequest(String category, String path,String query) {

        Call<Data> call = apiService.GET(category,path,query);

        enqueueCall(call);


    }


    public void GETRequest(String category, String path,JsonObject query) {

        Call<Data> call = apiService.GET(category,path,query);

        enqueueCall(call);


    }

    public void GET2Request(String category, String path,String query) {

        Call<Data> call = apiService.GET2(category,path,query);
//        Call<Data> call = apiService.GET2(category,path);
//        Call<Data> call = apiService.GET2(path);
        enqueueCall(call);

    }

//    public void GET2Request(String category, String path) {
//
//        Call<Data> call = apiService.GET2(category,path,"");
////        Call<Data> call = apiService.GET2(category,path);
////        Call<Data> call = apiService.GET2(path);
//        enqueueCall(call);
//
//    }
//


//
//    public void GETRequest(String category, String path,String query) {
//
//        Call<Data> call = apiService.GET(category,path,query);
//
//        enqueueCall(call);
//    }
//



//    public <T> void GETRequest(Class<T> activityClass,String category, String path) {
//
//        Call<T> call = (Call<activityClass>) apiService.GET(category,path);
//
//        enqueueCall(call);
//    }

    public void GETImage(String path) {

        String imageUrl = BASE_URL+path;
        Call<ResponseBody> call = apiService.downloadImage(imageUrl);


        enqueueCall(call);
    }

    public void postRequest(MultipartBody.Part image, RequestBody path, RequestBody body,RequestBody body2) {

        Call<Data> call = apiService.uploadImage(image, path, body, body2);
        enqueueCall(call);

    }
    public void postRequest(List<MultipartBody.Part> images, RequestBody body, RequestBody body2) {


//        posts/createPost
        Call<Data> call = apiService.uploadImages("posts","createPost",images,body,body2);
        enqueueCall(call);

    }



    public void postRequest(String catagory, String path ,List<MultipartBody.Part> images, RequestBody body, RequestBody body2) {


//        posts/createPost
        Call<Data> call = apiService.uploadImages(catagory,path,images,body,body2);
        enqueueCall(call);

    }
    public void postRequest(String catagory, String path , List<MultipartBody.Part> images, RequestBody body, RequestBody body2, RequestBody body3) {

        Call<Data> call = apiService.uploadImages(catagory, path, images,body,body2,body3);
        enqueueCall(call);

    }
}
