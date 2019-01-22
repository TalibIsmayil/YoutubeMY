package com.talib.youtuberx.youtubemy.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.talib.youtuberx.youtubemy.R;

public class ScoreDetailViewHolder extends RecyclerView.ViewHolder {

    public TextView txt_name1,text_score1;

    public ScoreDetailViewHolder(View itemView) {
        super(itemView);

        txt_name1 = (TextView)itemView.findViewById(R.id.txt_name1);
        text_score1 = (TextView)itemView.findViewById(R.id.txt_score1);
    }
}
