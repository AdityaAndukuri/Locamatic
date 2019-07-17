package com.example.adityaabhiram.silenzapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
public class INTERNET_ZONES extends AppCompatActivity {
    private Button new_int;
    private Button exist_int;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet__zones);
        new_int = (Button)findViewById(R.id.new_int_zone);
        exist_int=(Button)findViewById(R.id.exist_int_zones);
        new_int.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_new_int_zone();
            }
        });
        exist_int.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_exist_int_zones();
            }
        });
    }
    public void open_new_int_zone()
    {
        //Intent in = new Intent(this,Add_int_zone.class);
        Intent in = new Intent(this,MapWifi.class);
        startActivity(in);

    }
    public void open_exist_int_zones()
    {
        Intent in = new Intent(this,List_tabbed.class);
        in.putExtra("type","WifiZones");
        startActivity(in);
    }
}
