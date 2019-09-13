package com.akrivonos.app_standart_java.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.akrivonos.app_standart_java.R;
import com.akrivonos.app_standart_java.listeners.MapCoordinatesPhotoListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapSearch extends Fragment implements OnMapReadyCallback {
    private GoogleMap map;
    private MapCoordinatesPhotoListener mapCoordinatesPhotoListener;
    private FusedLocationProviderClient fusedLocationClient;
    private Button chooseButton;
    public MapSearch() {
        // Required empty public constructor
    }

    public void setMapListener(MapCoordinatesPhotoListener mapCoordinatesPhotoListener){
        this.mapCoordinatesPhotoListener = mapCoordinatesPhotoListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_search, container, false);
        setHasOptionsMenu(true);
        chooseButton = view.findViewById(R.id.choose_button);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapContainer);
        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(this);
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        return view;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        map = googleMap;
        View.OnClickListener chooseClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng latLng = googleMap.getCameraPosition().target;
                mapCoordinatesPhotoListener.setResultCoordinatesPic(latLng);
            }
        };
        chooseButton.setOnClickListener(chooseClickListener);
        map.setMyLocationEnabled(true);
        getCurrentLocation();
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (enabled) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).build();
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                        map.moveCamera(cameraUpdate);
                    }
                }
            });
        } else {
            Toast.makeText(getContext(), "Please enable gps module", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().setTitle("Find on the Map");
        super.onCreateOptionsMenu(menu, inflater);
    }
}
