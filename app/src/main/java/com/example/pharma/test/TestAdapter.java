package com.example.pharma.test;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.pharma.R;
import com.example.pharma.model.ImageBasedModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TestAdapter extends RecyclerView.Adapter<TestViewHolder> {
    private Context context;
    private ArrayList<ImageBasedModel> data;
    private String from;
    TestAdapter(Activity context, ArrayList<ImageBasedModel> data, String from) {
        this.context = context;
        this.data = data;
        this.from=from;
    }


    @NonNull
    @Override
    public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (from.contains("Category"))
            return new TestViewHolder(LayoutInflater.from(context).inflate(R.layout.category_card,parent,false));
        else if (from.contains("Web") && from.contains("Horizontal"))
            return new TestViewHolder(LayoutInflater.from(context).inflate(R.layout.web_article_horizontal,parent,false));
        else if (from.contains("Web"))
            return new TestViewHolder(LayoutInflater.from(context).inflate(R.layout.web_article_layout,parent,false));
        else if (from.contains("Test") && from.contains("Horizontal"))
            return new TestViewHolder(LayoutInflater.from(context).inflate(R.layout.test_horizontal_layout,parent,false));
        else
        return new TestViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_with_image_for_presc_and_test,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull TestViewHolder holder, int position) {
        ImageBasedModel model=data.get(position);
    try {if (from.contains("Category")){
        holder.setCardName(model.getName());
        holder.setCardIcon(model.getIcon());
        holder.setListeners(model,from);
    }else
        if (from.contains("Pres"))
        {
            holder.setId(model.getID());
            holder.setTime(model.getID());
            holder.setImage(model.getImages(),from);
            holder.setStatus(model.getStatus());
            holder.setTime(model.getID());
            holder.setListeners(model,from);
        }
        else if (from.contains("Test"))
        {
            if (from.contains("Horiz"))
            {
                holder.setPrice(model.getOriginal_price(),model.getPrice());
                holder.setListeners(model, from);
                holder.setId(model.getName());
                holder.setImage(model.getImages(), from);
            }
            else if (!from.contains("Test_Report")){
                holder.setName("₹ "+ model.getPrice());
                holder.setTime(model.getID());
                holder.setListeners(model, from);
                holder.setId(model.getName());
                holder.setImage(model.getImages(), from);
            }
            else {
                holder.setStatus(model.getStatus());
                holder.openTestDetail(model, from);
                holder.setId(model.getName());
                holder.setImage(model.getImages(), from);
            }
        }
        else{
            holder.setCardName(model.getName());
            holder.setCardIcon(model.getIcon());
            holder.setListeners(model.getName(),model.getUrl(),model.getID());
            holder.setNote((String) model.getNote());
            holder.setFromatedTime(model.getID());
            holder.setShare(model.getName(),model.getUrl());
            holder.setFabButton(model.isFav());
            holder.addToWebFav(model.getID(),"Favorite Articles");
        }
    }catch (Exception ignored){}
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
