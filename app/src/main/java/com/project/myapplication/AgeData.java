package com.project.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class AgeData extends AppCompatDialogFragment {
    private EditText editAge;

    FirebaseFirestore fStore;
    String UserID;
    FirebaseAuth fAuth;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.age_data,null);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        builder.setView(view);
        builder.setTitle("User data");
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {

                    int Age = Integer.parseInt(editAge.getText().toString());


                    UserID = fAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = fStore.collection("users").document(UserID);
                    documentReference.update("age", Age);
                }catch(NumberFormatException ex){ // handle your exception
                    Toast.makeText( getApplicationContext()
                            , "wrong input", Toast.LENGTH_SHORT).show();
                }

            }
        });

        editAge = view.findViewById(R.id.edit_age);

        return builder.create();
    }

}

