package com.talib.youtuberx.youtubemy;

import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.squareup.picasso.Callback;
import com.talib.youtuberx.youtubemy.Common.Common;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import info.hoang8f.widget.FButton;

public class PlayingActivity extends AppCompatActivity implements View.OnClickListener,RewardedVideoAdListener{

    final static long INTERVAL = 1000;
    final static long TIMEOUT = 15000;
    int progerssvalue = 0;

    private  InterstitialAd inter;

    private RelativeLayout comeon;
    private AnimationDrawable animationDrawable;

    private CountDownTimer mCountDown;

    private MediaPlayer mp3;

    int stopPosition = 0;

    int index = 0,score = 0,thisQuestion = 0,totalQuestion,correctAnswer;

    private RewardedVideoAd mAd;

    private SeekBar progressBar;
    private ImageView question_image;
    private FButton btnA,btnB,btnC,btnD,dialog,bana;
    private TextView txtScore,txtQuestionNum;
    private VideoView question_video;
    private ProgressBar buffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        MobileAds.initialize(getApplicationContext(),"ca-app-pub-3159482392412970~8839150844");
        mAd = MobileAds.getRewardedVideoAdInstance(PlayingActivity.this);
        mAd.setRewardedVideoAdListener(PlayingActivity.this);
        loadRewAd();

        comeon = (RelativeLayout)findViewById(R.id.comeon);
        animationDrawable = (AnimationDrawable)comeon.getBackground();
        animationDrawable.setExitFadeDuration(4500);
        animationDrawable.start();

        mp3 = MediaPlayer.create(this,R.raw.wrong);
        txtScore = (TextView)findViewById(R.id.txtScore);
        txtQuestionNum = (TextView)findViewById(R.id.txtTotalQuestion);
        question_video = (VideoView)findViewById(R.id.question_video);
        question_image = (ImageView)findViewById(R.id.question_image);

        buffer = (ProgressBar)findViewById(R.id.buffer);
        progressBar = (SeekBar) findViewById(R.id.progressBar);


        dialog = (FButton)findViewById(R.id.dialog);
        bana = (FButton)findViewById(R.id.ban);
        bana.setEnabled(false);

        inter = new InterstitialAd(this);
        inter.setAdUnitId("ca-app-pub-3159482392412970/5414397803");
        loadInter();

        bana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mAd.isLoaded()){
                    mAd.show();
                    mCountDown.cancel();
                    bana.setEnabled(false);
                }
            }
        });


        dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (score>=50) {
                    if (inter != null && inter.isLoaded()) {
                        inter.show();
                        inter.setAdListener(new AdListener() {

                            @Override

                            public void onAdClosed() {
                                loadInter();
                                showQuestion(++index);
                                correctAnswer++;
                                score-=50;
                                txtScore.setText(String.format(Locale.getDefault(),"%d",score));
                            }

                        });
                    } else {
                        loadInter();
                        showQuestion(++index);
                        correctAnswer++;
                        score-=50;
                        txtScore.setText(String.format(Locale.getDefault(),"%d",score));
                    }
                }else{

                }
            }
        });

        btnA = (FButton)findViewById(R.id.btnAnswerA);
        btnB = (FButton)findViewById(R.id.btnAnswerB);
        btnC = (FButton)findViewById(R.id.btnAnswerC);
        btnD = (FButton)findViewById(R.id.btnAnswerD);

        btnA.setOnClickListener(this);
        btnB.setOnClickListener(this);
        btnC.setOnClickListener(this);
        btnD.setOnClickListener(this);
    }

    private void loadInter() {
        if (!inter.isLoading() && !inter.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            inter.loadAd(adRequest);
        }
    }

    private void loadRewAd(){
        if (!mAd.isLoaded()){
            mAd.loadAd("ca-app-pub-3159482392412970/6875218514",new AdRequest.Builder().build());
        }
    }

    @Override
    public void onClick(View v) {
        Button clickedButton = (Button)v;
        mCountDown.cancel();
        if (index<totalQuestion)
        {
            if (clickedButton.getText().equals(Common.questionList.get(index).getDu()))
            {

                score+=10;
                correctAnswer++;
                showQuestion(++index);


            }
            else
            {
                mp3.start();
                Intent intent = new Intent(this,DoneActivity.class);
                Bundle dataSend = new Bundle();
                dataSend.putInt("SCORE",score);
                dataSend.putInt("TOTAL",totalQuestion);
                dataSend.putInt("CORRECT",correctAnswer);
                intent.putExtras(dataSend);
                startActivity(intent);
                finish();


            }
            txtScore.setText(String.format(Locale.getDefault(),"%d",score));

        }

    }

    private void showQuestion(int index) {
        if (index<totalQuestion)
        {
            thisQuestion++;
            progressBar.setProgress(0);
            progerssvalue = 0;

            if (Common.questionList.get(index).getSek().equals("true"))
            {
                //If it is image
                buffer.setVisibility(View.VISIBLE);
                Picasso.get().load(Common.questionList.get(index).getSul()).into(question_image, new Callback() {
                    @Override
                    public void onSuccess() {
                        buffer.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
                question_image.setVisibility(View.VISIBLE);
                question_video.setVisibility(View.INVISIBLE);

                btnA.setText(Common.questionList.get(index).getBri());
                btnB.setText(Common.questionList.get(index).getKi());
                btnC.setText(Common.questionList.get(index).getCu());
                btnD.setText(Common.questionList.get(index).getDor());

                mCountDown.start();

            }
            else {
                //if text
                buffer.setVisibility(View.VISIBLE);
                question_video.setVideoURI(Uri.parse(Common.questionList.get(index).getSul()));
                question_video.requestFocus();

                question_video.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int i, int extra) {
                        if (i == mp.MEDIA_INFO_BUFFERING_START) {

                            buffer.setVisibility(View.VISIBLE);

                        }else if (i == mp.MEDIA_INFO_BUFFERING_END){

                            buffer.setVisibility(View.INVISIBLE);

                        }
                        return false;
                    }
                });

                question_video.start();
                //question_video.setText(Common.questionList.get(index).getSul());
                question_image.setVisibility(View.INVISIBLE);
                question_video.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);

                btnA.setText(Common.questionList.get(index).getBri());
                btnB.setText(Common.questionList.get(index).getKi());
                btnC.setText(Common.questionList.get(index).getCu());
                btnD.setText(Common.questionList.get(index).getDor());

                mCountDown.cancel();

            }
        }
        else
        {
            //If it is final question
            Intent intent = new Intent(this,DoneActivity.class);
            Intent kec = new Intent(this,FavoriteFragment.class);
            Bundle dataSend = new Bundle();
            dataSend.putInt("SCORE",score);
            dataSend.putInt("TOTAL",totalQuestion);
            dataSend.putInt("CORRECT",correctAnswer);
            intent.putExtras(dataSend);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        mAd.destroy(this);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        mAd.pause(this);
        super.onPause();
        mCountDown.cancel();
        stopPosition = question_video.getCurrentPosition(); //stopPosition is an int
        question_video.pause();
    }

    @Override
    protected void onResume() {
        mAd.resume(this);
        super.onResume();
        question_video.seekTo(stopPosition);
        question_video.start();
        totalQuestion = Common.questionList.size();

        mCountDown = new CountDownTimer(TIMEOUT,INTERVAL) {
            @Override
            public void onTick(long minisec) {
                progressBar.setProgress(progerssvalue);
                progerssvalue++;
            }

            @Override
            public void onFinish() {
                mCountDown.cancel();
                showQuestion(++index);
            }
        };
        showQuestion(index);
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        bana.setEnabled(true);
    }

    @Override
    public void onRewardedVideoAdOpened() {


    }

    @Override
    public void onRewardedVideoStarted() {


    }

    @Override
    public void onRewardedVideoAdClosed() {
        question_video.start();
        loadRewAd();
        txtScore.setText(String.format(Locale.getDefault(),"%d",score));
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        score+=75;
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        loadRewAd();
    }

    @Override
    public void onRewardedVideoCompleted() {
        txtScore.setText(String.format(Locale.getDefault(),"%d",score));
    }
}
