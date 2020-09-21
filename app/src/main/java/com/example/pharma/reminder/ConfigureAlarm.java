package com.example.pharma.reminder;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.example.pharma.service.ReminderService;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class ConfigureAlarm {
    public void createNotification(Activity activity,Calendar calendar) {
        Intent myIntent = new Intent(getApplicationContext() , ReminderService. class ) ;
        AlarmManager alarmManager = (AlarmManager)activity.getSystemService( ALARM_SERVICE ) ;
        PendingIntent pendingIntent = PendingIntent. getBroadcast ( activity, 0 , myIntent , 0 ) ;
//        Calendar calendar = Calendar. getInstance () ;
//        calendar.set(Calendar. SECOND , 0 ) ;
//        calendar.set(Calendar. MINUTE , 0 ) ;
//        calendar.set(Calendar. HOUR , 0 ) ;
//        calendar.set(Calendar. AM_PM , Calendar. AM ) ;
//        calendar.add(Calendar. DAY_OF_MONTH , 1 ) ;
//            pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.setRepeating(AlarmManager. RTC_WAKEUP , calendar.getTimeInMillis() , 1000 * 60 * 60 * 24 , pendingIntent) ;
        Log.e("Alarm","Set At "+calendar);
    }
}
