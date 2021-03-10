package activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import adapters.SelectedGroupAdapter;
import config.FirebaseConfig;
import de.hdodenhof.circleimageview.CircleImageView;
import models.Group;
import models.User;
import pedroadmn.whatsappclone.com.R;

public class RegisterGroupActivity extends AppCompatActivity {

    private List<User> selectedMembersList = new ArrayList<>();
    private TextView tvTotalParticipants;
    private EditText etGroupName;
    private RecyclerView rvGroupMembers;
    private CircleImageView civGroupPhoto;

    private SelectedGroupAdapter selectedGroupAdapter;

    private static final int GALLERY_SELECTION = 200;

    private StorageReference storageReference;

    private Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_group);

        storageReference = FirebaseConfig.getFirebaseStorage();

        tvTotalParticipants = findViewById(R.id.tvTotalParticipants);
        etGroupName = findViewById(R.id.etGroupName);
        rvGroupMembers = findViewById(R.id.rvGroupMembers);
        civGroupPhoto = findViewById(R.id.civGroupPhoto);

        group = new Group();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("New Group");
        toolbar.setSubtitle("Define a name");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getExtras() != null) {
            List<User> members = (List<User>) getIntent().getExtras().getSerializable("selectedMembers");
            selectedMembersList.addAll(members);

            tvTotalParticipants.setText("Participants: " + selectedMembersList.size());
        }

        selectedGroupAdapter = new SelectedGroupAdapter(getApplicationContext(), selectedMembersList);

        RecyclerView.LayoutManager layoutManagerSelectedMember = new LinearLayoutManager(
                getApplicationContext(), LinearLayoutManager.HORIZONTAL, false
        );

        rvGroupMembers.setLayoutManager(layoutManagerSelectedMember);
        rvGroupMembers.setHasFixedSize(true);
        rvGroupMembers.setAdapter(selectedGroupAdapter);

        civGroupPhoto.setOnClickListener(v -> chooseGroupImage());
    }

    private void chooseGroupImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, GALLERY_SELECTION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap bitmap = null;
            try {
                switch (requestCode) {
                    case GALLERY_SELECTION:
                        Uri selectedImageLocal = data.getData();
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageLocal);
                        break;
                }

                if (bitmap != null) {
                    civGroupPhoto.setImageBitmap(bitmap);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] imageData = baos.toByteArray();

                    final StorageReference imageRef = storageReference
                            .child("images")
                            .child("groups")
                            .child(group.getId() + ".jpeg");

                    UploadTask uploadTask = imageRef.putBytes(imageData);
                    uploadTask
                            .addOnFailureListener(e -> Toast.makeText(RegisterGroupActivity.this, "Error on upload image", Toast.LENGTH_SHORT).show())
                            .addOnSuccessListener(taskSnapshot -> {
                                Toast.makeText(RegisterGroupActivity.this, "Photo Successfully uploaded", Toast.LENGTH_SHORT).show();
                                imageRef.getDownloadUrl().addOnCompleteListener(task -> {
                                    String url = task.getResult().toString();
                                    group.setPhoto(url);
                                });
                            });
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}