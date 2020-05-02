package com.example.signalify;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
// essai de suivre le tuto : https://github.com/osmdroid/osmdroid/wiki/How-to-use-the-osmdroid-library
// et https://stackoverflow.com/questions/18302603/where-do-i-place-the-assets-folder-in-android-studio?rq=1

public class MainActivity extends AppCompatActivity {
    private MapView map;
    private ImageView btnParam;
    private SearchView sv;
    private View rootView;
    public static boolean radarState,accidentState,chantierState,embouteillageState,imageNotifChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        IMapController mapController;
        ItemizedOverlayWithFocus<OverlayItem> mMyLocationOverlay;

        super.onCreate(savedInstanceState);
        //load/initialize the osmdroid configuration, this can be done
        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()) );

        //inflate and create the map
        setContentView(R.layout.activity_main);

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);    //render
        map.setBuiltInZoomControls(true);               // zoomable
        map.setMultiTouchControls(true);                //  zoom with 2 fingers

        mapController = map.getController();
        mapController.setZoom(18.0);
        GeoPoint startPoint = new GeoPoint(43.65020, 7.00517);
        mapController.setCenter(startPoint);

        sv=findViewById(R.id.sv_location);
        rootView = findViewById(R.id.root_layout);



        //create a new item to draw on the map
        //your items
        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
        OverlayItem home = new OverlayItem("F. Rallo", "nos bureaux", new GeoPoint(43.65020,7.00517));
        // Drawable m = home.getMarker(0);


        items.add(home); // Lat/Lon decimal degrees
        items.add(new OverlayItem("Resto", "chez babar", new GeoPoint(43.64950,7.00517))); // Lat/Lon decimal degrees

        //the Place icons on the map with a click listener
        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(this, items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        //do something
                        Intent intent=new Intent(getApplicationContext(),ShowDetailActivity.class);
                        intent.putExtra("code",index);
                        startActivity(intent);
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                });

        loadSwitchsState();

        mOverlay.setFocusItemsOnTap(true);
        map.getOverlays().add(mOverlay);
        getSupportActionBar().hide();

        FloatingActionButton floatingActionButton = findViewById(R.id.floating_action_button);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),AddAccident.class);
                startActivity(intent);
                finish();            }
        });

        btnParam= findViewById(R.id.btnParam);
        btnParam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),ParametersActivity.class);
                startActivity(intent);
                finish();
            }
        });

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
}
