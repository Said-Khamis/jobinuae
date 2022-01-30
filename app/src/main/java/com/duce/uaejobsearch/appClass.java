package com.duce.uaejobsearch;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatDelegate;

import com.applovin.sdk.AppLovinSdk;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OSNotificationReceivedEvent;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class appClass extends Application {

    private Context context;


    @Override
    public void onCreate() {
        super.onCreate();


        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        context = appClass.this.getApplicationContext();

        String ONESIGNAL_APP_ID = context.getString(R.string.oneSignalAppID);

        /*-------- FACEBOOK SDK ------------*/

        /*---- APPLOVIN INITIALIZATION ------------*/
        AppLovinSdk.getInstance(this).setMediationProvider("max");
        AppLovinSdk.initializeSdk(this);
        //AppLovinSdk.getInstance( this ).getSettings().setVerboseLogging( true );

        AppLovinSdk sdk = AppLovinSdk.getInstance( context );
        sdk.getSettings().setMuted( true );

        //AppLovinSdk.getInstance(context).getSettings().setTestDeviceAdvertisingIds(Arrays.asList("0b20b79e-f3ad-490d-b662-10ca1ad1abe1"));
       // AppLovinSdk.getInstance(this).showMediationDebugger();

        /*---- ONESIGNAL INITIALIZATION-------*/
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        OneSignal.setNotificationWillShowInForegroundHandler(new OneSignal.OSNotificationWillShowInForegroundHandler() {
            @Override
            public void notificationWillShowInForeground(OSNotificationReceivedEvent notificationReceivedEvent) {
                /* JSONObject data = notificationReceivedEvent.getNotification().getAdditionalData();
                 System.out.println("Notification Data");
                 System.out.println("Post ID:" + data);*/
            }
        });

        OneSignal.setNotificationOpenedHandler(new OneSignal.OSNotificationOpenedHandler() {
            @Override
            public void notificationOpened(OSNotificationOpenedResult result) {
                JSONObject data = result.getNotification().getAdditionalData();
             /*   System.out.println("Open - Notification Data");
                System.out.println("Print Data" + data);*/
               try {
                  if( data == null){
                         Intent intent = new Intent(context, MainActivity.class);
                         intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                         startActivity(intent);
                 }else {
                         String  postId =  data.getString("post_id").toString();
                         Intent intent = new Intent(context, JobDetails.class);
                         intent.putExtra("jobId",postId);
                         intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                         startActivity(intent);
                 }

             }catch (JSONException e){
                       e.printStackTrace();
             }
            }
        });

    }
}
