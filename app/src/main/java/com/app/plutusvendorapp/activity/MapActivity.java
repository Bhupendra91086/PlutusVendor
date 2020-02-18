package com.app.plutusvendorapp.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.app.plutusvendorapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private SharedPreferences pref = null;
    String lat;
    String llong;
    String businessName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getApplicationContext().getSharedPreferences(getString(R.string.appSharedPref), 0); // 0 - for private mode
        lat = pref.getString(getString(R.string.lat), "0");
        llong = pref.getString(getString(R.string.llong), "0");
        businessName = pref.getString(getString(R.string.sBusiness), "Please Edit Business Name");
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng sydney = new LatLng(Double.parseDouble(lat), Double.parseDouble(llong));
        googleMap.addMarker(new MarkerOptions().position(sydney)
                .title(businessName)

        );

        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14.0f));
    }


}
