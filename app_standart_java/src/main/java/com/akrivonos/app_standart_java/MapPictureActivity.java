package com.akrivonos.app_standart_java;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

import static com.akrivonos.app_standart_java.constants.Values.LAT_LNG;

public class MapPictureActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private final View.OnClickListener chooseClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LatLng latLng = map.getCameraPosition().target;
            Bundle bundle = new Bundle();
            bundle.putParcelable(LAT_LNG, latLng);
            setResult(1, new Intent().putExtra(LAT_LNG, bundle));
            finish();
        }
    };
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_picture);
        Button chooseButton = findViewById(R.id.choose_button);
        chooseButton.setOnClickListener(chooseClickListener);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapContainer);
        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(this);
        } else {
            Log.d("test", "supportMapFragment is null");
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getBaseContext());
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        map.setMyLocationEnabled(true);
        getCurrentLocation();
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        Log.d("test", "get current location start");
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (enabled) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).build();
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                        map.moveCamera(cameraUpdate);
                        Log.d("test", latLng.toString());
                        Log.d("test", "get current location finish move");
                    }
                }
            });
        } else {
            Toast.makeText(this, "Please enable gps module", Toast.LENGTH_SHORT).show();
        }
    }
}
