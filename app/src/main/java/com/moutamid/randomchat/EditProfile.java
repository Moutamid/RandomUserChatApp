package com.moutamid.randomchat;

import static com.bumptech.glide.Glide.with;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.DATA;
import static com.moutamid.randomchat.R.color.lighterGrey;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.moutamid.randomchat.Models.UserModel;
import com.moutamid.randomchat.databinding.ActivityEditProfileBinding;
import com.moutamid.randomchat.utils.Constants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class EditProfile extends AppCompatActivity {

    private ActivityEditProfileBinding b;
    private String fname,email,gender,lang,image;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    StorageReference mStorage;
    private Bitmap bitmap;
    private ProgressDialog dialog;
    RadioButton selectedRadioButton,selectedRadioButton2;
    int selectedRadioId,selectedRadioId2;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 101;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        mStorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        getUserDetail();
        b.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditProfile.this,MainActivity.class));
                finish();
            }
        });


        b.profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
            }
        });
        
        b.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   fname = b.fname.getText().toString();
                updateUserData();
            }
        });
    }

    private void updateUserData() {
        selectedRadioId = b.radioGroup.getCheckedRadioButtonId();
        if (selectedRadioId != -1) {
            selectedRadioButton = (RadioButton) findViewById(selectedRadioId);
            //gender = selectedRadioButton.getText().toString();
            //  radioBtn1 = selectedRadioButton.getText().toString();
        }

        selectedRadioId2 = b.langradioGroup.getCheckedRadioButtonId();
        if (selectedRadioId2 != -1) {
            selectedRadioButton2 = (RadioButton) findViewById(selectedRadioId2);
            //lang = selectedRadioButton2.getText().toString();
            //  radioBtn1 = selectedRadioButton.getText().toString();
        }
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("name",b.fname.getText().toString());
       // hashMap.put("email",email);
        hashMap.put("profile_url",image);
        hashMap.put("gender",selectedRadioButton.getText().toString());
        hashMap.put("language",selectedRadioButton2.getText().toString());
        Constants.databaseReference().child(Constants.USERS)
                .child(user.getUid()).updateChildren(hashMap);
        startActivity(new Intent(EditProfile.this,MainActivity.class));
        finish();
    }

    public void checkPermission()
    {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(EditProfile.this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(EditProfile.this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, STORAGE_PERMISSION_CODE);
        }
        else {
            openGallery();
            Toast.makeText(EditProfile.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
                Toast.makeText(EditProfile.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(EditProfile.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"SELECT IMAGE"),PICK_IMAGE_REQUEST);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == EditProfile.this.RESULT_OK &&
                data != null && data.getData() != null) {
            uri = data.getData();
            b.profileImg.setImageURI(uri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(EditProfile.this.getContentResolver(), uri);
                saveInformation();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void saveInformation() {
        dialog = new ProgressDialog(EditProfile.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Uploading your profile....");
        dialog.show();
        if (uri != null) {
            b.profileImg.setDrawingCacheEnabled(true);
            b.profileImg.buildDrawingCache();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
            byte[] thumb_byte_data = byteArrayOutputStream.toByteArray();

            final StorageReference reference = mStorage.child("Profile Images").child(user.getUid() + ".jpg");
            final UploadTask uploadTask = reference.putBytes(thumb_byte_data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return reference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                image = downloadUri.toString();
                                Toast.makeText(EditProfile.this, "upload successfully: ", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }else {
            Toast.makeText(EditProfile.this, "Please Select Image ", Toast.LENGTH_LONG).show();

        }
    }


    private void getUserDetail() {
        Constants.databaseReference().child(Constants.USERS)
                .child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            UserModel userModel = snapshot.getValue(UserModel.class);
                            fname = userModel.getName();
                            email = userModel.getEmail();
                            gender = userModel.getGender();
                            lang = userModel.getLanguage();
                            image = userModel.getProfile_url();
                            b.fname.setText(fname);
                            b.email.setText(email);
                            if(userModel.getProfile_url().equals("")){

                                Glide.with(EditProfile.this)
                                        .asBitmap()
                                        .load(R.drawable.img)
                                        .apply(new RequestOptions()
                                                .placeholder(lighterGrey)
                                                .error(lighterGrey)
                                        )
                                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                                        .into(b.profileImg);
                            }else {

                                Glide.with(EditProfile.this)
                                        .asBitmap()
                                        .load(image)
                                        .apply(new RequestOptions()
                                                .placeholder(lighterGrey)
                                                .error(lighterGrey)
                                        )
                                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                                        .into(b.profileImg);
                            }
                            if (gender.equals("Female")) {
                                b.femaleRadioBtn.setChecked(true);
                            }
                            else {
                                b.maleRadioBtn.setChecked(true);
                            }

                            if (lang.equals("English")){
                                b.rdEnglish.setChecked(true);
                            }else if (lang.equals("Urdu/Hindi")){
                                b.rdUrdu.setChecked(true);
                            }else if (lang.equals("Spanish")){
                                b.rdspanish.setChecked(true);
                            }else if (lang.equals("French")){
                                b.rdfrench.setChecked(true);
                            }else if (lang.equals("German")){
                                b.rdgerman.setChecked(true);
                            }else{
                                b.rditalian.setChecked(true);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}