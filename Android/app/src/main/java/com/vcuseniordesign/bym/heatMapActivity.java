package com.vcuseniordesign.bym;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.List;

public class heatMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    //String mostRecentTime;
    final int ELEMENTS_PER_BATCH=20000;
    boolean valuesLeft=true;
    ArrayList<LatLng> heatList=new ArrayList<LatLng>();
    HeatmapTileProvider generatedHeatMap;
    GoogleMap curMap;
    ProgressBar loadingCircleTwo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heat_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.heatMapFrag);
        mapFragment.getMapAsync(this);
        Log.d("Heatmap","We are starting the  hetmap screen");

        loadingCircleTwo = (ProgressBar)findViewById(R.id.LoadingCircle2);
        loadingCircleTwo.setVisibility(View.VISIBLE);

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




        ValueEventListener getMostRecentValue = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String recentTime="0";
                Log.d("HeatmapLoading","We have the data");

                int count=0;
                for(DataSnapshot curtime: dataSnapshot.getChildren()){
                    Log.d("HeatmapPoints","Adding a point to the heatmap.");

                    double newLat=0;
                    double newLong=0;
                    if(!(curtime.child("latitude").getValue()==null)&&!(curtime.child("longitude").getValue()==null)) {
                        if (curtime.child("latitude").getValue() instanceof Long) {
                            newLat = ((Long) curtime.child("latitude").getValue()).doubleValue();
                        } else newLat = (double) curtime.child("latitude").getValue();
                        if (curtime.child("longitude").getValue() instanceof Long) {
                            newLong = ((Long) curtime.child("longitude").getValue()).doubleValue();
                        } else newLong = (double) curtime.child("longitude").getValue();
                    }

                    if(newLat!=0&&newLong!=0) {
                        recentTime=curtime.getKey();
                        //changeMostRecent(curtime.getKey());
                        heatList.add(new LatLng(newLat, newLong));
                    }
                    //Double newLat= (Double)dataSnapshot.child("latitude").getValue();
                    //Double newLong=(Double)dataSnapshot.child("longitude").getValue();
                    count++;
                }
                Log.d("HeatmapLoading","Last batch had "+count+" points.");
                if(count>0) {
                    Log.d("HeatmapPoints","All points added. About to generate heatmap.");
                    generatedHeatMap=new HeatmapTileProvider.Builder().data(heatList).build();
                    curMap.addTileOverlay(new TileOverlayOptions().tileProvider(generatedHeatMap));
                    Log.d("HeatmapLoading","The heatmap has been added. Starting next set.");
                    getNextBatch(recentTime);
                }else{Log.d("HeatmapLoading","We have reached the end of the points");}
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {Log.d("HeatmapLoading","Error: "+databaseError.getDetails());}
        };


        Log.d("Heatmap","We are about to request data");
        Query lastQuery = newDBRef.child("heatmap").orderByKey().limitToFirst(5);
        Log.d("Heatmap","We have requested the heatmap data");

        lastQuery.addListenerForSingleValueEvent(getMostRecentValue);
        //getNextBatch(mostRecentTime);


        db.goOffline();
    }

    private void changeMostRecent(String newRecent){
        //mostRecentTime=newRecent;
    }

    private void getNextBatch(String ValToStartAt){
        Log.d("HeatmapLoading","Starting a new batch at: "+ValToStartAt);
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

        ValueEventListener getMostRecentValue = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String recentTime="0";
                Log.d("HeatmapLoading","We have the data");
                int count=0;
                for(DataSnapshot curtime: dataSnapshot.getChildren()){
                    Log.d("HeatmapPoints","Adding a point to the heatmap.");

                    double newLat=0;
                    double newLong=0;
                    if(!(curtime.child("latitude").getValue()==null)&&!(curtime.child("longitude").getValue()==null)) {
                        if (curtime.child("latitude").getValue() instanceof Long) {
                            newLat = ((Long) curtime.child("latitude").getValue()).doubleValue();
                        } else newLat = (double) curtime.child("latitude").getValue();
                        if (curtime.child("longitude").getValue() instanceof Long) {
                            newLong = ((Long) curtime.child("longitude").getValue()).doubleValue();
                        } else newLong = (double) curtime.child("longitude").getValue();
                    }

                    if(newLat!=0&&newLong!=0) {
                        recentTime=curtime.getKey();
                        //changeMostRecent(curtime.getKey());
                        heatList.add(new LatLng(newLat, newLong));
                    }
                    //Double newLat= (Double)dataSnapshot.child("latitude").getValue();
                    //Double newLong=(Double)dataSnapshot.child("longitude").getValue();
                    count++;
                }

                Log.d("HeatmapLoading","Last batch had "+count+" points.");
                if(!(count<ELEMENTS_PER_BATCH)) {
                    Log.d("HeatmapPoints","All points added. About to generate heatmap. Number of points: "+heatList.size());
                    generatedHeatMap.setData(heatList);

                    Log.d("HeatmapLoading","The heatmap has been added. Starting next set.");
                    getNextBatch(recentTime);
                }else{
                    Log.d("HeatmapPoints","All points added. About to generate heatmap. Number of points: "+heatList.size());
                    generatedHeatMap.setData(heatList);
                    Log.d("HeatmapLoading","We have reached the end of the points");
                    loadingCircleTwo.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {Log.d("HeatmapLoading","Error: "+databaseError.getDetails());}
        };

        Log.d("Heatmap","We are about to request data");
        Query lastQuery = newDBRef.child("heatmap").orderByKey().startAt(ValToStartAt).limitToFirst(ELEMENTS_PER_BATCH);
        Log.d("Heatmap","We have requested the heatmap data");
        lastQuery.addListenerForSingleValueEvent(getMostRecentValue);

        db.goOffline();


    }

    public void onMapReady(GoogleMap googleMap) {
        curMap=googleMap;
        ArrayList<Beacon> savedBeaconsCopy = (ArrayList<Beacon>) ((BeaconApplication)getApplication()).getSavedBeacons().clone();
        ArrayList<BeaconFoundEvent> foundBeaconEventsCopy = (ArrayList<BeaconFoundEvent>) ((BeaconApplication)getApplication()).getFoundBeaconEvents().clone();
        for(BeaconFoundEvent bfe: foundBeaconEventsCopy){
            if(savedBeaconsCopy.contains(bfe.getBeaconFound())){
                LatLng lastKnownLoc= new LatLng(bfe.getLastLat(),bfe.getLastLong());
                //googleMap.addMarker(new MarkerOptions().position(lastKnownLoc).title(bfe.getBeaconNickname()));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(lastKnownLoc));
                //mapDebug.append(bfe.getBeaconNickname()+ " Added\n");
            }
        }

    }
}
