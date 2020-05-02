package com.example.signalify;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class ParametersActivity  extends AppCompatActivity {

    ImageButton btnBack;
    Switch radar,accident,embouteillage,chantier;
    private boolean radarState,accidentState,embouteillageState,chantierState;
    public static String SHARED_PREFERS= "sharedprefs";
    public static String SRADAR= "radar";
    public static String SACCIDENT= "accident";
    public static String SEMBOUITEILLAGE= "embouteillage";
    public static String SCHANTIER= "chantier";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameters);
        btnBack=(ImageButton)findViewById(R.id.btnBack);
        radar= findViewById(R.id.sRadar);
        accident =findViewById(R.id.sAccident);
        embouteillage= findViewById(R.id.sEmbouteillage);
        chantier = findViewById(R.id.sChantier);

        Log.d("Essai",radar.isChecked()+"");

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
               // finish();
            }
        });

        loadSwitchsState();
        updateSwitchsState();
    }

    public void saveSwitchsState(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SRADAR,radar.isChecked());
        editor.putBoolean(SACCIDENT,accident.isChecked());
        editor.putBoolean(SCHANTIER,chantier.isChecked());
        editor.putBoolean(SEMBOUITEILLAGE,embouteillage.isChecked());
        editor.apply();
        MainActivity.radarState=radar.isChecked();
        MainActivity.accidentState=accident.isChecked();
        MainActivity.chantierState=chantier.isChecked();
        MainActivity.embouteillageState=embouteillage.isChecked();
    }

    private void loadSwitchsState(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERS,MODE_PRIVATE);
        radarState=sharedPreferences.getBoolean(SRADAR,true);
        accidentState = sharedPreferences.getBoolean(SACCIDENT, true);
        embouteillageState = sharedPreferences.getBoolean(SEMBOUITEILLAGE,true);
        chantierState = sharedPreferences.getBoolean(SCHANTIER,true);
    }

    public void updateSwitchsState(){
        accident.setChecked(accidentState);
        radar.setChecked(radarState);
        embouteillage.setChecked(embouteillageState);
        chantier.setChecked(chantierState);
        MainActivity.radarState=radar.isChecked();
        MainActivity.accidentState=accident.isChecked();
        MainActivity.chantierState=chantier.isChecked();
        MainActivity.embouteillageState=embouteillage.isChecked();
    }

    @Override
    public void onStop() {
        super.onStop();
        saveSwitchsState();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveSwitchsState();
    }
}
