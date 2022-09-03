package com.moutamid.randomchat;

import static com.bumptech.glide.Glide.with;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.DATA;
import static com.moutamid.randomchat.R.color.lighterGrey;
import static com.moutamid.randomchat.utils.Constants.userModel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.randomchat.databinding.FragmentHomeBinding;
import com.moutamid.randomchat.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;

public class Home extends Fragment {
    FragmentHomeBinding b;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ArrayList<String> connectionList;

    public Home() {
        // Required empty public constructor
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        b = FragmentHomeBinding.inflate(getLayoutInflater());

        b.Name.setText(userModel().name);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        connectionList = new ArrayList<>();
        with(requireActivity().getApplicationContext())
                .asBitmap()
                .load(userModel().profile_url)
                .apply(new RequestOptions()
                        .placeholder(lighterGrey)
                        .error(lighterGrey)
                )
                .diskCacheStrategy(DATA)
                .into(b.profileImg);

        b.getRoot().setOnTouchListener(new OnSwipeListener(getContext()) {
            @SuppressLint("ClickableViewAccessibility")
            public void onSwipeTop() {
                storeRandomChatUser();
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
        b.imgGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.cardGender.setVisibility(View.VISIBLE);
            }
        });
        b.imgLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.carLanguage.setVisibility(View.VISIBLE);
            }
        });
        b.rdMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.cardGender.setVisibility(View.GONE);
            }
        });
        b.rdEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.carLanguage.setVisibility(View.GONE);
            }
        });
        b.tvVipServcice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), VipServiceActivity.class));
            }
        });
        //checkRandomCall();
        return b.getRoot();
    }

    private void storeRandomChatUser() {

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("id",user.getUid());
        hashMap.put("connection",true);

        Constants.databaseReference()
                .child(Constants.RANDOM_CALL)
                .child(user.getUid())
                .setValue(hashMap);


        checkRandomCall();

       // startActivity(new Intent(getActivity(), RandomCallActivity.class));
    }

    private void checkRandomCall() {

        Constants.databaseReference()
                .child(Constants.RANDOM_CALL)
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
                                    //  Toast.makeText(getActivity(), "" + connectionList.size(), Toast.LENGTH_SHORT).show();
                                    if (connectionList.size() == 1){
                                        Toast.makeText(getActivity(), "Connection is not available now", Toast.LENGTH_SHORT).show();
                                    }
                                    else if (connectionList.size() == 2){
                                        connectionList.clear();
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                startActivity(new Intent(requireContext(), RandomCallActivity.class));
                                            }
                                        },1000);
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
                .child(Constants.RANDOM_CALL)
                .child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            HashMap<String,Object> hashMap = new HashMap<>();
                            hashMap.put("connection",false);

                            Constants.databaseReference()
                                    .child(Constants.RANDOM_CALL)
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