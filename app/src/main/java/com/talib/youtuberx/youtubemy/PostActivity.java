package com.talib.youtuberx.youtubemy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.talib.youtuberx.youtubemy.Common.Common;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class PostActivity extends AppCompatActivity {

    private EditText title,desc;
    private ImageButton imageButton;
    private Button sbmt;
    private Uri mImageUri = null;
    private static final int GALERY_REQUEST = 1;
    private StorageReference mStorage;
    private ProgressDialog mProgress;
    private DatabaseReference mData;
    RelativeLayout bas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        bas = (RelativeLayout)findViewById(R.id.bas);

        mStorage = FirebaseStorage.getInstance().getReference();
        mData = FirebaseDatabase.getInstance().getReference().child("Blog");
        mData.keepSynced(true);
        title = (EditText)bas.findViewById(R.id.tit);
        desc = (EditText)bas.findViewById(R.id.desc);

        sbmt = (Button)bas.findViewById(R.id.post);

        mProgress = new ProgressDialog(this);

        imageButton = (ImageButton)bas.findViewById(R.id.imageButton);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galeryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galeryIntent.setType("image/*");
                startActivityForResult(galeryIntent,GALERY_REQUEST);
            }
        });
        sbmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });
    }

    private void startPosting() {
        mProgress.setMessage("Please wait ! ");
        mProgress.show();
        mProgress.setCancelable(false);

        final String title_val = title.getText().toString().trim();
        final String desc_val = desc.getText().toString().trim();
        if (!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val) && mImageUri != null){

            StorageReference filepath = mStorage.child("Youtuber_Images").child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String downloadUrl = taskSnapshot.getMetadata().toString();
                    DatabaseReference newPost = mData.push();
                    newPost.child("title").setValue(title_val);
                    newPost.child("desc").setValue(desc_val);
                    newPost.child("likecount").setValue(0);
                    newPost.child("image").setValue(downloadUrl);

                    mProgress.dismiss();

                    startActivity(new Intent(PostActivity.this,HomeActivity.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplication(),R.string.worns,Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALERY_REQUEST && resultCode == RESULT_OK){

            mImageUri = data.getData();



        }

    }
}
