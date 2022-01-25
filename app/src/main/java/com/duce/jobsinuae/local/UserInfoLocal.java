package com.duce.jobsinuae.local;

import android.content.Context;
import android.content.SharedPreferences;

public class UserInfoLocal {

    private final SharedPreferences preferences;
    private final String USER_DATA = "USERDATA";
    private  final SharedPreferences.Editor editor;
    private final Context context;

    public UserInfoLocal(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void setFirstName(String firstName){
        editor.putString("firstName" ,  firstName);
        editor.apply();
    }

    public String getFirstName(){
         return preferences.getString("firstName","");
    }


}
