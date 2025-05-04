package com.example.manouba;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private Button navHome, navLocation, navTickets, navAccount;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
        // Initialize toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);  // Set the toolbar as the action bar

        // Initialize navigation buttons
        navHome = findViewById(R.id.nav_home);
        navLocation = findViewById(R.id.nav_location);
        navTickets = findViewById(R.id.nav_tickets);
        navAccount = findViewById(R.id.nav_account);

        // Set up navigation button listeners
        navHome.setOnClickListener(v -> {
            loadFragment(new HomeFragment(), "Tunisian Railways");
        });

        navLocation.setOnClickListener(v -> {
            loadFragment(new LocationFragment(), "Location");
        });

        navTickets.setOnClickListener(v -> {
            loadFragment(new TicketFragment(), "Tickets");
        });

        navAccount.setOnClickListener(v -> {
            loadFragment(new AccountFragment(), "Account");
        });

        // Load HomeFragment by default
        loadFragment(new HomeFragment(), "Tunisian Railways");
    }

    private void loadFragment(Fragment fragment, String title) {
        // Update the toolbar title based on the selected fragment
        getSupportActionBar().setTitle(title);

        // Begin a fragment transaction
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
