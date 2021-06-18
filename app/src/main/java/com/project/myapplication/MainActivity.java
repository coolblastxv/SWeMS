package com.project.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    EditText input;
    private long backPressedTime;
    private Toast backToast;
    String userID;
    FirebaseFirestore fstore;
    private FirestoreRecyclerAdapter adapter;
    private Double height;
    FirebaseAuth firebaseAuth;
    Button fetch;
    TextView scalevalue;


    FloatingActionButton wentry;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fetch = findViewById(R.id.fetch);


        DocumentReference documentReference = getUserDocument();
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("TAG", "Listen failed.", error);
                    return;
                }
                if (value != null && value.exists()) {
                    height = value.getDouble("height");
                    Log.w("height", String.valueOf(height));
                } else {
                }
            }
        });

        DatabaseReference database = FirebaseDatabase.getInstance("https://swems-c1fa6-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("weight");
        scalevalue= findViewById(R.id.scalevalue);
        Log.w("TAG", String.valueOf(FirebaseDatabase.getInstance()));

            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    try {
                    if (dataSnapshot.exists()) {
                        Double weightval = (Double) dataSnapshot.child("value").getValue();
                        BigDecimal wd = new BigDecimal(weightval).setScale(2, RoundingMode.HALF_UP);
                        Log.w("weightvalue", String.valueOf(weightval));
                        scalevalue.setText(String.valueOf(wd));

                        fetch.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Date date = new Date();
                                Double bmi = weightval/(Math.pow(height/100,2));
                                BigDecimal bd = new BigDecimal(bmi).setScale(2, RoundingMode.HALF_UP);
                                double weight = wd.doubleValue();
                                double BMI = bd.doubleValue();
                                Map<String, Object> weightentry = new HashMap<>();
                                weightentry.put("weight", weight);
                                weightentry.put("timestamp", date);
                                weightentry.put("BMI", BMI);

                                getUserDocument()
                                        .collection("weights").add(weightentry);

                            }
                        });
                    } else {
                        Log.w("TAG", " no weight");
                    }
                    } catch(Exception e)
                    {
                        Log.w("TAG"," no weight");
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("TAG", "Listen failed.");

                }
            });


        RecyclerView wlist = findViewById(R.id.weightlist);


            Query query = getUserDocument().collection("weights")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(90);
            FirestoreRecyclerOptions<weightEntry> options = new FirestoreRecyclerOptions.Builder<weightEntry>()
                    .setQuery(query, weightEntry.class)
                    .setLifecycleOwner(this)
                    .build();

            adapter = new FirestoreRecyclerAdapter<weightEntry, weightEntryVH>(options) {
                @NonNull

                @Override
                public weightEntryVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weight_entry, parent, false);
                    return new weightEntryVH(view);
                }

                @Override
                protected void onBindViewHolder(@NonNull weightEntryVH holder, int position, @NonNull weightEntry model) {

                    holder.weight_view.setText(model.getWeight() + " kg");
                    holder.time_view.setText(model.getTimestamp() + "");
                    holder.bmi_view.setText(model.getBMI()+"");

                }
            };
            wlist.setHasFixedSize(true);
            wlist.setLayoutManager(new LinearLayoutManager(this));
            wlist.setAdapter(adapter);

        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Weight");
        builder.setIcon(R.drawable.ic_launcher_foreground);
        builder.setMessage("Please fill in your current weight");


        input=new EditText(MainActivity.this);
        builder.setView(input);

        builder.setPositiveButton("submit", (dialog, which) -> {
            try{
            Double Weight = Double.valueOf(input.getText().toString());
            Date date = new Date();
            Double bmi = Weight/(Math.pow(height/100,2));
            BigDecimal bd = new BigDecimal(bmi).setScale(2, RoundingMode.HALF_UP);
            double BMI = bd.doubleValue();
                Map<String, Object> weightentry = new HashMap<>();
                weightentry.put("weight", Weight);
                weightentry.put("timestamp", date);
                weightentry.put("BMI", BMI);

                getUserDocument()
                        .collection("weights").add(weightentry);


            } catch(NumberFormatException ex){ // handle your exception
                Toast.makeText(getApplicationContext()
                        , "wrong input", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("cancel", (dialog, which) -> dialog.dismiss());
        final AlertDialog ad=builder.create();



        wentry = findViewById(R.id.w_entry);

        wentry.setOnClickListener(v -> {
            ad.show();
            ad.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        });

        input.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence c, int i, int i2, int i3) {}
            @Override public void onTextChanged(CharSequence c, int i, int i2, int i3) {}

            @Override
            public void afterTextChanged(Editable editable) {
                // Will be called AFTER text has been changed.
                if (editable.toString().length() == 0){
                    ad.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    ad.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.home:
                    return true;

                case R.id.dashboard:
                    startActivity(new Intent(getApplicationContext()
                            , Dashboard.class));
                    overridePendingTransition(0, 0);
                    return true;

                case R.id.trend:
                    startActivity(new Intent(getApplicationContext()
                            , Trend.class));
                    overridePendingTransition(0, 0);
                    return true;

                case R.id.me:
                    startActivity(new Intent(getApplicationContext()
                            , Me.class));
                    overridePendingTransition(0, 0);
                    return true;
            }
            return false;
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
        adapter.stopListening();
        finish();
    }
    @Override
    protected void onStart(){
        super.onStart();
        adapter.startListening();
    }


    private DocumentReference getUserDocument() {
        fstore = FirebaseFirestore.getInstance();
        CollectionReference users = fstore.collection("users");
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return users.document(userID);
    }



    private static class weightEntryVH extends RecyclerView.ViewHolder{

        private final TextView weight_view;
        private final TextView time_view;
        private final TextView bmi_view;
        public weightEntryVH(@NonNull View itemView) {
            super(itemView);

            weight_view = itemView.findViewById(R.id.weightView);
            time_view = itemView.findViewById(R.id.timeView);
            bmi_view = itemView.findViewById(R.id.BMIView);
        }
    }
}


