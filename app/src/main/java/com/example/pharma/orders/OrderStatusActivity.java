package com.example.pharma.orders;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pharma.Constants;
import com.example.pharma.HomeActivity;
import com.example.pharma.R;
import com.example.pharma.firebase.FirebaseCustomAuth;
import com.example.pharma.model.OrderModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.pharma.Constants.getWeekName;

public class OrderStatusActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        storeNewOrder();
    }

    private void storeNewOrder() {
        Intent intent=getIntent();
        OrderModel.OrderModelForRealtimeDb forRealtimeDb = (OrderModel.OrderModelForRealtimeDb)
                intent.getSerializableExtra("REALTIME_MODEL");
        long time=System.currentTimeMillis();
        Objects.requireNonNull(forRealtimeDb).setOrder_time(time);
        OrderModel.OrderModelForFirestore forFirestoreDb = (OrderModel.OrderModelForFirestore)
                intent.getSerializableExtra("FIRESTORE_MODEL");
        forRealtimeDb.setOrder_time(time);
        FirebaseApp app=new FirebaseCustomAuth().loadCustomFirebase(this,"tablethuts-orders");
        FirebaseFirestore.getInstance(app).collection("Orders").
                add(Objects.requireNonNull(forFirestoreDb)).addOnCompleteListener(task -> {

                    if (task.isCanceled())
                    {   showErrorMessage(Objects.requireNonNull(task.getException()).getMessage());
                        Snackbar.make(findViewById(R.id.parent),"An error occurred. Please try again after sometime.",Snackbar.LENGTH_LONG).show();
                    }
                    else{
                        String id=  Objects.requireNonNull(task.getResult()).getId();
                        FirebaseDatabase.getInstance(app).getReference("Orders").child(id).setValue(forRealtimeDb).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                findViewById(R.id.progressbar).setVisibility(View.GONE);
                                if (task.isCanceled())
                                {   showErrorMessage(Objects.requireNonNull(task.getException()).getMessage());
                                    Snackbar.make(findViewById(R.id.parent),"An error occurred. Please try again after sometime.",Snackbar.LENGTH_LONG).show();
                                }
                                else
                                {
                                    showSuccessMessage(id, Objects.requireNonNull(forRealtimeDb).getOrder_expected_delivery_time(),forRealtimeDb.getOrder_payment_from());
                                    updateAllMedData(forFirestoreDb.getOrder_medicines());
                                }
                            }
                        });
                    }
                });

    }

    private void updateAllMedData(ArrayList<HashMap<String, Object>> order_medicines) {
            FirebaseApp app=new FirebaseCustomAuth().loadCustomFirebase(this,"tablethuts-medicines");
            ArrayList<String> medidList=new ArrayList<>();
        for(HashMap<String,Object> med:order_medicines)
            medidList.add((String) med.get("medicine_id"));
        //customer med data
        HashMap<String,ArrayList<String>> medHas=new HashMap<>();
        medHas.put("medicine_list",medidList);
        DocumentReference db = FirebaseFirestore.getInstance().collection("Customers")
                .document(Constants.uid);
        db.get().addOnSuccessListener(documentSnapshot -> {
            try {
                if (documentSnapshot.getData()==null)
                    db.set(medHas).addOnCompleteListener(task -> {
                        if (task.isCanceled()) {
                            Log.e("NewOrder", "medicine data not created for user "+Constants.uid+" error " + task.getException());
                            Objects.requireNonNull(task.getException()).getStackTrace();
                        }else
                            Log.e("NewOrder", "medicine data created for user "+Constants.uid);
                    });
                else {
                    for (String medid:medidList)
                        db.update("medicines_list", FieldValue.arrayUnion(medid)).addOnCompleteListener(task -> {
                            if (task.isCanceled()) {
                                Log.e("NewOrder", "medicine data not updated for user " + Constants.uid + " error " + task.getException());
                                Objects.requireNonNull(task.getException()).getStackTrace();
                            } else
                                Log.e("NewOrder", "medicine data updated for user " + Constants.uid);
                        });
                }
            }catch (Exception e){e.printStackTrace();}


        });
        //medicine order data
        SharedPreferences sharedPreferences=getSharedPreferences("MyMedicines",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        for(String medid:medidList){
            editor.putInt(medid,1);
            editor.apply();
                FirebaseDatabase.getInstance(app).getReference("Medicines")
                        .child(medid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long total= (long) dataSnapshot.child("medicine_order").getValue();
                        FirebaseDatabase.getInstance(app).getReference("Medicines").
                                child(medid).child("medicine_order").setValue(total+1).addOnCompleteListener(task -> {
                                    if (task.isCanceled())
                                        Log.e("NewOrder","Increment med order cannot incremented for "+medid+" error"+task.getException().getLocalizedMessage());
                                    else Log.e("NewOrder","Increment med order incremented for "+medid);
                                });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

        //cart gets null
        FirebaseDatabase.getInstance().getReference("Customers")
                .child(Constants.uid).child("Cart").setValue(null).addOnCompleteListener(task -> {
                    if (task.isCanceled())
                        Log.e("NewOrder","Cart cannot get null "+task.getException().getLocalizedMessage());
                    else Log.e("NewOrder","Cart get null ");
                });

    }

    private void showErrorMessage(String error){
        ImageView imageView=findViewById(R.id.order_status_img);
        imageView.setVisibility(View.VISIBLE);
        Glide.with(this).load(R.drawable.ic_error_black_24dp)
                .into(imageView);
        findViewById(R.id.successLayout).setVisibility(View.GONE);
        TextView errorMessage=findViewById(R.id.order_status_message);
        errorMessage.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_error_black_24dp),null,null,null);
        errorMessage.setTextColor(getResources().getColor(R.color.red));
        errorMessage.setText(String.format("An error occurred. Error:%s", error));
    }
    private void showSuccessMessage(String id, long order_expected_delivery_time, String order_payment_from) {
        ImageView imageView=findViewById(R.id.order_status_img);
        imageView.setVisibility(View.VISIBLE);
        Glide.with(this).asGif().load(R.drawable.successimage).into(imageView);
        findViewById(R.id.successLayout).setVisibility(View.VISIBLE);
        TextView succMessage=findViewById(R.id.order_status_message);
        succMessage.setText(R.string.order_created_successfully);
        TextView idText=findViewById(R.id.order_id);
        idText.setText(String.format(Locale.UK,"#%s", id));
        TextView paymentText=findViewById(R.id.order_payment);
        paymentText.setText(order_payment_from);
        Calendar cl= Calendar.getInstance();
        cl.setTimeInMillis(order_expected_delivery_time);
        TextView dayText=findViewById(R.id.date);
        TextView monthAndWeekText=findViewById(R.id.month);
        dayText.setText(String.valueOf(cl.get(Calendar.DAY_OF_MONTH)));
        monthAndWeekText.setText(String.format(Locale.UK,"%s\n%s",Constants.getMonthName (cl.get(Calendar.MONTH) + 1, false), getWeekName(cl.get(Calendar.DAY_OF_WEEK))));
        SharedPreferences preferences=getSharedPreferences("Cart",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.clear();
        editor.apply();
    }


    @Override
    public void onBackPressed() {
        Intent intent1 = new Intent(getApplicationContext(), HomeActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent1);
    }

    public void backToHomeClick(View view) {
        onBackPressed();
    }
}
