package com.bairock.hamadev.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bairock.hamadev.R;

import java.util.List;

public class AdapterVideoDevice extends ArrayAdapter<String> {

    Context context;
    private List<String> items;

    public AdapterVideoDevice(final Context context, final int textViewResourceId, List<String> objects) {
        super(context, textViewResourceId, objects);
        this.items = objects;
        this.context = context;
    }

    @Override
    public View getDropDownView(int position, View convertView,@NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.video_device_list, parent, false);
        }

        TextView tv = convertView.findViewById(R.id.txtName);
        tv.setText(items.get(position));
        return convertView;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.video_device_list, parent, false);
        }

        TextView tv = convertView.findViewById(R.id.txtName);
        tv.setText(items.get(position));
        return convertView;
    }
}
