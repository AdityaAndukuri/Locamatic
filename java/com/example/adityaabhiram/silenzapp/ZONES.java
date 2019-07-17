package com.example.adityaabhiram.silenzapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class ZONES extends AppCompatActivity {
    private ImageButton int_zone;
    private ImageButton aud_zone;
    private FirebaseAuth firebaseAuth;
    private TextView welcome_text;
    private Button signout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zones);
        int_zone = (ImageButton) findViewById(R.id.internet);
        aud_zone = (ImageButton) findViewById(R.id.audio);
        signout=(Button)findViewById(R.id.sign_out);
        welcome_text=(TextView)findViewById(R.id.welcome_text);
        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null)
        {
            finish();
            startActivity(new Intent(this,Signin.class));
        }
        FirebaseUser user = firebaseAuth.getCurrentUser();
        welcome_text.setText("Welcome  " + user.getEmail());
        int_zone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_internet_zones();
            }
        });
        aud_zone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_audio_zones();
            }
        });
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(getApplicationContext(),Signin.class));
            }
        });
    }

    public void open_internet_zones() {
     // Intent in = new Intent(this,INTERNET_ZONES.class);
     // startActivity(in);
        Intent in = new Intent(this,List_tabbed.class);
        in.putExtra("type","WifiZones");
        startActivity(in);

    }
    public void open_audio_zones()
    {
       // Intent in = new Intent(this,AUDIO_ZONES.class);
        //startActivity(in);
        Intent in = new Intent(this,List_tabbed.class);
        in.putExtra("type","AudioZones");
        startActivity(in);
    }
}

