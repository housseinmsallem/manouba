package com.example.manouba;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {

    private TrainDatabaseHelper databaseHelper;
    private FusedLocationProviderClient fusedLocationClient;
    private List<Train> trainList;
    private TextView departureTimeText, departureStationText, arrivalStationText;

    public HomeFragment() {
        super(R.layout.fragment_home); // Use your XML layout file for the fragment
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(requireActivity());  // Ensure the activity uses edge-to-edge mode

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Request location permission if not granted
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            getLocationAndShowTrain();
        }

        // Load train data
        databaseHelper = new TrainDatabaseHelper(requireActivity());
        trainList = databaseHelper.getAllTrains();
    }

    private void getLocationAndShowTrain() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return; // Permissions not granted
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
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
        String station = getNearestStation(lat, lon);

        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        Log.d("TRAIN_DEBUG", "Current time: " + currentTime + ", Nearest Station: " + station);

        Map<String, Train> nextTrains = new HashMap<>();

        // Determine available directions based on station
        if ("Tunis".equals(station)) {
            nextTrains.put("allez", null);  // Only show trains going to GobaaVille (allez)
        } else if ("GobaaVille".equals(station)) {
            nextTrains.put("retour", null); // Only show trains going to Tunis (retour)
        } else {
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
        departureTimeText = getView().findViewById(R.id.departureText);
        departureStationText = getView().findViewById(R.id.departureStation);
        arrivalStationText = getView().findViewById(R.id.arrivalStation);

        StringBuilder infoBuilder = new StringBuilder();
        infoBuilder.append("Nearest Station: ").append(station).append("\n\n");

        Train allezTrain = trains.get("allez");
        Train retourTrain = trains.get("retour");

        if (allezTrain != null || retourTrain != null) {
            if (allezTrain != null) {
                String allezTime = allezTrain.getTimeForStation(station);
                infoBuilder.append("Outbound Train: ")
                        .append(" at ").append(allezTime).append("\n")
                        .append("Destination: ").append(allezTrain.getDestination()).append("\n\n");

                departureTimeText.setText(allezTime);
                departureStationText.setText(station);
                arrivalStationText.setText(allezTrain.getDestination());
            } else {
                infoBuilder.append("No upcoming outbound trains.\n\n");
            }

            if (retourTrain != null) {
                String retourTime = retourTrain.getTimeForStation(station);
                infoBuilder.append("Return Train: ")
                        .append(" at ").append(retourTime).append("\n")
                        .append("Destination: ").append(retourTrain.getDestination());
                if (allezTrain == null) {
                    departureTimeText.setText(retourTime);
                    departureStationText.setText(station);
                    arrivalStationText.setText(retourTrain.getDestination());
                }
            } else {
                infoBuilder.append("No upcoming return trains.");
            }
        } else {
            departureTimeText.setText("--:--");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocationAndShowTrain();
        }
    }
}
