package com.example.ironman.contactsphone;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ASUS on 09.10.2017.
 */

public class UtilsSharedPreferns {


  public void setActive(Context context,boolean isActive){
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
       prefs.edit().putBoolean("locked", isActive).commit();
  }

    public void setPhoneNumber(Context context,String phone){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString("phone", phone).commit();
    }
    public void setCount(Context context,int count){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putInt("count", count).commit();
    }
  public  Boolean isActive(Context context) {
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
      return prefs.getBoolean("locked",false);
  }

    public  int getCount(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt("count",0);
    }


    public  String getPhone(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("phone","");
    }
}
