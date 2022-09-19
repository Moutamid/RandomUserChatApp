package com.moutamid.randomchat.Adapters;

import static com.moutamid.randomchat.R.color.lighterGrey;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.randomchat.Models.Friends;
import com.moutamid.randomchat.Models.UserModel;
import com.moutamid.randomchat.R;
import com.moutamid.randomchat.utils.Constants;

import java.util.HashMap;
import java.util.List;

public class FriendRequestListAdapter extends RecyclerView.Adapter<FriendRequestListAdapter.FriendsViewHolder> {

    private Context context;
    private List<Friends> friendsList;

    public FriendRequestListAdapter(Context context, List<Friends> friendsList) {
        this.context = context;
        this.friendsList = friendsList;
    }

    @NonNull
    @Override
    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friend_request_list_view,parent,false);
        return new FriendsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Friends currentItem = friendsList.get(position);

        Constants.databaseReference().child(Constants.USERS)
                .child(currentItem.getUserId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            UserModel userModel = snapshot.getValue(UserModel.class);
                            holder.title.setText(userModel.getName());
                            if(userModel.getProfile_url().equals("")){

                                Glide.with(context)
                                        .asBitmap()
                                        .load(R.drawable.img)
                                        .apply(new RequestOptions()
                                                .placeholder(lighterGrey)
                                                .error(lighterGrey)
                                        )
                                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                                        .into(holder.img);
                            }else {

                                Glide.with(context)
                                        .asBitmap()
                                        .load(userModel.getProfile_url())
                                        .apply(new RequestOptions()
                                                .placeholder(lighterGrey)
                                                .error(lighterGrey)
                                        )
                                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                                        .into(holder.img);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("status","friends");
                String key = Constants.auth().getCurrentUser().getUid();
                Constants.databaseReference().child("Friends").child(key).child(currentItem.getUserId())
                        .updateChildren(hashMap);
                Constants.databaseReference().child("Friends").child(currentItem.getUserId()).child(key)
                        .updateChildren(hashMap);
                friendsList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeRemoved(position,friendsList.size());

            }
        });
        holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = Constants.auth().getCurrentUser().getUid();
                Constants.databaseReference().child("Friends").child(key).child(currentItem.getUserId()).removeValue();
                Constants.databaseReference().child("Friends").child(currentItem.getUserId()).child(key).removeValue();
                friendsList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeRemoved(position,friendsList.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public class FriendsViewHolder extends RecyclerView.ViewHolder{

        TextView title, bio;
        ImageView img;
        Button acceptBtn,cancelBtn;

        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.name);

//            bio = (TextView) itemView.findViewById(R.id.tvLastMsg);
            img = itemView.findViewById(R.id.profile);
            acceptBtn = itemView.findViewById(R.id.accept);
            cancelBtn = itemView.findViewById(R.id.cancel);

        }
    }
}
