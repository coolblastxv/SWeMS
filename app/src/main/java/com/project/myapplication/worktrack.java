package com.project.myapplication;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class worktrack extends AppCompatDialogFragment {
    private EditText editwork,editburn;

    FirebaseFirestore fStore;
    String userID;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    Calendar calendar;
    int preburn;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.worktrack,null);
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
                                                preburn =  document.getLong("burned").intValue();
                                                Integer burn= Integer.parseInt(editburn.getText().toString())+preburn;

                                                Map<String, Object> burns = new HashMap<>();
                                                burns.put("burned",burn);
                                                getUserDocument().collection("activity").document(day + "-" + month + "-" + year).update(burns);
                                            } else {
                                                Log.d("TAG", "No such document");
                                                Integer burned = Integer.valueOf(editburn.getText().toString());

                                                Map<String, Object> burns = new HashMap<>();
                                                burns.put("burned",burned);
                                                getUserDocument().collection("activity").document(day + "-" + month + "-" + year).update(burns);
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
                    String workout = editwork.getText().toString();
                    Integer burned = Integer.valueOf(editburn.getText().toString());
                    Map<String, Object> data = new HashMap<>();
                    data.put("workout", workout);
                    data.put("burned",burned);
                    getUserDocument().collection("activity").document(day + "-" + month + "-" + year).collection("food")
                            .add(data);



                }catch(NumberFormatException ex){ // handle your exception
                    Toast.makeText( getApplicationContext()
                            , "wrong input", Toast.LENGTH_SHORT).show();
                }
            }
        });

        editwork = view.findViewById(R.id.worktracker);
        editburn = view.findViewById(R.id.calorieburn);

        return builder.create();
    }
    private DocumentReference getUserDocument() {
        fstore = FirebaseFirestore.getInstance();
        CollectionReference users = fstore.collection("users");
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return users.document(userID);
    }

}
