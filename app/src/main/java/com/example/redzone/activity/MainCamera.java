package com.example.redzone.activity;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.redzone.R;
import com.example.redzone.networkAPI.CameraApi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainCamera extends  AppCompatActivity {
    Button bt_take_image;
    ImageView imageView;
    private Bitmap bitmap;
    private int IMG_REQUEST = 21;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_main);
        take_an_image();
    }

    public void take_an_image() {
        setContentView(R.layout.camera_main);

        //Assign Variable
        imageView = findViewById(R.id.imageview);
        bt_take_image = findViewById(R.id.bt_take_image);

        //Request For Camera permission 카메라 허가 요청
        if (ContextCompat.checkSelfPermission(MainCamera.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainCamera.this, new String[]{
                    Manifest.permission.CAMERA
            }, 100);
        }

        bt_take_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open camera 카메라 열기
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null) {

            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == 100) {
            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(captureImage);

            upload_an_image(captureImage);
        }


    }

    public void upload_an_image(Bitmap captureImage) {
        new AlertDialog.Builder(MainCamera.this)
                .setMessage("촬영된 이미지를 서버로 전송합니다. \n전송하시겠습니까?")
                .setPositiveButton("보내기", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "확인 누름", Toast.LENGTH_SHORT).show();
                        uploadphoto(captureImage);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "취소 누름", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    private void uploadphoto(Bitmap captureImage) {
        System.out.println("보내기 클릭");
        File imageFile = new File(saveBitmapToJpg(captureImage, "iamfromAndroidStudio!!"));
        System.out.println(imageFile.toString());


        Retrofit retrofit = new Retrofit.Builder().baseUrl(CameraApi.DJANGO_SITE).addConverterFactory(GsonConverterFactory.create()).build();
        CameraApi api = retrofit.create(CameraApi.class);

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/data"), imageFile);

        MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("model_pic", imageFile.getName(), requestBody);

        Call<RequestBody> call = api.uploadImage(multipartBody);

        call.enqueue(new Callback<RequestBody>() {
            @Override
            public void onResponse(Call<RequestBody> call, Response<RequestBody> response) {
                Log.d("good", "good");
                System.out.println(requestBody);
            }

            @Override
            public void onFailure(Call<RequestBody> call, Throwable t) {
                Log.d("fail", ""+t.getMessage());

            }
        });


    }

    public String saveBitmapToJpg(Bitmap bitmap, String name) {
        File storage = getCacheDir();
        String fileName = name + ".jpg";
        File imgFile = new File(storage, fileName);

        try {
            imgFile.createNewFile();
            FileOutputStream out = new FileOutputStream(imgFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
        } catch (FileNotFoundException e) {
            Log.e("saveBitmapToJpg", "FileNotFoundException: " + e.getMessage());
        } catch (IOException e) {
            Log.e("saveBitmapToJpg", "IOException: " + e.getMessage());
        }
        Log.d("imgPath", getCacheDir() + "/" + fileName);
        return getCacheDir() + "/" + fileName;

    }
}