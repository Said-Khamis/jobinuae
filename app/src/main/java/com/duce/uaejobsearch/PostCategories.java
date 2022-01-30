package com.duce.uaejobsearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.duce.uaejobsearch.local.Session;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.duce.uaejobsearch.Adapters.MoreAdapter;
import com.duce.uaejobsearch.Common.Config;
import com.duce.uaejobsearch.Model.Posts;
import com.duce.uaejobsearch.Volley.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PostCategories extends AppCompatActivity implements MaxAdViewAdListener {

    private TextView catId;
    private String categoryId,categoryTitle;
    private MoreAdapter moreAdapter;
    private ArrayList<Posts> postsCatArrayList;
    private Toolbar toolbar;
    private MaxInterstitialAd interstitialAd;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Session session;
    private AlertDialog alertDialog;
    private int counter = 0;
    private MaxAdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_categories);

        Intent titleIntent = getIntent();
        categoryId = titleIntent.getStringExtra("categoryId");
        categoryTitle = titleIntent.getStringExtra("categoryTitle");

        swipeRefreshLayout = findViewById(R.id.swiperRefreshLayoutMore);
        postsCatArrayList = new ArrayList<Posts>();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                boolean isConnected = isNetworkAvailable();

                if(isConnected){

                    sendRequestQueue();
                }else {
                    new AlertDialog.Builder(PostCategories.this)
                            .setTitle("Connection Status")
                            .setMessage("No Network Connection. Please connect to the internet, then retry")
                            .setCancelable(false)
                            .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = getIntent();
                                    startActivity(intent);
                                }
                            })
                            .create()
                            .show();

                    swipeRefreshLayout.setRefreshing(true);
                }
            }
        });

        session = new Session(getApplicationContext());
        int count = session.getClickActivityCount();
        int setCount = count + 1;
        if (session.getClickActivityCount() == 0){
            session.setActivityClick(setCount);
        }else {
            session.setActivityClick(setCount);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbarPostCat);
        toolbar.setTitle(" ");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        createInterstitialAd();

        /*---------
                         RECYCLEVIEW - ADAPTER
                                                         ------*/
        moreAdapter = new MoreAdapter(postsCatArrayList, this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycleViewMore);
        //recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(moreAdapter);


        /*---- BANNER AD APPLOVIN   -----------*/
        createBannerAd();

        boolean isConnected = this.isNetworkAvailable();

        if(isConnected){
            sendRequestQueue();
        }else {
            new AlertDialog.Builder(this)
                    .setTitle("Connection Status")
                    .setMessage("No Network Connection. Please connect to the internet, then retry")
                    .setCancelable(false)
                    .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                              Intent intent = getIntent();
                              startActivity(intent);
                        }
                    })
                    .create()
                    .show();
        }

        //Toast.makeText(PostCategories.this, String.valueOf(session.getClickActivityCount()), Toast.LENGTH_SHORT).show();

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
        //adView.loadAd();
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

    private  void sendRequestQueue(){
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(new Runnable() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                    }
                });
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                        Config.BASE_URL + "posts?_embed&categories=" + categoryId,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {

                                swipeRefreshLayout.setRefreshing(true);

                                postsCatArrayList.clear();

                                try {
                                    for (int index = 0; index < response.length(); index++){
                                        JSONObject jsonObject = response.getJSONObject(index);
                                        GsonBuilder builder = new GsonBuilder();
                                        Gson mGson = builder.create();
                                        Posts posts = mGson.fromJson(jsonObject.toString(),Posts.class);
                                        Log.i("MainActivity", posts.getTitle().getRendered().toString());

                                        toolbar.setTitle(categoryTitle);

                                        postsCatArrayList.add(posts);
                                    }
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }
                                moreAdapter.notifyDataSetChanged();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(false);
                                    }
                                });

                                /*-------- LOAD BANNER --------------*/
                                adView.loadAd();

                                if (session.getClickActivityCount() >= 2){

                                  runOnUiThread(new Runnable() {
                                      @Override
                                      public void run() {
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
                                  });

                                    session.setActivityClick(0);
                                }
                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (error instanceof com.android.volley.NoConnectionError){
                                    Toast.makeText(PostCategories.this, "Network Error", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(PostCategories.this, "Data fetching Error", Toast.LENGTH_SHORT).show();
                                }
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                });

                jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                AppController.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);
            }
        });
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

    private void filter(String text) {
        ArrayList<Posts> filteredlist = new ArrayList<>();
        for (Posts item : postsCatArrayList) {
            if (item.getTitle().getRendered().toLowerCase().contains(text.toLowerCase())) {

                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {

            moreAdapter.filterList(filteredlist);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}