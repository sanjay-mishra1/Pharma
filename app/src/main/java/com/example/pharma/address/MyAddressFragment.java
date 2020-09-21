package com.example.pharma.address;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pharma.CartActivity;
import com.example.pharma.Constants;
import com.example.pharma.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class MyAddressFragment  extends BottomSheetDialogFragment {

    private View root;
    private ChildEventListener listener;
    private DatabaseReference databaseReference;
    private ArrayList<String> list;
    private ArrayList<String> coordinate;
    private ArrayList<String> keyList;
    private AddressAdapter adapter;
    private TextView titleText;
    private TextView parentTextView;
    public MyAddressFragment() {
        // Required empty public constructor
    }

    MyAddressFragment(TextView textView) {
        this.parentTextView=textView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL,R.style.CustomBottomSheetTheme);
        list=new ArrayList<>();
        keyList=new ArrayList<>();
        coordinate=new ArrayList<>();
        loadFirebaseForAddress();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         root=inflater.inflate(R.layout.fragment_my_address, container, false);;
        root.findViewById(R.id.addAddress).setOnClickListener(v -> {
            Intent intent=new Intent(getContext(),SelectPinCodeActivity.class);
            startActivity(intent);
        });
        titleText=root.findViewById(R.id.title);
        return root;
    }
    private void loadFirebaseForAddress() {
        databaseReference=FirebaseDatabase.getInstance()
                .getReference("Customers/"+ Constants.uid).child("Address");
         listener=databaseReference.addChildEventListener(new ChildEventListener() {
             @Override
             public void onChildAdded(@NonNull DataSnapshot dataSnapshot1, @Nullable String s) {
                 Log.e("ValueEvent","Triggered....."+dataSnapshot1.getValue());
                 if (adapter!=null) {
                     if (!keyList.contains(dataSnapshot1.getKey())) {
                         keyList.add(dataSnapshot1.getKey());
                         list.add((String) dataSnapshot1.child("address_text").getValue());
                         coordinate.add(dataSnapshot1.child("address_lat").getValue()
                                 + "," + dataSnapshot1.child("address_lng").getValue());
                         adapter.notifyDataSetChanged();
                         titleText.setText(R.string.select_address);
                     }
                 }
             }

             @Override
             public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

             }

             @Override
             public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

             }

             @Override
             public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });
        databaseReference.addChildEventListener(listener);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                root.findViewById(R.id.progress_field).setVisibility(View.GONE);
                root.findViewById(R.id.address_field).setVisibility(View.VISIBLE);
               try {
                       RecyclerView listView = root.findViewById(R.id.address_list);
                       list = new ArrayList<>();
                       coordinate = new ArrayList<>();
                       keyList = new ArrayList<>();
                       for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                       {    keyList.add(dataSnapshot1.getKey());
                           list.add((String) dataSnapshot1.child("address_text").getValue());
                           coordinate.add(dataSnapshot1.child("address_lat").getValue()
                                   +","+dataSnapshot1.child("address_lng").getValue());
                       }
                           LinearLayoutManager mLayoutManager =
                                   new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                           adapter=new AddressAdapter(list,coordinate,keyList);
                           listView.setLayoutManager(mLayoutManager);
                           listView.setAdapter(adapter);
                   if (list.size() <= 0) {
                       titleText.setText(R.string.no_address_found);
                   }

               }catch (Exception e){e.printStackTrace();}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.e("Dismiss","Removing listener");
        databaseReference.removeEventListener(listener);
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
                String address=addressTxt.get(position);
                editor.putString("Address_Text", address);
                editor.putString("Address_Coordinate", coordTxt.get(position));
                editor.apply();
                Objects.requireNonNull(getDialog()).dismiss();
               try {
                   if (parentTextView!=null){
                       String pincode=address.substring(address.indexOf("Pincode :")).replaceAll("\\D+","").trim();
                       parentTextView.setText(pincode);
                   }
                   CartActivity.detectDismissDialog();
               }catch (Exception ignored){}
            });
            holder.itemView.findViewById(R.id.delete).setOnClickListener(v -> {
                ProgressBar progressBar=holder.itemView.findViewById(R.id.progressbar);
                progressBar.setVisibility(View.VISIBLE);
                FirebaseDatabase.getInstance().getReference("Customers/"+Constants.uid+"/Address")
                       .child(keyTxt.get(position)) .setValue(null).addOnCompleteListener(task -> {
                           if (task.isSuccessful()) {
                               coordTxt.remove(position);
                               addressTxt.remove(position);
                               keyTxt.remove(position);
                               if (keyTxt.isEmpty())
                                   titleText.setText(R.string.no_address_found);
                               notifyDataSetChanged();
                               progressBar.setVisibility(View.INVISIBLE);
                           }else Toast.makeText(getContext(),"Failed to delete address",Toast.LENGTH_LONG).show();
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
