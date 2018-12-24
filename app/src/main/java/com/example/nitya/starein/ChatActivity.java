package com.example.nitya.starein;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    ImageButton send;
    EditText msg;

    RecyclerView list;

    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent=getIntent();
        String me=intent.getStringExtra("me");
        String other=intent.getStringExtra("other");

        send=findViewById(R.id.btnSend);
        msg=findViewById(R.id.sendText);
        list=findViewById(R.id.recyclerView);

        ref=FirebaseDatabase.getInstance().getReference("Users").child(other);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                HashMap user=(HashMap) dataSnapshot.getValue();
                Log.i("username",user.get("username").toString());
                String name=user.get("username").toString();
                Toolbar toolbar=findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                getSupportActionBar().setTitle(name);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text=msg.getText().toString();

                if (!text.equals("")){

                    sendMessage(me,other,text);

                }
                else {
                    Toast.makeText(ChatActivity.this,"Please type Something!",Toast.LENGTH_LONG).show();
                }
                msg.setText("");

            }
        });


    }

    private void sendMessage(String sender,String receiver,String message){

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);

        ref.child("Chats").push().setValue(hashMap);
    }
}
