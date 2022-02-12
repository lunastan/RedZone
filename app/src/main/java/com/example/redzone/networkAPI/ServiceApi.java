package com.example.redzone.networkAPI;

import okhttp3.ResponseBody;
import retrofit2.Call;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ServiceApi {
    String DJANGO_SITE = "http://13.125.171.174:8000/";

    @POST("/android/register/")
    @FormUrlEncoded
    Call<ResponseBody> addUser(@Field("username") String username,
                               @Field("email") String email,
                               @Field("password1") String password1,
                               @Field("password2") String password2
    );

    @POST("/android/login/")
    @FormUrlEncoded
    Call<ResponseBody> addLog(@Field("username") String username,
                              @Field("password") String password
    );
}