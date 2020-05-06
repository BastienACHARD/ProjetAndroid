package com.example.signalify.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.signalify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
// essai de suivre le tuto : https://github.com/osmdroid/osmdroid/wiki/How-to-use-the-osmdroid-library
// et https://stackoverflow.com/questions/18302603/where-do-i-place-the-assets-folder-in-android-studio?rq=1

public class MainActivity extends AppCompatActivity {
    private MapView map;
    private ImageView btnParam;
    private SearchView sv;
    private View rootView;
    private String TAG = "MainActivity";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    HashMap<String, OverlayItem> items = new HashMap<String, OverlayItem>();
    private MyLocationNewOverlay mLocationOverlay;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    public static boolean radarState,accidentState,chantierState,embouteillageState,imageNotifChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        IMapController mapController;

        super.onCreate(savedInstanceState);
        //load/initialize the osmdroid configuration, this can be done
        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()) );

        //inflate and create the map
        setContentView(R.layout.activity_main);

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);    //render
        map.setBuiltInZoomControls(true);               // zoomable
        map.setMultiTouchControls(true);//  zoom with 2 fingers

        requestPermissionsIfNecessary(new String[] {
                // if you need to show the current location, uncomment the line below
                Manifest.permission.ACCESS_FINE_LOCATION,
                // WRITE_EXTERNAL_STORAGE is required in order to show the map
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });

         mapController = map.getController();
        mapController.setZoom(18.0);
        GeoPoint startPoint = new GeoPoint(43.65020, 7.00517);
        mapController.setCenter(startPoint);

        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()),map);
        this.mLocationOverlay.enableMyLocation();
        map.getOverlays().add(this.mLocationOverlay);

        sv=findViewById(R.id.sv_location);
        rootView = findViewById(R.id.root_layout);

        //create a new item to draw on the map
        //your items
        // OverlayItem home = new OverlayItem("F. Rallo", "nos bureaux", new GeoPoint(43.65020,7.00517));
        // Drawable m = home.getMarker(0);
        // items.add(home); // Lat/Lon decimal degrees
        // items.add(new OverlayItem("Resto", "chez babar", new GeoPoint(43.64950,7.00517))); // Lat/Lon decimal degrees
        // items.add(new OverlayItem("Ajout", "chez Lemuel", new GeoPoint(43.64850,7.00517)));

        db.collection("Accidents")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                                Log.d(TAG, document.getId() + " => " + document.get("type"));
                                // items.put(document.getId(),new OverlayItem(document.get("type").toString(), document.get("description").toString(), new GeoPoint(document.get("location").)));
                                items.put(document.getId(),new OverlayItem(document.get("type").toString(), document.get("description").toString(), new GeoPoint(43.64850,7.00517)));
                            }
                             setItemsOnMap(items);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        //the Place icons on the map with a click listener

        loadSwitchsState();
        getSupportActionBar().hide();

        FloatingActionButton floatingActionButton = findViewById(R.id.floating_action_button);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), AddAccidentActivity.class);
                startActivity(intent);
                finish();            }
        });

        btnParam= findViewById(R.id.btnParam);
        btnParam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), ParametersActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public void setItemsOnMap(final HashMap<String, OverlayItem> items){
        ArrayList<OverlayItem> list = new ArrayList<OverlayItem>(items.values());
        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(this, list,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        //do something
                        Intent intent=new Intent(getApplicationContext(), ShowDetailActivity.class);
                        intent.putExtra("code", getKey(items,item));
                        // Log.d(TAG, getKey(items,item));
                        startActivity(intent);
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                });
        mOverlay.setFocusItemsOnTap(true);
        map.getOverlays().add(mOverlay);
    }

    public static <K, V> K getKey(HashMap<K, V> map, V value) {
        for (K key : map.keySet()) {
            if (value.equals(map.get(key))) {
                return key;
            }
        }
        return null;
    }

    @Override
    public void onResume(){
        super.onResume();
        map.onResume();
        sv.setQuery("", false);
        rootView.requestFocus();
    }

    @Override
    public void onPause(){
        super.onPause();
        map.onPause();
    }

    public void loadSwitchsState(){
        ParametersActivity param=new ParametersActivity();
        SharedPreferences sharedPreferences = getSharedPreferences(param.SHARED_PREFERS,MODE_PRIVATE);
        radarState=sharedPreferences.getBoolean(param.SRADAR,true);
        accidentState = sharedPreferences.getBoolean(param.SACCIDENT, true);
        embouteillageState = sharedPreferences.getBoolean(param.SEMBOUITEILLAGE,true);
        chantierState = sharedPreferences.getBoolean(param.SCHANTIER,true);
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for(String permission : permissions) {
            if(ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0){
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
}
