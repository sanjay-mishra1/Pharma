package com.example.pharma.recycler;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.pharma.R;
import com.example.pharma.model.OrderModel;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderAdapter extends RecyclerView.Adapter<OrderViewHolder> {
    private int layout;
    private ArrayList<OrderModel.OrderModelForRealtimeDb> data;
    private Context context;
    private ArrayList<HashMap<String, Object>> list;
    private String from ;
    public OrderAdapter(ArrayList<OrderModel.OrderModelForRealtimeDb> data, Context context) {
        this.data = data;
        this.context = context;
        from="order";
        layout=R.layout.order_card_layout;
    }

    public OrderAdapter(ArrayList<HashMap<String, Object>> list, Context context, String from, int layout) {
        this.list=list;
        this.context=context;
        this.from=from;
        this.layout=layout;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderViewHolder(LayoutInflater.from(context).
                inflate(layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
//        HashMap<String,Object> order=data.get(position);
        if (from.equals("order")) {
            OrderModel.OrderModelForRealtimeDb model = data.get(position);
            holder.setOrderNo(model.getOrder_id());
            holder.setOrderStatus(model.getCurrent_order_status());
            holder.setOrderTotalAmount((Long) model.getOrder_amount_stats().get("total_amount"));
            holder.setOrderPaymentMode(model.getOrder_payment_from());
            holder.setOrderTime(model.getOrder_time());
            holder.setListener(model);
        }else{
            HashMap<String,Object> data=list.get(position);

            if (from.contains("med_list")){
                Log.e("med_list","->"+data);
                holder.setMedicineName((String) data.get("medicine_name"));
                holder.setMedicineQuantity((Long) data.get("medicine_quantity"));
                holder.setMedicinePrice((Long) data.get("medicine_price"));
                holder.setMedListener((String) data.get("medicine_id"));
            }else{
                Log.e("status_list","->"+data);
                holder.setStatusTime((Long) data.get("order_status_time"));
                holder.setStatusName((String) data.get("order_status_name"));
                holder.setStatusNote((String) data.get("order_status_note"));
            }

        }
    }

    @Override
    public int getItemCount()
    {   if (from.equals("order"))
            return data.size();
        else
        return list.size();
    }
}
