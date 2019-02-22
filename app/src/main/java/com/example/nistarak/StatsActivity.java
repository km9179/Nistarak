package com.example.nistarak;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class StatsActivity extends AppCompatActivity {

    private static final String GOOGLE_SERACH_URL = "https://www.google.com/search?q=";
    WebView webView1, webView2, webView3, webView4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        webView1 = findViewById(R.id.wvStat1);
        webView1.getSettings().setJavaScriptEnabled(true);
        webView1.loadUrl(GOOGLE_SERACH_URL + "india");

        webView2 = findViewById(R.id.wvStat2);
        webView2.getSettings().setJavaScriptEnabled(true);
        webView2.loadUrl("https://thingspeak.com/apps/plugins/275063?fbclid=IwAR3iyoKA5dXfwfkbAGvVULNQVW3FcyJUl-MmkhfsMjAt7MzOl1b_Ft14QNI");

        webView3 = findViewById(R.id.wvStat3);
        webView3.getSettings().setJavaScriptEnabled(true);
        webView3.loadUrl("https://thingspeak.com/apps/plugins/275063?fbclid=IwAR3iyoKA5dXfwfkbAGvVULNQVW3FcyJUl-MmkhfsMjAt7MzOl1b_Ft14QNI");

        webView4 = findViewById(R.id.wvStat4);
        webView4.getSettings().setJavaScriptEnabled(true);
        webView4.loadUrl("https://thingspeak.com/apps/plugins/275063?fbclid=IwAR3iyoKA5dXfwfkbAGvVULNQVW3FcyJUl-MmkhfsMjAt7MzOl1b_Ft14QNI");

    }
}
