package com.duce.jobsinuae;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import com.duce.jobsinuae.local.Session;
import com.duce.jobsinuae.R;

public class SpalshScreen extends AppCompatActivity {

    private Session session;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh_screen);

        session = new Session(getApplicationContext());

        session.setActivityClick(0);

        progressBar = findViewById(R.id.progress_bar_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SpalshScreen.this,MainActivity.class));
                finish();
            }
        },5000);
    }

}