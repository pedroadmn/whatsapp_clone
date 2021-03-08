package activities;

import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import adapters.ChatAdapter;
import config.FirebaseConfig;
import de.hdodenhof.circleimageview.CircleImageView;
import helper.Base64Custom;
import helper.FirebaseUserHelper;
import models.Message;
import models.User;
import pedroadmn.whatsappclone.com.R;

public class ChatActivity extends AppCompatActivity {

    private TextView tvChatContactName;
    private CircleImageView civPhotoChat;
    private User recipientUser;
    private EditText etMessage;
    private FloatingActionButton fabSendMessage;

    private RecyclerView rvChatMessage;
    private ChatAdapter chatAdapter;
    private List<Message> messages = new ArrayList<>();

    private String senderUserId;
    private String recipientUserId;

    private DatabaseReference database;
    private DatabaseReference messageRef;
    private ChildEventListener childEventListenerMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        tvChatContactName = findViewById(R.id.tvChatContactName);
        civPhotoChat = findViewById(R.id.civPhotoChat);
        etMessage = findViewById(R.id.etMessage);


        fabSendMessage = findViewById(R.id.fabSendMessage);
        fabSendMessage.setOnClickListener(v -> sendMessage());

        rvChatMessage = findViewById(R.id.rvChatMessages);

        senderUserId = FirebaseUserHelper.getUserId();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            recipientUser = (User) bundle.getSerializable("chatContact");

            tvChatContactName.setText(recipientUser.getName());

            String photo = recipientUser.getPhoto();

            if (photo != null) {
                Uri url = Uri.parse(photo);

                Glide.with(this)
                        .load(url)
                        .into(civPhotoChat);
            } else {
                civPhotoChat.setImageResource(R.drawable.padrao);
            }

            recipientUserId = Base64Custom.encodeBase64(recipientUser.getEmail());
        }

        chatAdapter = new ChatAdapter(this, messages);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvChatMessage.setLayoutManager(layoutManager);
        rvChatMessage.setHasFixedSize(true);
        rvChatMessage.setAdapter(chatAdapter);

        database = FirebaseConfig.getFirebaseDatabase();
        messageRef = database.child("messages")
                .child(senderUserId)
                .child(recipientUserId);
    }

    private void sendMessage() {
        String textMessage = etMessage.getText().toString();

        if (!textMessage.isEmpty()) {
            Message message = new Message();
            message.setUserId(senderUserId);
            message.setMessage(textMessage);

            saveMessage(senderUserId, recipientUserId, message);
        } else {
            Toast.makeText(this, "Type a message to send.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveMessage(String senderId, String recipientId, Message message) {
        DatabaseReference database = FirebaseConfig.getFirebaseDatabase();
        DatabaseReference messageRef = database.child("messages");

        messageRef
                .child(senderId)
                .child(recipientId)
                .push()
                .setValue(message);

        etMessage.setText("");
    }

    private void getMessages() {
        childEventListenerMessages = messageRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                messages.add(message);
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getMessages();
    }

    @Override
    protected void onStop() {
        super.onStop();
        messageRef.removeEventListener(childEventListenerMessages);
    }
}