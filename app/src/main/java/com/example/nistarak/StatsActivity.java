package com.example.nistarak;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class StatsActivity extends AppCompatActivity {

    WebView webView1, webView2, webView3, webView4, webView5, webView6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        webView1 = findViewById(R.id.wvStat1);
        webView1.getSettings().setJavaScriptEnabled(true);
        webView1.loadUrl("https://thingspeak.com/channels/710959/charts/1?bgcolor=%23ffffff&color=%23d62020&dynamic=true&results=60&type=line&update=15");

        webView2 = findViewById(R.id.wvStat2);
        webView2.getSettings().setJavaScriptEnabled(true);
        webView2.loadUrl("https://thingspeak.com/apps/plugins/275063?fbclid=IwAR3iyoKA5dXfwfkbAGvVULNQVW3FcyJUl-MmkhfsMjAt7MzOl1b_Ft14QNI");

        webView3 = findViewById(R.id.wvStat3);
        webView3.getSettings().setJavaScriptEnabled(true);
        webView3.loadUrl("https://thingspeak.com/channels/710959/charts/5?bgcolor=%23ffffff&color=%23d62020&dynamic=true&results=60&type=line&update=15");

        webView4 = findViewById(R.id.wvStat4);
        webView4.getSettings().setJavaScriptEnabled(true);
        webView4.loadUrl("https://thingspeak.com/channels/710959/charts/6?bgcolor=%23ffffff&color=%23d62020&dynamic=true&results=60&type=line&update=15");

        webView5 = findViewById(R.id.wvStat5);
        webView5.getSettings().setJavaScriptEnabled(true);
        webView5.loadUrl("https://thingspeak.com/channels/710965/charts/2?bgcolor=%23ffffff&color=%23d62020&dynamic=true&results=60&type=line&update=15");

        webView6 = findViewById(R.id.wvStat5);
        webView6.getSettings().setJavaScriptEnabled(true);
        webView6.loadUrl("https://thingspeak.com/channels/710965/charts/4?bgcolor=%23ffffff&color=%23d62020&dynamic=true&results=60&type=line&update=15");

    }
}
