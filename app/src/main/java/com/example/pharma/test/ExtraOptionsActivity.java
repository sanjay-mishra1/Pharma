package com.example.pharma.test;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pharma.Constants;
import com.example.pharma.R;
import com.example.pharma.firebase.FirebaseCustomAuth;
import com.example.pharma.model.ImageBasedModel;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

public class ExtraOptionsActivity extends AppCompatActivity {

    private boolean isRotate=true;
    private RecyclerView recyclerView;
    private String from;
    private String catID;
    private String catName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_new_test);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        from=getIntent().getStringExtra("FROM");
        FirebaseApp app;
        if (from!=null) {
            //check for floating bts
            if (from.contains("Test")) {
                configureScreenForTest();
            } else if (from.contains("Web")) {
                        configureScreenForWeb();
            } else {
                setTitle("Pending Prescriptions");
                app = new FirebaseCustomAuth().loadCustomFirebase(this, "tablethuts-notification");
                Query query = FirebaseDatabase.getInstance(app).getReference(from).orderByChild("status").equalTo(0);
                checkForFav(from, query);
            }
        }
    }

    private void configureScreenForWeb() {
        FirebaseApp app;
       try {
           if (Objects.equals(from, "Web Category")){
               setTitle("All Articles Category");
               app= new FirebaseCustomAuth().loadCustomFirebase(this,"tablethuts-extra");
               Query query= FirebaseDatabase.getInstance(app).getReference("Web Article Category");
               checkForFav(from,query);
           }else if (Objects.equals(from, "Web Articles"))
           {
               catID=getIntent().getStringExtra("CID");
               catName=getIntent().getStringExtra("CNAME");
               app= new FirebaseCustomAuth().loadCustomFirebase(this,"tablethuts-extra");
               Query query;
               Log.e("Catid","->"+catID);
               if (catID==null)
               {   setTitle("All Articles");
                   query = FirebaseDatabase.getInstance(app).getReference("Web Article");
               }
               else {String suffix;
                   if (catName.contains("articles")||catName.contains("article")||catName.contains("Articles")||catName.contains("Article"))
                       suffix="";
                   else suffix="'s Articles";
                   setTitle(catName + suffix);
                   query = FirebaseDatabase.getInstance(app).getReference("Web Article")
                           .orderByChild("category_id").equalTo(catID);
               }
               checkForFav(from,query);
           }
           else if (from.contains("Saved")){
               setTitle("Saved Articles");
               loadDataFromRealtimeDBForFav("Favorite Articles","Web Article");
           }else if (from.contains("Recent")){
               setTitle("Recent Articles");
               //loadDataFromRealtimeDBForFav("Recent Articles","Web Article");
               SharedPreferences sh=getSharedPreferences("RECENT_WEB",MODE_PRIVATE);
               Log.e("Recent","->"+sh.getAll());
               getData(new ArrayList<>((Collection<? extends String>) sh.getAll().values()),
                       false,"Web Article","RECENT_WEB");
           }
           from="Web Article";
       }catch (Exception e){
           from="Web Article";
       }
    }

    private void configureScreenForTest() {
        FirebaseApp app;
      try {
          if (Objects.equals(from, "Test_Report")) {
              setTitle("My Test");
              app= new FirebaseCustomAuth().loadCustomFirebase(this,"tablethuts-notification");
              Query query= FirebaseDatabase.getInstance(app).getReference("Test_Reports")
                      .orderByChild("uid").equalTo(Constants.uid);
              checkForFav(from,query);
          }else  if (Objects.equals(from, "All_Test_Category")){
              setTitle("All Test Category");
              app= new FirebaseCustomAuth().loadCustomFirebase(this,"tablethuts-extra");
              Query query= FirebaseDatabase.getInstance(app).getReference("Test Category");
              checkForFav(from,query);
          }
          else if (Objects.equals(from, "All_Test"))
          {
              app= new FirebaseCustomAuth().loadCustomFirebase(this,"tablethuts-extra");
              catID=getIntent().getStringExtra("CID");
              catName=getIntent().getStringExtra("CNAME");
              Query query;
              if (catID==null){
                  setTitle("All Test");
                  query = FirebaseDatabase.getInstance(app).getReference("Test");
              }
              else{   String suffix;
                  if (catName != null) {
                      if (catName.contains("Test")||catName.contains("test"))
                          suffix="";
                      else{
                          suffix="'s Test";
                      }
                      setTitle(catName+suffix);
                  }
                  query = FirebaseDatabase.getInstance(app).getReference("Test")
                          .orderByChild("category_id").equalTo(catID);

              }
//            Query query= FirebaseDatabase.getInstance(app).getReference("Test")
//                    .orderByChild("category_id").equalTo(catID);
              checkForFav(from,query);
          }
          else if (from.contains("Saved")){
              setTitle("Saved Test");
              loadDataFromRealtimeDBForFav("Favorite Test","Test");
          }else if (from.contains("Recent")){
              setTitle("Recent Test");
              //loadDataFromRealtimeDBForFav("Recent Test","Test");
              //loadDataFromLocal(getSharedPreferences("RECENT_TEST",MODE_PRIVATE),"Test","RECENT_TEST");
              SharedPreferences sh=getSharedPreferences("RECENT_TEST",MODE_PRIVATE);
              Log.e("Recent","->"+sh.getAll());
              getData(new ArrayList<>((Collection<? extends String>) sh.getAll().values()),
                      false,"Test","RECENT_TEST");
          }
          from="Test";
      }catch (Exception e){
          from="Test";
//            e.printStackTrace();
//            showEmptyScreen();
      }

    }

    void showEmptyScreen(){
        findViewById(R.id.no_data).setVisibility(View.VISIBLE);
        ImageView imageView=findViewById(R.id.empty_img);
        TextView textView=findViewById(R.id.empty_title);
        String text="";
        int img=0;
        if(from.contains("Report")){
            text="No Pending Test Report Found";
            img=R.drawable.ic_report;
        }else if (from.contains("Test")){
            text="No Test Found";
            img=R.drawable.ic_report;
        }else if (from.contains("Presc")){
            text="No Pending Prescription Found";
            img=R.drawable.ic_prescription;
        }else if (from.contains("Web")){
            text="No Web Category Found";
            img=R.drawable.ic_domain;
        }else {
            text="Nothing Found";
            img=R.drawable.ic_no_medicine;
        }
        textView.setText(text);
        Glide.with(this).load(img).into(imageView);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void checkForFav(String from, Query query) {
        SharedPreferences sharedPreferences;
        String dbName;
        String prefName;
        if (from.contains("Test"))
        {   dbName="Test";
            prefName="Favorite Test";
        }
        else {
            dbName="Web Article";
            prefName="Favorite Articles";
        }
        sharedPreferences=getSharedPreferences(prefName,MODE_PRIVATE);
        if (sharedPreferences.getBoolean("FavLoaded",false)){
            loadFirebase(from,query,sharedPreferences);
        }else{SharedPreferences.Editor editor=sharedPreferences.edit();
            FirebaseDatabase.getInstance().getReference("Customers").child(Constants.uid).child(dbName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot data) {
                                editor.putBoolean("FavLoaded",true);
                                if (data.hasChildren())
                                    for (DataSnapshot d:data.getChildren())
                                        editor.putBoolean(d.getKey(),true);
                                editor.apply();
                                loadFirebase(from,query,sharedPreferences);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void loadFirebase(String from, Query query, SharedPreferences sharedPreferences) {
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<ImageBasedModel> data=new ArrayList<>();
                findViewById(R.id.progressbar).setVisibility(View.GONE);
                for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    ImageBasedModel model = snapshot.getValue(ImageBasedModel.class);
                    if (model != null) {
                        model.setID(snapshot.getKey());
                        model.setFav(sharedPreferences.contains(model.getID()));
                        data.add(model);
                    }
                }
                if (!isDestroyed()) {
                    if (data.size() > 0)
                        loadFragment(data);
                    else showEmptyScreen();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void loadFragment(ArrayList<ImageBasedModel> data) {
        try {
            Fragment fragmentClass = new LoadMyDataFragment();
            FragmentTransaction fragmentTransaction2;
            fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putSerializable("DATA", data);
            bundle.putString("FROM", getIntent().getStringExtra("FROM"));
            if (Objects.requireNonNull(getIntent().getStringExtra("FROM")).contains("Category"))
                bundle.putBoolean("TYPE",false);
            fragmentClass.setArguments(bundle);
            fragmentTransaction2.replace(R.id.frame, Objects.requireNonNull(fragmentClass));
            fragmentTransaction2.commit();
        }catch (Exception e){e.printStackTrace();}
    }

    private void loadDataFromRealtimeDBForFav(String name,String dbName) {
        if (name.contains("Fav")) {
            if (Constants.uid == null)
                Constants.uid = getSharedPreferences("USER_CREDENTIALS", MODE_PRIVATE).getString("UID", null);
            SharedPreferences sharedPreferences = getSharedPreferences(name, MODE_PRIVATE);
            if (sharedPreferences.getBoolean("FavLoaded", false)) {
                loadDataFromLocal(sharedPreferences,dbName,name);
            } else {
                readFromUserDb(name,dbName,name);
            }
        }else readFromUserDb(name,dbName,name);

    }

    private void readFromUserDb(String name,String dbName,String prefName) {
        FirebaseDatabase.getInstance().getReference("Customers").child(Constants.uid)
                .child(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, String> data = (HashMap<String, String>) dataSnapshot.getValue();
                if (data!=null) {
                    try{
                        getData(new ArrayList<>(data.keySet()), false,dbName,prefName);
                    }catch (Exception e){e.printStackTrace();}
                }
                else loadEmptyCartUI();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadEmptyCartUI() {
    }

    private void loadDataFromLocal(SharedPreferences sharedPreferences,String dbName,String prefName) {
        Log.e("Recent",dbName+"->"+sharedPreferences.getAll());
        getData(new ArrayList<>(sharedPreferences.getAll().keySet()),
                false,dbName,prefName);

    }

    private void getData(ArrayList<String> articles,boolean storeToLocal,String dbName,String prefName) {
        SharedPreferences preferences=getSharedPreferences(prefName,MODE_PRIVATE);
        SharedPreferences.Editor edit=preferences.edit();
        if (storeToLocal) edit.putBoolean("FavLoaded", true);
        FirebaseApp app= new FirebaseCustomAuth().loadCustomFirebase(this,"tablethuts-extra");
        ArrayList<ImageBasedModel> data=new ArrayList<>();
        final int[] count = {1};
        int size=articles.size();
        for(String id:articles) {
            if (storeToLocal) {
                edit.putInt(id, 1);
                edit.apply();
            }
            if (!id.equalsIgnoreCase("favloaded")&&!id.equalsIgnoreCase("POINTER")) {
                FirebaseDatabase.getInstance(app).getReference(dbName).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {count[0]++;
                            findViewById(R.id.progressbar).setVisibility(View.GONE);
                            if (snapshot.exists()) {
                                ImageBasedModel model = snapshot.getValue(ImageBasedModel.class);
                                if (model != null) {
                                    model.setID(snapshot.getKey());
                                    model.setFav(preferences.contains(model.getID()));
                                    data.add(model);
                                }
                            }
                            if (count[0]==size)
                                if (!isDestroyed()) {
                                    if (data.size() > 0)
                                        loadFragment(data);
                                    else showEmptyScreen();
                                }
                        }catch (Exception e){e.printStackTrace();}
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }

}



