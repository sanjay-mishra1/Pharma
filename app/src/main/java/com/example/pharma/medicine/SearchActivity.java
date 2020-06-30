package com.example.pharma.medicine;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.example.pharma.Constants;
import com.example.pharma.R;
import com.example.pharma.firebase.FirebaseCustomAuth;
import com.example.pharma.imgeslider.ImageSliderFragment;
import com.example.pharma.recycler.SearchAdapter;
import com.example.pharma.recycler.SimpleRecycler;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SearchActivity extends AppCompatActivity {

    private ShimmerFrameLayout shimmer;
    private View searchResultCard;
    private ArrayList<HashMap<String,Object>> medData;
    private String searchKey="";
    private SearchAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_medicines);
        searchResultCard=findViewById(R.id.search_result_card);
        shimmer=findViewById(R.id.shimmer);

        loadAds();
        //loadSearching();
        loadMyMedDataFromFirestore();
    }
    Handler handler=new Handler();
    long delay=1000;
    long last_text_edit=0;
    private Runnable input_finish_checker= () -> {
        if (System.currentTimeMillis()>(last_text_edit+delay-500)){
            searchMedicine(searchKey,adapter,medData);
        }
    };

    private void loadSearching() {
        EditText searchEdit=findViewById(R.id.editSearch);
        medData=new ArrayList<>();
        adapter=new SearchAdapter(this,medData);
        RecyclerView recyclerView= findViewById(R.id.recycle);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(input_finish_checker);
            }

            @Override
            public void afterTextChanged(Editable s) {
//                if (!s.toString().trim().isEmpty()){
//                    searchMedicine(s.toString(),adapter,medData);
//                }else {
//                    searchResultCard.setVisibility(View.GONE);
//                    medData.clear();
//                    adapter.notifyDataSetChanged();
//                }
                if (s.length()>0){
                    searchKey=s.toString().trim();
                    last_text_edit=System.currentTimeMillis();
                    handler.postDelayed(input_finish_checker,delay);
                }else{
                    searchResultCard.setVisibility(View.GONE);
                    medData.clear();
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }

    private void loadAds(){
        loadContent(null);
        FirebaseApp app=new FirebaseCustomAuth().loadCustomFirebase(this,
                "TabletHuts-Extra");
        FirebaseDatabase.getInstance(app).getReference("UserScreen").child("SearchScreen")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<String > data=
                                (ArrayList<String>) dataSnapshot.getValue();
                        if (data!=null)
                            loadContent(data);
                        else {
                            Log.e("UserScreen","screen data is empty");
//                            findViewById(R.id.frame).setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadContent(ArrayList<String> data) {
//        ImageSliderFragment fragment=new ImageSliderFragment();
//        Bundle bundle=new Bundle();
//        bundle.putStringArrayList("DATA",data);
//        fragment.setArguments(bundle);
//        getSupportFragmentManager(fragment).beginTransaction();


        Fragment fragmentClass =new ImageSliderFragment();
        FragmentTransaction fragmentTransaction2;
        fragmentTransaction2= Objects.requireNonNull(this). getSupportFragmentManager().beginTransaction();
        Bundle bundle=new Bundle();
        ArrayList<String> images=new ArrayList<>();
        images.add("https://images.pexels.com/photos/806427/pexels-photo-806427.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940");
        images.add("https://images.pexels.com/photos/3652097/pexels-photo-3652097.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940");
        images.add("https://images.pexels.com/photos/208512/pexels-photo-208512.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940");
        images.add("https://images.pexels.com/photos/593451/pexels-photo-593451.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940");
        images.add("https://images.pexels.com/photos/606506/pexels-photo-606506.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940");
        try{bundle.putStringArrayList("DATA",images);
            fragmentClass.setArguments(bundle);
            fragmentTransaction2.replace(R.id.frame, Objects.requireNonNull(fragmentClass));
            fragmentTransaction2.commit();
        }catch (Exception ignored){}
    }


    private void searchMedicine(String key,SearchAdapter adapter,ArrayList<HashMap<String,Object>> medData) {
        medData.clear();
        adapter.notifyDataSetChanged();
        searchResultCard.setVisibility(View.INVISIBLE);
        FirebaseDatabase.getInstance(new FirebaseCustomAuth().loadCustomFirebase
                (this,"tablethuts-medicines"))
                .getReference("Medicines").orderByChild("medicine_name").startAt(key)
                .endAt(key+"\uf8ff").limitToFirst(5).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              try {if (dataSnapshot.getChildrenCount()>0)
                    searchResultCard.setVisibility(View.VISIBLE);
                  else searchResultCard.setVisibility(View.GONE);
                  for(DataSnapshot dataSnapshotValue:dataSnapshot.getChildren()) {
                      Log.e("SearchData","=>"+dataSnapshotValue.getValue());
                      HashMap<String, Object> map = new HashMap<>();
                      map.put("medicine_img",dataSnapshotValue != null ?
                              ((ArrayList<String>) dataSnapshotValue.child("medicine_images").getValue()).get(0) : "NA");
                      map.put("medicine_name", dataSnapshotValue != null ? dataSnapshotValue.child("medicine_name").getValue() : "NA");
                      map.put("medicine_weight", "");
                      map.put("medicine_id", dataSnapshotValue != null ? dataSnapshotValue.getKey() : "NA");
                      medData.add(map);
                  }
                  adapter.notifyDataSetChanged();
              }catch (Exception e){e.printStackTrace();}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadMyMedDataFromFirestore() {
        if (Constants.uid==null)
            Constants.uid=  getSharedPreferences("USER_CREDENTIALS",MODE_PRIVATE).getString("UID",null);
        SharedPreferences sharedPreferences=getSharedPreferences("MyMedicines",MODE_PRIVATE);
       // if (sharedPreferences.getBoolean("MedLoaded",false)){
            loadDataFromLocal(sharedPreferences);
        //}else
            {
            FirebaseFirestore.getInstance().collection("Customers")
                    .document(Constants.uid)
                    .get().addOnSuccessListener(documentSnapshot -> {
                ArrayList<String> data = (ArrayList<String>) documentSnapshot.get("medicines_list");
                if (data!=null) {
                    Log.e("Custom","custom="+data.toString());
                    try{
                        getMedicineData(data, true);
                    }catch (Exception e){e.printStackTrace();}
                }
                else {  Log.e("MedData","->"+documentSnapshot.getData());
                        loadEmptyCartUI();
                }

                    });
        }
    }

    private void loadEmptyCartUI() {
    }

    private void loadDataFromLocal(SharedPreferences sharedPreferences) {
        getMedicineData(new ArrayList<>(sharedPreferences.getAll().keySet()),
                false);

    }

    private void getMedicineData(ArrayList<String> medicine,boolean storeToLocal) {
        RecyclerView recyclerView= findViewById(R.id.myMedicineRecycler);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        ArrayList<HashMap<String,Object>> data=new ArrayList<>();
        SimpleRecycler adapter=new SimpleRecycler(data,R.layout.linear_medicine_card,this,"Custom", null);
        recyclerView.setAdapter(adapter);
        SharedPreferences preferences=getSharedPreferences("Favorite",MODE_PRIVATE);
        SharedPreferences preferencesCart=getSharedPreferences("Cart",MODE_PRIVATE);
        SharedPreferences.Editor edit=preferences.edit();
        Log.e("Custom","GetMedData=>"+medicine.toString());
        if (storeToLocal) edit.putBoolean("MedLoaded", true);
        FirebaseApp app= new FirebaseCustomAuth().loadCustomFirebase(this,"tablethuts-medicines");

        for(String id:medicine) {
            if (storeToLocal) {
                edit.putInt(id, 1);
                edit.apply();
            }
            if (!id.equalsIgnoreCase("medloaded")) {
                FirebaseDatabase.getInstance(app).getReference("Medicines").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        HashMap<String,Object> map=new HashMap<>();
                        try {
                            HashMap<String, Object> dataSnapshotValue = (HashMap<String, Object>) dataSnapshot.getValue();
                            map.put("medicine_img", dataSnapshotValue != null ? ((ArrayList<String>) dataSnapshotValue.get("medicine_images")).get(0) : "NA");
                            map.put("medicine_name", dataSnapshotValue != null ? dataSnapshotValue.get("medicine_name") : "NA");
                            map.put("medicine_id", dataSnapshotValue != null ? id : "NA");
                            map.put("medicine_in_my_fav", true);
                            map.put("medicine_max_quantity", dataSnapshotValue != null ? dataSnapshotValue.get("medicine_max_quantity") : "NA");
                            map.put("medicine_in_my_cart", preferencesCart.contains(id));
                            map.put("medicine_price", dataSnapshotValue != null ? dataSnapshotValue.get("medicine_price") : "NA");
                            map.put("medicine_original_price", ((HashMap<String,Object>) ((ArrayList<Object>)
                                    dataSnapshotValue.get("medicine_variant")).get(0)).get("medicine_original_price"));
                            data.add(map);
                            adapter.notifyDataSetChanged();
                            if (shimmer.isShimmerVisible())
                            {shimmer.stopShimmer();shimmer.setVisibility(View.GONE);
                            findViewById(R.id.medTitle).setVisibility(View.VISIBLE);
                            }
                        }catch (Exception e){e.printStackTrace();}
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }
}
