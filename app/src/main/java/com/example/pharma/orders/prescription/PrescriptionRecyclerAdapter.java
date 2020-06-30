package com.example.pharma.orders.prescription;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.pharma.R;
import com.example.pharma.imgeslider.SliderAdapter;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PrescriptionRecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {
    private ImageButton seeImageBt;
    private ArrayList<String> nameLink;
    private  View mainBt;
    private  View topBt;
    private ArrayList<String> list;
    private Context context;
    private SliderAdapter pagerAdapter;
    private MaterialButton uploadBt;
    public PrescriptionRecyclerAdapter(Context context, ArrayList<String> nameLink, ArrayList<String> link, View topBt, View mainBt) {
        this.context=context;
        this.list=link;
        this.nameLink=nameLink;
        this.mainBt=mainBt;
        this.topBt=topBt;
    }

    PrescriptionRecyclerAdapter(Context context, ArrayList<String> nameLink, ArrayList<String> link, MaterialButton tempUploadBt, View mainBt, SliderAdapter pagerAdapter, MaterialButton uploadBt, ImageButton seeImage) {
        this.context=context;
        this.list=link;
        this.nameLink=nameLink;
        this.mainBt=mainBt;
        this.topBt=tempUploadBt;
        this.pagerAdapter=pagerAdapter;
        this.uploadBt=uploadBt;
        this.seeImageBt=seeImage;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.simple_adapter,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.e("PresAdapter",list.get(position));
        holder.setImage(list.get(position));
        holder.setTitle(nameLink.get(position));
        holder.itemView.findViewById(R.id.delBt).setOnClickListener(v -> {
              try {
                  list.remove(position);
                  nameLink.remove(position);
                  if (pagerAdapter!=null)
                      pagerAdapter.notifyDataSetChanged();
                  if (list.size() == 0) {
                      mainBt.setVisibility(View.GONE);
                      topBt.setVisibility(View.VISIBLE);
                      if (uploadBt!=null)
                        uploadBt.setVisibility(View.GONE);
                      if (seeImageBt!=null)
                        seeImageBt.setVisibility(View.GONE);
                  }
                  notifyDataSetChanged();
              }catch (Exception e){e.printStackTrace();}
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
