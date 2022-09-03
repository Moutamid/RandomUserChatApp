package com.moutamid.randomchat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.randomchat.Adapters.HistoryAdapter;
import com.moutamid.randomchat.Models.Conversation;
import com.moutamid.randomchat.databinding.FragmentHistoryBinding;
import com.moutamid.randomchat.utils.Constants;


public class HistoryFragment extends Fragment {
    HistoryAdapter adapter;
    FragmentHistoryBinding binding;
    List<Conversation> list;


    public HistoryFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= FragmentHistoryBinding.inflate(getLayoutInflater());
        list=new ArrayList<>();
        setListData();
        binding.rec.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        return  binding.getRoot();
    }

    public void setListData(){
        Constants.databaseReference().child("Conversations")
                .child(Constants.auth().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            for (DataSnapshot ds : snapshot.getChildren()){
                                Conversation model = ds.getValue(Conversation.class);
                                list.add(model);
                            }
                            adapter = new HistoryAdapter(getActivity(), list);
                            binding.rec.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}