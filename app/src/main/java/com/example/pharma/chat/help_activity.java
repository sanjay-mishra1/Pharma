package com.example.pharma.chat;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.pharma.Constants;
import com.example.pharma.R;
import com.example.pharma.firebase.FirebaseCustomAuth;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class help_activity extends AppCompatActivity {
     private static final int READ_EXTERNAL_STORAGE =102 ;
    private static final int OPEN_CAMERA =103 ;
      TextView Edit_message;
    static boolean back = true;
    private static final int CHOOSE_IMAGE = 101;
   public static Uri uriProfileImage;
    boolean actionButtonStatus = false;
      private ImageButton actionButton;
     MyAdapter adapter;
     public static byte[] messageImage;
     RecyclerView recyclerView;
    public static String Caption;
     boolean isActive;
     private long lastseen;
     private FirebaseApp app;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_help_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Chat");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        if (Constants.uid==null)
            Constants.uid=  getSharedPreferences("USER_CREDENTIALS",MODE_PRIVATE).getString("UID",null);
        app=new FirebaseCustomAuth().
                loadCustomFirebase(this,"tablethuts-chats","tablethuts-chats");
        change_status_to_seen();
        findViewById(R.id.end).setOnClickListener(view -> help_activity.super.onBackPressed());

        recyclerView = findViewById(R.id.reyclerview_message_list);
        LinearLayoutManager horizontal = new LinearLayoutManager(help_activity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(horizontal);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("ddMM", Locale.ENGLISH);
        lastseen=Integer.parseInt(date.format(c.getTime()));
        FirebaseDatabase.getInstance(app).getReference().child("Messages").
                //child("Help").child(Constants.uid).
                child(Constants.uid).child("USERLastSeen")
                .setValue(lastseen);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance(app).getReference().child("Messages").
                //child("Help").child(Constants.uid).
                child(Constants.uid + "/Messages");
        adapter = new MyAdapter(app);
        adapter.startListening();
        horizontal.setStackFromEnd(true);
        ScrollPosition(databaseReference,recyclerView,adapter);
        recyclerView.setAdapter(adapter);
        checkMessage(databaseReference);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        Edit_message = findViewById(R.id.edittext_chatbox);
        actionButton = findViewById(R.id.button_send_message);
        actionButton.setOnClickListener(view -> setActionButton());
        Edit_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkMessageBox(charSequence.toString());
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        super.onStart();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        adapter.startListening();
        isActive=true;
        onlineStatus("Online");
        change_status_to_seen();
    try {
    if (messageImage.length>0){
        Glide.with(getApplicationContext()).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(uriProfileImage.getPath()).into((ImageView) findViewById(R.id.messageImage));
        reduceImage(messageImage,Caption);
    }
}catch (Exception ignored){}
    }
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        isActive=false;
        onlineStatus(String.valueOf(System.currentTimeMillis()));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onlineStatus(String.valueOf(System.currentTimeMillis()));
        isActive=false;
        adapter.stopListening();
    }
    void onlineStatus(String time){

//
//        FirebaseDatabase.getInstance().getReference().child("Messages").
//                child("Help").child(Constants.uid).child(FirebaseAuth.getInstance()
//                .getCurrentUser().getUid()).child("USEROnline")
//                .setValue(time);

    }
    @Override
    protected void onResume() {
        super.onResume();
        isActive=true;
        change_status_to_seen();
    }
    @Override
    protected void onStart() {
        super.onStart();
        back = true;
        isActive=true;
        change_status_to_seen();
//        onlineStatus("Online");
        //undo_notification();
    }
    void checkMessage(DatabaseReference Address){
        Log.e("CheckMessage","Inside check message");
        Address.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()||!dataSnapshot.hasChildren()){
                    //check_messages(0);
                    Log.e("CheckMessage","Inside check message inside not found");
                    findViewById(R.id.no_messages).setVisibility(View.VISIBLE);

                }else
                    Log.e("CheckMessage","Inside check message found");
                findViewById(R.id.progressbar).setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                findViewById(R.id.progressbar).setVisibility(View.GONE);
            }
        });

    }
    void checkMessageBox(String message) {
 //.matches(".*\\w.*"
        if (!message.trim().isEmpty()) {
             Glide.with(getApplicationContext()).load(R.drawable.ic_send_black_24dp).into(actionButton);
            actionButtonStatus = true;
        }

        else {
            Glide.with(getApplicationContext()).load(R.drawable.add_color).into(actionButton);
            actionButtonStatus = false;
        }
    }



     void setActionButton() {
         if (actionButtonStatus) {
            //TotalHelp(type);
            new StoreMessage("USER_TEXT",Constants.uid,(EditText) Edit_message,
                    findViewById(R.id.progressRelative),Edit_message.getText().toString().trim(),app);
        } else {
            CardView cardView = findViewById(R.id.card_view);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                if (cardView.getVisibility() == View.INVISIBLE) {
                    animation(true);
                } else {
                    animation(false);
                }
            } else {
                if (cardView.getVisibility() == View.INVISIBLE) {
                    findViewById(R.id.card_view).setVisibility(View.VISIBLE);
                    showZoomIn();
                    ExtraOptions();
                } else {
                    findViewById(R.id.card_view).setVisibility(View.INVISIBLE);
                }
            }

        }

    }
    void showZoomIn() {
        Animation zoomIn =
                AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.zoom_in);
        findViewById(R.id.relateGallery).startAnimation(zoomIn);
        findViewById(R.id.relateCamera).startAnimation(zoomIn);
        findViewById(R.id.relateOrder).startAnimation(zoomIn);
        ExtraOptions();

    }

    void ExtraOptions() {
        findViewById(R.id.gallery).setOnClickListener(view -> {
            showImageChooser();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                animation(false);
            } else findViewById(R.id.card_view).setVisibility(View.INVISIBLE);
        });
        findViewById(R.id.camera).setOnClickListener(view -> {
            openCamera();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                animation(false);
            } else findViewById(R.id.card_view).setVisibility(View.INVISIBLE);
        });
        findViewById(R.id.order).setOnClickListener(view -> {

            back = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // animation(false);
                findViewById(R.id.card_view).setVisibility(View.INVISIBLE);
                openOrders(true,"");
            } else {
                findViewById(R.id.card_view).setVisibility(View.INVISIBLE);
                openOrders(true,"");
            }
        });

    }

    void storeToStorage(String caption) {
        int Image_size;
        String scheme = uriProfileImage.getScheme();
         if (Objects.equals(scheme, ContentResolver.SCHEME_CONTENT)) {
            try {
                InputStream fileInputStream =
                        getApplicationContext().getContentResolver().openInputStream(uriProfileImage);
                if (fileInputStream != null) {
                     Image_size = fileInputStream.available() / 1024;
                    Log.e("help images", "Size is " + Image_size);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (Objects.equals(scheme, ContentResolver.SCHEME_FILE)) {
            String path = uriProfileImage.getPath();
            File f;
            try {
                f = new File(Objects.requireNonNull(path));
                Image_size = (int) f.length() / 1024;
                Log.e("help images", "Size is " + Image_size);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        reduceImage(55,caption);
    }

    void reduceImage(int quality, String caption) {
        try {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            byte[] data = baos.toByteArray();
            Log.e("help image", " Image size is in bytes " + data.length);
            if ((data.length) / 1024 > 250) {
                Log.e("help image", " Image size is modified in bytes " + data.length);
                reduceImage(quality - 15,caption);
            } else
                reduceImage(data,caption);
        } catch (Exception OutOfMemoryError) {
            Toast.makeText(this, "Failed to send image might be due to image size is more", Toast.LENGTH_SHORT).show();
            Log.e("help image", "help-activity Out of memory exception");
        }
    }
    void reduceImage(byte[] data, final String caption) {
        try {   String time= String.valueOf(System.currentTimeMillis());
            StorageReference profileImageRef =
                    FirebaseStorage.getInstance(app).getReference("Chat_Images/"+
                            Constants.uid+"/" + time + ".jpg");
            findViewById(R.id.imageCard).setVisibility(View.VISIBLE);
            final UploadTask uploadTask = profileImageRef.putBytes(data);
            final ProgressBar progressBar=findViewById(R.id.progressbarsmall);
            findViewById(R.id.cancel_message).setOnClickListener(v -> {
                if (!uploadTask.isComplete()){
                    uploadTask.cancel();
                    findViewById(R.id.imageCard).setVisibility(View.GONE);
                }
            });
            uploadTask.addOnProgressListener(taskSnapshot -> {
                double progress=(100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                int currentprogress=(int) progress;
                Log.e("firebase messaging","Progress is "+progress+" curre "+currentprogress);
                progressBar.setProgress(currentprogress);
            });
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                  Intent intent=new Intent(help_activity.this,image_zoomer.class);
                  intent.putExtra("Image",
                          "https://firebasestorage.googleapis.com/v0/b/tablethuts-chats.appspot.com/o/Chat_Images%2F" +
                                  Constants.uid+"%2F" +
                                  time  +
                                  ".jpg?alt=media&token=f5c3e80a-64e6-44e2-8b3b-5630d7ca1f47");
                 intent.putExtra("From", "Camera_Image");
                 intent.putExtra("Caption", caption);
                 messageImage=null;
                 findViewById(R.id.imageCard).setVisibility(View.GONE);
                 startActivity(intent);

                Toast.makeText(help_activity.this, "Upload task completed", Toast.LENGTH_SHORT).show();

            }).addOnFailureListener(e -> {
                findViewById(R.id.imageCard).setVisibility(View.GONE);
                messageImage=null;
                Toast.makeText(help_activity.this, "Upload task failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } catch (Exception OutOfMemoryError) {
            Log.e("help image", "help-activity firebase storage Out of memory exception "+OutOfMemoryError.getLocalizedMessage());
            OutOfMemoryError.printStackTrace();
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void animation(boolean show) {
        final CardView cardView = findViewById(R.id.card_view);
        int height = cardView.getHeight();
        int width = cardView.getWidth();
        int endRadius = (int) Math.hypot(width, height);
        int cx = (int) (actionButton.getX() + (cardView.getWidth() / 2));
        int cy = (int) (actionButton.getY()) + cardView.getHeight() + 56;

        if (show) {
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(cardView, cx, cy, 0, endRadius);
            revealAnimator.setDuration(700);
            revealAnimator.start();
            cardView.setVisibility(View.VISIBLE);
            showZoomIn();
        } else {
            Animator anim =
                    ViewAnimationUtils.createCircularReveal(cardView, cx, cy, endRadius, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    cardView.setVisibility(View.INVISIBLE);

                }
            });
            anim.setDuration(700);
            anim.start();
        }

    }

    public void setLastSeen() {

       FirebaseDatabase.getInstance(app).getReference().child("Messages").
               //     child("Help").child(Constants.uid).
               child(Constants.uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    long lastSeen;
                    String online;
                    try {
                        lastSeen = (long) dataSnapshot.child("CanteenLastSeen").getValue();
                        online = (String) dataSnapshot.child("CanteenOnline").getValue();
                        Log.e("MessageStatus","CanteenLastSeen is "+lastSeen+"Online is "+online);
                        SimpleDateFormat sfd;
                        TextView textView = findViewById(R.id.lastseen);

                        if ( lastseen == lastSeen) {
                            sfd = new SimpleDateFormat("HH:mm", Locale.UK);
                            textView.setVisibility(View.VISIBLE);
                            try {
                                textView.setText(String.format("Last seen at %s", sfd.format(new Date(Long.parseLong(Objects.requireNonNull(online))))));
                            }catch (Exception ignored){}

                            try {
                                if (online.equalsIgnoreCase("online"))
                                    textView.setText(R.string.online);
                                else{

                                    textView.setText(String.format("Last seen at %s", sfd.format(new Date(Long.parseLong(online)))));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("MessageStatus","Crash "+e.toString());
                                textView.setVisibility(View.GONE);
                            }

                        } else {
                            textView.setVisibility(View.VISIBLE);

                            sfd = new SimpleDateFormat("MMM", Locale.UK);
                            textView.setText(String.format("Last seen on %s", sfd.format(new Date(Long.parseLong(online)))));

                        }
                    }catch (Exception e){
                        Log.e("MessageStatus","Crash "+e.toString());

                        findViewById(R.id.lastseen).setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



    }

 void ScrollPosition(RecyclerView recyclerView, long pos, RecyclerView.Adapter adapter){

        recyclerView.scrollToPosition(adapter.getItemCount()-1);
        Log.e("Scrolling","Adapter item count "+adapter.getItemCount());
}
     void ScrollPosition(DatabaseReference Address, final RecyclerView recyclerView, final RecyclerView.Adapter adapter) {
          FirebaseDatabase.getInstance(app).getReference().child("Messages").
                 //child("Help").child(Constants.uid).
                  child(Constants.uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                 Log.e("scrolling", "Childs String s is "+s);
                try {
                    if (s.toLowerCase().contains("last"))
                    {
                      //  setLastSeen();
                    }
                }catch (Exception ignored){
                }
                change_status_to_seen();
                findViewById(R.id.no_messages).setVisibility(View.GONE);
                ScrollPosition(recyclerView,dataSnapshot.getChildrenCount(),adapter);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void change_status_to_seen() {
        Log.e("Seen","Changing seen status ");
        if (isActive) {
            FirebaseDatabase.getInstance(app).getReference().child("Messages").
                    //child("Help").child(Constants.uid).
                            child(Constants.uid + "/Messages")
                    .orderByChild("Seen").equalTo(false).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance(app).getReference().child("Messages").
                                    child(Constants.uid + "/Messages");
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        try {
                            if (Objects.requireNonNull(dataSnapshot1.child("From").getValue()).toString().contains("ADMIN")) {
                                Log.e("scrolling", "change seen status completed");
                                databaseReference.child(Objects.requireNonNull(dataSnapshot1.getKey())).child("Seen").setValue(true);
                            }

                        } catch (Exception ignored) {
                        }
                        Log.e("Seen", "Changing seen status completed ");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


    public   void openOrders(boolean show,String order) {

        if (show) {
//            orders_dialog();

        } else {

            Edit_message.setText(String.format("%s %s ", Edit_message.getText().toString().trim(),order));
         }
    }

    private void openCamera() {
        requestPermission(this,Manifest.permission.CAMERA,OPEN_CAMERA);
        Log.e("Camera","Inside Open_camera permission");
    }

    private void showImageChooser() {
        requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE);
    }
     private void requestPermission(Activity context, String permission, int value)  {
        boolean hasPermission = (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(context,
                    new String[]{permission},
                    value);
        } else if (value==105){
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");
            startActivityForResult(intent, 105);
        }
        else{
            if (value==103){Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
               startActivityForResult(intent, CHOOSE_IMAGE);
            }
          else  startActivity(new Intent(this, GallerySample.class));
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        startActivity(new Intent(this, GallerySample.class));
                 }
                break;
            }
            case 105:
                Intent intent2 = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent2.setType("*/*");
                startActivityForResult(intent2, 105);
                break;
            case OPEN_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, CHOOSE_IMAGE);
           }
                break;
        }
    }

    void send_to_Image_viewer(Uri url){
        start_camera_dialog(url);
    }
    void start_camera_dialog(final Uri uri){
            Log.e("Camera","Inside start_camera_dialog");
            final View dialogView = View.inflate(help_activity.this, R.layout.image_viewer, null);
            Dialog dialog = new Dialog(help_activity.this,R.style.Dialog);

        Window window=dialog.getWindow();
        if (window != null) {
            Log.e("Camera","Inside start_camera_dialog window is not null");
            window.setLayout( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        } else{
            dialog = new Dialog(help_activity.this);
              dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Log.e("Camera","Inside start_camera_dialog  window is null");
        }
        dialog.setContentView(dialogView);
        Glide.with(getApplicationContext()).load(uri).into((ImageView) dialogView.findViewById(R.id.imageId));

        final Dialog finalDialog = dialog;
        dialog.setOnKeyListener((dialogInterface, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_BACK) {
                finalDialog.dismiss();

                return true;
            }

            return false;
        });


           Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

         dialogView.findViewById(R.id.send).setOnClickListener(view -> {
             EditText editText=dialogView. findViewById(R.id.ImageCaption);
             Caption= editText.getText().toString().trim();
             storeToStorage(Caption);
             finalDialog.dismiss();
          });
     dialog.show();
    }
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            if (requestCode == CHOOSE_IMAGE) {
                uriProfileImage = data.getData();
                Log.e("help", "activity result successful");
                Log.e("Camera", uriProfileImage.toString());
                Bitmap bitmap = null;
                try {ImageView imageView=findViewById(R.id.messageImage);
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                send_to_Image_viewer(uriProfileImage);

            }else if (requestCode==105){
                //store to storage
                //add new message
                Log.e("DocumentFile","Selected "+data.getData());
            }
        }
        else {
            Log.e("help", "activity result unsuccessful reqcode" + requestCode + " resultcode " + requestCode
                    + " data ");
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        adapter.stopListening();
    }


    public void sendDocument(View view) {
        requestPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE,105);
    }

    public void sendPrescription(View view) {
        BottomSheetDialogFragment fragment=new ChatInformationSelectorFragment();
        Bundle bundle=new Bundle();
        bundle.putString("CLASS_NAME","Prescriptions");
        fragment.setArguments(bundle);
        fragment.showNow(Objects.requireNonNull(this).getSupportFragmentManager(), "Share information");

    }

    public void sendTestReport(View view) {
        BottomSheetDialogFragment fragment=new ChatInformationSelectorFragment();
        Bundle bundle=new Bundle();
        bundle.putString("CLASS_NAME","Test Report");
        fragment.setArguments(bundle);
        fragment.showNow(Objects.requireNonNull(this).getSupportFragmentManager(), "Share information");
    }
}

