package com.example.pharma.authentication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pharma.HomeActivity;
import com.example.pharma.R;
import com.example.pharma.firebase.FirebaseCustomAuth;
import com.example.pharma.model.SignUpModel;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class FacebookAuthentication extends AppCompatActivity {
    private CallbackManager mCallbackManager;
//    private FirebaseApp firebaseApp;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thirdpary_login_layout);
        //firebaseApp=new FirebaseCustomAuth().loadFirebase(this,String.valueOf(System.currentTimeMillis()));
        Glide.with(this).load(R.drawable.com_facebook_button_icon).into((ImageView) findViewById(R.id.icon));
        CardView cardView=findViewById(R.id.iconCard);
        cardView.setCardBackgroundColor(getResources().getColor(R.color.com_facebook_blue));
        // Initialize Facebook Login button
         mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.performClick();
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(getClass().getCanonicalName(), "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(getClass().getCanonicalName(), "facebook:onCancel");
                Snackbar.make(findViewById(R.id.parent), "Cancelled", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Snackbar.make(findViewById(R.id.parent), "An error occurred", Snackbar.LENGTH_SHORT).show();
                Log.d(getClass().getCanonicalName(), "facebook:onError", error);
                // ...
            }
        });
    }
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(getClass().getCanonicalName(), "handleFacebookAccessToken:" + token);
        mAuth=FirebaseAuth.getInstance();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(getClass().getCanonicalName(), "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(getClass().getCanonicalName(), "signInWithCredential:failure", task.getException());
                        Toast.makeText(FacebookAuthentication.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        findViewById(R.id.icon_progress).setVisibility(View.GONE);
        findViewById(R.id.profile_card).setVisibility(View.VISIBLE);
        TextView name=findViewById(R.id.profile_name);
        TextView email=findViewById(R.id.profile_email);
        name.setText(user.getDisplayName());
        email.setText(user.getEmail());
        Glide.with(this).load(user.getPhotoUrl()).into((ImageView) findViewById(R.id.profile_img));
        chekUser(user.getUid(),user.getEmail(),user.getDisplayName(), String.valueOf(user.getPhotoUrl()));
    }

    private void chekUser(String token,String email,String name,String img) {
        FirebaseDatabase.getInstance().getReference("Customers").
                orderByChild("email").equalTo(email).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            loadData(token,name,img);
                        }
                        else storeData(token,email,name,img);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void loadData(String token, String name, String img) {
        SharedPreferences sharedPreferences= Objects.requireNonNull(getSharedPreferences("CRED",MODE_PRIVATE));
        SharedPreferences.Editor edit=sharedPreferences.edit();
        edit.putString("NAME", name);
        edit.putString("UID", token);
        edit.putString("IMG", img);
        edit.apply();
        Intent intent=new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void storeData(String uid, String email, String name, String img) {
        SignUpModel model=new SignUpModel(email,name,null,img);
        FirebaseDatabase.getInstance().getReference("Customers").child(uid).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    loadData(uid,name,img);
                }else{
                   mAuth.signOut();
                   Snackbar.make(findViewById(R.id.parent),"An error occurred",Snackbar.LENGTH_LONG).show();
                   finish();
                }
            }
        });
    }


    @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            Log.e("FacebookResult",resultCode+" "+requestCode+" "+data);
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }

}
