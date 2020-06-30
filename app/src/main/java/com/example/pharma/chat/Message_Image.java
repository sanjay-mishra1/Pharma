package com.example.pharma.chat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.pharma.R;
import com.jsibbold.zoomage.ZoomageView;

import java.io.File;
 import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.prefs.Preferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

public class  Message_Image extends AppCompatActivity {
    private String Message;
    private String Img;
    ZoomageView imageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_display_image);
        Receive();
         imageView=findViewById(R.id.message_body_img);
        Glide.with(getApplicationContext()).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(Img).into(imageView);
        TextView textView=findViewById(R.id.text_message_name);
        textView.setText(Message);

    }

    void Receive(){
        try {
            Intent intent=getIntent();
            Img=intent.getStringExtra("Image");
            Message=intent.getStringExtra("Message");
        }catch (Exception e){e.printStackTrace();}

    }

    public void onclick_Download(View view) throws IOException {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission(this);
        }

    }

    private void SaveImage() {
          File imageRoot=new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "AppName");
            File image = new File(imageRoot, Img);

            // Make sure the Pictures directory exists.
            imageRoot.mkdirs();

    }

    public static final int REQUEST_WRITE_STORAGE = 112;

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission(Activity context) throws IOException {
        boolean hasPermission = ( checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        } else {
            save();
        }
    }
    void save() throws IOException {
        FileOutputStream outStream = null;
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + "/Mcafe");
        dir.mkdirs();
        String fileName = String.format("%d.jpg", System.currentTimeMillis());
        File outFile = new File(dir, fileName);
        imageView.setDrawingCacheEnabled(true);
        Bitmap bitmap=imageView.getDrawingCache(false);
        outStream = new FileOutputStream(outFile);
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
        }catch (Exception ignored){}
        outStream.flush();
        outStream.close();
        Toast.makeText(this,"Saved",Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case Preferences.MAX_VALUE_LENGTH: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                      //  requestPermission(this);
                        save();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(this, "The app was allowed to write to your storage!", Toast.LENGTH_LONG).show();
                    // Reload the activity with permission granted or use the features what required the permission
                } else {
                    Toast.makeText(this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }



    public void ONclickShare(View view)
    {
        Intent intent=new Intent(this,image_zoomer.class);
        intent.putExtra("Image",(Img));
        intent.putExtra("From","Message_Image");
        startActivity(intent);
        finish();
    }

    public void backtomessage(View view) {
        onBackPressed();
    }
}
