package com.example.nistarak;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class GovernmentActivity extends AppCompatActivity {

    Button btnSubmit;
    RetrofitClient retrofitClient;
    EditText etGovtName, etNotify, etGovtLoc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_government);
        retrofitClient = RetrofitClient.getInstance();

        etGovtLoc = findViewById(R.id.etGovtLoc);
        etGovtName = findViewById(R.id.etGovtName);
        etNotify = findViewById(R.id.etNotice);

        btnSubmit = findViewById(R.id.btnAdd);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification(etGovtLoc.getText().toString(),
                        etGovtName.getText().toString(),
                        etNotify.getText().toString());
            }
        });
    }

    void sendNotification(String loc, String name, String notice) {
        String token = "1551504985549";
        String target = "1,2,3,-4";
        notice = notice + " :: "+name;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait for a while");
        progressDialog.setMessage("Contacting server...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        Log.d("Token",token+" "+notice+" "+target+" "+loc);
        loc = loc.toLowerCase();
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .add_notification(token, notice, target, loc);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if(response.body() != null) {
                    Toast.makeText(GovernmentActivity.this, response.body().toString()+"Notification send successfully", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(GovernmentActivity.this, "No response from server", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(GovernmentActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
