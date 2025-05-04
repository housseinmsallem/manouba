package com.example.manouba;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.Manifest;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.Style;


public class LocationFragment extends Fragment {

    private MapView mapView;
    private static final String MAP_ID = "streets-v2";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize MapLibre (Mapbox) with null — API key is in the style URL
        Mapbox.getInstance(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_location, container, false);

        mapView = rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        // Load the map style with the MapTiler API key
        String apiKey = "9IJsjsOyyjzXmSzY1Vvq";
        String styleUrl = "https://api.maptiler.com/maps/" + MAP_ID + "/style.json?key=" + apiKey;

        mapView.getMapAsync(mapboxMap -> {
            mapboxMap.setStyle(new Style.Builder().fromUri(styleUrl), style -> {
                // Enable user location
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    LocationComponent locationComponent = mapboxMap.getLocationComponent();
                    locationComponent.activateLocationComponent(
                            LocationComponentActivationOptions.builder(requireContext(), style).build()
                    );
                    locationComponent.setLocationComponentEnabled(true);
                    locationComponent.setCameraMode(CameraMode.TRACKING);
                    locationComponent.setRenderMode(RenderMode.COMPASS);

                } else {
                    // Request permission or show a message
                    Toast.makeText(requireContext(), "Location permission not granted", Toast.LENGTH_SHORT).show();
                }
            });

            // Optional: set initial camera position
            mapboxMap.setCameraPosition(new CameraPosition.Builder()
                    .target(new LatLng(36.8, 10.1)) // You can change this to Manouba or your desired location
                    .zoom(12.0)
                    .build());
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}


