package com.example.pharma;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.pharma.firebase.FirebaseCustomAuth;
import com.example.pharma.model.ImageBasedModel;
import com.example.pharma.orders.prescription.LoadMyDataFragment;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

/*
* This activity is used to display the list for the prescription uploaded by the user
* It also for all the test report uploaded by the admin
* */
public class ListActivityForImage extends AppCompatActivity {
    private ShimmerFrameLayout shimmer;
    private String from;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_recyclerview_homepage);
        shimmer=findViewById(R.id.shimmer);
        TextView cartText = findViewById(R.id.totalCart);
        int totalCart=getSharedPreferences("Cart",MODE_PRIVATE).getAll().size();
        cartText.setText(String.valueOf(totalCart));
        findViewById(R.id.cart).setOnClickListener(v -> startActivity(new Intent(ListActivityForImage.this,CartActivity.class)));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        from=Objects.requireNonNull(getIntent().getStringExtra("FROM"));
        Log.e("From",from);
        if (from.contains("Pres"))
        {         setTitle("My Prescriptions");
            loadDataFromRealtimeDB(FirebaseDatabase.getInstance().getReference("Customers").
                    child(Constants.uid)
                    .child("Prescriptions"));
        }
        else if (from.contains("Test")){
            setTitle("Test Reports");
            loadDataFromRealtimeDB(FirebaseDatabase.getInstance().getReference("Customers").
                    child(Constants.uid)
                    .child("Test Reports"));
        }else{
            setTitle("Articles");
            FirebaseApp app=new FirebaseCustomAuth().loadCustomFirebase(this,"Tablethuts-Extra");
            DatabaseReference database=FirebaseDatabase.getInstance(app).getReference().
                    child("Web Articles");
            loadDataFromRealtimeDB(database);
        }
    }

    private void loadDataFromRealtimeDB(DatabaseReference database) {
        if (Constants.uid==null)
            Constants.uid=  getSharedPreferences("USER_CREDENTIALS",MODE_PRIVATE).
                    getString("UID",null);
        ArrayList<ImageBasedModel> data=new ArrayList<>();

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    ImageBasedModel model = snapshot.getValue(ImageBasedModel.class);
                    if (model != null) {
                        model.setID(snapshot.getKey());
                        data.add(model);
                    }
                }
                loadFragment(data);
                shimmer.stopShimmer();shimmer.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadFragment(ArrayList<ImageBasedModel> data) {
        if (!isDestroyed()) {
            Fragment fragmentClass = new LoadMyDataFragment();
            FragmentTransaction fragmentTransaction2;
            fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putSerializable("DATA", data);
            bundle.putString("FROM", from);
            fragmentClass.setArguments(bundle);
            fragmentTransaction2.replace(R.id.frame, Objects.requireNonNull(fragmentClass));
            fragmentTransaction2.commit();
        }
    }

    private void loadEmptyCartUI() {
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
