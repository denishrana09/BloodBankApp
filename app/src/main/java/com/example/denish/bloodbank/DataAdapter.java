package com.example.denish.bloodbank;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

    Button callButton;
    Context mContext;

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

        final DataItem data = getItem(position);
        String name = data.getName().substring(0, 1).toUpperCase() + data.getName().substring(1);
        nameTextView.setText(name);
        distanceTextView.setVisibility(View.INVISIBLE);
        //distanceTextView.setText("12 km");

        callButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:"+data.getPhoneno()));
                mContext.startActivity(i);
            }
        });

//        nameTextView.setShadowLayer(1.5f,-2,2, Color.GRAY);
//        distanceTextView.setShadowLayer(1.5f,-2,2,Color.GRAY);
        return convertView;
    }
}
