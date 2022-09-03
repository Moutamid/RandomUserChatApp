package com.moutamid.randomchat.activity;

import android.app.ProgressDialog;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.fxn.stash.Stash;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.randomchat.Models.UserModel;
import com.moutamid.randomchat.databinding.ActivityLoginBinding;
import com.moutamid.randomchat.utils.Constants;

public class RegistrationController {
    private static final String TAG = "RegistrationController";
    private LoginActivity activity;
    private ActivityLoginBinding b;
    public ProgressDialog progressDialog;

    public RegistrationController(LoginActivity activity, ActivityLoginBinding b) {
        this.activity = activity;
        this.b = b;
        progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");

    }

    public void loginUser(String emailStr, String passwordStr) {
        progressDialog.show();

        if (Constants.auth().getCurrentUser() != null) {
            Stash.clearAll();
            Constants.auth().signOut();
        }

        Constants.auth().signInWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "onComplete: sign in completed");
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: susseccfull");
//                            Stash.put(Constants.MY_PASSWORD, passwordStr);
                            Constants.databaseReference().child(Constants.USERS)
                                    .child(Constants.auth().getUid())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                Log.d(TAG, "onDataChange: got user data");
                                                UserModel userModel = snapshot.getValue(UserModel.class);
                                                Stash.put(Constants.CURRENT_USER_MODEL, userModel);
                                                activity.onCompleteMethod();
                                            } else {
                                                Log.d(TAG, "onDataChange: no snapchot");
                                                progressDialog.dismiss();
                                                Toast.makeText(activity, "Data doesn't exist!", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            progressDialog.dismiss();
                                            Log.d(TAG, "onCancelled: cancelled error");
                                            Toast.makeText(activity, error.toException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Log.d(TAG, "onComplete: failed");
                            progressDialog.dismiss();
                            toast(task.getException().getMessage());
                        }
                    }
                });

    }

    public void toast(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }


    public boolean checkEditTextLogin() {
        if (b.edEmail.getText().toString().isEmpty()) {
            toast("Please enter email!");
            return true;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(b.edEmail.getText().toString()).matches()) {
            toast("Email is invalid!");
            return true;
        }

        if (b.edPassword.getText().toString().isEmpty()) {
            toast("Please enter password!");
            return true;
        }

        return false;
    }

    public boolean checkEditTextSignUp() {
        if (b.edName.getText().toString().isEmpty()) {
            toast("Please enter email!");
            return true;
        }
        if (b.edEmail.getText().toString().isEmpty()) {
            toast("Please enter email!");
            return true;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(b.edEmail.getText().toString()).matches()) {
            toast("Email is invalid!");
            return true;
        }

        if (b.edPassword.getText().toString().isEmpty()) {
            toast("Please enter password!");
            return true;
        }

        return false;
    }

    public void registerUser(String emailStr, String passwordStr) {
        progressDialog.show();

        Constants.auth().createUserWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            activity.model.name = b.edName.getText().toString();
                            activity.model.email = b.edEmail.getText().toString();
                            if (b.maleRadioBtn.isChecked())
                                activity.model.gender = Constants.GENDER_MALE;
                            else activity.model.gender = Constants.GENDER_FEMALE;

                            activity.model.is_vip = false;

                            activity.model.uid = Constants.auth().getUid();

                            activity.model.profile_url = Constants.DEFAULT_PROFILE_URL;
                            /*activity.model.followers_count = 0;
                            activity.model.following_count = 0;*/

                            Stash.put(Constants.CURRENT_USER_MODEL, activity.model);
                            uploadUserInfoToDatabase();
                        } else {
                            toast(task.getException().getMessage());
                        }
                    }
                });
    }

    public void uploadUserInfoToDatabase() {
        Constants.databaseReference().child(Constants.USERS)
                .child(Constants.auth().getUid())
                .setValue(activity.model)
                .addOnCompleteListener(activity.onCompleteListener());
    }
}

