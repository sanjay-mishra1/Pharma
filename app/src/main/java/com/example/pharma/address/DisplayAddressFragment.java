package com.example.pharma.address;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.pharma.Constants;
import com.example.pharma.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DisplayAddressFragment extends Fragment {
    private TextView textView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_display_address, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("Address", Context.MODE_PRIVATE);
        textView=getView().findViewById(R.id.location_city);
        if (sharedPreferences==null){
            fetchUserAddress();
        }else{
            setAddress(sharedPreferences.getString("Pincode_Name",null)==null?
                            sharedPreferences.getString("Address_Text",null)+"=>get"
                    :sharedPreferences.getString("Pincode_Name",null)
                    ,false);
        }
        textView.setOnClickListener(v -> {
            MyAddressFragment fragment = new MyAddressFragment(textView);
            fragment.showNow(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "Creating New Category");
        });
    }


    private void fetchUserAddress() {
        Log.e("Address","Loading address from db");
        FirebaseDatabase.getInstance().getReference("Customers").child(Constants.uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               String address= (String) dataSnapshot.child("address").getValue();
               Log.e("Address","address from db "+address);
               setAddress(address, true);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setAddress(String address, boolean save) {
        Log.e("Address","Address  "+address);
        if (address==null)
            textView.setText(R.string.enter_address);
        else{
            if (save ) {
                String pincode = address.substring(address.indexOf("Pincode :")).replaceAll("\\D+", "").trim();
                textView.setText(pincode);
                SharedPreferences sharedPreferences=getActivity().getSharedPreferences("Address", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("Address_Text",address);
                editor.putString("Pincode_Name",pincode);
                editor.apply();
                getPinCodeName(textView,pincode);

            }else{
                if (address.contains("=>get")) {
                    String add=address.split("=>")[0];
                    String pincode = add.substring(add.indexOf("Pincode :")).replaceAll("\\D+", "").trim();
                    textView.setText(pincode);
                    getPinCodeName(textView,pincode);
                }else textView.setText(address);
            }
        }
    }

    private void getPinCodeName(TextView textView,String pincode) {
       try {
           String pincodeApi = "https://api.postalpincode.in/pincode/";
           RequestQueue volley = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));

           JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, pincodeApi + pincode, null, response -> {
               Log.e("Volley", "Response " + response);
               checkPincodeResponse(response, textView);
           }, error -> {
               error.printStackTrace();
               Log.e("Volley", "Checksum not received error occur " + error.toString());
           });
           volley.add(jsonArrayRequest);
       }catch (Exception e){e.printStackTrace();}
    }
    private void checkPincodeResponse(JSONArray response,TextView addressText) {
        try {
            if (response.getJSONObject(0).get("Status").toString().equals("Error"))
                Log.e("Volley", "pincode wrong received error occur ");
            else {
                JSONObject jsonObject=response.getJSONObject(0).getJSONArray("PostOffice").getJSONObject(0);
                String name=jsonObject.getString("Name");
                addressText.setText(String.format("%s", name));
                SharedPreferences sharedPreferences=getActivity().getSharedPreferences("Address", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("Pincode_Name",name);
                editor.apply();

            }
        } catch (JSONException e) {
            Log.e("Volley", "pincode wrong received error occur ");
            e.printStackTrace();
        }
    }

}
