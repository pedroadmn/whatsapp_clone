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
import android.widget.ImageButton;
import android.widget.ImageView;

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

    private static final int CAMERA_SELECTION = 100;
    private static final int GALLERY_SELECTION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ibCamera = findViewById(R.id.ibCamera);
        ibGallery = findViewById(R.id.ibGallery);
        civProfileImage = findViewById(R.id.civProfileImage);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);

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
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            if (requestCode == CAMERA_SELECTION) {

            } else if (requestCode == GALLERY_SELECTION) {

            }
        }
    }
}