package com.example.pharma.reminder;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.pharma.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
        timeList=new ArrayList<>();
        timeAuto= getView().findViewById(R.id.reminder_time_auto);
        AutoCompleteTextView titleAuto= getView().findViewById(R.id.title_auto);
        if (getTag()!=null&& getTag().contains("edit")){
            String title=getArguments().getString("title");
            String slot=getArguments().getString("slot");
            timeAuto.setText(slot);
            titleAuto.setText(title);
            setTileAdapter(titleAuto);
            this.title=title;
            time=slot;
            String[] allTimeSlots;
            if (title!=null) {
                if (title.contains("Mor"))
                    allTimeSlots = morningSlot;
                else if (title.contains("After"))
                    allTimeSlots = afterNoonSlot;
                else allTimeSlots = nightSlot;
                setTimeAdapter(allTimeSlots);
            }
            buttonClickEventsForEdit(title,slot);
        }else{
            setTileAdapter(titleAuto);
            buttonClickEvents();
        }
    }

    private void buttonClickEventsForEdit(String oldTitle,String oldSlot) {
        Button cancelBt=Objects.requireNonNull(getView()).findViewById(R.id.actionButtonCancel);
        cancelBt.setBackgroundColor(getResources().getColor(R.color.red));
        cancelBt.setText(R.string.delete);
        cancelBt.setOnClickListener(v -> {
           BottomSheetDialogFragment fragment=new DisplayReminder(getDialog());
           Bundle bundle=new Bundle();
           bundle.putString("slot",oldSlot);
           bundle.putString("title",oldTitle);
           fragment.setArguments(bundle);
           fragment.showNow(Objects.requireNonNull(getActivity()).getSupportFragmentManager(),"del_reminder");
        });
        Button actionBt=Objects.requireNonNull(getView()).findViewById(R.id.actionButton);
        actionBt.setText("Save Changes");
        actionBt.setOnClickListener(v -> {
            if (title==null || time==null)
                return;
            SharedPreferences preferences= Objects.requireNonNull(getActivity()).getSharedPreferences(getString(R.string.reminder_pref_name), Context.MODE_PRIVATE);
            HashMap<String,String>medList= (HashMap<String, String>) preferences.getAll();
            String slot=preferences.getString(getString(R.string.reminder_pref_name_for_time_slot),
                    null);
            String slotString=title+"-"+time;
            Log.e("Saving Slot...","slotstring->"+slotString+"\nall slot->"+slot);
            if (Objects.requireNonNull(slot).contains(slotString)){
                showSuccessSnackBar("Reminder already exist",false,false);
            }
            else{
                showSuccessSnackBar("Changing reminder time...",true,true);
                Log.e("Saving Slot...","Saved");
                if (slot.contains("->"+oldSlot))
                   slot= slot.replace("->"+oldTitle+"-"+oldSlot,"");
                else slot=slot.replace(oldTitle+"-"+oldSlot,"");

                slot=slot+"->"+slotString;
                if (slot.startsWith("->"))
                   slot= slot.replaceFirst("->","");
                slot= initializeSlot(slot);
                if (slot!=null) {
                    Log.e("Slot Saved", "" + slot);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(getString(R.string.reminder_pref_name_for_time_slot)
                            , slot);
                    //change old slot to new slot in med;
                    medList.remove(getString(R.string.reminder_pref_name_for_time_slot));
                    for (String medName : medList.keySet()) {
                        String allMedSlots = medList.get(medName);
                        if (Objects.requireNonNull(allMedSlots).contains(oldSlot)) {
                            allMedSlots = allMedSlots.replace(oldSlot, time);
                            editor.putString(medName, allMedSlots);
                        }
                    }
                    editor.apply();
                }else showSuccessSnackBar("An error occurred",false,false);
            }
        });

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
                showSuccessSnackBar("Creating new reminder...",true,true);
                if (slot==null){
                    SharedPreferences.Editor editor=preferences.edit();
                    Log.e("Saving Slot...","Slot is null new created and saved ");
                    editor.putString(getString(R.string.reminder_pref_name_for_time_slot),title+"-"+time);
                    editor.apply();

                }else if (slot.contains(slotString)){
                    Toast.makeText(getActivity(),"Reminder already exist",Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.e("Saving Slot...","Saved");
                    slot=slot+"->"+slotString;
                   slot= initializeSlot(slot);
                   Log.e("Slot Saved",""+slot);
                    if (slot!=null) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(getString(R.string.reminder_pref_name_for_time_slot)
                                , slot);
                        editor.apply();
                    }else showSuccessSnackBar("An error occurred",false,false);

                }
            }
        });
    }

    private String initializeSlot(String slotsString){
        try {
            System.out.println("--------------Initial Slots---------------");
            System.out.println(slotsString);
            String[] allSlot = slotsString.split("->");
            ArrayList<Double> slots = new ArrayList<>();
            ArrayList<Double> morningSlots = new ArrayList<>();
            for (String s : allSlot) {
                s = s.split("-")[1].replace(":", ".");
                if (s.contains("AM")) {
                    s = "-" + s.replace("AM", "");
                    morningSlots.add(Double.parseDouble(s));
                } else {
                    s = s.replace("PM", "");
                    slots.add(Double.parseDouble(s));
                }

            }

            Collections.sort(slots);
            Collections.sort(morningSlots, Collections.reverseOrder());
            morningSlots.addAll(slots);
            System.out.println("--------------Final Slots---------------");
            return formattedSlots(morningSlots);
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

     private String formattedSlots(ArrayList<Double> list){
        StringBuilder slots= null;
        for (double d : list) {
            String slot;
            String suffix;
            String prefix;

            if (slots==null) {
                slots = new StringBuilder();
                prefix="";
            }else prefix="->";

            slot=""+d;
            slot= slot.replace(".",":");
            if(slot.split(":")[1].length()==1)
                suffix="0";
            else
                suffix=""	;

            if (d<0) {
                prefix+="Morning";

                slot=slot.replace("-","");
                suffix+=" AM";
            }else{
                suffix+=" PM";
                if (d>0 && d<5)
                    prefix+="Afternoon";
                else prefix+="Night";

            }

            slots.append(prefix).append("-").append(slot).append(suffix);
        }
        return slots.toString();
    }


    private void setTimeAdapter(String[] timeSlot) {
//        if (adapter==null)
            adapter=new ArrayAdapter<>(getContext(),
                    R.layout.dropdown_textview,timeSlot);
        timeAuto.setAdapter(adapter);
        timeAuto.setOnItemClickListener((parent, view, position, id) -> {
            time=timeSlot[(position)];
        });
    }
    private void setTileAdapter(AutoCompleteTextView titleAuto) {
        String[] options=new String[]{"Morning","Afternoon","Evening"};
        ArrayAdapter<String> adapters=new ArrayAdapter<>(requireContext(),
                R.layout.dropdown_textview,options);

        titleAuto.setAdapter(adapters);
        titleAuto.setOnItemClickListener((parent, view, position, id) -> {
            title=options[position];
            timeList.clear();
            time=null;
            timeAuto.setText("");
            Log.e("Pos","->"+position);
            if (position==0)
                setTimeAdapter(morningSlot);
            else if (position==1)
                setTimeAdapter(afterNoonSlot);
            else
                setTimeAdapter(nightSlot);
            Log.e("TimeList","->"+timeList);
            adapter.notifyDataSetChanged();
        });
    }
    private void showSuccessSnackBar(String msg, boolean finish, boolean inDefinite){
        Snackbar snackbar= Snackbar.make(getView().findViewById(R.id.main_view), msg,Snackbar.LENGTH_SHORT);
        snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_FADE);
        snackbar.setBackgroundTint(getResources().getColor(R.color.colorPrimaryDark));
        snackbar.setTextColor(getResources().getColor(R.color.white));
        if (inDefinite)
        {
            snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
        }
        snackbar.show();
        Handler handler=new Handler();
        if (finish)
            handler.postDelayed(this::dismiss,3000);



    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL,R.style.CustomBottomSheetTheme);
    }
}
