package com.example.signalify.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.signalify.R;
import com.example.signalify.activities.MainActivity;

public class ParametersActivity  extends AppCompatActivity {

    ImageButton btnBack;
    Button btnReinit;
    Switch radar,accident,embouteillage,chantier,imgNotif;
    private boolean radarState,accidentState,embouteillageState,chantierState,imgNotifState;
    public static String SHARED_PREFERS= "sharedprefs";
    public static String SRADAR= "radar";
    public static String SACCIDENT= "accident";
    public static String SEMBOUITEILLAGE= "embouteillage";
    public static String SCHANTIER= "chantier";
    public static String SIMGNOTIF= "imgnotif";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameters);
        btnBack=(ImageButton)findViewById(R.id.btnBack);
        radar= findViewById(R.id.sRadar);
        accident =findViewById(R.id.sAccident);
        embouteillage= findViewById(R.id.sEmbouteillage);
        chantier = findViewById(R.id.sChantier);
        imgNotif= findViewById(R.id.sAutorisation);
        btnReinit= findViewById(R.id.btnReinitaliser);

        Log.d("Essai",radar.isChecked()+"");

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
               // finish();
            }
        });

        loadSwitchsState();
        updateSwitchsState();

        btnReinit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radarState=accidentState=embouteillageState=chantierState=imgNotifState=true;
                updateSwitchsState();
            }
        });
    }

    public void saveSwitchsState(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SRADAR,radar.isChecked());
        editor.putBoolean(SACCIDENT,accident.isChecked());
        editor.putBoolean(SCHANTIER,chantier.isChecked());
        editor.putBoolean(SEMBOUITEILLAGE,embouteillage.isChecked());
        editor.putBoolean(SIMGNOTIF,imgNotif.isChecked());
        editor.apply();
        MainActivity.radarState=radar.isChecked();
        MainActivity.accidentState=accident.isChecked();
        MainActivity.chantierState=chantier.isChecked();
        MainActivity.embouteillageState=embouteillage.isChecked();
        MainActivity.imageNotifChoice=imgNotif.isChecked();
    }

    private void loadSwitchsState(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERS,MODE_PRIVATE);
        radarState=sharedPreferences.getBoolean(SRADAR,true);
        accidentState = sharedPreferences.getBoolean(SACCIDENT, true);
        embouteillageState = sharedPreferences.getBoolean(SEMBOUITEILLAGE,true);
        chantierState = sharedPreferences.getBoolean(SCHANTIER,true);
        imgNotifState = sharedPreferences.getBoolean(SIMGNOTIF,true);
    }

    public void updateSwitchsState(){
        accident.setChecked(accidentState);
        radar.setChecked(radarState);
        embouteillage.setChecked(embouteillageState);
        chantier.setChecked(chantierState);
        imgNotif.setChecked(imgNotifState);
        MainActivity.radarState=radar.isChecked();
        MainActivity.accidentState=accident.isChecked();
        MainActivity.chantierState=chantier.isChecked();
        MainActivity.embouteillageState=embouteillage.isChecked();
        MainActivity.imageNotifChoice=imgNotif.isChecked();
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
