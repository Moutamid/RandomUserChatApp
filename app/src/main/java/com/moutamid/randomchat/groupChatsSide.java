package com.moutamid.randomchat;

import static androidx.recyclerview.widget.RecyclerView.VERTICAL;
import static com.bumptech.glide.Glide.with;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.DATA;
import static com.moutamid.randomchat.R.color.lighterGrey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class groupChatsSide extends AppCompatActivity {
    ActivityGroupChatsSideBinding b;

    String child;
    private String title;
    //private ArrayList<MessageModel> tasksArrayList = new ArrayList<>();

    private RecyclerView conversationRecyclerView;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private List<GroupChat> chatList;
    private DatabaseReference db;

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
        b.topAppBar.setTitle(title);
     //   b.topAppBar.setSubtitle("2 members");
        db = Constants.databaseReference().child("Groups")
                .child(child)
                .child(Constants.MESSAGES);
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
        getMessages();
    }

    private void sendMessages(String message) {
        String key = db.push().getKey();
        long timestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        GroupChat chat = new GroupChat(message,user.getUid(),timestamp);
        db.child(user.getUid()).child(String.valueOf(timestamp)).setValue(chat);
        b.message.setText("");
    }

    private void getMessages() {
        db.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    chatList.clear();
                    b.topAppBar.setSubtitle(String.valueOf(snapshot.getChildrenCount()) + " Members");
                    for (DataSnapshot ds : snapshot.getChildren()){
                        db.child(ds.getKey()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                if(snapshot1.exists()){
                                    for (DataSnapshot ds1 : snapshot1.getChildren()){
                                        GroupChat model = ds1.getValue(GroupChat.class);
                                        chatList.add(model);
                                    }

                                    initRecyclerView();
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
        conversationRecyclerView.setHasFixedSize(true);
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