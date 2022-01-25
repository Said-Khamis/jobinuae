package com.duce.jobsinuae;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.duce.jobsinuae.Adapters.MyAdapter;
import com.duce.jobsinuae.Common.Config;
import com.duce.jobsinuae.Model.Posts;
import com.duce.jobsinuae.Volley.AppController;
import com.duce.jobsinuae.local.Session;
import com.duce.jobsinuae.local.UserInfoLocal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MaxAdViewAdListener, NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private MyAdapter myAdapter;
    private ArrayList<Posts> postsArrayList;
    private JsonArrayRequest jsonArrayRequest;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MaxAdView adView;
    private MaxInterstitialAd interstitialAd;
    private int retryAttempt;
    private TextView textView,textViewMore,textFullName,textEmail;
    private Session session;
    private Button button;
    private UserInfoLocal userInfoLocal;
    private int count = 0;
    private AlertDialog alertDialog;
    private int counter  = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = findViewById(R.id.swiperRefreshLayout);
        postsArrayList = new ArrayList<Posts>();

        userInfoLocal = new UserInfoLocal(getApplicationContext());

        textViewMore = findViewById(R.id.more);

        textViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, More.class));
            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.home));
        setSupportActionBar(toolbar);

        session = new Session(getApplicationContext());
        count = session.getClickActivityCount();
        int setCount = count + 1;
        if (session.getClickActivityCount() == 0){
            session.setActivityClick(setCount);
        }else {
            session.setActivityClick(setCount);
        }

        createInterstitialAd();

        sendRequestQueue();

        boolean isConnected = this.isNetworkAvailable();

        if (isConnected){
            /*---------  VOLLEY REQUEST QUEUE ---------*/
            sendRequestQueue();
            swipeRefreshLayout.setRefreshing(true);
        }else {
            new AlertDialog.Builder(this)
                    .setTitle("Connection Status")
                    .setMessage("No Network Connection. Please connect to the internet, then retry")
                    .setCancelable(false)
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .create()
                    .show();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                sendRequestQueue();
            }
        });

        /*--------- RECYCLE VIEW -- ADAPTER   ------*/
        myAdapter = new MyAdapter(postsArrayList , this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(myAdapter);

        /* ----------- DRAWER LAYOUT ----------*/
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        navigationView = (NavigationView) findViewById(R.id.navigationview);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        createBannerAd();

        //Toast.makeText(MainActivity.this, String.valueOf(session.getClickActivityCount()), Toast.LENGTH_SHORT).show();

    }

    void createInterstitialAd() {
        String interstitalAdUnits = getString(R.string.interstitialAdUnits);
        interstitialAd = new MaxInterstitialAd( interstitalAdUnits, this );
        interstitialAd.setListener( this );
        interstitialAd.loadAd();
    }

    void createBannerAd() {
        String bannerAdUnit = getString(R.string.bannerAdUnits);
        adView = new MaxAdView( bannerAdUnit, this );
        adView.setListener( this );
        int bannerHeight = getResources().getDimensionPixelSize(R.dimen.banner_height);
        adView.setLayoutParams( new FrameLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT,bannerHeight, Gravity.BOTTOM) );
        adView.setBackgroundColor(getResources().getColor(R.color.white));
        ViewGroup rootView = findViewById( android.R.id.content );
        rootView.addView( adView );
       // adView.loadAd();
    }

    public  boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager=(ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        return networkInfo != null;
    }

    private  void sendRequestQueue(){
        jsonArrayRequest = new JsonArrayRequest(Config.BASE_URL + "posts?_embed",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        postsArrayList.clear();

                        try {
                            for (int index = 0; index < response.length(); index++){
                                JSONObject jsonObject = response.getJSONObject(index);
                                GsonBuilder builder = new GsonBuilder();
                                Gson mGson = builder.create();
                                Posts posts = mGson.fromJson(jsonObject.toString(),Posts.class);
                                postsArrayList.add(posts);
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                        myAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                        textViewMore.setVisibility(View.VISIBLE);


                        /*-------- LOAD BANNER --------------*/
                        adView.loadAd();

                        if(session.getClickActivityCount() == 1 ||
                          session.getClickActivityCount() >= 2 || (session.getClickActivityCount() == 1 && session.getClickActivityCount() >= 2)
                         ){
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    if (interstitialAd.isReady()){
                                        interstitialAd.showAd();

                                        session.setActivityClick(0);
                                    }
                                }
                            },3000);

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
                textViewMore.setVisibility(View.INVISIBLE);
                if (error instanceof com.android.volley.NoConnectionError){
                    Toast.makeText(MainActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "Data fetching Error", Toast.LENGTH_SHORT).show();
                }

            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }


    @Override
    public void onAdExpanded(MaxAd ad) {

    }

    @Override
    public void onAdCollapsed(MaxAd ad) {

    }

    @Override
    public void onAdLoaded(MaxAd ad) {

    }

    @Override
    public void onAdDisplayed(MaxAd ad) {
    }

    @Override
    public void onAdHidden(MaxAd ad) {
    }

    @Override
    public void onAdClicked(MaxAd ad) {
        session.setActivityClick(0);
    }

    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {

    }

    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_home,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.sync){
            boolean isConnected =this.isNetworkAvailable();
            if(isConnected){
                swipeRefreshLayout.setRefreshing(true);
                sendRequestQueue();
            }else {
                new AlertDialog.Builder(this)
                        .setTitle("Connection Status")
                        .setMessage("No Network Connection. Please connect to the internet, then retry")
                        .setCancelable(false)
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .create()
                        .show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.home){
            startActivity(new Intent(MainActivity.this,MainActivity.class));
            drawerLayout.closeDrawers();

        }else if(id == R.id.categories){
            startActivity(new Intent(MainActivity.this,Categories.class));
            drawerLayout.closeDrawers();

        }else if (id == R.id.rateus){

            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID) ));

        }else if (id == R.id.privacy){

            Toast.makeText(MainActivity.this,"Soon",Toast.LENGTH_SHORT).show();

        }else  if(id == R.id.shareToFriend){
            Intent  intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_SUBJECT,"Online Jobs Search");
            intent.putExtra(Intent.EXTRA_TEXT,
                    "Hey, check out this app for new Jobs opportiunities. Just click https://play.google.com/store/apps/details?id="+ BuildConfig.APPLICATION_ID
                    );
            intent.setType("text/plain");
            startActivity(intent);
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}