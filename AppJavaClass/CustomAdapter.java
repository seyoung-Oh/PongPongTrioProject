package com.example.mola;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private ArrayList<CatFoodRest> arrayList;
    private Context context;

    public CustomAdapter(ArrayList<CatFoodRest> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_time, parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
//        Glide.with(holder.itemView)
//                .load(arrayList.get(position).getProfile());
//                .into(holder.iv_profile);
        holder.tv_rest.setText(String.valueOf(arrayList.get(position).getRestT()));
        holder.tv_date.setText(arrayList.get(position).getDat());
    }

    @Override
    public int getItemCount() {
        //삼항연산자
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView tv_rest;
        TextView tv_date;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_rest = itemView.findViewById(R.id.tv_rest);
            this.tv_date = itemView.findViewById(R.id.tv_date);
        }
    }
}
