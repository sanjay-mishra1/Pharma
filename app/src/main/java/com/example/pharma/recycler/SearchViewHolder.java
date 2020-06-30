package com.example.pharma.recycler;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pharma.R;
import com.example.pharma.WebArticleActivity;
import com.example.pharma.medicine.ShowMedicinesActivity;
import com.example.pharma.medicine_info.MedicineDetailsActivity;
import com.example.pharma.test.ExtraOptionsActivity;
import com.example.pharma.test.ShowTestDetails;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class SearchViewHolder  extends RecyclerView.ViewHolder {
    SearchViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setName(String medicine_name) {
        TextView textView=itemView.findViewById(R.id.medicineName);
        textView.setText(medicine_name);
    }

    void setWeight(String medicine_weight) {
        TextView textView=itemView.findViewById(R.id.medicine_weight);
        textView.setText(medicine_weight);
        textView.setVisibility(View.VISIBLE);
    }
    void openMedicineInfo(String medicine_id, String medicine_name) {
        itemView.setOnClickListener(v -> {
            Class className;
            Intent intent;
            String name=medicine_name;
            if (name==null)
                name=medicine_id;
            if (medicine_name==null)
            {  intent= new Intent(itemView.getContext(), ShowMedicinesActivity.class);
                Log.e("Viewholder name","->"+name);
                intent.putExtra("DATA","medicine_company");
                intent.putExtra("name",name);
                intent.putExtra("key","medicine_company");
                intent.putExtra("value",name);
            }else{
                intent=new Intent(itemView.getContext(), MedicineDetailsActivity.class);
                intent.putExtra("MEDICINE_NAME",name);
                intent.putExtra("MEDICINE_ID",medicine_id);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            itemView.getContext().startActivity(intent);
        });
    }

    public void setImg(String medicine_img) {
        if (medicine_img!=null&& !medicine_img.isEmpty())
        Glide.with(itemView.getContext()).load(medicine_img).into((ImageView)itemView.findViewById(R.id.medicine_img));
    }

    void openAllTestInCategory(String id,String name) {
        itemView.setOnClickListener(v -> {
            Intent intent=new Intent(itemView.getContext(), ExtraOptionsActivity.class);
            intent.putExtra("FROM","All_Test");
            intent.putExtra("CID",id);
            intent.putExtra("CNAME",name);
            itemView.getContext().startActivity(intent);
        });

    }

    void openAllArticleInCategory(String id, String name) {
        itemView.setOnClickListener(v -> {
            Intent intent=new Intent(itemView.getContext(), ExtraOptionsActivity.class);
            intent.putExtra("FROM","Web Articles");
            intent.putExtra("CID",id);
            intent.putExtra("CNAME",name);
            itemView.getContext().startActivity(intent);
        });
    }

    void openTestDetail(String id) {
        itemView.setOnClickListener(v -> {
            Intent intent=new Intent(itemView.getContext(), ShowTestDetails.class);
            intent.putExtra("DATA",id);
            itemView.getContext().startActivity(intent);
        });
    }

    void openWebArticle(String id,String url,String name) {
        itemView.setOnClickListener(v -> {
            Intent intent=new Intent(itemView.getContext(), WebArticleActivity.class);
            intent.putExtra("NAME",name);
            intent.putExtra("URL",url);
            intent.putExtra("ID",id);
            intent.putExtra("SAVE",true);
            itemView.getContext().startActivity(intent);
        });
    }
}
