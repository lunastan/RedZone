package com.example.redzone.activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class MainRegister extends AppCompatActivity {
    private EditText mNameView;
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mRepasswordView;
    private Button mRegisterButton;
    private ServiceApi service;

    private ProgressBar mProgressView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_main);

        mNameView = (EditText) findViewById(R.id.name);
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mRepasswordView = (EditText) findViewById(R.id.repassword);
        mRegisterButton = (Button) findViewById(R.id.RegisterBtn);

        mProgressView = (ProgressBar) findViewById(R.id.register_progress);

        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServiceApi.DJANGO_SITE)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        service = retrofit.create(ServiceApi.class);


        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptJoin();
            }
        });
    }


    private void attemptJoin() {
        mNameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mRepasswordView.setError(null);

        String name = mNameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String repassword= mRepasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // 이름의 유효성 검사
        if (name.isEmpty()) {
            mNameView.setError("이름을 입력해주세요.");
            focusView = mNameView;
            cancel = true;
        }

        // 이메일의 유효성 검사
        if (email.isEmpty()) {
            mEmailView.setError("이메일을 입력해주세요.");
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError("@를 포함한 유효한 이메일을 입력해주세요.");
            focusView = mEmailView;
            cancel = true;
        }
        else if(!isEmailValid2(email)) {
            mEmailView.setError(".를 포함한 유효한 이메일 형식으로 입력해주세요.");
            focusView = mEmailView;
            cancel = true;
        }
        // 패스워드1의 유효성 검사
        if (password.isEmpty()) {
            mPasswordView.setError("비밀번호를 입력해주세요.");
            focusView = mPasswordView;
            cancel = true;
        }
        // 패스워드2의 유효성 검사
        if (repassword.isEmpty()) {
            mRepasswordView.setError("비밀번호를 입력해주세요.");
            focusView = mRepasswordView;
            cancel = true;
        }
        //비밀번호가 일치하지 않을 경우
        if(!(mPasswordView.getText().toString().equals(mRepasswordView.getText().toString()))){
            mRepasswordView.setError("비밀번호가 일치하지 않습니다. 다시 입력해주세요");
            focusView = mRepasswordView;
            cancel = true;
        }


        if (cancel) {
            focusView.requestFocus();
        } else {

            startJoin(name,email,password,repassword);
            showProgress(true);
        }
    }

    private void startJoin(String name, String email,String password,String repassword){
        Call<ResponseBody> call = service.addUser(name, email, password, repassword);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                System.out.println(response);
                int code = Integer.parseInt(response.toString().split("=")[2].split(",")[0]);

                if (code == 200){
                    Toast.makeText(MainRegister.this, "회원가입이 완료되었습니다. 축하합니다.", Toast.LENGTH_SHORT).show();
                    Log.d("Success","Successssssssssssssssssss");
                    showProgress(false);
                    finish();
                } else {
                    Toast.makeText(MainRegister.this, "이미 존재하는 아이디 입니다.", Toast.LENGTH_SHORT).show();
                    showProgress(false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainRegister.this, "서버 통신 에러. 네트워크를 확인하세요.", Toast.LENGTH_SHORT).show();
                Log.e("회원가입 에러 발생", t.getMessage());
                showProgress(false);
                finish();//엑티비티의 종료(라이브러리임)
            }
        });


    }

    private boolean isEmailValid(String email) { return email.contains("@"); }
    private boolean isEmailValid2(String email) { return email.contains("."); }
    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE );
    }
}