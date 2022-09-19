package com.moutamid.randomchat;

import static androidx.recyclerview.widget.RecyclerView.VERTICAL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.randomchat.Adapters.ChatRoomAdapter;
import com.moutamid.randomchat.Models.Chat;
import com.moutamid.randomchat.Models.UserModel;
import com.moutamid.randomchat.VoiceCall.IncomingCallActivity;
import com.moutamid.randomchat.VoiceCall.OutgoingCallActivity;
import com.moutamid.randomchat.VoiceCall.VoiceCallActivity;
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
    private ImageView sendImg,backImg,callImg;
    private TextView toolbar;
    private String reward = "";
    private String idFrom;
    private static UserModel userModel;
    private boolean watched = false;
    private DatabaseReference voiceDB;
    private RewardedVideoAd AdMobrewardedVideoAd;

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
        backImg = findViewById(R.id.back);
        toolbar = findViewById(R.id.name);
        callImg = findViewById(R.id.call);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatWithFriendSide.this,MainActivity.class));
            }
        });
        getIds();
        MobileAds.initialize(ChatWithFriendSide.this);
        // loading Video Ad
        loadRewardedVideoAd();
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
                            userModel = model;
                            toolbar.setText(model.getName());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        voiceDB = Constants.databaseReference().child("VoiceCall");

        checkCalling();
        callImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               checkVipUser();
            }
        });
        getMessages();
    }

    private void getIds() {
        Constants.databaseReference().child("AdmobId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    reward=snapshot.child("reward").getValue().toString();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

  /*  private void endCall() {
        Constants.databaseReference().child(Constants.USERS)
                .child(idFrom).child("accepted").setValue(null);
    }*/

    private void checkVipUser() {
        Constants.databaseReference().child(Constants.USERS)
                .child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            //for (DataSnapshot ds : snapshot.getChildren()){
                            UserModel model = snapshot.getValue(UserModel.class);
                            if (model.is_vip){
                                Intent intent = new Intent(ChatWithFriendSide.this, OutgoingCallActivity.class);
                                intent.putExtra("user",userModel);
                                startActivity(intent);
                            }else {
                                if (watched){
                                    Intent intent = new Intent(ChatWithFriendSide.this, OutgoingCallActivity.class);
                                    intent.putExtra("user",userModel);
                                    startActivity(intent);
                                    finish();
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

    private void showVipPurchase() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatWithFriendSide.this);
        LayoutInflater inflater = getLayoutInflater();
        View add_view = inflater.inflate(R.layout.vip_dialog_box,null);
        AppCompatButton vipBtn = add_view.findViewById(R.id.vip);
        AppCompatButton adsBtn = add_view.findViewById(R.id.ads);
        builder.setView(add_view);
        AlertDialog alertDialog = builder.create();
        vipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatWithFriendSide.this,VipServiceActivity.class));
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
                = MobileAds.getRewardedVideoAdInstance(ChatWithFriendSide.this);

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


    private void checkCalling() {
        voiceDB.child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                       if (snapshot.exists()){
                           String status = snapshot.child("status").getValue().toString();
                           String id = snapshot.child("id").getValue().toString();
                           //Toast.makeText(ChatWithFriendSide.this,id,Toast.LENGTH_LONG).show();
                           if (id.equals(idFrom)) {
                               if (status.equals("incoming")) {
                                   Intent intent = new Intent(ChatWithFriendSide.this, IncomingCallActivity.class);
                                   intent.putExtra("user", userModel);
                                   startActivity(intent);
                                   finish();
                               }
                               /*else if (status.equals("accepted")) {
                                   Intent intent = new Intent(ChatWithFriendSide.this, VoiceCallActivity.class);
                                   intent.putExtra("user", userModel);
                                   startActivity(intent);
                                   finish();
                               }*/
                           }
                       }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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