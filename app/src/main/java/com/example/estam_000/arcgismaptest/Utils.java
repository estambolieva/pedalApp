package com.example.estam_000.arcgismaptest;

import android.support.v7.app.AppCompatActivity;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/** Tracks latitudinal and longitudinal data from device sensor. */
public class Utils extends AppCompatActivity {

    // System.currentTimeMillis
    public static SortedMap<Long, Double[]> TRIP_RECORD = new TreeMap<>();

    static String hhmmss(long millis) {
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

    }

    /** Obtains distance in meters between two coordinates.
     * Source: http://www.ridgesolutions.ie/index.php/2013/11/14/algorithm-to-calculate-speed-from-two-gps-latitude-and-longitude-points-and-time-difference/ */
    static double getDistance(double lat1, double lon1, double lat2, double lon2) {

        // Convert degrees to radians
        lat1 = lat1 * Math.PI / 180.0;
        lon1 = lon1 * Math.PI / 180.0;

        lat2 = lat2 * Math.PI / 180.0;
        lon2 = lon2 * Math.PI / 180.0;

        // radius of earth in metres
        double r = 6378100;

        // P
        double rho1 = r * Math.cos(lat1);
        double z1 = r * Math.sin(lat1);
        double x1 = rho1 * Math.cos(lon1);
        double y1 = rho1 * Math.sin(lon1);

        // Q
        double rho2 = r * Math.cos(lat2);
        double z2 = r * Math.sin(lat2);
        double x2 = rho2 * Math.cos(lon2);
        double y2 = rho2 * Math.sin(lon2);

        // Dot product
        double dot = (x1 * x2 + y1 * y2 + z1 * z2);
        double cos_theta = dot / (r * r);

        double theta = Math.acos(cos_theta);

        // Distance in Metres
        return r * theta;
    }

    double getSpeed(double distance, double timeDifference, boolean mps) {

        // where timeDiff = (p2.timeStamp - p1.timeStamp) / 1000.0

        double metersPerSecond = distance / timeDifference;

        if (mps) {
            return metersPerSecond;
        } else {
            return (metersPerSecond * 3600.0) / 1000.0;
        }

    }
}
