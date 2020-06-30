package com.example.pharma.test;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.pharma.LoadRecentData;
import com.example.pharma.R;
import com.example.pharma.firebase.FirebaseCustomAuth;
import com.example.pharma.model.ImageBasedModel;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class ShowTestDetails extends AppCompatActivity {
    private ImageBasedModel model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_test_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Test");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        receive();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void receive() {
        String tid=getIntent().getStringExtra("DATA");
        model= (ImageBasedModel) getIntent().getSerializableExtra("MODEL");
        if (model==null)
            loadTestDetails(tid);
        else loadData();
    }

    private void loadTestDetails(String tid) {
        FirebaseApp app=new FirebaseCustomAuth().loadCustomFirebase(this,"tablethuts-extra");
        FirebaseDatabase.getInstance(app).getReference("Test").child(tid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                    ImageBasedModel model=dataSnapshot.getValue(ImageBasedModel.class);
                    model.setID(tid);
                    loadData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void savedToRecent(String tid) {
        new LoadRecentData().addNewRecent(tid,this,getString(R.string.test_pref_name));
    }

    private void loadData() {
        findViewById(R.id.progressbar).setVisibility(View.GONE);
        findViewById(R.id.data_card).setVisibility(View.VISIBLE);
        savedToRecent(model.getID());
        TextView nameText=findViewById(R.id.name);
        nameText.setText(model.getName());
        setTitle(model.getName());
        TextView priceText=findViewById(R.id.price);
        TextView labText=findViewById(R.id.lab_name);
        if (model.getLab()!=null)
        {   ArrayList<HashMap<String,Object>> allLabs= (ArrayList<HashMap<String, Object>>) model.getLab();
            HashMap<String,Object>lab=allLabs.get(0);
            labText.setText((String)lab.get("name"));
            long price= (long) lab.get("price");
            priceText.setText(String.format(Locale.UK,"₹ %d", price));
            long originalPrice= (long) lab.get("original_price");
            if (originalPrice!=0 && originalPrice!=price){
                TextView priceOriginalText=findViewById(R.id.originalPrice);
                findViewById(R.id.discount_field).setVisibility(View.VISIBLE);
                priceOriginalText.setText(String.format(Locale.UK,"₹ %d", originalPrice));
                TextView priceDiscountText=findViewById(R.id.discount);
                priceOriginalText.setText(String.format(Locale.UK, "₹ %d", originalPrice));
                priceOriginalText.setPaintFlags(priceOriginalText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                int dis = (int) (((originalPrice - price) * 100) / originalPrice);
                priceDiscountText.setText(String.format(Locale.UK, "%d%% OFF", dis));
            }
            if (model.getNote()!=null)
                setupTabs((HashMap<String, String>) model.getNote());

        }
    }

    private void setupTabs(HashMap<String,String> note) {
        findViewById(R.id.tab_title).setVisibility(View.VISIBLE);
        findViewById(R.id.tabcard).setVisibility(View.VISIBLE);
        ViewPager viewPager = findViewById(R.id.viewPager);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setVisibility(View.VISIBLE);
        ArrayList<String> data=new ArrayList<>();
        for (String title:note.keySet()){
            tabs.addTab(tabs.newTab().setText(title));
            data.add(note.get(title));
        }
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);
        MyAdapter adapter = new MyAdapter(getSupportFragmentManager(),
                data);
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

    public void selectButtonOnClick(View view) {
       try {
           Intent intent = new Intent(this, LabSelectorActivity.class);
           intent.putExtra("DATA", (ArrayList<HashMap<String, Object>>) model.getLab());
           startActivity(intent);
       }catch (Exception e){e.printStackTrace();}
    }

    static class MyAdapter extends FragmentPagerAdapter {

        ArrayList<String> data;
        MyAdapter(@NonNull FragmentManager fm, ArrayList<String> data) {
            super(fm);
            this.data=data;
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            Log.e("GetItem","Position"+position);
            Fragment fragment;
            Bundle bundle=new Bundle();
                bundle.putString("DATA",data.get(position));
                fragment = new SimpleFragment();
                fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return data.size();
        }
    }

}
