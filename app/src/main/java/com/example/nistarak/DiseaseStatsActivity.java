package com.example.nistarak;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiseaseStatsActivity extends AppCompatActivity {

    String text="java android c python java";
    TextView myTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_stats);

        myTextView=(findViewById(R.id.textView16));

        //setting Textview Bold
        /*SpannableStringBuilder str = new SpannableStringBuilder("Your awesome text");
str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), INT_START, INT_END, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
TextView tv=new TextView(context);
tv.setText(str);*/

        final Pattern p = Pattern.compile("Java");
        final Matcher matcher = p.matcher(text);

        final SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        final ForegroundColorSpan span = new ForegroundColorSpan(Color.GREEN);
        while (matcher.find()) {
            spannable.setSpan(
                    span, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }
        myTextView.setText(spannable);
    }
}
