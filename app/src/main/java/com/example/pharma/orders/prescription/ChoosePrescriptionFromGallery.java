package com.example.pharma.orders.prescription;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.pharma.Constants;
import com.example.pharma.R;
import com.example.pharma.firebase.FirebaseCustomAuth;
import com.example.pharma.imgeslider.SliderAdapter;
import com.example.pharma.view_image_layout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;

public class ChoosePrescriptionFromGallery extends Fragment {
    private ArrayList<String> link;
    private ArrayList<String> nameLink;
    private PrescriptionRecyclerAdapter adapter;
    private View mainBt;
    private View topBt;
    private SliderAdapter pagerAdapter;
    private MaterialButton uploadBt;
    private UploadTask uploadTask;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.upload_prescription_screen, container, false);
    }
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        link=new ArrayList<>();
        nameLink=new ArrayList<>();
        MaterialButton tempaddMoreImgBt=view.findViewById(R.id.uploadFirstImageBt);
        topBt=tempaddMoreImgBt;
        tempaddMoreImgBt.setIcon(view.getContext().getResources().getDrawable(R.drawable.ic_gallery));
        MaterialButton addMoreImgBt=view.findViewById(R.id.uploadImage);
        addMoreImgBt.setIcon(view.getContext().getResources().getDrawable(R.drawable.ic_gallery));
        mainBt=addMoreImgBt;
        tempaddMoreImgBt.setOnClickListener(v -> showImageChooser());
        addMoreImgBt.setOnClickListener(v -> showImageChooser());
        uploadBt=view.findViewById(R.id.doneUploadBt);
        uploadBt.setOnClickListener(v -> showSuccessSnackBar("Saving prescription please wait...",false,true,"Cancel"));
        ImageButton seeImage=view.findViewById(R.id.seeImage);
        seeImage.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(), view_image_layout.class);
            intent.putExtra("DATA",link);
            startActivity(intent);
        });
        RecyclerView listView=view.findViewById(R.id.listview);
        initializePager();
         adapter=new PrescriptionRecyclerAdapter(getContext(),nameLink,link,tempaddMoreImgBt
                 ,mainBt,pagerAdapter,uploadBt,seeImage);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(mLayoutManager);
        listView.setAdapter(adapter);
    }

    private void showImageChooser() {
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getView()).getContext(), READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 102);
        }else{
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),new String[]{READ_EXTERNAL_STORAGE},102);
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            String value=String.valueOf(data.getData());
            if (link.contains(value))
            {
                showErrorSnackBar("Image already selected");
                return;
            }
            Log.e("Img","Img=>"+value);
            link.add(value);
            nameLink.add(""+System.currentTimeMillis());
            if (link.size()==1){
                    uploadBt.setVisibility(View.VISIBLE);
                    mainBt.setVisibility(View.VISIBLE);
                    topBt.setVisibility(View.GONE);
           }
            adapter.notifyDataSetChanged();
            pagerAdapter.notifyDataSetChanged();
        }else{Log.e("Img","Error " );
            showErrorSnackBar("An error occurred");
        }
    }

    private void showSuccessSnackBar(String msg, boolean finish, boolean inDefinite, String buttonMsg){
        Snackbar snackbar= Snackbar.make(getView().findViewById(R.id.linear_view), msg,Snackbar.LENGTH_SHORT);
        snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_FADE);
        snackbar.setBackgroundTint(getResources().getColor(R.color.colorPrimaryDark));
        snackbar.setTextColor(getResources().getColor(R.color.white));
        if (inDefinite)
        {   uploadImage(link,getActivity());
            snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
        }
        snackbar.show();
        Handler handler=new Handler();
        if (finish)
            handler.postDelayed(() -> getActivity().onBackPressed(),3000);
        if (buttonMsg!=null){
                snackbar.setActionTextColor(getResources().getColor(R.color.white));
                snackbar.setAction(buttonMsg, v -> {
                    if (buttonMsg.contains("Cancel"))
                        if (uploadTask!=null)
                            uploadTask.cancel();
                        else getActivity().onBackPressed();
                });
        }

    }
    private void showErrorSnackBar(String msg){
        Snackbar snackbar=Snackbar.make(getView(). findViewById(R.id.linear_view),msg,Snackbar.LENGTH_SHORT);
        snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_FADE);
        snackbar.setBackgroundTint(getResources().getColor(R.color.red));
        snackbar.setTextColor(getResources().getColor(R.color.white));
        snackbar.show();
    }

    private void initializePager() {
        ViewPager viewPager = getView().findViewById(R.id.viewPager);
        try {
            pagerAdapter = new SliderAdapter(getActivity(),viewPager,link,true);
            viewPager.setAdapter(pagerAdapter);
        }catch (Exception ignored){}
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
            showImageChooser();
        }
    }

    private void uploadImage(ArrayList<String> arrayList, Context context){
        FirebaseApp app=new FirebaseCustomAuth().loadCustomFirebase(context,
                "tablethuts-notification","tablethuts-notification");
        FirebaseStorage storage = FirebaseStorage.getInstance(app);
        ArrayList<String> downloadLink=new ArrayList<>();
        AtomicBoolean stop= new AtomicBoolean(false);
        for(String uri:arrayList) {
            if (stop.get())
                break;
            StorageReference storageRef = storage.getReference("Prescriptions/"+ Constants.uid+"/"+System.currentTimeMillis()+".png");
            uploadTask = storageRef.putFile(Uri.parse(uri));
            uploadTask.addOnFailureListener(exception -> {
                Log.d("Upload Image", "Image uploaded fail");
            }).addOnSuccessListener(taskSnapshot -> {
                Log.d("Upload Image", "Image uploaded");
                Objects.requireNonNull(Objects.requireNonNull(taskSnapshot.getMetadata()).getReference()).getDownloadUrl().addOnSuccessListener(uri1 -> {
                    try {
                        downloadLink.add(String.valueOf(uri1));
                        Log.e("Storage","Downloaded Link "+downloadLink);
                        if (downloadLink.size() == arrayList.size()) {
                            storePrescription(app,downloadLink);
                        }
                    }catch (Exception e){e.printStackTrace();}
                }).addOnFailureListener(e -> stop.set(true));
            }).addOnFailureListener(e -> {
                 stop.set(true);
                 showErrorSnackBar("Uploading failed "+e.getMessage());
            }).addOnCanceledListener(() -> {
                stop.set(true);
                showErrorSnackBar("Uploading Cancelled ");
                if (downloadLink.size()>0)
                    for (String img:downloadLink)
                        FirebaseStorage.getInstance(app).getReferenceFromUrl(img).delete();
            });
        }
    }

    private void storePrescription(FirebaseApp app, ArrayList<String> downloadLink) {
        try {
            HashMap<String, Object> map = new HashMap<>();
            map.put("images", downloadLink);
            map.put("status", "0");
            map.put("uid", Constants.uid);
            String time = String.valueOf(System.currentTimeMillis());
            FirebaseDatabase.getInstance(app).getReference("Prescriptions").child(time).
                    setValue(map).addOnSuccessListener(aVoid -> showSuccessSnackBar("Prescription stored successfully",true,false,"Go Back"));
        }catch (Exception e){e.printStackTrace();}
    }

}
