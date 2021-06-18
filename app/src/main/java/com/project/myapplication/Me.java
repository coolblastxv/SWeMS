package com.project.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class Me extends AppCompatActivity{
    ImageView ivImage;
    TextView tvName;
    TextView tvName2;
    Button btLogout;
    TextView ageView, genderView, heightView;
    Button editButton;
    Button editButton2;
    Button editButton3;
    String userId;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore fstore;
    GoogleSignInClient googleSignInClient;
    private long backPressedTime;
    private Toast backToast;
    TextView idWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);

        ivImage = findViewById(R.id.iv_image);
        tvName = findViewById(R.id.tv_name);
        tvName2 = findViewById(R.id.tv_name2);
        btLogout = findViewById(R.id.bt_logout);
        ageView = findViewById(R.id.age);
        genderView = findViewById(R.id.gender);
        heightView = findViewById(R.id.height);
        editButton = findViewById(R.id.edit_button);
        editButton2 = findViewById(R.id.edit_button2);
        editButton3 = findViewById(R.id.edit_button3);
        idWeight = findViewById(R.id.idealWeight);

        firebaseAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            Glide.with(Me.this)
                    .load(firebaseUser.getPhotoUrl())
                    .into(ivImage);

            tvName.setText(firebaseUser.getDisplayName());
            tvName2.setText(firebaseUser.getEmail());
            userId = firebaseAuth.getCurrentUser().getUid();
            DocumentReference documentReference = fstore.collection("users").document(userId);
                documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w("TAG", "Listen failed.", error);
                            return;
                        }
                        if (value != null && value.exists()) {
                            ageView.setText(String.valueOf(value.getDouble("age").intValue()));
                            genderView.setText(value.getString("gender"));
                            heightView.setText(String.valueOf(value.getDouble("height")));

                            String gender = value.getString("gender");
                            Double age = value.getDouble("age");
                            Double height = value.getDouble("height");
                            if(gender.equals("male")){

                                Double ideal = 52 + (1.9*(height/2.54-60));
                                BigDecimal idweight = new BigDecimal(ideal).setScale(2, RoundingMode.HALF_UP);
                                idWeight.setText(String.valueOf(idweight));
                                double Ideal = idweight.doubleValue();
                                userId = firebaseAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = fstore.collection("users").document(userId);
                                Map<String, Object> user = new HashMap<>();
                                documentReference.update("ideal",Ideal);
                            }
                            else {
                                Double ideal = 49 + (1.7*(height/2.54-60));
                                BigDecimal idweight = new BigDecimal(ideal).setScale(2, RoundingMode.HALF_UP);
                                idWeight.setText(String.valueOf(idweight));
                                double Ideal = idweight.doubleValue();
                                userId = firebaseAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = fstore.collection("users").document(userId);
                                Map<String, Object> user = new HashMap<>();
                                documentReference.update("ideal",Ideal);
                            }
                        }
                        else {
                        }
                    }
                });


            editButton.setOnClickListener(v -> {
                openDialog();
            });
            editButton2.setOnClickListener(v -> {
                openDialog2();
            });
            editButton3.setOnClickListener(v -> {
                openDialog3();
            });

            googleSignInClient = GoogleSignIn.getClient(Me.this
                    , GoogleSignInOptions.DEFAULT_SIGN_IN);

            btLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                firebaseAuth.signOut();
                                Toast.makeText(getApplicationContext()
                                        , "Logout successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Me.this
                                        , login.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                finish();
                            }
                        }
                    });
                }
            });

            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

            bottomNavigationView.setSelectedItemId(R.id.me);

            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.home:
                            startActivity(new Intent(getApplicationContext()
                                    , MainActivity.class));
                            overridePendingTransition(0, 0);
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
                            return true;
                    }
                    return false;
                }
            });

        }
    }
    @Override
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
    public void openDialog(){
        AgeData exampleDialog = new AgeData();
        exampleDialog.show(getSupportFragmentManager(),"example dialog");

    }
    public void openDialog2(){
        GenderData exampleDialog = new GenderData();
        exampleDialog.show(getSupportFragmentManager(),"example dialog");

    }
    public void openDialog3(){
        HeightData exampleDialog = new HeightData();
        exampleDialog.show(getSupportFragmentManager(),"example dialog");
    }

}