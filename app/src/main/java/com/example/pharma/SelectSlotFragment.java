package com.example.pharma;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.TextView;

import com.example.pharma.firebase.FirebaseCustomAuth;
import com.example.pharma.model.OrderModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;


public class SelectSlotFragment extends BottomSheetDialogFragment {

    private TextView dateText;
    private TextView monthText;
    private long delivery_time;
    private TextView expectedText;
    private TextView tempExpectedText;
    private View root;

    public SelectSlotFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadSlotInfoFromDB();

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_select_slot, container, false);
        root=view;
        try {
            dateText = Objects.requireNonNull(root).findViewById(R.id.date);
            monthText = Objects.requireNonNull(root).findViewById(R.id.month);
            expectedText = Objects.requireNonNull(root).findViewById(R.id.expecteddelivery);
            tempExpectedText = Objects.requireNonNull(root).findViewById(R.id.deliveryExtras);
            root.findViewById(R.id.nextBt).setOnClickListener(v -> {
                if (getArguments() != null && delivery_time!=0) {
                    OrderModel.OrderModelForRealtimeDb forRealtimeDb = (OrderModel.OrderModelForRealtimeDb)
                            getArguments().getSerializable("REALTIME_MODEL");
                    forRealtimeDb.setOrder_expected_delivery_time(delivery_time);
                    OrderModel.OrderModelForFirestore forFirestoreDb = (OrderModel.OrderModelForFirestore)
                            getArguments().getSerializable("FIRESTORE_MODEL");
                    Intent intent = new Intent(getActivity(), PaymentMethodActivity.class);
                    intent.putExtra("REALTIME_MODEL", forRealtimeDb);
                    intent.putExtra("FIRESTORE_MODEL",forFirestoreDb);
                    intent.putExtra("TOTAL_AMOUNT",(long)forRealtimeDb.getOrder_amount_stats().get("total_amount"));
                    startActivity(intent);
                }
            });
        }catch (Exception e){e.printStackTrace();}
        return view;
    }

    private void selectSlotFragment(HashMap<String, Long> slots){
        View dialogView = View.inflate( getContext(), R.layout.calendar_layout, null);
        final Dialog dialog = new Dialog(Objects.requireNonNull(getContext()),R.style.Dialog1);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        lp.windowAnimations = R.style.DialogAnimation;
        Objects.requireNonNull(dialog.getWindow()).setAttributes(lp);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(dialogView);
        dialog.setOnKeyListener((dialogInterface, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_BACK) {
                dialog.dismiss();
                return true;
            }
            return false;
        });
        CalendarView calendarView=dialogView.findViewById(R.id.calendar);
        View tempTimeField=root.findViewById(R.id.deliveryDisplay);
        View timeField=root.findViewById(R.id.RelativeDelivery);
        ArrayList<Long> data=new ArrayList<>();
        for (String time:slots.keySet())
            data.add(Long.parseLong(time));
        calendarView.setMaxDate(Collections.max(data));
        calendarView.setMinDate(Collections.min(data));
        calendarView.setOnDateChangeListener((calendarView1, year, month, day) -> {
            long time= convertDateTOMillis(day+"/"+(month+1)+"/"+year+" 00:00:00");
          try {Log.e("SlotMilli","->"+time+" =>"+day+"/"+month+"/"+year);
              if (slots.get(String.valueOf(time)) > 0)
              {   timeField.setVisibility(View.VISIBLE);
                  tempTimeField.setVisibility(View.GONE);
                  delivery_time=time;
                  expectedText.setText(R.string.earliest_delivery);
                  if (day<10)
                  dateText.setText(String.format(Locale.UK,"0%d", day));
                  else dateText.setText(String.valueOf(day));
                  Log.e("Calenderview",">"+calendarView1.getDate());
                  Calendar cl= Calendar.getInstance();
                  cl.setTimeInMillis(time);
                  monthText.setText(String.format("%s\n%s", Constants.getMonthName(month + 1, false),
                          Constants.getWeekName(Calendar.DAY_OF_WEEK)));

              }else{  tempExpectedText.setText(R.string.no_slots);
                  expectedText.setText("");
                  timeField.setVisibility(View.GONE);
                  tempTimeField.setVisibility(View.VISIBLE);
              }
          }catch(Exception e){e.printStackTrace();}
            dialog.dismiss();
        });
        dialog.show();
    }
    private void loadSlotInfoFromDB(){
        Calendar cl= Calendar.getInstance();
        cl.setTimeInMillis(System.currentTimeMillis());
        long time=convertDateTOMillis(cl.get(Calendar.DAY_OF_MONTH)+"/"+
                (cl.get(Calendar.MONTH)+1)+"/"+cl.get(Calendar.YEAR)+ " 00:00:00");
        Log.e("LoadSlot","=>"+time);
        FirebaseDatabase.getInstance(new FirebaseCustomAuth()
                .loadCustomFirebase(getContext(),"TabletHuts-Extra")).
                getReference("Slot Information")
                .orderByChild("slot_date").startAt(time)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String,Long> slots= new HashMap<>();
                for (DataSnapshot ds:dataSnapshot.getChildren())
                    slots.put(String.valueOf(ds.child("slot_date").getValue()), (Long) ds.child("slot_available").getValue());
                root.findViewById(R.id.slotField).setVisibility(View.VISIBLE);
                root.findViewById(R.id.nextBt).setVisibility(View.VISIBLE);
                root.findViewById(R.id.progressbar).setVisibility(View.GONE);
                if (slots.size()==0){
                    TextView textView= Objects.requireNonNull(root).findViewById(R.id.deliveryExtras);
                    textView.setText(R.string.no_slots);
                }else loadListeners(slots);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadListeners(HashMap<String, Long> slots) {
        Log.e("Slots","=>"+slots);
        root.findViewById(R.id.deliveryDisplay).setOnClickListener(v -> selectSlotFragment(slots));
        root.findViewById(R.id.delivery_time_ui).setOnClickListener(v -> selectSlotFragment(slots));
    }

    private long convertDateTOMillis(String date){
        try {
            return (new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH).parse(date).getTime());
        } catch (ParseException e) {
            return -1;
        }
    }

}
