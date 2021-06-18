package com.project.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Drink_act extends AppCompatActivity {
    String userID;
    FirebaseFirestore fstore;
    ImageButton plus;
    ImageButton minus;
    TextView drinked,drinktarget,drinkcount;
    ProgressBar simpleProgressBar;
    Calendar calendar;
    int progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink);
        simpleProgressBar= findViewById(R.id.drinkBar);
        plus = findViewById(R.id.plusdrink);
        minus = findViewById(R.id.minusdrink);
        drinked = findViewById(R.id.drinked);
        drinktarget = findViewById(R.id.drinkTarget);
        drinkcount = findViewById(R.id.drinkcount);


       calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH));
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        getUserDocument().collection("activity").document(day + "-" + month + "-" + year).update("drink", FieldValue.increment(0));
        getUserDocument().collection("activity").document(day + "-" + month + "-" + year).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            try {
                                assert document != null;
                                if (document.exists()) {
                                    Log.d("TAG", "DocumentSnapshot data: " + document.getData());

                                    progress = Integer.parseInt(String.valueOf(document.get("drink")));


                                    plus.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            simpleProgressBar.setProgress(progress++);

                                            Map<String, Object> data = new HashMap<>();
                                            data.put("drink", simpleProgressBar.getProgress());
                                            getUserDocument().collection("activity").document(day + "-" + month + "-" + year).set(data, SetOptions.merge());
                                            drinkcount.setText(String.valueOf(simpleProgressBar.getProgress()));
                                            drinked.setText(String.valueOf(simpleProgressBar.getProgress()));
                                        }
                                    });
                                    minus.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            simpleProgressBar.setProgress(progress--);
                                            ;

                                            Map<String, Object> data = new HashMap<>();
                                            data.put("drink", simpleProgressBar.getProgress());
                                            getUserDocument().collection("activity").document(day + "-" + month + "-" + year).set(data, SetOptions.merge());
                                            drinkcount.setText(String.valueOf(simpleProgressBar.getProgress()));
                                            drinked.setText(String.valueOf(simpleProgressBar.getProgress()));
                                        }

                                    });




                                } else {
                                    Log.d("TAG", "No such document");
                                    getUserDocument().collection("activity").document(day + "-" + month + "-" + year).update("drink",0);
                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.d("TAG", "get failed with ", task.getException());
                        }
                    }
                });
        simpleProgressBar.setMax(12);
        drinktarget.setText(String.valueOf(simpleProgressBar.getMax()));

    }
    public void onBackPressed() {
        startActivity(new Intent(Drink_act.this
                ,Dashboard.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

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
    }

}