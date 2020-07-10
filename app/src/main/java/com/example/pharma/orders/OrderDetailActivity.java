package com.example.pharma.orders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pharma.Constants;
import com.example.pharma.R;
import com.example.pharma.firebase.FirebaseCustomAuth;
import com.example.pharma.model.OrderModel;
import com.example.pharma.recycler.OrderAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import static com.example.pharma.Constants.getFormattedTime;
import static com.example.pharma.Constants.getWeekName;

public class OrderDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        if (Constants.uid==null)
            Constants.uid=  getSharedPreferences("USER_CREDENTIALS",MODE_PRIVATE).getString("UID",null);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("My Order");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        receive();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void receive() {
        Intent intent=getIntent();
        OrderModel.OrderModelForRealtimeDb model= (OrderModel.OrderModelForRealtimeDb) intent.getSerializableExtra("DATA");
        fetchDataFromFirestore(model.getOrder_id(),model);
    }

    private void fetchDataFromFirestore(String order_id, OrderModel.OrderModelForRealtimeDb data) {
        FirebaseApp app=new FirebaseCustomAuth().loadCustomFirebase(this,"tablethuts-orders");
        FirebaseFirestore.getInstance(app).collection("Orders").document(order_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
             try {
                 if (task.isCanceled() || Objects.requireNonNull(task.getResult()).getData()==null){

                 }else{
                     DocumentSnapshot documentSnapshot=task.getResult();
                     ArrayList<HashMap<String,Object>> list= (ArrayList<HashMap<String, Object>>) documentSnapshot.get("order_medicines");
                     OrderAdapter adapter=new OrderAdapter(list,getApplicationContext(),"med_list",R.layout.layout_medicine_list);
                     LinearLayoutManager mLayoutManager =
                             new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                     RecyclerView listView=findViewById(R.id.medicine_list);
                     listView.setLayoutManager(mLayoutManager);
                     listView.setAdapter(adapter);

                     ImageView payMode=findViewById(R.id.paymentMode);
                    try {
                        Glide.with(getApplicationContext()).load(getResources().getDrawable(getDrawable(data.getOrder_payment_from()))).into(payMode);

                        ArrayList<HashMap<String,Object>> status_list= (ArrayList<HashMap<String, Object>>) documentSnapshot.get("order_status");
                        OrderAdapter adapter2=new OrderAdapter(status_list,getApplicationContext(),"med_status",R.layout.order_status_layout);
                        RecyclerView listView2=findViewById(R.id.status_list_view);
                        LinearLayoutManager mLayoutManager2 =
                                new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                        listView2.setLayoutManager(mLayoutManager2);
                        listView2.setAdapter(adapter2);

                        setFirebaseData(data);


                    }catch (Exception e){e.printStackTrace();}
                 }
             }catch (Exception e){e.printStackTrace();}
            }
        });
    }

    private void loadMedDataFromDb(ArrayList<HashMap<String, Object>> list) {
        SimpleAdapter adapter=new SimpleAdapter
                (getApplicationContext(),list,R.layout.layout_medicine_list,
                        new String[]{"medicine_name","medicine_price","medicine_quantity"},
                        new int[]{R.id.medicine_name,R.id.medicine_price,R.id.medicine_quantity});
        ListView listView=findViewById(R.id.medicine_list);
        listView.setAdapter(adapter);
        FirebaseApp app=new FirebaseCustomAuth().loadCustomFirebase(this,"tablethuts-medicines");
        for (int i=0;i<list.size();i++) {
            int finalI = i;
            FirebaseDatabase.getInstance(app).getReference("Medicines").child((String) list.get(i).get("medicine_id")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    HashMap<String,Object> map=list.get(finalI);
                    map.put("medicine_name",dataSnapshot.child("medicine_name").getValue());
                    adapter.notifyDataSetChanged();
                        findViewById(R.id.progressbar).setVisibility(View.GONE);
                        findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public int getDrawable(String orderPaymentMode) {
        switch (orderPaymentMode){
            case "Cash On Delivery":return R.drawable.ic_cod;
            case "Paytm":return R.drawable.ic_paytm;
            case "PhonePay":return R.drawable.ic_phonepe;
            case "AmazonPay":return R.drawable.ic_amazonpay;
            case "GooglePay":return R.drawable.ic_googlepay;
            default:return R.drawable.ic_credit_card_black_24dp;
        }
    }
    void setFirebaseData(OrderModel.OrderModelForRealtimeDb data){
     TextView ordernoText=findViewById(R.id.orderno);
     TextView orderTimeText=findViewById(R.id.time);
     ordernoText.setText("#"+data.getOrder_id());
     orderTimeText.setText(getFormattedTime(data.getOrder_time()));
     TextView totalText=findViewById(R.id.cartamount);
     TextView discountText=findViewById(R.id.total_saving);
     TextView deliveryChargeText=findViewById(R.id.delivery_charge);
     HashMap<String,Object> amountStat=data.getOrder_amount_stats();
     TextView finalTotalText=findViewById(R.id.total_final);
     long total_amount= (long) amountStat.get("total_amount");
     long sub_total_amount= (long) amountStat.get("sub_total");
     long delivery_charge= (long) amountStat.get("delivery_charge");
        Log.e("SubTotal","S "+sub_total_amount);
     finalTotalText.setText(Constants.getFormatedAmount(total_amount ));
     deliveryChargeText.setText(Constants.getFormatedAmount(delivery_charge));
     totalText.setText(Constants.getFormatedAmount(sub_total_amount));
     discountText.setText(Constants.getFormatedAmount(sub_total_amount-
             (total_amount -delivery_charge)));

     TextView textView=findViewById(R.id.address);
     textView.setText(String.format("%s \nLandMark: %s",
             data.getOrder_delivery_address_text(), data.getOrder_delivery_address_landmark()));

        Calendar cl= Calendar.getInstance();
        TextView dayText=findViewById(R.id.date);
        TextView monthAndWeekText=findViewById(R.id.month);
     if( data.getCurrent_order_status().contains("Complete")){
         cl.setTimeInMillis(data.getOrder_time());
         dayText.setText(String.valueOf(cl.get(Calendar.DAY_OF_MONTH)));
         monthAndWeekText.setText(String.format(Locale.UK,"%s\n%s",Constants.getMonthName (cl.get(Calendar.MONTH) + 1, false), getWeekName(cl.get(Calendar.DAY_OF_WEEK))));
         TextView timeTitle=findViewById(R.id.expecteddelivery);
         timeTitle.setText(R.string.delivery_at);
         TextView timeText=findViewById(R.id.deliveryToday);
         timeText.setText(String.format(Locale.UK,"%d:%d %d",
                 Calendar.HOUR, Calendar.MINUTE, Calendar.AM_PM));
     }else{
         cl.setTimeInMillis(data.getOrder_expected_delivery_time());
         dayText.setText(String.valueOf(cl.get(Calendar.DAY_OF_MONTH)));
         monthAndWeekText.setText(String.format(Locale.UK,"%s\n%s",Constants.getMonthName (cl.get(Calendar.MONTH) + 1, false), getWeekName(cl.get(Calendar.DAY_OF_WEEK))));
     }
        findViewById(R.id.progressbar).setVisibility(View.GONE);
        findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
    }

    public void needHelpClicked(View view) {

    }
}
