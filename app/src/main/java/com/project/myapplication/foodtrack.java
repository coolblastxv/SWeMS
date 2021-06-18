package com.project.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class foodtrack extends AppCompatDialogFragment {
    private EditText editfood,editcalories;

    FirebaseFirestore fStore;
    String userID;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    Calendar calendar;
    int precal;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_foodtrack,null);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH));
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        builder.setView(view);
        builder.setTitle("What do you eat today?");
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try{
                    getUserDocument().collection("activity").document(day + "-" + month + "-" + year).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            try {
                                if (document.exists()) {
                                    Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                                    precal =  document.getLong("calorie").intValue();
                                    Integer calorie = Integer.parseInt(editcalories.getText().toString())+precal;

                                    Map<String, Object> calories = new HashMap<>();
                                    calories.put("calorie",calorie);
                                    getUserDocument().collection("activity").document(day + "-" + month + "-" + year).update(calories);
                                } else {
                                    Log.d("TAG", "No such document");
                                    Integer calorie = Integer.valueOf(editcalories.getText().toString());

                                    Map<String, Object> calories = new HashMap<>();
                                    calories.put("calorie",calorie);
                                    getUserDocument().collection("activity").document(day + "-" + month + "-" + year).update(calories);
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
                    getUserDocument().collection("activity").document(day + "-" + month + "-" + year).get();
                    String food = editfood.getText().toString();
                    Integer calorie = Integer.valueOf(editcalories.getText().toString());
                    Map<String, Object> data = new HashMap<>();
                    data.put("food", food);
                    data.put("calorie",calorie);
                    getUserDocument().collection("activity").document(day + "-" + month + "-" + year).collection("food")
                            .add(data);



                }catch(NumberFormatException ex){ // handle your exception
                    Toast.makeText( getApplicationContext()
                            , "wrong input", Toast.LENGTH_SHORT).show();
                }
            }
        });

        editfood = view.findViewById(R.id.foodtracker);
        editcalories = view.findViewById(R.id.foodcalorie);

        return builder.create();
    }
    private DocumentReference getUserDocument() {
        fstore = FirebaseFirestore.getInstance();
        CollectionReference users = fstore.collection("users");
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return users.document(userID);
    }

}
