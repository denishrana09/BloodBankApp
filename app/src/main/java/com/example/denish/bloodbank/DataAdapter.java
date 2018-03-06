package com.example.denish.bloodbank;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by denish on 5/3/18.
 */

public class DataAdapter extends ArrayAdapter<DataItem>{

    public DataAdapter(@NonNull Context context, int resource, @NonNull List<DataItem> objects) {
        super(context, resource, objects);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.list_item, parent, false);
        }

        TextView nameTextView = convertView.findViewById(R.id.tv_name);
        TextView distanceTextView = convertView.findViewById(R.id.tv_distance);

        DataItem data = getItem(position);
        nameTextView.setText(data.getName());
        distanceTextView.setText("12 km");
        return convertView;
    }
}
