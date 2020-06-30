package com.example.pharma.medicine;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.pharma.Constants;
import com.example.pharma.R;
import com.example.pharma.firebase.FirebaseCustomAuth;
import com.example.pharma.recycler.SimpleRecycler;
import com.facebook.shimmer.ShimmerFrameLayout;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CustomMedicineListActivity extends AppCompatActivity {
    ShimmerFrameLayout shimmer;
    TextView cartText;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_recyclerview_homepage);
        shimmer=findViewById(R.id.shimmer);
        cartText=findViewById(R.id.totalCart);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);

        if (getIntent().getStringExtra("FROM").equals("FAV"))
        {         setTitle("My Saved Medicines");
            loadDataFromRealtimeDBForFav();
        }
       else {        setTitle(getIntent().getStringExtra("DATA_NAME"));
            String[] medicines = getIntent().getStringArrayExtra("DATA");
            FirebaseApp app = new FirebaseCustomAuth().loadCustomFirebase(this, "tablethuts-medicines");
            fetchMedicines(app, medicines != null ? medicines : new String[0]);
        }
    }

    private void fetchMedicines(FirebaseApp app,String[] medicines) {
        RecyclerView recyclerView= findViewById(R.id.recycle);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        ArrayList<HashMap<String,Object>> data=new ArrayList<>();
        SimpleRecycler adapter=new SimpleRecycler(data,R.layout.linear_medicine_card,this,"Custom",
                cartText);
        recyclerView.setAdapter(adapter);
        SharedPreferences sharedPreferencesCart=getSharedPreferences("Cart",MODE_PRIVATE);
        cartText.setText(String.valueOf(sharedPreferencesCart.getAll().size()));
        SharedPreferences sharedPreferencesFav=getSharedPreferences("Favorite",MODE_PRIVATE);
        for(String id:medicines){
            FirebaseDatabase.getInstance(app).getReference("Medicines").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    HashMap<String,Object> map=new HashMap<>();
                    try {
                        HashMap<String, Object> dataSnapshotValue = (HashMap<String, Object>) dataSnapshot.getValue();
                        map.put("medicine_img", dataSnapshotValue != null ? ((ArrayList<String>) dataSnapshotValue.get("medicine_images")).get(0) : "NA");
                        map.put("medicine_name", dataSnapshotValue != null ? dataSnapshotValue.get("medicine_name") : "NA");
                        map.put("medicine_id", dataSnapshotValue != null ? id : "NA");
                        map.put("medicine_max_quantity", dataSnapshotValue != null ? dataSnapshotValue.get("medicine_max_quantity") : "NA");
                        map.put("medicine_in_my_fav", sharedPreferencesFav.contains(id));
                        map.put("medicine_in_my_cart", sharedPreferencesCart.contains(id));
                        map.put("medicine_price", dataSnapshotValue != null ? dataSnapshotValue.get("medicine_price") : "NA");
                        map.put("medicine_original_price", ((HashMap<String,Object>) ((ArrayList<Object>)
                                dataSnapshotValue.get("medicine_variant")).get(0)).get("medicine_original_price"));

                        data.add(map);
                        adapter.notifyDataSetChanged();
                    }catch (Exception e){e.printStackTrace();}
                 if (shimmer.isShimmerVisible())
                 {shimmer.stopShimmer();shimmer.setVisibility(View.GONE);}
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void loadDataFromRealtimeDBForFav() {
        if (Constants.uid==null)
            Constants.uid=  getSharedPreferences("USER_CREDENTIALS",MODE_PRIVATE).getString("UID",null);
        SharedPreferences sharedPreferences=getSharedPreferences("Favorite",MODE_PRIVATE);
        cartText.setText(String.valueOf(getSharedPreferences("Cart",MODE_PRIVATE).getAll().size()));
        if (sharedPreferences.getBoolean("FavLoaded",false)){
            loadDataFromLocal(sharedPreferences);
        }else {
            FirebaseDatabase.getInstance().getReference("Customers").child(Constants.uid)
                    .child("Favorites").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    HashMap<String, String> data = (HashMap<String, String>) dataSnapshot.getValue();
                    if (data!=null) {
                        try{
                            getMedicineData(new ArrayList<>(data.keySet()), true);
                        }catch (Exception e){e.printStackTrace();}
                    }
                    else loadEmptyCartUI();

                    shimmer.stopShimmer();shimmer.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void loadEmptyCartUI() {
    }

    private void loadDataFromLocal(SharedPreferences sharedPreferences) {
        getMedicineData(new ArrayList<>(sharedPreferences.getAll().keySet()),
                false);

    }

    private void getMedicineData(ArrayList<String> medicine,boolean storeToLocal) {
        RecyclerView recyclerView= findViewById(R.id.recycle);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        ArrayList<HashMap<String,Object>> data=new ArrayList<>();
        SimpleRecycler adapter=new SimpleRecycler(data,R.layout.linear_medicine_card,this,"Custom", cartText);
        recyclerView.setAdapter(adapter);
        SharedPreferences preferences=getSharedPreferences("Favorite",MODE_PRIVATE);
        SharedPreferences preferencesCart=getSharedPreferences("Cart",MODE_PRIVATE);
        SharedPreferences.Editor edit=preferences.edit();
        if (storeToLocal) edit.putBoolean("FavLoaded", true);
        FirebaseApp app= new FirebaseCustomAuth().loadCustomFirebase(this,"tablethuts-medicines");

        final int[] count = {0};
        for(String id:medicine) {
            if (storeToLocal) {
                edit.putInt(id, 1);
                edit.apply();
            }
            if (!id.equalsIgnoreCase("favloaded")) {
                FirebaseDatabase.getInstance(app).getReference("Medicines").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        HashMap<String,Object> map=new HashMap<>();
                        try {
                            HashMap<String, Object> dataSnapshotValue = (HashMap<String, Object>) dataSnapshot.getValue();
                            map.put("medicine_img", dataSnapshotValue != null ? ((ArrayList<String>) dataSnapshotValue.get("medicine_images")).get(0) : "NA");
                            map.put("medicine_name", dataSnapshotValue != null ? dataSnapshotValue.get("medicine_name") : "NA");
                            map.put("medicine_id", dataSnapshotValue != null ? id : "NA");
                            map.put("medicine_in_my_fav", true);
                            map.put("medicine_max_quantity", dataSnapshotValue != null ? dataSnapshotValue.get("medicine_max_quantity") : "NA");
                            map.put("medicine_in_my_cart", preferencesCart.contains(id));
                            map.put("medicine_price", dataSnapshotValue != null ? dataSnapshotValue.get("medicine_price") : "NA");
                            map.put("medicine_original_price", ((HashMap<String,Object>) ((ArrayList<Object>)
                                    dataSnapshotValue.get("medicine_variant")).get(0)).get("medicine_original_price"));
                            data.add(map);
                            adapter.notifyDataSetChanged();
                            if (shimmer.isShimmerVisible())
                            {shimmer.stopShimmer();shimmer.setVisibility(View.GONE);}
                        }catch (Exception e){e.printStackTrace();}
                       }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
