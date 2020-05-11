package com.example.signalify.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toolbar;

import com.example.signalify.R;

public class AddCommentsActivity extends AppCompatActivity {

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comments);
        Intent intent=getIntent();
        String accidentId =intent.getStringExtra("code");
        getSupportActionBar().setTitle("Ajouter un commentaire");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

}
