package com.cellfishpool.app.powwow;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    ImageButton send;
    EditText msg;

    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    List<Chat> mChat;

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

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


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
                //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                //getSupportActionBar().
                readMessage(me,other);

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

    private void readMessage(String sender,String receiver){

        mChat=new ArrayList<>();

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mChat.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){

                    HashMap hm=(HashMap) snapshot.getValue();

                    String s=hm.get("sender").toString();
                    String r=hm.get("receiver").toString();
                    String m=hm.get("message").toString();

                    Chat chat=new Chat(s,r,m);

                    if (chat.getReceiver().equals(sender) && chat.getSender().equals(receiver) ||
                            chat.getSender().equals(sender) && chat.getReceiver().equals(receiver)){
                        mChat.add(chat);
                    }

                    messageAdapter=new MessageAdapter(ChatActivity.this,mChat);
                    recyclerView.setAdapter(messageAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
