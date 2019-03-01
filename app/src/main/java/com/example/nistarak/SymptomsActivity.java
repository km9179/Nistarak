package com.example.nistarak;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SymptomsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.symptoms_activity);

        RadioGroup rgp = (RadioGroup) findViewById(R.id.radio_group);
        int buttons = 50;
        for (int i = 1; i <= buttons; i++) {
            RadioButton rbn = new RadioButton(this);
            rbn.setId(View.generateViewId());
            rbn.setText("RadioButton" + i);
            rgp.addView(rbn);
        }
    }
}

