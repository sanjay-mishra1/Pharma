package com.example.pharma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.pharma.firebase.FirebaseCustomAuth;
import com.example.pharma.model.OrderModel;
import com.example.pharma.orders.OrderStatusActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class PaymentMethodActivity extends AppCompatActivity {

    private TextView textPayTitle;
    private TextView textPayMode;
    String mode="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        textPayTitle=findViewById(R.id.pay_mode_title);
        textPayMode=findViewById(R.id.pay_mode);
        Intent intent=getIntent();
        TextView amountText=findViewById(R.id.totalAmount);
        amountText.setText(Constants.getFormatedAmount(intent.getLongExtra("TOTAL_AMOUNT",0)));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void placeOrderOnClick(View view) {
        if (mode.isEmpty()){
            Snackbar.make(findViewById(R.id.parent),"Please select payment mode",Snackbar.LENGTH_LONG).show();
            return;
        }
       try {
           Intent intent = getIntent();
           OrderModel.OrderModelForRealtimeDb forRealtimeDb = (OrderModel.OrderModelForRealtimeDb)
                   intent.getSerializableExtra("REALTIME_MODEL");
           Objects.requireNonNull(forRealtimeDb).setOrder_payment_from(mode);
           OrderModel.OrderModelForFirestore forFirestoreDb = (OrderModel.OrderModelForFirestore)
                   intent.getSerializableExtra("FIRESTORE_MODEL");
           Log.e("datareceived", "=>" + forRealtimeDb.getOrder_delivery_address_text() + " " +
                   forFirestoreDb.getOrder_medicines());
           HashMap<String, String> payStat = new HashMap<>();
//           payStat.put(String.valueOf(System.currentTimeMillis()), mode);
//           forFirestoreDb.setOrder_payment_stats(payStat);
       HashMap<String,Object> paymentStats=new HashMap<>();
       paymentStats.put("order_payment_mode",mode);
       paymentStats.put("order_payment_ref_id",null);
       paymentStats.put("order_payment_time",null);
       forFirestoreDb.setOrder_payment_stats(paymentStats);
           Intent intent2 = new Intent(getApplicationContext(), OrderStatusActivity.class);
           intent2.putExtra("REALTIME_MODEL", forRealtimeDb);
           intent2.putExtra("FIRESTORE_MODEL", forFirestoreDb);
           startActivity(intent2);
       }catch (Exception e){e.printStackTrace();}



    }

    public void netBankingOnClick(View view) {
        setDrawableAndText(R.string.netbanking,
                R.drawable.ic_netbanking);

    }

    public void creditCardOnClick(View view) {
        setDrawableAndText(R.string.credit_debit_card,
                R.drawable.ic_credit_card_black_24dp);
    }

    public void googlePayOnClick(View view) {
        setDrawableAndText(R.string.googlePay,
                R.drawable.ic_googlepay);
    }

    public void amazonPayOnClick(View view) {
        setDrawableAndText(R.string.amazonPay,
                R.drawable.ic_amazonpay);
    }

    private void setDrawableAndText(int text, int drawable) {
        mode=getString(text);
        textPayTitle.setText(R.string.pay_using);
        textPayTitle.setCompoundDrawablesWithIntrinsicBounds(
                getResources().getDrawable(drawable),null,null,null);
        textPayTitle.setVisibility(View.VISIBLE);
        textPayMode.setText(text);

    }

    public void phonepayOnClick(View view) {
        setDrawableAndText(R.string.phonepay,
                R.drawable.ic_phonepe);

    }

    public void paytmOnClick(View view) {
        setDrawableAndText(R.string.paytm,
                R.drawable.ic_paytm);

    }

    public void codOnClick(View view) {
        setDrawableAndText(R.string.cod,
                R.drawable.ic_cod);

    }
}
