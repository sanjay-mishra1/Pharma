package com.example.pharma.chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pharma.Constants;
import com.example.pharma.R;
import com.example.pharma.firebase.FirebaseCustomAuth;
import com.example.pharma.model.ImageBasedModel;
import com.example.pharma.model.OrderModel;
import com.example.pharma.orders.AllOrdersFragment;
import com.example.pharma.orders.prescription.LoadMyDataFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ChatInformationSelectorFragment extends BottomSheetDialogFragment {
    public ChatInformationSelectorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_chat_information_selector, container, false);;
        String className= Objects.requireNonNull(getArguments()).getString("CLASS_NAME");
        if (Objects.requireNonNull(className).equals("Orders"))
        {checkOrders();
        }
        else {
            loadUserPersonalData(className);
        }
        return view;
    }

    private void loadUserPersonalData(String from) {
        FirebaseDatabase.getInstance().getReference("Customers").
                child(Constants.uid)
                .child(from).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<ImageBasedModel> data=new ArrayList<>();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    ImageBasedModel model = snapshot.getValue(ImageBasedModel.class);
                    if (model != null) {
                        model.setID(snapshot.getKey());
                        data.add(model);
                    }
                }
                loadFragment(from,data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkOrders() {
        FirebaseApp app= new FirebaseCustomAuth().loadCustomFirebase(getContext(),"tablethuts-orders");
        FirebaseDatabase.getInstance(app).getReference("Orders")
                .orderByChild("customer_id").equalTo(Constants.uid).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<OrderModel.OrderModelForRealtimeDb> allOrderMap=new ArrayList<>();
                        for (DataSnapshot ds:dataSnapshot.getChildren()){
                            OrderModel.OrderModelForRealtimeDb model=ds.getValue(OrderModel.OrderModelForRealtimeDb.class);
                            model.setOrder_id(ds.getKey());
                                allOrderMap.add(model);
                                allOrderMap.add(model);
                        }
                             loadAllOrders(allOrderMap);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadAllOrders(ArrayList<OrderModel.OrderModelForRealtimeDb> allOrderMap) {
        try {
            AllOrdersFragment fragment = new AllOrdersFragment();
            FragmentTransaction fragmentTransaction2;
            fragmentTransaction2 =getActivity(). getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putSerializable("ALL_ORDER", allOrderMap);
            bundle.putBoolean("CHAT", true);
            fragment.setArguments(bundle);
            fragmentTransaction2.replace(R.id.frame, Objects.requireNonNull(fragment));
            fragmentTransaction2.commit();
        }catch (Exception ignored){}
    }

    private void loadFragment(String from, ArrayList<ImageBasedModel> data) {
        Fragment fragmentClass =new LoadMyDataFragment();
        FragmentTransaction fragmentTransaction2;
        fragmentTransaction2= getActivity(). getSupportFragmentManager().beginTransaction();
        Bundle bundle=new Bundle();
        bundle.putSerializable("DATA",data);
        bundle.putString("FROM",from);
        bundle.putBoolean("CHAT",true);
        fragmentClass.setArguments(bundle);
        fragmentTransaction2.replace(R.id.frame, Objects.requireNonNull(fragmentClass));
        fragmentTransaction2.commit();
    }
}
