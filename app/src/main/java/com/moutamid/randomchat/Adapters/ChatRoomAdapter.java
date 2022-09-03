package com.moutamid.randomchat.Adapters;

import static com.bumptech.glide.load.engine.DiskCacheStrategy.DATA;
import static com.moutamid.randomchat.R.color.lighterGrey;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.randomchat.Models.Chat;
import com.moutamid.randomchat.Models.UserModel;
import com.moutamid.randomchat.R;
import com.moutamid.randomchat.utils.Constants;

import org.w3c.dom.Text;

import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_INCOMING = 0;
    private final int TYPE_OUTGOING = 1;
    List<Chat> chatList;
    private Context mContext;


    public ChatRoomAdapter(Context context, List<Chat> chats) {
        this.mContext = context;
        this.chatList = chats;
    }

    @Override
    public int getItemViewType(int position) {
        Chat chat = chatList.get(position);

       /* if (position == 0){
            return TYPE_TIME;
        }*/
        return tes(chat);
      }

    private int tes(Chat chat) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser().getUid().equalsIgnoreCase(chat.getSenderUid())) {
            return TYPE_OUTGOING;
        }
        return TYPE_INCOMING;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
       if(viewType == TYPE_INCOMING){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_incoming, parent, false);
            return new IncomingViewHolder(view);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_outgoing, parent, false);
            return new OutgoingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
         if (holder.getItemViewType() == TYPE_INCOMING) {
            IncomingViewHolder holder1 = (IncomingViewHolder) holder;
            configureViewHolderIncoming(holder1, position);
        }  else{
            OutgoingViewHolder holder2 = (OutgoingViewHolder) holder;
            configureViewholderOutgoing(holder2, position);
        }
    }



    @Override
    public int getItemCount() {
        return chatList.size();
    }

    private void configureViewHolderIncoming(final IncomingViewHolder holder, int position) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        Chat chat = (Chat) chatList.get(position);
        if (chat != null) {
            getProfilePic(chat.getSenderUid(), holder.imageView);
            holder.message.setText(chat.getMessage());
        }

    }



    private void configureViewholderOutgoing(final OutgoingViewHolder holder, int position) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userUid = mAuth.getCurrentUser().getUid();
        Chat chat = (Chat) chatList.get(position);
        if (chat != null) {
            getProfilePic(chat.getReceiverUid(),holder.imageView);
            holder.message.setText(chat.getMessage());
        }
    }
    private void getProfilePic(String receiverUid, CircleImageView imageView) {

        //  Toast.makeText(mContext,receiverUid,Toast.LENGTH_LONG).show();

        DatabaseReference mUserReference = Constants.databaseReference().child(Constants.USERS).child(receiverUid);
        mUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                if (ds.exists()){
                    UserModel model = ds.getValue(UserModel.class);
                    if (model.getProfile_url().equals("")){
                        Glide.with(mContext)
                                .asBitmap()
                                .load(R.drawable.img)
                                .apply(new RequestOptions()
                                        .placeholder(lighterGrey)
                                        .error(lighterGrey)
                                )
                                .diskCacheStrategy(DATA)
                                .into(imageView);
                    }else{
                        Glide.with(mContext)
                                .asBitmap()
                                .load(model.getProfile_url())
                                .apply(new RequestOptions()
                                        .placeholder(lighterGrey)
                                        .error(lighterGrey)
                                )
                                .diskCacheStrategy(DATA)
                                .into(imageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public class IncomingViewHolder extends RecyclerView.ViewHolder {
        private TextView message;
        private ConstraintLayout constraintLayout;
        private RelativeLayout layout;
        private CircleImageView imageView;

        public IncomingViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView2);
            message = itemView.findViewById(R.id.textView);
            constraintLayout = itemView.findViewById(R.id.layout_first_incoming);
            layout = itemView.findViewById(R.id.layout_chat_incoming);

        }
    }

    public class OutgoingViewHolder extends RecyclerView.ViewHolder {
        private TextView message, reply_message, username;
        private ConstraintLayout constraintLayout;
        private RelativeLayout layout;
        private CircleImageView imageView;

        public OutgoingViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView2);
            message = itemView.findViewById(R.id.textView);
            constraintLayout = itemView.findViewById(R.id.layout_first);
            layout = itemView.findViewById(R.id.layout_chat);


        }
    }
}
