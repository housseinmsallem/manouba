package com.example.manouba;

import android.Manifest;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TrainDatabaseHelper databaseHelper;
    private FusedLocationProviderClient fusedLocationClient;
    private List<Train> trainList;
    private Button navHome, navLocation, navTickets, navAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize navigation buttons
        navHome = findViewById(R.id.nav_home);
        navLocation = findViewById(R.id.nav_location);
        navTickets = findViewById(R.id.nav_tickets);
        navAccount = findViewById(R.id.nav_account);

        // Set up navigation button listeners
        navHome.setOnClickListener(v -> {
            showHomeContent();
            Toast.makeText(MainActivity.this, "Home page loaded", Toast.LENGTH_SHORT).show();
        });

        navLocation.setOnClickListener(v -> loadFragment(new LocationFragment()));
        navTickets.setOnClickListener(v -> loadFragment(new TicketFragment()));
        navAccount.setOnClickListener(v -> loadFragment(new AccountFragment()));

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Request location permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            getLocationAndShowTrain();
        }

        // Handle system window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainTrainCard), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Load train data
        databaseHelper = new TrainDatabaseHelper(this);
        trainList = databaseHelper.getAllTrains();

        // Show home content by default
        showHomeContent();
    }

    private void showHomeContent() {
        // Make the train info views visible
        findViewById(R.id.trainInfoCard).setVisibility(View.VISIBLE);
        // Hide any fragments that might be visible
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new Fragment())
                .commit();
    }

    private void loadFragment(Fragment fragment) {
        // Hide the train info views when showing fragments
        findViewById(R.id.trainInfoCard).setVisibility(View.GONE);
        findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null) // Optional: allows back button navigation
                .commit();
    }

    private void getLocationAndShowTrain() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return; // Permissions not granted
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        String nearestStation = getNearestStation(location.getLatitude(), location.getLongitude());
                        Map<String, Train> nextTrain = getNextTrainsFromNearestStation(location.getLatitude(), location.getLongitude());
                        updateUI(nearestStation, nextTrain);
                        Log.d("LOCATION_DEBUG", "Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude());
                    } else {
                        updateUI("Unknown", null);
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocationAndShowTrain();
        }
    }

    private String getNearestStation(double lat, double lon) {
        Map<String, double[]> stations = new HashMap<>();
        stations.put("Tunis", new double[]{36.8008, 10.18});
        stations.put("SaiidaManoubia", new double[]{36.7999, 10.12});
        stations.put("Mellassine", new double[]{36.81, 10.13});
        stations.put("Erraoudha", new double[]{36.812, 10.11});
        stations.put("LeBardo", new double[]{36.818, 10.10});
        stations.put("ElBortal", new double[]{36.825, 10.095});
        stations.put("Manouba", new double[]{36.83, 10.09});
        stations.put("LesOrangers", new double[]{36.835, 10.085});
        stations.put("Gobaa", new double[]{36.84, 10.08});
        stations.put("GobaaVille", new double[]{36.845, 10.075});

        String nearest = "Unknown";
        double minDistance = Double.MAX_VALUE;

        for (Map.Entry<String, double[]> entry : stations.entrySet()) {
            double[] coords = entry.getValue();
            double distance = Math.pow(lat - coords[0], 2) + Math.pow(lon - coords[1], 2);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = entry.getKey();
            }
        }

        return nearest;
    }

    private Map<String, Train> getNextTrainsFromNearestStation(double lat, double lon) {
        // Get the nearest station
        String station = getNearestStation(lat, lon);

        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        Log.d("TRAIN_DEBUG", "Current time: " + currentTime + ", Nearest Station: " + station);

        Map<String, Train> nextTrains = new HashMap<>();

        // Determine available directions based on station
        if ("Tunis".equals(station)) {
            // Only show trains going to GobaaVille (allez)
            nextTrains.put("allez", null);
        } else if ("GobaaVille".equals(station)) {
            // Only show trains going to Tunis (retour)
            nextTrains.put("retour", null);
        } else {
            // Intermediate station - show both directions
            nextTrains.put("allez", null);  // Towards GobaaVille
            nextTrains.put("retour", null); // Towards Tunis
        }

        for (Train train : trainList) {
            String timeAtStation = train.getTimeForStation(station);
            if (timeAtStation == null) continue;

            // Determine direction based on terminal stations
            String direction;
            if (train.getTerminus().equals("GobaaVille")) {
                direction = "allez";
            } else if (train.getTerminus().equals("Tunis")) {
                direction = "retour";
            } else {
                continue; // Skip trains with invalid destinations
            }

            // Skip if this direction isn't relevant for current station
            if (!nextTrains.containsKey(direction)) continue;

            if (timeAtStation.compareTo(currentTime) > 0) {
                Train currentNextTrain = nextTrains.get(direction);
                if (currentNextTrain == null ||
                        timeAtStation.compareTo(currentNextTrain.getTimeForStation(station)) < 0) {
                    nextTrains.put(direction, train);
                }
            }
        }

        return nextTrains;
    }

    private void updateUI(String station, Map<String, Train> trains) {
        // Find all needed TextViews from the UI
        Log.d("UPDATEUI_DEBUG", "Trains Object: "+trains.toString());
        TextView trainInfoText = findViewById(R.id.trainInfoText);
        TextView departureTimeText = findViewById(R.id.departureText);
        TextView arrivalTimeText = findViewById(R.id.arrivalText);
        TextView statusText = findViewById(R.id.statusText);
        TextView departureStationText = findViewById(R.id.departureStationText);
        TextView arrivalStationText = findViewById(R.id.arrivalStationText);
        TextView departureTrainText = findViewById(R.id.trainNumberText);

        StringBuilder infoBuilder = new StringBuilder();
        infoBuilder.append("Nearest Station: ").append(station).append("\n\n");

        // Get trains in both directions
        Train allezTrain = trains.get("allez");
        Train retourTrain = trains.get("retour");

        if (allezTrain != null || retourTrain != null) {
            // Process outbound ("allez") train
            if (allezTrain != null) {
                String allezTime = allezTrain.getTimeForStation(station);
                infoBuilder.append("Outbound Train: ")
                        .append(" at ").append(allezTime).append("\n")
                        .append("Destination: ").append(allezTrain.getDestination()).append("\n\n");

                // Update the main train display if we have an outbound train
                departureTimeText.setText(allezTime);
                departureStationText.setText(station);
                arrivalStationText.setText(allezTrain.getDestination());

            } else {
                infoBuilder.append("No upcoming outbound trains.\n\n");
            }

            // Process return ("retour") train
            if (retourTrain != null) {
                String retourTime = retourTrain.getTimeForStation(station);
                infoBuilder.append("Return Train: ")
                        .append(" at ").append(retourTime).append("\n")
                        .append("Destination: ").append(retourTrain.getDestination());

                // Only update arrival time if we have a return train
                arrivalTimeText.setText(retourTime);

                // If there's no outbound train but we have a return train, use it for the main display
                if (allezTrain == null) {
                    departureTimeText.setText(retourTime);
                    departureStationText.setText(station);
                    arrivalStationText.setText(retourTrain.getDestination());
                }
            } else {
                infoBuilder.append("No upcoming return trains.");
            }

            // Display combined information in the info text view
            trainInfoText.setText(infoBuilder.toString());
        } else {
            // No trains available in either direction
            trainInfoText.setText("Nearest Station: " + station + "\nNo upcoming trains available in either direction.");

            // Reset main display elements
            departureTimeText.setText("--:--");
            arrivalTimeText.setText("--:--");
            statusText.setText("No trains");
            departureTrainText.setText("No service");
        }
    }
}