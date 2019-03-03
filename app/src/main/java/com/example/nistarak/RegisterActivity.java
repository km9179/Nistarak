package com.example.nistarak;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText name, email, password, uniqueId,re_password;
    private Button btnRegisterUser, btnRegisterHospital, btnRegisterGovernment,btn_signup;
    private TextView link_login;
    private LinearLayout _signuplinLay;
    private LoginActivity ologinActivity = new LoginActivity();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name = (EditText) findViewById(R.id.input_name);
        email = (EditText) findViewById(R.id.input_email);
        password = (EditText) findViewById(R.id.input_password);
        uniqueId = (EditText) findViewById(R.id.aadhar);
        re_password = (EditText) findViewById(R.id.input_reEnterPassword);
        btn_signup = (Button) findViewById(R.id.btn_signup);
        link_login=(TextView)findViewById(R.id.link_login);
        _signuplinLay = (LinearLayout) findViewById(R.id.btn_linlay_signup);
        btnRegisterUser = (Button)findViewById(R.id.btn_pblc_signup);
        if(ologinActivity.level_activity==0){
            _signuplinLay.setVisibility(_signuplinLay.GONE);
        }
        else
        {
            btn_signup.setVisibility(btn_signup.GONE);
            _signuplinLay.setVisibility(_signuplinLay.VISIBLE);

        }

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String _name = name.getText().toString();
                String _email = email.getText().toString();
                String _password = password.getText().toString();
                String _uniqueId = uniqueId.getText().toString();

                validateAndRegister(_name, _email, _password, _uniqueId,ologinActivity.level_user);
            }
        });

        link_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });
    }

    private void validateAndRegister(String mName, String mEmail, String mPassword, String mUniqueId, int level) {
        registerProcessWithRetrofit(mName, mEmail, mPassword, mUniqueId, level);
    }

    private void registerProcessWithRetrofit(String name, String email, String password, String uniqueId, int level) {
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .register(name, email, password, uniqueId, 0);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if(response.body() != null) {
                        String s = response.body().string();
                        JSONObject jsonObject = new JSONObject(s);

                        Toast.makeText(RegisterActivity.this, s, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(RegisterActivity.this,"No response from server",Toast.LENGTH_SHORT);
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
