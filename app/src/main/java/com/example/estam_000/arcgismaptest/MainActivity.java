package com.example.estam_000.arcgismaptest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.LinearUnit;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.geometry.Unit;

public class MainActivity extends AppCompatActivity {

    final static String LOG_TAG = MainActivity.class.getName();

    LocationDisplayManager lDisplayManager = null;
    MapView mapView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        //check for location permission
        int locPermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        // requests permissions for buildSDKs &gte 23
        if(locPermissionCheck != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        Log.v(LOG_TAG, "location permission is " + ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION));


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Perform Action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // get the map view from the layout files
        mapView = (MapView) findViewById(R.id.map);

    }

    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // display current location on the map
                    MyStatusChangedListener myListener = new MyStatusChangedListener(mapView);
                    mapView.setOnStatusChangedListener(myListener);

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public boolean checkLocationPermission()
    {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class MyStatusChangedListener implements OnStatusChangedListener{
        MapView mapView;

        public MyStatusChangedListener(MapView view) {
            this.mapView = view;
        }

        @Override
        public void onStatusChanged(Object o, STATUS status) {
            if (o == mapView && status == STATUS.INITIALIZED) {
                lDisplayManager = mapView.getLocationDisplayManager();
                lDisplayManager.setAutoPanMode(LocationDisplayManager.AutoPanMode.LOCATION);

                lDisplayManager.setLocationListener(new LocationListener() {
                    // Zooms to the current location when first GPS fix arrives.
                    @Override
                    public void onLocationChanged(Location location) {
                        boolean locationChanged = false;

                        if (!locationChanged) {
                            locationChanged = true;

                            double locy = location.getLatitude();
                            double locx = location.getLongitude();
                            Point wgspoint = new Point(locx, locy);
                            Point mapPoint = (Point) GeometryEngine
                                    .project(wgspoint,
                                            SpatialReference.create(4326),
                                            mapView.getSpatialReference());

                            Unit mapUnit = mapView.getSpatialReference()
                                    .getUnit();
                            double zoomWidth = Unit.convertUnits(
                                    5,
                                    Unit.create(LinearUnit.Code.KILOMETER),
                                    mapUnit);
                            Envelope zoomExtent = new Envelope(mapPoint,
                                    zoomWidth, zoomWidth);
                            mapView.setExtent(zoomExtent);
                        }
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {}

                    @Override
                    public void onProviderEnabled(String s) { }

                    @Override
                    public void onProviderDisabled(String s) {}
                });
                lDisplayManager.start();
            }
        }
    }
}
