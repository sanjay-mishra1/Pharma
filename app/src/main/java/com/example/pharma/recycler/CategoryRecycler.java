package com.example.pharma.recycler;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.pharma.R;
import com.example.pharma.model.CategoryModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryRecycler extends RecyclerView.Adapter<CategoryViewHolder> {
    private Fragment fragment;
    private ArrayList<CategoryModel> data;
    private Context context;
    private int layout;
    private String from;
    public CategoryRecycler(Context context, ArrayList<CategoryModel> data,String from){
        this.context=context;
        this.data=data;
        this.layout=R.layout.simple_circle_layout;
        this.from=from;
    }
    public CategoryRecycler(Context context, ArrayList<CategoryModel> data, int layout, Fragment fragment,String from){
        this.context=context;
        this.data=data;
        this.layout=layout;
        this.fragment=fragment;
        this.from=from;
    }


    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      return new CategoryViewHolder(LayoutInflater.from(context).inflate(layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder viewHolder, int position) {
        CategoryModel medicineModel=data.get(position);
        Log.e("Category","=>"+medicineModel.getCategory_name());
        viewHolder.setCategoryImage(medicineModel.getIcon(),position);
        viewHolder.setText(medicineModel.getCategory_name());
       try {
           if (medicineModel.getCategory_name().contains("All Categories"))
               viewHolder.OpenAllCategory(fragment);
           else
//               viewHolder.setBoxListener(medicineModel.getCategory_id(),medicineModel.getCategory_name());
               viewHolder.setBoxListener2(medicineModel.getCategory_name(),from,
                       medicineModel.getCategory_id());

       }catch (Exception ignored){}
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
