package activities;

import android.content.Intent;
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
import android.widget.AdapterView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import adapters.ContactAdapter;
import adapters.SelectedGroupAdapter;
import config.FirebaseConfig;
import helper.FirebaseUserHelper;
import helper.RecyclerItemClickListener;
import models.User;
import pedroadmn.whatsappclone.com.R;

public class GroupActivity extends AppCompatActivity {

    private RecyclerView rvSelectedMembers;
    private RecyclerView rvMembers;

    private ContactAdapter contactAdapter;
    private SelectedGroupAdapter selectedGroupAdapter;
    private List<User> memberList = new ArrayList<>();
    private List<User> selectedMembersList = new ArrayList<>();

    private ValueEventListener valueEventListenerMembers;
    private DatabaseReference userRef;

    private FirebaseUser currentUser;

    private Toolbar toolbar;

    private FloatingActionButton fabGoRegisterGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        userRef = FirebaseConfig.getFirebaseDatabase().child("users");
        currentUser = FirebaseUserHelper.getCurrentUser();

        rvSelectedMembers = findViewById(R.id.rvSelectedMembers);
        rvMembers = findViewById(R.id.rvMembers);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("New Group");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fabGoRegisterGroup = findViewById(R.id.fabGoRegisterGroup);
        fabGoRegisterGroup.setOnClickListener(view -> goToRegisterGroup());

        contactAdapter = new ContactAdapter(getApplicationContext(), memberList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvMembers.setLayoutManager(layoutManager);
        rvMembers.setHasFixedSize(true);
        rvMembers.setAdapter(contactAdapter);

        rvMembers.addOnItemTouchListener(new RecyclerItemClickListener(
                GroupActivity.this,
                rvMembers,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        User selectedMember = memberList.get(position);

                        memberList.remove(selectedMember);
                        contactAdapter.notifyDataSetChanged();

                        rvSelectedMembers.setVisibility(View.VISIBLE);
                        selectedMembersList.add(selectedMember);

                        selectedGroupAdapter.notifyDataSetChanged();

                        updateToolbarMembers();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));

        selectedGroupAdapter = new SelectedGroupAdapter(getApplicationContext(), selectedMembersList);

        RecyclerView.LayoutManager layoutManagerSelectedMember = new LinearLayoutManager(
                getApplicationContext(), LinearLayoutManager.HORIZONTAL, false
        );

        rvSelectedMembers.setLayoutManager(layoutManagerSelectedMember);
        rvSelectedMembers.setHasFixedSize(true);
        rvSelectedMembers.setAdapter(selectedGroupAdapter);

        rvSelectedMembers.addOnItemTouchListener(new RecyclerItemClickListener(
                GroupActivity.this,
                rvSelectedMembers,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        User selectedUser = selectedMembersList.get(position);

                        selectedMembersList.remove(selectedUser);
                        selectedGroupAdapter.notifyItemRemoved(position);

                        if (selectedMembersList.isEmpty()) {
                            rvSelectedMembers.setVisibility(View.GONE);
                        }

                        memberList.add(selectedUser);
                        contactAdapter.notifyDataSetChanged();

                        updateToolbarMembers();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));

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
                updateToolbarMembers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateToolbarMembers() {
        int totalSelected = selectedMembersList.size();
        int total = memberList.size() + totalSelected;
        toolbar.setSubtitle(totalSelected + " de " + total + " selected");
    }

    private void goToRegisterGroup() {
        Intent intent = new Intent(this, RegisterGroupActivity.class);
        intent.putExtra("selectedMembers", (Serializable) selectedMembersList);
        startActivity(intent);
    }
}