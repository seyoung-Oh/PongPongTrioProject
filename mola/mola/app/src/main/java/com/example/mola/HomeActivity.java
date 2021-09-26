package com.example.mola;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {
    Button btn_record;
    Button btn_chart;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://mola-c4bdf.appspot.com");
        StorageReference storageRef = storage.getReference();

        for(int i =1;i<=8;i++) {
            ImageView load;
            final int num = i;
            load = (ImageView) findViewById(getResources().getIdentifier("loadimg_" + num, "id", "com.example.mola"));
            storageRef.child("image_store/catImg_"+num+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    System.out.println(num);
                    Glide.with(getApplicationContext())
                            .load(uri)
                            .into(load);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                    //이미지 로드 실패시
                  //  Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                }
            });


        }

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference rest = database.child("catfood").child("rest").child("num");

        PieChart mPieChart = (PieChart) findViewById(R.id.piechart);
        final int[] chartNum = {0};
        final int[] last = {0};
        rest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                System.out.println("시작");
                int mynum = snapshot.getValue(Integer.class);
                chartNum[0] = mynum;
                System.out.println(mynum);
                System.out.println(last[0]);
                mPieChart.clearChart();
                mPieChart.addPieSlice(new PieModel("최대 급여 횟수", chartNum[0], Color.parseColor("#ffb8b8")));
                mPieChart.addPieSlice(new PieModel("현재까지 먹은 사료", 100-chartNum[0], Color.parseColor("#fff4f4")));

                if(last[0] != chartNum[0] && last[0] != 0 && last[0] - chartNum[0] > 0) {
                    createNotification();
                }
                last[0] = mynum;
                mPieChart.startAnimation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });



        btn_record = findViewById(R.id.btn_record);
        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RecordActivity.class);
                startActivity(intent);
            }
        });
        btn_chart = findViewById(R.id.btn_chart);
        btn_chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ChartActivity.class);
                startActivity(intent);
            }
        });


    }



    public void createNotification() {
        Intent intent = new Intent(this, RecordActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        Date date_now = new Date(System.currentTimeMillis());
        SimpleDateFormat simpl = new SimpleDateFormat("yyyy년 MM월 dd일 aa hh시 mm분 ss초");
        String dateString = simpl.format(date_now);

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("고양이가 사료를 섭취했습니다.");
        builder.setContentText(dateString);

        builder.setColor(Color.RED);
        // 사용자가 탭을 클릭하면 자동 제거
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }

        // id값은
        // 정의해야하는 각 알림의 고유한 int값
        notificationManager.notify(1, builder.build());



    }



}