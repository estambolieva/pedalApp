package com.example.estam_000.arcgismaptest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.esri.android.map.MapView;

/**
 * Created by Bernardo on 14-07-2016.
 */
public class MapActivity extends AppCompatActivity {

    // util
    final static String LOG_TAG = MainActivity.class.getName();

    // TODO replace these by the ESRI equivalents
    private LocationManager locationManager; // accesses location services
    private LocationListener locationListener; // tracks location changes

    MapView mapView = null;
    TextView tvLatitude;
    TextView tvLongitude;

    // the fabulous button
    FloatingActionButton fab;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize the fabulous fab button
        fab = (FloatingActionButton) findViewById(R.id.fab);

        // permission handling
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // handle the user permission
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 10);
                return;
            }
        }



        // initialize map
        mapView = (MapView) findViewById(R.id.map);

        // initialize the TextViews
        tvLatitude = (TextView) findViewById(R.id.latitude);
        tvLongitude = (TextView) findViewById(R.id.longitude);

        // initialize location objects
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                tvLatitude.setText("Lat: " + location.getLatitude());
                tvLongitude.setText("Lon: " + location.getLongitude());

                Log.v(LOG_TAG, "Printing latitude: " + tvLatitude.toString());
                Log.v(LOG_TAG, "Printing latitude: " + tvLongitude.toString());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                // TODO request coarse location updates instead of this thing. // Hint: use NETWORK_PROVIDER value
                // we can write an intent that will take the user
                // to the control panel to turn GPS on
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        activateFab();

    }

    // this method is linked to the requestPermissions()
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 10:
                // if permission is granted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    activateFab();
                }
                return;
        }
    }

    // YES
    // It is complaining because we don't check the permission within activateFab(). We do it way before

    public void activateFab() {
        Log.v(LOG_TAG, "fab == null -> " + (locationListener == null));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationManager.requestLocationUpdates("gps", 1000, 0, locationListener);
            }
        });
    }

}

