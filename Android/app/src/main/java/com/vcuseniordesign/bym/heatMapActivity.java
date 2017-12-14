package com.vcuseniordesign.bym;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

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

    ArrayList<LatLng> heatList;
    HeatmapTileProvider generatedHeatMap;
    GoogleMap curMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heat_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.heatMapFrag);
        mapFragment.getMapAsync(this);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference newDBRef = db.getReference();
        Query lastQuery = newDBRef.child("observations").child("locations").orderByKey();


        ValueEventListener getMostRecentValue = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                heatList=new ArrayList<LatLng>();
                for(DataSnapshot curtime: dataSnapshot.getChildren()){

                    double newLat;
                    double newLong;
                    if(curtime.child("latitude").getValue()instanceof Long){
                        newLat =((Long) curtime.child("latitude").getValue()).doubleValue();
                    }else newLat=(double)curtime.child("latitude").getValue();
                    if(curtime.child("longitude").getValue()instanceof Long){
                        newLong =((Long) curtime.child("longitude").getValue()).doubleValue();
                    }else newLong=(double)curtime.child("longitude").getValue();

                    if(newLat!=0&&newLong!=0)
                    heatList.add(new LatLng(newLat,newLong));
                //Double newLat= (Double)dataSnapshot.child("latitude").getValue();
                //Double newLong=(Double)dataSnapshot.child("longitude").getValue();

                }
                generatedHeatMap=new HeatmapTileProvider.Builder().data(heatList).build();
                curMap.addTileOverlay(new TileOverlayOptions().tileProvider(generatedHeatMap));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        lastQuery.addListenerForSingleValueEvent(getMostRecentValue);

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
