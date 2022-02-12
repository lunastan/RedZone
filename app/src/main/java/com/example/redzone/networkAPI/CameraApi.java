package com.example.redzone.networkAPI;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import retrofit2.Call;

import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface CameraApi {

    String DJANGO_SITE="http://13.125.171.174:8000/";

    @Multipart
    @POST("/image/")
    Call<RequestBody> uploadImage(@Part MultipartBody.Part file);
}