package com.moutamid.randomchat.VoiceCall;

import static com.moutamid.randomchat.R.color.lighterGrey;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.randomchat.ChatWithFriendSide;
import com.moutamid.randomchat.Models.UserModel;
import com.moutamid.randomchat.R;
import com.moutamid.randomchat.utils.Constants;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class OutgoingCallActivity extends AppCompatActivity {

    private ImageView cancelCallButt;
    private TextView receiverNameText;
    private CircleImageView receiverImage;
    private static UserModel otherUser;
    private TextView callingInProcess;
    private DatabaseReference db;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing_call);
        receiverImage = findViewById(R.id.senderImage);
        receiverNameText = (TextView) findViewById(R.id.senderNameText);
        callingInProcess = (TextView) findViewById(R.id.callingInProcess);
        cancelCallButt = (ImageView) findViewById(R.id.cancelCallButt);
        try {
            currentUser = Constants.auth().getCurrentUser();
        } catch(Exception e) {}
        otherUser = getIntent().getParcelableExtra("user");

        db = Constants.databaseReference().child("VoiceCall");
        // On end
        startCall();
        checkCalling();
        cancelCallButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelCall();
            }
        });
    }

    private void checkCalling() {
        db.child(otherUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String status = snapshot.child("status").getValue().toString();
                            String id = snapshot.child("id").getValue().toString();
                            if (status.equals("accepted")){
                                Intent intent = new Intent(OutgoingCallActivity.this, VoiceCallActivity.class);
                                intent.putExtra("user", otherUser);
                                startActivity(intent);
                                finish();
                            }
                        }else {
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void cancelCall() {
        db.child(currentUser.getUid()).removeValue();
        db.child(otherUser.getUid()).removeValue();
        finish();
    }

    private void startCall() {

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("id",otherUser.getUid());
        hashMap.put("status","calling");
        db.child(currentUser.getUid()).setValue(hashMap);

        HashMap<String,Object> hashMap2 = new HashMap<>();
        hashMap2.put("id",currentUser.getUid());
        hashMap2.put("status","incoming");
        db.child(otherUser.getUid()).setValue(hashMap2);


        setParams();
    }

    private void setParams() {
        if(otherUser.getProfile_url().equals("")){

            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(R.drawable.img)
                    .apply(new RequestOptions()
                            .placeholder(lighterGrey)
                            .error(lighterGrey)
                    )
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(receiverImage);
        }else {

            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(otherUser.getProfile_url())
                    .apply(new RequestOptions()
                            .placeholder(lighterGrey)
                            .error(lighterGrey)
                    )
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(receiverImage);
        }

        receiverNameText.setText(otherUser.getName());
    }

  /*  private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent finalIntent) {
            String type = finalIntent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if (type != null) {
                if (type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)) {
                    Intent intent = new Intent(OutgoingCallActivity.this, VoiceCallActivity.class);
                    intent.putExtra("user",otherUser);
                    startActivity(intent);
                } else if (type.equals(Constants.REMOTE_MSG_INVITATION_REJECTED)) {
                    finish();
                }else {
                    Toast.makeText(OutgoingCallActivity.this, "Invitation Cancelled", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        //  initView();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                invitationResponseReceiver,
                new IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE)
        );
    }
    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                invitationResponseReceiver
        );
        resetWindowFlags();
        finish();
    }

    private void resetWindowFlags() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }*/

}