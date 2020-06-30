package com.example.pharma.orders.prescription;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pharma.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

 class ViewHolder extends RecyclerView.ViewHolder{

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
    }

     public void setImage(String datum) {
         ImageView imageView=itemView.findViewById(R.id.imageView);
         Glide.with(itemView).load(datum).into(imageView);
     }

     public void setTitle(String datum) {
         TextView textView=itemView.findViewById(R.id.title);
         textView.setText(datum);
    }

 }