package com.example.pharma;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class WebArticleActivity extends AppCompatActivity {
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getIntent().getStringExtra("NAME"));
        String url= getIntent().getStringExtra("URL");
        if (getIntent().getBooleanExtra("SAVE",false))
            new LoadRecentData().addNewRecent(getIntent().getStringExtra("ID"),this,"RECENT_WEB");
      loadUrl(url);
         }
     @SuppressLint("SetJavaScriptEnabled")
     void loadUrl(String url){
         Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
         Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
         WebView webView=findViewById(R.id.webview);
         ProgressBar progressBar=findViewById(R.id.progressBar);
         webView.setWebViewClient(new Browse(url));
         webView.setWebChromeClient(new WebChromeClient(){
             @Override
             public void onProgressChanged(WebView view, int newProgress) {
                 super.onProgressChanged(view, newProgress);
                 progressBar.setProgress(newProgress);
             }
         });
         webView.getSettings().setLoadsImagesAutomatically(true);
         webView.getSettings().setJavaScriptEnabled(true);
         webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
         webView.loadUrl(url);
     }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private class Browse extends WebViewClient {
        String url;
        Browse(String url){
            this.url=url;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            setTitle (view.getTitle());
            findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(url);
            return super.shouldOverrideUrlLoading(view, request);
        }
    }
}
