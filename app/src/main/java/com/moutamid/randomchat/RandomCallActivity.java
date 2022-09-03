package com.moutamid.randomchat;

import static com.bumptech.glide.Glide.with;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.DATA;
import static com.moutamid.randomchat.R.color.lighterGrey;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.randomchat.Adapters.MessagesAdapter;
import com.moutamid.randomchat.Models.Conversation;
import com.moutamid.randomchat.Models.UserModel;
import com.moutamid.randomchat.databinding.ActivityRandomChatBinding;
import com.moutamid.randomchat.databinding.FragmentBlankBinding;
import com.moutamid.randomchat.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import okhttp3.OkHttpClient;

public class RandomCallActivity extends AppCompatActivity {

    RecyclerView rec;
    List<String> list;
    MessagesAdapter adapter;
    Boolean status = false;
    private FragmentBlankBinding b;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference db;
    private static final String LOG_TAG = RandomCallActivity.class.getSimpleName();

    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;
    private String url = "https://kainatkhan46.herokuapp.com/access_token?";

    private RtcEngine mRtcEngine; // Tutorial Step 1
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() { // Tutorial Step 1

        /**
         * Occurs when a remote user (Communication)/host (Live Broadcast) leaves the channel.
         *
         * There are two reasons for users to become offline:
         *
         *     Leave the channel: When the user/host leaves the channel, the user/host sends a goodbye message. When this message is received, the SDK determines that the user/host leaves the channel.
         *     Drop offline: When no data packet of the user or host is received for a certain period of time (20 seconds for the communication profile, and more for the live broadcast profile), the SDK assumes that the user/host drops offline. A poor network connection may lead to false detections, so we recommend using the Agora RTM SDK for reliable offline detection.
         *
         * @param uid ID of the user or host who
         * leaves
         * the channel or goes offline.
         * @param reason Reason why the user goes offline:
         *
         *     USER_OFFLINE_QUIT(0): The user left the current channel.
         *     USER_OFFLINE_DROPPED(1): The SDK timed out and the user dropped offline because no data packet was received within a certain period of time. If a user quits the call and the message is not passed to the SDK (due to an unreliable channel), the SDK assumes the user dropped offline.
         *     USER_OFFLINE_BECOME_AUDIENCE(2): (Live broadcast only.) The client role switched from the host to the audience.
         */
        @Override
        public void onUserOffline(final int uid, final int reason) { // Tutorial Step 4
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserLeft(uid, reason);
                }
            });
        }

        /**
         * Occurs when a remote user stops/resumes sending the audio stream.
         * The SDK triggers this callback when the remote user stops or resumes sending the audio stream by calling the muteLocalAudioStream method.
         *
         * @param uid ID of the remote user.
         * @param muted Whether the remote user's audio stream is muted/unmuted:
         *
         *     true: Muted.
         *     false: Unmuted.
         */
        @Override
        public void onUserMuteAudio(final int uid, final boolean muted) { // Tutorial Step 6
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserVoiceMuted(uid, muted);
                }
            });
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = FragmentBlankBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        list = new ArrayList<>();

        rec = findViewById(R.id.recMessages);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = Constants.databaseReference().child("Conversations");

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)) {
            initAgoraEngineAndJoinChannel();
            getUserData();
        }


        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(RandomCallActivity.this);
        bottomSheetDialog.setContentView(R.layout.send_message_botttom_sheet);
        EditText message = bottomSheetDialog.findViewById(R.id.edit_gchat_message);
        findViewById(R.id.cardMessaging).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.show();
                bottomSheetDialog.findViewById(R.id.button_gchat_send).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!status) {
                            list.add(message.getText().toString());
                            rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));//
                            adapter = new MessagesAdapter(getApplicationContext(), list);//this is for sending our data to adapter so adapter can display here we are sending a list
                            rec.setAdapter(adapter);
                        } else {
                            list.add(message.getText().toString());
                            adapter.notifyDataSetChanged();
                        }
                        status = true;

                    }
                });

            }
        });
    }

    public boolean checkSelfPermission(String permission, int requestCode) {
        Log.i(LOG_TAG, "checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestCode);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(LOG_TAG, "onRequestPermissionsResult " + grantResults[0] + " " + requestCode);

        switch (requestCode) {
            case PERMISSION_REQ_ID_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initAgoraEngineAndJoinChannel();
                    getUserData();
                } else {
                    showLongToast("No permission for " + Manifest.permission.RECORD_AUDIO);
                    finish();
                }
                break;
            }
        }
    }

    public final void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }
    private void initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine();     // Tutorial Step 1
        joinChannel();               // Tutorial Step 2
    }

    // Tutorial Step 7
    public void onLocalAudioMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.purple_500), PorterDuff.Mode.MULTIPLY);
        }

        // Stops/Resumes sending the local audio stream.
        mRtcEngine.muteLocalAudioStream(iv.isSelected());
    }

    // Tutorial Step 5
    public void onSwitchSpeakerphoneClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.purple_500), PorterDuff.Mode.MULTIPLY);
        }

        // Enables/Disables the audio playback route to the speakerphone.
        //
        // This method sets whether the audio is routed to the speakerphone or earpiece. After calling this method, the SDK returns the onAudioRouteChanged callback to indicate the changes.
        mRtcEngine.setEnableSpeakerphone(view.isSelected());
    }

    // Tutorial Step 3
    public void onEncCallClicked(View view) {
        leaveChannel();
        //RtcEngine.destroy();
        //mRtcEngine = null;
        finish();
    }

    // Tutorial Step 1
    private void initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));

            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    String newToken;
    // Tutorial Step 2
    private void joinChannel() {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    //Your code goes here
                    newToken = fetchToken(url, "ChatRoom", 0);
                    mRtcEngine.setChannelProfile(io.agora.rtc.Constants.CHANNEL_PROFILE_COMMUNICATION);

                    // Allows a user to join a channel.
                    mRtcEngine.joinChannel(newToken, "ChatRoom", "Extra Optional Data", 0); // if you do not specify the uid, we will generate the uid for you
             //       mRtcEngine.setEnableSpeakerphone(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();


    }


    String fetchToken(String urlBase, String channelName, int userId) {
      //  Logger log = LoggerFactory.getLogger("AgoraTokenRequester");

        OkHttpClient client = new OkHttpClient();
        String url = urlBase + "channel=" + channelName + "&uid=" + userId;

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .method("GET", null)
                .build();

        try (okhttp3.Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
              //  log.debug("Unexpected code " + response);
            } else {
                JSONObject jObject = new JSONObject(response.body().string());
                return jObject.getString("token");
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return "";
    }


    // Tutorial Step 3
    private void leaveChannel() {
        mRtcEngine.leaveChannel();
        Constants.databaseReference()
                .child(Constants.RANDOM_CALL)
                .child(user.getUid())
                .removeValue();
    }

    // Tutorial Step 4
    private void onRemoteUserLeft(int uid, int reason) {
        showLongToast(String.format(Locale.US, "user %d left %d", (uid & 0xFFFFFFFFL), reason));
       // View tipMsg = findViewById(R.id.quick_tips_when_use_agora_sdk); // optional UI
       // tipMsg.setVisibility(View.VISIBLE);
    }

    // Tutorial Step 6
    private void onRemoteUserVoiceMuted(int uid, boolean muted) {
        showLongToast(String.format(Locale.US, "user %d muted or unmuted %b", (uid & 0xFFFFFFFFL), muted));
    }
    private void getUserData() {
        Constants.databaseReference()
                .child(Constants.RANDOM_CALL)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                //UserModel userModel = dataSnapshot.getValue(UserModel.class);
                                String key = dataSnapshot.getKey().toString();
                                // Toast.makeText(RandomCallActivity.this,dataSnapshot.getKey().toString(),Toast.LENGTH_LONG).show();
                                if (!key.equals(user.getUid())) {

                                    Constants.databaseReference()
                                            .child(Constants.USERS)
                                            .child(key)
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                                    if (snapshot1.exists()){
                                                        UserModel userModel = snapshot1.getValue(UserModel.class);
                                                        b.UserName.setText(userModel.name);
                                                        with(getApplicationContext())
                                                                .asBitmap()
                                                                .load(userModel.profile_url)
                                                                .apply(new RequestOptions()
                                                                        .placeholder(lighterGrey)
                                                                        .error(lighterGrey)
                                                                )
                                                                .diskCacheStrategy(DATA)
                                                                .into(b.imageView2);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                    long timestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                                    Conversation conversationSender = new Conversation(user.getUid(),key,timestamp);
                                    db.child(user.getUid())
                                            .child(key)
                                            //.child(String.valueOf(timestamp))
                                            .setValue(conversationSender);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();

        leaveChannel();
        RtcEngine.destroy();
        mRtcEngine = null;
        Constants.databaseReference()
                .child(Constants.RANDOM_CALL)
                .child(user.getUid())
                .removeValue();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        leaveChannel();
        sendMainActivity();
    }
    private void sendMainActivity() {
        startActivity(new Intent(RandomCallActivity.this,MainActivity.class));
        finish();
    }

}