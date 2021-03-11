package fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import activities.ChatActivity;
import adapters.ContactAdapter;
import adapters.TalkAdapter;
import config.FirebaseConfig;
import helper.FirebaseUserHelper;
import helper.RecyclerItemClickListener;
import models.Talk;
import models.User;
import pedroadmn.whatsappclone.com.R;

public class TalkFragment extends Fragment {

    private RecyclerView rvTalks;
    private TalkAdapter talkAdapter;
    private ArrayList<Talk> talkList = new ArrayList<>();

    private DatabaseReference databaseReference;
    private DatabaseReference talkRef;

    private ChildEventListener childEventListenerTalks;

    private FirebaseUser currentUser;

    public TalkFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_talk, container, false);

        rvTalks = view.findViewById(R.id.rvTalks);
        databaseReference = FirebaseConfig.getFirebaseDatabase();
        currentUser = FirebaseUserHelper.getCurrentUser();

        talkAdapter = new TalkAdapter(getActivity(), talkList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvTalks.setLayoutManager(layoutManager);
        rvTalks.setHasFixedSize(true);
        rvTalks.setAdapter(talkAdapter);

        String userId = FirebaseUserHelper.getUserId();

        talkRef = databaseReference.child("talks")
                .child(userId);

        rvTalks.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                rvTalks,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        List<Talk> updatedTalkList = talkAdapter.getTalks();
                        Talk selectedTalk = updatedTalkList.get(position);

                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        if ("true".equals(selectedTalk.getIsGroup())) {
                            intent.putExtra("chatGroup", selectedTalk.getGroup());
                        } else {
                            intent.putExtra("chatContact", selectedTalk.getUser());
                        }
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

    private void getTalks() {
        talkList.clear();

        childEventListenerTalks = talkRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Talk talk = snapshot.getValue(Talk.class);
                talkList.add(talk);
                talkAdapter.notifyDataSetChanged();
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
    public void onStart() {
        super.onStart();

        getTalks();
    }

    @Override
    public void onStop() {
        super.onStop();

        talkRef.removeEventListener(childEventListenerTalks);
    }

    public void searchTalks(String searchText) {
        List<Talk> searchTalkList = new ArrayList<>();

        for (Talk talk : talkList) {
            String name = "";
            String lastMessage = talk.getLastMessage().toLowerCase();
            if (talk.getUser() != null) {
                name = talk.getUser().getName().toLowerCase();
            } else {
                name = talk.getGroup().getName().toLowerCase();
            }

            if (name.contains(searchText) || lastMessage.contains(searchText)) {
                searchTalkList.add(talk);
            }
        }

        talkAdapter = new TalkAdapter(getActivity(), searchTalkList);
        rvTalks.setAdapter(talkAdapter);
        talkAdapter.notifyDataSetChanged();
    }

    public void reloadTalks() {
        talkAdapter = new TalkAdapter(getActivity(), talkList);
        rvTalks.setAdapter(talkAdapter);
        talkAdapter.notifyDataSetChanged();
    }
}