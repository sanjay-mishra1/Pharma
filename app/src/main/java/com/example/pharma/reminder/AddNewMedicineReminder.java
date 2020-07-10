package com.example.pharma.reminder;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.TextView;

import com.example.pharma.R;
import com.example.pharma.firebase.FirebaseCustomAuth;
import com.example.pharma.recycler.SearchAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AddNewMedicineReminder extends BottomSheetDialogFragment {
    private TextView textView;
    private String slot;
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
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL,R.style.CustomBottomSheetTheme);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_medicine_reminder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       try {slot=getArguments().getString("slot");
           textView = view.findViewById(R.id.listenerText);
           searchResultCard=view.findViewById(R.id.search_result_card);
           progressbar=view.findViewById(R.id.search_progressbar);
           midList=new ArrayList<>();
           app = new FirebaseCustomAuth().loadCustomFirebase
                   (getContext(), "tablethuts-search");
           initializeMedSearch();
           EditText searchEdit = getView().findViewById(R.id.editSearch);
           loadSearching(searchEdit, "return");
           setListener();
       }catch (Exception e){e.printStackTrace();
       showErrorSnackBar("An error occurred");}
    }

    private void setListener() {
        textView.addTextChangedListener(new TextWatcher() {     @Override     public void beforeTextChanged(CharSequence s, int start, int count, int after) {            }          @Override            public void onTextChanged(CharSequence s, int start, int before, int count) {            }            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().startsWith(getString(R.string.search_prefix))){
                    String medName=textView.getText().toString().trim().replace(getString(R.string.search_prefix),"").trim();
                    SharedPreferences preferences=getActivity().getSharedPreferences(getString(R.string.reminder_pref_name), Context.MODE_PRIVATE);
                    String medSlots =  preferences.getString(medName,"");
                   try {
                       if (medName.isEmpty()){
                           showErrorSnackBar("Please select medicine to add the reminder");
                       }
                       if (medSlots.contains(slot))
                       {   textView.setText("");
                           showErrorSnackBar("Medicine already exist");
                       }else{
                           BottomSheetDialogFragment fragment=new DisplayReminder(getDialog());
                           Bundle bundle=new Bundle();
                           bundle.putString("med_name",medName);
                           bundle.putString("slot",slot);
                           fragment.setArguments(bundle);
                           fragment.showNow(getActivity().getSupportFragmentManager(),"add_med");

                       }
                   }catch (Exception e){
                       e.printStackTrace();
                       showErrorSnackBar("An error occurred");
                   }

                    Log.e("AddReminder","Adding...");
                }
            }
        });
        textView.setOnClickListener(v -> textView.setText(String.format("%s %s", getString(R.string.search_prefix), textView.getText().toString().replace("Add","").trim())));

    }

    private void showErrorSnackBar(String msg){
        Snackbar snackbar=Snackbar.make(Objects.requireNonNull(getView()). findViewById(R.id.main_view),msg,Snackbar.LENGTH_SHORT);
        snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_FADE);
        snackbar.setBackgroundTint(getResources().getColor(R.color.red));
        snackbar.setTextColor(getResources().getColor(R.color.white));
        snackbar.show();
    }
    private void initializeMedSearch(){
        input_finish_checker= () -> {
            if (System.currentTimeMillis()>(last_text_edit+delay-500)){
                finalSearchMedicine(searchKey,adapter,medData);
            }
        };
    }

    private void loadSearching(EditText searchEdit, String from) {
        medData=new ArrayList<>();
        adapter=new SearchAdapter(getContext(),medData,from,textView);
        Log.e("LoadSearch","Edit...");
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
                    textView.setText(String.format("Add %s", searchKey));
                    textView.setVisibility(View.VISIBLE);
                }else{
                    textView.setText("");
                    textView.setVisibility(View.INVISIBLE);
                    searchResultCard.setVisibility(View.GONE);
                    medData.clear();
                    adapter.notifyDataSetChanged();

                }
            }
        });

    }

    private void finalSearchMedicine(String key, SearchAdapter adapter, ArrayList<HashMap<String,Object>> medData) {
        resetView();
        Log.e("SearchMed","Initiated...");
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
//                                mKey= mKey.split("__")[0];
//                                midList.add(mKey);
//                                map.put("medicine_key",mKey);
//                                medData.add(map);
//                                adapter.notifyItemInserted(medData.size()-1);
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
