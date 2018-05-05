package com.example.denish.bloodbank;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.Comparator;

/**
 * Created by denish on 10/3/18.
 */

public class SortPlaces implements Comparator<DataItem> {
    LatLng currentLoc;
    private static final String TAG = "SortPlaces";

    public SortPlaces(LatLng latLng){
        currentLoc = new LatLng(latLng.latitude,latLng.longitude);
    }

    @Override
    public int compare(DataItem o1, DataItem o2) {
        double lat1 = Double.parseDouble(o1.getLat());
        double lon1 = Double.parseDouble(o1.getLon());
        double lat2 = Double.parseDouble(o2.getLat());
        double lon2 = Double.parseDouble(o2.getLon());

        double distanceToPlace1 = dis1(currentLoc.latitude, currentLoc.longitude, lat1, lon1);
        double distanceToPlace2 = dis1(currentLoc.latitude, currentLoc.longitude, lat2, lon2);
        Log.d(TAG, "compare: dis1 - dis2 : " + (distanceToPlace1 - distanceToPlace2));
        Log.d(TAG, "compare: o1 = " + o1.getName() + ", o2 = "+ o2.getName() );
        return (int) (distanceToPlace1 - distanceToPlace2);
    }

//    public double distance(double fromLat, double fromLon, double toLat, double toLon) {
//        double radius = 6378137;   // approximate Earth radius, *in meters*
//        double deltaLat = toLat - fromLat;
//        double deltaLon = toLon - fromLon;
//        double angle = 2 * Math.asin( Math.sqrt(
//                Math.pow(Math.sin(deltaLat/2), 2) +
//                        Math.cos(fromLat) * Math.cos(toLat) *
//                                Math.pow(Math.sin(deltaLon/2), 2) ) );
//        return radius * angle;
//    }


//    //not using
//    public double distance(double fromLat, double fromLon, double toLat, double toLon){
//        double deltaLat = toLat - fromLat;
//        double deltaLon = toLon - fromLon;
//        double result = Math.sqrt(Math.pow(deltaLat,2)+Math.pow(deltaLon,2));
//        //Log.d(TAG, "distance: " + deltaLon);
//        return result;
//    }

    public int dis1(double userLat, double userLng,
                    double venueLat, double venueLng) {
        double AVERAGE_RADIUS_OF_EARTH_KM = 6371;
        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (int) (Math.round(AVERAGE_RADIUS_OF_EARTH_KM * c));
    }
}
