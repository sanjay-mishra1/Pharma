package com.example.pharma.test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.pharma.ListActivityForImage;
import com.example.pharma.R;
import com.example.pharma.extra.DisplayScreenAdFragment;
import com.example.pharma.extra.InfoBoxBottomSheet;
import com.example.pharma.firebase.FirebaseCustomAuth;
import com.example.pharma.orders.prescription.PrescriptionActivity;
import com.example.pharma.recycler.HomePageRecycler;
import com.example.pharma.search.SearchFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TestActivity extends AppCompatActivity {
    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Test");
        intent=new Intent(new Intent(this, ExtraOptionsActivity.class));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        loadSearchFragment();
        loadFragment();
        loadData();
    }

    private void loadData() {
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(TestActivity.this, LinearLayoutManager.VERTICAL, false);
        RecyclerView recyclerView=findViewById(R.id.recycle);
        recyclerView.setLayoutManager(mLayoutManager);
        FirebaseDatabase.getInstance(new FirebaseCustomAuth()
                .loadCustomFirebase(this,"tablethuts-extra"))
                .getReference("UserScreen").child("Test").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()&& dataSnapshot.hasChildren()){
                    Log.e("Test Content Loaded","->"+dataSnapshot.getValue());
                    ArrayList<HashMap<String, Object>> data= (ArrayList<HashMap<String, Object>>) dataSnapshot.getValue();
                    HomePageRecycler adapter =new HomePageRecycler(TestActivity.this,data, findViewById(R.id.totalCart));
                    recyclerView.setAdapter(adapter);
                }else Log.e("Test Content","->Not found ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadSearchFragment() {
        try{
            Fragment fragmentClass =new SearchFragment();
            FragmentTransaction fragmentTransaction2;
            fragmentTransaction2=  getSupportFragmentManager().beginTransaction();
            fragmentTransaction2.replace(R.id.search_frame, Objects.requireNonNull(fragmentClass),"test");
            fragmentTransaction2.commit();
        }catch (Exception ignored){}
    }

    private void loadFragment() {
        try{
            Fragment fragmentClass =new DisplayScreenAdFragment();
            FragmentTransaction fragmentTransaction2;
            fragmentTransaction2=  getSupportFragmentManager().beginTransaction();
            fragmentTransaction2.replace(R.id.frame, Objects.requireNonNull(fragmentClass),"Test");
            fragmentTransaction2.commit();
        }catch (Exception ignored){}
    }

    public void loadAll(View view) {
        intent.putExtra("FROM","All_Test");
        startActivity(intent);
    }

    public void load_category(View view) {
        intent.putExtra("FROM","All_Test_Category");
        startActivity(intent);
    }

    public void third_option(View view) {
        Intent intent=new Intent(this, PrescriptionActivity.class);
        startActivity(intent);
    }

    public void fourth_option(View view) {
        InfoBoxBottomSheet infoBoxBottomSheet=new InfoBoxBottomSheet(R.drawable.ic_phone,"Book test on call","Give us missed call on +919893909516.\nWe will call you back to book your test shortly.","call_for_test","Miss Call");
        infoBoxBottomSheet.showNow(getSupportFragmentManager(),"Test");
    }

    public void seenRecent(View view) {
        intent.putExtra("FROM","Test Recent");
        startActivity(intent);
    }

    public void seenSaved(View view) {
        intent.putExtra("FROM","Test Saved");
        startActivity(intent);
    }

    public void seeMyReports(View view) {
        Intent intent=new Intent(this, ExtraOptionsActivity.class);
        intent.putExtra("FROM","Test_Report");
        startActivity(intent);
    }
    public void my_test_prescriptions(View view) {
        Intent intent=new Intent(this, ListActivityForImage.class);
        intent.putExtra("FROM","Prescription");
        startActivity(intent);
     }
}
