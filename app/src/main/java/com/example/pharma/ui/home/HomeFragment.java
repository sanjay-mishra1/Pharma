package com.example.pharma.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pharma.CartActivity;
import com.example.pharma.R;
import com.example.pharma.chat.help_activity;
import com.example.pharma.extra.DisplayScreenAdFragment;
import com.example.pharma.firebase.FirebaseCustomAuth;
import com.example.pharma.medicine.CustomMedicineListActivity;
import com.example.pharma.medicine.SearchActivity;
import com.example.pharma.model.CategoryModel;
import com.example.pharma.orders.MyOrdersActivity;
import com.example.pharma.orders.prescription.PrescriptionActivity;
import com.example.pharma.recycler.CategoryRecycler;
import com.example.pharma.recycler.HomePageRecycler;
import com.example.pharma.reminder.MedicineReminderActivity;
import com.example.pharma.test.TestActivity;
import com.example.pharma.test.WebArticlesHomeActivity;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeFragment extends Fragment {

    private ShimmerFrameLayout shimmerFrameLayout;
    private RecyclerView recyclerView;
    private TextView cartText;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        shimmerFrameLayout=root.findViewById(R.id.shimmer);
        recyclerView= Objects.requireNonNull(root).findViewById(R.id.recycle);
        cartText=root.findViewById(R.id.totalCart);
        root.findViewById(R.id.cart).setOnClickListener(v -> startActivity
                (new Intent(getContext(), CartActivity.class)));
        root.findViewById(R.id.notification).setOnClickListener(v -> {
        });
        root.findViewById(R.id.uploadPrescriptionBt).setOnClickListener(v -> {
            startActivity(new Intent(getContext(),PrescriptionActivity.class));
        });
        root.findViewById(R.id.editSearch).setOnClickListener(v -> {
            startActivity(new Intent(getContext(),SearchActivity.class));
        });
        connectToContentDatabase(root);
        setListeners(root);
        setViewPager();
        int totalCart=getContext().getSharedPreferences("Cart", Context.MODE_PRIVATE).getAll().size();
        if (totalCart>0)
        cartText.setText(String.valueOf(totalCart));
        else cartText.setVisibility(View.GONE);

        return root;
    }

    private void setViewPager() {
        Fragment fragmentClass =new DisplayScreenAdFragment();
        FragmentTransaction fragmentTransaction2;
        fragmentTransaction2= getActivity(). getSupportFragmentManager().beginTransaction();
        fragmentTransaction2.replace(R.id.frame, Objects.requireNonNull(fragmentClass),"HomePage");
        fragmentTransaction2.commit();
//        Bundle bundle=new Bundle();
//        ArrayList<String> images=new ArrayList<>();
//        images.add("https://images.pexels.com/photos/806427/pexels-photo-806427.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940");
//        images.add("https://images.pexels.com/photos/3652097/pexels-photo-3652097.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940");
//        images.add("https://images.pexels.com/photos/208512/pexels-photo-208512.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940");
//        images.add("https://images.pexels.com/photos/593451/pexels-photo-593451.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940");
//        images.add("https://images.pexels.com/photos/606506/pexels-photo-606506.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940");
//        try{Fragment fragmentClass =new ImageSliderFragment();
//            FragmentTransaction fragmentTransaction2;
//            fragmentTransaction2= Objects.requireNonNull(getActivity()). getSupportFragmentManager().beginTransaction();
//            bundle.putStringArrayList("DATA",images);
//            fragmentClass.setArguments(bundle);
//            fragmentTransaction2.replace(R.id.frame, Objects.requireNonNull(fragmentClass));
//            fragmentTransaction2.commit();
//        }catch (Exception ignored){}
    }

    private void setListeners(View root) {
        root.findViewById(R.id.open_medicine).setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });
        root.findViewById(R.id.open_orders).setOnClickListener(v -> {
                startActivity(new Intent(getActivity(), MyOrdersActivity.class));
        });
        root.findViewById(R.id.open_reminders).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), MedicineReminderActivity.class));
        });
        root.findViewById(R.id.open_chat).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), help_activity.class));

        });
        root.findViewById(R.id.open_test_reports).setOnClickListener(v -> {
//            Intent intent=new Intent(getActivity(), ListActivityForImage.class);
            Intent intent=new Intent(getActivity(), TestActivity.class);
            intent.putExtra("FROM","Test");
            getActivity().startActivity(intent);
        });
        root.findViewById(R.id.open_favorites).setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(), CustomMedicineListActivity.class);
            intent.putExtra("FROM","FAV");
            getActivity().startActivity(intent);
        });
        root.findViewById(R.id.open_articale).setOnClickListener(v -> {
//            Intent intent=new Intent(getActivity(), ListActivityForImage.class);
            Intent intent=new Intent(getActivity(), WebArticlesHomeActivity.class);
            intent.putExtra("FROM","Web");
            getActivity().startActivity(intent);
        });
        root.findViewById(R.id.open_prescription).setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(), PrescriptionActivity.class);
            getActivity().startActivity(intent);
        });
        
    }

    private void connectToContentDatabase(View root) {
        FirebaseApp app=new FirebaseCustomAuth().loadCustomFirebase(getContext(),
                "TabletHuts-Extra");
        loadTopCategory(app,root);
        FirebaseDatabase.getInstance(app).getReference("UserScreen").child("HomePage")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<HashMap<String,Object> > data=
                                (ArrayList<HashMap<String, Object>>) dataSnapshot.getValue();
                        if (data!=null)
                            loadContent(data);
                        else {
                            shimmerFrameLayout.stopShimmer();
                            shimmerFrameLayout.setVisibility(View.GONE);
                            //Log.e("Data","Null "+dataSnapshot.getValue());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    private void loadTopCategory(FirebaseApp app, View root) {
        RecyclerView recyclerView= Objects.requireNonNull(root).findViewById(R.id.category_recycler);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        ArrayList<CategoryModel> data=new ArrayList<>();
        CategoryRecycler adapter=new CategoryRecycler(getContext(),data,R.layout.simple_category_layout,HomeFragment.this,"medicine_category");
        recyclerView.setAdapter(adapter);

        Query databaseReference= FirebaseDatabase.getInstance(app).getReference().child("Category").limitToFirst(10);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    CategoryModel model=snapshot.getValue(CategoryModel.class);
                    Objects.requireNonNull(model).setCategory_id(snapshot.getKey());
                    data.add(model);
                }
                CategoryModel model=new CategoryModel();
                model.setIcon("Category");
                model.setCategory_id("All Category");
                model.setCategory_name("All Categories");
                data.add(0,model);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void loadContent(ArrayList<HashMap<String, Object>> data) {
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
        HashMap<String,Object> tempMap=new HashMap<>();
        tempMap.put("type_name","ui_buttons");
        if (data.size()<=2)
            data.add(tempMap);
        else
            data.add(2,tempMap);

        Log.e("loadContent","loaded..."+data);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        HomePageRecycler adapter =new HomePageRecycler(getContext(),data,cartText);
        recyclerView.setAdapter(adapter);
    }
}
