package com.example.adityaabhiram.silenzapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AUDIO_ZONES_LIST extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    DatabaseReference ref;
    ListView listViewaudiozones;
    List<SavedZones> zonesList;
    String input;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio__zones__list);
        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null)
        {
            finish();
            startActivity(new Intent(this,Signin.class));
        }
        FirebaseUser user = firebaseAuth.getCurrentUser();
        Intent in = getIntent();
        input = in.getStringExtra("type");
        ref=FirebaseDatabase.getInstance().getReference(user.getUid()+"/"+input);
        listViewaudiozones=(ListView)findViewById(R.id.ListViewAudioZones);
        zonesList = new ArrayList<>();

    }

    @Override
    protected void onStart() {
        super.onStart();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                zonesList.clear();
                for(DataSnapshot audiozoneSnapShot: dataSnapshot.getChildren()){
                    SavedZones zone = audiozoneSnapShot.getValue(SavedZones.class);
                    zonesList.add(zone);
                }
                AudioZonesList adapter = new AudioZonesList(AUDIO_ZONES_LIST.this,zonesList);
                listViewaudiozones.setAdapter(adapter);
                listViewaudiozones.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i,long l) {
                        Intent in = new Intent(getApplicationContext(),ZoneInfo.class);
                        //startActivity(new Intent(getApplicationContext(),ZoneInfo.class));
                        in.putExtra("name",zonesList.get(i).name);
                        in.putExtra("latitude",zonesList.get(i).latitude);
                        in.putExtra("longitude",zonesList.get(i).longitude);
                        in.putExtra("onoff",zonesList.get(i).onoff);
                        in.putExtra("radius",zonesList.get(i).radius);
                        in.putExtra("type",input);
                        startActivity(in);

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
