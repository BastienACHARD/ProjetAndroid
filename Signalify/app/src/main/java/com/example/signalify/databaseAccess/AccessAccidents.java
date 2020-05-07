package com.example.signalify.databaseAccess;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.signalify.models.Accident;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class AccessAccidents {
    public HashMap<String, Accident> accidentsListe = new HashMap<>();
    FirebaseFirestore db;

    public AccessAccidents(){
        db = FirebaseFirestore.getInstance();
    }

    public interface MyCallback {
        void onCallback(HashMap<String, Accident> accidentsList);
    }

    public void allAccidents(final MyCallback myCallback){
        db.collection("Accidents")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            accidentsListe.clear();
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {

                                Accident newItem = new Accident(
                                        document.getString("type"),
                                        document.getGeoPoint("location"),
                                        (ArrayList<String>) document.get("description"),
                                        (ArrayList<String>) document.get("image"));
                                accidentsListe.put(document.getId(), newItem);

                            }
                            myCallback.onCallback(accidentsListe);
                        } else {
                            Log.d("all", "Error getting documents: ", task.getException());
                        }
                    }
                });
       // Log.d("Affiche", accidentsListe.toString());
    }
}
