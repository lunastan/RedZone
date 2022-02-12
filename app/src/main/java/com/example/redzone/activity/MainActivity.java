package com.example.redzone.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.content.Intent;

import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.example.redzone.R;
import com.example.redzone.networkAPI.ServiceApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private EditText mUsername;
    private EditText mPassword2;
    private  Button mLoginBtn;
    private ServiceApi service;
    private ProgressBar mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsername = (EditText) findViewById(R.id.username);
        mPassword2 = (EditText) findViewById(R.id.password2);

        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServiceApi.DJANGO_SITE)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        service = retrofit.create(ServiceApi.class);


        Button toregister = (Button) findViewById(R.id.toRegisterBtn);

        toregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainRegister.class);
                startActivity(intent);
            }
        });


        mLoginBtn = (Button) findViewById(R.id.LoginBtn);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin(){
        mUsername.setError(null);
        mPassword2.setError(null);

        String username = mUsername.getText().toString();
        String password2 = mPassword2.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (username.isEmpty()) {
            mUsername.setError("이름을 입력해주세요.");
            focusView = mUsername;
            cancel = true;
        }
        if (password2.isEmpty()){
            mPassword2.setError("비밀번호를 입력해주세요.");
            focusView = mPassword2;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            startLogin(username, password2);
//            showProgress(true);
        }
    }

    private void startLogin(String username, String password){
        Call<ResponseBody> call = service.addLog(username, password);  // no error

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                System.out.println(response);
                int code = Integer.parseInt(response.toString().split("=")[2].split(",")[0]);
                if (code ==200){
                    Toast.makeText(MainActivity.this, "환영합니다 " + username + "님.", Toast.LENGTH_SHORT).show();
                    Log.d("Success","Successssssssssssssssssss");
                    Intent intent = new Intent(getApplicationContext(), MainResult.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "아이디 또는 비밀번호를 잘못 입력하셨습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("로그인 에러 발생", t.getMessage());
            }
        });



    }
}