package com.example.denish.bloodbank;

/**
 * Created by denish on 5/4/18.
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyService extends Service
{
    private static final String TAG = "MyService";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 500;//1000;
    private static final float LOCATION_DISTANCE = 1000f;//2000f;

    String dbLat,mLat="";
    String dbLon,mLon="";
    String mobile;

    FirebaseDatabase mFirebaseDatabase;
    ChildEventListener mChildEventListener;
    DatabaseReference mDBRef;

    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;

        public LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            Log.e(TAG, "onLocationChanged: " + location);
//            Toast.makeText(MyService.this, "Location : " + location.getLatitude() + ", " +
//                    location.getLongitude(), Toast.LENGTH_SHORT).show();
            mLastLocation.set(location);
            mLat = location.getLatitude()+"";
            mLon = location.getLongitude()+"";
            if(mLat!=null && mLon!=null && dbLat!=null && dbLon!=null) {
                if (mLat.length() > 0 && mLon.length() > 0 && dbLat.length() > 0 && dbLon.length() > 0) {
                    Log.d(TAG, "onCreate: inside first if");
                    Log.d(TAG, "onCreate: (mLat,mLon):" + mLat + "," + mLon + " & (dbLat,dbLon):" + dbLat + "," + dbLon);
                    if (!mLat.equals(dbLat) && !mLon.equals(dbLon)) {
                        mDBRef.child(mobile).child("lat").setValue(mLat);//updateChildren(userMap);
                        mDBRef.child(mobile).child("lon").setValue(mLon);//updateChildren(userMap);
                        //Toast.makeText(MyService.this, "Diff ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate()
    {
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        mobile = pref.getString("mobile", "");


        FirebaseApp.initializeApp(getApplicationContext());
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDBRef = mFirebaseDatabase.getReference().child("users");//.child("9090989890");



        if(mChildEventListener==null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    DataItem user = dataSnapshot.getValue(DataItem.class);
                    if(user.getPhoneno().equals(mobile)) {
                        dbLat = user.getLat();
                        dbLon = user.getLon();
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                        SharedPreferences.Editor editor = pref.edit();

                        editor.putString("latt", dbLat); // Storing string
                        editor.putString("lonn", dbLon);
                        editor.apply();
                        Log.e(TAG, "onChildAdded: got value (" + dbLat + "," + dbLon +")");
                        //Toast.makeText(MyService.this, "got value (" + dbLat + "," + dbLon +")", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mDBRef.addChildEventListener(mChildEventListener);
        }

//        if(mLat.length()>0 && mLon.length()>0 && dbLat.length()>0 && dbLon.length()>0) {
//            Log.d(TAG, "onCreate: inside first if");
//            Log.d(TAG, "onCreate: (mLat,mLon):"+ mLat+","+mLon + " & (dbLat,dbLon):"+ dbLat+","+dbLon);
//            if(!mLat.equals(dbLat) && !mLon.equals(dbLon)) {
//                mDBRef.child("lat").setValue(mLat);//updateChildren(userMap);
//                mDBRef.child("lon").setValue(mLon);//updateChildren(userMap);
//                Double diff = Double.parseDouble(dbLat) - Double.parseDouble(dbLon);
//                Toast.makeText(this, "Diff : " + diff, Toast.LENGTH_SHORT).show();
//            }
//        }

//        sharedLat = pref.getString("lat", "");
//        sharedLon = pref.getString("lon", "");
//        mDBRef.child(mobile).updateChildren();
//        Map<String,Object> userMap = new HashMap<String,Object>();
//        userMap.put("lat", mLat);
//        userMap.put("lon", mLon);

    }

    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

}
