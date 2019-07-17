package com.example.adityaabhiram.silenzapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AUDIO_ZONES extends AppCompatActivity {
    private Button add_aud_zone;
    private Button exist_aud_zone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio__zones);
        add_aud_zone=(Button)findViewById(R.id.new_aud_zone);
        exist_aud_zone=(Button)findViewById(R.id.exist_aud_zones);
        add_aud_zone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_add_aud_zone();
            }
        });
        exist_aud_zone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_exist_aud_zones();
            }
        });
    }
    public void open_add_aud_zone()
    {
        Intent in = new Intent(this,MapsActivity.class);
        startActivity(in);
    }
    public void open_exist_aud_zones()
    {
        Intent in = new Intent(this,List_tabbed.class);
        in.putExtra("type","AudioZones");
        startActivity(in);
    }



}