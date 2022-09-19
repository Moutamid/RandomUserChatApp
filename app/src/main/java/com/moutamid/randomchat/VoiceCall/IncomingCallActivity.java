package com.moutamid.randomchat.VoiceCall;

import static com.moutamid.randomchat.R.color.lighterGrey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

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

public class IncomingCallActivity extends AppCompatActivity {

    private CircleImageView senderImage;
    private ImageView acceptVideoCallButt;
    private TextView senderNameText;
    private ImageView declineVideoCalButt;

    private static FirebaseUser currentUser;
    public static UserModel otherUser;
    private MediaPlayer mediaPlayer;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // to wake up screen
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        int wakeFlags = PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP;
        if (Build.VERSION.SDK_INT <= 15) {
            wakeFlags |= PowerManager.SCREEN_BRIGHT_WAKE_LOCK;
        }

        PowerManager.WakeLock wakeLock = pm.newWakeLock(wakeFlags, "MyApp:Call");
        wakeLock.acquire(30000);
        try {
            currentUser = Constants.auth().getCurrentUser();
        } catch(Exception e) {}
        mediaPlayer = MediaPlayer.create(this, R.raw.calling);
        mediaPlayer.start();
        senderImage = findViewById(R.id.senderImage);
        senderNameText = (TextView) findViewById(R.id.senderNameText);
        acceptVideoCallButt = (ImageView) findViewById(R.id.acceptVideoCallButt);
        declineVideoCalButt = (ImageView) findViewById(R.id.declineVideoCalButt);
        otherUser = getIntent().getParcelableExtra("user");
        setParams();
        db = Constants.databaseReference().child("VoiceCall");
        checkCalling();
        acceptVideoCallButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptCall();
            }
        });

        declineVideoCalButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                declineCall();
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
                                mediaPlayer.stop();
                                Intent intent = new Intent(IncomingCallActivity.this, VoiceCallActivity.class);
                                intent.putExtra("user", otherUser);
                                startActivity(intent);
                                finish();
                            }
                        }else {
                            mediaPlayer.stop();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void acceptCall() {

        HashMap<String,Object> hashMap = new HashMap<>();
      //  hashMap.put("id",otherUser.getUid());
        hashMap.put("status","accepted");
        db.child(currentUser.getUid()).updateChildren(hashMap);

        HashMap<String,Object> hashMap2 = new HashMap<>();
      //  hashMap2.put("id",currentUser.getUid());
        hashMap2.put("status","accepted");
        db.child(otherUser.getUid()).updateChildren(hashMap2);

    }

    private void declineCall() {
        mediaPlayer.stop();
        db.child(currentUser.getUid()).removeValue();
        db.child(otherUser.getUid()).removeValue();
        finish();
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
                    .into(senderImage);
        }else {

            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(otherUser.getProfile_url())
                    .apply(new RequestOptions()
                            .placeholder(lighterGrey)
                            .error(lighterGrey)
                    )
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(senderImage);
        }

        senderNameText.setText(otherUser.getName());

    }


}