package com.moutamid.randomchat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.randomchat.Adapters.FriendListAdapter;
import com.moutamid.randomchat.Models.Friends;
import com.moutamid.randomchat.Models.GroupsModel;
import com.moutamid.randomchat.databinding.FragmentFriendListBinding;
import com.moutamid.randomchat.utils.Constants;


public class FriendListFragment extends Fragment {

FragmentFriendListBinding binding;
    FriendListAdapter adapter;
    List<Friends> list;
    private DatabaseReference db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Context mContext;


    public FriendListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = MainActivity.context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       binding=FragmentFriendListBinding.inflate(getLayoutInflater());

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = Constants.databaseReference().child("Friends");
        list = new ArrayList<>();
        this.mContext = MainActivity.context;
        if (mContext != null) {
            binding.rec.setLayoutManager(new LinearLayoutManager(mContext));
            getFriendsList();
        }
        binding.friendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext,FriendsRequestScreen.class));
            }
        });
        return binding.getRoot();

    }

    private void getFriendsList() {
        db.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    list.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Friends model = ds.getValue(Friends.class);
                        if (model.getStatus().equals("friends")) {
                            list.add(model);
                        }
                    }
                    adapter = new FriendListAdapter(getActivity(), list);
                    binding.rec.setAdapter(adapter);
                    adapter.setOnItemClick(new FriendListAdapter.OnitemClickListener() {
                        @Override
                        public void OnItemClick(int position, View view) {
                            showPopup(list.get(position),position,view);
                        }

                        @Override
                        public void onaddclick(int position) {

                        }
                    });
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showPopup(Friends model, int position, View view) {
        PopupMenu popupMenu = new PopupMenu(getActivity(),view);
        popupMenu.inflate(R.menu.menu_main);
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.action_delete:
                        deleteUser(model.getUserId(),position);
                        break;
                }

                return true;
            }
        });
    }

    private void deleteUser(String userId, int position) {
        db.child(user.getUid()).child(userId).removeValue();
        list.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeRemoved(position, list.size());

    }

}