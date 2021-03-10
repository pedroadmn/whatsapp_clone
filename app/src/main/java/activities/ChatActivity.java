package activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import adapters.ChatAdapter;
import config.FirebaseConfig;
import de.hdodenhof.circleimageview.CircleImageView;
import helper.Base64Custom;
import helper.FirebaseUserHelper;
import models.Group;
import models.Message;
import models.Talk;
import models.User;
import pedroadmn.whatsappclone.com.R;

public class ChatActivity extends AppCompatActivity {

    private TextView tvChatContactName;
    private CircleImageView civPhotoChat;
    private ImageView ivMessagePhoto;
    private User recipientUser;
    private EditText etMessage;
    private FloatingActionButton fabSendMessage;

    private RecyclerView rvChatMessage;
    private ChatAdapter chatAdapter;
    private List<Message> messages = new ArrayList<>();
    private static final int CAMERA_SELECTION = 100;

    private String senderUserId;
    private String recipientUserId;
    private Group group;

    private DatabaseReference database;
    private DatabaseReference messageRef;
    private ChildEventListener childEventListenerMessages;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        tvChatContactName = findViewById(R.id.tvChatContactName);
        civPhotoChat = findViewById(R.id.civPhotoChat);
        etMessage = findViewById(R.id.etMessage);

        ivMessagePhoto = findViewById(R.id.ivMessagePhoto);
        ivMessagePhoto.setOnClickListener(v -> sendPhoto());

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

            if (bundle.containsKey("chatGroup")) {
                group = (Group) bundle.getSerializable("chatGroup");
                recipientUserId = group.getId();
                tvChatContactName.setText(group.getName());

                String photo = group.getPhoto();
                if (photo != null) {
                    Uri url = Uri.parse(photo);

                    Glide.with(this)
                            .load(url)
                            .into(civPhotoChat);
                } else {
                    civPhotoChat.setImageResource(R.drawable.padrao);
                }
            } else {
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

        chatAdapter = new ChatAdapter(this, messages);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvChatMessage.setLayoutManager(layoutManager);
        rvChatMessage.setHasFixedSize(true);
        rvChatMessage.setAdapter(chatAdapter);

        database = FirebaseConfig.getFirebaseDatabase();
        storageRef = FirebaseConfig.getFirebaseStorage();

        messageRef = database.child("messages")
                .child(senderUserId)
                .child(recipientUserId);
    }

    private void sendMessage() {
        String textMessage = etMessage.getText().toString();

        if (!textMessage.isEmpty()) {
            if (recipientUser != null) {
                Message message = new Message();
                message.setUserId(senderUserId);
                message.setMessage(textMessage);

                // Save message to sender on firebase
                saveMessage(senderUserId, recipientUserId, message);

                // Save message to recipient on firebase
                saveMessage(recipientUserId, senderUserId, message);

                saveTalk(message, false);
            } else {
                for (User groupMember : group.getMembers()) {
                    String senderGroupId = Base64Custom.encodeBase64(groupMember.getEmail());
                    String groupLoggedUserId = FirebaseUserHelper.getUserId();

                    Message message = new Message();
                    message.setUserId(groupLoggedUserId);
                    message.setMessage(textMessage);

                    // Save message to sender on firebase
                    saveMessage(senderGroupId, recipientUserId, message);

                    saveTalk(message, true);
                }
            }
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

    private void sendPhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAMERA_SELECTION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap bitmap = null;

            try {
                switch (requestCode) {
                    case CAMERA_SELECTION:
                        bitmap = (Bitmap) data.getExtras().get("data");
                        break;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            if (bitmap != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] imageData = baos.toByteArray();


                String imageName = UUID.randomUUID().toString();

                final StorageReference imageRef = storageRef
                        .child("images")
                        .child("photos")
                        .child(senderUserId)
                        .child(imageName + ".jpeg");

                UploadTask uploadTask = imageRef.putBytes(imageData);
                uploadTask
                        .addOnFailureListener(e -> {
                            Toast.makeText(ChatActivity.this, "Error on upload image", Toast.LENGTH_SHORT).show();
                        })
                        .addOnSuccessListener(taskSnapshot -> {
                            Toast.makeText(ChatActivity.this, "Photo Successfully uploaded", Toast.LENGTH_SHORT).show();
                            imageRef.getDownloadUrl().addOnCompleteListener(task -> {
                                Uri url = task.getResult();
                                Message message = new Message();
                                message.setUserId(senderUserId);
                                message.setMessage("image.jpeg");
                                message.setImage(url.toString());

                                saveMessage(senderUserId, recipientUserId, message);
                                saveMessage(recipientUserId, senderUserId, message);
                            });
                        });
            }
        }
    }

    private void saveTalk(Message message, boolean isGroup) {
        Talk talk = new Talk();
        talk.setSenderId(senderUserId);
        talk.setRecipientId(recipientUserId);
        talk.setLastMessage(message.getMessage());

        if (isGroup) {
            talk.setGroup(group);
            talk.setIsGroup("true");
        } else {

            talk.setUser(recipientUser);
            talk.setIsGroup("false");

        }
        talk.save();
    }
}