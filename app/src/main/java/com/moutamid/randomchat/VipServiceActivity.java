package com.moutamid.randomchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.PurchaseInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.randomchat.Models.VipCost;
import com.moutamid.randomchat.utils.Constants;

import java.util.HashMap;

public class VipServiceActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler{

    BillingProcessor bp;
//    public static final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsrpGtYDbov4k2klc+GDzkTQPszy82XbwooD4Z+rLsb6zKbhrMNXrti2evpD1L9tTLTDl676Aorj79AhsC0bI2cz0bTDyuQ3slr48DOCTuIV/VTPzSmu9taZJjwhr1+WkP5VlE+BQCXsIBavnQ/i2Y650RGtZlyr/Nz7MBu6iPEUCrFyutJkShnb5ISmY+2I5gLSRFSGEYogZRUY+o109X7IbrBXrnciZK6DEXHlx0JJF9O+MxAHOc4QdSvTcYvlx5a/I9SHyuet6tcafblPmyJnVcNlKn8a/ckcIOXY7i7w4PCwZbHyQ3O/t1BwFaC+XpwSUdZM1KBnuCFu+9jrAOQIDAQAB";
    public static final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3iwKMl2X26J7j+CZDgB1/LYg4z5aCPurK3m9MQvjLPm1z3FrITt88tIr0ad1m6cyGcFIZ+6zyM6J1iIit9cbFgd+in+3JD9On5hc5QcXYGt61kYZr+WTpIUT7cFzfYIGOTTf5zW75IMvlczUylj+UCkuNiH2ZQlrPxXyIdqvz4pENJav4YGJzkkh6TPj6/6skaLpabq/HrVuv35qTEfDDgH6XAziWEv+L8DC8py7uRahA65+nDyiY/SuOlvCs4BqNSGSkVsqPIPFXPbWFP/Hs/YNs/Y3a/sSOpaBaBiwMpa1broAQvPtYKpOwAneIAJ+QtqOcdCcDtkvyFIOaaNeOQIDAQAB";
    public static final String ONE_MONTH_DOLLAR_PRODUCT = "one.month.com.moutamid.randomchat";
    public static final String THREE_MONTH_DOLLAR_PRODUCT = "three.month.com.moutamid.randomchat";
    public static final String ONE_YEAR_DOLLAR_PRODUCT = "one.year.com.moutamid.randomchat";
    private FirebaseAuth mAuth;
    private DatabaseReference db;
    private Button button1,button2,button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_service);
        mAuth = FirebaseAuth.getInstance();
        button1 = findViewById(R.id.one_month);
        button2 = findViewById(R.id.three_month);
        button3 = findViewById(R.id.one_year);
        bp = BillingProcessor.newBillingProcessor(this, LICENSE_KEY, this);
        bp.initialize();
        db = Constants.databaseReference().child("VipCosts");
        getCosts();
        button1.setOnClickListener((View.OnClickListener) view ->
                bp.subscribe(VipServiceActivity.this, ONE_MONTH_DOLLAR_PRODUCT)
        );
        button2.setOnClickListener((View.OnClickListener) view ->
                bp.subscribe(VipServiceActivity.this, THREE_MONTH_DOLLAR_PRODUCT)
        );
        button3.setOnClickListener((View.OnClickListener) view ->
                bp.subscribe(VipServiceActivity.this, ONE_YEAR_DOLLAR_PRODUCT)
        );
    }

    private void getCosts() {
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    VipCost cost = snapshot.getValue(VipCost.class);
                    button1.setText("1 Month Subscription "+cost.getMonth1());
                    button2.setText("3 Month Subscription "+cost.getMonth3());
                    button3.setText("1 Year Subscription "+cost.getYear());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable PurchaseInfo details) {
       // if (productId.equals(ONE_MONTH_DOLLAR_PRODUCT)){
            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("is_vip",true);
            Constants.databaseReference().child(Constants.USERS).child(mAuth.getCurrentUser().getUid())
                    .updateChildren(hashMap);
            Toast.makeText(VipServiceActivity.this, "Subscribe Successfully!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(VipServiceActivity.this,MainActivity.class));
            finish();
        //}
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        Toast.makeText(VipServiceActivity.this, "onBillingError: code: " + errorCode + " \n" + error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBillingInitialized() {

    }
    @Override
    protected void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }
}