package com.moutamid.randomchat;

import static androidx.recyclerview.widget.RecyclerView.VERTICAL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.randomchat.Adapters.ChatRoomAdapter;
import com.moutamid.randomchat.Models.Chat;
import com.moutamid.randomchat.Models.UserModel;
import com.moutamid.randomchat.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChatWithFriendSide extends AppCompatActivity {

    private List<Chat> chatList;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ChatRoomAdapter adapter;
    private DatabaseReference db;
    private RecyclerView recyclerView;
    private EditText msgTxt;
    private ImageView sendImg;
    private MaterialToolbar toolbar;
    private String idFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_with_friend_side);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        chatList = new ArrayList<>();
        db = Constants.databaseReference().child(Constants.MESSAGES);
        idFrom = getIntent().getStringExtra("id");
        recyclerView = findViewById(R.id.recyclerView);
        msgTxt = findViewById(R.id.message);
        sendImg = findViewById(R.id.send);
        toolbar = findViewById(R.id.topAppBar);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(VERTICAL);
        //linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        sendImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = msgTxt.getText().toString();
                if (message.isEmpty()){
                    sendImg.setEnabled(false);
                }else {
                    sendImg.setEnabled(true);
                    sendMessages(message);
                }
            }
        });
        Constants.databaseReference().child(Constants.USERS)
                        .child(idFrom).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            UserModel model = snapshot.getValue(UserModel.class);
                            toolbar.setTitle(model.getName());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        getMessages();
    }
    private void sendMessages(String message) {
        long timestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        Chat chatReciever = new Chat(message,user.getUid(), idFrom,timestamp);


        DatabaseReference senderReference1 = db.child(user.getUid()).child(idFrom);
        senderReference1.child(String.valueOf(timestamp)).setValue(chatReciever);
        DatabaseReference receiverReference1 = db.child(idFrom).child(user.getUid());
        receiverReference1.child(String.valueOf(timestamp)).setValue(chatReciever);
        msgTxt.setText("");

    }

    private void getMessages() {

        Constants.databaseReference().child(Constants.MESSAGES)
                .child(user.getUid()).child(idFrom)
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
                            adapter = new ChatRoomAdapter(ChatWithFriendSide.this,chatList);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}