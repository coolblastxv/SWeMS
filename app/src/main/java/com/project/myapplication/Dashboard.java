package com.project.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.type.DateTime;

import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class Dashboard extends AppCompatActivity {
    private long backPressedTime;
    private Toast backToast;
    LinearLayout workoutPlan;
    LinearLayout drinkPlan;
    LinearLayout eatPlan;
    TextView maintain;
    TextView mildLoss;
    TextView loss;
    TextView extremeLoss;
    String userID;
    FirebaseFirestore fstore;
    private Double height;
    private Double weight;
    private Double age;
    FirebaseAuth fAuth;
    Double AMR;
    Double AMR2;
    Double AMR3;
    Double AMR4;
    Double BMR= Double.valueOf(0);
    FirebaseAuth firebaseAuth ;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH));
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        workoutPlan = findViewById(R.id.workoutPlan);
        workoutPlan.setOnClickListener(v -> {
            DocumentReference documentReference = getUserDocument().collection("activity").document(day + "-" + month + "-" + year);
            Map<String, Object> data = new HashMap<>();
            data.put("sample",0);
            documentReference.set(data, SetOptions.merge());
            startActivity(new Intent(Dashboard.this
                    ,work_act.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        });
        drinkPlan = findViewById(R.id.drinkPlan);
        drinkPlan.setOnClickListener(v -> {
            DocumentReference documentReference = getUserDocument().collection("activity").document(day + "-" + month + "-" + year);
            Map<String, Object> data = new HashMap<>();
            data.put("sample",0);
            documentReference.set(data, SetOptions.merge());

            startActivity(new Intent(Dashboard.this
                    ,Drink_act.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        });
        eatPlan = findViewById(R.id.eatPlan);
        eatPlan.setOnClickListener(v -> {
            DocumentReference documentReference = getUserDocument().collection("activity").document(day + "-" + month + "-" + year);
            Map<String, Object> data = new HashMap<>();
            data.put("sample",0);
            documentReference.set(data, SetOptions.merge());
            startActivity(new Intent(Dashboard.this
                    ,Eat_act.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        });

        maintain = findViewById(R.id.maintain);
        mildLoss = findViewById(R.id.mild);
        loss = findViewById(R.id.loss);
        extremeLoss = findViewById(R.id.extreme);


        getUserDocument().addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("TAG", "Listen failed.", error);
                    return;
                }
                if (value != null && value.exists()) {
                    height = value.getDouble("height");
                    age = value.getDouble("age");


                } else {
                }
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();

       getUserDocument().collection("weights")
               .orderBy("timestamp",Query.Direction.DESCENDING)

                .limit(1)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
           @Override
           public void onComplete(@NonNull Task<QuerySnapshot> task) {
               if (task.isSuccessful()) {
                   for (QueryDocumentSnapshot document : task.getResult()) {
                       Log.d("TAG", document.getId() + " => " + document.getData());
                       weight = document.getDouble("weight");
                       Date timestamp = document.getTimestamp("timestamp").toDate();

                       Log.d("TAG", String.valueOf(document.getDouble("weight")));
                       Log.d("height",String.valueOf(height));
                       Log.d("age",String.valueOf(age));

                       BMR = (10*weight) + (6.25*height) - (5*age) + 5;
                       AMR =BMR * 1.2;
                       AMR2 = AMR * (0.89);
                       AMR3 = AMR * (0.78);
                       AMR4 = AMR * (0.55);

                       maintain.setText(String.format("%.2f", AMR));
                       mildLoss.setText(String.format("%.2f", AMR2));
                       loss.setText(String.format("%.2f", AMR3));
                       extremeLoss.setText(String.format("%.2f", AMR4));
                       userID = firebaseAuth.getCurrentUser().getUid();
                       DocumentReference documentReference = fstore.collection("users").document(userID);
                       Map<String, Object> user = new HashMap<>();
                       documentReference.update("calorie",AMR3);


                   }
               } else {
                   Log.d("TAG", "Error getting documents: ", task.getException());
               }
           }
       });





        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.dashboard);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext()
                                ,MainActivity .class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.dashboard:
                        return true;

                    case R.id.trend:
                        startActivity(new Intent(getApplicationContext()
                                , Trend.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.me:
                        startActivity(new Intent(getApplicationContext()
                                ,Me.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }


    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressedTime = System.currentTimeMillis();
    }
    @Override
    protected void onStop() {

        super.onStop();
        finish();
    }
    private DocumentReference getUserDocument() {
        fstore = FirebaseFirestore.getInstance();
        CollectionReference users = fstore.collection("users");
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return users.document(userID);
    }}