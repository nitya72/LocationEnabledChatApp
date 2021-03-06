package com.cellfishpool.app.powwow;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.rengwuxian.materialedittext.MaterialEditText;

public class RegisterActivity extends AppCompatActivity {

    MaterialEditText user,email,password;
    Button btnRegister;

    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user=findViewById(R.id.username);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        btnRegister=findViewById(R.id.btnRegister);

        auth=FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String txtUsername=user.getText().toString();
                String txtEmail=email.getText().toString();
                String txtPassword=password.getText().toString();

                if(txtUsername.equals("") || txtEmail.equals("") || txtPassword.equals("")){

                    Toast.makeText(RegisterActivity.this, "All fields should be filled", Toast.LENGTH_SHORT).show();
                }
                else {
                    register(txtUsername,txtEmail,txtPassword);
                }
            }
        });
    }

    private void register(String user,String email,String password){

        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            FirebaseUser firebaseUser=auth.getCurrentUser();

                            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                        Log.d("Email","Email sent");
                                }
                            });
                            String userId=firebaseUser.getUid();

                            reference=FirebaseDatabase.getInstance().getReference("Users").child(userId);
                            LatLng latLng=new LatLng(25.3111, 82.9864);

                            Component component=new Component(userId,user,latLng);

                            reference.setValue(component).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                        else{
                            Toast.makeText(RegisterActivity.this,String.valueOf(task.getException()),Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


}

