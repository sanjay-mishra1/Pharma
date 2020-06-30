package com.example.pharma.medicine;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pharma.GridSpacingItemDecoration;
import com.example.pharma.R;
import com.example.pharma.firebase.FirebaseCustomAuth;
import com.example.pharma.model.CategoryModel;
import com.example.pharma.recycler.CategoryRecycler;
import com.example.pharma.recycler.CategoryViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AllMedicineDiseaseFragment extends Fragment {
    FirebaseRecyclerAdapter<CategoryModel, CategoryViewHolder> fr;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_category, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 4);
        RecyclerView recyclerView= Objects.requireNonNull(getView()).findViewById(R.id.recycle);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(4, dpToPx(5), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        firebase(recyclerView);
    }

    private void firebase(RecyclerView recyclerView) {
        ArrayList<CategoryModel> data=new ArrayList<>();
        CategoryRecycler adapter=new CategoryRecycler(getContext(),data,"medicine_disease");
        recyclerView.setAdapter(adapter);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance(new FirebaseCustomAuth().loadCustomFirebase(getContext(),"tablethuts-extra")).getReference().child("Disease");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    CategoryModel model=snapshot.getValue(CategoryModel.class);
                    Objects.requireNonNull(model).setCategory_id(snapshot.getKey());
                    data.add(model);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


}
