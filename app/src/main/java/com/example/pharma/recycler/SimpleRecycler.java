package com.example.pharma.recycler;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pharma.R;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SimpleRecycler extends RecyclerView.Adapter<ViewHolder> {
    private TextView carText;
    private String from;
    private  Context context;
    private  ArrayList<HashMap<String, Object>> data;
    private  int layout;
    private int transparentviewtype=R.layout.transparent_layout;;
    private int mainviewtype=R.layout.medicine_card_layout;

    public SimpleRecycler(ArrayList<HashMap<String, Object>> data, int layout, Context context, String cart, TextView cartText) {
        this.data=data;
        this.layout=layout;
        this.context=context;
        this.from=cart;
        this.carText=cartText;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==transparentviewtype)
        return new ViewHolder(LayoutInflater.from(context).inflate(transparentviewtype,parent,false));
        else return new ViewHolder(LayoutInflater.from(context).inflate(layout,parent,false));
    }

    @Override
    public int getItemViewType(int position) {
        if (from.equals("Home")) {
            if (position == 0)
                return transparentviewtype;
            else return layout;
        }else return layout;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (!from.equals("cart")){
        if (position!=0 || from.equals("Custom")) {
            try {
                Log.e("SimpleRecycler", "=>" + data.get(position).toString());
                HashMap<String, Object> map = data.get(position);
                holder.setBoxListeners(map);
                holder.setMedicineImg((String) map.get("medicine_img"));
                holder.setMedicineName((String) map.get("medicine_name"));
                holder.setMedicinePrice((long) map.get("medicine_price"));
                holder.setMedicineOriginalPrice((long) map.get("medicine_price"), (long) map.get("medicine_original_price"));
                String medid=(String) map.get("medicine_id");
                holder.setAddToFavListener(medid);
                if ((boolean) map.get("medicine_in_my_fav"))
                    Glide.with(holder.itemView).load(R.drawable.ic_favorite_red_24dp).
                            into((ImageView)holder.itemView.findViewById(R.id.addToFav));
                else Glide.with(holder.itemView).load(R.drawable.ic_favorite_black_24dp).
                        into((ImageView)holder.itemView.findViewById(R.id.addToFav));

                if ((boolean) map.get("medicine_in_my_cart"))
                {   holder.itemView.findViewById(R.id.quantity_bt).setVisibility(View.VISIBLE);
                    holder.itemView.findViewById(R.id.addToCart).setVisibility(View.GONE);
                    try {
                        holder.setIncreaseMedQuantity((long) map.get("medicine_max_quantity"),medid);
                        holder.setDecreaseMedQuantity(medid);
                    }catch (Exception ignored){}
                }else{holder.itemView.findViewById(R.id.quantity_bt).setVisibility(View.GONE);
                    holder.itemView.findViewById(R.id.addToCart).setVisibility(View.VISIBLE);
                    holder.setAddToCartListener(medid,carText,map.get("medicine_variant"),(long)map.get("medicine_max_quantity"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            HashMap<String, Object> map = data.get(0);
            holder.setListenerForTransparentCard((String)map.get("medicine_type_id"),(String)map.get("medicine_type_Data"));
        }
        }
        else{
            //cart stuffs
            try {
                HashMap<String, Object> map = data.get(position);
                String medid= (String) map.get("medicine_id");
                holder.setMedicineImg((String) map.get("medicine_img"));
                holder.setMedicineName((String) map.get("medicine_name"));
                holder.setMedicinePrice((long) map.get("medicine_price"));
                holder.setIncreaseMedQuantity((long) map.get("medicine_max_quantity"),medid);
                holder.setDecreaseMedQuantity(medid);
                holder.setMedicineQuantity((int) map.get("medicine_quantity"));
                holder.setBoxListeners(map);
                holder.itemView.findViewById(R.id.delFromCart).setOnClickListener(v -> {
                    data.remove(position);
                    notifyDataSetChanged();
                    holder.delMedicineFromCart(medid);

                });
            }catch (Exception e){e.printStackTrace();}
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }
}
