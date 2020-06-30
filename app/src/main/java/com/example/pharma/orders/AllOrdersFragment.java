package com.example.pharma.orders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pharma.R;
import com.example.pharma.model.OrderModel;
import com.example.pharma.recycler.OrderAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllOrdersFragment extends Fragment {

    public AllOrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            ArrayList<OrderModel.OrderModelForRealtimeDb> allOrders= (ArrayList<OrderModel.OrderModelForRealtimeDb>) getArguments().getSerializable("ALL_ORDER");
            Log.e("AllOrders","DataReceived ->"+allOrders);
            RecyclerView recyclerView= Objects.requireNonNull(getView()).findViewById(R.id.recycle);
            LinearLayoutManager mLayoutManager =
                    new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(mLayoutManager);
            OrderAdapter adapter=new OrderAdapter(allOrders,getContext());
            recyclerView.setAdapter(adapter);
        }catch (Exception e){e.printStackTrace();}
    }
}
