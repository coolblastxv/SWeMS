package com.project.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Eat_act extends AppCompatActivity {
    String userID;
    FirebaseFirestore fstore;
    private ImageButton eatgain;
    ProgressBar simpleProgressBar;
    Calendar calendar;
    int progress;
    int max;
    Button foodtrack;
    TextView eattarget, eaten;
    private FirestoreRecyclerAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eat);
        simpleProgressBar = findViewById(R.id.eatBar);
        foodtrack = findViewById(R.id.foodtrack);
        eaten = findViewById(R.id.eaten);
        eattarget = findViewById(R.id.eattarget);

        simpleProgressBar.setMin(0);
        
        calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH));
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        getUserDocument().collection("activity").document(day + "-" + month + "-" + year).update("calorie", FieldValue.increment(0));        getUserDocument().get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            try {
                                if (document.exists()) {
                                    Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                                    max = document.getDouble("calorie").intValue();
                                    Log.d("TAG", String.valueOf(document.getDouble("calorie")));
                                    simpleProgressBar.setMax(max);
                                    eattarget.setText(String.valueOf(simpleProgressBar.getMax()));


                                } else {
                                    Log.d("TAG", "No such document");
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("calorie",0);
                                    getUserDocument().collection("activity").document(day + "-" + month + "-" + year).set(data);
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
        getUserDocument().collection("activity").document(day + "-" + month + "-" + year).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            try {
                                if (document.exists()) {
                                    Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                                    progress =  Objects.requireNonNull(document.getLong("calorie")).intValue();
                                    Log.d("TAG", String.valueOf(document.getDouble("calorie")));
                                    simpleProgressBar.setProgress(progress);
                                    eaten.setText(String.valueOf(simpleProgressBar.getProgress()));
                                    foodtrack.setOnClickListener(v -> {
                                        Eat_act.this.openDialog();
                                        progress =  Objects.requireNonNull(document.getLong("calorie")).intValue();
                                        simpleProgressBar.getProgress();
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



        RecyclerView flist = findViewById(R.id.foodlist);
        Query query = getUserDocument().collection("activity").document(day + "-" + month + "-" + year)
                .collection("food")
                .limit(20);
        FirestoreRecyclerOptions<foodEntry> options = new FirestoreRecyclerOptions.Builder<foodEntry>()
                .setQuery(query,foodEntry.class)
                .setLifecycleOwner(this)
                .build();

        adapter = new FirestoreRecyclerAdapter<foodEntry, Eat_act.foodEntryVH>(options) {


            @Override
            public Eat_act.foodEntryVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_entry, parent, false);
                return new Eat_act.foodEntryVH(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull Eat_act.foodEntryVH holder, int position, @NonNull foodEntry model) {

                holder.food_view.setText(model.getFood() );
                holder.calorie_view.setText(model.getCalorie() + "");


            }
        };
        flist.setHasFixedSize(true);
        flist.setLayoutManager(new LinearLayoutManager(this));
        flist.setAdapter(adapter);
    }
    public void onBackPressed() {
        startActivity(new Intent(Eat_act.this
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
        foodtrack exampleDialog = new foodtrack();
        exampleDialog.show(getSupportFragmentManager(),"example dialog");

    }
    private static class foodEntryVH extends RecyclerView.ViewHolder{

        private final TextView food_view;
        private final TextView calorie_view;

        public foodEntryVH(@NonNull View itemView) {
            super(itemView);

            food_view = itemView.findViewById(R.id.foodView);
            calorie_view = itemView.findViewById(R.id.calorieView);

        }
    }
}