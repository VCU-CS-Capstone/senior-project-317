package com.vcuseniordesign.bym;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.estimote.coresdk.service.BeaconService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;


public class BeaconTracker extends Service implements BeaconConsumer {
    BeaconManager beaconManager;
    LocationManager phoneLocationManager;
    double curLat, curLong;
    Thread updateThread;
    boolean[] isServiceStillRunning = new boolean[4];

    @Override
    public void onCreate(){
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.bind(this);
        beaconManager.getBeaconParsers().add(new BeaconParser("CorrectParse").setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.setForegroundScanPeriod(3100);
        beaconManager.setForegroundBetweenScanPeriod(1000);
        beaconManager.setBackgroundScanPeriod(5100);
        beaconManager.setBackgroundBetweenScanPeriod(2000);
        phoneLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        try{
            phoneLocationManager.requestLocationUpdates(phoneLocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    curLat=location.getLatitude();
                    curLong=location.getLongitude();
                }
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}
                @Override
                public void onProviderEnabled(String provider) {}
                @Override
                public void onProviderDisabled(String provider) {}
            });}catch(SecurityException e){}
        runUpdateThread();
    }

    public void runUpdateThread(){

        serviceIsStillRunning();
        updateThread = new Thread() {
            @Override
            public void run() {
                try {
                    while(isServiceStillRunning[0] || isServiceStillRunning[1] || isServiceStillRunning[2] || isServiceStillRunning[3]){
                    //doStuff

                    ArrayList<BeaconFoundEvent> foundBeaconEventCopy = (ArrayList<BeaconFoundEvent>) ((BeaconApplication) getApplication()).getFoundBeaconEvents().clone();
                    BeaconFoundEvent locationBeacon = new BeaconFoundEvent(new Beacon.Builder().build(),curLat,curLong,System.currentTimeMillis());
                    foundBeaconEventCopy.add(locationBeacon);
                    Log.d("UpdateThread","updateThreadisStillRunning"+isServiceStillRunning[0]+isServiceStillRunning[1]+isServiceStillRunning[2]+isServiceStillRunning[3]);
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    final DatabaseReference newDBRef = db.getReference();

                    Long curTime = System.currentTimeMillis();
                    newDBRef.child("observations").child("locations").child(Long.toString(curTime)).child("latitude").setValue(curLat);
                    newDBRef.child("observations").child("locations").child(Long.toString(curTime)).child("longitude").setValue(curLong);

                    for (BeaconFoundEvent bfe : foundBeaconEventCopy) {
                        Log.d("UpdateThread","foundBeaconEventSize = " + foundBeaconEventCopy.size());

                        final BeaconFoundEvent bfeToUse=bfe;
                        try {

                            newDBRef.child("observations").child(bfe.getBeaconFound().getId2().toString() + ":" + bfe.getBeaconFound().getId3().toString())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dbSnapShot) {
                                    if (!dbSnapShot.hasChild(Long.toString(bfeToUse.getLastTime()))) {
                                        newDBRef.child("observations").child(bfeToUse.getBeaconFound().getId2().toString() + ":" + bfeToUse.getBeaconFound().getId3().toString())
                                                .child(Long.toString(bfeToUse.getLastTime())).child("latitude").setValue(bfeToUse.getLastLat());
                                        newDBRef.child("observations").child(bfeToUse.getBeaconFound().getId2().toString() + ":" + bfeToUse.getBeaconFound().getId3().toString())
                                                .child(Long.toString(bfeToUse.getLastTime())).child("longitude").setValue(bfeToUse.getLastLong());
                                        newDBRef.child("observations").child(bfeToUse.getBeaconFound().getId2().toString() + ":" + bfeToUse.getBeaconFound().getId3().toString())
                                                .child(Long.toString(bfeToUse.getLastTime())).child("message").setValue(bfeToUse.getBeaconNickname());
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });


                        }catch (Exception e){}
                    }
                    //((ArrayList<BeaconFoundEvent>) ((BeaconApplication) getApplication()).getFoundBeaconEvents()).clear();
                    Log.d("UpdateThread","We got this far");

                    sleep(10000);



                    for (int k = 0; k < isServiceStillRunning.length; k++) {
                        if (isServiceStillRunning[k] == true) {
                            isServiceStillRunning[k] = false;
                            k = isServiceStillRunning.length;
                        }
                    }
                }
                }catch(Exception e){
                    Log.d("UpdateThread",e.toString());
                    e.printStackTrace();
                }
                }
        };
        updateThread.start();
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                ArrayList<Beacon> foundBeaconsCopy = (ArrayList<Beacon>)((BeaconApplication) getApplication()).getFoundBeacons().clone();
                ArrayList<BeaconFoundEvent> foundBeaconEventCopy = (ArrayList<BeaconFoundEvent>)((BeaconApplication) getApplication()).getFoundBeaconEvents().clone();
                if (beacons.size() > 0) {
                    for(Beacon b :beacons) {
                        Log.d("FoundBeacon","THE FOUND BEACON UUID IS : "+b.getId1());
                        if (!foundBeaconsCopy.contains(b)&&b.getId1().toString().equals("d0d3fa86-ca76-45ec-9bd9-6af4f6016926")){
                            foundBeaconsCopy.add(b);
                            foundBeaconEventCopy.add(new BeaconFoundEvent(b,curLat,curLong,System.currentTimeMillis()));
                        }else{
                        if(foundBeaconsCopy.contains(b)&&b.getId1().toString().equals("d0d3fa86-ca76-45ec-9bd9-6af4f6016926")){
                            for(BeaconFoundEvent bfe:((BeaconApplication) getApplication()).getFoundBeaconEvents()){
                                if(bfe.getBeaconFound().equals(b)){
                                    foundBeaconEventCopy.remove(bfe);
                                    foundBeaconEventCopy.add(new BeaconFoundEvent(b,curLat,curLong,System.currentTimeMillis()));
                                }
                            }
                        }
                        }
                    }
                }
                Log.d("BaconUpdatingService","WE ARE ABOUT TO SEND THE UPDATE INTENT");
                serviceIsStillRunning();
                BeaconApplication curApp=((BeaconApplication)getApplication());
                curApp.setFoundBeacons(foundBeaconsCopy);
                curApp.setFoundBeaconEvents(foundBeaconEventCopy);

                Intent updateListIntent = new Intent();
                updateListIntent.setAction("com.vcuseniordesign.bym");
                updateListIntent.putExtra("UpdateIntent","UPDATE");
                sendBroadcast(updateListIntent);
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void serviceIsStillRunning(){
        for(int k =0;k<isServiceStillRunning.length;k++){
            isServiceStillRunning[k]=true;
        }
    }

}
