package example.com.groupeasy.activities;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import example.com.groupeasy.R;
import example.com.groupeasy.adapters.MessageAdapter;
import example.com.groupeasy.pojo.chatMessage;

public class chatRoomActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private String room_name;
    private TextView roomName;
    private ListView listView;
    private ImageView sendButton;
    private EditText messageContent;
    private ConstraintLayout myEventsFrame;

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatroom_main);

        initElementWithIds();
        initElementsWithListeners();

        showAllOldMessages();

        //snippet to stop keyboard from appearing onCreate
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // get room name from last intent and override the chatroom title
        room_name = getIntent().getExtras().get("room_name").toString();

        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle(room_name);
        roomName.setText(room_name);
        roomName.setVisibility(View.VISIBLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Messages").child("");

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Members").child(current_uid);

       }

    public void initElementsWithListeners() {

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


// if entered string is null a tost will prompt
                if(messageContent.getText().toString().isEmpty()){
                    Toast.makeText(chatRoomActivity.this, "You have entered nothing", Toast.LENGTH_SHORT).show();
                }
// else the entered string will be pushed to the firebase database reference
                else {
                    mUserDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String name = dataSnapshot.child("member").getValue().toString();

                            String key = room_name;
                            Map<String,Object> value = new HashMap<>();
                            value.put("content",messageContent.getText().toString());
                            value.put("name",name);
                            value.put("group",room_name);

                            FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child("messages")
//                                    .child("")
                                    .push()
                                    .setValue(new chatMessage(messageContent.getText().toString(),
                                            name,
                                            room_name));

                                            /*messageContent.getText().toString(),
                                            FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                                            FirebaseAuth.getInstance().getCurrentUser().getUid()
                                            */

                            messageContent.setText("");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        myEventsFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(chatRoomActivity.this, "Cicked",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(chatRoomActivity.this,lists_activity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private String loggedInUserName = "";

    private void showAllOldMessages() {

        loggedInUserName = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("Main", "user id: " + loggedInUserName);

         adapter = new MessageAdapter(this, chatMessage.class, R.layout.item_in_message,
                FirebaseDatabase.getInstance().getReference().child("messages").child(""));
        listView.setAdapter(adapter);

    }

    private void initElementWithIds() {

        mToolBar = (Toolbar) findViewById(R.id.toolbar_chat);
        roomName = (TextView) findViewById(R.id.room_name);
        listView = (ListView) findViewById(R.id.list);
        sendButton = (ImageView) findViewById(R.id.send_button);
        messageContent = (EditText) findViewById(R.id.message_content);

        myEventsFrame = (ConstraintLayout) findViewById(R.id.active_events);
    }

    public String getLoggedInUserName() {
        return loggedInUserName;
    }


}


