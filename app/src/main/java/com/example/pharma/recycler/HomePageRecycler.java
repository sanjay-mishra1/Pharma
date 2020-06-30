package com.example.pharma.recycler;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pharma.R;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HomePageRecycler extends RecyclerView.Adapter<ViewHolder> {
    private  Context context;
    private  TextView cartText;
//    private ArrayList<HashMap<String, String>> data;
    private ArrayList<HashMap<String, Object>> data;
    private int type;
    public HomePageRecycler(Context context, ArrayList<HashMap<String, Object>> data, TextView cartText) {
        this.data=data;
        this.context=context;
        this.cartText=cartText;
    }
    @Override
    public int getItemViewType(int position) {
        String type= (String) data.get(position).get("type_name");
        if (type.contains("medicine"))
        {   this.type=1;
            return R.layout.recyclerview;
        }
        else if ((type.contains("Advertisement")||type.contains("Information Box"))&& data.get(position).get("type_id").equals("2"))
        {this.type=2;
            return R.layout.information_box;
        }
        else if ((type.contains("Advertisement")||type.contains("Information Box")))
        {   this.type=3;
            return R.layout.ui_element_ad;
        }
        else {
            this.type=4;
            return R.layout.ui_element_container;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(viewType,parent,false));
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.e("Data","=>"+data.get(position).toString());
//        holder.setView(data.get(position),cartText);
        if (type==1)
            holder.setView2(data.get(position),cartText);
        else if (type==2)
            holder.setInfoBox(data.get(position));
        else if (type==3)
            holder.setAdFragment(data.get(position));
        else if (type==4)
            holder.setContainer(data.get(position),cartText);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
