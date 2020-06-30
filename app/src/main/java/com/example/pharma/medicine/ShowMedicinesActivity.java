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
import android.widget.Toast;

import com.example.pharma.R;
import com.example.pharma.firebase.FirebaseCustomAuth;
import com.example.pharma.model.MedicineModel;
import com.example.pharma.recycler.RecyclerUI;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

public class ShowMedicinesActivity extends AppCompatActivity {
    private  long MIN_PRICE =0 ;
    private  long MAX_PRICE =100000 ;
    private ArrayList<MedicineModel> list;
    private RecyclerUI.FilterAdapter adapter;
    private Query fireQuery;
    private HashMap<String,Object> filterData;
    private String category;
    private TextView sortText;
    private String key;
    private String value;
    private DocumentSnapshot queryDocumentSnap;
    private boolean isDBReachedLast=false;
    private boolean isDbBusyLoading=false;
    private View noMedFoundView;
    private TextView totalFilterApplied;
    private Dialog dialog;
    private AtomicBoolean set;
    private FirebaseApp app;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_medicines);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        list = new ArrayList<>();
        sortText=findViewById(R.id.sortValue);
        totalFilterApplied=findViewById(R.id.filterValue);
        noMedFoundView=findViewById(R.id.no_med_view);
        receive();
        loadCartInfo();
        findViewById(R.id.filter).setOnClickListener(v -> {
            Intent intent = new Intent(this, AllMedicineSortingActivity.class);
            intent.putExtra("min_price", MIN_PRICE);
            intent.putExtra("max_price", MAX_PRICE);
            intent.putExtra("key", key);
            intent.putExtra("value", value);
            intent.putExtra("DATA", filterData);
            startActivityForResult(intent, 101);
        });
        findViewById(R.id.sort).setOnClickListener(v -> loadSortDialog());
        loadRecyclerListener();
        app= new FirebaseCustomAuth().loadCustomFirebase(this,"tablethuts-medicines");
        FirebaseFirestore firedb = FirebaseFirestore.getInstance(app);

        try {
            Log.e("Key","->"+key+" ->"+value);
            fireQuery = firedb.collection("Medicines")
            //.whereGreaterThanOrEqualTo("medicine_price", 0)
            //.whereLessThanOrEqualTo("medicine_price", 100000)
            ;
            Query queryPrice= FirebaseFirestore.getInstance(app).collection("Medicines");
//           filterData.put("medicine_price",new long[2]);
            if (key.contains("disease"))
            {
                fireQuery=fireQuery.whereArrayContains("medicine_disease",value);
                queryPrice=queryPrice.whereArrayContains("medicine_disease",value);
            }
            else {
                fireQuery=fireQuery.whereEqualTo(key,value)
                        .whereArrayContains("medicine_disease", "All")
                ;
                Log.e("Inside","not disease "+key+"->"+value);
                queryPrice= queryPrice.whereEqualTo(key,value);
            }
            getPriceFromDb(queryPrice.orderBy("medicine_price", Query.Direction.DESCENDING)
                    .limit(1),false);
            getPriceFromDb(queryPrice.orderBy("medicine_price", Query.Direction.ASCENDING).limit(1),
                    true);
        }catch (Exception e){
            e.printStackTrace();
            return;
        }
        firebase(true);
    }

    private void loadCartInfo() {
        int totalCart=getSharedPreferences("Cart",MODE_PRIVATE).getAll().size();
        TextView cartText=findViewById(R.id.totalCart);
        cartText.setText(String.valueOf(totalCart));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void receive() {
        Intent intent=getIntent();
        if (intent != null) {
            category=intent.getStringExtra("DATA");
            value=intent.getStringExtra("value");
            key =intent.getStringExtra("key");
            String name =intent.getStringExtra("key");
            String title=intent.getStringExtra("name");
            if (name!=null)
                setTitle(title);
            else setTitle("Medicines");
        }else {
            Toast.makeText(this,"Error occurred",Toast.LENGTH_LONG).show();
            onBackPressed();
        }

        filterData = new HashMap<>();

    }

    private void loadRecyclerListener(){
        RecyclerView recyclerView = findViewById(R.id.recycle);
        adapter=new RecyclerUI.FilterAdapter(list,this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new RecyclerUI.GridSpacingItemDecoration(2,0, true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)
                        && newState==RecyclerView.SCROLL_STATE_IDLE &&
                        !isDBReachedLast&& !isDbBusyLoading){
                    Log.e("Scroll","Detected->"+newState);
                    firebase(false);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });
    }
    private void getPriceFromDb(Query query, boolean isMin){
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            try {
                List<DocumentSnapshot> result = queryDocumentSnapshots.getDocuments();
                Log.e("ReceivedPrices","->"+result);
//                Log.e("price","->"+result.get(0).getLong("medicine_price"));
                if (!queryDocumentSnapshots.isEmpty()) {
//                    long[] prices= (long[]) filterData.get("medicine_price");
                    if (isMin)
                    {
//                        prices[0]=result.get(0).getLong("medicine_price");;
                        MIN_PRICE = result.get(0).getLong("medicine_price");
                    }
                    else{
//                        prices[1]=result.get(0).getLong("medicine_price");;
                        MAX_PRICE = result.get(0).getLong("medicine_price");
                    }
//                    filterData.put("medicine_price",prices);
                }
            }catch (Exception e){e.printStackTrace();};
        });
    }
    private void firebase(boolean start) {
        noMedFoundView.setVisibility(View.GONE);
        Log.e("Query","requesting...");
        isDbBusyLoading=true;
        if (fireQuery != null && !start) {
            fireQuery=   FirebaseFirestore.getInstance(app).collection("Medicines")//.orderBy("medicine_price")
                    .whereEqualTo("medicine_category",value)
                    .startAfter(queryDocumentSnap).limit(6);
        }else fireQuery=  Objects.requireNonNull(fireQuery).limit(8);
        findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
        fireQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
            try {findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
                if (queryDocumentSnapshots.isEmpty())
                { isDBReachedLast=true;
                    Log.e("Result","No Query Results");
                    if (list.size()==0)
                        noMedFoundView.setVisibility(View.VISIBLE);
                }
                else for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    {
                        MedicineModel model = document.toObject(MedicineModel.class);
                        if (model != null) {
                            model.setMedicine_id(document.getId());
                            list.add(model);
                            queryDocumentSnap=document;
                        }
                    }
                    Log.e("Model", "=>" + list.toString());
//                new RecyclerUI(this, recyclerView, list);
                    isDbBusyLoading=false;
                    adapter.notifyDataSetChanged();
                }
            }catch (Exception e){e.getMessage();isDbBusyLoading=false;}
        }).addOnFailureListener(e -> Log.e("Result","Failed: No Query Results "+e.getMessage())).addOnCanceledListener(() -> Log.e("Result","Canceled: No Query Results "));

    }

    private void loadSortDialog() {
        if (dialog==null) {
            final View dialogView = View.inflate(this, R.layout.sorting_optionsxml, null);
            dialog = new Dialog(this, R.style.Dialog1);
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
            set = new AtomicBoolean(false);
            dialogView.findViewById(R.id.relevance).setOnClickListener(v -> {
                set.set(true);
                isDBReachedLast = true;
                fireQuery = FirebaseFirestore.getInstance(app)
                        .collection("Medicines");
                sortText.setText(R.string.relevance);
                dialog.dismiss();
            });
            if (filterData.isEmpty()) {
                attachDiscountListener();
                attachMedicineNameListener();
            } else {
                dialogView.findViewById(R.id.discount).setVisibility(View.GONE);
                dialogView.findViewById(R.id.medicineName).setVisibility(View.GONE);
            }

            dialogView.findViewById(R.id.price_low_high).setOnClickListener(v -> {
                isDBReachedLast = true;
                set.set(true);
                fireQuery = FirebaseFirestore.getInstance(app).collection("Medicines")
                        .orderBy("medicine_price", Query.Direction.ASCENDING);
                sortText.setText(R.string.price_low_to_high);
                dialog.dismiss();
            });
            dialogView.findViewById(R.id.price_high_low).setOnClickListener(v -> {
                isDBReachedLast = true;
                fireQuery = FirebaseFirestore.getInstance(app).collection("Medicines")
                        .orderBy("medicine_price", Query.Direction.DESCENDING);
                set.set(true);
                sortText.setText(R.string.price_high_to_low);
                dialog.dismiss();
            });
            dialog.setOnDismissListener(dialog1 -> {
                if (set.get()) {
                    if (key.contains("disease"))
                        fireQuery = fireQuery.whereArrayContains(key, value);
                    else
                        fireQuery = fireQuery.whereEqualTo(key, value);
                    set.set(false);
                    list.clear();
                    adapter.notifyDataSetChanged();
                    if (filterData.isEmpty())
                        firebase(true);
                    else
                        makeQuery(filterData, fireQuery);
                }
            });
            dialog.show();
        }else{
            if (!filterData.isEmpty()){
                dialog.findViewById(R.id.discount).setVisibility(View.GONE);
                dialog.findViewById(R.id.medicineName).setVisibility(View.GONE);
            }else{
                dialog.findViewById(R.id.discount).setVisibility(View.VISIBLE);
                dialog.findViewById(R.id.medicineName).setVisibility(View.VISIBLE);
                if (!dialog.findViewById(R.id.discount).hasOnClickListeners())
                    attachDiscountListener();
                if (dialog.findViewById(R.id.medicineName).hasOnClickListeners())
                    attachMedicineNameListener();
            }
            dialog.show();
        }
    }

    private void attachMedicineNameListener() {
        dialog.findViewById(R.id.medicineName).setOnClickListener(v -> {
            set.set(true);
            isDBReachedLast = true;
            fireQuery = FirebaseFirestore.getInstance(app).collection("Medicines")
                    .orderBy("medicine_name", Query.Direction.ASCENDING);

            sortText.setText(R.string.medicine_name);
            dialog.dismiss();
        });
    }

    private void attachDiscountListener() {
        dialog.findViewById(R.id.discount).setOnClickListener(v -> {
            fireQuery = FirebaseFirestore.getInstance(app).collection("Medicines")
                    .orderBy("medicine_discount", Query.Direction.DESCENDING).whereEqualTo(key, value);
            set.set(true);
            sortText.setText(R.string.discount);
            dialog.dismiss();
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==101 && data!=null){
//            if (!filterData.equals(data.getStringArrayListExtra("DATA")))
//            filterData=data.getStringArrayListExtra("DATA");
            //setFilterDataQuery();
            try {
                filterData=(HashMap<String, Object>) data.getSerializableExtra("DATA");
                Log.e("Received","->"+filterData);
                //fireQuery=
                makeQuery(Objects.requireNonNull(filterData),null);

            }catch (Exception e){e.printStackTrace();}
        }
    }

    private void makeQuery(HashMap<String, Object> queryData,Query query){
        try {
            if (query == null)
                if (key.contains("disease"))
                    fireQuery = FirebaseFirestore.getInstance(app).collection("Medicines")
                            .whereArrayContains(key, value);
                else
                    fireQuery = FirebaseFirestore.getInstance(app).collection("Medicines")
                            .whereEqualTo(key, value);
            else fireQuery = query;
            int totalField = queryData.size();
            if (totalField == 0)
                totalFilterApplied.setVisibility(View.GONE);
            else {
                totalFilterApplied.setVisibility(View.VISIBLE);
            }
            for (String field : queryData.keySet()) {
                Log.e("Field", "->" + field);
                switch (field) {
                    case "medicine_company":
                        Log.e("Field", "Company->" + Objects.requireNonNull(queryData.get("medicine_company")));
                        if (key.contains("category"))
                            fireQuery = fireQuery.whereIn("medicine_company", (List<?>) Objects.requireNonNull(queryData.get("medicine_company")));
                        else fireQuery=fireQuery.whereEqualTo("medicine_company",queryData.get("medicine_company"));
                        break;
                    case "medicine_price":
                        long[] values = (long[]) queryData.get("medicine_price");
                        if (values!=null&& values[0]==MIN_PRICE&& values[1]==MAX_PRICE)
                            totalField--;
                        Log.e("Field", "Price->" + Arrays.toString(values));
                        fireQuery = fireQuery.whereGreaterThanOrEqualTo("medicine_price", Objects.requireNonNull(values)[0]).whereLessThanOrEqualTo("medicine_price", values[1]);
                        break;
                    case "medicine_type":
                        Log.e("Field", "Type->" + queryData.get("medicine_type"));
                        if (key.contains("disease"))
                            fireQuery = fireQuery.whereIn("medicine_type", (List<?>) Objects.requireNonNull(queryData.get("medicine_type")));
                        else
                            fireQuery = fireQuery.whereEqualTo("medicine_type", queryData.get("medicine_type"));
                        break;
                    case "medicine_category":
                        fireQuery = fireQuery.whereEqualTo("medicine_category", queryData.get("medicine_category"));
                        break;
                    case "prescription_needed":
                        Log.e("Field", "Pres->" + queryData.get("prescription_needed"));
                        fireQuery = fireQuery.whereEqualTo("prescription_needed",
                                Boolean.parseBoolean((String) queryData.get("prescription_needed")));
                        break;
                    case "medicine_disease":
                        Log.e("Field", "Disease->" + queryData.get("medicine_disease"));
                        if (key.contains("company"))
                            fireQuery = fireQuery.whereArrayContainsAny("medicine_disease",
                                    (List<?>) Objects.requireNonNull(queryData.get("medicine_disease")));
                        else {String disease= (String) queryData.get("medicine_disease");
                            if (Objects.requireNonNull(disease).equals("All"))
                                totalField--;
                            fireQuery = fireQuery.whereArrayContains("medicine_disease", disease);
                        }
                        break;
                }
            }
            totalFilterApplied.setText(String.valueOf(totalField));
            list.clear();
            adapter.notifyDataSetChanged();
            firebase(true);
            sortText.setText(R.string.relevance);
        }catch (Exception e){e.printStackTrace();}
    }

}
