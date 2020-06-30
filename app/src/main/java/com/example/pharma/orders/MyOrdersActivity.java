package com.example.pharma.orders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.pharma.Constants;
import com.example.pharma.R;
import com.example.pharma.firebase.FirebaseCustomAuth;
import com.example.pharma.model.OrderModel;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MyOrdersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_orders);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        setTitle("My Orders");
        checkOrders();
    }

    private void checkOrders() {
        FirebaseApp app= new FirebaseCustomAuth().loadCustomFirebase(this,"tablethuts-orders");
        Log.e("uid","->"+Constants.uid);
        FirebaseDatabase.getInstance(app).getReference("Orders")
                .orderByChild("customer_id").equalTo(Constants.uid).
                addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             ArrayList<OrderModel.OrderModelForRealtimeDb> activeOrderMap=new ArrayList<>();
                ArrayList<OrderModel.OrderModelForRealtimeDb> allOrderMap=new ArrayList<>();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    String orderStatus=((String)ds.child("current_order_status").getValue());
                    OrderModel.OrderModelForRealtimeDb model=ds.getValue(OrderModel.OrderModelForRealtimeDb.class);
                    model.setOrder_id(ds.getKey());
                    if (Objects.requireNonNull(orderStatus).equalsIgnoreCase("complete")
                            ||orderStatus.equalsIgnoreCase("cancel") ){
                        allOrderMap.add(model);
                    }else{
                        activeOrderMap.add(model);
                    }
                }
                if (!activeOrderMap.isEmpty())
                    loadAllOrders(true,activeOrderMap,allOrderMap);
                else loadAllOrders(false,null,allOrderMap);
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

    void loadAllOrders(boolean activeOrders, ArrayList<OrderModel.OrderModelForRealtimeDb> activeOrderList,
                      ArrayList<OrderModel.OrderModelForRealtimeDb> allOrderMap) {
       try {
           ShimmerFrameLayout shimmerFrameLayout = findViewById(R.id.shimmer);
           shimmerFrameLayout.stopShimmer();
           shimmerFrameLayout.setVisibility(View.GONE);
           Fragment fragment;
           if (!activeOrders) {
               fragment = new AllOrdersFragment();
           } else fragment = new ShowOrdersFragment();
           FragmentTransaction fragmentTransaction2;
           fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
           Bundle bundle = new Bundle();
           bundle.putSerializable("ACTIVE_ORDER", activeOrderList);
           bundle.putSerializable("ALL_ORDER", allOrderMap);
           fragment.setArguments(bundle);
           fragmentTransaction2.replace(R.id.frame, Objects.requireNonNull(fragment));
           fragmentTransaction2.commit();
       }catch (Exception ignored){}
    }

}
