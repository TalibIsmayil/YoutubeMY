package com.talib.youtuberx.youtubemy;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.talib.youtuberx.youtubemy.Common.Common;
import com.talib.youtuberx.youtubemy.Interface.ItemClickListener;
import com.talib.youtuberx.youtubemy.ViewHolder.CategoryViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class CategoryFragment extends Fragment {

    View mFragment;



    private RecyclerView listCategory;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseRecyclerAdapter<Category,CategoryViewHolder> adapter;

    private InterstitialAd inters;

    private FirebaseDatabase database;
    private DatabaseReference categories;

    public static CategoryFragment newInstance(){
        CategoryFragment categoryFragment = new CategoryFragment();
        return categoryFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        categories = database.getReference("Category");
        categories.keepSynced(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragment = inflater.inflate(R.layout.fragment_category,container,false);
        listCategory = (RecyclerView)mFragment.findViewById(R.id.listCategory);
        listCategory.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(container.getContext());
        listCategory.setLayoutManager(layoutManager);


        inters = new InterstitialAd(getContext());
        inters.setAdUnitId("ca-app-pub-3159482392412970/8299597355");
        if (!inters.isLoading() && !inters.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            inters.loadAd(adRequest);
        }


        loadCategories();
        
        return mFragment;
    }

    private void loadCategories() {
        adapter = new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(Category.class,
                R.layout.category_layout,
                CategoryViewHolder.class,
                categories) {
            @Override
            protected void populateViewHolder(CategoryViewHolder viewHolder, final Category model, int position) {
                viewHolder.category_name.setText(model.getAd());
                viewHolder.setImage(model.getSekil());
                //Picasso.get().load(model.getImage()).into(viewHolder.category_image);
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, final int position, boolean isLongClick) {
                        {
                            if (inters != null && inters.isLoaded()) {
                                inters.show();
                                inters.setAdListener(new AdListener() {

                                    @Override

                                    public void onAdClosed() {
                                        Intent play = new Intent(getActivity(), StartActivity.class);
                                        Common.categoryId = adapter.getRef(position).getKey();
                                        Common.categoryName = model.getAd();
                                        startActivity(play);
                                    }

                                });
                            } else {
                                Intent play = new Intent(getActivity(), StartActivity.class);
                                Common.categoryId = adapter.getRef(position).getKey();
                                Common.categoryName = model.getAd();
                                startActivity(play);
                            }
                        }
                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        listCategory.setAdapter(adapter);
    }

}
