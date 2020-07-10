package com.example.pharma.reminder;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.pharma.Constants;
import com.example.pharma.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import devs.mulham.horizontalcalendar.HorizontalCalendar;

public class MedicineReminderActivity extends AppCompatActivity {
    public static StringBuilder dayValue=new StringBuilder();
    private HorizontalCalendar horizontalCalendar;
    private TextView monthName;
   static public ReminderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_reminder);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Medicine Reminder");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        monthName=findViewById(R.id.monthName);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);
        monthName.setText(Constants.getFormattedTime(System.currentTimeMillis(),"MMMM"));
        horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(7)
                .configure().showTopText(true).showBottomText(false)
                .formatTopText("EEEEE").
                        end()
                .build();
        //HorizontalCalendarConfig config=new HorizontalCalendarConfig().setFormatTopText("E");


        fetchPreference();
    }
    void  fetchPreference(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
        String date = dateFormat.format(new Date());
        dayValue=dayValue.append( Constants. convertDateTOMillis( date + " 00:00:00"));
//        createTestPref();
        HashMap<String,String>map= (HashMap<String, String>)
                getSharedPreferences(getString(R.string.reminder_pref_name),MODE_PRIVATE).getAll();
        String []slots= Objects.requireNonNull(map.get(getString(R.string.reminder_pref_name_for_time_slot))).split("->");
        map.remove(getString(R.string.reminder_pref_name_for_time_slot));
        RecyclerView recyclerView=findViewById(R.id.reminder_recycle);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
         adapter=new ReminderAdapter(this,slots,map,dayValue,
                horizontalCalendar,monthName);
        recyclerView.setAdapter(adapter);
    }


    private void createTestPref() {
        SharedPreferences sharedPreferences=getSharedPreferences(getString(R.string.reminder_pref_name),MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(getString(R.string.reminder_pref_name_for_time_slot)
                ,"Morning-8:00 AM->Afternoon-12:00 PM->Night-8:00 PM");
        editor.putString("Paracetamol","8:00 AM->12:00 PM");
        editor.putString("B12 Capsule","8:00 AM");
        editor.putString("Simpo Sodium Chloride","8:00 AM");
        editor.putString("Blood Pressure Checkup","8:00 AM");
        editor.apply();
        String slot= String.valueOf(dayValue);
//        dayValue.append(slot);
        SharedPreferences sh=getSharedPreferences("reminder_"+slot,MODE_PRIVATE);
        SharedPreferences.Editor ed=sh.edit();
        Set<String> allSlot=new HashSet<>();
        allSlot.add("8:00 AM->s");
        allSlot.add("12:00 PM->f");
        ed.putStringSet("Paracetamol",allSlot);
        Set<String> allSlot2=new HashSet<>();
        allSlot2.add("8:00 AM->s");
        ed.putStringSet("Blood Pressure Checkup",allSlot2);
        ed.apply();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void addNewReminder(View view) {
        BottomSheetDialogFragment fragment=new AddNewReminderFragment();
        fragment.showNow(getSupportFragmentManager(),"add");
    }
}
