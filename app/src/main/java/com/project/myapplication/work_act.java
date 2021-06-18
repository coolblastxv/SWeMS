package com.project.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Objects;

public class work_act extends AppCompatActivity {
    String userID;
    FirebaseFirestore fstore;

    ProgressBar simpleProgressBar;
    Calendar calendar;
    int progress;
    int max;
    Button worktrack;
    TextView worktarget, worked;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);
        simpleProgressBar = findViewById(R.id.workBar);
        worktrack = findViewById(R.id.worktrack);
        worked = findViewById(R.id.workedout);
        worktarget = findViewById(R.id.worktarget);

        simpleProgressBar.setMin(0);
        simpleProgressBar.setMax(339);
        worktarget.setText(String.valueOf(simpleProgressBar.getMax()));

        calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH));
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        getUserDocument().collection("activity").document(day + "-" + month + "-" + year).update("burned", FieldValue.increment(0));
        getUserDocument().collection("activity").document(day + "-" + month + "-" + year).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            try {
                                if (document.exists()) {
                                    Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                                    progress =  Objects.requireNonNull(document.getLong("burned")).intValue();
                                    Log.d("TAG", String.valueOf(document.getDouble("burned")));
                                    simpleProgressBar.setProgress(progress);
                                    worked.setText(String.valueOf(simpleProgressBar.getProgress()));
                                    worktrack.setOnClickListener(v -> {
                                        work_act.this.openDialog();
                                        progress =  Objects.requireNonNull(document.getLong("burned")).intValue();
                                        simpleProgressBar.setProgress(progress);
                                    });

                                } else {
                                    Log.d("TAG", "No such document");
                                }
                            }
                            catch(Exception e) {
                                Log.w("TAG"," no calories");
                            }
                        }
                        else{
                            Log.d("TAG", "get failed with ", task.getException());
                        }
                    }
                });
    }
    public void onBackPressed() {
        startActivity(new Intent(work_act.this
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
    public void openDialog(){
        worktrack exampleDialog = new worktrack();
        exampleDialog.show(getSupportFragmentManager(),"example dialog");

    }
}