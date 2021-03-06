package com.duce.uaejobsearch;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.duce.uaejobsearch.Common.Config;
import com.duce.uaejobsearch.Model.Posts;
import com.duce.uaejobsearch.Volley.AppController;
import com.duce.uaejobsearch.local.Session;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JobDetails extends AppCompatActivity implements MaxAdViewAdListener {

    private TextView titleTextView,jobDescriptionsTextView,textViewDate;
    private Toolbar toolbar;
    private ImageLoader imageLoader;
    private ImageView networkImageView;
    private MaxAdView adView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String jobId;
    private MaxInterstitialAd interstitialAd;
    private Session session;
    private int count = 0;
    private AlertDialog alertDialog;
    public  int counter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);


        titleTextView = (TextView) findViewById(R.id.title);
        textViewDate = (TextView) findViewById(R.id.datePosted);
        jobDescriptionsTextView = (TextView) findViewById(R.id.jobDescriptions);
        networkImageView = (ImageView) findViewById(R.id.featuredImage);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.jobDetailsRefreshLayout);

        session = new Session(getApplicationContext());
        count = session.getClickActivityCount();
        int setCount = count + 1;
        if (session.getClickActivityCount() == 0){
            session.setActivityClick(setCount);
        }else {
            session.setActivityClick(setCount);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbarJobDetails);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 finish();
            }
        });

        Intent titleIntent = getIntent();
        jobId = titleIntent.getStringExtra("jobId");

        swipeRefreshLayout.setRefreshing(true);
        sendRequestQueue();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                sendRequestQueue();
            }
        });


        createInterstitialAd();

        boolean isConneced = this.isNetworkAvailable();
        if(isConneced){
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

        createBannerAd();


    }


    void createBannerAd() {
        String bannerAdUnit = getString(R.string.bannerAdUnits);
        adView = new MaxAdView( bannerAdUnit, this );
        adView.setListener( this );
        int bannerHeight = getResources().getDimensionPixelSize(R.dimen.banner_height);
        adView.setLayoutParams( new FrameLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT,bannerHeight, Gravity.BOTTOM) );
        ViewGroup rootView = findViewById( android.R.id.content );
        rootView.addView( adView );
    }

    void createInterstitialAd() {
        String interstitalAdUnits = getString(R.string.interstitialAdUnits);
        interstitialAd = new MaxInterstitialAd( interstitalAdUnits, this );
        interstitialAd.setListener( this );
        interstitialAd.loadAd();
    }

    public  boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.job_details,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.share){
            Intent  intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_SUBJECT,"Online Jobs Search");
            intent.putExtra(Intent.EXTRA_TEXT,
                    "Hey, check out this app for new Jobs Opportiunities. Just click https://play.google.com/store/apps/details?id="+ BuildConfig.APPLICATION_ID + " new Jobs opportiunity "
            );
            intent.setType("text/plain");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendRequestQueue(){
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

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Config.BASE_URL + "posts/" + jobId + "?_embed",
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                //jobDescriptionsTextView.setText(Html.fromHtml(posts.getContent().getRendered()));

                                GsonBuilder builder = new GsonBuilder();
                                Gson mGson = builder.create();
                                Posts posts = mGson.fromJson(response.toString(), Posts.class);
                                titleTextView.setText(posts.getTitle().getRendered().toUpperCase(Locale.ROOT).toString());

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    jobDescriptionsTextView.setText(Html.fromHtml(posts.getContent().getRendered(), Html.FROM_HTML_MODE_COMPACT));
                                } else {
                                    jobDescriptionsTextView.setText(Html.fromHtml(posts.getContent().getRendered()));
                                }

                                //toolbar.setTitle(posts.getTitle().getRendered().toString());

                                String date= posts.getDate().toString();
                                final SimpleDateFormat ymdFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                                final SimpleDateFormat EEEddMMMyyyy = new SimpleDateFormat("EEE, d MMM yyyy", Locale.US);
                                String outputDateStr = parseDate(date, ymdFormat, EEEddMMMyyyy);
                                textViewDate.setText(outputDateStr);
                                textViewDate.setVisibility(View.VISIBLE);

                                if(posts.getEmbedded().getWpFeaturedmedia().size() == 0){
                                    Picasso.get()
                                            .load("https://image.freepik.com/free-vector/error-404-concept-illustration_114360-1811.jpg")
                                            .into(networkImageView);
                                }else {
                                    Picasso.get()
                                            .load(posts.getEmbedded().getWpFeaturedmedia().get(0).getSourceUrl().toString())
                                            .into(networkImageView);
                                }

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

                                }
                                Log.i("JobsDetails", response.toString());
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (error instanceof com.android.volley.NoConnectionError){
                                    Toast.makeText(JobDetails.this, "Network Error", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(JobDetails.this, "Data fetching Error", Toast.LENGTH_SHORT).show();
                                }
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                });

                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                AppController.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
            }
        });
    }

    /*void showShackBar(){
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

    }*/

    public static String parseDate(String inputDateString, SimpleDateFormat inputDateFormat, SimpleDateFormat outputDateFormat) {
        Date date = null;
        String outputDateString = null;
        try {
            date = inputDateFormat.parse(inputDateString);
            outputDateString = outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputDateString;
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
    public void onAdExpanded(MaxAd ad) {

    }

    @Override
    public void onAdCollapsed(MaxAd ad) {

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

