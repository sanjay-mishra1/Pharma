package com.example.pharma.test;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pharma.R;
import com.example.pharma.firebase.FirebaseCustomAuth;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LabSelectorActivity extends AppCompatActivity {
    ArrayList<HashMap<String,Object>> data;
    CollectionReference db;
    private int selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_selector);
       try {
           data = (ArrayList<HashMap<String, Object>>) getIntent().getSerializableExtra("DATA");
           FirebaseApp app = new FirebaseCustomAuth().loadCustomFirebase(this, "tablethuts-extra");
           db = FirebaseFirestore.getInstance(app).collection("Lab");
           RecyclerView recyclerView = findViewById(R.id.recycle);
           LinearLayoutManager manager = new LinearLayoutManager(this);
           recyclerView.setLayoutManager(manager);
           Adapter adapter = new Adapter();
           recyclerView.setAdapter(adapter);
       }catch (Exception e){e.printStackTrace();}
    }
    class Adapter extends RecyclerView.Adapter<ViewHolder>{
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(LabSelectorActivity.this)
                    .inflate(R.layout.lab_list_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
           try {
               HashMap<String, Object> map = data.get(position);
               String location = (String) map.get("location");
               if (location == null)
                   loadLocation(map,position);
               else {
                   TextView locationText = holder.itemView.findViewById(R.id.location);
                   locationText.setText(location);
                   String img = (String) map.get("icon");
                   if (img != null && !img.isEmpty())
                       Glide.with(holder.itemView).load(img).into((ImageView) holder.itemView.findViewById(R.id.image));
               }
               TextView nameText = holder.itemView.findViewById(R.id.name);
               nameText.setText((String) map.get("name"));
               long price= (long) map.get("price");
               TextView priceText = holder.itemView.findViewById(R.id.price);
               priceText.setText(String.format(Locale.UK,"%s%d", getString(R.string.rupees), price));
               long originalPrice= (long) map.get("original_price");
               if (map.get("original_price")!=null&& originalPrice!=price){
                   TextView originalPriceText = holder.itemView.findViewById(R.id.originalPrice);
                   originalPriceText.setText(String.format(Locale.UK,"%s%d",
                           getString(R.string.rupees), originalPrice));
                   originalPriceText.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
               }
               RadioButton radioButton = holder.itemView.findViewById(R.id.radio);
               if (map.get("selected")==null)
                   map.put("selected",false);
               boolean checked = (boolean) map.get("selected");
               radioButton.setChecked(checked);
               if (radioButton.hasOnClickListeners()) ;
               radioButton.setOnClickListener(v -> {
                   map.put("selected", true);
                   if (position != selected) {
                       data.get(selected).put("selected", false);
                       selected = position;
                       notifyDataSetChanged();
                   }
               });
           }catch (Exception e){e.printStackTrace();}
        }

        private void loadLocation(HashMap<String, Object> map,int position) {
            db.document((String) Objects.requireNonNull(map.get("name"))).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()&& documentSnapshot.get("location")!=null)
                {
                    map.put("location",documentSnapshot.get("location")+"\nPin Code: "+documentSnapshot.get("location"));
                    map.put("pincode",documentSnapshot.get("pincode"));
                }
                else map.put("location","Not Available");
                notifyItemChanged(position);
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }
    static class ViewHolder extends RecyclerView.ViewHolder{

        ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
