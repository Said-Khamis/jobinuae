package com.duce.jobsinuae;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.SearchView;
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
import com.duce.jobsinuae.local.Session;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.duce.jobsinuae.Adapters.CategoriesAdapter;
import com.duce.jobsinuae.Common.Config;
import com.duce.jobsinuae.Model.CategoriesPost;
import com.duce.jobsinuae.Volley.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Categories extends AppCompatActivity implements MaxAdViewAdListener {

    private ArrayList<CategoriesPost> postsArrayList;
    private CategoriesAdapter catetegoryAdaper;
    private JsonArrayRequest jsonArrayRequest;
    private Toolbar toolbar;
    private MaxAdView adView;
    private Button button;
    private MaxInterstitialAd interstitialAd;
    private int retryAttempt;
    private SwipeRefreshLayout  swipeRefreshLayout;
    private Session session;
    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        postsArrayList = new ArrayList<CategoriesPost>();

        swipeRefreshLayout = findViewById(R.id.swiperCategories);


        toolbar = (Toolbar) findViewById(R.id.toolbarCat);
        toolbar.setTitle("Categories");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Categories.this, MainActivity.class));
            }
        });

        createInterstitialAd();

        session = new Session(getApplicationContext());
        int count = session.getClickActivityCount();
        int setCount = count + 1;
        if (session.getClickActivityCount() == 0){
            session.setActivityClick(setCount);
        }else {
            session.setActivityClick(setCount);
        }

        boolean isConneced = this.isNetworkAvailable();

        if(isConneced){
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
            swipeRefreshLayout.setRefreshing(false);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(isConneced){
                    swipeRefreshLayout.setRefreshing(true);
                    sendRequestQueue();
                }else {
                    new AlertDialog.Builder(Categories.this)
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
        });
        /*--------- RECYCLE VIEW -- ADAPTER   ------*/
        catetegoryAdaper = new CategoriesAdapter(postsArrayList , this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycleViewCat);
        //recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(catetegoryAdaper);

        createBannerAd();

        //Toast.makeText(Categories.this, String.valueOf(session.getClickActivityCount()), Toast.LENGTH_SHORT).show();


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

    }


    public  boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbarmenu,menu);
        MenuItem menuItem = menu.findItem(R.id.searchView);

        SearchView searchView = ( SearchView) menuItem.getActionView();
        searchView.setQueryHint("Searching.......");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void filter(String text) {
        ArrayList<CategoriesPost> filteredlist = new ArrayList<>();
        for (CategoriesPost item : postsArrayList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            catetegoryAdaper.filterList(filteredlist);
        }
    }

    void showShackBar(){
        AlertDialog.Builder settingdialog = new AlertDialog.Builder(this,R.style.MyDialog);
        LayoutInflater inflater = this.getLayoutInflater();
        View settinview = inflater.inflate(R.layout.snackbar_lay, null);

        final TextView countime = settinview.findViewById(R.id.count_down);

        settingdialog.setView(settinview);
        alertDialog = settingdialog.create();

        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.show();

        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT);

        new CountDownTimer(6000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countime.setText(String.valueOf(millisUntilFinished/1000));
            }
            @Override
            public void onFinish() {
                alertDialog.dismiss();
            }
        }.start();

    }

    private  void sendRequestQueue(){
        jsonArrayRequest = new JsonArrayRequest(
                Config.BASE_URL + "categories",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        swipeRefreshLayout.setRefreshing(true);

                        postsArrayList.clear();

                        try {
                            for (int index = 0; index < response.length(); index++){
                                JSONObject jsonObject = response.getJSONObject(index);
                                GsonBuilder builder = new GsonBuilder();
                                Gson mGson = builder.create();
                                CategoriesPost posts = mGson.fromJson(jsonObject.toString(),CategoriesPost.class);
                                //Log.i("MainActivity", posts.getTitle().getRendered().toString());
                                postsArrayList.add(posts);
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        catetegoryAdaper.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);

                        adView.loadAd();

                        if (session.getClickActivityCount() >= 2){

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

                if (error instanceof com.android.volley.NoConnectionError){
                   Toast.makeText(Categories.this, "Network Error", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Categories.this, "Data fetching Error", Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        swipeRefreshLayout.setRefreshing(false);

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
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        //interstitialAd.destroy();
        super.onDestroy();
    }

}