package activities;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import adapters.SelectedGroupAdapter;
import de.hdodenhof.circleimageview.CircleImageView;
import models.User;
import pedroadmn.whatsappclone.com.R;

public class RegisterGroupActivity extends AppCompatActivity {

    private List<User> selectedMembersList = new ArrayList<>();
    private TextView tvTotalParticipants;
    private EditText etGroupName;
    private RecyclerView rvGroupMembers;
    private CircleImageView civGroupPhoto;

    private SelectedGroupAdapter selectedGroupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_group);

        tvTotalParticipants = findViewById(R.id.tvTotalParticipants);
        etGroupName = findViewById(R.id.etGroupName);
        rvGroupMembers = findViewById(R.id.rvGroupMembers);
        civGroupPhoto = findViewById(R.id.civGroupPhoto);

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
    }
}