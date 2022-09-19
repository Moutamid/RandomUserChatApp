package com.moutamid.randomchat;

import static com.bumptech.glide.Glide.with;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.DATA;
import static com.moutamid.randomchat.R.color.lighterGrey;
import static com.moutamid.randomchat.utils.Constants.userModel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.request.RequestOptions;
import com.fxn.stash.Stash;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.moutamid.randomchat.Models.UserModel;
import com.moutamid.randomchat.databinding.FragmentHomeBinding;
import com.moutamid.randomchat.utils.Constants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Home extends Fragment {
    FragmentHomeBinding b;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ArrayList<String> connectionList;
    private String gender = "";
    private String lang = "";
    private String reward = "";
    private String banner = "";
    private RewardedVideoAd AdMobrewardedVideoAd;
    private DatabaseReference db;
    private Context mContext;
    private boolean watched = false;


    public Home() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = MainActivity.context;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        b = FragmentHomeBinding.inflate(getLayoutInflater());
        this.mContext = MainActivity.context;
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        connectionList = new ArrayList<>();
        gender = Stash.getString("gender");
        lang = Stash.getString("lang");
        db = Constants.databaseReference().child("AdmobId");
        getIds();
        if (mContext != null) {
            MobileAds.initialize(mContext, getString(R.string.admob_app_id));
        }
        // loading Video Ad
        loadRewardedVideoAd();
        getUserDetail();
   //     MobileAds.initialize(getApplicationContext(), getString(R.string.admob_app_id));
        b.getRoot().setOnTouchListener(new OnSwipeListener(getContext()) {
            @SuppressLint("ClickableViewAccessibility")
            public void onSwipeTop() {
            //    storeRandomChatUser();
                checkVipUser();
            }

            @SuppressLint("ClickableViewAccessibility")
            public void onSwipeRight() {
                Log.d("OnSwipeListener", "onSwipeRight");
            }

            @SuppressLint("ClickableViewAccessibility")
            public void onSwipeLeft() {
                Log.d("OnSwipeListener", "onSwipeLeft");
            }

            @SuppressLint("ClickableViewAccessibility")
            public void onSwipeBottom() {
                Log.d("OnSwipeListener", "onSwipeBottom");
            }

        });
        b.close1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.cardGender.setVisibility(View.GONE);
            }
        });
        b.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.carLanguage.setVisibility(View.GONE);
            }
        });

        b.imgGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  b.cardGender.setVisibility(View.VISIBLE);
                Constants.databaseReference().child(Constants.USERS)
                        .child(user.getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    //for (DataSnapshot ds : snapshot.getChildren()){
                                    UserModel userModel = snapshot.getValue(UserModel.class);
                                    if (userModel.is_vip){
                                        b.cardGender.setVisibility(View.VISIBLE);
                                    }else {
                                        if (watched){
                                            b.cardGender.setVisibility(View.VISIBLE);
                                        }else {
                                            showVipPurchase();
                                        }
                                    }
                                    //}
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });
        b.imgLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //b.carLanguage.setVisibility(View.VISIBLE);
                Constants.databaseReference().child(Constants.USERS)
                        .child(user.getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    //for (DataSnapshot ds : snapshot.getChildren()){
                                    UserModel userModel = snapshot.getValue(UserModel.class);
                                    if (userModel.is_vip){
                                        b.carLanguage.setVisibility(View.VISIBLE);
                                    }else {
                                        if (watched){
                                            b.carLanguage.setVisibility(View.VISIBLE);
                                        }else {
                                            showVipPurchase();
                                        }
                                    }
                                    //}
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });
        b.rdMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.cardGender.setVisibility(View.GONE);
            }
        });
        b.rdEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.carLanguage.setVisibility(View.GONE);
            }
        });
        b.tvVipServcice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, VipServiceActivity.class));
            }
        });
        b.rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int radioButtonID = radioGroup.getCheckedRadioButtonId();

                RadioButton radioButton = (RadioButton) radioGroup.findViewById(radioButtonID);
                gender = (String) radioButton.getText();
                Stash.put("gender",gender);
                b.cardGender.setVisibility(View.GONE);
            }
        });
        b.rGLanguage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int radioButtonID = radioGroup.getCheckedRadioButtonId();

                RadioButton radioButton = (RadioButton) radioGroup.findViewById(radioButtonID);
                lang = (String) radioButton.getText();
                Stash.put("lang",lang);
                b.carLanguage.setVisibility(View.GONE);
            }
        });
        b.imgCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext,VipServiceActivity.class));
            }
        });
        b.tvVipServcice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext,VipServiceActivity.class));
            }
        });

        b.profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext,EditProfile.class);
               // intent.putExtra("id",user.getUid());
                startActivity(intent);
            }
        });
     /*   b.upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                b.menu.setVisibility(View.GONE);
            }
        });
        b.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),UserProfileFragment.class);
                intent.putExtra("id",user.getUid());
                startActivity(intent);
                b.menu.setVisibility(View.GONE);
            }
        });*/
        Constants.databaseReference().child(Constants.USERS)
                .child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            //for (DataSnapshot ds : snapshot.getChildren()){
                            UserModel userModel = snapshot.getValue(UserModel.class);
                            if (userModel.is_vip){
                                b.tvVipServcice.setVisibility(View.GONE);
                                b.imgCoin.setVisibility(View.GONE);
                            }else {
                                b.tvVipServcice.setVisibility(View.VISIBLE);
                                b.imgCoin.setVisibility(View.VISIBLE);
                            }
                            //}
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        //checkRandomCall();
        return b.getRoot();
    }
    private void getIds() {
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    reward=snapshot.child("reward").getValue().toString();
                    banner=snapshot.child("banner").getValue().toString();
                    AdView adView = new AdView(mContext);
                    adView.setAdSize(AdSize.SMART_BANNER);
                    adView.setAdUnitId(banner);
                    AdRequest request = new AdRequest.Builder().build();
                    adView.loadAd(request);
                    b.adView.addView(adView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showVipPurchase() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View add_view = inflater.inflate(R.layout.vip_dialog_box,null);
        AppCompatButton vipBtn = add_view.findViewById(R.id.vip);
        AppCompatButton adsBtn = add_view.findViewById(R.id.ads);
        builder.setView(add_view);
        AlertDialog alertDialog = builder.create();
        vipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext,VipServiceActivity.class));
                alertDialog.dismiss();
            }
        });
        adsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRewardedVideoAd();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

    }

    void loadRewardedVideoAd()
    {
        // initializing RewardedVideoAd Object
        // RewardedVideoAd  Constructor Takes Context as its
        // Argument
        AdMobrewardedVideoAd
                = MobileAds.getRewardedVideoAdInstance(mContext);

        // Rewarded Video Ad Listener
        AdMobrewardedVideoAd.setRewardedVideoAdListener(
                new RewardedVideoAdListener() {
                    @Override
                    public void onRewardedVideoAdLoaded()
                    {
                        // Showing Toast Message
                      //  Toast.makeText(MainActivity.this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRewardedVideoAdOpened()
                    {
                        // Showing Toast Message
                    }

                    @Override
                    public void onRewardedVideoStarted()
                    {
                        // Showing Toast Message
                    }

                    @Override
                    public void onRewardedVideoAdClosed()
                    {
                    }

                    @Override
                    public void onRewarded(
                            RewardItem rewardItem)
                    {
                        // Showing Toast Message

                    }

                    @Override
                    public void
                    onRewardedVideoAdLeftApplication()
                    {

                    }

                    @Override
                    public void onRewardedVideoAdFailedToLoad(
                            int i)
                    {

                    }

                    @Override
                    public void onRewardedVideoCompleted()
                    {

                    }
                });

        // Loading Rewarded Video Ad
        AdMobrewardedVideoAd.loadAd(reward, new AdRequest.Builder().build());
    }

    public void showRewardedVideoAd()
    {
        // Checking If Ad is Loaded or Not
        if (AdMobrewardedVideoAd.isLoaded()) {
            // showing Video Ad
            watched = true;
            AdMobrewardedVideoAd.show();
        }
        else {
            // Loading Rewarded Video Ad
            AdMobrewardedVideoAd.loadAd(reward, new AdRequest.Builder().build());
        }
    }

    private void getUserDetail() {
        Constants.databaseReference().child(Constants.USERS)
                .child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            UserModel userModel = snapshot.getValue(UserModel.class);
                            b.Name.setText(userModel.getName());
                            with(MainActivity.context)
                                    .asBitmap()
                                    .load(userModel.getProfile_url())
                                    .apply(new RequestOptions()
                                            .placeholder(lighterGrey)
                                            .error(lighterGrey)
                                    )
                                    .diskCacheStrategy(DATA)
                                    .into(b.profileImg);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void checkVipUser() {
        Constants.databaseReference().child(Constants.USERS)
                .child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            //for (DataSnapshot ds : snapshot.getChildren()){
                                UserModel userModel = snapshot.getValue(UserModel.class);
                                if (userModel.is_vip){
                                    filterUser();
                                    b.tvVipServcice.setVisibility(View.GONE);
                                    b.imgCoin.setVisibility(View.GONE);
                                }else {
                                    storeRandomChatUser();
                                    b.tvVipServcice.setVisibility(View.VISIBLE);
                                    b.imgCoin.setVisibility(View.VISIBLE);
                                }
                            //}
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void filterUser() {
        Constants.databaseReference()
                .child(Constants.RANDOM_CALL).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            for (DataSnapshot ds : snapshot.getChildren()) {
                        //        boolean connection = (boolean) ds.child("connection").getValue();
                                String id = (String) ds.child("id").getValue();
                                Constants.databaseReference().child(Constants.USERS)
                                        .child(id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                                if (snapshot1.exists()){
                                                    UserModel model = snapshot1.getValue(UserModel.class);
                                                    if (model.getGender().equals(gender) && model.getLanguage().equals(lang)){
                                                        storeRandomChatUser();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }
                        }else {
                        //    Toast.makeText(mContext, "Connection is not available now", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void storeRandomChatUser() {

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("id",user.getUid());
        hashMap.put("connection",true);

        Constants.databaseReference()
                .child(Constants.RANDOM_CALL)
                .child(user.getUid())
                .setValue(hashMap);


        checkRandomCall();
    }
    private void checkRandomCall() {

        Constants.databaseReference()
                .child(Constants.RANDOM_CALL)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            for (DataSnapshot ds : snapshot.getChildren()){
                                boolean connection = (boolean) ds.child("connection").getValue();
                                String id = (String) ds.child("id").getValue();
                                if (connection){
                                    if (!connectionList.contains(id)) {
                                        connectionList.add(id);
                                    }
                                    if (connectionList.size() == 1){
                                        new CountDownTimer(30000, 1000) {
                                            public void onTick(long millisUntilFinished) {

                                            }
                                            // When the task is over it will print 00:00:00 there
                                            public void onFinish() {
                                                Toast.makeText(mContext, "Connection is not available now", Toast.LENGTH_SHORT).show();
                                            }
                                        }.start();
                                    }
                                    else if (connectionList.size() == 2){
                                        connectionList.clear();
                                        startActivity(new Intent(mContext, RandomCallActivity.class));
                                    }
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();
      //  setConnectionFalse();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        setConnectionFalse();
    }


    private void setConnectionFalse() {
        Constants.databaseReference()
                .child(Constants.RANDOM_CALL)
                .child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            HashMap<String,Object> hashMap = new HashMap<>();
                            hashMap.put("connection",false);

                            Constants.databaseReference()
                                    .child(Constants.RANDOM_CALL)
                                    .child(user.getUid())
                                    .updateChildren(hashMap);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}