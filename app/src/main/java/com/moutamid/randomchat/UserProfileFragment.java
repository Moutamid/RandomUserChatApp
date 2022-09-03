package com.moutamid.randomchat;

import static com.moutamid.randomchat.R.color.lighterGrey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.randomchat.Adapters.FriendListAdapter;
import com.moutamid.randomchat.Models.Friends;
import com.moutamid.randomchat.Models.GroupsModel;
import com.moutamid.randomchat.Models.UserModel;
import com.moutamid.randomchat.utils.Constants;

public class UserProfileFragment extends AppCompatActivity {
    FriendListAdapter adapter;
    List<Friends> list;
    public String userId;
    RecyclerView recyclerView;
    private TextView nameTxt;
    private ImageView profileImg;
    private Button sendBtn;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user);
        list = new ArrayList<>();
        recyclerView = findViewById(R.id.recFriends);
        nameTxt = findViewById(R.id.tvName);
        profileImg = findViewById(R.id.imageView);
        sendBtn = findViewById(R.id.button);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userId = getIntent().getStringExtra("id");
        LinearLayoutManager manager = new LinearLayoutManager(UserProfileFragment.this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);
        getUserData();
        db = Constants.databaseReference().child("Friends");
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendFriendRequest();
            }
        });
        getUserFriends();
    }

    private void getUserFriends() {
        db.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Friends model = ds.getValue(Friends.class);
                        if (model.getStatus().equals("friends")) {
                            list.add(model);
                        }
                    }
                    adapter = new FriendListAdapter(UserProfileFragment.this, list);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendFriendRequest() {
        Friends sender = new Friends(userId,"pending");
        db.child(user.getUid()).child(userId).setValue(sender);
        Friends receiver = new Friends(user.getUid(),"request");
        db.child(userId).child(user.getUid()).setValue(receiver);
        Toast.makeText(this, "Request Sent!", Toast.LENGTH_SHORT).show();
    }

    private void getUserData() {
        Constants.databaseReference().child(Constants.USERS)
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            UserModel model = snapshot.getValue(UserModel.class);
                            nameTxt.setText(model.getName());
                            if(model.getProfile_url().equals("")){

                                Glide.with(UserProfileFragment.this)
                                        .asBitmap()
                                        .load(R.drawable.img)
                                        .apply(new RequestOptions()
                                                .placeholder(lighterGrey)
                                                .error(lighterGrey)
                                        )
                                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                                        .into(profileImg);
                            }else {

                                Glide.with(UserProfileFragment.this)
                                        .asBitmap()
                                        .load(model.profile_url)
                                        .apply(new RequestOptions()
                                                .placeholder(lighterGrey)
                                                .error(lighterGrey)
                                        )
                                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                                        .into(profileImg);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


}