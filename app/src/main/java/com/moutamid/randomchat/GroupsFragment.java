package com.moutamid.randomchat;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.randomchat.Adapters.GroupsAdapter;
import com.moutamid.randomchat.Models.GroupsModel;
import com.moutamid.randomchat.databinding.FragmentGroupsBinding;
import com.moutamid.randomchat.utils.Constants;

public class GroupsFragment extends Fragment {

    FragmentGroupsBinding b;
    GroupsAdapter adapter;
    List<GroupsModel> list;

    public GroupsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentGroupsBinding.inflate(getLayoutInflater());

        list = new ArrayList<>();
        b.rec.setLayoutManager(new LinearLayoutManager(getActivity()));

        b.cardCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),CreateGroupActivity.class));
            }
        });
        getGroupList();
        return b.getRoot();
    }

    private void getGroupList() {
        Constants.databaseReference().child("Groups")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            list.clear();
                            for (DataSnapshot ds : snapshot.getChildren()){
                                GroupsModel model = ds.getValue(GroupsModel.class);
                                list.add(model);
                            }
                            adapter = new GroupsAdapter(getActivity(),list);
                            b.rec.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }else {
                            b.cardCreateGroup.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}