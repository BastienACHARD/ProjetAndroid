package com.example.signalify.databaseAccess;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.signalify.models.Accident;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class AccessAccidents {
    public static HashMap<String, Accident> accidentsListe = new HashMap<>();
    FirebaseFirestore db;
    public static String lastAddedKey;
    public static Accident lastAddedAccident;

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
                                        (ArrayList<String>) document.get("image"),
                                        Integer.parseInt(document.getString("veracity")));
                                if(Integer.parseInt(newItem.getVeracity()) >= 2){
                                    deleteAccident(document.getId());
                                }else{
                                    accidentsListe.put(document.getId(), newItem);
                                }

                            }
                            myCallback.onCallback(accidentsListe);
                        } else {
                            Log.d("all", "Error getting documents: ", task.getException());
                        }
                    }
                });
       // Log.d("Affiche", accidentsListe.toString());
    }

    private void deleteAccident(String id) {
        db.collection("Accidents").document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Delete", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Delete", "Error deleting document", e);
                    }
                });
    }


    public void pickLastAdd(){
        db.collection("cities")
                .whereEqualTo("state", "CA")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("Listen", "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d("Listen", "New city: " + dc.getDocument().getData());
                                    lastAddedKey = dc.getDocument().getId();
                                    lastAddedAccident = (Accident) dc.getDocument().getData();
                                    break;
                                case MODIFIED:
                                    Log.d("Listen", "Modified city: " + dc.getDocument().getData());
                                    break;
                                case REMOVED:
                                    Log.d("Listen", "Removed city: " + dc.getDocument().getData());
                                    break;
                            }
                        }

                    }
                });
    }
}
