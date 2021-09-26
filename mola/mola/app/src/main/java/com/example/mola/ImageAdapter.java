package com.example.mola;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.anni.uploaddataexcelsheet.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

//    private ArrayList<String> imageList;
    private ArrayList<CatFace> date;
    public ImageAdapter(ArrayList<CatFace> date, Context context) {
//        this.imageList = imageList;
        this.date = date;
        this.context = context;
    }

    private Context context;
    @NonNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_photo,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ViewHolder holder, int position) {
// loading the images from the position
        Glide.with(holder.itemView.getContext()).load(date.get(position).getProfile()).into(holder.imageView);
        holder.tv_eating.setText(String.valueOf(date.get(position).getTime()));
    }

    @Override
    public int getItemCount() {
        return (date != null ? date.size() : 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tv_eating;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_eating = itemView.findViewById(R.id.tv_eating);
            this.imageView=itemView.findViewById(R.id.item);
        }
    }
}