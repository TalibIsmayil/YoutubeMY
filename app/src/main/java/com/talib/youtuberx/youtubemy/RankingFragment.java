package com.talib.youtuberx.youtubemy;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.talib.youtuberx.youtubemy.Common.Common;
import com.talib.youtuberx.youtubemy.Interface.ItemClickListener;
import com.talib.youtuberx.youtubemy.Interface.RankingCallBack;
import com.talib.youtuberx.youtubemy.ViewHolder.RankingViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class RankingFragment extends Fragment{
    View mFragment;

    private InterstitialAd mAsass;

    private SwipeRefreshLayout swaw;

    private RecyclerView rankingList;
    private LinearLayoutManager layoutManager;
    private FirebaseRecyclerAdapter<Ranking,RankingViewHolder> adapter;

    int sum=0;

    private FirebaseDatabase database;
    private DatabaseReference questionScore,rankingTbl,num;

    public static RankingFragment newInstance(){
        RankingFragment rankingFragment = new RankingFragment();
        return rankingFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        questionScore = database.getReference("Question_Score");
        rankingTbl = database.getReference("Ranking");
        questionScore.keepSynced(true);
        rankingTbl.keepSynced(true);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragment = inflater.inflate(R.layout.fragment_ranking,container,false);

        mAsass = new InterstitialAd(getContext());
        mAsass.setAdUnitId("ca-app-pub-3159482392412970/9551690618");
        loadInters();

        AdView adView = (AdView)mFragment.findViewById(R.id.adView7);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        rankingList = (RecyclerView)mFragment.findViewById(R.id.rankingList);
        layoutManager = new LinearLayoutManager(getActivity());
        rankingList.setHasFixedSize(true);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        rankingList.setLayoutManager(layoutManager);


        updateScore(Common.currentUser.getUserName(), new RankingCallBack<Ranking>() {
            @Override
            public void callBack(Ranking ranking) {
                rankingTbl.child(ranking.getUserName()).setValue(ranking);
            }
        });


        adapter = new FirebaseRecyclerAdapter<Ranking, RankingViewHolder>(
                Ranking.class,
                R.layout.layout_ranking,
                RankingViewHolder.class,
                rankingTbl.orderByChild("score")
        ) {
            @Override
            protected void populateViewHolder(RankingViewHolder viewHolder, final Ranking model, int position) {
                viewHolder.txt_name.setText(model.getUserName());
                viewHolder.txt_score.setText(String.valueOf(model.getScore()));

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        if (mAsass != null && mAsass.isLoaded()){
                            mAsass.show();
                            mAsass.setAdListener(new AdListener() {

                                @Override

                                public void onAdClosed() {
                                    loadInters();
                                    Intent scoreDetail = new Intent(getActivity(),ScoreDetailActivity.class);
                                    scoreDetail.putExtra("viewUser",model.getUserName());
                                    startActivity(scoreDetail);
                                }

                            });
                        }else {
                            loadInters();
                            Intent scoreDetail = new Intent(getActivity(),ScoreDetailActivity.class);
                            scoreDetail.putExtra("viewUser",model.getUserName());
                            startActivity(scoreDetail);
                        }
                        }
                });
            }
        };

        adapter.notifyDataSetChanged();
        rankingList.setAdapter(adapter);

        return mFragment;
    }

    private void loadInters() {
        if (!mAsass.isLoading() && !mAsass.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAsass.loadAd(adRequest);
        }
    }

    private void updateScore(final String userName, final RankingCallBack<Ranking> callback) {
        questionScore.orderByChild("user").equalTo(userName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren())
                {
                    QuestionScore ques = data.getValue(QuestionScore.class);
                    sum+=Integer.parseInt(ques.getScore());
                }
                Ranking ranking = new Ranking(userName,sum);
                callback.callBack(ranking);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
