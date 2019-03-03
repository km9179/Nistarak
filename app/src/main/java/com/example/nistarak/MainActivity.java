package com.example.nistarak;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btnMap, btnLogin, btnRegister, btnAdd, btnStats, btnStats2, btnNgo, btnSymptoms,
    btnStatsReport, btnDiseaseStats, btnGovt;
    private LoginActivity oLoginActivity = new LoginActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGovt = findViewById(R.id.btnGovt);
        btnGovt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GovernmentActivity.class));
            }
        });


        btnDiseaseStats = findViewById(R.id.btnDisease);
        btnDiseaseStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DiseaseStatsActivity.class));
            }
        });

        btnStatsReport = findViewById(R.id.btnStatsReport);
        btnStatsReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StatsReportActivity.class));
            }
        });

        btnSymptoms = findViewById(R.id.btnSymptoms);
        btnSymptoms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SymptomsActivity.class));
            }
        });

        btnMap = findViewById(R.id.btnMap);

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UserActivity.class));
            }
        });

        btnNgo = findViewById(R.id.btn_ngo);
        btnNgo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Maps2Activity.class));
            }
        });

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });



        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

        btnAdd = findViewById(R.id.btnRegisterNewCase);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // startActivity(new Intent(MainActivity.this, ReportDiseaseCase.class));
                startActivity(new Intent(MainActivity.this, ReportDiseaseCase.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        btnStats = findViewById(R.id.btnStats);
        btnStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StatsActivity.class));
            }
        });

    }
}
