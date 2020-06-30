package com.example.pharma.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;


import com.example.pharma.R;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class imageViewer extends AppCompatActivity {
    WebView webView;
    String img;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_viewer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        receive();
       // webView=findViewById(R.id.webview);
        loadImage();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    void loadImage(){
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.loadUrl(img);
    }
    void receive(){
       try{
           Intent intent=getIntent();
           img=intent.getStringExtra("Image");
       }catch (Exception e){
           e.printStackTrace();
       }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
