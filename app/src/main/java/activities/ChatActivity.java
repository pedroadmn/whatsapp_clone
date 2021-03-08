package activities;

import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    private String senderUserId;
    private String recipientUserId;

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
}