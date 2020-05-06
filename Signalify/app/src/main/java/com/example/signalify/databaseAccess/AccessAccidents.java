package com.example.signalify.databaseAccess;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.signalify.models.Accident;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccessAccidents {
    private FirebaseDatabase accidentsDatabase;
    private DatabaseReference accidentsReference;
    private List<Accident> accidentsListes = new ArrayList<>();
    private Map<String, Accident> accidentsListe = new HashMap<>();
    FirebaseFirestore db;

    public AccessAccidents(){
        db = FirebaseFirestore.getInstance();
    }

    public void readAccidents(){
        db.collection("Accidents")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("Access", "Listen failed.", e);
                            return;
                        }

                        List<String> cities = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            Log.d("Access", "Current cites in CA: " + cities);
                        }
                    }
                });
    }
}
