package com.example.signalify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.signalify.models.Accident;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;


public class AddAccident extends AppCompatActivity {

    Accident accident;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String type = "route";
    GeoPoint location = new GeoPoint(11,11);
    ArrayList<String> descriptions = new ArrayList<>();
    ArrayList<String> images = new ArrayList<>();

    public AddAccident(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_accident);

        Button valid = (Button) findViewById(R.id.valid);
        Button cancel = (Button) findViewById(R.id.cancel);
        EditText description = (EditText) findViewById(R.id.editText);
        ImageButton back = (ImageButton) findViewById(R.id.back);

        descriptions.add(description.getText().toString().trim());
        images.add("image1");

        valid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAccident(type, location, descriptions, images);
                addAccidentDataBase(accident);
                Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void setAccident(String type, GeoPoint location, ArrayList<String> description, ArrayList<String> image){
        accident = new Accident(type, location, description, image);
    }

    public void addAccidentDataBase(Accident accident){
        db.collection("Accidents").add(accident);
    }

}
