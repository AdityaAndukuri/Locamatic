package com.example.adityaabhiram.silenzapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TabOne extends Fragment {
    private FirebaseAuth firebaseAuth;
    DatabaseReference ref;
    ListView listViewaudiozones;
    List<SavedZones> zonesList;
    String input;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_one,container,false);
        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null)
        {
            getActivity().finish();
            startActivity(new Intent(getActivity(),Signin.class));
        }
        FirebaseUser user = firebaseAuth.getCurrentUser();
        Intent in = getActivity().getIntent();
        input = in.getStringExtra("type");
        ref=FirebaseDatabase.getInstance().getReference(user.getUid()+"/"+input);
        listViewaudiozones=(ListView)view.findViewById(R.id.ListViewAudioZones);
        zonesList = new ArrayList<>();
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                zonesList.clear();
                for(DataSnapshot audiozoneSnapShot: dataSnapshot.getChildren()){
                    SavedZones zone = audiozoneSnapShot.getValue(SavedZones.class);
                    if(zone.getOnoff()==1)
                      zonesList.add(zone);
                }
                AudioZonesList adapter = new AudioZonesList(getActivity(),zonesList);
                listViewaudiozones.setAdapter(adapter);
                listViewaudiozones.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i,long l) {
                        Intent in = new Intent(getActivity(),ZoneInfo.class);
                        //startActivity(new Intent(getApplicationContext(),ZoneInfo.class));
                        in.putExtra("name",zonesList.get(i).name);
                        in.putExtra("latitude",zonesList.get(i).latitude);
                        in.putExtra("longitude",zonesList.get(i).longitude);
                        in.putExtra("onoff",zonesList.get(i).onoff);
                        in.putExtra("radius",zonesList.get(i).radius);
                        in.putExtra("type",input);
                        in.putExtra("add",zonesList.get(i).getAddress());
                        in.putExtra("alt",zonesList.get(i).getAltitude());
                        in.putExtra("image",zonesList.get(i).image);
                        startActivity(in);

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),"DB error",Toast.LENGTH_SHORT).show();
            }
        });
    }

}
