package com.example.pharma.medicine_info;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pharma.R;
import com.example.pharma.recycler.ViewHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MedicineInfoPage extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_medicine_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    try {
        RecyclerView recyclerView=view.findViewById(R.id.recycle);
        HashMap<String,String> map= (HashMap<String, String>) getArguments().getSerializable("DATA");
        String medid= getArguments().getString("medicine_id");
        if (map!=null) {
            LinearLayoutManager manager = new LinearLayoutManager(getContext());
            Adapter adapter = new Adapter(map.keySet(), map.values());
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(adapter);
                    ArrayList<HashMap<String,String>> temp=configureNote(map);
                    Log.e("Notes","Info->map->"+map+"\n->arraylist->"+temp);
        }
    }catch (Exception e){e.printStackTrace();}
    }

    private ArrayList<HashMap<String, String>> configureNote(HashMap<String, String> map) {
        ArrayList<HashMap<String,String>> notes=new ArrayList<>();
        int i=0;
        for (String key:map.keySet()){
            String time=String.valueOf(System.currentTimeMillis());
            HashMap<String,String> temp=new HashMap<>();
            temp.put("note_title",key);
            temp.put("note_desc",map.get(key));
            temp.put("note_time",time+i);
            notes.add(temp);
            i++;
        }
    return notes;
    }


    class  Adapter extends RecyclerView.Adapter<ViewHolder>{
        private String[] title;
        String[] desc;
        private int layout;

        Adapter(String[] desc) {
            this.desc = desc;
            layout=R.layout.textview_layout;
        }

        Adapter(Set<String> title, Collection<String> desc) {
            this.title= title.toArray(new String[0]);
            this.desc= desc.toArray(new String[0]);
            layout=R.layout.medicine_detail_slots;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(getContext())
                    .inflate(layout,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (layout==R.layout.medicine_detail_slots){
                TextView textView=holder.itemView.findViewById(R.id.slot_title);
                textView.setText(title[position]);
                RecyclerView recyclerView=holder.itemView.findViewById(R.id.slot_recycle);
                LinearLayoutManager manager=new LinearLayoutManager(getContext());
                Adapter adapter=new Adapter(desc[position].split("->"));
                recyclerView.setLayoutManager(manager);
                recyclerView.setAdapter(adapter);
            }else{
                TextView descText=holder.itemView.findViewById(R.id.text);
                descText.setText(desc[position]);
            }
    }

        @Override
        public int getItemCount() {

            if (layout==R.layout.medicine_detail_slots)
                return title.length;
            else
            return desc.length;
        }
    }
}
