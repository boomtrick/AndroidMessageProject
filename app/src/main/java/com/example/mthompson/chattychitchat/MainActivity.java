package com.example.mthompson.chattychitchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final int SIGN_IN_REQUEST_CODE = 9001;
    private FirebaseListAdapter<Chat> chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null)
        {
            // Start sign in/sign up activity
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
        }
        else {
            // User is already signed in. Therefore
            // Load chat rooms
            displayChatRooms();

        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_REQUEST_CODE)
        {
            if(resultCode == RESULT_OK)
            {
                Toast.makeText(this, "Successfully signed in. Welcome!", Toast.LENGTH_LONG).show();
                displayChatRooms();
            }
            else {
                Toast.makeText(this,
                        "We couldn't sign you in. Please try again later.",
                        Toast.LENGTH_LONG)
                        .show();

                // Close the app
                finish();
            }
        }
    }
    
    private void displayChatRooms()
    {
        ListView listOfChats = findViewById(R.id.list_of_chats);


        chatAdapter = new FirebaseListAdapter<Chat>(this,Chat.class,R.layout.chat,FirebaseDatabase.getInstance().getReference("Chats")) {
            @Override
            protected void populateView(View v, Chat model, int position) {
                Button chatname = (Button)v.findViewById(R.id.chat_name);
                chatname.setText(model.name);
            }
        };

        listOfChats.setAdapter(chatAdapter);

    }

    public void AddNewChat(View view)
    {
        EditText input = (EditText) findViewById(R.id.input);

        if(input.getText().length() == 0)
        {
            Toast.makeText(this,"chat must have a name",Toast.LENGTH_LONG).show();
        }
        else
        {
            FirebaseDatabase.getInstance().getReference().child("Chats").push().setValue(new Chat(input.getText().toString()));
            input.setText("");
        }
    }

    public void ChatClicked(View view)
    {
        Intent intent = new Intent(this,ChatRoomActivity.class);
        Button chatname = (Button)view.findViewById(R.id.chat_name);
        intent.putExtra("chatName",chatname.getText().toString());
        startActivity(intent);
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
                    Toast.makeText(MainActivity.this, "You have been signed out.", Toast.LENGTH_LONG).show();
                    // Close activity
                    finish();
                }
            });
        }
        if(item.getItemId() == R.id.chat_rooms)
        {
            Toast.makeText(MainActivity.this, "You are already in chat rooms menu", Toast.LENGTH_LONG).show();
        }
        return true;
    }

}
