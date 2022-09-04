package com.moutamid.randomchat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fxn.stash.Stash;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.randomchat.Models.UserModel;
import com.moutamid.randomchat.databinding.FragmentChatingFramentBinding;
import com.moutamid.randomchat.databinding.FragmentHomeBinding;
import com.moutamid.randomchat.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatingFrament extends Fragment {
    FragmentChatingFramentBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ArrayList<String> connectionList;
    private String gender = "";
    private String lang = "";

    public ChatingFrament() {
        // Required empty public constructor
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentChatingFramentBinding.inflate(getLayoutInflater());
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        connectionList = new ArrayList<>();
        gender = Stash.getString("gender");
        lang = Stash.getString("lang");
      //  Toast.makeText(getActivity(),gender,Toast.LENGTH_LONG).show();
        binding.getRoot().setOnTouchListener(new OnSwipeListener(getContext()) {
            @SuppressLint("ClickableViewAccessibility")
            public void onSwipeTop() {
                checkVipUser();
             //   startActivity(new Intent(requireContext(), RandomChatActivity.class));
            }
            @SuppressLint("ClickableViewAccessibility")
            public void onSwipeRight() {
                Log.d("OnSwipeListener", "onSwipeRight");
            }
            @SuppressLint("ClickableViewAccessibility")
            public void onSwipeLeft() {
                Log.d("OnSwipeListener", "onSwipeLeft");
            }
            @SuppressLint("ClickableViewAccessibility")
            public void onSwipeBottom() {
                Log.d("OnSwipeListener", "onSwipeBottom");
            }
        });

        return  binding.getRoot();
    }

    private void checkVipUser() {
        Constants.databaseReference().child(Constants.USERS)
                .child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            //for (DataSnapshot ds : snapshot.getChildren()){
                            UserModel userModel = snapshot.getValue(UserModel.class);
                            if (userModel.is_vip){
                                filterUser();
                            }else {
                                storeRandomChatUser();
                            }
                            //}
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void filterUser() {
        Constants.databaseReference()
                .child(Constants.RANDOM_CHAT).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                //        boolean connection = (boolean) ds.child("connection").getValue();
                                String id = (String) ds.child("id").getValue();
                                Constants.databaseReference().child(Constants.USERS)
                                        .child(id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                                if (snapshot1.exists()){
                                                    UserModel model = snapshot1.getValue(UserModel.class);
                                                    if (model.getGender().equals(gender)){
                                                        storeRandomChatUser();
                                                    }else {
                                                        Toast.makeText(getActivity(), "Connection is not available now", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }
                        }else {
                            Toast.makeText(getActivity(), "Connection is not available now", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void storeVipRandomChatUser() {

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("id",user.getUid());
        hashMap.put("connection",true);

        Constants.databaseReference()
                .child(Constants.RANDOM_CHAT)
                .child(user.getUid())
                .setValue(hashMap);
        checkRandomCall();
        //filterUser();
    }
    private void storeRandomChatUser() {

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("id",user.getUid());
        hashMap.put("connection",true);

        Constants.databaseReference()
                .child(Constants.RANDOM_CHAT)
                .child(user.getUid())
                .setValue(hashMap);

        checkRandomCall();

        // startActivity(new Intent(getActivity(), RandomCallActivity.class));
    }
    private void checkRandomCall() {

        Constants.databaseReference()
                .child(Constants.RANDOM_CHAT)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            for (DataSnapshot ds : snapshot.getChildren()){
                                boolean connection = (boolean) ds.child("connection").getValue();
                                String id = (String) ds.child("id").getValue();
                                if (connection){
                                    if (!connectionList.contains(id)) {
                                        connectionList.add(id);
                                    }
                                    Toast.makeText(getActivity(), "" + connectionList.size(), Toast.LENGTH_SHORT).show();
                                    if (connectionList.size() == 1){
                                        Toast.makeText(getActivity(), "Connection is not available now", Toast.LENGTH_SHORT).show();
                                    }
                                    else if (connectionList.size() == 2){
                                        connectionList.clear();
                                        startActivity(new Intent(requireContext(), RandomChatActivity.class));
                                    }
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();
        //  setConnectionFalse();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        setConnectionFalse();
    }


    private void setConnectionFalse() {
        Constants.databaseReference()
                .child(Constants.RANDOM_CHAT)
                .child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            HashMap<String,Object> hashMap = new HashMap<>();
                            hashMap.put("connection",false);

                            Constants.databaseReference()
                                    .child(Constants.RANDOM_CHAT)
                                    .child(user.getUid())
                                    .updateChildren(hashMap);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}