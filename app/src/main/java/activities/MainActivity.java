package activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import config.FirebaseConfig;
import fragments.ContactFragment;
import fragments.TalkFragment;
import pedroadmn.whatsappclone.com.R;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseConfig.getFirebaseAuth();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Whatsapp Clone");
        setSupportActionBar(toolbar);

        FragmentPagerItemAdapter fragmentAdapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                .add("Talks", TalkFragment.class)
                .add("Contacts", ContactFragment.class)
                .create()
        );

        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(fragmentAdapter);

        SmartTabLayout smartTabLayout = findViewById(R.id.smartTabLayout);
        smartTabLayout.setViewPager(viewPager);

        searchView = findViewById(R.id.svMain);

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                TalkFragment talkFragment = (TalkFragment) fragmentAdapter.getPage(0);
                talkFragment.reloadTalks();
            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                switch (viewPager.getCurrentItem()) {
                    case 0:
                        TalkFragment talkFragment = (TalkFragment) fragmentAdapter.getPage(0);
                        if (newText != null && !newText.isEmpty()) {
                            talkFragment.searchTalks(newText.toLowerCase());
                        } else {
                            talkFragment.reloadTalks();
                        }
                        break;
                    case 1:
                        ContactFragment contactFragment = (ContactFragment) fragmentAdapter.getPage(1);
                        if (newText != null && !newText.isEmpty()) {
                            contactFragment.searchContacts(newText.toLowerCase());
                        } else {
                            contactFragment.reloadTalks();
                        }
                        break;
                }

                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.searchMenu);
        searchView.setMenuItem(searchItem);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logoutMenu:
                logout();
                finish();
                break;
            case R.id.configMenu:
                goToSettings();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        try {
            firebaseAuth.signOut();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void goToSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }
}