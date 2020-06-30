package com.example.pharma;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Objects;

public class LoadRecentData {
    public LoadRecentData(){

    }
    private int maxRecentLimit=10;
    private ArrayList<String> loadDataFromSharedPref(Context context){
        SharedPreferences sh= Objects.requireNonNull(context).
                getSharedPreferences("RECENT", Context.MODE_PRIVATE);
        //return loadRecentData(sh);
        return  null;
    }

    public ArrayList<String> loadRecentData(Context context) {
        SharedPreferences sh= Objects.requireNonNull(context).
                getSharedPreferences("RECENT", Context.MODE_PRIVATE);
        ArrayList<String> arr=new ArrayList<>();
        for(int i=1;i<maxRecentLimit;i++){
            String data=sh.getString(String.valueOf(i),null);
            if(data==null)
                break;
            arr.add(data);
        }
        return arr;
    }
    public void addNewRecent( String uid, Activity activity,String preferenceName){
        Log.e("PrefName",preferenceName);
        SharedPreferences sh= activity.getSharedPreferences(preferenceName,Context.MODE_PRIVATE);
        int pointer=sh.getInt("POINTER",1);
        //check if exist in shared pref
        int initial=0;//if data exist in pref it will not be zero

        if(sh.getString("1",null)==null){
            replaceWithNew(pointer, sh,  uid);
        }else {
            for (int i = 1; i <= maxRecentLimit; i++) {
                String data = sh.getString(String.valueOf(i), null);
                if (data == null)
                    break;
                if (data.contains(uid))
                    initial = i;
            }
            if (initial != 0 && initial!=pointer-1)
                changePosition(initial, pointer, sh, uid);
            else if(initial==0)
                replaceWithNew(pointer, sh, uid);

        }
    }
    /*Add new recent contact into shared pref*/
    private void replaceWithNew(int pointer, SharedPreferences sh, String uid) {
        SharedPreferences.Editor edit=sh.edit();
        edit.putString(String.valueOf(pointer),uid);
        if(pointer==maxRecentLimit)
            pointer=1;
        else pointer++;

        edit.putInt("POINTER",pointer);
        edit.apply();
    }
    /*
     * If the recent already exist in shred pref recent so we put the new one at the pointer position ie at last
     * and we move the rest one step decrement ie data of 3 to 2 , data of 4 to 3 etc.
     * */
    private void changePosition(int initial, int pointer,SharedPreferences sh,String uid) {
        SharedPreferences.Editor edit=sh.edit();
        for(int i=initial;i<pointer-1;i++)
            edit.putString(String.valueOf(i), sh.getString(String.valueOf(i + 1), ""));

        edit.putString(String.valueOf(pointer-1),uid);
        edit.apply();

    }


}
