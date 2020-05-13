package com.example.signalify.activities;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
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
import androidx.core.app.NotificationManagerCompat;
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
import com.google.type.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.signalify.activities.INotificationAction.NOTIFY_ID;
import static com.example.signalify.activities.INotificationAction.NO_ACTION;
import static com.example.signalify.activities.INotificationAction.YES_ACTION;
import static com.example.signalify.models.Notifications.CHANNEL_ID;
// essai de suivre le tuto : https://github.com/osmdroid/osmdroid/wiki/How-to-use-the-osmdroid-library
// et https://stackoverflow.com/questions/18302603/where-do-i-place-the-assets-folder-in-android-studio?rq=1

public class MainActivity extends AppCompatActivity implements LocationListener {
    private MapView map;
    private ImageView btnParam;
    private SearchView sv;
    Marker startMarker;
    int id = 0;
    public GeoPoint myLocation;
    IMapController mapController;
    private View rootView;
    private LocationManager locationManager;
    private String TAG = "MainActivity";
    private static final String CANAL = "MyNotifCanal";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static boolean check = false;
    public static HashMap<String, Accident> accidentsListe = new HashMap<String, Accident>();
    HashMap<String, Accident> accidentsListeInt = new HashMap<String, Accident>();
    HashMap<String, OverlayItem> items = new HashMap<String, OverlayItem>();
    private MyLocationNewOverlay mLocationOverlay;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private NotificationManager notificationManager;

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
        map.setBuiltInZoomControls(true);
        Log.d("checkk",""+MainActivity.imageNotifChoice);// zoomable
        map.setMultiTouchControls(true);//  zoom with 2 fingers
        requestPermissionsIfNecessary(new String[]{
                // if you need to show the current location, uncomment the line below
                Manifest.permission.ACCESS_FINE_LOCATION,
                // WRITE_EXTERNAL_STORAGE is required in order to show the map
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });

        mapController = map.getController();
        mapController.setZoom(18.0);
        final GeoPoint startPoint = new GeoPoint(43.6522, 7.00547);
        mapController.setCenter(startPoint);
        addMaker(startPoint);
        myLocation = startPoint;


        sv = findViewById(R.id.sv_location);
        rootView = findViewById(R.id.root_layout);

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = sv.getQuery().toString();
                List<Address> adressList=null;
                if(location !=null )
                {

                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    try{
                        adressList = geocoder.getFromLocationName(location,1);

                    } catch (IOException e) {
                       e.printStackTrace();
                    }
                    if(adressList.size()<=0) {
                        Toast.makeText(getApplicationContext(),"Adresse introuvable",Toast.LENGTH_LONG).show();

                    }
                    else {
                        Address adress = adressList.get(0);

                        GeoPoint geo = new GeoPoint(adress.getLatitude(), adress.getLongitude());


                        addAnyMarker(geo, location);
                        map.getController().animateTo(geo);
                    }
                }

                return  false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(newText.equals(""))
                    map.getController().animateTo(myLocation);

                return false;
            }
        });

        new AccessAccidents().allAccidents(new AccessAccidents.MyCallback() {
            @Override
            public void onCallback(HashMap<String, Accident> accidentsList) {
                accidentsListeInt.putAll(accidentsList);
                accidentsListe = accidentsListeInt;
                items = constructOverlay(accidentsListe);
                setItemsOnMap(items);
                if(!check){
                    check = true;

                    String accidentKey = checkProximity(myLocation);
                    if( accidentKey != null) showActionButtonsNotification(accidentKey);
                }
            }
        });

        new AccessAccidents().pickLastAdd();

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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, this);

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

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }


    private void addMaker(GeoPoint startPoint) {
        startMarker = new Marker(map);
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
            if(displayThat(accident.getType())){
                addAccidentMarker(accident.getType(),new GeoPoint(accident.getLocation().getLatitude(), accident.getLocation().getLongitude()));
                items.put((String) mapentry.getKey(),new OverlayItem(accident.getType(), accident.getDescription().get(0),
                        new GeoPoint(accident.getLocation().getLatitude(), accident.getLocation().getLongitude())));
            }
        }
         return items;
    }

    private boolean displayThat(String type) {
        if(type.equals("Routier") && accidentState) return true;
        if(type.equals("Embouteillage") && embouteillageState) return true;
        if(type.equals("Chantier") && chantierState) return true;
        if(type.equals("Radar") && radarState) return true;
        return false;
    }

    public void addAccidentMarker(String type,GeoPoint geoPoint) {
        Marker m = new Marker(map);
        m.setPosition(geoPoint);
        m.setTextLabelBackgroundColor(
                Color.TRANSPARENT
        );
        m.setTextLabelForegroundColor(
                Color.RED
        );
        m.setTextLabelFontSize(40);
        m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        if(type.equals("Routier")) m.setIcon(getResources().getDrawable(R.drawable.ic_directions_car_black_30dp));
        if(type.equals("Embouteillage")) m.setIcon(getResources().getDrawable(R.drawable.ic_rv_hookup_black_24dp));
        if(type.equals("Chantier")) m.setIcon(getResources().getDrawable(R.drawable.ic_location_city_black_30dp));
        if(type.equals("Radar")) m.setIcon(getResources().getDrawable(R.drawable.ic_track_changes_black_30dp));
        m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_TOP);
        map.getOverlays()
                .add(m);
        map.invalidate();
    }

    public void addAnyMarker(GeoPoint geopoint, String loc)
    {
        ArrayList<OverlayItem> items= new ArrayList<>();
        OverlayItem searched =new OverlayItem(loc,"", geopoint);
        items.add(searched);
        ItemizedOverlayWithFocus<OverlayItem> overLay=new ItemizedOverlayWithFocus<OverlayItem>(getApplicationContext(), items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(int index, OverlayItem item) {
                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(int index, OverlayItem item) {
                        return false;
                    }
                });
        overLay.setFocusItemsOnTap(true);
        map.getOverlays().add(overLay);

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
        imageNotifChoice = sharedPreferences.getBoolean(param.SIMGNOTIF,false);
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
        mapController.animateTo(center);
        startMarker.setPosition(myLocation);
        String accidentKey = checkProximity(myLocation);
         if( accidentKey != null) showActionButtonsNotification(accidentKey);
       // Log.d("CHANGE","Changement");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider)


    {

    }

    private String checkProximity(GeoPoint myLocation) {
        for (Map.Entry mapentry : accidentsListe.entrySet()) {
            Accident accident = (Accident) mapentry.getValue();
            double distance = myLocation.distanceToAsDouble(new GeoPoint(accident.getLocation().getLatitude(), accident.getLocation().getLongitude()));
            if(distance <= 10000000){
                return (String) mapentry.getKey();
            }
        }
        return null;
    }

    private Intent getNotificationIntent(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }

    private void showActionButtonsNotification(String accidentKey){
        Intent intent=new Intent(getApplicationContext(), ShowDetailActivity.class);
        intent.putExtra("code", accidentKey);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        Intent yesIntent = getNotificationIntent();
        yesIntent.setAction(YES_ACTION);

        Intent noIntent = getNotificationIntent();
        yesIntent.setAction(NO_ACTION);

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.alarm)
                .setTimeoutAfter(2000)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle("Vous êtes proche d'un accident !")
                .setContentText(" Vous êtes à 100 Mètres d'un accident.")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_stat_name,
                        getString(R.string.yes),
                        PendingIntent.getActivity(this, 0, yesIntent, PendingIntent.FLAG_UPDATE_CURRENT)))
                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_close,
                        getString(R.string.no),
                        PendingIntent.getActivity(this, 0, noIntent, PendingIntent.FLAG_UPDATE_CURRENT)))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(" Vous êtes à 100 Mètres d'un accident. Nous aimerions bien que vous nous donniez votre vision actuelle de " +
                                "situation. Cliquez sur TOUJOURS LA si l'accident est toujours là ou PAS LA dans le cas contraire..."))

                .build();

        notificationManager.notify(NOTIFY_ID, notification);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        processIntentAction(intent);
        super.onNewIntent(intent);
    }

    private void processIntentAction(Intent intent) {
        if(intent.getAction() != null) {
            switch (intent.getAction()) {
                case YES_ACTION:
                    Toast.makeText(this,"Yes :)", Toast.LENGTH_SHORT).show();
                    break;
                case NO_ACTION:
                    Toast.makeText(this, "No :(", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
