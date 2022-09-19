package com.moutamid.randomchat;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.randomchat.Models.Friends;
import com.moutamid.randomchat.utils.Constants;
import com.moutamid.randomchat.databinding.ActivityMainBinding;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    ActivityMainBinding binding;
    public static Context context;
    private InterstitialAd mInterstitialAd;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = getApplicationContext();
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(MainActivity.this);
        binding.bottomNavigationView.setSelectedItemId(R.id.homes);
        db = Constants.databaseReference().child("AdmobId");
        getIds();
       // checkFriendRequest();
    }

    private void getIds() {
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String id=snapshot.child("interstitial").getValue().toString();
                    loadAdmobBanner(id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void loadAdmobBanner(String id) {

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(id);
        AdRequest ad = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(ad);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mInterstitialAd.show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.

                Log.e("ads",String.valueOf(errorCode));
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
            }
        });
    }

    private void checkFriendRequest() {
        Constants.databaseReference().child("Friends")
                .child(Constants.auth().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                Friends model = ds.getValue(Friends.class);
                                if (model.getStatus().equals("request")) {
                                    showAlertDialogBox(model.getUserId());
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showAlertDialogBox(String userId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("You have a Friend Request");
        alertDialogBuilder.setMessage("Are you sure, you want to accept this friend request? ");
        alertDialogBuilder.setPositiveButton("Accept",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("status","friends");
                        String key = Constants.auth().getCurrentUser().getUid();
                        Constants.databaseReference().child("Friends").child(key).child(userId).updateChildren(hashMap);
                        Constants.databaseReference().child("Friends").child(userId).child(key).updateChildren(hashMap);
                    }
                });
        alertDialogBuilder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String key = Constants.auth().getCurrentUser().getUid();
                Constants.databaseReference().child("Friends").child(key).child(userId).removeValue();
                Constants.databaseReference().child("Friends").child(userId).child(key).removeValue();
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
}

    Home home = new Home();
    ChatingFrament chatingFrament = new ChatingFrament();
    HistoryFragment historyFragment = new HistoryFragment();
    GroupsFragment groupsFragment = new GroupsFragment();
    FriendListFragment friendListFragment = new FriendListFragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.homes:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, home).commit();
                return true;
            case R.id.chats:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, chatingFrament).commit();
                return true;
            case R.id.history:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, historyFragment).commit();
                return true;
            case R.id.groups:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, groupsFragment).commit();
                return true;
            case R.id.frends:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, friendListFragment).commit();
                return true;
        }
        return false;
    }
}