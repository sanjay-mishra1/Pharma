package com.example.pharma.search;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.pharma.R;
import com.example.pharma.firebase.FirebaseCustomAuth;
import com.example.pharma.recycler.SearchAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SearchFragment extends Fragment {
    private View searchResultCard;
    private ArrayList<HashMap<String,Object>> medData;
    private String searchKey="";
    private SearchAdapter adapter;
    private ProgressBar progressbar;
    private FirebaseApp app;
    private ArrayList<Object> midList;
    private Handler handler=new Handler();
    private long delay=1000;
    private long last_text_edit=0;
    private Runnable input_finish_checker;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.search_layout, container, false);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchResultCard=view.findViewById(R.id.search_result_card);
        progressbar=view.findViewById(R.id.search_progressbar);
        midList=new ArrayList<>();
        String tag=getTag();
        Log.e("SearchMedFrag","Tag->"+tag+"->"+getTag());
        if ( tag!=null){
            EditText searchEdit=getView().findViewById(R.id.editSearch);
            String from=null;
            if (tag.contains("search")) {
                app = new FirebaseCustomAuth().loadCustomFirebase
                        (getContext(), "tablethuts-search");
                initializeMedSearch();
            }else if (tag.contains("test")){
                searchEdit.setHint("Search Test and Category");
                app = new FirebaseCustomAuth().loadCustomFirebase
                        (getContext(), "tablethuts-extra");
                initializeOtherSearch("Test");
                from="test";
            }else if (tag.contains("web")){
                searchEdit.setHint("Search Web and Category");
                app = new FirebaseCustomAuth().loadCustomFirebase
                        (getContext(), "tablethuts-extra");
                from="web";
                initializeOtherSearch("Web Article");
            }
            loadSearching(searchEdit,from);
        }
    }

    private void initializeOtherSearch(String dbName) {
        input_finish_checker= () -> {
            if (System.currentTimeMillis()>(last_text_edit+delay-500)){
                searchOther(searchKey,adapter,medData,dbName);
            }
        };
    }

    private void searchOther(String searchKey, SearchAdapter adapter, ArrayList<HashMap<String, Object>> medData, String dbname) {
        Log.e("DbName","->"+dbname);
        resetView();
        midList.clear();
        FirebaseDatabase.getInstance(app).getReference(dbname)
                .orderByChild("name")
                .startAt(searchKey)
                .endAt(searchKey+"\uf8ff")
                .limitToFirst(5)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressbar.setVisibility(View.GONE);
                if (dataSnapshot.getChildrenCount()>0)
                    searchResultCard.setVisibility(View.VISIBLE);
//                else searchResultCard.setVisibility(View.GONE);

                for (DataSnapshot d:dataSnapshot.getChildren())
                {   if (!midList.contains(d.getKey())) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("id", d.getKey());
                    map.put("name", d.child("name").getValue());
                    map.put("medicine_img", d.child("icon").getValue());
                    map.put("main", true);
                    map.put("url", d.child("url").getValue());
                    medData.add(map);
                    adapter.notifyDataSetChanged();
                }
                    Log.e("Items","->"+medData);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        FirebaseDatabase.getInstance(app).getReference(dbname+" Category")
                .orderByChild("name")
                .startAt(searchKey)
                .endAt(searchKey+"\uf8ff")
                .limitToFirst(5)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        progressbar.setVisibility(View.GONE);
                        if (dataSnapshot.getChildrenCount()>0)
                            searchResultCard.setVisibility(View.VISIBLE);
//                        else searchResultCard.setVisibility(View.GONE);
                        for (DataSnapshot d:dataSnapshot.getChildren())
                        {   if (!midList.contains(d.getKey())) {
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("id", d.getKey());
                            map.put("name", d.child("name").getValue());
                            map.put("medicine_img", d.child("icon").getValue());
                            medData.add(map);
                            adapter.notifyDataSetChanged();
                        }
                        }
                        Log.e("cate","->"+medData);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }


    private void initializeMedSearch(){
        input_finish_checker= () -> {
            if (System.currentTimeMillis()>(last_text_edit+delay-500)){
                finalSearchMedicine(searchKey,adapter,medData);
            }
        };
    }

    private void loadSearching(EditText searchEdit,String from) {

        medData=new ArrayList<>();
        adapter=new SearchAdapter(getContext(),medData,from);
        RecyclerView recyclerView= getView().findViewById(R.id.recycle);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
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

    private void finalSearchMedicine(String key, SearchAdapter adapter, ArrayList<HashMap<String,Object>> medData) {
        resetView();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance(app).getReference();
        FirebaseDatabase.getInstance(app)
                .getReference().orderByChild("medicine_index").startAt(key)
                .endAt(key+"\uf8ff")
                .limitToFirst(5).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                medData.clear();
                midList.clear();
                progressbar.setVisibility(View.GONE);
                try {if (dataSnapshot.getChildrenCount()>0)
                    searchResultCard.setVisibility(View.VISIBLE);
                else searchResultCard.setVisibility(View.GONE);
                    for(DataSnapshot dataSnapshotValue:dataSnapshot.getChildren()) {
                        HashMap<String, Object> map = new HashMap<>();
                        String mKey= dataSnapshotValue.getKey();
                        Log.e("MedKey","->"+mKey);
                        String mid=mKey.split("_")[0];//= (String) dataSnapshotValue.child("medicine_id").getValue();
                        if (!midList.contains(mid) && !midList.contains(mKey)) {
                            Log.e("SearchData","=>"+dataSnapshotValue.getValue());
                            if (mKey.lastIndexOf("__")!=-1)
                            {//that means it is a company not medicine
                                mKey= mKey.split("__")[0];
                                midList.add(mKey);
                                map.put("medicine_key",mKey);
                                medData.add(map);
                                adapter.notifyItemInserted(medData.size()-1);
                            }
                            else{
                                midList.add(mid);//that means it is a medicine not company
                                databaseReference.child(mid).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshotValue) {
                                        map.put("medicine_img", dataSnapshotValue.child("medicine_img").getValue());
                                        map.put("medicine_name", dataSnapshotValue.child("medicine_index").getValue());
                                        map.put("medicine_company", dataSnapshotValue.child("medicine_company").getValue());
                                        map.put("medicine_id", mid);
//                                        map.put("medicine_key", mKey);
                                        medData.add(map);
                                        adapter.notifyDataSetChanged();
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                            }
                        }
                    }
                }catch (Exception e){e.printStackTrace();}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void resetView() {
        medData.clear();
        adapter.notifyDataSetChanged();
        searchResultCard.setVisibility(View.VISIBLE);
        progressbar.setVisibility(View.VISIBLE);
    }

}
