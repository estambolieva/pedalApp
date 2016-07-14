package com.example.estam_000.arcgismaptest;

import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;

import java.util.SortedMap;
import java.util.TreeMap;

/** Tracks latitudinal and longitudinal data from device sensor. */
public class Tracker extends AppCompatActivity {

    public LocationListener LL;
    public LocationManager LM;
    public static double LAT;
    public static double LON;
    // public static double SPEED; // by one second difference kinda thing between coordinates
    // public static double alt; // in case you want altitude from android device, but

    static int t = 1;
    public static SortedMap<Integer, Double[]> TRIP_RECORD = new TreeMap<>();

}
