package com.example.denish.bloodbank;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;

import static com.example.denish.bloodbank.MainActivity.RC_TYPE;

public class SelectGroup extends AppCompatActivity{

    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private Boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
//    private LocationRequest mLocationRequest;
//    private GoogleApiClient mGoogleApiClient;

    private static final String TAG = "SelectGroup";
    EditText mSelectGroup,mMobileno;
    Button mNext;
    String type;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group);

        mSelectGroup = findViewById(R.id.et_select_group);
        mMobileno = findViewById(R.id.et_mobileno);
        mNext = findViewById(R.id.btn_next);

//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
        getLocationPermission();

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Inside SelectGroup Listener");
                if((type = mSelectGroup.getText().toString()).length() > 0 &&
                        type.equals("O+") || type.equals("O-") || type.equals("A-") || type.equals("A+")
                        || type.equals("B+") || type.equals("B-") || type.equals("AB-") || type.equals("AB+")){

                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();

                    editor.putString("group", type); // Storing string
                    editor.putString("mobile",mMobileno.getText().toString());
                    editor.apply();
                    Log.d(TAG, "onClick: preference added (group,mobile) :" + type + "," + mMobileno.getText().toString());

                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
//                    intent.putExtra("group","O+ve");
//                    intent.putExtra("group",type);
//                    intent.putExtra("mobile","9988998877");
//                    intent.putExtra("mobile",mMobileno.getText().toString());
//                    setResult(RC_TYPE,intent);
                    startActivity(intent);
                }else{
                    Toast.makeText(SelectGroup.this, "Write Proper Blood Group Please", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //calling getDeviceLocation()
    private void init(){
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
//            mMap.setMyLocationEnabled(true);
//            mMap.getUiSettings().setMyLocationButtonEnabled(false);
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
            }
        }else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
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
                    Toast.makeText(this, "Permission Granted : OnRequest", Toast.LENGTH_SHORT).show();
                    //initialize our map
                    Log.d(TAG, "onRequestPermissionsResult: calling init");
                    init();
                }
            }
        }
    }
}
