package com.moutamid.randomchat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.randomchat.activity.LoginActivity;
import com.moutamid.randomchat.databinding.ActivityPrivacyPolicyScreenBinding;
import com.moutamid.randomchat.utils.Constants;

import java.util.HashMap;

public class PrivacyPolicyScreen extends AppCompatActivity {

    private ActivityPrivacyPolicyScreenBinding b;
    private DatabaseReference db;
    String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityPrivacyPolicyScreenBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        db = Constants.databaseReference().child("Settings");
        b.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PrivacyPolicyScreen.this, LoginActivity.class));
                finish();
            }
        });
        getPrivacyPolicy();

    }

    private void getPrivacyPolicy() {
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
             if(snapshot.child("privacy_policy_url").exists()){
                 url = snapshot.child("privacy_policy_url").getValue().toString();
                 loadUrl(url);
             }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadUrl(String urls) {
        b.webView.getSettings().setJavaScriptEnabled(true);
        b.webView.getSettings().setDomStorageEnabled(true);

        final Activity activity = this;
        b.webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                activity.setProgress(progress * 100);
            }
        });

        b.webView.loadUrl(urls);
    }


    // Go back to previous upon clicking back button
    // instead of closing app.
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && b.webView.canGoBack())
        {
            b.webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}