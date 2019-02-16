package com.example.nistarak;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText name, email, password, uniqueId;
    private Button btnRegisterUser, btnRegisterHospital, btnRegisterGovernment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = (EditText) findViewById(R.id.etNewUsername);
        email = (EditText) findViewById(R.id.etNewEmail);
        password = (EditText) findViewById(R.id.etNewPassword);
        uniqueId = (EditText) findViewById(R.id.etNewAdhaar);

        btnRegisterUser = (Button)findViewById(R.id.btnRegisterUser);
        btnRegisterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _name = name.getText().toString();
                String _email = email.getText().toString();
                String _password = password.getText().toString();
                String _uniqueId = uniqueId.getText().toString();

                validateAndRegister(_name, _email, _password, _uniqueId, 0);
            }
        });

        btnRegisterHospital = (Button)findViewById(R.id.btnRegisterHospital);
        btnRegisterHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _name = name.getText().toString();
                String _email = email.getText().toString();
                String _password = password.getText().toString();
                String _uniqueId = uniqueId.getText().toString();

                validateAndRegister(_name, _email, _password, _uniqueId, 1);
            }
        });

        btnRegisterGovernment = (Button)findViewById(R.id.btnRegisterGovernment);
        btnRegisterGovernment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _name = name.getText().toString();
                String _email = email.getText().toString();
                String _password = password.getText().toString();
                String _uniqueId = uniqueId.getText().toString();

                validateAndRegister(_name, _email, _password, _uniqueId, 2);
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
                        Toast.makeText(RegisterActivity.this, s, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(RegisterActivity.this,"No response from server",Toast.LENGTH_SHORT);
                    }
                }
                catch (IOException e) {
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
