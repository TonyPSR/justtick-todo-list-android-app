package com.myapp.tonypsr.to_doapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class activity_about extends AppCompatActivity {

    TextView text1,text2,text3,feedbackTextView;
    Button shareButton,instagramButton, facebookButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setTitle("About");

        text1 = (TextView)findViewById(R.id.textView1);
        text2 = (TextView)findViewById(R.id.textView2);
        feedbackTextView = (TextView)findViewById(R.id.feedbackTextView);
        instagramButton = (Button)findViewById(R.id.instagramButton);
        facebookButton = (Button)findViewById(R.id.facebookButton);
        shareButton = (Button)findViewById(R.id.shareButton);


        Typeface productSans = Typeface.createFromAsset(getAssets(), "fonts/ProductSansRegular.ttf");
        Typeface productSansBold = Typeface.createFromAsset(getAssets(), "fonts/ProductSansBold.ttf");
        Typeface productSansItalic = Typeface.createFromAsset(getAssets(), "fonts/ProductSansItalic.ttf");
        Typeface productSansBoldItalic = Typeface.createFromAsset(getAssets(), "fonts/ProductSansBoldItalic.ttf");

        text1.setTypeface(productSans);
        text2.setTypeface(productSans);
        shareButton.setTypeface(productSansBold);
        instagramButton.setTypeface(productSansItalic);
        facebookButton.setTypeface(productSansItalic);
        feedbackTextView.setTypeface(productSans);


        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String shareBody = "Checkout this app: https://play.google.com/store/apps/details?id=com.myapp.tonypsr.to_doapp";
                String shareSubject = "s.tonypsr@outlook.com";
                myIntent.putExtra(Intent.EXTRA_SUBJECT,shareSubject);
                myIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(myIntent, "Share Using"));
            }
        });

        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browersIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/tonypsr007"));
                startActivity(browersIntent);
            }
        });
        instagramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browersIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/tony_psr"));
                startActivity(browersIntent);
            }
        });
    }


    @Override
    public void onBackPressed() {
        finish();
        return;
    }
}
