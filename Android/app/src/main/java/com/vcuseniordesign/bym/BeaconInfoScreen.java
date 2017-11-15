package com.vcuseniordesign.bym;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class BeaconInfoScreen extends AppCompatActivity implements OnMapReadyCallback{
    BeaconFoundEvent currentBeaconEvent=new BeaconFoundEvent();
    TextView beaconName;
    TextView beaconIds;
    Button saveNicknameButton;
    Button unpairButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_info_screen);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFrag);
        mapFragment.getMapAsync(this);

        if(getIntent().getSerializableExtra("beaconInfo")!=null){
            currentBeaconEvent=(BeaconFoundEvent)getIntent().getSerializableExtra("beaconInfo");
        }
        beaconName = (TextView) findViewById(R.id.beaconNickname);
        beaconIds = (TextView) findViewById(R.id.beaconIds);
        beaconName.setText(currentBeaconEvent.getBeaconNickname());
        beaconIds.setText(currentBeaconEvent.getBeaconFound().getId2().toString()+"\n");
        beaconIds.append(currentBeaconEvent.getBeaconFound().getId3().toString());
        saveNicknameButton=(Button) findViewById(R.id.saveNicknameButton);
        saveNicknameButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentBeaconEvent.setBeaconNickname(beaconName.getText().toString());
            }
        });
        unpairButton=(Button) findViewById(R.id.unpairButton);
        unpairButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                returnAndUnpair();

            }
        });

    }

    public void onMapReady(GoogleMap googleMap){
        LatLng lastKnownLoc= new LatLng(currentBeaconEvent.getLastLat(),currentBeaconEvent.getLastLong());
        googleMap.addMarker(new MarkerOptions().position(lastKnownLoc).title(currentBeaconEvent.getBeaconNickname()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(lastKnownLoc));
    }

    public void returnAndUnpair(){
        Intent intent = new Intent(this, TagInfo.class);
        intent.putExtra("beaconToUnpair",currentBeaconEvent);
        startActivity(intent);
    }
}
