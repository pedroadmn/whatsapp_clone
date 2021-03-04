package fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import activities.ChatActivity;
import adapters.ContactAdapter;
import config.FirebaseConfig;
import helper.FirebaseUserHelper;
import helper.RecyclerItemClickListener;
import models.User;
import pedroadmn.whatsappclone.com.R;

public class ContactFragment extends Fragment {

    private RecyclerView rvContacts;
    private ContactAdapter contactAdapter;
    private ArrayList<User> contactList = new ArrayList<>();

    private DatabaseReference userRef;

    private ValueEventListener valueEventListenerContacts;

    private FirebaseUser currentUser;

    public ContactFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        rvContacts = view.findViewById(R.id.rvContacts);
        userRef = FirebaseConfig.getFirebaseDatabase().child("users");
        currentUser = FirebaseUserHelper.getCurrentUser();

        contactAdapter = new ContactAdapter(getActivity(), contactList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvContacts.setLayoutManager(layoutManager);
        rvContacts.setHasFixedSize(true);
        rvContacts.setAdapter(contactAdapter);

        rvContacts.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                rvContacts,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getContacts();
    }

    @Override
    public void onStop() {
        super.onStop();
        userRef.removeEventListener(valueEventListenerContacts);
    }

    private void getContacts() {
        valueEventListenerContacts = userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contactList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    User user = data.getValue(User.class);
                    if (!currentUser.getEmail().equals(user.getEmail())) {
                        contactList.add(user);
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