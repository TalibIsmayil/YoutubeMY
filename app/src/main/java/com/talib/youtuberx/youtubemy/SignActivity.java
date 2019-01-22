package com.talib.youtuberx.youtubemy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.talib.youtuberx.youtubemy.BroadcastReciever.AlarmReceiver;
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
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Calendar;

import es.dmoral.toasty.Toasty;
import info.hoang8f.widget.FButton;


public class SignActivity extends AppCompatActivity {
    private AppCompatEditText edtNewUser,edtNewPassword,edtNewEmail;
    private AppCompatEditText edtUser,edtPassword;

    private RelativeLayout lay;
//    private AnimationDrawable animationDrawable;

    private InterstitialAd mInterstitialAd;

    Context context = this;

    private FButton btnSignUp,btnSignIn;

    private ProgressDialog pro;

    private FirebaseDatabase database;
    private DatabaseReference users;

    private CheckBox checkBox;

    ShredPref shredPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3159482392412970/1973509534");
        loadIstsSta();

        shredPref = new ShredPref();

        AdView adView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        checkBox = (CheckBox)findViewById(R.id.checkbox);


        //registerAlarm();
        
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        users.keepSynced(true);

        edtUser = (AppCompatEditText)findViewById(R.id.edtUsername);
        edtPassword = (AppCompatEditText)findViewById(R.id.edtPassword);

        btnSignIn = (FButton)findViewById(R.id.btnSignIn);
        btnSignUp = (FButton)findViewById(R.id.btnSignUp);

        pro = new ProgressDialog(this);

        lay = (RelativeLayout) findViewById(R.id.lay);
//        animationDrawable = (AnimationDrawable)lay.getBackground();
//        animationDrawable.setEnterFadeDuration(5500);
//        animationDrawable.setExitFadeDuration(5500);
//        animationDrawable.start();

        if (shredPref.getValBoolean(context,"remmember")){
            edtUser.setText(shredPref.getVal(context,"username"));
            checkBox.setChecked(shredPref.getValBoolean(context,"remmember"));
        }

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnected(SignActivity.this)) {
                    buildDialog(SignActivity.this).show();
                }else {
                        if (mInterstitialAd != null && mInterstitialAd.isLoaded()){
                            mInterstitialAd.show();
                            mInterstitialAd.setAdListener(new AdListener() {

                                @Override

                                public void onAdClosed() {
                                    loadIstsSta();
                                    showSignUpDialog();
                                }

                            });
                        }else {
                            loadIstsSta();
                            showSignUpDialog();
                        }
                }
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnected(SignActivity.this)) {
                    buildDialog(SignActivity.this).show();
                }else {
                    if (mInterstitialAd != null && mInterstitialAd.isLoaded()){
                        mInterstitialAd.show();
                        mInterstitialAd.setAdListener(new AdListener() {

                            @Override

                            public void onAdClosed() {
                                loadIstsSta();
                                signIn(edtUser.getText().toString(), edtPassword.getText().toString());
                                if (checkBox.isChecked()){
                                    shredPref.save(context,"username",edtUser.getText().toString());
                                }else {
                                    shredPref.save(context, "username", "");
                                }
                                shredPref.saveBoolean(context,"remmember",checkBox.isChecked());
                            }

                        });
                    }else {
                        loadIstsSta();
                        signIn(edtUser.getText().toString(), edtPassword.getText().toString());
                        if (checkBox.isChecked()){
                            shredPref.save(context,"username",edtUser.getText().toString());
                        }else {
                            shredPref.save(context, "username", "");
                        }
                        shredPref.saveBoolean(context,"remmember",checkBox.isChecked());
                    }

                }
            }
        });
    }

    private void loadIstsSta() {
        if (!mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mInterstitialAd.loadAd(adRequest);
        }
    }

    private void registerAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,9);
        calendar.set(Calendar.MINUTE,10);
        calendar.set(Calendar.SECOND,0);

        Intent intent = new Intent(SignActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(SignActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager)this.getSystemService(this.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
    }


    private void signIn(final String user, final String pwd) {
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(user).exists())
                {
                    if (!user.isEmpty())
                    {
                        User login = dataSnapshot.child(user).getValue(User.class);
                        if (login.getPassword().equals(pwd))
                        {
                            Intent pass = new Intent(SignActivity.this,HomeActivity.class);
                            Common.currentUser = login;
                            startActivity(pass);
                            finish();
                            //FancyToast.makeText(SignActivity.this,"Giriş edilir",FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,R.drawable.asdfg);
                        }

                        else
                            Toasty.error(getApplicationContext(), "Yalnış şifrə ! ", Toast.LENGTH_SHORT, true).show();
                            //FancyToast.makeText(SignActivity.this,"Yalnış şifrə ! ",FancyToast.LENGTH_LONG,FancyToast.ERROR,R.drawable.asdfg);
                            //Snackbar.make(lay,R.string.wrong,Snackbar.LENGTH_LONG).setAction("Action",null).show();
                    }
                    else
                    {
                        Toasty.warning(getApplicationContext(), "Xahiş edirik məlumatları tam doldurun ! ", Toast.LENGTH_LONG, true).show();
                        //FancyToast.makeText(SignActivity.this,"Xahiş edirik məlumatları tam doldurun !",FancyToast.LENGTH_LONG,FancyToast.INFO,R.drawable.asdfg);
                        //Snackbar.make(lay,R.string.alert,Snackbar.LENGTH_LONG).setAction("Action",null).show();
                    }
                }
                else
                    Toasty.error(getApplicationContext(), "İstifadəçi mövcud deyil !", Toast.LENGTH_SHORT, true).show();
                    //FancyToast.makeText(SignActivity.this,"İstifadəçi mövcud deyil !",FancyToast.LENGTH_SHORT,FancyToast.ERROR,R.drawable.asdfg);
                    //Snackbar.make(lay,R.string.noexists,Snackbar.LENGTH_LONG).setAction("Action",null).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toasty.error(getApplicationContext(), "Nəsə düz getmir ! ", Toast.LENGTH_SHORT, true).show();
            }
        });
    }


    private void showSignUpDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignActivity.this);
        alertDialog.setTitle(R.string.signup);
        alertDialog.setMessage(R.string.alert);

        LayoutInflater inflater = this.getLayoutInflater();
        View sign_up_layout = inflater.inflate(R.layout.sign_up_layout,null);

        edtNewUser = (AppCompatEditText)sign_up_layout.findViewById(R.id.edtNewUsername);
        edtNewEmail = (AppCompatEditText)sign_up_layout.findViewById(R.id.edtNewEmail);
        edtNewPassword = (AppCompatEditText)sign_up_layout.findViewById(R.id.edtNewPassword);

        alertDialog.setView(sign_up_layout);
        alertDialog.setIcon(R.drawable.ic_account_box_black_24dp);

        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!edtNewUser.getText().toString().equals("") &&
                        !edtNewPassword.getText().toString().equals("") &&
                        !edtNewEmail.getText().toString().equals("")) {
                    final User user = new User(edtNewUser.getText().toString(),
                            edtNewPassword.getText().toString(),
                            edtNewEmail.getText().toString());

                    pro.setCancelable(false);
                    users.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (edtNewUser.getText().toString().equals("") && edtNewPassword.getText().toString().equals("") &&
                                    edtNewEmail.getText().toString().equals("")){
                                Toasty.warning(getApplicationContext(), "Xahiş edirik məlumatları tam doldurun ! ", Toast.LENGTH_LONG, true).show();
                                //FancyToast.makeText(getApplicationContext(),"Xahiş edirik məlumatları tam doldurun !",FancyToast.LENGTH_SHORT,FancyToast.INFO,R.drawable.asdfg);
                                //Toast.makeText(getApplicationContext(),R.string.alert,Toast.LENGTH_SHORT).show();
                            }else {
                                if (dataSnapshot.child(user.getUserName()).exists())
                                    Toasty.warning(getApplicationContext(), "Xahiş edirik məlumatları tam doldurun ! ", Toast.LENGTH_SHORT, true).show();
                                    //FancyToast.makeText(getApplicationContext(),"İstifadəçi artıq mövcuddur ! ",FancyToast.LENGTH_SHORT,FancyToast.WARNING,R.drawable.asdfg);
                                    //Snackbar.make(lay, R.string.exists, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                else {
                                    users.child(user.getUserName())
                                            .setValue(user);
                                    Toasty.success(getApplicationContext(), "Qeydiyyat uğurla başa çatdı ! ", Toast.LENGTH_LONG, true).show();
                                    //FancyToast.makeText(getApplicationContext(),"Qeydiyyat uğurla başa çatdı ! ",FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,R.drawable.asdfg);
                                    //Snackbar.make(lay, R.string.success, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toasty.error(getApplicationContext(), "Nəsə düz getmir ! ", Toast.LENGTH_SHORT, true).show();
                        }
                    });
                }

                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }
    public boolean isConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mb = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((mb != null && mb.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()))
                return true;
            else return false;
        }
        else return false;

    }
    public AlertDialog.Builder buildDialog(Context c){

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(R.string.noconnect);
        builder.setIcon(R.drawable.ic_signal_wifi_off_black_24dp);
        builder.setMessage(R.string.message);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        return builder;
    }
}
