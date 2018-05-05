package com.example.denish.bloodbank;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by denish on 5/3/18.
 */

public class DataAdapter extends ArrayAdapter<DataItem>{

    private static final String TAG = "DataAdapter";

    Button callButton;
    Context mContext;
    double mLat,mLon,dataLat,dataLon,deltaLat,deltaLon;
    double result;String res;
    int intResult;

    public DataAdapter(@NonNull Context context, int resource, @NonNull List<DataItem> objects) {
        super(context, resource, objects);
        mContext = context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.list_item, parent, false);
        }

        TextView nameTextView = convertView.findViewById(R.id.tv_name);
        TextView distanceTextView = convertView.findViewById(R.id.tv_distance);
        callButton = convertView.findViewById(R.id.btn_call);

        SharedPreferences pref = mContext.getSharedPreferences("MyPref", 0); // 0 - for private mode

        Double mLat1 = Double.parseDouble(pref.getString("lat", "0"));
        Double mLon1 = Double.parseDouble(pref.getString("lon", "0"));

        mLat = Double.parseDouble(pref.getString("latt", mLat1+""));
        mLon = Double.parseDouble(pref.getString("lonn", mLon1+""));

        final DataItem data = getItem(position);
        dataLat = Double.parseDouble(data.getLat());
        dataLon = Double.parseDouble(data.getLon());

        //Log.d(TAG, "getView: "+ data.getName()+" = ("+mLat+","+mLon+"),("+dataLat+","+dataLon+")");

//        Location location1 = new Location("");
//        location1.setLatitude(mLat);
//        location1.setLongitude(mLon);
//
//        Location location2 = new Location("");
//        location2.setLatitude(dataLat);
//        location2.setLongitude(dataLon);

//        double al1 = location1.getAltitude();
//        double al2 = location2.getAltitude();

        //result = distance(mLat,dataLat,mLon,dataLon,al1,al2);
        intResult = dis1(mLat,mLon,dataLat,dataLon);

        String name = data.getName().substring(0, 1).toUpperCase() + data.getName().substring(1);
        nameTextView.setText(name);
        res = intResult+"";
        distanceTextView.setText(res + " km");

        if(intResult==0){
            distanceTextView.setText("so near");
        }

        callButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:"+data.getPhoneno()));
                mContext.startActivity(i);
            }
        });
        return convertView;
    }

//    public static double distance(double lat1, double lat2, double lon1,
//                                  double lon2, double el1, double el2) {
//
//        final int R = 6371; // Radius of the earth
//
//        double latDistance = Math.toRadians(lat2 - lat1);
//        double lonDistance = Math.toRadians(lon2 - lon1);
//        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
//                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
//                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//        double distance = R * c * 1000; // convert to meters
//
//        double height = el1 - el2;
//
//        distance = Math.pow(distance, 2) + Math.pow(height, 2);
//
//        distance/=1000;
//        return Math.sqrt(distance);
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
