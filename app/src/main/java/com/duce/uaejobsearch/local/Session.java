package com.duce.uaejobsearch.local;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {
    private final SharedPreferences preferences;
    private final String USER_DATA = "ISFirstTime";
    private  final SharedPreferences.Editor editor;
    private final Context context;

    public Session(Context context) {
          this.context = context;
          preferences = context.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE);
          editor = preferences.edit();
    }

    public void setIsFirstTIme(boolean isFirstTime){
         editor.putBoolean("isFirstTime",isFirstTime);
         editor.apply();
    }

    public void setNumberOfClicks(int clickCount){
        editor.putInt("count",clickCount);
        editor.apply();
    }

    public void setActivityClick(int clickC){
        editor.putInt("perActivityCount",clickC);
        editor.apply();
    }

    public int getClickActivityCount(){
         return preferences.getInt("perActivityCount",1);
    }

    public int getClickCount(){
         return  preferences.getInt("count",1);
    }

    public boolean getIsFirstTime(){
        return preferences.getBoolean("isFirstTime" ,true);
    }
}
