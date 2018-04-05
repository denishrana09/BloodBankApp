package com.example.denish.bloodbank;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends BaseActivity {

    //RDX - @a123456 , DENRAN
    private static final String TAG = "MainActivity";
    public static final int RC_SIGN_IN = 1;
    public static final int RC_TYPE = 123;
    private static final String ANONYMOUS = "anonymous";
    private static final int REQUEST_PHONE_CALL = 11;
//    String type;

    private ListView mListView;
    private DataAdapter mDataAdapter;
    private ProgressBar mProgressBar;
    private Button temp;

    private String mUsername;
    private String mMobileNumber;
    private String mGroup;
    private String mLat;
    private String mLon;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabaseReference;
    private ChildEventListener mChildEventListener;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    List<DataItem> currentdataItems = new ArrayList<>();
    String queryResult;


    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 789;
    private Boolean mLocationPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activeToolbar(false);

        Log.d(TAG, "onCreate: starts");
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        mUserDatabaseReference = mFirebaseDatabase.getReference().child("users");

        // initializing references to views
        //mProgressBar = findViewById(R.id.progress_bar);
        mListView = findViewById(R.id.itemListView);
        temp = findViewById(R.id.temp_button);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d(TAG, "onAuthStateChanged: inside auth");
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    onSignedInInitialized(user.getDisplayName());

                } else {
                    // User is signed out
                    onSignedOutCleanup();
                    Log.d(TAG, "onAuthStateChanged: starting auth activity");
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (isFirstRun) {
            Log.d(TAG, "onCreate: before Intent of SelectGroup");
            Intent i1 = new Intent(getApplicationContext(),SelectGroup.class);
            startActivity(i1);
            Log.d(TAG, "onCreate: after Intent of SelectGroup");
            Toast.makeText(MainActivity.this, "First Run", Toast.LENGTH_LONG)
                    .show();
        }

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode

        mGroup = pref.getString("group", null);
        mMobileNumber = pref.getString("mobile", null);
        mLat = pref.getString("lat",null);
        mLon = pref.getString("lon",null);
        Log.d(TAG, "onCreate: (Group,Mobile,Lat,Lon) : " + mGroup + "," + mMobileNumber + ","+mLat + "," + mLon);


        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).apply();

        // remove himself from result
//        if (currentdataItems.size()>0)
//            currentdataItems.remove(0);
        Log.d(TAG, "onCreate: CurrentDataItems : "+ currentdataItems.toString());
        //currentdataItems.add(new DataItem("bro","8956895689","O+","23.25698215","67.23568952"));
        //logic();

        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Temp clicked");

                // Clear input box
                DataItem dataItem =
                        new DataItem(mUsername,mMobileNumber,mGroup,mLat,mLon);
                mUserDatabaseReference.push().setValue(dataItem);
                Log.d(TAG, "onClick: data sent");
            }
        });

        getCallPermission();
        getLocationPermission();
//        startService(new Intent(this, MyService.class));

    }

    private void onSignedInInitialized(String displayName) {
        Log.d(TAG, "onSignedInInitialized: starts");
        //Log.d(TAG, "onSignedInInitialized: IsFirstRun "+ isFirstRunClone);
        mUsername = displayName;
        //isFirstRunClone = true;
        attachDatabaseReadListener();
    }

    private void onSignedOutCleanup(){
        Log.d(TAG, "onSignedOutCleanup: starts");
        //Log.d(TAG, "onSignedOutCleanup: IsFirstRun "+ isFirstRunClone);
        mUsername = ANONYMOUS;
        if(mDataAdapter!=null) {
            if (!mDataAdapter.isEmpty())
                mDataAdapter.clear();
        }
        //isFirstRunClone = true;
        detachDatabaseReadListener();
    }

    private void attachDatabaseReadListener(){
        Log.d(TAG, "attachDatabaseReadListener: attached");
        if(mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    DataItem dataItem = dataSnapshot.getValue(DataItem.class);
                    //Log.d(TAG, "onChildAdded: DataItem : " + dataItem.toString());
                    if(dataItem.getBloodgroup().equals(queryResult)){
                        currentdataItems.add(dataItem);
                        Log.d(TAG, "onChildAdded: Added DataItem = "+ dataItem.toString());
                        Log.d(TAG, "onChildAdded: true "+ dataItem.getBloodgroup() + ", query = " + queryResult);
                    }else {
                        //Log.d(TAG, "onChildAdded: false "+ dataItem.getBloodgroup() + ", query = " + queryResult);
                    }
                    //if(currentdataItems.size() == 4)
                        logic();
                    //Log.d(TAG, "onChildAdded: after if");
                    //Log.d(TAG, "onChildAdded: currentDataItems : "+ currentdataItems.toString());
                    //mDataAdapter.add(dataItem);
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
        }
        Log.d(TAG, "attachDatabaseReadListener: before child");
        mUserDatabaseReference.addChildEventListener(mChildEventListener);
        for(DataItem d : currentdataItems){
            Log.d(TAG, "attachDBListener: " + d.toString() + "\n");
        }
    }

    private void detachDatabaseReadListener(){
        Log.d(TAG, "detachDatabaseReadListener: detached");
        if(mChildEventListener != null) {
            mUserDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    public void getCallPermission(){
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
        }else{
            //Toast.makeText(this, "call permission done", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "getCallPermission: call permission done");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: created");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: selecting option");
        switch (item.getItemId()){
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                //isFirstRunClone = true;
                return true;
            case R.id.action_search:
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: starts");
        //Log.d(TAG, "onResume: IsFirstRun " + isFirstRunClone);
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        queryResult = sharedPreferences.getString("query","");
        if(queryResult.length() > 0){
            Toast.makeText(this, "Blood Group : "+ queryResult, Toast.LENGTH_SHORT).show();
        }

        for(DataItem d : currentdataItems){
            Log.d(TAG, "onResume: " + d.toString() + "\n");
        }


        startService(new Intent(this, MyService.class));

        //logic();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: starts");
        super.onPause();
        //Log.d(TAG, "onPause: IsFirstRun "+ isFirstRunClone);
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        detachDatabaseReadListener();
        if(mDataAdapter!=null){
            if(!mDataAdapter.isEmpty())
                mDataAdapter.clear();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: starts");
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                Toast.makeText(this, "Signed In", Toast.LENGTH_SHORT).show();
            }
            else if(resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Signed In Cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PHONE_CALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(this, "got permission for call", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onRequestPermissionsResult: got permission for call");
                }
                return;
            }
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i=0; i<grantResults.length;i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    Log.d(TAG, "onRequestPermissionsResult: " + mLocationPermissionGranted);
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        for(DataItem d : currentdataItems){
            Log.d(TAG, "onStart: " + d.toString() + "\n");
        }
        startService(new Intent(this, MyService.class));
    }

    public void logic(){
        if(currentdataItems!=null && mLat!=null && mLon!=null) {
            Collections.sort(currentdataItems, new SortPlaces(new LatLng(Double.parseDouble(mLat), Double.parseDouble(mLon))));
            for (DataItem d : currentdataItems) {
                Log.d(TAG, "logic: Items : " + d.toString());
            }
        }
        // remove himself from result
//        if (currentdataItems.size()>0)
//            currentdataItems.remove(0);
        //Log.d(TAG, "onStart: CurrentDataItems : "+ currentdataItems.toString());
        if(currentdataItems!=null) {
            mDataAdapter = new DataAdapter(this, R.layout.list_item, currentdataItems);
            mListView.setAdapter(mDataAdapter);
            startService(new Intent(this, MyService.class));
        }
    }

    private void getLocationPermission(){
        String permissions[] = {android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION};

        Log.d(TAG, "getLocationPermission: before if condition");
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true;
                Log.d(TAG, "permission : " + mLocationPermissionGranted);
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

}
