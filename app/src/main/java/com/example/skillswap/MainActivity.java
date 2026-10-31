package com.example.skillswap;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.skillswap.fragments.DiscoverFragment;
import com.example.skillswap.fragments.MatchesFragment;
import com.example.skillswap.fragments.MessagesFragment;
import com.example.skillswap.fragments.ProfileFragment;
import com.example.skillswap.fragments.SessionsFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class MainActivity extends AppCompatActivity {

    MaterialToolbar topToolbar;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up Top Toolbar
        topToolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(topToolbar); // Make it the ActionBar so menu shows

        // Set up Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(navListener);

        // Load default fragment (Discover)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new DiscoverFragment())
                    .commit();
        }
    }

    // Inflate the top menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    // Handle top menu clicks
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_filter) {
            showFilterBottomSheet();
            return true;
        } else if (itemId == R.id.menu_refresh) {
            Toast.makeText(this, "Refresh clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.menu_notifications) {
            Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.action_logout) {
            Toast.makeText(this, "Logout clicked", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Show Bottom Sheet for Filter
    private void showFilterBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.filter_bottom_sheet);

        CheckBox javaCheck = bottomSheetDialog.findViewById(R.id.filter_skill_java);
        CheckBox pythonCheck = bottomSheetDialog.findViewById(R.id.filter_skill_python);
        CheckBox designCheck = bottomSheetDialog.findViewById(R.id.filter_skill_design);
        Button applyButton = bottomSheetDialog.findViewById(R.id.apply_filter_button);

        if (applyButton != null) {
            applyButton.setOnClickListener(v -> {
                StringBuilder filters = new StringBuilder("Selected: ");
                if (javaCheck != null && javaCheck.isChecked()) filters.append("Java ");
                if (pythonCheck != null && pythonCheck.isChecked()) filters.append("Python ");
                if (designCheck != null && designCheck.isChecked()) filters.append("UI/UX ");

                Toast.makeText(this, filters.toString(), Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            });
        }

        bottomSheetDialog.show();
    }

    private final BottomNavigationView.OnItemSelectedListener navListener =
            new BottomNavigationView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    int itemId = item.getItemId();

                    if (itemId == R.id.nav_discover) {
                        selectedFragment = new DiscoverFragment();
                    } else if (itemId == R.id.nav_matches) {
                        selectedFragment = new MatchesFragment();
                    } else if (itemId == R.id.nav_sessions) {
                        selectedFragment = new SessionsFragment();
                    } else if (itemId == R.id.nav_messages) {
                        selectedFragment = new MessagesFragment();
                    } else if (itemId == R.id.nav_profile) {
                        selectedFragment = new ProfileFragment();
                    }

                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, selectedFragment)
                                .commit();
                    }
                    return true;
                }
            };
}
