package com.moutamid.randomchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fxn.stash.Stash;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.randomchat.MainActivity;
import com.moutamid.randomchat.Models.UserModel;
import com.moutamid.randomchat.PrivacyPolicyScreen;
import com.moutamid.randomchat.R;
import com.moutamid.randomchat.TermsAndCondition;
import com.moutamid.randomchat.databinding.ActivityLoginBinding;
import com.moutamid.randomchat.utils.Constants;

public class LoginActivity extends AppCompatActivity {
    private RegistrationController controller;
    private static final String TAG = "RegistrationActivity";

    private boolean isLogin = true;

    private static final String USER_INFO = "userinfo";
    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;
    private boolean agreed = false;
    private String termsTxt;

    private ActivityLoginBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        if (Constants.auth().getCurrentUser() !=null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        controller = new RegistrationController(this, b);
        termsTxt = "By tapping Login and Accept, you acknowledge that you have read the Privacy Policy and agree to the Terms And Conditions.";

        SpannableString ss = new SpannableString(termsTxt);

        ClickableSpan cs1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {

                Intent intent = new Intent(LoginActivity.this, PrivacyPolicyScreen.class);
                startActivity(intent);
            }
        };


        ClickableSpan cs2 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {

                Intent intent = new Intent(LoginActivity.this, TermsAndCondition.class);
                startActivity(intent);
            }
        };
        ss.setSpan(cs1,68,82, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(cs2,100,120, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        b.termsPolicy.setText(ss);
        b.termsPolicy.setMovementMethod(LinkMovementMethod.getInstance());
        b.agreed.setMovementMethod(LinkMovementMethod.getInstance());

        b.agreed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    agreed = true;
                }else{
                    agreed = false;
                }
            }
        });

        b.tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLogin) {
                    b.tvName.setVisibility(View.VISIBLE);
                    b.edName.setVisibility(View.VISIBLE);
                    b.radioGroup.setVisibility(View.VISIBLE);
                    b.langradioGroup.setVisibility(View.VISIBLE);
                    b.tvlang.setVisibility(View.VISIBLE);

                    b.tvLogin.setText("Sign Up");
                    b.tvSignup.setText("Login");
                    b.btnLogin.setText("Sign Up");
                    b.termsCondition.setVisibility(View.GONE);
                    b.textt.setText("Already have an account?");
                    isLogin = false;
                } else {
                    b.tvName.setVisibility(View.GONE);
                    b.edName.setVisibility(View.GONE);
                    b.radioGroup.setVisibility(View.GONE);
                    b.langradioGroup.setVisibility(View.GONE);
                    b.tvlang.setVisibility(View.GONE);
                    b.termsCondition.setVisibility(View.VISIBLE);
                    b.tvLogin.setText("Login");
                    isLogin = true;
                    b.tvSignup.setText("Sign Up");
                    b.btnLogin.setText("Login");
                    b.textt.setText("Don't have any account?");
                }
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        b.ivGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.progressDialog.show();
                signIn();
            }
        });

        b.btnLogin.setOnClickListener(view -> {
            Log.d(TAG, "onCreate: loginbtn");

            if (isLogin) {
                if (controller.checkEditTextLogin())
                    return;

                String emailStr = b.edEmail.getText().toString();
                String passwordStr = b.edPassword.getText().toString();

                if(agreed){
                    controller.loginUser(emailStr, passwordStr);
                }else {
                    Toast.makeText(LoginActivity.this, "", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (controller.checkEditTextSignUp())
                    return;

                String emailStr = b.edEmail.getText().toString();
                String passwordStr = b.edPassword.getText().toString();

                controller.registerUser(emailStr, passwordStr);
            }
        });

        // [END config_signin]

    /*    findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });*/

    }

    public OnCompleteListener<Void> onCompleteListener() {
        return new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                controller.progressDialog.dismiss();

                if (task.isSuccessful()) {
                    onCompleteMethod();
                } else {
                    controller.toast(task.getException().getMessage());
                    Log.d(TAG, "onComplete: error " + task.getException().getMessage());

                }
            }
        };
    }

    public void onCompleteMethod() {
        Log.d(TAG, "onCompleteMethod: triggered");
        controller.progressDialog.dismiss();
        Stash.put(Constants.IS_LOGGED_IN, true);

        Log.d(TAG, "onCompleteMethod: should go to home screen");

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);
    }

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                controller.progressDialog.dismiss();
                Toast.makeText(this, "Google sign in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        Constants.auth().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = Constants.auth().getCurrentUser();
                            updateUI(user);
                        } else {
                            controller.progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Google sign in failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // [END signin]
    public UserModel model = new UserModel();

    private void updateUI(FirebaseUser user) {
        if (user != null) {

            if (user.getDisplayName().isEmpty() || user.getDisplayName() == null) {
                model.name = user.getEmail();
            } else
                model.name = user.getDisplayName();

            model.email = user.getEmail();
            model.gender = Constants.GENDER_MALE;
            model.language = "English";
            model.is_vip = false;

            model.uid = user.getUid();
//            model.number = "000000000";
//            model.bio = Constants.NULL;
            model.profile_url = Constants.DEFAULT_PROFILE_URL;
//            model.followers_count = 0;
//            model.following_count = 0;
            Stash.put(Constants.CURRENT_USER_MODEL, model);
            Constants.databaseReference().child(Constants.USERS)
                    .child(Constants.auth().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                controller.uploadUserInfoToDatabase();
                            } else {
                                UserModel userModel = snapshot.getValue(UserModel.class);
                                Stash.put(Constants.CURRENT_USER_MODEL, userModel);
                                controller.progressDialog.dismiss();
                                onCompleteMethod();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }


}