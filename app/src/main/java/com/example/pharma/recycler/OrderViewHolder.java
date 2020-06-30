package com.example.pharma.recycler;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pharma.Constants;
import com.example.pharma.R;
import com.example.pharma.model.OrderModel;
import com.example.pharma.orders.OrderDetailActivity;
import com.google.android.material.button.MaterialButton;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderViewHolder extends RecyclerView.ViewHolder {
    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    void setOrderNo(String orderId) {
        TextView textView=itemView.findViewById(R.id.order_no);
        textView.setText(String.format("#%s", orderId));
    }

    void setOrderStatus(String orderStatus) {
        MaterialButton bt=itemView.findViewById(R.id.order_status);
        bt.setText(String.format("%s", orderStatus));
        if (orderStatus.contains("Cancel"))
        {
            bt.setTextColor(itemView.getResources().getColor(R.color.red));
        }else if (orderStatus.contains("Complete"))
            bt.setVisibility(View.GONE);
    }

    void setOrderTotalAmount(long orderTotalAmount) {
       try {
           TextView textView = itemView.findViewById(R.id.totalAmount);
           textView.setText(Constants.getFormatedAmount(orderTotalAmount));
       }catch (Exception e){}
    }

    void setOrderPaymentMode(String orderPaymentMode) {
        ImageView imageView=itemView.findViewById(R.id.order_mode);
        Glide.with(itemView).load(getDrawable(orderPaymentMode)).into(imageView);
//        textView.setCompoundDrawablesWithIntrinsicBounds(itemView.getResources()
//                .getDrawable(getDrawable(orderPaymentMode)),null,null,null);
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

    void setOrderTime(long orderTime) {
       try {
           TextView textView = itemView.findViewById(R.id.order_time);
           textView.setText(getFormattedTime(orderTime));
       }catch (Exception e){}
    }
    private String getFormattedTime(long time){
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yy hh:mm aa", Locale.UK);
        return "Ordered on "+dateFormat.format(calendar.getTime());
    }

    void setListener(OrderModel.OrderModelForRealtimeDb order) {
        itemView.findViewById(R.id.parent).setOnClickListener(v -> {
            Intent intent=new Intent(itemView.getContext(), OrderDetailActivity.class);
            intent.putExtra("DATA",order);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            itemView.getContext().startActivity(intent);

        });
    }

    void setMedicineName(String medicine_name) {
        TextView textView=itemView.findViewById(R.id.medicine_name);
        textView.setText(medicine_name);
    }

    void setMedicineQuantity(long medicine_quantity) {
        TextView textView=itemView.findViewById(R.id.medicine_quantity);
        textView.setText(String.valueOf(medicine_quantity));
    }

    void setMedicinePrice(long medicine_price) {
        TextView textView=itemView.findViewById(R.id.medicine_price);
        textView.setText(String.valueOf(medicine_price));
    }


    void setMedListener(String medicine_id) {

    }

    void setStatusTime(long order_status_time) {
        TextView textView=itemView.findViewById(R.id.order_status_time);
        textView.setText(Constants.getFormattedTime(order_status_time));
    }

    void setStatusName(String order_status_name) {
        TextView textView=itemView.findViewById(R.id.order_status_name);
        textView.setText(order_status_name);
    }

    void setStatusNote(String order_status_note) {
    if (order_status_note!=null){
        TextView textView=itemView.findViewById(R.id.order_status_note);
        textView.setText(order_status_note);
    }
    }
}
