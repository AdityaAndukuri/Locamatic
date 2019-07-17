package com.example.adityaabhiram.silenzapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class InternetMap extends FragmentActivity implements OnMapReadyCallback,KeyInputDialog.KeyInputDialogListener {

    private GoogleMap mMap;
    FusedLocationProviderClient myloc;
    TextView curprof;
    Button but;
    boolean mylocperm;
    private FirebaseAuth firebaseAuth;
    private Location mylastloc;
    private static final int MY_PERMISSION_REQUEST_CODE = 7192;
    private static final int DEFAULT_ZOOM=15;
    private LatLng mydefaultlocation=new LatLng(-33.5363,140.13265321);
    public String id="hello";
    public int zone_rad=0;
    DatabaseReference ref,ref2;
    GeoFire geoFire;
    Marker Mycurrent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);
        myloc = LocationServices.getFusedLocationProviderClient(this);
        curprof = (TextView) findViewById(R.id.textView);
        but = (Button) findViewById(R.id.button);
        mylocperm = false;
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null)
        {
            finish();
            startActivity(new Intent(this,Signin.class));
        }
        FirebaseUser user = firebaseAuth.getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference("MyWifiLoc");
        ref2=FirebaseDatabase.getInstance().getReference(user.getUid()+"/WIFI");
        geoFire = new GeoFire(ref);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });


    }
    @Override
    public void applyText(String zone_name,int radius) {
        id=zone_name;
        zone_rad = radius;
        if(TextUtils.isEmpty(id)||zone_rad<=0)
        {
            Toast.makeText(this,"Invalid input, please enter again :(",Toast.LENGTH_SHORT).show();
            openDialog();
        }
        else
        {
            addLocation();
        }
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
        //prompt the user to give access to current location
        getPermission();
        updateLocUI();
        // get access to location
        //getLocation();
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot audiozoneSnapShot: dataSnapshot.getChildren()){
                    SavedZones zone = audiozoneSnapShot.getValue(SavedZones.class);
                    if(firebaseAuth.getCurrentUser()==null)
                    {
                        finish();
                        startActivity(new Intent(getApplicationContext(),Signin.class));
                    }
                    setZones(mMap,zone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void getPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mylocperm = true;
            updateLocUI();
            getLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
       // mylocperm=false;
        switch (requestCode)
        {
            case 1:
            {
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    mylocperm=true;
                    updateLocUI();
                    getLocation();
                }
            }

        }


    }
    private void updateLocUI()
    {
        if(mMap==null)
        {
            return;
        }
        try
        {
            if(mylocperm==true)
            {
                Toast.makeText(getApplicationContext(),"update",Toast.LENGTH_SHORT).show();
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMapToolbarEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setAllGesturesEnabled(true);

            }
            else
            {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                getPermission();
            }
        }catch(SecurityException e)
        {
            Log.e("SecurityException",e.getMessage());
        }
    }
    private void getLocation()
    {
        try
        {
            if(mylocperm==true)
            {
                Task<Location> locres = myloc.getLastLocation();
                locres.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if(task.isSuccessful()) {
                            mylastloc = task.getResult();
                            curprof.setText(String.format("your location is %f / %f ",mylastloc.getLatitude(),mylastloc.getLongitude()));
                            if(Mycurrent!=null)
                            {
                                Mycurrent.remove();//remove old marker
                            }
                            Mycurrent=mMap.addMarker(new MarkerOptions().position(new LatLng(mylastloc.getLatitude(),mylastloc.getLongitude())).title("you"));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mylastloc.getLatitude(), mylastloc.getLongitude()), 40.0f));
                        }
                        else
                        {
                            Log.d("result_status","current location is null");
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mydefaultlocation,DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);

                        }
                    }
                });
            }
        }catch(SecurityException e)
        {
            Log.e("SecurityException",e.getMessage());
        }
    }
    private void addLocation()
    {   if(firebaseAuth.getCurrentUser()==null)
    {
        finish();
        startActivity(new Intent(getApplicationContext(),Signin.class));
    }
        try
        {
            if(mylocperm==true)
            {   Toast.makeText(getApplicationContext(),"update",Toast.LENGTH_SHORT).show();
                Task<Location> locres = myloc.getLastLocation();
                locres.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if(task.isSuccessful()) {
                            mylastloc = task.getResult();
                            final double latitude = mylastloc.getLatitude();
                            final double longitude = mylastloc.getLongitude();
                            saveZone(latitude,longitude);
                            geoFire.setLocation("you", new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
                                @Override
                                public void onComplete(String key, DatabaseError error) {
                                    //Add marker
                                   // if(Mycurrent!=null)
                                    //{
                                       // Mycurrent.remove();//remove old marker
                                   // }
                                    //Mycurrent=mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title("you"));
                                    //move camera to this position
                                   // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),20.0f));
                                }
                            });
                            curprof.setText(String.format("your location is %f / %f ",latitude,longitude));
                           // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mylastloc.getLatitude(), mylastloc.getLongitude()), DEFAULT_ZOOM));
                        }
                        else
                        {
                            Log.d("result_status","current location is null");
                           // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mydefaultlocation,DEFAULT_ZOOM));
                            //mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }


                });
            }
        }catch(SecurityException e)
        {
            Log.e("SecurityException",e.getMessage());
        }
    }
    public void openDialog() {
        KeyInputDialog keyInputDialog = new KeyInputDialog();
        keyInputDialog.setCancelable(false);
        keyInputDialog.show(getSupportFragmentManager(),"Add Name to zone ");
    }

    private void saveZone(double latitude,double longitude) {
        final SavedZones zone = new SavedZones(latitude,longitude,id,zone_rad,"",0,0);
        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(id))
                {
                    Toast.makeText(getApplicationContext(),"Already existing zone name :( try again",Toast.LENGTH_SHORT).show();
                    openDialog();
                }
                else
                {
                    //String id=ref2.push().getKey();
                    ref2.child(id).setValue(zone, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            Toast.makeText(getApplicationContext(),"Successfully added zone",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void setZones(GoogleMap mMap,SavedZones zone)
    {   Circle mapCircle;
        LatLng silent_zone = new LatLng(zone.latitude,zone.longitude);
        if(firebaseAuth.getCurrentUser()==null)
        {
            finish();
            startActivity(new Intent(getApplicationContext(),Signin.class));
        }
        mapCircle=mMap.addCircle(new CircleOptions()
                .center(silent_zone)
                .radius(zone.getRadius())
                .strokeColor(Color.BLUE)
                .fillColor(0x220000FF)
                .strokeWidth(5.0f));
        if(mapCircle!=null){
            mapCircle.remove();

        }
        if(zone.getOnoff()==1)
            mapCircle=mMap.addCircle(new CircleOptions()
                    .center(silent_zone)
                    .radius(zone.getRadius())
                    .strokeColor(Color.BLUE)
                    .fillColor(0x220000FF)
                    .strokeWidth(5.0f));
        else
        {
            mapCircle=mMap.addCircle(new CircleOptions()
                    .center(silent_zone)
                    .radius(zone.getRadius())
                    .strokeColor(Color.RED)
                    .fillColor(0x22FF3333)
                    .strokeWidth(5.0f));
        }
        final int onoff = zone.getOnoff();
        //Add GeoQuery here
        // 0.01f = 10 meters
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(silent_zone.latitude,silent_zone.longitude),((float)zone.getRadius())/1000);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {


                        Toast.makeText(getApplicationContext(), "normal", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onKeyExited(String key) {
                // sendNotification("Aditya",String.format("%s exited the silent zone",key));



                    Toast.makeText(getApplicationContext(), "normal", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                curprof.setText(String.format("%s moved within the silent zone [%f/%f]",key,location.latitude,location.longitude));
                Log.d("MOVE",String.format("%s moved within the silent zone [%f/%f]",key,location.latitude,location.longitude));
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.e("ERROR",""+error);
            }
        });

    }

}
