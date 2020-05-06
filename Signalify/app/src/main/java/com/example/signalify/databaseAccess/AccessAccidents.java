package com.example.signalify.databaseAccess;

import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class AccessAccidents {
    private FirebaseDatabase accidentsDatabase;


    private void getAccidentDescription(String id, final View root, final String search, int view,  ArrayList<String> obj) {

        FirebaseFirestore.getInstance().collection("Accidents").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {

                }

                if(task.isComplete())
                {
                    DocumentSnapshot doc = task.getResult();
                    ArrayList<String> tmp=(ArrayList<String>) doc.getData().get(search);

                }
            }
        });

    }
}
