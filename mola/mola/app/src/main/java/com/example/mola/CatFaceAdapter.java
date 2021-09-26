package com.example.mola;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CatFaceAdapter extends RecyclerView.Adapter<CatFaceAdapter.CustomViewHolder>{
    private ArrayList<CatFace> arrayList;
    private Context context;

    public CatFaceAdapter(ArrayList<CatFace> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CatFaceAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_photo, parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(arrayList.get(position).getProfile())
                .into(holder.iv_catFace);
        holder.catFaceAndDate.setText(arrayList.get(position).getTime());
    }


    @Override
    public int getItemCount() {
        //삼항연산자
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_catFace;
        TextView catFaceAndDate;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_catFace = itemView.findViewById(R.id.catfaceAndDate);
            this.catFaceAndDate = itemView.findViewById(R.id.catfaceAndDate);
        }
    }
}
