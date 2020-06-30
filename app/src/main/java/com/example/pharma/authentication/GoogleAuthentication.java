package com.example.pharma.authentication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pharma.HomeActivity;
import com.example.pharma.R;
import com.example.pharma.firebase.FirebaseCustomAuth;
import com.example.pharma.model.SignUpModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static java.security.AccessController.getContext;

public class GoogleAuthentication extends AppCompatActivity {
    private static final int GOOGLE_SIGN_IN = 101;
    private GoogleSignInClient mGoogleSignInClient;
//    private FirebaseApp firebaseApp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thirdpary_login_layout);
//        firebaseApp=new FirebaseCustomAuth().loadFirebase(this,String.valueOf(System.currentTimeMillis()));
        // Configure sign-in to request the user's ID, email address, and basic
       // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signinwithGoogle(mGoogleSignInClient);
    }

    private void signinwithGoogle(GoogleSignInClient mGoogleSignInClient) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task);
        }else
            Snackbar.make(findViewById(R.id.parent), "An error occurred", Snackbar.LENGTH_LONG).show();
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            if (account != null) {
                updateUI(account);
            }
            Log.e("GoogleSignIn","Successful "+account+" email "+account.getEmail()+" id "+account.getId()+
                    " name "+account.getDisplayName());
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(getClass().getCanonicalName(), "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }
    private void updateUI(GoogleSignInAccount user) {
        findViewById(R.id.icon_progress).setVisibility(View.GONE);
        findViewById(R.id.profile_card).setVisibility(View.VISIBLE);
        TextView name=findViewById(R.id.profile_name);
        TextView email=findViewById(R.id.profile_email);
        name.setText(user.getDisplayName());
        email.setText(user.getEmail()+"  ");
        Glide.with(this).load(user.getPhotoUrl()).into((ImageView) findViewById(R.id.profile_img));
        chekUser(user.getId(),user.getEmail(),user.getDisplayName(), String.valueOf(user.getPhotoUrl()));

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
                    mGoogleSignInClient.signOut();
                    finish();
                    Snackbar.make(findViewById(R.id.parent),"An error occurred",Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account==null)
            Log.e("GoogleSignIn","Not signed with google");
            //not signin
        else Log.e("GoogleSignIn"," signed with google");
    }
}
