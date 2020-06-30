package com.example.pharma;

import android.content.Intent;
import android.os.Bundle;

import com.example.pharma.authentication.LoginMobileActivity;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(FirebaseAuth.getInstance().getUid() ==null)
            startActivity(new Intent(this, LoginMobileActivity.class));
        else startActivity(new Intent(this,HomeActivity.class));
    }
}
