package com.example.pharma.recycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.pharma.R;
import com.example.pharma.model.ImageBasedModel;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ImageBasedListAdapter extends RecyclerView.Adapter<ImageBasedListViewHolder> {
    private Context context;
    private ArrayList<ImageBasedModel> data;
    private String from;
    public ImageBasedListAdapter(Context context, ArrayList<ImageBasedModel> data, String from) {
        this.context = context;
        this.data = data;
        this.from=from;
    }

    @NonNull
    @Override
    public ImageBasedListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        return new ImageBasedListViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_with_image_for_presc_and_test,parent,false));
        if (from.contains("Category"))
            return new ImageBasedListViewHolder(LayoutInflater.from(context).inflate(R.layout.category_card,parent,false));
        else if (from.contains("Web") && from.contains("Horizontal"))
            return new ImageBasedListViewHolder(LayoutInflater.from(context).inflate(R.layout.web_article_horizontal,parent,false));
        else if (from.contains("Web"))
            return new ImageBasedListViewHolder(LayoutInflater.from(context).inflate(R.layout.web_article_layout,parent,false));
        else if (from.contains("Test") && from.contains("Horizontal"))
            return new ImageBasedListViewHolder(LayoutInflater.from(context).inflate(R.layout.test_horizontal_layout,parent,false));
        else
            return new ImageBasedListViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_with_image_for_presc_and_test,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageBasedListViewHolder holder, int position) {
        ImageBasedModel model=data.get(position);
    try {
        if (from.contains("Pres")){
            holder.setId(model.getID());
            holder.setTime(model.getID());
            holder.setImage(model.getImages());
            holder.setStatus(model.getStatus());
            holder.setTime(model.getID());
            holder.setListeners(model,from,"Pres");
        }else if (from.contains("Test")){
            if (from.contains("Horiz")){
                HashMap<String,Object>lab=((ArrayList<HashMap<String, Object>>)model.getLab()).get(0);
                holder.setPrice((long)lab.get("original_price"),(long)lab.get("price"));
                holder.setName("By "+ lab.get("name"));
                holder.setId(model.getName());
            }else{
                holder.setName("₹ "+ model.getPrice());
                holder.setTime(model.getID());
                holder.setId(model.getID());
            }
            holder.setImage(model.getImages());
//            holder.setTime(model.getID());
            holder.setListeners(model, from,"Test");
        }else{
            if (from.contains("Hori")){
                holder.setCardName(model.getName());
                holder.setCardIcon(model.getIcon());
                holder.setCardListeners(model.getName(),model.getUrl(),model.getID());
                holder.setNote((String) model.getNote());
                holder.setFromatedTime(model.getID());
                holder.setShare(model.getName(),model.getUrl());
            }else {
                holder.setName(model.getName());
                holder.setImage(model.getImages());
                holder.setNote((String) model.getNote());
                holder.setTime(model.getID());
                holder.setListeners(model.getName(), model.getUrl());
            }
        }
    }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
