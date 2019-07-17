package com.example.adityaabhiram.silenzapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Signup extends AppCompatActivity {
    private Button signup;
    private EditText textemail;
    private EditText textpassword;
    private TextView textsignin;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    public static Activity signupActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        signup=(Button)findViewById(R.id.signupbutton);
        textemail=(EditText)findViewById(R.id.emailid);
        textpassword=(EditText)findViewById(R.id.password);
        textsignin=(TextView)findViewById(R.id.signinlink);
        signupActivity = this;
        progressDialog=new ProgressDialog(this);
        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null)
        {   finish();
            startActivity(new Intent(this,ZONES.class));
        }
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                registerUser();
               // textemail.setText("hello");

            }
        });
        textsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //textemail.setText("hello");
                open_signin();

            }
        });

    }
    private void open_signin()
    {   finish();
        Intent in = new Intent(this,Signin.class);
        startActivity(in);
    }
    private void registerUser()
    {
        String email=textemail.getText().toString().trim();
        String password = textpassword.getText().toString().trim();
        if(TextUtils.isEmpty(email))
        {
            //email is empty
            Toast.makeText(this,"please enter email id",Toast.LENGTH_SHORT).show();
            //stop the function from executing further
            return;
        }
        if(TextUtils.isEmpty(password))
        {
            //password is empty
            Toast.makeText(this,"please enter password",Toast.LENGTH_SHORT).show();
            //stop the function from executing further
            return;
        }
        //valid inputs
        //show process bar
        progressDialog.setMessage("Signing-up!!!");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful())
                {
                    //successful sign-up
                    Toast.makeText(Signup.this,"Signed up - sucessfully :) ",Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(getApplicationContext(),ZONES.class));
                }
                else
                {
                    Toast.makeText(Signup.this,"Failed to sign-up :( .. try again",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
