package com.talib.youtuberx.youtubemy.ViewHolder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.talib.youtuberx.youtubemy.Common.Common;
import com.talib.youtuberx.youtubemy.Interface.ItemClickListener;
import com.talib.youtuberx.youtubemy.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    View mView;

    public TextView title,desc;
    public ImageView image;
    public ImageButton like1;


    private ItemClickListener itemClickListener;

    private DatabaseReference post;
    public PostViewHolder(View itemView) {
        super(itemView);

        mView = itemView;
        post = FirebaseDatabase.getInstance().getReference().child("Blog");
        post.keepSynced(true);
        like1 = (ImageButton)mView.findViewById(R.id.like_button);
        itemView.setOnClickListener(this);
    }

    public void setTitle(String title){
       TextView post_title = (TextView)mView.findViewById(R.id.post_title) ;
       post_title.setText(title);
    }
    public void setDesc(String desc){
        TextView post_desc = (TextView)mView.findViewById(R.id.post_desc);
        post_desc.setText(desc);
    }

    public void setImage(Context ctx,String image){
        ImageView post_image = (ImageView)mView.findViewById(R.id.post_image);
        Picasso.get().load(image).into(post_image);
    }

    public void setLikeCount(String likeCount){
        TextView tvlikeCount = (TextView)mView.findViewById(R.id.likeCount);
        tvlikeCount.setText(Integer.toString(Integer.parseInt(likeCount)));
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }

    public void setAnimate (final String post_key){
        post.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(post_key).hasChild(Common.currentUser.getUserName())){
                    like1.setImageResource(R.drawable.ic_action_likes);
                }else {
                    like1.setImageResource(R.drawable.ic_action_dislikes);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
