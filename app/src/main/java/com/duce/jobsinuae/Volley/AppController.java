package com.duce.jobsinuae.Volley;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class AppController {
    private  static AppController appController;
    private RequestQueue mRequestQueue;
    private  final Context mCtx;

    public AppController(Context context) {
            this.mCtx = context;
           mRequestQueue = getRequestQueue();
    }

    public static synchronized AppController getInstance(Context context){
        if(appController == null){
            appController = new AppController(context);
        }
        return appController;
    }

    private RequestQueue getRequestQueue() {

        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return  mRequestQueue;
    }

    public  void addToRequestQueue(Request req) {
        getRequestQueue().add(req);
    }
}
