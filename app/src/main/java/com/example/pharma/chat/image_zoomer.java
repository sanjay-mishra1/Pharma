package com.example.pharma.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.example.pharma.Constants;
import com.example.pharma.R;
import com.example.pharma.firebase.FirebaseCustomAuth;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jsibbold.zoomage.ZoomageView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class image_zoomer extends AppCompatActivity {
    Bitmap bmp = null;
    private String from="";
    public static byte[] data;
    private String caption="";
    private FirebaseApp app;

    public image_zoomer() {
    }
    String img;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_viewer);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
//        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        ZoomageView mImageView = findViewById(R.id.imageId);
        mImageView.setDrawingCacheEnabled(true);
        app = new FirebaseCustomAuth().loadCustomFirebase(this, "tablethuts-chats");
        receive();
        Log.e("Camera ","Img "+img);
        Glide.with(getApplicationContext()).load(img).into(mImageView);
     try {
         if (from.contains("Camera_Image")){
             EditText editText=findViewById(R.id.ImageCaption);
             new StoreMessage("USER_IMG",Constants.uid
                     ,editText, findViewById(R.id.progressRelative),img,caption,app);

             finish();
         }else{
             setContentView(R.layout.image_viewer);
             mImageView.setDrawingCacheEnabled(true);
             Log.e("Camera ","Img "+img);
             Glide.with(getApplicationContext()).load(img).into(mImageView);
             Reduce_Image reduce_image = new Reduce_Image(img, getApplicationContext(), mImageView);
             reduce_image.doInBackground();
         }
     }catch (Exception e){
         setContentView(R.layout.image_viewer);
         mImageView =  findViewById(R.id.imageId);
         mImageView.setDrawingCacheEnabled(true);
         Log.e("Camera ","Img "+img);
         Glide.with(getApplicationContext()).load(img).into(mImageView);
         Reduce_Image reduce_image = new Reduce_Image(img, getApplicationContext(), mImageView);
         reduce_image.doInBackground();
     }
    }

    void receive() {
        try {
            Intent intent = getIntent();
            img = intent.getStringExtra("Image");

            help_activity.uriProfileImage= Uri.parse(img);
            from = intent.getStringExtra("From");
            Log.e("Camera","From imagezoomer "+img);
            caption = intent.getStringExtra("Caption");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void StoreTodatabase() {
        try {
            findViewById(R.id.progressRelative).setVisibility(View.VISIBLE);
            help_activity. messageImage=data;
            EditText editText=findViewById(R.id.ImageCaption);
            help_activity. Caption=editText.getText().toString().trim();
            finish();
        } catch (Exception e) {
            Log.e("help image Firebase", " Error " + e.getMessage());
        }
    }
    void saveMessage(String img, String date, String key, String Time,boolean Status,Long Total) {
        Log.e("Firebase Messaging","Inside save message  ");
        DatabaseReference   database = FirebaseDatabase.getInstance(app).getReference().child("Messages").
                child("Help").child(Constants.uid).
                child(Constants.uid + "/Messages/"+ "/").
                child(String.valueOf(Total));
        EditText message = findViewById(R.id.ImageCaption);
         if (!Status)
        {
            storeNewKey(key,date, String.valueOf(Total+1));
            database.child("From").setValue("Date");
            database.child("Date").setValue(date.toUpperCase());
            database = FirebaseDatabase.getInstance(app).getReference().child("Messages").
                    child("Help").child(Constants.uid)
                    .child(Constants.uid + "/Messages/"+ "/").
                    child(String.valueOf(Total+1));
            database.child("Date").setValue(date);

        }else{
            database.child("Date").setValue("");
        }
        if (message.getText().toString().trim().isEmpty())
            database.child("Message").setValue("");
        else database.child("Message").setValue(message.getText().toString().trim());
        Log.e("help", " Message Saved ");
        database.child("Food_Image").setValue(img);
        database.child("Time").setValue(Time);
        database.child("Date").setValue(date);
        database.child("Status").setValue("UNREAD");
        database.child("From").setValue("USER_IMG");
        finish();
    }
    void storeNewKey(String key, String date, String Total){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance(app).getReference().child("Messages").
                child("Help").child(Constants.uid)
                .child(Constants.uid + "/Keys/"+key);
        databaseReference.child("Date").setValue(date);
        databaseReference.child("key").setValue(key);
        databaseReference.child("Total").setValue(Total);
    }

    public void backtomessage(View view) {
        onBackPressed();
    }
    @Override
    public void onBackPressed() {
        bmp = null;
        data = null;
        super.onBackPressed();
    }
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return true;
    }

    public void Onclick_storeToFirebase(View view) {
       try {
           if (!from.contains("Message_Image")) {
               StoreTodatabase();
           } else {
               EditText editText=findViewById(R.id.ImageCaption);
               new StoreMessage("USER_IMG",Constants.uid,editText,
                       findViewById(R.id.progressRelative),img,editText.getText().toString().trim(),app);
           }
       }catch (Exception e){  StoreTodatabase();}
    }

    public static class Reduce_Image extends AsyncTask<Void, Integer, String> {
        private Bitmap bmp;
        private String img;
        private Context context;
        public View view;
        Reduce_Image(String img, Context context, View view) {
             this.img = img;
            this.context = context;
            this.view = view;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(Void... params) {
            Log.e("Reducing Image","Background task started");
            Reducing_Image();
            return null;
        }
        void Reducing_Image() {
            int size;
            Log.e("Reducing Image","Inside Reducing Image");
            size = ImageSize();
            if (size <= 4000) {
                reduceImage(55);
            } else if (size <= 5000) {
                reduceImage(70);
            } else if (size <= 6000) {
                reduceImage(85);
            }
        }
        int ImageSize() {
            int Image_size = 0;
            {
                try {
                    InputStream fileInputStream =
                            context.getContentResolver().openInputStream(Uri.parse(img));
                    if (fileInputStream != null) {
                        //      dataSize = fileInputStream.available();
                        Image_size = fileInputStream.available() / 1024;
                        Log.e("help images", "Size is " + Image_size);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


            return Image_size;

        }
        void reduceImage(int quality) {
            try {   Log.e("Reducing Image","Inside Reduce Image found quality "+quality);
                bmp = BitmapFactory.decodeFile(img);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, quality, baos);
                data = baos.toByteArray();
                Log.e("Reducing Image", " Image size is in bytes " + data.length);
                if ((data.length) / 1024 > 250) {
                    Log.e("Reducing Image", " Image size is modified in bytes " + data.length);
                    bmp = null;
                    baos = null;
                    reduceImage(quality - 15);
                } else {
                    Glide.with(context).load(bmp).into((ZoomageView) view);
                }
            } catch (Exception OutOfMemoryError) {
                Glide.with(context).load(bmp).into((ZoomageView) view);
                 Log.e("Reducing Image", "image-zoomer Out of memory exception");
            }

        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // back to main thread after finishing doInBackground
            // update your UI or take action after
            // exp; make progressbar gone
            Log.e("Reducing Image", "Background task completed");

            Glide.with(context).load(bmp).into((ZoomageView) view);

        }
    }
}

