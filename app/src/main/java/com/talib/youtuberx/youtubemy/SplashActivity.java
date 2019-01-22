package com.talib.youtuberx.youtubemy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class SplashActivity extends AppCompatActivity {

    private ImageView logo;
    private TextView app;

    private RelativeLayout splash;
    //private AnimationDrawable animationDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



        splash = (RelativeLayout)findViewById(R.id.splash);
//        animationDrawable = (AnimationDrawable)splash.getBackground();
//        animationDrawable.setEnterFadeDuration(3500);
//        animationDrawable.setExitFadeDuration(3500);
//        animationDrawable.start();

        logo = (ImageView)findViewById(R.id.logo);
        app = (TextView)findViewById(R.id.app);
        Typeface mT = Typeface.createFromAsset(getAssets(),"fonts/CandyScript.ttf");
        app.setTypeface(mT);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(),SignActivity.class);
                startActivity(intent);
                finish();
            }
        },2000);
    }
}
