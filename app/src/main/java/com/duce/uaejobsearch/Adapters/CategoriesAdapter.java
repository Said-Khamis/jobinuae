package com.duce.uaejobsearch.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.duce.uaejobsearch.Model.CategoriesPost;
import com.duce.uaejobsearch.PostCategories;
import com.duce.uaejobsearch.R;
import java.util.ArrayList;
import java.util.Locale;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
    private ArrayList<CategoriesPost> postsArrayList;
    private Context context;

    public CategoriesAdapter(ArrayList<CategoriesPost> categories , Context context) {
        this.postsArrayList = categories;
        this.context = context;
    }

    public void filterList(ArrayList<CategoriesPost> filterllist) {
        postsArrayList = filterllist;
        notifyDataSetChanged();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private TextView textViewCount, textViewLeading;
        private CardView cardView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.categoryTitle);
            textViewCount = (TextView) itemView.findViewById(R.id.count);
            textViewLeading = (TextView) itemView.findViewById(R.id.textLeading);
            cardView = (CardView) itemView.findViewById(R.id.cardCategory);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_list,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
           holder.textView.setText(postsArrayList.get(position).getName().toString());
           holder.textViewCount.setText(postsArrayList.get(position).getCount().toString());
           holder.textViewLeading.setText(postsArrayList.get(position).getName().substring(0,1).toUpperCase(Locale.ROOT).toString());

          holder.cardView.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Intent intent = new Intent(v.getContext(), PostCategories.class);
                  intent.putExtra("categoryId",postsArrayList.get(holder.getAdapterPosition()).getId().toString());
                  intent.putExtra("categoryTitle",postsArrayList.get(holder.getAdapterPosition()).getName().toString());
                  v.getContext().startActivity(intent);
              }
          });
    }


    @Override
    public int getItemCount() {
        return postsArrayList.size();
    }


}
