package com.example.adityaabhiram.silenzapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

//import android.location.LocationListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        android.location.LocationListener, KeyInputDialog.KeyInputDialogListener {

    private GoogleMap mMap;
    //play services location
    private static final int MY_PERMISSION_REQUEST_CODE = 7192;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 300193;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth firebaseAuth;
    private Location mLastLocation;
    private static int UPDATE_INTERVAL = 5000; //2 secs
    private static int FAST_INTERVAL = 100; //1 secs
 //   private static int DISPLACEMENT = 5;
    private TextView textView;
    private AudioManager audioManager,myaudio;
    public String id="hello";
    public int zone_rad=0;
    DatabaseReference ref,ref2;
    GeoFire geoFire;
    Marker Mycurrent;
    Button click;
    Geocoder geocoder;
    TextView address;
    List<Address> addresses;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map1);
        textView=(TextView)findViewById(R.id.LatLong);
        address=(TextView)findViewById(R.id.address);
        geocoder= new Geocoder(this,Locale.getDefault());

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null)
        {
            finish();
            startActivity(new Intent(this,Signin.class));
        }
        FirebaseUser user = firebaseAuth.getCurrentUser();
        mapFragment.getMapAsync(this);
        ref = FirebaseDatabase.getInstance().getReference("MyLocation");
        ref2=FirebaseDatabase.getInstance().getReference(user.getUid()+"/AudioZones");
        geoFire = new GeoFire(ref);
        audioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        myaudio=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        click=(Button)findViewById(R.id.Addl);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();

            }
        });
        setUpLocation();
    }
    // press cntl + o

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case MY_PERMISSION_REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(checkPlayServices())
                    {
                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();
                    }
                }
                break;

        }
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
        {
            addLocation();
        }
    }
    public void addLocation()
    {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(firebaseAuth.getCurrentUser()==null)
        {
            finish();
            startActivity(new Intent(getApplicationContext(),Signin.class));
        }
        if(mLastLocation!=null) {
            final double latitude = mLastLocation.getLatitude();
            final double longitude = mLastLocation.getLongitude();
            final double altitude = mLastLocation.getAltitude();
            String Fulladdress="";
            try
            {
               addresses = geocoder.getFromLocation(latitude,longitude,1);
               Fulladdress = addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getLocality()+", "+addresses.get(0).getAdminArea();
               //address.setText(Fulladdress);
            }
            catch(IOException i)
            {

            }
            int image=R.drawable.sound;
            if("home".equals(id.toLowerCase()))
            {
                image=R.drawable.home;
            }
            if("hospital".equals(id.toLowerCase()))
            {
                image=R.drawable.hospital;
            }
            if("college".equals(id.toLowerCase())||"school".equals(id.toLowerCase()))
            {
                image=R.drawable.college;
            }
            if("office".equals(id.toLowerCase()))
            {
                image=R.drawable.office;
            }
            if("library".equals(id.toLowerCase()))
            {
                image=R.drawable.library;
            }
            final SavedZones zone = new SavedZones(latitude,longitude,id,zone_rad,Fulladdress,altitude,image);
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
    }

    public void openDialog() {
        KeyInputDialog keyInputDialog = new KeyInputDialog();
        keyInputDialog.setCancelable(false);
        keyInputDialog.show(getSupportFragmentManager(),"Add Name to zone ");
    }

    private void setUpLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            //request runtime permission
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            },MY_PERMISSION_REQUEST_CODE);
        }
        else
        {
            if(checkPlayServices())
            {
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();
            }
        }
    }

    private void displayLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation!=null)
        {
            final double latitude = mLastLocation.getLatitude();
            final double longitude = mLastLocation.getLongitude();
            textView.setText(String.format("your location is %f / %f ",latitude,longitude));
            Log.d("ADITYA",String.format("your location is %f / %f ",latitude,longitude));
            String Fulladdress="";
            try
            {
                addresses = geocoder.getFromLocation(latitude,longitude,1);
                Fulladdress = addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getLocality()+", "+addresses.get(0).getAdminArea();
                address.setText(Fulladdress);
            }
            catch(IOException i)
            {

            }
            // update to firebase
            geoFire.setLocation("you", new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    //Add marker
                    if(Mycurrent!=null)
                    {
                        Mycurrent.remove();//remove old marker
                    }
                    Mycurrent=mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title("you").icon(BitmapDescriptorFactory.fromResource(R.drawable.red)));
                    //move camera to this position
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),20.0f));
                }
            });


        }
        else
        {
            textView.setText("cannot get your location");
            Log.d("ADITYA","Cannot get your location");
        }
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FAST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
      //  mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

    }

    private void buildGoogleApiClient() {
        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }


    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultCode!= ConnectionResult.SUCCESS)
        {
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode,this,PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else
            {
                Toast.makeText(this,"This devide is not supported",Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
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


    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setTrafficEnabled(true);
        displayLocation();
        startLocationUpdates();

    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLastLocation = location;
                displayLocation();
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    public void setZones(GoogleMap mMap,final SavedZones zone)
    {   Circle mapCircle;
        final LatLng silent_zone = new LatLng(zone.latitude,zone.longitude);
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
             //   Toast.makeText(getApplicationContext(),""+mLastLocation.getAltitude(),Toast.LENGTH_SHORT).show();
              //  if(mLastLocation.getAltitude() == zone.getAltitude()) {
                    textView.setText(String.format("%s entered silent zone: "+zone.getName(), key));
                    myaudio = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
                    int currentRingerMode = myaudio.getRingerMode();
                    if (onoff == 1) {

                        if (currentRingerMode != AudioManager.RINGER_MODE_VIBRATE) {
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                            Toast.makeText(getApplicationContext(), " vibrate", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (onoff == 0) {
                        if (currentRingerMode != AudioManager.RINGER_MODE_NORMAL) {
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                            Toast.makeText(getApplicationContext(), "normal", Toast.LENGTH_SHORT).show();
                        }
                    }
               // }

            }

            @Override
            public void onKeyExited(String key) {
                // sendNotification("Aditya",String.format("%s exited the silent zone",key));
               // Toast.makeText(getApplicationContext(),""+mLastLocation.getAltitude(),Toast.LENGTH_SHORT).show();
              // if(mLastLocation.getAltitude()==zone.getAltitude()) {
                   textView.setText(String.format("%s exited silent zone: "+zone.getName(), key));
                   myaudio = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
                   int currentRingerMode = myaudio.getRingerMode();
                   if (currentRingerMode != AudioManager.RINGER_MODE_NORMAL) {
                       audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                       Toast.makeText(getApplicationContext(), "normal", Toast.LENGTH_SHORT).show();
                   }
             //  }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
               // if(mLastLocation.getAltitude()==zone.getAltitude()) {
                    textView.setText(String.format("%s moved within the silent zone:"+zone.getName()+ "-> [%f/%f]", key, location.latitude, location.longitude));
                    Log.d("MOVE", String.format("%s moved within the silent zone [%f/%f]", key, location.latitude, location.longitude));
              //  }
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
