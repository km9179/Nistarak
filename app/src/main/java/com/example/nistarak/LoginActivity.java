package com.example.nistarak;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private String serverResponse = null;
    private EditText email, password;
    private Button loginUser, loginClient, btnRegister;
    private RetrofitClient retrofitClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.etEmail);
        password = (EditText) findViewById(R.id.etPassword);
        retrofitClient = RetrofitClient.getInstance();

        loginUser = (Button) findViewById(R.id.btnLoginUser);
        loginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(email.getText() == null || password.getText() == null) {
                    Toast.makeText(LoginActivity.this, "Please enter Email and Password", Toast.LENGTH_LONG);
                }
                else {
                    String _email = email.getText().toString();
                    String _password = password.getText().toString();
                    validateAndLoginUser(_email, _password);
                }
            }
        });

        loginClient = (Button) findViewById(R.id.btnLoginClient);
        loginClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText() == null || password.getText() == null) {
                    Toast.makeText(LoginActivity.this, "Please enter Email and Password", Toast.LENGTH_LONG);
                }
                else {
                    String _email = email.getText().toString();
                    String _password = password.getText().toString();
                    validateAndLoginClient(_email, _password);
                }
            }
        });

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void validateAndLoginUser(String mEmail, String mPassword) {
        loginProcessWithRetrofit(mEmail, mPassword, 0);
    }

    private void validateAndLoginClient(String mEmail, String mPassword) {
        loginProcessWithRetrofit(mEmail, mPassword, 1);
    }

    private void loginProcessWithRetrofit(String email, String password, final int id) {
        Toast.makeText(LoginActivity.this,"Sorry bud", Toast.LENGTH_LONG);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Hold while we you log in");
        progressDialog.setMessage("Contacting server...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .login(email, password);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                try {
                    if(response.body() != null) {
                        serverResponse = response.body().string();
                        Toast.makeText(LoginActivity.this, serverResponse+" received", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "No response from server", Toast.LENGTH_SHORT);
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                if(serverResponse != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(serverResponse);
                        retrofitClient.reset();

                        if(serverResponse.contains("errcode"))
                            retrofitClient.errcode = jsonObject.getInt("errcode");
                        if(serverResponse.contains("success"))
                            retrofitClient.success = jsonObject.getInt("success");
                        if(serverResponse.contains("token"))
                            retrofitClient.token = jsonObject.getString("token");

                        if(retrofitClient.token != null) {
                            finish();
                            if(id == 0) {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            }
                            else {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            }
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
