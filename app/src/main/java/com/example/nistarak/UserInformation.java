package com.example.nistarak;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class UserInformation extends AppCompatActivity {

    private RetrofitClient retrofitClient;
    private EditText mName, mPhone, mAddress;
    private Button btnSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);

        retrofitClient = RetrofitClient.getInstance();

        mName = (EditText) findViewById(R.id.etUsername);
        mPhone = (EditText) findViewById(R.id.etPhone);
        mAddress = (EditText) findViewById(R.id.etAddress);

        btnSave = (Button) findViewById(R.id.btnSaveUserInformation);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrofitClient.name = mName.getText().toString();
                retrofitClient.phone = mPhone.getText().toString();
                retrofitClient.address = mAddress.getText().toString();
            }
        });
    }
}
