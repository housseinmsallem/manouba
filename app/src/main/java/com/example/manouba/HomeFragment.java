package com.example.manouba; // Replace with your actual package name

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;
import androidx.fragment.app.Fragment;

import com.example.manouba.R;
import com.example.manouba.Train;
import com.example.manouba.TrainDatabaseHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {

    private String closestStationText;
    private TrainDatabaseHelper databaseHelper;
    private FusedLocationProviderClient fusedLocationClient;
    private List<Train> trainList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set immersive full screen mode
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        // Adjust padding for system insets
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.mainTrainCard), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize database and location services
        databaseHelper = new TrainDatabaseHelper(requireContext());
        trainList = databaseHelper.getAllTrains();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Check location permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            getLocationAndShowTrain(view);
        }
    }

    private void getLocationAndShowTrain(View view) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null) {
                String nearestStation = getNearestStation(location.getLatitude(), location.getLongitude());
                Map<String, Train> nextTrain = getNextTrainsFromNearestStation(location.getLatitude(), location.getLongitude());
                updateUI(view, nearestStation, nextTrain);
                Log.d("LOCATION_DEBUG", "Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude());
            } else {
                updateUI(view, "Unknown", null);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            View view = getView();
            if (view != null) {
                getLocationAndShowTrain(view);
            }
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
        closestStationText = nearest;
        return nearest;
    }

    private Map<String, Train> getNextTrainsFromNearestStation(double lat, double lon) {
        String station = getNearestStation(lat, lon);
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        Log.d("TRAIN_DEBUG", "Current time: " + currentTime + ", Nearest Station: " + station);

        Map<String, Train> nextTrains = new HashMap<>();

        if ("Tunis".equals(station)) {
            nextTrains.put("allez", null);
        } else if ("GobaaVille".equals(station)) {
            nextTrains.put("retour", null);
        } else {
            nextTrains.put("allez", null);
            nextTrains.put("retour", null);
        }

        for (Train train : trainList) {
            String timeAtStation = train.getTimeForStation(station);
            if (timeAtStation == null) continue;

            String direction;
            if (train.getTerminus().equals("GobaaVille")) {
                direction = "allez";
            } else if (train.getTerminus().equals("Tunis")) {
                direction = "retour";
            } else {
                continue;
            }

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

    private void updateUI(View view, String station, Map<String, Train> trains) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String formattedTime = sdf.format(calendar.getTime());

        TextView departureTimeText = view.findViewById(R.id.departureText);
        TextView departureStationText = view.findViewById(R.id.departureStation);
        TextView arrivalStationText = view.findViewById(R.id.arrivalStation);
        TextView arrivalStationTime = view.findViewById(R.id.arrivalTimeText);
        TextView closestStation = view.findViewById(R.id.closestStationText);
        TextView currentTime = view.findViewById(R.id.currentTimeText);
        TextView returnDepartureStation = view.findViewById(R.id.returnDepartureStation);

        if (closestStation != null) closestStation.setText(station != null ? station : "Unknown");
        if (departureStationText != null) departureStationText.setText(station != null ? station : "Unknown");
        if (returnDepartureStation != null) returnDepartureStation.setText(station != null ? station : "Unknown");
        if (currentTime != null) currentTime.setText(formattedTime);

        Train allezTrain = null;
        Train retourTrain = null;

        if (trains != null) {
            allezTrain = trains.get("allez");
            retourTrain = trains.get("retour");
        }

        if (allezTrain != null && departureTimeText != null && arrivalStationText != null) {
            String allezTime = allezTrain.getTimeForStation(station);
            departureTimeText.setText(allezTime != null ? allezTime : "--:--");
            arrivalStationText.setText(allezTrain.getDestination());
        }

        if (retourTrain != null && arrivalStationTime != null) {
            String retourTime = retourTrain.getTimeForStation(station);
            arrivalStationTime.setText(retourTime != null ? retourTime : "--:--");

            if (allezTrain == null && departureTimeText != null && arrivalStationText != null) {
                departureTimeText.setText(retourTime != null ? retourTime : "--:--");
                arrivalStationText.setText(retourTrain.getDestination());
            }
        }

        if (allezTrain == null && retourTrain == null && departureTimeText != null) {
            departureTimeText.setText("--:--");
        }
    }

}
