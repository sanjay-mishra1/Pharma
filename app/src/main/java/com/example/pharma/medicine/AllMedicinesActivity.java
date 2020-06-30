package com.example.pharma.medicine;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.pharma.CartActivity;
import com.example.pharma.GridSpacingItemDecoration;
import com.example.pharma.R;
import com.example.pharma.firebase.FirebaseCustomAuth;
import com.example.pharma.model.MedicineModel;
import com.example.pharma.recycler.RecyclerUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AllMedicinesActivity extends AppCompatActivity {
    private ArrayList<MedicineModel> list;
    private RecyclerUI.FilterAdapter adapter;
    private Query fireQuery;
    private List<String> filterData;
    private String category;
    private FirebaseApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_medicines);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        int totalCart=getSharedPreferences("Cart",MODE_PRIVATE).getAll().size();
        TextView cartText=findViewById(R.id.totalCart);
        cartText.setText(String.valueOf(totalCart));
        findViewById(R.id.cart).setOnClickListener(v -> startActivity(new Intent(AllMedicinesActivity.this, CartActivity.class)));
        receive();
        loadView();
    }

    private void loadView() {
        list = new ArrayList<>();
        filterData = new ArrayList<>();
        findViewById(R.id.filter).setOnClickListener(v -> {
//            Intent intent=new Intent(getApplicationContext(),AllMedicineSortingActivity.class);
//            intent.putExtra("DATA",new ArrayList<String>());
//            startActivityForResult(intent,101);
        });
        findViewById(R.id.sort).setOnClickListener(v -> loadSortDialog());
        RecyclerView recyclerView = findViewById(R.id.recycle);
        adapter=new RecyclerUI.FilterAdapter(list,this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2,0, true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        app= new FirebaseCustomAuth().loadCustomFirebase(this,"tablethuts-medicines");
        FirebaseFirestore firedb = FirebaseFirestore.getInstance(app);
        fireQuery = firedb.collection("Medicines")
                .whereEqualTo("medicine_category",category);
//        fireQuery = firedb.collection("Medicines");//.orderBy("medicine_category")
//                //.whereEqualTo("medicine_category",category);
        firebase(true);
    }
    private void firebase(boolean start) {
        if (fireQuery != null && !start) {
            fireQuery.startAfter(fireQuery);
        }
        Log.e("All Medicine","Inside firebase");
        //Objects.requireNonNull(fireQuery).limit(2);
        fireQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.e("All Medicine","Inside success "+task.getResult().getDocuments());
            }
        }).addOnSuccessListener(queryDocumentSnapshots -> {
            Log.e("All Medicine","Inside success");
            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                {   Log.e("All Medicine","=>"+document.get("medicine_name"));
                    MedicineModel model = document.toObject(MedicineModel.class);
                    if (model != null) {
                        model.setMedicine_id(document.getId());
                        list.add(model);
                    }
                }
                Log.e("Model", "=>" + list.toString());
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(e -> Log.e("Firestore","Failed "+e.getMessage()));

    }
    private void loadSortDialog() {
        final View dialogView = View.inflate(this, R.layout.sorting_optionsxml, null);
        final Dialog dialog = new Dialog(this, R.style.Dialog1);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        lp.windowAnimations = R.style.DialogAnimation;
        Objects.requireNonNull(dialog.getWindow()).setAttributes(lp);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(dialogView);
        dialog.setOnKeyListener((dialogInterface, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_BACK) {
                dialog.dismiss();

                return true;
            }

            return false;
        });
        AtomicBoolean set= new AtomicBoolean(false);
        dialogView.findViewById(R.id.medicineName).setOnClickListener(v -> {
            set.set(true);
            fireQuery=FirebaseFirestore.getInstance(app).collection("Medicines");
            dialog.dismiss();
        });

        dialogView.findViewById(R.id.medicineName).setOnClickListener(v -> {
            set.set(true);
            fireQuery=FirebaseFirestore.getInstance(app).collection("Medicines").orderBy("medicine_name", Query.Direction.ASCENDING);

            dialog.dismiss();
        });
        dialogView.findViewById(R.id.price_low_high).setOnClickListener(v -> {
            set.set(true);
            fireQuery= FirebaseFirestore.getInstance(app).collection("Medicines").orderBy("medicine_price", Query.Direction.ASCENDING);

            dialog.dismiss();
        });

        dialogView.findViewById(R.id.price_high_low).setOnClickListener(v -> {
            fireQuery=FirebaseFirestore.getInstance(app).collection("Medicines").orderBy("medicine_price", Query.Direction.DESCENDING);
            set.set(true);
            dialog.dismiss();
        });

        dialogView.findViewById(R.id.discount).setOnClickListener(v -> {
            fireQuery= FirebaseFirestore.getInstance(app).collection("Medicines").orderBy("price_discount", Query.Direction.ASCENDING);
            set.set(true);
            dialog.dismiss();
        });
        dialog.setOnDismissListener(dialog1 -> {

            if (set.get())
            {
                setFilterDataQuery();

            }
        });
        dialog.show();
    }
    private void setFilterDataQuery(){
        //0 key
        //1 equality type( equal,between,more)
        //2 value if between split with comma
        for(String data:filterData){
            String[] temp=data.split("@/data/@");
            String key=temp[0];//topic name like company name, price,discount etc;
            String equality=temp[1];
            String value=temp[2];// might be multivalued split with @/value/@
            if (key.contains("presc")){
                if (value.contains("true"))
                    fireQuery.whereEqualTo(key,true);
                else fireQuery.whereEqualTo(key,false);
            }else
            if (equality.contains("equal"))
            {
                String[] all =value.split("@/value/@");
                for(String x:all)
                    fireQuery.whereEqualTo(key,x);
            }else
            if (equality.contains("more"))
                fireQuery.whereGreaterThanOrEqualTo(key, value);
            else if (equality.contains("between")){
                String[] all =value.split("@/value/@");
                fireQuery.whereGreaterThan(key,all[0]).whereLessThan(key,all[1]);
            }

        }
        list.clear();
        adapter.notifyDataSetChanged();
        firebase(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==101 && data!=null){
            if (!filterData.equals(data.getStringArrayListExtra("DATA")))
                filterData=data.getStringArrayListExtra("DATA");
            setFilterDataQuery();
        }
    }


    private void receive() {
        Intent intent=getIntent();
        category= intent.getStringExtra("DATA_ID");
        setTitle(intent.getStringExtra("DATA_NAME"));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
