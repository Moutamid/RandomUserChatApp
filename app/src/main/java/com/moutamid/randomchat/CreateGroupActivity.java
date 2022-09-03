package com.moutamid.randomchat;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.hbb20.CountryCodePicker;
import com.moutamid.randomchat.Models.GroupsModel;
import com.moutamid.randomchat.utils.Constants;

public class CreateGroupActivity extends AppCompatActivity {

    private TextInputEditText titleTxt,bioTxt;
    private CountryCodePicker ccp;
    private Spinner languageSpinner;
    private Button saveBtn;
    private String title,purpose,language = "";
    private ProgressDialog pd;
    private int flag;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        titleTxt = findViewById(R.id.supermeetingtitle);
        bioTxt = findViewById(R.id.EtPurpose);
        languageSpinner = findViewById(R.id.spnLangs);
        ccp = findViewById(R.id.countryCode_picker);
        saveBtn = findViewById(R.id.supermeetingbtn);
        flag = ccp.getSelectedCountryFlagResourceId();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                language = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validInfo()){
                    pd = new ProgressDialog(CreateGroupActivity.this);
                    pd.setMessage("Creating Group.....");
                    pd.show();
                    saveGroup();
                }else {

                }
            }
        });
    }

    private void saveGroup() {
        DatabaseReference db = Constants.databaseReference().child("Groups");
        String key = db.push().getKey();
        GroupsModel model = new GroupsModel("",title,purpose,key,flag,language,user.getUid());
        db.child(key).setValue(model);
        pd.dismiss();
        startActivity(new Intent(CreateGroupActivity.this,MainActivity.class));
        finish();
    }

    public boolean validInfo() {
        title = titleTxt.getText().toString();
        purpose = bioTxt.getText().toString();

        if(title.isEmpty()){
            titleTxt.setText("Input Title");
            titleTxt.requestFocus();
            return false;
        }

        if(purpose.isEmpty()){
            bioTxt.setText("Input Purpose");
            bioTxt.requestFocus();
            return false;
        }

        if (language.isEmpty()){
            Toast.makeText(CreateGroupActivity.this, "Please Select Language", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (flag == 0){
            Toast.makeText(CreateGroupActivity.this, "Please Select Country", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}