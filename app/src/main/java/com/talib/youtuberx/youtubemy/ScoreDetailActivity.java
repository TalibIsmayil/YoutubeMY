package com.talib.youtuberx.youtubemy;

import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.talib.youtuberx.youtubemy.ViewHolder.ScoreDetailViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ScoreDetailActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private String viewUser = "";

    private SwipeRefreshLayout swa;

    private FirebaseDatabase database;
    private DatabaseReference question_score;

    private RecyclerView scoreList;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseRecyclerAdapter<QuestionScore,ScoreDetailViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_detail);

        swa = (SwipeRefreshLayout)findViewById(R.id.swa);
        swa.setOnRefreshListener(this);

        AdView adView = (AdView)findViewById(R.id.adView4);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        database = FirebaseDatabase.getInstance();
        question_score = database.getReference("Question_Score");
        question_score.keepSynced(true);

        scoreList = (RecyclerView)findViewById(R.id.scoreList);
        scoreList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        scoreList.setLayoutManager(layoutManager);

        if (getIntent() != null)
            viewUser = getIntent().getStringExtra("viewUser");
        if (!viewUser.isEmpty())
            loadScoreDetail(viewUser);
    }

    private void loadScoreDetail(String viewUser) {

        adapter = new FirebaseRecyclerAdapter<QuestionScore, ScoreDetailViewHolder>(QuestionScore.class,
                R.layout.score_detail_layout,
                ScoreDetailViewHolder.class,
                question_score.orderByChild("user").equalTo(viewUser)) {
            @Override
            protected void populateViewHolder(ScoreDetailViewHolder viewHolder, QuestionScore model, int position) {
                viewHolder.txt_name1.setText(model.getCategoryName());
                viewHolder.text_score1.setText(model.getScore());
            }
        };
        adapter.notifyDataSetChanged();
        scoreList.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {
        loadScoreDetail(viewUser);
        adapter.notifyDataSetChanged();
        scoreList.setAdapter(adapter);
        if (swa.isRefreshing()){
            swa.setRefreshing(false);
        }
    }
}
