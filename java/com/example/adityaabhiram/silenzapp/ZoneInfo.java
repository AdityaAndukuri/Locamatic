package com.example.adityaabhiram.silenzapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class ZoneInfo extends FragmentActivity implements OnMapReadyCallback,DeleteZone.DeleteZoneListener {

    private GoogleMap mMap;
    SavedZones zone;
    String name;
    double latitude;
    double longitude;
    Switch aSwitch;
    int on_off;
    int radius;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    Circle mapCircle;
    double altitude;
    String address;
    public Toolbar toolbar;
    private ImageButton deleteButton;
    TextView textView;
    String type;
    int image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone_info);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);
        Intent callingIntent = getIntent();
        name = callingIntent.getExtras().getString("name");
        latitude = callingIntent.getExtras().getDouble("latitude");
        longitude = callingIntent.getExtras().getDouble("longitude");
        on_off = callingIntent.getExtras().getInt("onoff");
        radius = callingIntent.getExtras().getInt("radius");
        type= callingIntent.getStringExtra("type");
        address = callingIntent.getStringExtra("add");
        altitude=callingIntent.getExtras().getDouble("alt");
        image = callingIntent.getExtras().getInt("image",R.drawable.logo_new);
        zone = new SavedZones(latitude,longitude,name,radius,address,altitude,image);
        textView = (TextView)findViewById(R.id.address3);
        textView.setText(address);
        zone.setOnoff(on_off);
        aSwitch=(Switch)findViewById(R.id.zone_activation_switch);
        deleteButton=(ImageButton)findViewById(R.id.delete_zone);
        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        toolbar=(Toolbar)findViewById(R.id.Zone_Name);
        toolbar.setTitle(name.toUpperCase());
        if(zone.getOnoff()==1)
        {
            aSwitch.setChecked(true);
            toolbar.setBackgroundColor(Color.BLUE);

        }
        else
        {
            aSwitch.setChecked(false);
            toolbar.setBackgroundColor(Color.RED);
        }
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(aSwitch.isChecked())
                {
                    zone.setOnoff(1);
                    toolbar.setBackgroundColor(Color.BLUE);
                    databaseReference=FirebaseDatabase.getInstance().getReference(user.getUid()+"/"+type).child(zone.getName());
                    if(databaseReference.getKey()!=null) {
                        databaseReference.setValue(zone);
                        Toast.makeText(getApplicationContext(), "Zone is Activated", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {   zone.setOnoff(0);
                    toolbar.setBackgroundColor(Color.RED);
                    databaseReference=FirebaseDatabase.getInstance().getReference(user.getUid()+"/"+type).child(zone.getName());
                    if(databaseReference.getKey()!=null) {
                        databaseReference.setValue(zone);
                        Toast.makeText(getApplicationContext(), "Zone is Deactivated", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
    }
    public void openDialog()
    {
        DeleteZone deleteDialog = new DeleteZone();
        deleteDialog.show(getSupportFragmentManager(),"Delete zone dialog");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        final LatLng zone_location = new LatLng(zone.getLatitude(),zone.getLongitude());
        mMap.addMarker(new MarkerOptions().position(zone_location).title(zone.getName()));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(zone_location));
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference(user.getUid()+"/"+type).child(zone.getName());
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SavedZones zone_modified = dataSnapshot.getValue(SavedZones.class); //retrieving single object from datasnapshot
                if(zone_modified!=null) {
                    if (mapCircle != null) {
                        mapCircle.remove();

                    }
                    if (zone_modified.getOnoff() == 1)
                        mapCircle = mMap.addCircle(new CircleOptions()
                                .center(zone_location)
                                .radius(zone_modified.getRadius())
                                .strokeColor(Color.BLUE)
                                .fillColor(0x220000FF)
                                .strokeWidth(5.0f));
                    else {
                        mapCircle = mMap.addCircle(new CircleOptions()
                                .center(zone_location)
                                .radius(zone_modified.getRadius())
                                .strokeColor(Color.RED)
                                .fillColor(0x22FF3333)
                                .strokeWidth(5.0f));
                    }
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(zone.getOnoff()==1)
        mapCircle=mMap.addCircle(new CircleOptions()
                .center(zone_location)
                .radius(zone.getRadius())
                .strokeColor(Color.BLUE)
                .fillColor(0x220000FF)
                .strokeWidth(5.0f));
        else
        {
            mapCircle=mMap.addCircle(new CircleOptions()
                    .center(zone_location)
                    .radius(zone.getRadius())
                    .strokeColor(Color.RED)
                    .fillColor(0x22FF6666)
                    .strokeWidth(5.0f));
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(zone.getLatitude(),zone.getLongitude()),20.0f));
    }

    @Override
    public void deleteFlag(int flag) {
        if(flag==1)
        {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(user.getUid()+"/"+type).child(zone.getName());
                zone.setOnoff(0);
                ref.setValue(zone);
            Toast.makeText(getApplicationContext(),"Zone Deleted Successfully!!!",Toast.LENGTH_SHORT).show();
            /*try
            {
                Thread.sleep(1000);
            }
            catch (Exception e)
            {
                Toast.makeText(this,"Interrupt",Toast.LENGTH_SHORT).show();
            }*/
            ref.removeValue();
            finish();

        }
    }
}
