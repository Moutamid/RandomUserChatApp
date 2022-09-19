package com.moutamid.randomchat;

import static androidx.recyclerview.widget.RecyclerView.VERTICAL;
import static com.bumptech.glide.Glide.with;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.DATA;
import static com.moutamid.randomchat.R.color.lighterGrey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.randomchat.Models.GroupChat;
import com.moutamid.randomchat.Models.UserModel;
import com.moutamid.randomchat.databinding.ActivityGroupChatsSideBinding;
import com.moutamid.randomchat.utils.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class groupChatsSide extends AppCompatActivity {
    ActivityGroupChatsSideBinding b;
    private InterstitialAd mInterstitialAd;

    String child;
    private String title;
    //private ArrayList<MessageModel> tasksArrayList = new ArrayList<>();

    private RecyclerView conversationRecyclerView;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private List<GroupChat> chatList;
    private DatabaseReference db,db1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityGroupChatsSideBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        child = getIntent().getStringExtra(Constants.PARAMS);
        title = getIntent().getStringExtra("groupName");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        chatList = new ArrayList<>();
        b.name.setText(title);
        initRecyclerView();
     //   b.topAppBar.setSubtitle("2 members");
        db = Constants.databaseReference().child("Groups")
                .child(child);
        db1 = Constants.databaseReference().child("AdmobId");
        b.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = b.message.getText().toString();
                if (message.isEmpty()){
                    b.send.setEnabled(false);
                }else {
                    b.send.setEnabled(true);
                    sendMessages(message);
                }
            }
        });
        b.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(groupChatsSide.this,MainActivity.class));
            }
        });
        getIds();
        getMembers();
        getMessages();
    }

    private void getIds() {
        db1.addValueEventListener(new ValueEventListener() {
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


    private void getMembers() {
        db.child("Members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    b.total.setText(String.valueOf(snapshot.getChildrenCount()) + " Members");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessages(String message) {
        long timestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        GroupChat chat = new GroupChat(message,user.getUid(),timestamp);
        db.child("Messages").child(String.valueOf(timestamp)).setValue(chat);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("id",user.getUid());
        db.child("Members").child(user.getUid()).setValue(hashMap);
        b.message.setText("");
    }

    private void getMessages() {
        db.child("Messages").orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    chatList.clear();
                 //   b.topAppBar.setSubtitle(String.valueOf(snapshot.getChildrenCount()) + " Members");
                    for (DataSnapshot ds : snapshot.getChildren()){
                        GroupChat model = ds.getValue(GroupChat.class);
                        chatList.add(model);
                    }
                    Collections.sort(chatList, new Comparator<GroupChat>() {
                        @Override
                        public int compare(GroupChat groupChat, GroupChat t1) {
                            return Long.compare(groupChat.getTimestamp(),t1.getTimestamp());
                        }
                    });
                    RecyclerViewAdapterMessages adapter = new RecyclerViewAdapterMessages();
                    conversationRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void initRecyclerView() {

        conversationRecyclerView = findViewById(R.id.groupChatrecyclerView);
        //conversationRecyclerView.addItemDecoration(new DividerItemDecoration(conversationRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        //        LinearLayoutManager layoutManagerUserFriends = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        //    int numberOfColumns = 3;
        //int mNoOfColumns = calculateNoOfColumns(getApplicationContext(), 50);
        //  recyclerView.setLayoutManager(new GridLayoutManager(this, mNoOfColumns));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(VERTICAL);
        //linearLayoutManager.setReverseLayout(true);
        conversationRecyclerView.setLayoutManager(linearLayoutManager);
       // conversationRecyclerView.setHasFixedSize(true);
        conversationRecyclerView.setNestedScrollingEnabled(false);



    }

    private class RecyclerViewAdapterMessages extends RecyclerView.Adapter
            <RecyclerViewAdapterMessages.ViewHolderRightMessage> {

        @NonNull
        @Override
        public ViewHolderRightMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row, parent, false);
            return new ViewHolderRightMessage(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolderRightMessage holder, int position) {
            GroupChat model = chatList.get(position);
            holder.message.setText(model.getMessage());

            Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
            calendar.setTimeInMillis(model.getTimestamp() * 1000);
            long tes = calendar.getTimeInMillis();
            DateFormat.format("M/dd/yyyy", calendar);
            CharSequence now = DateUtils.getRelativeTimeSpanString(tes, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
            holder.time.setText(now);

            DatabaseReference mUserReference = Constants.databaseReference().child(Constants.USERS).child(model.getSenderUid());
            mUserReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot ds) {
                    if (ds.exists()){
                        UserModel model = ds.getValue(UserModel.class);
                        holder.name.setText(model.getName());
                        if (model.getProfile_url().equals("")){
                            Glide.with(groupChatsSide.this)
                                    .asBitmap()
                                    .load(R.drawable.img)
                                    .apply(new RequestOptions()
                                            .placeholder(lighterGrey)
                                            .error(lighterGrey)
                                    )
                                    .diskCacheStrategy(DATA)
                                    .into(holder.profile);
                        }else{
                            Glide.with(groupChatsSide.this)
                                    .asBitmap()
                                    .load(model.getProfile_url())
                                    .apply(new RequestOptions()
                                            .placeholder(lighterGrey)
                                            .error(lighterGrey)
                                    )
                                    .diskCacheStrategy(DATA)
                                    .into(holder.profile);
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            if (model.getSenderUid().equals(user.getUid())){
                holder.profile.setEnabled(false);
            }else {
                holder.profile.setEnabled(true);
            }

            holder.profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(groupChatsSide.this,UserProfileFragment.class);
                    intent.putExtra("id",model.getSenderUid());
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            if (chatList == null)
                return 0;
            return chatList.size();
        }

        public class ViewHolderRightMessage extends RecyclerView.ViewHolder {

            TextView name, message, time;
            ImageView profile;

            public ViewHolderRightMessage(@NonNull View v) {
                super(v);
                name = v.findViewById(R.id.text_gchat_user_other);
                message = v.findViewById(R.id.text_gchat_message_other);
                time = v.findViewById(R.id.text_gchat_timestamp_other);
                profile = v.findViewById(R.id.image_gchat_profile_other);

            }
        }

    }

}