package com.moutamid.randomchat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


    public FriendListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       binding=FragmentFriendListBinding.inflate(getLayoutInflater());

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = Constants.databaseReference().child("Friends");
        list = new ArrayList<>();
        binding.rec.setLayoutManager(new LinearLayoutManager(getActivity()));
        getFriendsList();
        return binding.getRoot();

    }

    private void getFriendsList() {
        db.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Friends model = ds.getValue(Friends.class);
                        if (model.getStatus().equals("friends")) {
                            list.add(model);
                        }
                    }
                    adapter = new FriendListAdapter(getActivity(), list);
                    binding.rec.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setListData() {
        for (int i = 0; i < 10; i++) {
//            GroupsModel data = new GroupsModel(2, R.drawable.img, "Ali Ahmed", "lets chat");
//            list.add(data);
        }
    }
}