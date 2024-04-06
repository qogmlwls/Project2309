package com.example.project2309.data;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class Data {

    public static String PASS = "pass";
    public static String NOTPASS = "notpass";

    public static String SUCCESS = "success";
    public static String Fail = "fail";

    public static String ERROR = "error";
    public static String RESULT = "result";
    public static String ERRCODE = "errorCode";
    public static String ERRDESCRIPTION = "errorDescription";


    public static String REASONCODE = "reasonCode";
    public static String REASONDESCRIPTION = "reasonDescription";



    @SerializedName("result")
    public JsonObject result;


    // 로그인, 회원가입, 마이페이지 작업시 적용 안된부분임. 수정 필요.
    @SerializedName("reason")
    public JsonObject reason;


}