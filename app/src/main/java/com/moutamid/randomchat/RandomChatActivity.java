package com.moutamid.randomchat;

import static androidx.recyclerview.widget.RecyclerView.VERTICAL;
import static com.bumptech.glide.Glide.with;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.DATA;
import static com.moutamid.randomchat.R.color.lighterGrey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.randomchat.Adapters.ChatRoomAdapter;
import com.moutamid.randomchat.Models.Chat;
import com.moutamid.randomchat.Models.UserModel;
import com.moutamid.randomchat.databinding.ActivityRandomChatBinding;
import com.moutamid.randomchat.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RandomChatActivity extends AppCompatActivity {
    private ActivityRandomChatBinding b;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ChatRoomAdapter adapter;
    private List<Chat> chatList;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityRandomChatBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        chatList = new ArrayList<>();
        db = Constants.databaseReference().child(Constants.MESSAGES);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(VERTICAL);
        //linearLayoutManager.setReverseLayout(true);
        b.recyclerView.setLayoutManager(linearLayoutManager);
        b.recyclerView.setHasFixedSize(true);
        b.recyclerView.setNestedScrollingEnabled(false);

        initializeToolbar();
    }


    private void initializeToolbar() {
        Constants.databaseReference()
                .child(Constants.RANDOM_CHAT)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                //UserModel userModel = dataSnapshot.getValue(UserModel.class);
                                String idFrom = dataSnapshot.getKey().toString();
                                // Toast.makeText(RandomCallActivity.this,dataSnapshot.getKey().toString(),Toast.LENGTH_LONG).show();
                                if (!idFrom.equals(user.getUid())) {

                                    Constants.databaseReference()
                                            .child(Constants.USERS)
                                            .child(idFrom)
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                                    if (snapshot1.exists()){
                                                        UserModel userModel = snapshot1.getValue(UserModel.class);
                                                        b.toolbar.setTitle(userModel.name);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                    b.send.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            String message = b.message.getText().toString();
                                            if (message.isEmpty()){
                                                b.send.setEnabled(false);
                                            }else {
                                                b.send.setEnabled(true);
                                                sendMessages(message,idFrom);
                                            }
                                        }
                                    });
                                    getMessages(idFrom);
                                }
                            }


                        } else {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void sendMessages(String message, String idFrom) {
        long timestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        Chat chatReciever = new Chat(message,user.getUid(), idFrom,timestamp);


        DatabaseReference senderReference1 = db.child(user.getUid()).child(idFrom);
        senderReference1.child(String.valueOf(timestamp)).setValue(chatReciever);
        DatabaseReference receiverReference1 = db.child(idFrom).child(user.getUid());
        receiverReference1.child(String.valueOf(timestamp)).setValue(chatReciever);
        b.message.setText("");
        getMessages(idFrom);
    }

    private void getMessages(String idFrom) {

        Constants.databaseReference().child(Constants.MESSAGES)
                .child(user.getUid()).child(idFrom).orderByChild("timestamp")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            chatList.clear();
                            for (DataSnapshot ds : snapshot.getChildren()){
                                Chat model = ds.getValue(Chat.class);
                                chatList.add(model);
                            }
                     //       Toast.makeText(RandomChatActivity.this, ""+chatList.size(), Toast.LENGTH_SHORT).show();
                            adapter = new ChatRoomAdapter(RandomChatActivity.this,chatList);
                            b.recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeUser();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        removeUser();
        sendMainActivity();
    }

    private void sendMainActivity() {
        startActivity(new Intent(RandomChatActivity.this,MainActivity.class));
        finish();
    }

    private void removeUser() {
        Constants.databaseReference()
                .child(Constants.RANDOM_CHAT)
                .child(user.getUid())
                .removeValue();
    }

}