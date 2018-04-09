package com.example.denish.bloodbank;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SelectGroup extends BaseActivity
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private GoogleApiClient mGoogleApiClient;

    private Boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private static final String TAG = "SelectGroup";
    EditText mSelectGroup, mMobileno;
    Button mNext;
    String type;
    double longitude,latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group);

        activeToolbar(true);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mSelectGroup = findViewById(R.id.et_select_group);
        mMobileno = findViewById(R.id.et_mobileno);
        mNext = findViewById(R.id.btn_next);

        LocationRequest request = LocationRequest.create();
        LocationSettingsRequest settingsRequest = new LocationSettingsRequest.Builder()
                .addLocationRequest(request)
                .build();
        LocationServices.getSettingsClient(SelectGroup.this)
                .checkLocationSettings(settingsRequest)
                .addOnCompleteListener(new OnCompleteListener() {
                    public void onComplete(Task task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "onComplete: task successful , calling getLocationPermission");
                            getLocationPermission();
                            Log.d(TAG, "onComplete: task successful , calling getDeviceLocation");
                            getDeviceLocation();
                        } else {
                            Exception e = task.getException();
                            if (e instanceof ResolvableApiException) {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult()
                                try {
                                    ((ResolvableApiException)e).startResolutionForResult(SelectGroup.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException e1) {
                                    e1.printStackTrace();
                                }
                            } else {
                                //Location can not be resolved, inform the user
                            }
                        }
                    }
                });

        final String mob = mMobileno.getText().toString();
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Inside SelectGroup Listener");
                if ((type = mSelectGroup.getText().toString()).length() > 0 &&
                        type.equalsIgnoreCase("O+") || type.equalsIgnoreCase("O-")
                        || type.equalsIgnoreCase("A-") || type.equalsIgnoreCase("A+")
                        || type.equalsIgnoreCase("B+") || type.equalsIgnoreCase("B-")
                        || type.equalsIgnoreCase("AB-") || type.equalsIgnoreCase("AB+")) {

                    if(mMobileno.getText().toString() != null && mMobileno.getText().toString().length() > 0){// && mob.matches("639[0-9]{9}")) {

                        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                        SharedPreferences.Editor editor = pref.edit();

                        editor.putString("group", type.toUpperCase()); // Storing string
                        editor.putString("mobile", mMobileno.getText().toString());
                        editor.apply();
                        Log.d(TAG, "onClick: preference added (group,mobile) :" + type + "," + mMobileno.getText().toString());
                        Log.d(TAG, "onClick: lat,lon : " + latitude + "," + longitude);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }else {
                        Toast.makeText(SelectGroup.this, "Write Proper Mobile Number Please", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SelectGroup.this, "Write Proper Blood Group Please", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //calling getDeviceLocation()
    private void init() {
        Log.d(TAG, "init: starts");
        if (mLocationPermissionGranted) {
            Log.d(TAG, "init: calling getDeviceLocation");

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                    (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "init: returning");
                return;
            }
            getDeviceLocation();
        }
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionGranted) {

                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location");
                            Location currentLocation = (Location) task.getResult();
                            //LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            Log.d(TAG, "onComplete: currentlocation is " + currentLocation);

                            if(currentLocation !=null) {
                                double lat = currentLocation.getLatitude();
                                double lon = currentLocation.getLongitude();

                                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("lat", lat + "");
                                editor.putString("lon", lon + "");
                                editor.apply();
                                latitude = lat;
                                longitude = lon;
                                Log.d(TAG, "onComplete: lat,lon : " + lat + "," + lon);
                            }
                        }
                    }
                });
            }

        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException " + e.getMessage());
        }
    }

    //calling init() after complete
    private void getLocationPermission(){
        String permissions[] = {android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

        Log.d(TAG, "getLocationPermission: before if condition");
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true;
                Log.d(TAG, "getLocationPermission: calling init");
                init();
            }else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
                Log.d(TAG, "getLocationPermission: else part 1");
            }
        }else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
            Log.d(TAG, "getLocationPermission: else part 2");
        }
    }

    //calling init() after complete
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i=0; i<grantResults.length;i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    //Toast.makeText(this, "Permission Granted : OnRequest", Toast.LENGTH_SHORT).show();
                    //initialize our map
                    Log.d(TAG, "onRequestPermissionsResult: calling init");
                    init();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
// Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        //startLocationUpdates();
                        getLocationPermission();
                        Log.d(TAG, "onActivityResult: Result Ok for GPS");
                        //Toast.makeText(this, "onActivityResult Ok", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        getLocationPermission();
                        //settingsrequest();//keep asking if imp or do whatever
                        Log.d(TAG, "onActivityResult: Result cancelled for GPS");
                        //Toast.makeText(this, "onActivityResult cancelled", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
