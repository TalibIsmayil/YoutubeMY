package com.talib.youtuberx.youtubemy;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.talib.youtuberx.youtubemy.Common.Common;
import com.talib.youtuberx.youtubemy.Interface.ItemClickListener;
import com.talib.youtuberx.youtubemy.Interface.LikeCallBack;
import com.talib.youtuberx.youtubemy.Interface.RankingCallBack;
import com.talib.youtuberx.youtubemy.ViewHolder.PostViewHolder;
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

import com.squareup.picasso.Picasso;

import es.dmoral.toasty.Toasty;

public class FavoriteFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

     View mFragment;

     private SwipeRefreshLayout mS;

     private Toolbar tol;

     private InterstitialAd mAssa;

     private RecyclerView favoriteList;
     private LinearLayoutManager layoutManager;
     private FirebaseRecyclerAdapter<PostModel,PostViewHolder> adapter;

     private boolean mProcessLike = false;

     private FirebaseDatabase database;
     private DatabaseReference post,likecount,question_score;

    public static FavoriteFragment newInstance(){
        FavoriteFragment favoriteFragment = new FavoriteFragment();
        return favoriteFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        post = database.getReference().child("Blog");
        question_score = database.getReference("Question_Score");
        question_score.keepSynced(true);
        likecount = database.getReference().child("Blog");
        post.keepSynced(true);
        likecount.keepSynced(true);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragment = inflater.inflate(R.layout.fragment_favorite, container, false);

        tol = (Toolbar)mFragment.findViewById(R.id.tol);
        ((AppCompatActivity) getActivity()).setSupportActionBar(tol);

        mAssa = new InterstitialAd(getContext());
        mAssa.setAdUnitId("ca-app-pub-3159482392412970/1529632308");
        loadFav();

        mS = (SwipeRefreshLayout)mFragment.findViewById(R.id.swipe);
        mS.setOnRefreshListener(this);

        AdView adView = (AdView)mFragment.findViewById(R.id.adView6);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        favoriteList = (RecyclerView) mFragment.findViewById(R.id.listFavorite);
        layoutManager = new LinearLayoutManager(getActivity());
        favoriteList.setHasFixedSize(true);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        favoriteList.setLayoutManager(layoutManager);



        loadFavorite();

        return mFragment;
    }

    private void loadFav() {
        if (!mAssa.isLoading() && !mAssa.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAssa.loadAd(adRequest);
        }
    }


    private void loadFavorite() {
        adapter = new FirebaseRecyclerAdapter<PostModel, PostViewHolder>(
                PostModel.class,
                R.layout.favorite_card,
                PostViewHolder.class,
                post.orderByChild("likecount")
        ) {
            @Override
            protected void populateViewHolder(PostViewHolder viewHolder, final PostModel model, int position) {

                final String post_key = getRef(position).getKey();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setLikeCount(String.valueOf(model.getLikecount()));
                viewHolder.setImage(getContext(),model.getImage());
                viewHolder.setAnimate(post_key);
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        if (mAssa != null && mAssa.isLoaded()){
                            mAssa.show();
                            mAssa.setAdListener(new AdListener() {

                                @Override

                                public void onAdClosed() {
                                    loadFav();
                                    Toasty.info(getActivity(), "Öz sevimli youtuberinizi önə çıxarmağ üçün onu dəstəkləyin ! ", Toast.LENGTH_LONG, true).show();
                                    //Toast.makeText(getActivity(), R.string.support, Toast.LENGTH_SHORT).show();
                                }

                            });
                        }else {
                            loadFav();
                            Toasty.info(getActivity(), "Öz sevimli youtuberinizi önə çıxarmağ üçün onu dəstəkləyin ! ", Toast.LENGTH_LONG, true).show();
                            //Toast.makeText(getActivity(), R.string.support, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                viewHolder.like1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mAssa != null && mAssa.isLoaded()){
                            mAssa.show();
                            mAssa.setAdListener(new AdListener() {

                                @Override

                                public void onAdClosed() {
                                    loadFav();
                        mProcessLike = true;
                        likecount.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (mProcessLike) {
                                    if (dataSnapshot.child(post_key).hasChild(Common.currentUser.getUserName())) {

                                        likecount.child(post_key).child(Common.currentUser.getUserName()).removeValue();


                                        int sum = 0;
                                        sum = dataSnapshot.child(post_key).child("likecount").getValue(Integer.class);
                                        likecount.child(post_key).child("likecount").setValue(sum-1);
                                        mProcessLike = false;
                                    } else {
                                        likecount.child(post_key).child(Common.currentUser.getUserName()).setValue("Like");


                                        int sum = 0;
                                        sum = dataSnapshot.child(post_key).child("likecount").getValue(Integer.class);
                                        likecount.child(post_key).child("likecount").setValue(sum+1);
                                        mProcessLike = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                                }

                            });
                        }else {
                            loadFav();
                            mProcessLike = true;
                            likecount.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (mProcessLike) {
                                        if (dataSnapshot.child(post_key).hasChild(Common.currentUser.getUserName())) {

                                            likecount.child(post_key).child(Common.currentUser.getUserName()).removeValue();


                                            int sum = 0;
                                            sum = dataSnapshot.child(post_key).child("likecount").getValue(Integer.class);
                                            likecount.child(post_key).child("likecount").setValue(sum-1);
                                            mProcessLike = false;
                                        } else {
                                            likecount.child(post_key).child(Common.currentUser.getUserName()).setValue("Like");


                                            int sum = 0;
                                            sum = dataSnapshot.child(post_key).child("likecount").getValue(Integer.class);
                                            likecount.child(post_key).child("likecount").setValue(sum+1);
                                            mProcessLike = false;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                });


            }
        };
        adapter.notifyDataSetChanged();
        favoriteList.setAdapter(adapter);
    }









    @Override
    public void onRefresh() {
        adapter.notifyDataSetChanged();
        favoriteList.setAdapter(adapter);
        if (mS.isRefreshing()){
            mS.setRefreshing(false);
        }
    }
}
