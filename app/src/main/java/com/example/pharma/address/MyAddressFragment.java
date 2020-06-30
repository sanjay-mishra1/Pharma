package com.example.pharma.address;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.pharma.CartActivity;
import com.example.pharma.Constants;
import com.example.pharma.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class MyAddressFragment  extends BottomSheetDialogFragment {

    private View root;

    public MyAddressFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL,R.style.CustomBottomSheetTheme);
        loadFirebaseForAddress();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         root=inflater.inflate(R.layout.fragment_my_address, container, false);;
        root.findViewById(R.id.addAddress).setOnClickListener(v -> {
            Intent intent=new Intent(getContext(),SelectAddressFromMapActivity.class);
            startActivity(intent);
        });
        return root;
    }
    private void loadFirebaseForAddress() {
        FirebaseDatabase.getInstance().getReference("Customers/"+ Constants.uid).child("Address").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                root.findViewById(R.id.progress_field).setVisibility(View.GONE);
                root.findViewById(R.id.address_field).setVisibility(View.VISIBLE);
               try {
                       RecyclerView listView = root.findViewById(R.id.address_list);
                       ArrayList<String> list = new ArrayList<>();
                       ArrayList<String> coordinate = new ArrayList<>();
                       ArrayList<String> keyList = new ArrayList<>();
                       for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                       {    keyList.add(dataSnapshot1.getKey());
                           list.add((String) dataSnapshot1.child("address_text").getValue());
                           coordinate.add(dataSnapshot1.child("address_lat").getValue()
                                   +","+dataSnapshot1.child("address_lng").getValue());
                       }
                       if (list.size() > 0) {
                           LinearLayoutManager mLayoutManager =
                                   new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                           AddressAdapter adapter=new AddressAdapter(list,coordinate,keyList);
                           listView.setLayoutManager(mLayoutManager);
                           listView.setAdapter(adapter);

                       }else root.findViewById(R.id.no_address).setVisibility(View.VISIBLE);

               }catch (Exception e){e.printStackTrace();}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    class AddressAdapter extends RecyclerView.Adapter<AddressViewHolder>{
        List<String> addressTxt;
        List<String> coordTxt;
        List<String> keyTxt;

        AddressAdapter(List<String> addressTxt, List<String> coordTxt, List<String> keyTxt) {
            this.addressTxt = addressTxt;
            this.coordTxt = coordTxt;
            this.keyTxt = keyTxt;
        }

        @NonNull
        @Override
        public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new AddressViewHolder(LayoutInflater.from(getContext())
                    .inflate(R.layout.radio_button_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
            RadioButton radioButton=holder.itemView.findViewById(R.id.radio);
            radioButton.setText(addressTxt.get(position));
            radioButton.setOnClickListener(v -> {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("Address", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Address_Text", addressTxt.get(position));
                editor.putString("Address_Coordinate", coordTxt.get(position));
                editor.apply();
                Objects.requireNonNull(getDialog()).dismiss();
                CartActivity.detectDismissDialog();
                getDialog().dismiss();
            });
            holder.itemView.findViewById(R.id.delete).setOnClickListener(v -> {
                ProgressBar progressBar=holder.itemView.findViewById(R.id.progressbar);
                progressBar.setVisibility(View.VISIBLE);
                FirebaseDatabase.getInstance().getReference("Customers/"+Constants.uid+"/Address")
                       .child(keyTxt.get(position)) .setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            coordTxt.remove(position);
                            addressTxt.remove(position);
                            keyTxt.remove(position);
                            notifyDataSetChanged();
                            progressBar.setVisibility(View.INVISIBLE);
                        }else Toast.makeText(getContext(),"Failed to delete address",Toast.LENGTH_LONG).show();
                    }
                });
            });
        }

        @Override
        public int getItemCount() {
            return addressTxt.size();
        }
    }
    class AddressViewHolder extends RecyclerView.ViewHolder{

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
