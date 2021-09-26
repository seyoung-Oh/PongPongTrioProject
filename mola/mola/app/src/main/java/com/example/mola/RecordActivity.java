package com.example.mola;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class RecordActivity extends AppCompatActivity {

    Button btn_record;
    ArrayList<String> imagelist;
    ArrayList<CatFace> date;
    RecyclerView recyclerView;
    StorageReference root;
    // ProgressBar progressBar;
    ImageAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        imagelist=new ArrayList<String>();
        date = new ArrayList<CatFace>();
        recyclerView=findViewById(R.id.rv_time);

        adapter=new ImageAdapter(date,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
// progressBar=findViewById(R.id.progress);
// progressBar.setVisibility(View.VISIBLE);
        StorageReference listRef = FirebaseStorage.getInstance("gs://mola-c4bdf.appspot.com").getReference().child("image_store");
        listRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference file:listResult.getItems()){
                    CatFace cf = new CatFace();
                    file.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                        @Override
                        public void onSuccess(StorageMetadata storageMetadata) {
                            Calendar calendar = Calendar.getInstance();
                            Date timeInDate = new Date(storageMetadata.getCreationTimeMillis());
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                            String timeInFormat = sdf.format(timeInDate);
// Log.e("metadata", timeInFormat);
// CatFoodRest cfr = ss.getValue(CatFoodRest.class);//만들어 뒀던 객체에 데이터를 담는다.
                            cf.setTime(timeInFormat);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
// Uh-oh, an error occurred!
                        }
                    });
                    file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            cf.setProfile(uri.toString());
//                            imagelist.add(uri.toString());
                            Log.e("Itemvalue",uri.toString());
                            date.add(cf);
// System.out.println("완료1");

                        }
                    }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            recyclerView.setAdapter(adapter);
// System.out.println("완료2");
// progressBar.setVisibility(View.GONE);
                        }
                    });


                }
            }
        });

    }
}