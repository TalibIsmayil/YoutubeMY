package com.talib.youtuberx.youtubemy;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.talib.youtuberx.youtubemy.Common.Common;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.Random;

import info.hoang8f.widget.FButton;

public class StartActivity extends AppCompatActivity {

    private FButton btnPlay;

    private RelativeLayout masa;
//    private AnimationDrawable animationDrawable;
    private InterstitialAd mAd;
    private TextView aydin;

    private FirebaseDatabase database;
    private DatabaseReference question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mAd = new InterstitialAd(this);
        mAd.setAdUnitId("ca-app-pub-3159482392412970/2701527026");
        AdRequest request = new AdRequest.Builder().build();
        mAd.loadAd(request);

        AdView adView = (AdView)findViewById(R.id.adView5);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        aydin = (TextView)findViewById(R.id.aydin);
        final Typeface mA = Typeface.createFromAsset(getAssets(),"fonts/Quicksand-Bold.ttf");
        aydin.setTypeface(mA);

        masa = (RelativeLayout)findViewById(R.id.masa);
//        animationDrawable = (AnimationDrawable)masa.getBackground();
//        animationDrawable.setEnterFadeDuration(5500);
//        animationDrawable.setExitFadeDuration(5500);
//        animationDrawable.start();

        database = FirebaseDatabase.getInstance();
        question = database.getReference("Questions");
        question.keepSynced(true);
        
        loadQuestions(Common.categoryId);

        btnPlay = (FButton)findViewById(R.id.playGame);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAd.isLoaded()) {
                    mAd.show();
                    mAd.setAdListener(new AdListener() {

                        @Override

                        public void onAdClosed() {
                            Intent intent = new Intent(getApplicationContext(), PlayingActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    });
                }else {
                    Intent intent = new Intent(getApplicationContext(), PlayingActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    private void loadQuestions(String categoryId) {

        //First, clear list if have old questions
        if (Common.questionList.size()>0)
            Common.questionList.clear();

        question.orderByChild("categoryId").equalTo(categoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    Question question = postSnapshot.getValue(Question.class);
                    Common.questionList.add(question);
                    Collections.shuffle(Common.questionList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Random list
        Collections.shuffle(Common.questionList);
    }
}
