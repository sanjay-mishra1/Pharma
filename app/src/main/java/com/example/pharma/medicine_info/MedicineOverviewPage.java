package com.example.pharma.medicine_info;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pharma.R;
import com.example.pharma.model.MedicineModel;
import com.example.pharma.view_image_layout;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MedicineOverviewPage extends Fragment {


    private View view;
    private String currentWeight;
    private MedicineModel.MedicineModelForRealtime realtimeModel;
    private MedicineModel model;
    private ArrayList<String> listOfImage;
    private int variantIndex;
    private HashMap receivedMapFromFragment;
    private MaterialButton previousVariantButton;
    private MaterialButton currentVariantButton;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {  view=inflater.inflate(R.layout.fragment_medicine_overview_admin, container, false);
        view.findViewById(R.id.addToDisable).setOnClickListener(v -> {
        });


        return view;
    }





    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        receiveData();
        configureVariants();

    }

    private void configureVariants() {
    }


    private void receiveData() {
        model= (MedicineModel) requireArguments().getSerializable("Firestore");
        loadName();
        loadDisease();
        loadDataFromFirebase(model.getMedicine_weight());
    }


    private void loadDisease() {
        try {
            if (Objects.requireNonNull(model).getMedicine_disease()!=null) {
                TextView disease=view.findViewById(R.id.disease);
                String diseaseTxt = model.getMedicine_disease().toString();
                diseaseTxt = diseaseTxt.replace("[", "");
                diseaseTxt = diseaseTxt.replace("]", "");
                disease.setText(diseaseTxt);
            }
        }catch (Exception e){e.printStackTrace();}
    }

    private  void loadName(){
        try {
            TextView name= requireView().findViewById(R.id.medicineName);
            name.setText(Objects.requireNonNull(model).getMedicine_name());
        }catch (Exception e){e.printStackTrace();}
        try {
            TextView companyName = requireView().findViewById(R.id.companyName);
            companyName.setText(String.format("By %s", Objects.requireNonNull(model).getMedicine_company()));
        }catch (Exception e){e.printStackTrace();}
        try {
            TextView discount=view.findViewById(R.id.priceDiscount);
            TextView originalPrice=view.findViewById(R.id.medicineOriginalPrice);
            if (Objects.requireNonNull(model).getMedicine_original_price()==0 || model.getMedicine_original_price()==model.getMedicine_price())
            {originalPrice.setVisibility(View.GONE);
                discount.setVisibility(View.GONE);
            }else {
                originalPrice.setText(String.format(Locale.UK, "₹ %d", Objects.requireNonNull(model).getMedicine_original_price()));
                originalPrice.setPaintFlags(originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                int dis = (int) (((model.getMedicine_original_price() - model.getMedicine_price()) * 100) / model.getMedicine_original_price());
                discount.setText(String.format(Locale.UK, "%d%% OFF", dis));
            }
        }catch (Exception e){e.printStackTrace();}
        try {
            TextView price= requireView().findViewById(R.id.medicinePrice);
            price.setText(String.format(Locale.UK,"₹ %d", Objects.requireNonNull(model).getMedicine_price()));
        }catch (Exception e){e.printStackTrace();}
        loadPrescription();
        try {
            TextView weight=view.findViewById(R.id.medicineType);
            weight.setText(Objects.requireNonNull(model).getMedicine_type());
        }catch (Exception e){e.printStackTrace();}
    }

    private void loadPrescription() {
        try {
            TextView prescription=view.findViewById(R.id.prescriptionNeeded);
            if (model != null && model.isPrescription_needed())
                prescription.setText(R.string.need_pres);
            else prescription.setText(R.string.not_pres);
        }catch (Exception e){e.printStackTrace();}
    }

    private void loadDataFromFirebase(String weight) {
         realtimeModel =
                (MedicineModel.MedicineModelForRealtime) requireArguments().getSerializable("Realtime");
        String order_st = String.valueOf(Objects.requireNonNull(realtimeModel).getMedicine_order());
        try {
            ArrayList<HashMap<String, Object>> weight_st;
            weight_st=(realtimeModel.getMedicine_variant());
            currentWeight=weight;
            loadWeight(weight_st);
        } catch (Exception e) {
            e.printStackTrace();
        }
        imagePager(realtimeModel.getMedicine_images());
    }


    private void loadWeight(ArrayList<HashMap<String, Object>> variant) {
        Log.e("Weight","->"+variant);
        final String[] previousWeight = {currentWeight};
        TextView price= requireView().findViewById(R.id.medicinePrice);
        TextView discount=view.findViewById(R.id.priceDiscount);
        TextView originalPrice=view.findViewById(R.id.medicineOriginalPrice);
        MaterialButton[] button =new MaterialButton[] {getView().findViewById(R.id.variant1),getView().findViewById(R.id.variant2),getView().findViewById(R.id.variant3),getView().findViewById(R.id.variant4),getView().findViewById(R.id.variant5),getView().findViewById(R.id.variant6),getView().findViewById(R.id.variant7),getView().findViewById(R.id.variant8),getView().findViewById(R.id.variant9),getView().findViewById(R.id.variant10),getView().findViewById(R.id.variant11)};
        if (currentVariantButton==null)
        currentVariantButton = button[0];
        previousVariantButton = null;
        for (int position=0;position<variant.size();position++) {
            HashMap<String, Object> data = variant.get(position);
            String w = (String) data.get("medicine_weight");
            changeVariantColor(button[position],w,position);
            button[position].setVisibility(View.VISIBLE);
            int finalPosition = position;
            int finalPosition1 = position;
            button[position].setOnClickListener(v -> {
                if (v.getId()==R.id.variant1)
                    button[0].setIcon(getResources().getDrawable(R.drawable.ic_home_white_24dp));
                else button[0].setIcon(getResources().getDrawable(R.drawable.ic_home_blue_24dp));
                long originalPriceNumber= (long) variant.get(finalPosition).get("medicine_original_price");
                long priceNumber= (long) variant.get(finalPosition).get("medicine_price");
                previousWeight[0] =currentWeight;
                currentWeight= (String) variant.get(finalPosition).get("medicine_weight");
                price.setText(String.format(Locale.UK,"₹ %d", priceNumber));
                previousVariantButton = currentVariantButton;
                currentVariantButton = button[finalPosition];
                if (previousVariantButton !=null && previousWeight[0]!=null)
                    changeVariantColor(previousVariantButton, previousWeight[0], finalPosition1);
                changeVariantColor(currentVariantButton,currentWeight, finalPosition1);
                if (originalPriceNumber != priceNumber) {
                    originalPrice.setText(String.format(Locale.UK,"₹ %d", originalPriceNumber));
                    originalPrice.setPaintFlags(originalPrice.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
                    int dis = (int) ((((originalPriceNumber-priceNumber)* 100) / originalPriceNumber) );
                    discount.setText(String.format(Locale.UK,"%d%% OFF", dis));
                }
            });

        }

    }
    private void changeVariantColor(MaterialButton button, String medicineWeight, int position){
     try {
         if (Objects.requireNonNull(medicineWeight).equalsIgnoreCase(String.valueOf(currentWeight))) {
             //Activated
             button.setBackgroundColor(requireContext().getResources().getColor(R.color.colorPrimaryDark));
             button.setText( medicineWeight);
             variantIndex=position;
             button.setTextColor(getContext().getResources().getColor(R.color.white));
         } else {//unactivated
             button.setBackgroundColor(requireContext().getResources().getColor(R.color.white));
             button.setTextColor(getContext().getResources().getColor(R.color.colorPrimaryDark));
             button.setText( medicineWeight);
             Log.e("Position","->"+position);
         }
     }catch (Exception e){e.printStackTrace();}
    }
    private  void imagePager(ArrayList<String> uri){
        listOfImage=new ArrayList<>(uri);
        if (uri.size() > 0) {
            Fragment fragmentClass = new ImageSliderFragment();
            FragmentTransaction fragmentTransaction2;
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("DATA", uri);
            fragmentClass.setArguments(bundle);

            try {
                fragmentTransaction2 = requireActivity().getSupportFragmentManager()
                        .beginTransaction();
                fragmentTransaction2.replace(R.id.frameOverview, Objects.requireNonNull(fragmentClass));
                fragmentTransaction2.commit();

            requireView().findViewById(R.id.fullscreen).setOnClickListener(v -> {
                Intent intent=new Intent(getActivity(), view_image_layout.class);
                intent.putExtra("DATA",uri);
                startActivity(intent);
            });
            } catch (Exception ignored) {
            }
        }
    }
}
