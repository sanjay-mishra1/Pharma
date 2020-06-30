package com.example.pharma.medicine;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.pharma.R;
import com.example.pharma.firebase.FirebaseCustomAuth;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AllMedicineSortingActivity extends AppCompatActivity {
    View visible;
    //    private String data_tag="@/data/@";
//    private String value_tag="@/value/@";
    HashMap<String ,Object> queryData;
    private RadioGroup  priceField;
    private LinearLayout  stockField;
    private RadioGroup  companyField;
    private RadioGroup  diseaseField;
    private RadioGroup  typeField;
    private LinearLayout  prescriptionField;
    private RadioGroup  categoryField;
//    private ArrayList<String> companyList;
//    private ArrayList<String> typeList;
//    private ArrayList<String> diseaseList;
    private View searchView;
//    private Query query;
    private String key;
    private String value;
    private long MAX_PRICE;
    private long MIN_PRICE;
    private TextView priceTitleTextView;
    private TextView typeTitleTextView;
    private TextView presTitleTextView;
    private TextView disTitleTextView;
    private TextView catTitleTextView;
    private TextView companyTitleTextView;
    private TextView visibleTextView;
    private View visibleClickView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medicine_sorting_bottom_sheet);
//        queryData=new HashMap<>();
        searchView=findViewById(R.id.appbar);
        searchView.setBackgroundColor(Color.TRANSPARENT);
        //priceList=new ArrayList<>();
        initializeScreenVariables();
        visible=priceField;
        receive();
        //String key="1590474559613";
    }

    private void receive() {
        try {
            Intent intent = getIntent();
            key = intent.getStringExtra("key");
            value = intent.getStringExtra("value");
            MAX_PRICE = intent.getLongExtra("max_price", 0);
            MIN_PRICE = intent.getLongExtra("min_price", 0);
            queryData = (HashMap<String, Object>) intent.getSerializableExtra("DATA");
            Log.e("Prices", "->" + MIN_PRICE + "->" + MAX_PRICE);
            setPriceField();
            FirebaseApp app = new FirebaseCustomAuth().loadCustomFirebase(this, "tablethuts-extra");
            FirebaseFirestore firestore = FirebaseFirestore.getInstance(app);
            String extra_key = key.split("_")[1].toLowerCase();
            extra_key = extra_key.substring(0, 1).toUpperCase() + extra_key.substring(1).trim();
            Log.e("Key", "Filtered->" + extra_key);

            getDataFromDb(firestore.collection(extra_key).document(value), app);
            Log.e("Pres Received", "->" + queryData.get("prescription_needed"));
            if (queryData.get("prescription_needed") != null) {
                boolean pres = Boolean.parseBoolean((String) queryData.get("prescription_needed"));
                TextView textView = findViewById(R.id.prescriptionText);
                textView.setVisibility(View.VISIBLE);
                if (pres) {
                    textView.setText(R.string.prescriptionNeeded);
                    CheckBox checkBox = findViewById(R.id.presNeeded);
                    checkBox.setChecked(true);
                } else {
                    textView.setText(R.string.no_prescription_needed);
                    CheckBox checkBox = findViewById(R.id.presNotNeeded);
                    checkBox.setChecked(true);
                }
            }
        }catch (Exception e){
            findViewById(R.id.cardview).setVisibility(View.GONE);
            Snackbar snackbar= Snackbar.make(findViewById(R.id.cardview),"No filters found", Snackbar.LENGTH_LONG);
            snackbar.setTextColor(getResources().getColor(R.color.white));
            snackbar.setBackgroundTint(getResources().getColor(R.color.colorPrimaryDark));
            snackbar.show();
            Handler handler=new Handler();
            handler.postDelayed(() -> {
                snackbar.dismiss();
                finish();
            },2000);
        }
    }

    private void initializeFilter() {
        if (key.contains("category") && queryData.get("medicine_disease")==null)
            queryData.put("medicine_disease","All");
        if (queryData.get("medicine_price") ==null)
        queryData.put("medicine_price",new long[]{MIN_PRICE,MAX_PRICE});
    }

    void getDataFromDb(DocumentReference query, FirebaseApp app){
        query.get().addOnSuccessListener(documentSnapshot -> {
            try {
                if(documentSnapshot.exists()) {
                    findViewById(R.id.progressbar).setVisibility(View.GONE);
                   ArrayList<String> companyList = (ArrayList<String>) documentSnapshot.get("medicine_company");
                   ArrayList<String> diseaseList = (ArrayList<String>) documentSnapshot.get("medicine_disease");
                   ArrayList<String> typeList = (ArrayList<String>) documentSnapshot.get("medicine_type");
                    Log.e("company","->"+companyList);
                    Log.e("disease","->"+diseaseList);
                    Log.e("type","->"+typeList);
//                    if (!key.contains("disease"))
//                    setDiseaseView();
//                    else diseaseField.setVisibility(View.GONE);
                    if (key.contains("category"))
                    {setCheckBoxCompanyView(companyList);
                     setRadioDiseaseView(Objects.requireNonNull(diseaseList));
                     setRadioTypeView(Objects.requireNonNull(typeList));
                     findViewById(R.id.filter_three).setVisibility(View.GONE);
                    }else if (key.contains("disease")){
                        fetchCategoryData((ArrayList<String>) Objects.requireNonNull(documentSnapshot.get("medicine_category")),app);
                        setRadioCompanyView(Objects.requireNonNull(companyList));
                        setCheckBoxTypeView(typeList);
                        findViewById(R.id.filter_five).setVisibility(View.GONE);
                    }
                    else if (key.contains("company")){
                        findViewById(R.id.filter_four).setVisibility(View.GONE);
                        setCheckBoxDiseaseView(diseaseList);
                        setRadioTypeView(Objects.requireNonNull(typeList));
                        fetchCategoryData((ArrayList<String>) Objects.requireNonNull(documentSnapshot.get("medicine_category")),app);
                    }
//                    if (!key.contains("disease"))
//                    {   setCheckBoxCompanyView();
//                        setRadioTypeView();
//                    }
//                    else{   setRadioCompanyView();
//                            setCheckBoxTypeView();
//                    }



                }else Log.e("Fetching Data","Field not exist k->"+key+" v->"+value);
            }catch (Exception e){e.printStackTrace();Log.e("Fetching Data","Field not exist k->"+key+" v->"+value);}
        });
    }

    private void fetchCategoryData(ArrayList<String> medicine_category, FirebaseApp app) {
       DatabaseReference databaseReference= FirebaseDatabase.getInstance(app).getReference("Category");
       HashMap<String,String>categoryMap=new HashMap<>();
        final int[] count = {medicine_category.size()};
       for (String catid:medicine_category){
        databaseReference.child(catid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                count[0]--;
               if (dataSnapshot.exists()){
                   categoryMap.put(catid, (String) dataSnapshot.child("category_name").getValue());
               }
              try {
                  if (count[0]==0)
                      setCategoryView(categoryMap);
              }catch (Exception e){e.printStackTrace();}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
       }

    }

    private void setCategoryView(HashMap<String,String> data) {
        TextView leftText=findViewById(R.id.categoryText);
        String previousCatId= (String) queryData.get("medicine_category");
        Object[] catIDs= data.keySet().toArray();
        Log.e("Catids","->"+data.keySet());
        for (String key:data.keySet()){
            RadioButton radioButton=new RadioButton(this);
            radioButton.setText(data.get(key));
            radioButton.setTextSize(16);
            radioButton.setPadding(0,30,0,30);
            categoryField.addView(radioButton);
            if (key.equals(previousCatId))
            {   leftText.setVisibility(View.VISIBLE);
                leftText.setText(data.get(key));
                radioButton.setChecked(true);
            }
        }
        categoryField.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton1=findViewById(checkedId);
            String value=radioButton1.getText().toString().trim();
            leftText.setText(value);
            leftText.setVisibility(View.VISIBLE);
            queryData.put("medicine_category", catIDs[group.indexOfChild(radioButton1)]);
        });
    }


    private void setPriceField() {
        long min=MIN_PRICE;//priceList.get(0);
        long max=MAX_PRICE;//priceList.get(1);
        int diff= (int) (max-min);
      try {
          String[] loop = getMinDivisions(diff, min, max);
          long[] price_received = (long[]) queryData.get("medicine_price");
          Log.e("Price Received","->"+ Arrays.toString(price_received));
          TextView leftText = findViewById(R.id.priceText);
          for (String price : loop) {
              RadioButton radioButton = new RadioButton(this);
              radioButton.setText(price);
              radioButton.setTextSize(16);
              radioButton.setPadding(0, 30, 0, 30);
              priceField.addView(radioButton);
              if (price_received != null) {
                  if (price.contains(String.valueOf(price_received[0])) ||
                          price.contains(String.valueOf(price_received[1]))
                          && price.contains(String.valueOf(price_received[0])) &&
                          price.contains(String.valueOf(price_received[1]))) {
                      leftText.setText(price.replace("Between", "").trim());
                      leftText.setVisibility(View.VISIBLE);
                      radioButton.setChecked(true);
                  }
              }
          }
          priceField.setOnCheckedChangeListener((group, checkedId) -> {
              try {
                  RadioButton radioButton1 = findViewById(checkedId);
                  Log.e("PriceRadio","->checked");
                  String[] values = radioButton1.getText().toString().split("₹ ");
                  long[] num = new long[]{min, max};
                  leftText.setText(radioButton1.getText().toString().replace("Between","").trim());
                  leftText.setVisibility(View.VISIBLE);
                  if (values.length == 2) {
                      num[0] = Long.parseLong(values[1].trim());
                  } else {
                      num[0] = Long.parseLong(values[1].substring(0, values[1].indexOf("To") - 1).trim());
                      num[1] = Long.parseLong(values[2].trim());
                  }
                  Log.e("Price", "->" + num[0] + num[1]);
                  queryData.put("medicine_price", num);
              } catch (Exception e) {
                  e.printStackTrace();
              }
          });
      }catch (Exception e){e.printStackTrace();}
    }
    private String[] getMinDivisions(int diff, long min, long max) {
        int div;
        if (diff<100) div=2;
       else if ( diff<300)
            div=3;
        else if (diff<500)
            div=4;
        else if ( diff<1000)//if (diff>5000 )
            div=5;
        else div=6;

        String[] data=new String[div];
        int part= (int) (max/div);
        int priceCounter= (int) min;
        for (int i=0;i<div;i++)
        {
            if (i==div-1)
                data[i]="Above ₹ "+(priceCounter+1);
            else{   if (i!=0)
                        data[i]="Between ₹ "+(priceCounter+1);
                    else data[i]="Between ₹ "+(priceCounter);
                priceCounter+=part;
                data[i]=data[i]+" To ₹ "+(priceCounter);
            }
        }
        return data;
    }

    private void setRadioCompanyView(ArrayList<String> companyList) {
        String company= (String) queryData.get("medicine_company");
        TextView leftText=findViewById(R.id.diseaseText);
        for (String key:companyList){
            RadioButton radioButton=new RadioButton(this);
            radioButton.setText(key);
            radioButton.setTextSize(16);
            radioButton.setPadding(0,30,0,30);
            companyField.addView(radioButton);
            if (key.equals(company))
            {   leftText.setVisibility(View.VISIBLE);
                leftText.setText(key);
                radioButton.setChecked(true);
            }

        }
        companyField.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton1=findViewById(checkedId);
            String value=radioButton1.getText().toString().trim();
            leftText.setText(value);
            leftText.setVisibility(View.VISIBLE);
            queryData.put("medicine_company",value);
        });
    }
    private void setCheckBoxCompanyView(ArrayList<String> companyList) {
        ArrayList<String>comp= (ArrayList<String>) queryData.get("medicine_company");
        TextView leftText=findViewById(R.id.companyText);
        if (comp!=null)
            leftText.setText(String.format(Locale.UK,"%d Selected", comp.size()));
        for (String key: companyList){
            CheckBox checkBox=new CheckBox(this);
            checkBox.setText(key);
            checkBox.setTextSize(16);
            checkBox.setPadding(0,30,0,30);
            companyField.addView(checkBox);
            if (comp!=null&& comp.contains(key))
            {   leftText.setVisibility(View.VISIBLE);
                checkBox.setChecked(true);
            }

            checkBox.setOnClickListener(v -> {
                CheckBox checkBox1 = (CheckBox) v;
                ArrayList<String> company= (ArrayList<String>) queryData.get("medicine_company");
                if (company==null){
                    company=new ArrayList<>();
                    company.add(checkBox1.getText().toString());
                }else if (checkBox1.isChecked())
                    company.add(checkBox1.getText().toString());
                else company.remove(checkBox1.getText().toString());
                queryData.put("medicine_company",company);
                if (company.size()==0)
                {   queryData.remove("medicine_company");
                    leftText.setVisibility(View.GONE);
                }
                else{
                    leftText.setVisibility(View.VISIBLE);
                    leftText.setText(String.format(Locale.UK,"%d Selected", company.size()));
                }
            });
        }
    }
    private void setCheckBoxDiseaseView(ArrayList<String> diseaseList) {
        ArrayList<String>medicine_disease=(ArrayList<String>) queryData.get("medicine_disease");
        TextView leftText=findViewById(R.id.diseaseText);
        if (medicine_disease!=null)
            leftText.setText(String.format(Locale.UK,"%d Selected", medicine_disease.size()));
        for (String key:diseaseList){
            CheckBox checkBox=new CheckBox(this);
            checkBox.setText(key);
            checkBox.setTextSize(16);
            checkBox.setPadding(0,30,0,30);
            diseaseField.addView(checkBox);
            if (medicine_disease!=null&& medicine_disease.contains(key))
            {   leftText.setVisibility(View.VISIBLE);
                checkBox.setChecked(true);
            }

            checkBox.setOnClickListener(v -> {
                CheckBox checkBox1 = (CheckBox) v;
                ArrayList<String> disease= (ArrayList<String>) queryData.get("medicine_disease");
                if (disease==null){
                    disease=new ArrayList<>();
                    disease.add(checkBox1.getText().toString());
                }else if (checkBox1.isChecked())
                    disease.add(checkBox1.getText().toString());
                else disease.remove(checkBox1.getText().toString());
                queryData.put("medicine_disease",disease);
                if (disease.size()==0)
                {   queryData.remove("medicine_disease");
                    leftText.setVisibility(View.GONE);
                }
                else{
                    leftText.setVisibility(View.VISIBLE);
                    leftText.setText(String.format(Locale.UK,"%d Selected", disease.size()));
                }
            });
        }
    }
    private void setRadioDiseaseView(ArrayList<String> diseaseList) {
        String disease= (String) queryData.get("medicine_disease");
        TextView leftText=findViewById(R.id.diseaseText);
        for (String key:diseaseList){
            RadioButton radioButton=new RadioButton(this);
            radioButton.setText(key);
            radioButton.setTextSize(16);
            radioButton.setPadding(0,30,0,30);
            diseaseField.addView(radioButton);
            if (key.equals(disease))
            {   leftText.setVisibility(View.VISIBLE);
                leftText.setText(key);
                radioButton.setChecked(true);
            }

        }
        diseaseField.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton1=findViewById(checkedId);
            String value=radioButton1.getText().toString().trim();
            leftText.setText(value);
            leftText.setVisibility(View.VISIBLE);
            queryData.put("medicine_disease",value);
        });
    }
    private void setCheckBoxTypeView(ArrayList<String> typeList) {
        ArrayList<String>comp= (ArrayList<String>) queryData.get("medicine_type");
        TextView leftText=findViewById(R.id.typeText);
        if (comp!=null)
            leftText.setText(String.format(Locale.UK,"%d Selected", comp.size()));
        for (String key:typeList){
            CheckBox checkBox=new CheckBox(this);
            checkBox.setText(key);
            checkBox.setTextSize(16);
            checkBox.setPadding(0,30,0,30);
            typeField.addView(checkBox);
            if (comp!=null&& comp.contains(key))
            {   leftText.setVisibility(View.VISIBLE);
                checkBox.setChecked(true);
            }

            checkBox.setOnClickListener(v -> {
                CheckBox checkBox1 = (CheckBox) v;
                ArrayList<String> type= (ArrayList<String>) queryData.get("medicine_type");
                if (type==null){
                    type=new ArrayList<>();
                    type.add(checkBox1.getText().toString());
                }else if (checkBox1.isChecked())
                    type.add(checkBox1.getText().toString());
                else type.remove(checkBox1.getText().toString());
                queryData.put("medicine_type",type);
                if (type.size()==0)
                {   queryData.remove("medicine_type");
                    leftText.setVisibility(View.GONE);
                }
                else{
                    leftText.setVisibility(View.VISIBLE);
                    leftText.setText(String.format(Locale.UK,"%d Selected", type.size()));
                }
            });
        }
    }
    private void setRadioTypeView(ArrayList<String> typeList) {
        String type= (String) queryData.get("medicine_type");
        TextView leftText=findViewById(R.id.typeText);
        for (String key:typeList){
            RadioButton radioButton=new RadioButton(this);
            radioButton.setText(key);
            radioButton.setTextSize(16);
            radioButton.setPadding(0,30,0,30);
            typeField.addView(radioButton);
            if (type!=null&& type.equals(key))
            {   leftText.setText(key);
                leftText.setVisibility(View.VISIBLE);
                radioButton.setChecked(true);
            }

        }
        typeField.setOnCheckedChangeListener((group, checkedId) -> {
            Log.e("Click","->");
            RadioButton radioButton1=group. findViewById(checkedId);
            String value=radioButton1.getText().toString().trim();
            leftText.setText(value);
            leftText.setVisibility(View.VISIBLE);
            queryData.put("medicine_type",value);
        });
    }
    private void initializeScreenVariables() {
        priceField=findViewById(R.id.priceField);
        stockField=findViewById(R.id.stockAvailableField);
        companyField=findViewById(R.id.companyField);
        diseaseField=findViewById(R.id.diseaseField);
        typeField=findViewById(R.id.typeField);
        prescriptionField=findViewById(R.id.prescriptionField);
        categoryField=findViewById(R.id.categoryField);
        priceTitleTextView=findViewById(R.id.filter_two_title);
        typeTitleTextView=findViewById(R.id.type_title);
        companyTitleTextView=findViewById(R.id.company_title);
        catTitleTextView=findViewById(R.id.category_title);
        disTitleTextView=findViewById(R.id.disease_title);
        presTitleTextView=findViewById(R.id.prescription_title);
        priceField.setVisibility(View.VISIBLE);
        visibleTextView=priceTitleTextView;
        visibleClickView=findViewById(R.id.filter_two);
    }

    public void cancelOnClick(View view) {
        onBackPressed();
    }
    void changeColor(View view,TextView newTextView){
        visibleClickView.setBackgroundColor(getResources().getColor(R.color.filter_background_color));
        visibleClickView=view;
        visibleClickView.setBackgroundColor(getResources().getColor(R.color.white));
        visibleTextView.setTextColor(getResources().getColor(R.color.black));
        visibleTextView=newTextView;
        visibleTextView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
    }
    public void priceOnClick(View view) {
        changeColor(view,priceTitleTextView);
        visible.setVisibility(View.GONE);
        visible=priceField;
        visible.setVisibility(View.VISIBLE);
    }

    public void diseaseOnClick(View view) {
        changeColor(view,disTitleTextView);
        visible.setVisibility(View.GONE);
        visible=diseaseField;
        visible.setVisibility(View.VISIBLE);

    }

    public void companyOnClick(View view) {
        changeColor(view,companyTitleTextView);
        visible.setVisibility(View.GONE);
        visible=companyField;
        visible.setVisibility(View.VISIBLE);

    }

    public void prescriptionOnClick(View view) {
        changeColor(view,presTitleTextView);
        visible.setVisibility(View.GONE);
        visible=prescriptionField;
        visible.setVisibility(View.VISIBLE);
    }

    public void typeOnClick(View view) {
        changeColor(view,typeTitleTextView);
        visible.setVisibility(View.GONE);
        visible=typeField;
        visible.setVisibility(View.VISIBLE);
            }


    public void clearAllOnClick(View view) {
//        Log.e("QueryData","->"+queryData);
        Intent intent=new Intent();
        queryData.clear();
        intent.putExtra("DATA",queryData);
        setResult(101,intent);
        onBackPressed();
    }
    public void applyChangesOnClick(View view) {
        initializeFilter();
        Intent intent=new Intent();
        intent.putExtra("DATA", queryData);
        setResult(101,intent);
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    public void stockOnClick(View view) {
    }

    public void categoryOnClick(View view) {
        changeColor(view,catTitleTextView);
        visible.setVisibility(View.GONE);
        visible=categoryField;
        visible.setVisibility(View.VISIBLE);
    }

    public void availabilityOnClick(View view) {
        visible.setVisibility(View.GONE);
        visible=stockField;
        visible.setVisibility(View.VISIBLE);
    }

    public void presNeededOnClick(View view) {
        CheckBox checkBox= (CheckBox) view;
        CheckBox checkBox1=findViewById(R.id.presNotNeeded);
        checkBox1.setChecked(false);
        TextView leftText=findViewById(R.id.prescriptionText);

        if (checkBox.isChecked())
        {   leftText.setVisibility(View.VISIBLE);
            leftText.setText(R.string.prescriptionNeeded);
            queryData.put("prescription_needed","true");
        }
        else {  leftText.setVisibility(View.GONE);
                queryData.remove("prescription_needed");
        }
    }

    public void presNotNeededOnClick(View view) {
        CheckBox checkBox= (CheckBox) view;
        CheckBox checkBox1=findViewById(R.id.presNeeded);
        checkBox1.setChecked(false);
        TextView leftText=findViewById(R.id.prescriptionText);

        if (checkBox.isChecked())
        {   leftText.setVisibility(View.VISIBLE);
            leftText.setText(R.string.no_prescription_needed);
            queryData.put("prescription_needed","false");
        }
        else{leftText.setVisibility(View.GONE);
            queryData.remove("prescription_needed");
        }
    }
}
