package com.example.pharma.reminder;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pharma.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DisplayReminder extends BottomSheetDialogFragment {
    private  Dialog dialog;

    DisplayReminder(Dialog dialog) {
        this.dialog=dialog;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL,R.style.CustomBottomSheetTheme);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.reminder_display_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String tag=getTag();
        if (tag != null) {
            if (tag.contains("del_reminder")){
                String title=getArguments().getString("title");
                String slot=getArguments().getString("slot");
                Button cancel=view.findViewById(R.id.actionButtonCancel);
                cancel.setVisibility(View.GONE);
                Button add=view.findViewById(R.id.actionButton);
                add.setText(R.string.delete);
                add.setBackgroundColor(getResources().getColor(R.color.red));
                setData(null,slot,"Delete all medicine and disable this reminder");
                initDelScreen(slot,title,add);
            }else if (tag.contains("del_med")){
                String medName=getArguments().getString("med_name");
                String slot=getArguments().getString("slot");
                Button cancel=view.findViewById(R.id.actionButtonCancel);
                cancel.setVisibility(View.GONE);
                Button add=view.findViewById(R.id.actionButton);
                add.setText(R.string.delete);
                add.setBackgroundColor(getResources().getColor(R.color.red));
                setData(medName,slot,"Delete medicine from reminder?");
                add.setOnClickListener(v -> {
                    showSuccessSnackBar("Deleting medicine...",true,true,null);
                    delMedSlot();
                });
            }
            else if (tag.contains("add_med")){
                String medName=getArguments().getString("med_name");
                String slot=getArguments().getString("slot");
                Button cancel=view.findViewById(R.id.actionButtonCancel);
                cancel.setText(R.string.cancel);
                Button add=view.findViewById(R.id.actionButton);
                add.setText(R.string.add);
                setListenersTOAddMed(add,cancel,slot,medName);
                setData(medName,slot,getString(R.string.confirm_med_reminder));
            }
        }
    }

    private void initDelScreen(String slot, String title, Button add) {
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences(getString(R.string.reminder_pref_name),Context.MODE_PRIVATE);
        HashMap<String,String> medList= (HashMap<String, String>) sharedPreferences.getAll();
        ArrayList<String> medNames=new ArrayList<>();
        for (String medName:medList.keySet()){
            String medSlot=medList.get(medName);
            if (!medName.equals(getString(R.string.reminder_pref_name_for_time_slot))
                    &&medSlot!=null && medSlot.contains(slot))
                medNames.add(medName);
        }
        setListView(medNames);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        setDeleteListener(slot,title,add,medList,editor,medNames);
    }

    private void setListView(ArrayList<String> medNames) {
        ListView listView= Objects.requireNonNull(getView()).findViewById(R.id.listview);
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(Objects.requireNonNull(getContext()),
                R.layout.textview_with_image,R.id.textView,medNames);
        listView.setAdapter(arrayAdapter);
    }

    private void setDeleteListener(String slot, String title, Button delBt, HashMap<String, String> medList, SharedPreferences.Editor editor, ArrayList<String> medNames) {
        delBt.setOnClickListener(v -> {
            showSuccessSnackBar("Removing reminder...",true,true,null);
            String titleSlots=medList.get(getString(R.string.reminder_pref_name_for_time_slot));
            String stringSlot=title+"-"+slot;
            if (titleSlots==null)
                return;
            Log.e("StringSlot","->"+stringSlot);
            if (titleSlots.contains("->"+stringSlot))
               titleSlots= titleSlots.replace("->"+stringSlot,"");
            else titleSlots= titleSlots.replace(stringSlot,"");
            editor.putString(getString(R.string.reminder_pref_name_for_time_slot),titleSlots);
            medList.remove(getString(R.string.reminder_pref_name_for_time_slot));

            for (String medName:medNames)
                delMedSlot(medName);
            dialog.dismiss();
            editor.apply();
        });
    }

    private void setData(String medName, String slot,String desc) {
        TextView timeSlot= Objects.requireNonNull(getView()).findViewById(R.id.time_title);
        timeSlot.setText(slot);
        if (medName!=null) {
            TextView medNameTextView=getView().findViewById(R.id.med_title);
            medNameTextView.setVisibility(View.VISIBLE);
            medNameTextView.setText(medName);
        }
        TextView medDesc=getView().findViewById(R.id.desc);
        medDesc.setText(desc);


    }

    private void setListenersTOAddMed(Button add, Button cancel,String slot, String medName) {
        add.setOnClickListener(v -> {
            if (!medName.isEmpty()) {
                SharedPreferences preferences=getActivity().getSharedPreferences(getString(R.string.reminder_pref_name), Context.MODE_PRIVATE);
                String medSlots = preferences.getString(medName,"");
                SharedPreferences.Editor editor=preferences.edit();
                if (medSlots.isEmpty()){
                    medSlots=medName;
                }else medSlots+="->"+slot;
                editor.putString(medName,medSlots);
                editor.apply();
                dialog.dismiss();
                showSuccessSnackBar("Medicine added at "+slot,true,false,"Undo");
            }
        });
        cancel.setOnClickListener(v -> dismiss());
    }

    private void showSuccessSnackBar(String msg, boolean finish, boolean inDefinite, String buttonMsg){
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
        if (buttonMsg!=null){
            snackbar.setActionTextColor(getResources().getColor(R.color.white));
            snackbar.setAction(buttonMsg, v -> {
                if (buttonMsg.contains("Undo"))
                    delMedSlot();
            });
        }

    }

    private void delMedSlot(String ...oldMedName) {
        String medName;
        if (oldMedName.length==0)
         medName=getArguments().getString("med_name");
        else medName=oldMedName[0];
        String slot=getArguments().getString("slot");
        SharedPreferences preferences=getActivity().getSharedPreferences(getString(R.string.reminder_pref_name), Context.MODE_PRIVATE);
        String medSlots = preferences.getString(medName,"");
        SharedPreferences.Editor editor=preferences.edit();
        if (medSlots.contains("->"+slot))
           medSlots= medSlots.replace("->"+slot,"");
        else medSlots= medSlots.replace(medName,"");
        if (medSlots.trim().isEmpty())
            editor.remove(medName);
        else editor.putString(medName,medSlots);
        editor.apply();
    }

}
