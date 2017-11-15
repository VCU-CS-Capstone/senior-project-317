package com.vcuseniordesign.bym;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;

public class AllMap extends AppCompatActivity implements OnMapReadyCallback {
    private TextView mapDebug;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.ALLMapFrag);
        mapFragment.getMapAsync(this);
        mapDebug = (TextView) findViewById(R.id.mapDebugText);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        ArrayList<Beacon> savedBeaconsCopy = (ArrayList<Beacon>) ((BeaconApplication)getApplication()).getSavedBeacons().clone();
        ArrayList<BeaconFoundEvent> foundBeaconEventsCopy = (ArrayList<BeaconFoundEvent>) ((BeaconApplication)getApplication()).getFoundBeaconEvents().clone();
        for(BeaconFoundEvent bfe: foundBeaconEventsCopy){
            if(savedBeaconsCopy.contains(bfe.getBeaconFound())){
                LatLng lastKnownLoc= new LatLng(bfe.getLastLat(),bfe.getLastLong());
                googleMap.addMarker(new MarkerOptions().position(lastKnownLoc).title(bfe.getBeaconNickname()));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(lastKnownLoc));
                mapDebug.append(bfe.getBeaconNickname()+ " Added\n");
            }
        }

    }
}
