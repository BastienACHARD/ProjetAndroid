package com.example.signalify.activities;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.signalify.models.Notifications;
import com.example.signalify.R;
import com.example.signalify.databaseAccess.AccessAccidents;
import com.example.signalify.models.Accident;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
// essai de suivre le tuto : https://github.com/osmdroid/osmdroid/wiki/How-to-use-the-osmdroid-library
// et https://stackoverflow.com/questions/18302603/where-do-i-place-the-assets-folder-in-android-studio?rq=1

public class MainActivity extends AppCompatActivity implements LocationListener {
    private MapView map;
    private ImageView btnParam;
    private SearchView sv;
    private GeoPoint myLocation;
    IMapController mapController;
    private View rootView;
    private LocationManager locationManager;
    private String TAG = "MainActivity";
    private static final String CANAL = "MyNotifCanal";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static HashMap<String, Accident> accidentsListe = new HashMap<String, Accident>();
    HashMap<String, Accident> accidentsListeInt = new HashMap<String, Accident>();
    HashMap<String, OverlayItem> items = new HashMap<String, OverlayItem>();
    private MyLocationNewOverlay mLocationOverlay;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    public static boolean radarState, accidentState, chantierState, embouteillageState, imageNotifChoice;

    @Override
    protected void onStart() {
        Log.d("START", "ON START LO");
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        setContentView(R.layout.activity_main);
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);    //render
        map.setBuiltInZoomControls(true);               // zoomable
        map.setMultiTouchControls(true);//  zoom with 2 fingers
        requestPermissionsIfNecessary(new String[]{
                // if you need to show the current location, uncomment the line below
                Manifest.permission.ACCESS_FINE_LOCATION,
                // WRITE_EXTERNAL_STORAGE is required in order to show the map
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });

        mapController = map.getController();
        mapController.setZoom(18.0);
        GeoPoint startPoint = new GeoPoint(43.6522, 7.00547);
        mapController.setCenter(startPoint);
        addMaker(startPoint);
        if(checkProximity(startPoint)) generateNotification();


        sv = findViewById(R.id.sv_location);
        rootView = findViewById(R.id.root_layout);

        //create a new item to draw on the map
        //your items
        //OverlayItem home = new OverlayItem("F. Rallo", "nos bureaux", new GeoPoint(43.65020,7.00517));
        //Drawable m = home.getMarker(0);
        //items.put("1",home); // Lat/Lon decimal degrees
        // items.add(new OverlayItem("Resto", "chez babar", new GeoPoint(43.64950,7.00517))); // Lat/Lon decimal degrees
        //items.put("1",new OverlayItem("Ajout", "chez Lemuel", new GeoPoint(43.64850,7.00517)));
        //the Place icons on the map with a click listener
        new AccessAccidents().allAccidents(new AccessAccidents.MyCallback() {
            @Override
            public void onCallback(HashMap<String, Accident> accidentsList) {
                accidentsListeInt.putAll(accidentsList);
                accidentsListe = accidentsListeInt;
                items = constructOverlay(accidentsListe);
                setItemsOnMap(items);
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        loadSwitchsState();
        getSupportActionBar().hide();

        FloatingActionButton floatingActionButton = findViewById(R.id.floating_action_button);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), AddAccidentActivity.class);
                startActivity(intent);
                        }
        });

        btnParam= findViewById(R.id.btnParam);
        btnParam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), ParametersActivity.class);
                startActivity(intent);

            }
        });

        //subscribeToNotificationService();
        //subscribeToTheToken();
    }

    private void subscribeToTheToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(!task.isSuccessful()) {
                            Log.w("TAG", "getInstanceId failed", task.getException());
                            return;
                        }

                        String token = task.getResult().getToken();

                        //String msg = getString(R.string.msg_token_fmt, token);
                        Log.d("TAG", "Token : "+token);
                        Toast.makeText(MainActivity.this, "Token : "+token, Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void subscribeToNotificationService() {
        FirebaseMessaging.getInstance().subscribeToTopic("Lemuel")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Souscription Avec Success !!!",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Souscription Echouée !!!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void addMaker(GeoPoint startPoint) {
        Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_BOTTOM);
        startMarker.setIcon(getResources().getDrawable(R.mipmap.ic_nav));
        startMarker.setTitle("Position Actuelle");
        map.getOverlays().add(startMarker);
        map.invalidate();
    }

    public HashMap<String, OverlayItem> constructOverlay(final HashMap<String, Accident> map){
        for (Map.Entry mapentry : map.entrySet()) {
            Accident accident = (Accident) mapentry.getValue();
            addAccidentMarker(new GeoPoint(accident.getLocation().getLatitude(), accident.getLocation().getLongitude()));
            items.put((String) mapentry.getKey(),new OverlayItem(accident.getType(), accident.getDescription().get(0),
                    new GeoPoint(accident.getLocation().getLatitude(), accident.getLocation().getLongitude())));
        }
         return items;
    }

    public void addAccidentMarker(GeoPoint geoPoint) {
        Marker pointMarker = new Marker(map);
        pointMarker.setPosition(geoPoint);
        pointMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        pointMarker.setIcon(getResources().getDrawable(R.mipmap.ic_lo));
        map.getOverlays().add(pointMarker);
        map.invalidate();
    }

    public void setItemsOnMap(final HashMap<String, OverlayItem> items){
        ArrayList<OverlayItem> list = new ArrayList<OverlayItem>(items.values());
        Log.d("Tab", String.valueOf(items.values()));
        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(this, list,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        Intent intent=new Intent(getApplicationContext(), ShowDetailActivity.class);
                        intent.putExtra("code", getKey(items,item));
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
        //subscribeToNotificationService();
        //subscribeToTheToken();
    }

    @Override
    public void onPause(){
        super.onPause();
        map.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(locationManager != null){
            locationManager.removeUpdates(this);
        }
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

    @Override
    public void onLocationChanged(Location location) {
        GeoPoint center = new GeoPoint(location.getLatitude(), location.getLongitude());
        myLocation = center;
       // mapController.animateTo(center);
       // addMaker(center);
        if(checkProximity(center)) generateNotification();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private boolean checkProximity(GeoPoint myLocation) {
        for (Map.Entry mapentry : accidentsListe.entrySet()) {
            Accident accident = (Accident) mapentry.getValue();
            double distance = myLocation.distanceToAsDouble(new GeoPoint(accident.getLocation().getLatitude(), accident.getLocation().getLongitude()));
            if(distance <= 10000000){
                return true;
            }
        }
        return false;
    }

    public void generateNotification(){
        //Notifications notifications = new Notifications();
        //notifications.sendNotificationChannelNormal("Vous êtes proche d'un accident !","Vous êtes à 100 Mètres d'un accident. Cliquez pour en savoir plus.",Notifications.CHANNEL_ID,NotificationCompat.PRIORITY_HIGH);
    }
}
