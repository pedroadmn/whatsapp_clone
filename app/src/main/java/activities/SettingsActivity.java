package activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import config.FirebaseConfig;
import helper.FirebaseUserHelper;
import helper.Permission;
import pedroadmn.whatsappclone.com.R;

public class SettingsActivity extends AppCompatActivity {

    private String[] permissions = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private ImageButton ibCamera;
    private ImageButton ibGallery;
    private ImageView civProfileImage;
    private EditText etProfileName;

    private static final int CAMERA_SELECTION = 100;
    private static final int GALLERY_SELECTION = 200;

    private StorageReference storageReference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storageReference = FirebaseConfig.getFirebaseStorage();
        userId = FirebaseUserHelper.getUserId();

        ibCamera = findViewById(R.id.ibCamera);
        ibGallery = findViewById(R.id.ibGallery);
        civProfileImage = findViewById(R.id.civProfileImage);
        etProfileName = findViewById(R.id.etProfileName);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);

        FirebaseUser user = FirebaseUserHelper.getCurrentUser();
        Uri photoUrl = user.getPhotoUrl();

        if (photoUrl == null) {
            civProfileImage.setImageResource(R.drawable.padrao);
        } else {
            Glide.with(this)
                    .load(photoUrl)
                    .into(civProfileImage);
        }

        etProfileName.setText(user.getDisplayName());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Permission.validatePermissions(permissions, this, 1);

        ibCamera.setOnClickListener(v -> openCamera());
        ibGallery.setOnClickListener(v -> openGallery());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int permissionResult : grantResults) {
            if (permissionResult == PackageManager.PERMISSION_DENIED) {
                warningPermissionValidation();
            }
        }
    }

    private void warningPermissionValidation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Denied Permissions");
        builder.setMessage("To use the app is necessary accept the permissions");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAMERA_SELECTION);
        }
    }

    private void openGallery() {
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
                    case CAMERA_SELECTION:
                        bitmap = (Bitmap) data.getExtras().get("data");
                        break;
                    case GALLERY_SELECTION:
                        Uri selectedImageLocal = data.getData();
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageLocal);
                        break;
                }

                if (bitmap != null) {
                    civProfileImage.setImageBitmap(bitmap);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] imageData = baos.toByteArray();

                    final StorageReference imageRef = storageReference
                            .child("images")
                            .child("perfil")
                            .child(userId + ".jpeg");

                    UploadTask uploadTask = imageRef.putBytes(imageData);
                    uploadTask.addOnFailureListener(e -> {
                        Toast.makeText(SettingsActivity.this, "Error on upload image", Toast.LENGTH_SHORT).show();
                    })
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(SettingsActivity.this, "Photo Successfully uploaded", Toast.LENGTH_SHORT).show();
                        imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                Uri url = task.getResult();
                                updateUserPhoto(url);
                            }
                        });
                    });
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private void updateUserPhoto(Uri url) {
        FirebaseUserHelper.updateUserPhoto(url);
    }
}