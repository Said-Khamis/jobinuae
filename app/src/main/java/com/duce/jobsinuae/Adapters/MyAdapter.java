package com.duce.jobsinuae.Adapters;

import android.content.Context;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.toolbox.ImageLoader;
import com.google.android.material.card.MaterialCardView;
import com.duce.jobsinuae.JobDetails;
import com.duce.jobsinuae.Model.Posts;
import com.duce.jobsinuae.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


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
        private TextView textView;
        private ImageView networkImageView;
        private MaterialCardView materialCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textTitle);
            networkImageView = (ImageView) itemView.findViewById(R.id.imageNetwork);
            materialCardView = (MaterialCardView) itemView.findViewById(R.id.cardJob);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.jobs_list,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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

          holder.materialCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), JobDetails.class);
                intent.putExtra("jobId",postsArrayList.get(holder.getAdapterPosition()).getId().toString());
                v.getContext().startActivity(intent);
              }
          });
    }

    @Override
    public int getItemCount() {
        return Math.min(10,postsArrayList.size());
    }

}
