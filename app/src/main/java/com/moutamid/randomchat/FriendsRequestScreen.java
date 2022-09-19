package com.moutamid.randomchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.randomchat.Adapters.FriendRequestListAdapter;
import com.moutamid.randomchat.Models.Friends;
import com.moutamid.randomchat.databinding.ActivityFriendsRequestScreenBinding;
import com.moutamid.randomchat.databinding.ActivityGroupChatsSideBinding;
import com.moutamid.randomchat.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class FriendsRequestScreen extends AppCompatActivity {

    private ActivityFriendsRequestScreenBinding b;
    private List<Friends> friendsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityFriendsRequestScreenBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        friendsList = new ArrayList<>();
        b.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FriendsRequestScreen.this,MainActivity.class));
                finish();
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        b.recyclerView.setLayoutManager(manager);
        checkFriendRequest();
    }

    private void checkFriendRequest() {
        Constants.databaseReference().child("Friends")
                .child(Constants.auth().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            friendsList.clear();
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                Friends model = ds.getValue(Friends.class);
                                if (model.getStatus().equals("request")) {
                                    friendsList.add(model);
                                }
                            }

                            FriendRequestListAdapter adapter = new FriendRequestListAdapter(FriendsRequestScreen.this,
                                    friendsList);
                            b.recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}