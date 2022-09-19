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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.moutamid.randomchat.Models.GroupsModel;
import com.moutamid.randomchat.R;
import com.moutamid.randomchat.groupChatsSide;
import com.moutamid.randomchat.utils.Constants;

//adapter is a class which we used to show list of data for example this adapter is used to show all the compaings in the project
public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.View_Holder> {
    private GroupsAdapter.OnitemClickListener mListener;

    public interface OnitemClickListener {
        void OnItemClick(int position);//

        void onaddclick(int position);

    }

    public void setOnItemClick(GroupsAdapter.OnitemClickListener listener) {
        mListener = listener;
    }

    LayoutInflater layoutInflater;
    List<GroupsModel> users;
    Context context;

    public GroupsAdapter(Context ctx, List<GroupsModel> users) {
        this.context = ctx;
    //    this.layoutInflater = LayoutInflater.from(ctx);
        this.users = users;
    }

    @NonNull
    @Override
    public View_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.groups_items, parent, false);//here we define what view is our adapter showing here we are showing row_all_compaings view which you can see in res->layout
        return new View_Holder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull View_Holder holder, int position) {
        GroupsModel currentItem = users.get(position);

        holder.title.setText(currentItem.name);
        holder.bio.setText(currentItem.desc);
        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(currentItem.getFlag())
                .apply(new RequestOptions()
                        .placeholder(lighterGrey)
                        .error(lighterGrey)
                )
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(holder.flag);

     /*   if(currentItem.getProfile_url().equals("")){

            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(R.drawable.img)
                    .apply(new RequestOptions()
                            .placeholder(lighterGrey)
                            .error(lighterGrey)
                    )
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(holder.img);
        }else {

            Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(currentItem.profile_url)
                    .apply(new RequestOptions()
                            .placeholder(lighterGrey)
                            .error(lighterGrey)
                    )
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(holder.img);
        }*/
        ColorGenerator generator = ColorGenerator.MATERIAL;
        String test = currentItem.getName();
        String s = test.substring(0, 1);

        if (Character.isDigit(test.charAt(0))) {
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound("#", generator.getRandomColor());
            holder.img.setImageDrawable(drawable);

            //ivThumbnail.setImageResource(R.drawable.chaticon);
        } else {
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(s, generator.getRandomColor());

            holder.img.setImageDrawable(drawable);
        }

        holder.chatRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,groupChatsSide.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constants.PARAMS, currentItem.getPush_key());
                intent.putExtra("groupName", currentItem.getName());
                holder.itemView.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class View_Holder extends RecyclerView.ViewHolder {
        TextView title, bio;
        ImageView img,flag;
        ConstraintLayout chatRow;

        public View_Holder(@NonNull View itemView, final GroupsAdapter.OnitemClickListener listener) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tvName);
            bio = (TextView) itemView.findViewById(R.id.tvLastMsg);
            img = itemView.findViewById(R.id.profile_images);
            chatRow = itemView.findViewById(R.id.chat_row_container);
            flag = itemView.findViewById(R.id.imageView6);
        }

    }
}


