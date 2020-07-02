package com.example.pharma.reminder;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.pharma.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AddNewReminderFragment extends BottomSheetDialogFragment {
    private String title;
    private ArrayAdapter<String> adapter;
    private AutoCompleteTextView timeAuto;
    private ArrayList<String> timeList;
    private String time;
    private String[] morningSlot=new String[]{"6:00 AM","6:30 AM","7:00 AM","7:30 AM","8:00 AM","8:30 AM","9:00 AM","9:30 AM","10:00 AM","10:30 AM","11:00 AM","11:30 AM"};
    private String[] afterNoonSlot=new String[]{"12:00 PM","12:30 PM","1:00 PM","1:30 PM","2:00 PM","2:30 PM","3:00 PM","3:30 PM","4:00 PM","4:30 PM","5:00 PM","5:30 PM"};
    private String[] nightSlot=new String[]{"6:00 PM","6:30 PM","7:00 PM","7:30 PM","8:00 PM","8:30 PM","9:00 PM","9:30 PM","10:00 PM","10:30 PM","11:00 PM","11:30 PM","12:00 AM","12:30 AM"};
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_new_reminder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTileAdapter();
        setTimeAdapter();
        buttonClickEvents();
    }

    private void buttonClickEvents() {
        Objects.requireNonNull(getView()).findViewById(R.id.actionButtonCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        Objects.requireNonNull(getView()).findViewById(R.id.actionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title==null || time==null)
                    return;
                SharedPreferences preferences=getActivity().getSharedPreferences(getString(R.string.reminder_pref_name), Context.MODE_PRIVATE);
                String slot=preferences.getString(getString(R.string.reminder_pref_name_for_time_slot),
                        null);
                String slotString=title+"-"+time;
                Log.e("Saving Slot...","->"+slotString);
                if (slot==null){
                    SharedPreferences.Editor editor=preferences.edit();
                    Log.e("Saving Slot...","Slot is null new created and saved ");
                    editor.putString(getString(R.string.reminder_pref_name_for_time_slot),title+"-"+time);
                    editor.apply();
                    dismiss();
                }else if (slot.contains(slotString)){
                    Toast.makeText(getActivity(),"Reminder already exist",Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.e("Saving Slot...","Saved");
                    slot=slot+"->"+slotString;

                    Log.e("Slot Saved",""+slot);
//                    SharedPreferences.Editor editor=preferences.edit();
//                    editor.putString(getString(R.string.reminder_pref_name_for_time_slot)
//                            ,slot);
//                    editor.apply();
//                    dismiss();
                }
            }
        });
    }
    private StringBuilder filterSlot(StringBuilder timeSlot, String s) {
        if (timeSlot==null)
            timeSlot = new StringBuilder(s);
        else{
            if (timeSlot.toString().split("->")[0].compareTo(s)>0)
                timeSlot.insert(0, s + "->");
            else
                timeSlot.append("->").append(s);
        }
        return timeSlot;
    }

    private void manipulateSlotToAddNew(String slot,String newSlotReminder) {
        StringBuilder morning= null;
        StringBuilder afternoon=null;
        StringBuilder night=null;
        String[] tempSlot=slot.split("->");
        for (String s:tempSlot){
            if (s.contains("Morning")) morning = filterSlot(morning, s);
            if (s.contains("Afternoon")) afternoon = filterSlot(morning, s);
            if (s.contains("Night")) night = filterSlot(morning, s);
        }
//        if (newSlotReminder.contains("Morning"))
//           return addSlot(morning,newSlotReminder,slot,afternoon,night);
//        else if (newSlotReminder.contains("Afternoon"))
//            return addSlot(afternoon,newSlotReminder,slot,);
//        else
            //return addSlot(night,newSlotReminder,slot);
    }

    private String addSlot(StringBuilder timeSlot,String allSlots, String newSlotReminder,StringBuilder second,StringBuilder third) {
        if (timeSlot==null)
            return newSlotReminder+"->"+allSlots;

    return null;
    }

    private void setTimeAdapter() {
        if (adapter==null)
            adapter=new ArrayAdapter<>(requireContext(),
                    R.layout.dropdown_textview,timeList);
        timeAuto= getView().findViewById(R.id.reminder_time_auto);
        timeAuto.setAdapter(adapter);
        timeAuto.setOnItemClickListener((parent, view, position, id) -> {
            time=timeList.get(position);
        });
    }
    private void setTileAdapter() {
        timeList=new ArrayList<>();
        String[] options=new String[]{"Morning","AfterNoon","Evening"};
        ArrayAdapter<String> adapter=new ArrayAdapter<>(requireContext(),
                R.layout.dropdown_textview,options);
        AutoCompleteTextView titleAuto= getView().findViewById(R.id.title_auto);
        titleAuto.setAdapter(adapter);
        titleAuto.setOnItemClickListener((parent, view, position, id) -> {
            title=options[position];
            timeList.clear();
            time=null;
            timeAuto.setText("");
            if (position==0)
                Collections.addAll(timeList, morningSlot);
            else if (position==1)
                Collections.addAll(timeList, afterNoonSlot);
            else
                Collections.addAll(timeList, nightSlot);
            this.adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL,R.style.CustomBottomSheetTheme);
    }
}
