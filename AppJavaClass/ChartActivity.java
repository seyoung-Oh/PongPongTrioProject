package com.example.mola;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

//import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
//import java.util.HashMap;
import java.util.Locale;
//import java.util.Map;


public class ChartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<CatFoodRest> arrayList;
    private FirebaseDatabase fdb;
    private DatabaseReference databaseReference;

    Button btn_fulldialog;
    Button btn_restdialog;
    Button btn_goback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        recyclerView = findViewById(R.id.rv_chart); // 아이디 연결
        recyclerView.setHasFixedSize(true);//성능강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();//CFR 객체를 담을 리스트

        fdb = FirebaseDatabase.getInstance();//db연동
        databaseReference = fdb.getReference("feedTimeRec");//db테이블 연결
        //여기까지 했슈
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //firebase database의 데이터 받아오는 곳
                arrayList.clear(); //기존 배열 초기화
                for(DataSnapshot ss : snapshot.getChildren()){//반복문으로 데이터 list를 추출
                    CatFoodRest cfr = ss.getValue(CatFoodRest.class);//만들어 뒀던 객체에 데이터를 담는다.
                    arrayList.add(cfr); // 담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비
                }
                adapter.notifyDataSetChanged(); //리스트 저장 및 새로고침
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //디비를 가져오던 중 에러 발생시
                Log.e("ChartActivity", String.valueOf(error.toException()));
            }
        });
        adapter = new CustomAdapter(arrayList,this);
        recyclerView.setAdapter(adapter);//리사이클러뷰에 어댑터 연결



        final int[] chartNum = new int[1];
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference rest = database.child("catfood").child("rest").child("num");
        PieChart mPieChart = (PieChart) findViewById(R.id.piechart);
        rest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int mynum = snapshot.getValue(Integer.class);
                chartNum[0] = mynum;
                mPieChart.addPieSlice(new PieModel("최대 급여 횟수", 0+chartNum[0], Color.parseColor("#ffb8b8")));
                mPieChart.addPieSlice(new PieModel("현재까지 먹은 사료", 100-chartNum[0], Color.parseColor("#fff4f4")));

                mPieChart.startAnimation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });




        btn_fulldialog = (Button)findViewById(R.id.btn_full);
        btn_restdialog = (Button)findViewById(R.id.btn_rest);
        btn_goback = (Button)findViewById(R.id.btn_chart);
        btn_fulldialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder ad = new AlertDialog.Builder(ChartActivity.this);
                ad.setTitle("사료 채우기");
                ad.setMessage("사료를 채우시겠습니까?");

//                final EditText et = new EditText(ChartActivity.this);
//                ad.setView(et);

                ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //여기부터 firebase에 시간 저장
                        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("feedTimeRec");
                        String pattern = "MMMMM dd HH:mm";
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, new Locale("ko", "KR"));
                        String date = simpleDateFormat.format(new Date());
                        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference DateAndTime = database.child("feedTimeRec");
                        DateAndTime dt = new DateAndTime(5,date);
                        DateAndTime.push().setValue(dt);
                        //여기까지 시간 저장
                        rest.setValue(100);
                        chartNum[0] = 100;
                        mPieChart.clearChart();
//                        mPieChart.addPieSlice(new PieModel("최대 급여 횟수", chartNum[0], Color.parseColor("#ffb8b8")));
//                        mPieChart.addPieSlice(new PieModel("", 0, Color.parseColor("#fff4f4")));
//                        mPieChart.startAnimation();
                        dialogInterface.dismiss();
                    }
                });
                ad.show();
            }

        });
        btn_restdialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder ad = new AlertDialog.Builder(ChartActivity.this);
                ad.setTitle("사료 잔여량 조절");
                ad.setMessage("사료 잔여량 조절");
                final EditText et = new EditText(ChartActivity.this);
                ad.setView(et);
                ad.setMessage("/5");

                ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String a = et.getText().toString();
                        int val = Integer.parseInt(a);
                        chartNum[0] = val * 20;
                        rest.setValue(chartNum[0]);
                        if(val >=0 && val <= 5){
                            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("feedTimeRec");
                            String pattern = "MMMMM dd HH:mm";
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, new Locale("ko", "KR"));
                            String date = simpleDateFormat.format(new Date());
                            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                            DatabaseReference DateAndTime = database.child("feedTimeRec");
                            DateAndTime dt = new DateAndTime(val,date);
                            DateAndTime.push().setValue(dt);
                        }
                        mPieChart.clearChart();
//                        mPieChart.addPieSlice(new PieModel("최대 급여 횟수", chartNum[0], Color.parseColor("#ffb8b8")));
//                        mPieChart.addPieSlice(new PieModel("", 100-chartNum[0], Color.parseColor("#fff4f4")));
//                        mPieChart.startAnimation();
                        dialogInterface.dismiss();
                    }
                });
                ad.show();
            }
        });

        btn_goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(intent);
            }
        });

    }


}