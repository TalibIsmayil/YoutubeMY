package com.talib.youtuberx.youtubemy.ViewHolder;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.talib.youtuberx.youtubemy.Interface.ItemClickListener;
import com.talib.youtuberx.youtubemy.R;
import com.squareup.picasso.Picasso;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    View mV;

    public TextView category_name;
    public ImageView category_image,gradient2;
    public Button btnPlay;

    private ItemClickListener itemClickListener;

    public CategoryViewHolder(View itemView) {
        super(itemView);

        mV = itemView;

        category_image = (ImageView)itemView.findViewById(R.id.category_image);
        category_name = (TextView)itemView.findViewById(R.id.category_name);
        gradient2 = (ImageView)itemView.findViewById(R.id.gradient2);
        btnPlay = (Button)itemView.findViewById(R.id.btn_play);
        btnPlay.setTag(R.id.btn_play,itemView);
        btnPlay.setOnClickListener(this);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    public void setImage(String image){
        ImageView category_image = (ImageView)mV.findViewById(R.id.category_image);
        Picasso.get().load(image).into(category_image);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }
}
