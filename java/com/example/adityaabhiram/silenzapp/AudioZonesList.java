package com.example.adityaabhiram.silenzapp;

import android.app.Activity;
import android.graphics.Color;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class AudioZonesList extends ArrayAdapter<SavedZones> {
    private Activity context;
    private List<SavedZones> zonesList;
    public AudioZonesList(Activity context,List<SavedZones> zonesList)
    {   super(context,R.layout.audiozones_list,zonesList);
        this.context=context;
        this.zonesList=zonesList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final View listviewzone = inflater.inflate(R.layout.audiozones_list,null,true);
        TextView textViewname = (TextView)listviewzone.findViewById(R.id.name);
        TextView textViewlatitude = (TextView)listviewzone.findViewById(R.id.latitude);
        TextView textViewlongitude = (TextView)listviewzone.findViewById(R.id.longitude);
      //  TextView textViewaltitude = (TextView)listviewzone.findViewById(R.id.altitude);
        TextView textViewadd = (TextView)listviewzone.findViewById(R.id.addre);
        SavedZones zone = zonesList.get(position);
        textViewname.setText(zone.getName());
        textViewlatitude.setText("Latitude: "+zone.getLatitude());
        textViewlongitude.setText("Longitude: "+zone.getLongitude());
        ImageView imageView = (ImageView)listviewzone.findViewById(R.id.disp);
        imageView.setImageResource(zone.image);
       // textViewaltitude.setText("Altitude: "+zone.getAltitude());
        textViewadd.setText("Address: "+zone.getAddress());
        if(zone.getOnoff()==0)
            listviewzone.setBackgroundResource(R.drawable.custom_shape3);
        else
            listviewzone.setBackgroundResource(R.drawable.custom_shape2);
        return listviewzone;
    }
}
