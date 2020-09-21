package com.example.pharma;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Locale;

public class Constants {
    public static String uid;
    public static String MOBILE;
    public static String Name="";

    public static String getMonthName(int monthNumber, boolean isFullName) {
        switch (monthNumber){
            case 1:if (isFullName)return"January"; else return "Jan";
            case 2:if (isFullName) return "February";else return "Feb";
            case 3:if (isFullName)return"March";else return "Mar";
            case 4:if (isFullName)return"April";else return "Apr";
            case 5:return "May";
            case 6:if (isFullName)return"June";else return "Jun";
            case 7:if (isFullName)return "July";else return "Jul";
            case 8:if (isFullName)return"August";else return "Aug";
            case 9:if (isFullName)return"September";else return "Sep";
            case 10:if (isFullName)return"October";else return "Oct";
            case 11:if (isFullName)return"November";else return "Nov";
            case 12:if (isFullName)return"December";else return "Dec";
        }
        return "";
    }

    public static String getWeekName(int weekNumber) {
        switch (weekNumber){
            case 1:return "Sun";
            case 2:return "Mon";
            case 3:return "Tue";
            case 4:return "Wed";
            case 5:return "Thr";
            case 6:return "Fri";
            case 7:return "Sat";
        }
        return "";
    }
    public static long convertDateTOMillis(String date){
        try {
            return (new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH).parse(date).getTime());
        } catch (ParseException e) {
            return -1;
        }
    }

  public    static String getFormatedAmount(long amount){
        return "₹ "+ NumberFormat.getNumberInstance(Locale.UK).format(amount);
    }
    public static String getFormattedTime(long time){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yy hh:mm aa", Locale.UK);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return dateFormat.format(calendar.getTime());
    }

    public static String getFormattedTime(long time,String pattern){
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.UK);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return dateFormat.format(calendar.getTime());
    }
    public static String getDateInNumber(long time){
        String d=getFormattedTime(System.currentTimeMillis(),"dd MMM YYYY");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate date=LocalDate.parse(getFormattedTime(time,"yyyy-MM-dd"));
            LocalDate current=LocalDate.parse(getFormattedTime(System.currentTimeMillis(),"yyyy-MM-dd"));
            long diffdays= ChronoUnit.DAYS.between(date,current);
            if (diffdays==0)
                return "Today";
            if (diffdays==1)
                return "Yesterday";
            if (diffdays<8)
                return diffdays+" days ago";
            if (diffdays<30)
                return diffdays/7+" weeks ago";
        }
        return getFormattedTime(time,"dd MMM YYYY");
    }

    public static Class getActionClassName(String action) {
        Class className=null;
        return className;
    }
}
