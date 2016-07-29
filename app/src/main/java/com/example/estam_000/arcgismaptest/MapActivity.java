package com.example.estam_000.arcgismaptest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleMarkerSymbol;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity {

    // util
    static boolean firstUpdate = true;

    // declare location objects
    private LocationManager locationManager; // accesses location services

    // declare views
    TextView mSpeed = null;
    TextView mDistance = null;
    TextView mTime = null;
    TextView mProvider = null;
    TextView mPrecision = null;
    TextView mLatitude = null;
    TextView mLongitude = null;

    Drawable dStop = null;
    Drawable dBike = null;

    // the buttons
    FloatingActionButton fab = null;
    Button bStop = null;

    // declare ESRI objects
    public MapView mapView = null;
    public LocationDisplayManager ldm = null;
    GraphicsLayer graphicsLayer = null;

    // the record assets
    static public Double[] currentCoordinates = new Double[2]; // Double[0] <- Longitude
    static ArrayList<Double[]> record = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        handleLocationPermissions();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        configureButtons();
        configureMap();
    }

    public void configureMap() {
        // to add tracking points
        graphicsLayer = new GraphicsLayer();
        mapView.addLayer(graphicsLayer);
        // http://stackoverflow.com/questions/30251063/why-is-projection-not-working-using-my-mapviews-spatial-reference-arcgis-sdk

        mapView.setOnStatusChangedListener(new OnStatusChangedListener() {
            // https://gist.github.com/phpmaps/5d766fe34b032fd6aaa5
            @Override
            public void onStatusChanged(Object o, STATUS status) {
                if ((status == STATUS.INITIALIZED) && (o instanceof MapView )) {
                    Log.d("StatusChangedListener","Map initialization succeeded");
                    ldm = mapView.getLocationDisplayManager();
                    ldm.setAutoPanMode(LocationDisplayManager.AutoPanMode.LOCATION);
                    ldm.setShowLocation(true);
                    ldm.setLocationListener(new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            Log.d("LocationLISTENER", "lat: " + location.getLatitude() + " long: " + location.getLongitude());
                            if(location != null) {
                                ldm.setShowLocation(true);
                                ldm.setAutoPanMode(LocationDisplayManager.AutoPanMode.LOCATION);

                                // Log.d("LocationLISTENER", "lat: " + location.getLatitude() + " long: " + location.getLongitude());
                                Log.d("LocationLISTENER", "Location found....");

                                String longitudeStr = "Longitude: " + location.convert(location.getLongitude(), location.FORMAT_SECONDS);
                                String latitudeStr = "Latitude: " + location.convert(location.getLatitude(), location.FORMAT_SECONDS);
                                String precisionStr = String.format("Precisão: %.2f", location.getAccuracy());
                                String providerStr = "Fornecedor: " + location.getProvider();
                                String speedStr = String.format("Velocidade: %.1fmps", location.getSpeed());

                                mLatitude.setText(latitudeStr);
                                mLongitude.setText(longitudeStr);
                                mSpeed.setText(speedStr);
                                mProvider.setText(providerStr);
                                mPrecision.setText(precisionStr);

                                currentCoordinates[0] = location.getLongitude();
                                currentCoordinates[1] = location.getLatitude();
                                record.add(currentCoordinates);

                                if (location.getSpeed() == 0) {
                                    SimpleMarkerSymbol stoppedSymbol = new SimpleMarkerSymbol(Color.RED, 20, SimpleMarkerSymbol.STYLE.CIRCLE);
                                    Point pointGeometry = (Point) GeometryEngine.project(new Point(location.getLongitude(), location.getLatitude()), SpatialReference.create(4326), mapView.getSpatialReference());
                                    Graphic pointGraphic = new Graphic(pointGeometry, stoppedSymbol);
                                    graphicsLayer.addGraphic(pointGraphic);
                                } else {
                                    SimpleMarkerSymbol movingSymbol = new SimpleMarkerSymbol(Color.GREEN, 20, SimpleMarkerSymbol.STYLE.DIAMOND);
                                    Point pointGeometry = (Point) GeometryEngine.project(new Point(location.getLongitude(), location.getLatitude()), SpatialReference.create(4326), mapView.getSpatialReference());
                                    Graphic pointGraphic = new Graphic(pointGeometry, movingSymbol);
                                    graphicsLayer.addGraphic(pointGraphic);
                                }

                            }

                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                            Log.d("LocationLISTENER", "onStatusChanged");

                            Context context = getApplicationContext();
                            CharSequence text = "Estado de GPS alterado";
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                            Log.d("LocationLISTENER", "LOCATION PROVIDER ENABLED");

                            Context context = getApplicationContext();
                            CharSequence text = "GPS ligado! :)";
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                            Log.d("LocationLISTENER", "LOCATION PROVIDER DISABLED");
                            ldm.setShowLocation(false);

                            Context context = getApplicationContext();
                            CharSequence text = "GPS desligado! :(";
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    });
                    ldm.start();
                }
            }
        });
    }

    public void initializeViews() {
        // initialize the buttons
        fab = (FloatingActionButton) findViewById(R.id.fab);
        bStop = (Button) findViewById(R.id.stopbutton);

        // initialize TextViews
        mSpeed = (TextView) findViewById(R.id.velocidade);
        mDistance = (TextView) findViewById(R.id.distancia);
        mTime = (TextView) findViewById(R.id.tempo);
        mProvider = (TextView) findViewById(R.id.fornecedor);
        mPrecision = (TextView) findViewById(R.id.precisao);
        mLatitude = (TextView) findViewById(R.id.latitude);
        mLongitude = (TextView) findViewById(R.id.longitude);

        // initialize map
        mapView = (MapView) findViewById(R.id.map);
    }

    public void handleLocationPermissions() {
        // permission handling
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // request permissions
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 10);
                // handle permission results by calling back to onRequestPermissionsResults()
                return;
            }
        }
        // permission handling in android goes like
        // 1 -> checkSelfPermission()
        // 2 -> requestPermissions() -> this method calls back to onRequestPermissionResults() that is overriden
        // 3 -> onRequestPermissionResults() -> handles the permission results that result from requestPermissions()
    }

    // this method is a callback of requestPermissions()
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 10:
                // if permission is granted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // if not enabled after granted permission intent that will take the user to the control panel to turn GPS on
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                    // http://stackoverflow.com/questions/843675/how-do-i-find-out-if-the-gps-of-an-android-device-is-enabled
                }
                return;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.unpause();
    }


    public void configureButtons() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // later work on the animation -> https://developer.android.com/training/material/animations.html#Reveal
                if (bStop.getVisibility() == View.INVISIBLE) {
                    bStop.setVisibility(View.VISIBLE);
                } else {
                    bStop.setVisibility(View.INVISIBLE);
                }
            }

        });

        bStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO send record to server before finishing activity
                // Intent intent = new Intent();
                // intent.putExtras();
                finish();
            }
        });

    }

}

