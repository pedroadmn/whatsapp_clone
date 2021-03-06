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
import java.util.List;

import activities.ChatActivity;
import activities.GroupActivity;
import adapters.ContactAdapter;
import adapters.TalkAdapter;
import config.FirebaseConfig;
import helper.FirebaseUserHelper;
import helper.RecyclerItemClickListener;
import models.Talk;
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
                        List<User> updatedContactList = contactAdapter.getContactList();

                        User selectedContact = updatedContactList.get(position);

                        boolean isHeader = selectedContact.getEmail().isEmpty();

                        if (isHeader) {
                            Intent intent = new Intent(getActivity(), GroupActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(getActivity(), ChatActivity.class);
                            intent.putExtra("chatContact", selectedContact);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
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

                User groupItem = new User();
                groupItem.setName("New Group");
                groupItem.setEmail("");

                contactList.add(groupItem);

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

    public void searchContacts(String searchText) {
        List<User> searchUserList = new ArrayList<>();

        for (User user : contactList) {
            String name = user.getName().toLowerCase();
            if (name.contains(searchText)) {
                searchUserList.add(user);
            }
        }

        contactAdapter = new ContactAdapter(getActivity(), searchUserList);
        rvContacts.setAdapter(contactAdapter);
        contactAdapter.notifyDataSetChanged();
    }

    public void reloadTalks() {
        contactAdapter = new ContactAdapter(getActivity(), contactList);
        rvContacts.setAdapter(contactAdapter);
        contactAdapter.notifyDataSetChanged();
    }
}