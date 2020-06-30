package com.example.pharma.recycler;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.pharma.R;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
    private  ArrayList<HashMap<String, Object>> medData;
    private Context context;
    private String from;
    public SearchAdapter(Context context, ArrayList<HashMap<String, Object>> medData){
        this.context=context;
        this.medData=medData;
    }

    public SearchAdapter(Context context, ArrayList<HashMap<String, Object>> medData, String from) {
        this.context=context;
        this.medData=medData;
        this.from=from;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchViewHolder(LayoutInflater.from(context).inflate(R.layout.search_adapter_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        HashMap<String,Object> map=medData.get(position);
        Log.e("SearchAdapter","MedData "+medData.toString());
//        holder.setName((String) map.get("medicine_name"));
//        String weight= (String) map.get("medicine_weight");
//        if (!weight.isEmpty())
//        holder.setWeight((String) map.get("medicine_weight"));
//        holder.setImg((String) map.get("medicine_img"));
//

        //new
        if (from==null) {
            if (map.get("medicine_name") == null) {
                String name = (String) map.get("medicine_key");
                map.put("medicine_id", name);
                if (name != null)
                    holder.setName(name);
                holder.setWeight("In company");
            } else {
                holder.setName((String) map.get("medicine_name"));
                String companyName = (String) map.get("medicine_company");
                if (companyName != null && !companyName.isEmpty())
                    holder.setWeight("By " + companyName);
            }
            holder.openMedicineInfo((String) map.get("medicine_id"), (String) map.get("medicine_name"));
        }else if (from.contains("test")){
            if (map.get("main")==null){
                holder.setWeight("In Test Category");
                holder.openAllTestInCategory((String) map.get("id"),(String) map.get("name"));
            }else{
                holder.setWeight("In Test");
                holder.openTestDetail((String) map.get("id"));
            }
            holder.setName((String) map.get("name"));
        }else if (from.contains("web")){
            if (map.get("main")==null){
                holder.setWeight("In Web Category");
                holder.openAllArticleInCategory((String) map.get("id"),(String) map.get("name"));
            }else{
                holder.setWeight("In Web Article");
                holder.openWebArticle(
                        (String)map.get("id"),
                        (String)map.get("url"),
                        (String)map.get("name"));
            }
            holder.setName((String) map.get("name"));
        }
        holder.setImg((String) map.get("medicine_img"));
    }

    @Override
    public int getItemCount() {
        return medData.size();
    }
}
