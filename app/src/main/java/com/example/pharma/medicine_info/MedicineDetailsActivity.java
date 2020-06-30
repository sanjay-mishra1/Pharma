package com.example.pharma.medicine_info;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.pharma.LoadRecentData;
import com.example.pharma.R;
import com.example.pharma.firebase.FirebaseCustomAuth;
import com.example.pharma.model.MedicineModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

public class MedicineDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_info);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Medicine");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        receiveData();
    }

    private void receiveData() {
//        MedicineModel model= (MedicineModel) getIntent().getSerializableExtra("DATA");
        String id= getIntent().getStringExtra("MEDICINE_ID");
        String name= getIntent().getStringExtra("MEDICINE_NAME");
        setTitle(name != null ? name : "Not Found");
        try {Log.e("Data","name->"+name+"id->"+id);
            loadDataFromFirebase(id);
        }catch (Exception e){e.printStackTrace();}

    }
    private void loadDataFromFirebase(String medicineid) {
//        "XshjooK1Jn7g72Xzx8r10"
        FirebaseApp app= new FirebaseCustomAuth().loadCustomFirebase(this,"tablethuts-medicines");
        FirebaseDatabase.getInstance(app).getReference("Medicines").child(medicineid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseFirestore.getInstance(app).collection("Medicines").document(medicineid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        try {
                            if (documentSnapshot != null){
                                MedicineModel.MedicineModelForRealtime modelForRealtime=dataSnapshot.getValue(MedicineModel.MedicineModelForRealtime.class);
                                MedicineModel modelForFireStore= documentSnapshot.toObject(MedicineModel.class);
                                if (modelForFireStore.getMedicine_disease()!=null)
                                Objects.requireNonNull(modelForFireStore).getMedicine_disease().remove("All");
                                modelForFireStore.setMedicine_id(dataSnapshot.getKey());
                                modelForFireStore.setMedicine_original_price((Long)
                                        modelForRealtime.getMedicine_variant().get(0)
                                                .get("medicine_original_price"));
                                findViewById(R.id.progressbar).setVisibility(View.GONE);
                                if (modelForRealtime != null)
                                {setupTabs(modelForRealtime,modelForFireStore);
                                    new LoadRecentData().addNewRecent(modelForFireStore.getMedicine_id(),MedicineDetailsActivity.this,"MED_RECENT");
                                }
                            }
                        }catch (Exception e){e.printStackTrace();}

                    }
                }).addOnFailureListener(e -> {

                }).addOnCanceledListener(() -> {

                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setupTabs(MedicineModel.MedicineModelForRealtime modelForRealtime, MedicineModel modelForFirestore) {
        if (modelForRealtime.getMedicine_note()==null){
            Fragment fragmentClass =new MedicineOverviewPage();
            FragmentTransaction fragmentTransaction2;
            Bundle bundle=new Bundle();
            bundle.putSerializable("Firestore",modelForFirestore);
            bundle.putSerializable("Realtime",modelForRealtime);
            fragmentClass.setArguments(bundle);
            findViewById(R.id.tabs).setVisibility(View.GONE);
            findViewById(R.id.horizontal_line).setVisibility(View.GONE);
            findViewById(R.id.viewPager).setVisibility(View.GONE);
            findViewById(R.id.frame).setVisibility(View.VISIBLE);
            try{fragmentTransaction2=  getSupportFragmentManager()
                    .beginTransaction();
                fragmentTransaction2.replace(R.id.frame, Objects.requireNonNull(fragmentClass));
                fragmentTransaction2.commit();
            }catch (Exception e){e.printStackTrace();}

        }else {
            findViewById(R.id.horizontal_line).setVisibility(View.VISIBLE);
            ViewPager viewPager = findViewById(R.id.viewPager);
            TabLayout tabs = findViewById(R.id.tabs);
            tabs.setVisibility(View.VISIBLE);
            tabs.addTab(tabs.newTab().setText("Overview"));
            tabs.addTab(tabs.newTab().setText("Details"));
            tabs.setTabGravity(TabLayout.GRAVITY_FILL);
            MyAdapter adapter = new MyAdapter(getSupportFragmentManager(),
                    modelForFirestore, modelForRealtime);
            viewPager.setAdapter(adapter);
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
            tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition(),true);
                }
                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }
    }
    static class MyAdapter extends FragmentPagerAdapter {

        ArrayList<String> data;
        MedicineModel.MedicineModelForRealtime forRealtime;
        MedicineModel forFireStore;
        MyAdapter(@NonNull FragmentManager fm, ArrayList<String> data) {
            super(fm);
            this.data=data;
        }

        MyAdapter(FragmentManager fm, MedicineModel modelForFirestore, MedicineModel.MedicineModelForRealtime modelForRealtime) {
            super(fm);
            this.forRealtime=modelForRealtime;
            this.forFireStore=modelForFirestore;

        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            Log.e("GetItem","Position"+position);
            Fragment fragment;
            Bundle bundle=new Bundle();
            if (position==0) {
                bundle.putSerializable("Realtime",forRealtime);
                bundle.putSerializable("Firestore",forFireStore);
                fragment = new MedicineOverviewPage();
                fragment.setArguments(bundle);
            }
            else {
                bundle.putSerializable("DATA",forRealtime.getMedicine_note());
                bundle.putString("medicine_id",forFireStore.getMedicine_id());
                fragment = new MedicineInfoPage();
                fragment.setArguments(bundle);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}
