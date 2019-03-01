package com.example.nistarak;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class UserInformation extends AppCompatActivity {

    private RetrofitClient retrofitClient;
    private TextView mName, mEmail, mAdhaar, mUserType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);

        retrofitClient = RetrofitClient.getInstance();

        mName = (TextView) findViewById(R.id.tvUsername);
        mEmail = (TextView) findViewById(R.id.tvEmail);
        mAdhaar = (TextView) findViewById(R.id.tvAdhaar);
        mUserType = (TextView) findViewById(R.id.tvUserType);
    }
}
