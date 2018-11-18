package com.example.mthompson.chattychitchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Date;

public class ChatRoomActivity extends AppCompatActivity {
    private String chatName;
    private FirebaseListAdapter<ChatMessage> messageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        Bundle extras = getIntent().getExtras();
        chatName =  extras.getString("chatName");
        displayChatMessages();
    }

    private void displayChatMessages()
    {
        ListView listOfMessages = (ListView)findViewById(R.id.list_of_messages);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("ChatMessages").orderByChild("chatName").equalTo(chatName);
        messageAdapter = new FirebaseListAdapter<ChatMessage>(this,ChatMessage.class,R.layout.message,query) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                if(model.chatName.equals(chatName))
                {
                    TextView messageText = v.findViewById(R.id.message_text);
                    TextView messageUser = v.findViewById(R.id.message_user);
                    TextView messageTime = v.findViewById(R.id.message_time);

                    // Set their text
                    messageText.setText(model.messageText);
                    messageUser.setText(model.username);

                    // Format the date before showing it
                    messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.messageTime));
                }

            }
        };

        listOfMessages.setAdapter(messageAdapter);
    }

    public void addNewMessage(View view)
    {
        EditText input = findViewById(R.id.messageInput);
        if(input == null)
        {
            Toast.makeText(this,"message must contain some words",Toast.LENGTH_LONG).show();
        }
        else
        {
            String messageText = input.getText().toString();
            String username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

            FirebaseDatabase.getInstance().getReference().child("ChatMessages").push().setValue(new ChatMessage(chatName,messageText,username,new Date().getTime()));

            input.setText("");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == R.id.menu_sign_out)
        {
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    Toast.makeText(ChatRoomActivity.this, "You have been signed out.", Toast.LENGTH_LONG).show();
                    // Close activity
                    finish();
                }
            });
        }
        if(item.getItemId() == R.id.chat_rooms)
        {
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }
        return true;
    }
}
