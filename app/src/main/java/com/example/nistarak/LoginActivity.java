package com.example.nistarak;

import android.app.ProgressDialog;
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
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    public static int level_activity,level_user;
    private String serverResponse = null;
    private EditText email, password;
    private Button loginUser, loginClient,loginGovt,btnRegister,login;
    private TextView _linkSignup;
    private RetrofitClient retrofitClient;
    private LinearLayout _loginLinlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        level_activity=0;


        email = (EditText) findViewById(R.id.tvEmail);
        password = (EditText) findViewById(R.id.input_password);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        _loginLinlay=(LinearLayout) findViewById(R.id.btn_linlay);
        _linkSignup=(TextView) findViewById(R.id.link_signup);
        loginClient = (Button) findViewById(R.id.btn_hosp);
        loginUser = (Button) findViewById(R.id.btn_pblc);
        loginGovt=(Button) findViewById(R.id.btn_govt);
        login=(Button) findViewById(R.id.btn_login);
        retrofitClient = RetrofitClient.getInstance();
        _loginLinlay.setVisibility(_loginLinlay.GONE);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _loginLinlay.setVisibility(_loginLinlay.VISIBLE);
                login.setEnabled(false);
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

            }
        });

        _linkSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _loginLinlay.setVisibility(_loginLinlay.VISIBLE);
                loginUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        level_user=0;
                        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

                    }
                });

                loginClient.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        level_user=1;
                        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                });

                loginGovt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        level_user=2;
                        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                });
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
