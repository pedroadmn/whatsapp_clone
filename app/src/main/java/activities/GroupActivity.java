package activities;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

import adapters.ContactAdapter;
import config.FirebaseConfig;
import helper.FirebaseUserHelper;
import models.User;
import pedroadmn.whatsappclone.com.R;

public class GroupActivity extends AppCompatActivity {

    private RecyclerView rvSelectedMembers;
    private RecyclerView rvMembers;

    private ContactAdapter contactAdapter;
    private List<User> memberList = new ArrayList<>();

    private ValueEventListener valueEventListenerMembers;
    private DatabaseReference userRef;

    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        userRef = FirebaseConfig.getFirebaseDatabase().child("users");
        currentUser = FirebaseUserHelper.getCurrentUser();

        rvSelectedMembers = findViewById(R.id.rvSelectedMembers);
        rvMembers = findViewById(R.id.rvMembers);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Group");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        contactAdapter = new ContactAdapter(getApplicationContext(), memberList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvMembers.setLayoutManager(layoutManager);
        rvMembers.setHasFixedSize(true);
        rvMembers.setAdapter(contactAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        getMembers();
    }

    @Override
    protected void onStop() {
        super.onStop();

        userRef.removeEventListener(valueEventListenerMembers);
    }

    private void getMembers() {
        valueEventListenerMembers = userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                memberList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    User user = data.getValue(User.class);
                    if (!currentUser.getEmail().equals(user.getEmail())) {
                        memberList.add(user);
                    }
                }
                contactAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}