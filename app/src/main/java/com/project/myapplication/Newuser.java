package com.project.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Newuser extends AppCompatActivity {
    private EditText editHeight;
    private EditText editAge;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button submit;
    private Toast toast;

    FirebaseFirestore fStore;
    String UserID;
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newuser);

        editAge = findViewById(R.id.edit_age2);
        editHeight = findViewById(R.id.edit_height2);
        radioGroup = findViewById(R.id.genderGroup);
        submit = findViewById(R.id.newbutton);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{

                    Double Height = Double.parseDouble(editHeight.getText().toString());
                    int Age = Integer.parseInt(editAge.getText().toString());
                int radioId = radioGroup.getCheckedRadioButtonId();
                radioButton = findViewById(radioId);
                String Gender = radioButton.getText().toString();


                UserID = fAuth.getCurrentUser().getUid();
                Map<String, Object> Data = new HashMap<>();
                Data.put("age", Age);
                Data.put("gender", Gender);
                Data.put("height", Height);
                fStore.collection("users").document(UserID)
                        .set(Data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("TAG", "DocumentSnapshot successfully written!");
                                toast = Toast.makeText(getBaseContext(), "Data successfully saved!", Toast.LENGTH_SHORT);
                                toast.show();
                                startActivity(new Intent(Newuser.this
                                        ,MainActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("TAG", "Error writing document", e);
                            }
                        });

                } catch(NumberFormatException ex){ // handle your exception
                    Toast.makeText(getApplicationContext()
                            , "wrong input ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    protected void onStop() {

        super.onStop();
        finish();
    }}
