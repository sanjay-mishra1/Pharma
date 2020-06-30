package com.example.pharma.recycler;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pharma.Constants;
import com.example.pharma.R;
import com.example.pharma.firebase.FirebaseCustomAuth;
import com.example.pharma.imgeslider.SliderAdapter;
import com.example.pharma.medicine.CustomMedicineListActivity;
import com.example.pharma.medicine.ShowMedicinesActivity;
import com.example.pharma.medicine_info.MedicineDetailsActivity;
import com.example.pharma.model.ImageBasedModel;
import com.example.pharma.test.ExtraOptionsActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import static android.content.Context.MODE_PRIVATE;
import static com.example.pharma.recycler.RecyclerUI.dpToPx;

public class ViewHolder extends RecyclerView.ViewHolder {
    public ViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    void setView(HashMap<String, Object> viewData, TextView cartText) {
        TextView title=itemView.findViewById(R.id.title);
        title.setText((String)viewData.get("type_title"));
        String typeName= (String) viewData.get("type_name");
       FirebaseApp app= new FirebaseCustomAuth().loadCustomFirebase(itemView.getContext(),"tablethuts-medicines");
        if (Objects.requireNonNull(typeName).equalsIgnoreCase("category")){
                loadCategoryData((String)viewData.get("type_id"),app,
                        (String)viewData.get("type_title"),"",cartText);
        }else{
            loadCustomData(Objects.requireNonNull((String)viewData.get("type_id")),app,
                    (String)viewData.get("type_title"),cartText);
        }
    }
    void setView2(HashMap<String, Object> viewData, TextView cartText) {
        TextView title=itemView.findViewById(R.id.title);
        title.setText((String)viewData.get("type_title"));
        String typeName=(String)viewData.get("type_name");
        FirebaseApp app= new FirebaseCustomAuth().loadCustomFirebase(itemView.getContext(),"tablethuts-medicines");
        String sorting=(String)viewData.get("type_sorting");
        if (Objects.requireNonNull(typeName).equalsIgnoreCase("medicine_category")||
                typeName.contains("company")){
            loadCategoryData((String)viewData.get("type_id"),app,
                    (String)viewData.get("type_title"),typeName,cartText);
        }else if (typeName.contains("medicine_disease")){
            loadMedicineBasedOnDisease((String)viewData.get("type_id"),app,
                    (String) viewData.get("type_title"),cartText);
        }
        else{
            loadCustomData((String)Objects.requireNonNull(viewData.get("type_id")),app,
                    (String) viewData.get("type_title"),cartText);
        }
    }
    private void loadCategoryData(String typeID, FirebaseApp app, String type_title,String type, TextView cartText) {
        RecyclerView recyclerView= Objects.requireNonNull(itemView).findViewById(R.id.recyclerView);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        ArrayList<HashMap<String,Object>> data=new ArrayList<>();
        HashMap <String,Object> temp=new HashMap<>();
        temp.put("medicine_type_id",typeID+";"+type_title+";"+type);
        data.add(temp);
        SimpleRecycler adapter=new SimpleRecycler(data,R.layout.medicine_card_layout,
                itemView.getContext(),"Home",cartText);
        recyclerView.setAdapter(adapter);
//        Query query=FirebaseDatabase.getInstance(app).getReference("Medicines").
//                orderByChild("medicine_category").equalTo(typeID).limitToFirst(10);
        Query query=FirebaseDatabase.getInstance(app).getReference("Medicines").
                orderByChild(type).equalTo(typeID).limitToFirst(10);
        SharedPreferences sharedPreferencesCart=itemView.getContext().getSharedPreferences("Cart",MODE_PRIVATE);
        SharedPreferences sharedPreferencesFav=itemView.getContext().getSharedPreferences("Favorite",MODE_PRIVATE);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                    HashMap<String, Object> map = new HashMap<>();
                    try {
                        HashMap<String, Object> dataSnapshotValue = (HashMap<String, Object>) dataSnapshot1.getValue();
                        map.put("medicine_img", dataSnapshotValue != null ? ((ArrayList<String>) dataSnapshotValue.get("medicine_images")).get(0) : "NA");
                        map.put("medicine_name", dataSnapshotValue != null ? dataSnapshotValue.get("medicine_name") : "NA");
//                        map.put("medicine_price", dataSnapshotValue != null ? dataSnapshotValue.get("medicine_price") : "NA");
                        map.put("medicine_price",((HashMap<String, Object>) ((ArrayList<Object>)
                                dataSnapshotValue.get("medicine_variant")).get(0)).get("medicine_price"));

                        map.put("medicine_id", dataSnapshotValue != null ? dataSnapshot1.getKey() : "NA");
                        map.put("medicine_max_quantity", dataSnapshotValue != null ? dataSnapshotValue.get("medicine_max_quantity") : "NA");
                        map.put("medicine_in_my_fav", sharedPreferencesFav.contains(dataSnapshot1.getKey()));
                        map.put("medicine_in_my_cart", sharedPreferencesCart.contains(dataSnapshot1.getKey()));
                        map.put("medicine_original_price", ((HashMap<String, Object>) ((ArrayList<Object>)
                                dataSnapshotValue.get("medicine_variant")).get(0)).get("medicine_original_price"));
                        data.add(map);
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void loadMedicineBasedOnDisease(String typeID, FirebaseApp app, String type_title,TextView cartText) {
        RecyclerView recyclerView= Objects.requireNonNull(itemView).findViewById(R.id.recyclerView);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        ArrayList<HashMap<String,Object>> data=new ArrayList<>();
        HashMap <String,Object> temp=new HashMap<>();
        temp.put("medicine_type_id",typeID+";"+type_title+";medicine_disease");
        data.add(temp);
        SimpleRecycler adapter=new SimpleRecycler(data,R.layout.medicine_card_layout,
                itemView.getContext(),"Home",cartText);
        recyclerView.setAdapter(adapter);
        com.google.firebase.firestore.Query query= FirebaseFirestore.getInstance(app)
                .collection("Medicines").whereArrayContains("medicine_disease",typeID);
        SharedPreferences sharedPreferencesCart=itemView.getContext().getSharedPreferences("Cart",MODE_PRIVATE);
        SharedPreferences sharedPreferencesFav=itemView.getContext().getSharedPreferences("Favorite",MODE_PRIVATE);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                HashMap<String, Object> map = new HashMap<>();
                try {String medId=documentSnapshot.getId();
                    HashMap<String, Object> dataSnapshotValue = (HashMap<String, Object>) documentSnapshot.getData();
                    map.put("medicine_img", dataSnapshotValue != null ? ((String) dataSnapshotValue.get("medicine_img")) : "NA");
                    map.put("medicine_name", dataSnapshotValue != null ? dataSnapshotValue.get("medicine_name") : "NA");
                    map.put("medicine_id", dataSnapshotValue != null ? medId : "NA");
                    map.put("medicine_price", dataSnapshotValue.get("medicine_price"));
                    map.put("medicine_original_price", dataSnapshotValue.get("medicine_original_price"));
                    map.put("medicine_in_my_fav", sharedPreferencesFav.contains(medId));
                    map.put("medicine_in_my_cart", sharedPreferencesCart.contains(medId));

                    data.add(map);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    void setMedicineImg(String img) {
        Glide.with(itemView).load(img).into((ImageView)itemView.findViewById(R.id.medicine_img));
    }

    void setMedicineName(String medicine_name) {
        TextView textView=itemView.findViewById(R.id.medicine_name);
        textView.setText(medicine_name);
    }

    void setMedicinePrice(long medicine_price) {
        TextView textView=itemView.findViewById(R.id.medicine_price);
        textView.setText(String.format(Locale.UK,"₹ %d", medicine_price));
    }

    void setMedicineOriginalPrice(long medicine_price,long medicine_original_price) {
        TextView textView=itemView.findViewById(R.id.medicine_original_price);
        textView.setText(String.format(Locale.UK,"₹ %d", medicine_original_price));
        textView.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        TextView discount=itemView.findViewById(R.id.medicine_discount);
        int dis=(int)(((medicine_original_price-medicine_price)*100)/medicine_original_price);
        discount.setText(String.format(Locale.UK,"%d%% OFF", dis));
    }
    private void loadCustomData(String type_id, FirebaseApp app, String type_title, TextView cartText) {
        String[] s =type_id.split(";");
        RecyclerView recyclerView= Objects.requireNonNull(itemView).findViewById(R.id.recyclerView);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        ArrayList<HashMap<String,Object>> data=new ArrayList<>();
        HashMap <String,Object> temp=new HashMap<>();
        temp.put("medicine_type_id","Custom;"+type_title);
        temp.put("medicine_type_Data",type_id);
        data.add(temp);
        SimpleRecycler adapter=new SimpleRecycler(data,R.layout.medicine_card_layout,
                itemView.getContext(),"Home", cartText);
        recyclerView.setAdapter(adapter);
        SharedPreferences sharedPreferencesCart=itemView.getContext().getSharedPreferences("Cart",MODE_PRIVATE);
        SharedPreferences sharedPreferencesFav=itemView.getContext().getSharedPreferences("Favorite",MODE_PRIVATE);
        for(String id:s){
            FirebaseDatabase.getInstance(app).getReference("Medicines").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    HashMap<String,Object> map=new HashMap<>();
                  try {
                      HashMap<String, Object> dataSnapshotValue = (HashMap<String, Object>) dataSnapshot.getValue();
                      map.put("medicine_img", dataSnapshotValue != null ? ((ArrayList<String>) dataSnapshotValue.get("medicine_images")).get(0) : "NA");
                      map.put("medicine_name", dataSnapshotValue != null ? dataSnapshotValue.get("medicine_name") : "NA");
                      map.put("medicine_id", dataSnapshotValue != null ? id : "NA");
                      map.put("medicine_in_my_fav", sharedPreferencesFav.contains(id));
                      map.put("medicine_in_my_cart", sharedPreferencesCart.contains(id));
                      map.put("medicine_max_quantity", dataSnapshotValue != null ? dataSnapshotValue.get("medicine_max_quantity") : "NA");
//                      map.put("medicine_price", dataSnapshotValue != null ? dataSnapshotValue.get("medicine_price") : "NA");
                      map.put("medicine_price",((HashMap<String, Object>) ((ArrayList<Object>)
                              dataSnapshotValue.get("medicine_variant")).get(0)).get("medicine_price"));
                      map.put("medicine_original_price", ((HashMap<String,Object>) ((ArrayList<Object>)
                              dataSnapshotValue.get("medicine_variant")).get(0)).get("medicine_original_price"));

                      data.add(map);
                      adapter.notifyDataSetChanged();
                  }catch (Exception e){e.printStackTrace();}
                    //Log.e("Custom","name "+model.getMedicine_name()+" snapshot "+dataSnapshot);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    void setListenerForTransparentCard(String medicine_type_id,String data) {
        itemView.findViewById(R.id.transparent_parent).setOnClickListener(v -> {
            if (medicine_type_id.contains("Custom")){
                Intent intent=new Intent(itemView.getContext(), CustomMedicineListActivity.class);
                String[] s=medicine_type_id.split(";");
                intent.putExtra("DATA",data.split(";"));
                intent.putExtra("DATA_NAME",s[1]);
                intent.putExtra("FROM","Custom");
                itemView.getContext().startActivity(intent);
            }else{
                Intent intent=new Intent(itemView.getContext(), ShowMedicinesActivity.class);
                String s[]=medicine_type_id.split(";");
                intent.putExtra("DATA",s[1]);
                intent.putExtra("name",s[1]);
                String key;
                if (s.length==3)
                    key=s[2];
                else key="medicine_category";
                intent.putExtra("key",key);
                intent.putExtra("value",s[0]);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                itemView.getContext().startActivity(intent);



            }
        });
    }

    void setAddToCartListener(String medicine_id, TextView carText) {
    itemView.findViewById(R.id.addToCart).setOnClickListener(v -> {
    if (Constants.uid==null)
    Constants.uid= Objects.requireNonNull(itemView.getContext()). getSharedPreferences("USER_CREDENTIALS",MODE_PRIVATE).getString("UID",null);
    SharedPreferences preferences=itemView.getContext().getSharedPreferences("Cart",MODE_PRIVATE);
    SharedPreferences.Editor edit=preferences.edit();
    edit.putInt(medicine_id,1);
    try {
        carText.setText(String.valueOf(preferences.getAll().size()));
    }catch (Exception ignored){}
    edit.apply();
    try {
        itemView.findViewById(R.id.quantity_bt).setVisibility(View.VISIBLE);
        itemView.findViewById(R.id.addToCart).setVisibility(View.GONE);
    }catch (Exception ignored){}
    Toast.makeText(itemView.getContext(),"Added to cart",Toast.LENGTH_SHORT).show();
    FirebaseDatabase.getInstance().getReference("Customers").child(Constants.uid)
            .child("Cart/"+medicine_id).setValue(1);
        });
    }

    void setIncreaseMedQuantity(long maxQuantity,String medid) {
       itemView.findViewById(R.id.imageButton_add).setOnClickListener(v -> {
           try {
               TextView textView = itemView.findViewById(R.id.medicine_quantity);
               int quantity = Integer.parseInt(textView.getText().toString().trim());
               Log.e("Quantity","=>"+quantity);
               if (quantity + 1 <= maxQuantity) {
                   quantity++;
                   textView.setText(String.valueOf( quantity));
                   SharedPreferences preferences = itemView.getContext().getSharedPreferences("Cart", MODE_PRIVATE);
                   SharedPreferences.Editor edit = preferences.edit();
                   edit.putInt(medid, quantity);
                   edit.apply();
               } else
                   Toast.makeText(itemView.getContext(), "Only " + maxQuantity + " items is allowed.", Toast.LENGTH_LONG).show();
           }catch (Exception e){e.printStackTrace();}
       });

    }

    void setDecreaseMedQuantity(String medid) {
        itemView.findViewById(R.id.quantity_reduce).setOnClickListener(v -> {
            TextView textView=itemView.findViewById(R.id.medicine_quantity);
            int quantity= Integer.parseInt(textView.getText().toString().trim());
            if (!textView.getText().toString().trim().equals("1"))
                quantity--;
            textView.setText(String.valueOf(quantity));
            SharedPreferences preferences=itemView.getContext().getSharedPreferences("Cart",MODE_PRIVATE);
            SharedPreferences.Editor edit = preferences.edit();
            edit.putInt(medid,quantity);
            edit.apply();
        });

    }

    void setAddToFavListener(String medicine_id) {
        itemView.findViewById(R.id.addToFav).setOnClickListener(v -> {
            if (Constants.uid==null)
                Constants.uid= Objects.requireNonNull(itemView.getContext()). getSharedPreferences("USER_CREDENTIALS",MODE_PRIVATE).getString("UID",null);
            SharedPreferences preferences=itemView.getContext().getSharedPreferences("Favorite",MODE_PRIVATE);
            SharedPreferences.Editor edit = preferences.edit();
            String value;
            String msg;
            if (!preferences.contains(medicine_id)) {
                edit.putString(medicine_id, medicine_id);
                edit.apply();
                value=medicine_id;
                msg="Added to ";
                Glide.with(itemView.getContext()).load(R.drawable.ic_favorite_red_24dp).into((ImageView)itemView.findViewById(R.id.addToFav));
            }else{
                edit.remove(medicine_id);
                edit.apply();
                value=null;
                Glide.with(itemView.getContext()).load(R.drawable.ic_favorite_black_24dp).into((ImageView)itemView.findViewById(R.id.addToFav));
                msg="removed from ";
            }
            Toast.makeText(itemView.getContext(), msg+"favorites", Toast.LENGTH_SHORT).show();
            FirebaseDatabase.getInstance().getReference("Customers").child(Constants.uid)
                    .child("Favorites/" + medicine_id).setValue(value);
        });
    }

    void setMedicineQuantity(int quantity) {
        TextView textView=itemView.findViewById(R.id.medicine_quantity);
        textView.setText(String.valueOf(quantity));
    }

    void delMedicineFromCart(String medid) {
        SharedPreferences preferences=itemView.getContext().getSharedPreferences("Cart",MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.remove(medid);
        edit.apply();
        Toast.makeText(itemView.getContext(),"Medicine Deleted",Toast.LENGTH_SHORT).show();
        FirebaseDatabase.getInstance().getReference("Customers").child(Constants.uid)
                .child("Cart/"+medid).setValue(null);
    }

    void setBoxListeners(HashMap<String, Object> map) {
        itemView.findViewById(R.id.color).setOnClickListener(v -> {
            Intent intent=new Intent(itemView.getContext(), MedicineDetailsActivity.class);
//            intent.putExtra("DATA",map);
            intent.putExtra("MEDICINE_ID",(String) map.get("medicine_id"));
            intent.putExtra("MEDICINE_NAME",(String) map.get("medicine_name"));
            Log.e("MapData","->"+map);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            itemView.getContext().startActivity(intent);
        });
    }
    private void loadWebData(String typeID, String type_title) {
        FirebaseApp app=new FirebaseCustomAuth().loadCustomFirebase(itemView.getContext(),"tablethuts-extra");
        RecyclerView recyclerView= Objects.requireNonNull(itemView).findViewById(R.id.recyclerView);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        ArrayList<HashMap<String,Object>> data=new ArrayList<>();
        HashMap <String,Object> temp=new HashMap<>();
        temp.put("medicine_type_id",typeID+";"+type_title);
        data.add(temp);
        Query query;
        query = FirebaseDatabase.getInstance(app).getReference("Web Article")
                .orderByChild("category_id").equalTo(typeID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<ImageBasedModel> data=new ArrayList<>();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    ImageBasedModel model = snapshot.getValue(ImageBasedModel.class);
                    if (model != null) {
                        model.setID(snapshot.getKey());
                        data.add(model);
                    }
                }
                LinearLayoutManager gridManager =
                        new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(gridManager);
                ImageBasedListAdapter adapter = new ImageBasedListAdapter((Activity) itemView.getContext(),
                        data, "Web_Horizontal");
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadTestData(String typeID, String type_title,TextView cartText) {
        FirebaseApp app=new FirebaseCustomAuth().loadCustomFirebase(itemView.getContext(),"tablethuts-extra");
        RecyclerView recyclerView= Objects.requireNonNull(itemView).findViewById(R.id.recyclerView);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        ArrayList<HashMap<String,Object>> data=new ArrayList<>();
        HashMap <String,Object> temp=new HashMap<>();
        temp.put("medicine_type_id",typeID+";"+type_title);
        data.add(temp);
        SimpleRecycler adapter=new SimpleRecycler(data,R.layout.medicine_card_layout,
                itemView.getContext(),"Home",cartText);
        recyclerView.setAdapter(adapter);
        Query query;
        query = FirebaseDatabase.getInstance(app).getReference("Test")
                .orderByChild("category_id").equalTo(typeID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<ImageBasedModel> data=new ArrayList<>();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    ImageBasedModel model = snapshot.getValue(ImageBasedModel.class);
                    if (model != null) {
                        model.setID(snapshot.getKey());
                        data.add(model);
                    }
                }
                LinearLayoutManager gridManager =
                        new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(gridManager);
                ImageBasedListAdapter adapter = new ImageBasedListAdapter((Activity) itemView.getContext(), data, "Test_Horizontal");
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    void setInfoBox(HashMap<String, Object> data) {
        ImageView iconImage=itemView.findViewById(R.id.icon);
        TextView textViewTitle=itemView.findViewById(R.id.title);
        TextView textViewDesc=itemView.findViewById(R.id.Desc);
        TextView textViewAction=itemView.findViewById(R.id.action_text);
        String title= (String) data.get("type_title");
        textViewTitle.setText(title);
        String desc= (String) data.get("type_desc");
        if (desc!=null)
            textViewDesc.setText(desc);
        HashMap<String,String>img_data= (HashMap<String, String>) data.get("type_img");
        String img= img_data.get("type_img");
        String action=img_data.get("type_action");
        if (img!=null)
            Glide.with(itemView).load(img).into(iconImage);
        String actionBtText= (String) data.get("type_button");
        if (actionBtText!=null)
            textViewAction.setText(actionBtText);
    }

    void setAdFragment(HashMap<String, Object> data) {
        try{
            HashMap<String,Object>  img=
                    (HashMap<String,Object>) data.get("type_img");
            Log.e("HasMap","->"+img);
            ViewPager viewPager = itemView.findViewById(R.id.viewPager);
            SliderAdapter adapter;
            if (img==null) {
                adapter= new SliderAdapter(itemView.getContext(), viewPager, (ArrayList<String>) data.get("type_img"));
            }else{HashMap<String,String>map=new HashMap<>();
                ArrayList<String>i=new ArrayList<>();
                for (String key:img.keySet()){
                    HashMap<String,String> temp= (HashMap<String, String>) img.get(key);
                    String image=temp.get("type_img");
                    i.add(image);
                    map.put(image,(String) data.get("type_action"));
                }
                if (i.size()>1) {
                    adapter = new SliderAdapter(itemView.getContext(), viewPager, i, map);
                    viewPager.setClipToPadding(false);
                    viewPager.setPageMargin(dpToPx(15,itemView.getContext()));
                    viewPager.setAdapter(adapter);
                }else{
                    viewPager.setVisibility(View.GONE);
                    itemView.findViewById(R.id.imageCard).setVisibility(View.VISIBLE);
                    Glide.with(itemView).load(i.get(0)).into((ImageView)itemView.findViewById(R.id.imageView));
                }
            }


        }catch (Exception e){e.printStackTrace();}
    }

    void setContainer(HashMap<String, Object> data, TextView cartText) {
        String title= (String) data.get("type_title");
        String id= (String) data.get("type_id");
        String typeName= (String) data.get("type_name");
        TextView textViewTitle=itemView.findViewById(R.id.title);
        textViewTitle.setText(title);
        if (typeName.contains("test"))
        {
            loadTestData(id,title,cartText);
            openCategory(id,title,"All_Test");
        }
        else {
            loadWebData(id,title);
            openCategory(id,title,"Web Articles");
        }
    }

    private void openCategory(String id, String title, String type) {
        itemView.findViewById(R.id.actionButton).setOnClickListener(v -> {
            Intent intent=new Intent(itemView.getContext(), ExtraOptionsActivity.class);
            intent.putExtra("FROM",type);
            intent.putExtra("CID",id);
            intent.putExtra("CNAME",title);
            itemView.getContext().startActivity(intent);
        });
    }
}
