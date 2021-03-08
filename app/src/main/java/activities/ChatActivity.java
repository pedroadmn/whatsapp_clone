package activities;

import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import models.User;
import pedroadmn.whatsappclone.com.R;

public class ChatActivity extends AppCompatActivity {

    private TextView tvChatContactName;
    private CircleImageView civPhotoChat;
    private User recipientUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        tvChatContactName = findViewById(R.id.tvChatContactName);
        civPhotoChat = findViewById(R.id.civPhotoChat);

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
        }
    }
}