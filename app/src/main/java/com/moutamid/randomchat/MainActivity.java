package com.moutamid.randomchat;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.randomchat.Models.Friends;
import com.moutamid.randomchat.databinding.ActivityMainBinding;
import com.moutamid.randomchat.utils.Constants;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(MainActivity.this);
        binding.bottomNavigationView.setSelectedItemId(R.id.homes);
        checkFriendRequest();
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