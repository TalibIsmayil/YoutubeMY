package com.talib.youtuberx.youtubemy;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.talib.youtuberx.youtubemy.Common.Common;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

import info.hoang8f.widget.FButton;

public class DoneActivity extends AppCompatActivity {

    private FButton btnTry;
    private TextView txtResultScore,getTxtResultQuestion;
    private ProgressBar progressBar;

    private InterstitialAd mAsd;

    private RelativeLayout hadi;
//    private AnimationDrawable animationDrawable;

    private FirebaseDatabase database;
    private DatabaseReference question_score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);

        mAsd = new InterstitialAd(this);
        mAsd.setAdUnitId("ca-app-pub-3159482392412970/1973509534");
        loadReIn();

        AdView adView = (AdView)findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        hadi = (RelativeLayout)findViewById(R.id.hadi);
//        animationDrawable = (AnimationDrawable)hadi.getBackground();
//        animationDrawable.setEnterFadeDuration(5500);
//        animationDrawable.setExitFadeDuration(5500);
//        animationDrawable.start();

        database = FirebaseDatabase.getInstance();
        question_score = database.getReference("Question_Score");
        question_score.keepSynced(true);

        txtResultScore = (TextView)findViewById(R.id.txtTotalScore);
        getTxtResultQuestion = (TextView)findViewById(R.id.txtTotalQuestion);
        progressBar = (ProgressBar)findViewById(R.id.doneProgressBar);
        btnTry = (FButton)findViewById(R.id.btnTryAgain);

        btnTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAsd != null && mAsd.isLoaded()) {
                    mAsd.show();
                    mAsd.setAdListener(new AdListener() {

                        @Override

                        public void onAdClosed() {
                            if (!mAsd.isLoading() && !mAsd.isLoaded()) {
                                AdRequest adRequest = new AdRequest.Builder().build();
                                mAsd.loadAd(adRequest);
                            }
                            finish();
                        }

                    });
                }else {
                    if (!mAsd.isLoading() && !mAsd.isLoaded()) {
                        AdRequest adRequest = new AdRequest.Builder().build();
                        mAsd.loadAd(adRequest);
                    }
                    finish();
                }
            }
        });

        //Get data from Bundle and set view
        Bundle extra = getIntent().getExtras();
        if (extra != null)
        {
            int score = extra.getInt("SCORE");
            int totalQuestion = extra.getInt("TOTAL");
            int correctAnswer = extra.getInt("CORRECT");

            txtResultScore.setText(String.format(Locale.getDefault(),"Xal : %d",score));
            getTxtResultQuestion.setText(String.format(Locale.getDefault(),  "Keçildi : %d / %d",correctAnswer,totalQuestion));

            progressBar.setMax(totalQuestion);
            progressBar.setProgress(correctAnswer);

            //Upload point to DB
            question_score.child(String.format("%s_%s", Common.currentUser.getUserName(),
                    Common.categoryId)).setValue(new QuestionScore(String.format("%s_%s",Common.currentUser.getUserName(),
                    Common.categoryId),
                    Common.currentUser.getUserName(),
                    String.valueOf(score),
                    Common.categoryId,
                    Common.categoryName));
        }
    }

    private void loadReIn() {
        if (!mAsd.isLoading() && !mAsd.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAsd.loadAd(adRequest);
        }
    }
}
