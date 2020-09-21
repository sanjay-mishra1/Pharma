package com.example.pharma;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.pharma.address.MyAddressFragment;
import com.example.pharma.firebase.FirebaseCustomAuth;
import com.example.pharma.model.OrderModel;
import com.example.pharma.recycler.SimpleRecycler;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CartActivity extends AppCompatActivity {
    static SharedPreferences sharedPreferences;
    static TextView addressText;
    private SharedPreferences sharedCart;
    private ArrayList<HashMap<String, Object>> medData;
    private long totalAmount;
    private int subTotal;
    private int charge;
    private ShimmerFrameLayout shimmer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        if (Constants.uid==null)
            Constants.uid=  getSharedPreferences("USER_CREDENTIALS",MODE_PRIVATE).getString("UID",null);
        sharedPreferences=getSharedPreferences("Address",MODE_PRIVATE);
        shimmer = findViewById(R.id.shimmer);
        addressText = findViewById(R.id.address);
        sharedCart=getSharedPreferences("Cart",MODE_PRIVATE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        loadDataFromRealtimeDB();
    }

    private void loadCartChangeListener() {
        TextView total=findViewById(R.id.total_amount);
        TextView finaltotal=findViewById(R.id.total_amount_final);
        TextView saved=findViewById(R.id.order_discount);
        TextView chargeText=findViewById(R.id.order_delivery_charge);
        charge=2;
        chargeText.setText("₹ "+charge);
        calculateTotal(total,finaltotal,saved,false);
        sharedCart.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            calculateTotal(total,finaltotal,saved,true);
        });
    }


    void calculateTotal(TextView total, TextView finaltotal, TextView saved,boolean loadEmptyCart){
        int cal=0;
        int originalTotal=0;
        if (medData.size()>0){
        for(HashMap<String,Object> data:medData) {
            {
                cal += (long) data.get("medicine_price") * sharedCart
                        .getInt((String) data.get("medicine_id"), 1);
                originalTotal += (long) data.get("medicine_original_price") * sharedCart
                        .getInt((String) data.get("medicine_id"), 1);
                ;
            }
        }
            subTotal=originalTotal;
            total.setText(String.format(Locale.UK,"₹ %d", subTotal));

            saved.setText(String.format(Locale.UK,"₹ %d", originalTotal - (cal)));
            totalAmount=cal + charge;
            finaltotal.setText(String.format(Locale.UK,"₹ %d", totalAmount));
        }else{
            if (loadEmptyCart)
            loadEmptyCartUI("Calculate Total");
        }

    }
    private void loadDataFromRealtimeDB() {
        if (Constants.uid==null)
            Constants.uid=  getSharedPreferences("USER_CREDENTIALS",MODE_PRIVATE).getString("UID",null);
        Log.e("Cart","->"+sharedCart.getAll());
        if ( sharedCart.getAll().size()>0 ){
            loadDataFromLocal(sharedCart);
        }else {
            FirebaseDatabase.getInstance().getReference("Customers").child(Constants.uid)
                    .child("Cart").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    HashMap<String, Long> data = (HashMap<String, Long>) dataSnapshot.getValue();
                    if (dataSnapshot.hasChildren() && data!=null ) {
                        try{
                            getMedicineData(new ArrayList<>(data.keySet()), new ArrayList<>(data.values()), true);
                        }catch (Exception e){e.printStackTrace();}
                    }
                    else loadEmptyCartUI("LoadCartData From Realtime");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void loadEmptyCartUI(String from) {
        ShimmerFrameLayout shimmer = findViewById(R.id.shimmer);
        shimmer.stopShimmer();
        shimmer.setVisibility(View.GONE);
        findViewById(R.id.amount_view).setVisibility(View.GONE);
        findViewById(R.id.checkoutBt).setVisibility(View.GONE);
        findViewById(R.id.empty_screen).setVisibility(View.VISIBLE);
    }

    private void loadDataFromLocal(SharedPreferences sharedPreferences) {
        Log.e("LocalCart","Data from local");
        getMedicineData(new ArrayList<>(sharedPreferences.getAll().keySet()),
                new ArrayList<>((Collection<? extends Long>) sharedPreferences.getAll().values()),
                false);

    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (medData!=null)
            loadCartChangeListener();
        }catch (Exception ignored){}
      SharedPreferences sharedPreferences=getSharedPreferences("Address",MODE_PRIVATE);
      String address=  sharedPreferences.getString("Address_Text","");
        if (!address.equals("")) {
            TextView textView = findViewById(R.id.address);
            textView.setVisibility(View.VISIBLE);
            textView.setText(address);
        }
    }

    private void getMedicineData(ArrayList<String> medicines, ArrayList<Long> medQuant, boolean storeToLocal) {
        RecyclerView recyclerView= findViewById(R.id.recycle);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
         medData=new ArrayList<>();
        SimpleRecycler adapter=new SimpleRecycler(medData,R.layout.linear_card,getApplicationContext(),"cart", null);
        recyclerView.setAdapter(adapter);
            SharedPreferences preferences=getSharedPreferences("Cart",MODE_PRIVATE);
            SharedPreferences.Editor edit=preferences.edit();
        FirebaseApp app= new FirebaseCustomAuth().loadCustomFirebase(this,"tablethuts-medicines");
        final int[] count = {0};
        for(String id:medicines) {
            if (storeToLocal) {
                edit.putInt(id, 1);
                edit.apply();
            }
//            if (!id.equalsIgnoreCase("cartloaded")) {
                FirebaseDatabase.getInstance(app).getReference("Medicines").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        HashMap<String, Object> map = new HashMap<>();
                        Log.e("Med","->"+dataSnapshot.getValue());
                        try {
                            HashMap<String, Object> dataSnapshotValue = (HashMap<String, Object>) dataSnapshot.getValue();
                            map.put("medicine_img", dataSnapshotValue != null ? ((ArrayList<String>) dataSnapshotValue.get("medicine_images")).get(0) : "NA");
                            map.put("medicine_name", dataSnapshotValue != null ? dataSnapshotValue.get("medicine_name") : "NA");
                            map.put("medicine_id", id);
                            map.put("medicine_price", dataSnapshotValue != null ?
                                    ((HashMap<String,Object>) ((ArrayList<Object>)
                                            dataSnapshotValue.get("medicine_variant")).get(0))
                                            .get("medicine_price") : "NA");
                            map.put("medicine_original_price", ((HashMap<String,Object>) ((ArrayList<Object>)
                                    dataSnapshotValue.get("medicine_variant")).get(0)).get("medicine_original_price"));
                            map.put("medicine_max_quantity", dataSnapshotValue != null ? dataSnapshotValue.get("medicine_max_quantity") : "NA");
                            map.put("medicine_quantity", medQuant.get(count[0]));
                            count[0]++;
                            medData.add(map);
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }finally {
                            if (count[0]==medicines.size())
                            { try {
                                loadCartChangeListener();
                                shimmer.stopShimmer();
                                shimmer.setVisibility(View.GONE);
                                findViewById(R.id.amount_view).setVisibility(View.VISIBLE);
                                findViewById(R.id.recycle).setVisibility(View.VISIBLE);
                                findViewById(R.id.checkoutBt).setVisibility(View.VISIBLE);

                            }catch (Exception e){e.printStackTrace();
                                loadEmptyCartUI("GetMedData");
                            }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void selectAddressClicked(View view) {
        MyAddressFragment fragment = new MyAddressFragment();
        fragment.showNow(Objects.requireNonNull(this).getSupportFragmentManager(), "Creating New Category");
    }
    static void setPreference(){
        String address=  sharedPreferences.getString("Address_Text","");
        if (!address.equals("")) {
            addressText.setVisibility(View.VISIBLE);
            addressText.setText(address);
        }
    }
    public static void detectDismissDialog(){
        setPreference();
    }

    @Override
    public void onBackPressed() {
        addressText=null;
        super.onBackPressed();
    }

    public void checkoutButtonClicked(View view) {
        String[] address=sharedPreferences.getString("Address_Text","").split("Landmark");
    try {
        if (address.length==2) {
            HashMap<String,Object> map=new HashMap<>();
            map.put("total_amount",totalAmount);
            map.put("sub_total",subTotal);
            map.put("delivery_charge",charge);
        OrderModel.OrderModelForRealtimeDb forRealtimeDb = new OrderModel.OrderModelForRealtimeDb(
                map,
                0, "Pending", address[0],
                sharedPreferences.getString("Address_Coordinate", ""),
                address[1], null,null,
                0
        );
        ArrayList<HashMap<String,Object>> med=new ArrayList<>();
        for (HashMap<String,Object> data:medData)
        {   HashMap<String,Object> temp=new HashMap<>();
            temp.put("medicine_price",data.get("medicine_price"));
            temp.put("medicine_id",data.get("medicine_id"));
            temp.put("medicine_name",data.get("medicine_name"));
            int quant= (sharedCart.
                    getInt((String) data.get("medicine_id"),1));
            temp.put("medicine_quantity",quant);
            med.add(temp);
        }
        OrderModel.OrderModelForFirestore forFirestore = new OrderModel.OrderModelForFirestore(
                "Pending",
                null,
                null,
                med,
                null,
                0

        );
        SelectSlotFragment fragment = new SelectSlotFragment();
        Bundle bundle=new Bundle();
        bundle.putSerializable("REALTIME_MODEL",forRealtimeDb);
        bundle.putSerializable("FIRESTORE_MODEL",forFirestore);
        fragment.setArguments(bundle);
        fragment.showNow(Objects.requireNonNull(this).getSupportFragmentManager(), "Creating New Category");
    }
}catch (Exception e){e.printStackTrace();}
    }
}
