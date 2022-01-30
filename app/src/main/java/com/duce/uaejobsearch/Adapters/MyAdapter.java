package com.duce.uaejobsearch.Adapters;

import android.content.Context;

import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.toolbox.ImageLoader;
import com.google.android.material.card.MaterialCardView;
import com.duce.uaejobsearch.JobDetails;
import com.duce.uaejobsearch.Model.Posts;
import com.duce.uaejobsearch.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>  {
    private ArrayList<Posts> postsArrayList;
    private ImageLoader imageLoader;
    private Context context;


    public void filterList(ArrayList<Posts> filterllist) {
        postsArrayList = filterllist;
        notifyDataSetChanged();
    }

    public MyAdapter(ArrayList<Posts> posts , Context context) {
         this.postsArrayList = posts;
         this.context = context;
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView,datePosted,postInShort;
        private ImageView networkImageView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            datePosted = (TextView) itemView.findViewById(R.id.datePosted);
            textView = (TextView) itemView.findViewById(R.id.textTitle);
            postInShort = (TextView) itemView.findViewById(R.id.postInShort);
            networkImageView = (ImageView) itemView.findViewById(R.id.imageNetwork);
            //materialCardView = (MaterialCardView) itemView.findViewById(R.id.cardJob);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), JobDetails.class);
                    intent.putExtra("jobId",postsArrayList.get(getAdapterPosition()).getId().toString());
                    view.getContext().startActivity(intent);
                }
            });
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_jobs_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.postInShort.setText(
                    !postsArrayList.get(position).getContent().getRendered().toString().equals(" ")
                            ? Html.fromHtml(postsArrayList.get(position).getContent().getRendered()).toString().substring(0,40) + "...."
                            : " "
            );
            holder.datePosted.setText(
                    !postsArrayList.get(position).getDate().toString().equals(" ")
                            ? getFormatedDate(postsArrayList.get(position).getDate().toString())
                            : " "
            );
              holder.textView.setText(
                      !postsArrayList.get(position).getTitle().getRendered().toString().equals(" ")
                      ? postsArrayList.get(position).getTitle().getRendered().toString()
                              : " "
              );
              if(postsArrayList.get(position).getEmbedded().getWpFeaturedmedia().size() == 0){
                  Picasso.get()
                          .load("https://image.freepik.com/free-vector/error-404-concept-illustration_114360-1811.jpg")
                          .placeholder(R.drawable.splashicon)
                          .error(R.drawable.splashicon)
                          .into(holder.networkImageView);
              }else {
                  Picasso.get()
                          .load(postsArrayList.get(position).getEmbedded().getWpFeaturedmedia().get(0).getSourceUrl().toString())
                          .placeholder(R.drawable.splashicon)
                          .error(R.drawable.splashicon)
                          .into(holder.networkImageView);
              }


      /*
           holder.materialCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), JobDetails.class);
                intent.putExtra("jobId",postsArrayList.get(holder.getAdapterPosition()).getId().toString());
                v.getContext().startActivity(intent);
              }
          });
          */
    }

    @Override
    public int getItemCount() {
        return Math.min(10,postsArrayList.size());
    }

    public String  getFormatedDate(String date){
        final SimpleDateFormat ymdFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        final SimpleDateFormat EEEddMMMyyyy = new SimpleDateFormat("EEE, d MMM yyyy", Locale.US);
        return parseDate(date, ymdFormat, EEEddMMMyyyy);
    }

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

}
