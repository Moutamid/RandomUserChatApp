package com.moutamid.randomchat.Adapters;


import static com.moutamid.randomchat.R.color.lighterGrey;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.randomchat.Models.Conversation;
import com.moutamid.randomchat.Models.UserModel;
import com.moutamid.randomchat.R;
import com.moutamid.randomchat.UserProfileFragment;
import com.moutamid.randomchat.utils.Constants;

//adapter is a class which we used to show list of data for example this adapter is used to show all the compaings in the project
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.View_Holder> {
    private HistoryAdapter.OnitemClickListener mListener;

    public interface OnitemClickListener {
        void OnItemClick(int position);//
        void onaddclick(int position);

    }

    public void setOnItemClick(HistoryAdapter.OnitemClickListener listener) {
        mListener = listener;
    }

    LayoutInflater layoutInflater;
    List<Conversation> users;
    Context context;


    public HistoryAdapter(Context ctx, List<Conversation> users) {
        this.context = ctx;
      //  this.layoutInflater = LayoutInflater.from(ctx);
        this.users = users;
    }

    @NonNull
    @Override
    public View_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_item, parent, false);//here we define what view is our adapter showing here we are showing row_all_compaings view which you can see in res->layout
        return new View_Holder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull View_Holder holder, int position) {
        Conversation currentItem = users.get(position);
        Constants.databaseReference().child(Constants.USERS)
                        .child(currentItem.getChatWithId()).addValueEventListener(new ValueEventListener() {
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
                                        .load(userModel.profile_url)
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,UserProfileFragment.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("id",currentItem.getChatWithId());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class View_Holder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView img;

        public View_Holder(@NonNull View itemView, final HistoryAdapter.OnitemClickListener listener) {
            super(itemView);
            //here we are initializing our components that were in the roww_all_views
            title = (TextView) itemView.findViewById(R.id.userNames);
            img=itemView.findViewById(R.id.imgProfile);


        }
    }
}


