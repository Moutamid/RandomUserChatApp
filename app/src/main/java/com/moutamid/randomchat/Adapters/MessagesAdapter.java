package com.moutamid.randomchat.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import com.moutamid.randomchat.R;

//adapter is a class which we used to show list of data for example this adapter is used to show all the compaings in the project
public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.View_Holder> {
    private MessagesAdapter.OnitemClickListener mListener;

    public interface OnitemClickListener {
        void OnItemClick(int position);//

        void onaddclick(int position);

    }

    public void setOnItemClick(MessagesAdapter.OnitemClickListener listener) {
        mListener = listener;
    }

    LayoutInflater layoutInflater;
    List<String> users;


    public MessagesAdapter(Context ctx, List<String> users) {
        this.layoutInflater = LayoutInflater.from(ctx);
        this.users = users;
    }

    @NonNull
    @Override
    public View_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.message_view, parent, false);//here we define what view is our adapter showing here we are showing row_all_compaings view which you can see in res->layout
        return new View_Holder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull View_Holder holder, int position) {
        String currentItem = users.get(position);
        holder.title.setText(users.get(position));//here we are defining our data what we have to show it is coming from tha api



    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class View_Holder extends RecyclerView.ViewHolder {
        TextView title, description, createdBy, support,date;

        public View_Holder(@NonNull View itemView, final MessagesAdapter.OnitemClickListener listener) {
            super(itemView);
            //here we are initializing our components that were in the roww_all_views
            title = (TextView) itemView.findViewById(R.id.tvMessage);


        }
    }
}


