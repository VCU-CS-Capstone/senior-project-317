package com.vcuseniordesign.bym;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class BeaconInfoScreen extends AppCompatActivity implements OnMapReadyCallback{
    BeaconInfoScreen curScreen = this;
    BeaconFoundEvent currentBeaconEvent=new BeaconFoundEvent();
    BeaconSaved currentBeaconInfo;
    TextView beaconName;
    TextView beaconIds;
    TextView timeText;
    Button saveNicknameButton;
    Button unpairButton;
    Button findBeaconbutton;
    BroadcastReceiver updateReceiver;
    GoogleMap curMap;
    Marker curMarker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);;
        setContentView(R.layout.activity_beacon_info_screen);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFrag);
        mapFragment.getMapAsync(this);

        if(getIntent().getSerializableExtra("beaconInfo")!=null){
            currentBeaconEvent=(BeaconFoundEvent)getIntent().getSerializableExtra("beaconInfo");
            ArrayList<BeaconSaved> beaconInfoCopy = (ArrayList<BeaconSaved>)((BeaconApplication) getApplication()).getSavedBeaconsInfo().clone();
            for(BeaconSaved curBeaconInfo:beaconInfoCopy){
                if(curBeaconInfo.getCurBeacon().equals(currentBeaconEvent.getBeaconFound())){
                    currentBeaconInfo=curBeaconInfo;
                }
            }
        }



        beaconName = (TextView) findViewById(R.id.beaconNickname);
        beaconIds = (TextView) findViewById(R.id.beaconIds);
        timeText =(TextView) findViewById(R.id.timeText);
        Date dateFromTime = new Date(currentBeaconEvent.getLastTime());
        timeText.setText(dateFromTime.toString());

        //timeText.setText("Lat: "+currentBeaconEvent.getLastLat());
        //timeText.append("Long: "+currentBeaconEvent.getLastLong());
        beaconName.setText(currentBeaconInfo.getBeaconName());
        beaconIds.setText(currentBeaconEvent.getBeaconFound().getId2().toString()+":"+currentBeaconEvent.getBeaconFound().getId3().toString());
        //beaconIds.append(currentBeaconEvent.getBeaconFound().getId3().toString());
        saveNicknameButton=(Button) findViewById(R.id.saveNicknameButton);
        saveNicknameButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentBeaconEvent.setBeaconNickname(beaconName.getText().toString());
                currentBeaconInfo.setBeaconName(beaconName.getText().toString());
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(curScreen.getCurrentFocus().getWindowToken(), 0);
            }
        });
        unpairButton=(Button) findViewById(R.id.unpairButton);
        unpairButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(curScreen);
                builder.setMessage("Are you sure you want to delete beacon(s)?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                returnAndUnpair();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                builder.create().show();
            }
        });
        findBeaconbutton = (Button) findViewById(R.id.findBeaconButton);
        findBeaconbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference newDBRef = db.getReference();

                DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
                connectedRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        boolean connected = snapshot.getValue(Boolean.class);
                        if (connected) {

                        } else {
                            db.goOnline();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        System.err.println("Listener was cancelled");
                    }
                });

                Query lastQuery = newDBRef.child("observations").child(currentBeaconEvent.getBeaconFound().getId2().toString()+":"+currentBeaconEvent.getBeaconFound().getId3().toString()).orderByKey().limitToLast(1);


                ValueEventListener getMostRecentValue = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot mostRecentEvent:dataSnapshot.getChildren()) {
                            Double newLat = (Double) mostRecentEvent.child("latitude").getValue();
                            Double newLong = (Double) mostRecentEvent.child("longitude").getValue();
                            updateMap(new LatLng(newLat, newLong));
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                };
                lastQuery.addListenerForSingleValueEvent(getMostRecentValue);

                db.goOffline();
            }
        });

        updateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getStringExtra("UpdateIntent")!=null) {
                    Log.d("BaconUpdateReceiverBIS","WE ARE UPDATING THE MORE INFO SCREEN");

                }
            }
        };
        registerReceiver(updateReceiver,new IntentFilter("com.vcuseniordesign.bym"));


    }

    public void onBackPressed() {
        Intent intent = new Intent(this, TagInfo.class);
        startActivity(intent);
        //super.onBackPressed();
    }

    public void onPause(){
        super.onPause();
        unregisterReceiver(updateReceiver);
    }

    public void onResume(){
        super.onResume();
        registerReceiver(updateReceiver,new IntentFilter("com.vcuseniordesign.bym"));
    }

    public void onMapReady(GoogleMap googleMap){
        curMap=googleMap;
        LatLng lastKnownLoc= new LatLng(currentBeaconEvent.getLastLat(),currentBeaconEvent.getLastLong());

        String titleText = currentBeaconEvent.getBeaconNickname();
        if (titleText.equals("empty") || titleText.equals("") || titleText.equals(" ")) {
            titleText = "Scanned by user";
        }
        curMarker =googleMap.addMarker(new MarkerOptions().position(lastKnownLoc).title(titleText));

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(lastKnownLoc));
        curMap.moveCamera(CameraUpdateFactory.zoomTo(16));
    }

    public void updateMap(LatLng newLoc){
        curMarker.remove();

        String titleText = currentBeaconEvent.getBeaconNickname();
        if (titleText.equals("empty") || titleText.equals("") || titleText.equals(" ")) {
            titleText = "Scanned by user";
        }

        curMarker=curMap.addMarker(new MarkerOptions().position(newLoc).title(titleText));
        //timeText.setText("Lat: "+newLoc.latitude);
        //timeText.append(" Long: "+newLoc.longitude);
        curMap.moveCamera(CameraUpdateFactory.newLatLng(newLoc));
        curMap.moveCamera(CameraUpdateFactory.zoomTo(16));
    }


    public void returnAndUnpair(){
        Intent intent = new Intent(this, TagInfo.class);
        intent.putExtra("beaconToUnpair",currentBeaconEvent);
        startActivity(intent);
    }
}
