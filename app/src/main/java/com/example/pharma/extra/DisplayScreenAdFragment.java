package com.example.pharma.extra;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pharma.R;
import com.example.pharma.firebase.FirebaseCustomAuth;
import com.example.pharma.imgeslider.SliderAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class DisplayScreenAdFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.top_imageview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String tag=getTag();
        Log.e("Tag","->"+tag);
        if (tag!=null && !tag.isEmpty())
        loadAds(tag);
    }

    private void loadAds(String tag) {
        FirebaseApp app= new FirebaseCustomAuth().loadCustomFirebase(getContext(),"tablethuts-extra");
        FirebaseDatabase.getInstance(app).getReference("UserScreen").child("Top Ads")
                .orderByChild("screen").equalTo(tag).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if (dataSnapshot.hasChildren()) {
                   HashMap<String, String> img_data = new HashMap<>();
                   ArrayList<String> img = new ArrayList<>();
                   for (DataSnapshot d : dataSnapshot.getChildren()) {
                       String i = (String) d.child("img").getValue();
                       Log.e("displayAds", "->" + i);
                       img_data.put(i, (String) d.child("action").getValue());
                       img.add(i);
                   }
                   setAds(img, img_data);
               }else
                   getView().findViewById(R.id.viewPager).setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void setAds(ArrayList<String> imgs, HashMap<String, String> data){
        try{
            ViewPager viewPager = getView().findViewById(R.id.viewPager);
            viewPager.setClipToPadding(false);
            viewPager.setPageMargin(dpToPx(15));
            SliderAdapter adapter;
                adapter= new SliderAdapter(getContext(), viewPager, imgs,data);
                viewPager.setAdapter(adapter);
        }catch (Exception e){e.printStackTrace();
        }
    }
    private int dpToPx(int dp) {
        Resources r = Objects.requireNonNull(getContext()). getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
