package com.example.pharma.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.pharma.R;
import com.example.pharma.extra.DisplayScreenAdFragment;
import com.example.pharma.firebase.FirebaseCustomAuth;
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

public class WebArticlesHomeActivity extends AppCompatActivity {
    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Health Articles");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        findViewById(R.id.buttons).setVisibility(View.GONE);
        intent=new Intent(new Intent(this, ExtraOptionsActivity.class));

        loadSearchFragment();
        configureScreen();
        loadFragment();
        loadData();
    }
    private void loadData() {
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(WebArticlesHomeActivity.this, LinearLayoutManager.VERTICAL, false);
        RecyclerView recyclerView=findViewById(R.id.recycle);
        recyclerView.setLayoutManager(mLayoutManager);
        FirebaseDatabase.getInstance(new FirebaseCustomAuth()
                .loadCustomFirebase(this,"tablethuts-extra"))
                .getReference("UserScreen").child("Web").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()&& dataSnapshot.hasChildren()){
                    ArrayList<HashMap<String, Object>> data= (ArrayList<HashMap<String, Object>>) dataSnapshot.getValue();
                    HomePageRecycler adapter =new HomePageRecycler(WebArticlesHomeActivity.this,data,null);
                    recyclerView.setAdapter(adapter);
                }
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

    private void loadSearchFragment() {
        try{
            Fragment fragmentClass =new SearchFragment();
            FragmentTransaction fragmentTransaction2;
            fragmentTransaction2=  getSupportFragmentManager().beginTransaction();

            fragmentTransaction2.replace(R.id.search_frame,
                    Objects.requireNonNull(fragmentClass)
                    ,"web Article");
            fragmentTransaction2.commit();
        }catch (Exception ignored){}
    }

    private void loadFragment() {
        try{
            Fragment fragmentClass =new DisplayScreenAdFragment();
            FragmentTransaction fragmentTransaction2;
            fragmentTransaction2=  getSupportFragmentManager().beginTransaction();
            fragmentTransaction2.replace(R.id.frame, Objects.requireNonNull(fragmentClass),"Web Article");
            fragmentTransaction2.commit();
        }catch (Exception ignored){}
    }

    private void configureScreen() {
        TextView allItems=findViewById(R.id.all_items);
        TextView allCategory=findViewById(R.id.load_category);
        TextView recent=findViewById(R.id.third_option);
        TextView saved=findViewById(R.id.fourth_option);
        setData(allItems,"All Articles",R.drawable.ic_domain);
        setData(allCategory,"All Category",R.drawable.ic_web_category);
        setData(recent,"Recent",R.drawable.ic_resent_black_24dp);
        setData(saved,"Saved",R.drawable.ic_fav_list);

    }
    private void setData(TextView textView,String text,int drawable){
        textView.setText(text);
        textView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(drawable),null,null,null);
    }
    public void loadAll(View view) {
        intent.putExtra("FROM","Web Articles");
        startActivity(intent);
    }

    public void load_category(View view) {
        intent.putExtra("FROM","Web Category");
        startActivity(intent);
    }

    public void third_option(View view) {
        intent.putExtra("FROM","Web Recent");
        startActivity(intent);
    }

    public void fourth_option(View view) {
        intent.putExtra("FROM","Web Saved");
        startActivity(intent);
    }
}
