package com.example.pharma;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pharma.medicine.CustomMedicineListActivity;
import com.example.pharma.orders.ImageDataDetailActivity;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MedicineDetailsOverview extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.medicine_details_overview, container, false);
        HashMap<String,Object> map= (HashMap<String, Object>) getArguments().get("DATA");
        TextView name=root.findViewById(R.id.medicine_name);
        name.setText((String) map.get("medicine_name"));
        int medicine_price= (int) map.get("medicine_price");
        TextView price=root.findViewById(R.id.medicine_price);
        price.setText(getFormattedAmount(medicine_price));
        if (map.get("medicine_original_price")!=null){
           int medicineOriginalPrice= (int) map.get("medicine_original_price");
            if (medicine_price!=medicineOriginalPrice && medicineOriginalPrice==0) {
                TextView originalPrice = root.findViewById(R.id.medicine_original_price);
                originalPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                originalPrice.setText(getFormattedAmount(medicineOriginalPrice));
                TextView discount = root.findViewById(R.id.medicine_discount);
                int dis= ((medicineOriginalPrice-medicine_price)*100)/medicineOriginalPrice;
                discount.setText(String.format(Locale.UK,"%d%% OFF", dis));
            }
        }
        root.findViewById(R.id.addToCart).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(),CartActivity.class));
        });
        root.findViewById(R.id.addToFav).setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(), CustomMedicineListActivity.class);
            intent.putExtra("FROM","FAV");
            startActivity(intent);
        });
        return root;
    }
    private void setUpRecycler(String orders, View root, ArrayList<String> list) {
        SimpleAdapter adapter=new SimpleAdapter(orders.split(";"));
        RecyclerView recyclerView= root.findViewById(R.id.variantRecycler);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private String getFormattedAmount(long amount){
        return "₹ "+ NumberFormat.getNumberInstance(Locale.UK).format(amount);
    }

    public  class SimpleAdapter extends RecyclerView.Adapter<ViewHolder>{
        private String[] list;

        SimpleAdapter(String[] list){
            this.list=list;
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(getContext())
                    .inflate(R.layout.list_button_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            TextView textView= holder.itemView.findViewById(R.id.text);
            textView.setText(list[position]);
        }

        @Override
        public int getItemCount() {
            return list.length;
        }
    }
    static class ViewHolder extends RecyclerView.ViewHolder{

        ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
