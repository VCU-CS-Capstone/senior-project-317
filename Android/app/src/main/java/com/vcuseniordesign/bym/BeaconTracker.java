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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
                        if (!foundBeaconsCopy.contains(b)){
                            foundBeaconsCopy.add(b);
                            foundBeaconEventCopy.add(new BeaconFoundEvent(b,curLat,curLong,System.currentTimeMillis()));
                        }else{
                        if(foundBeaconsCopy.contains(b)){
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
                BeaconApplication curApp=((BeaconApplication)getApplication());
                curApp.setFoundBeacons(foundBeaconsCopy);
                curApp.setFoundBeaconEvents(foundBeaconEventCopy);

                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference newDBRef = db.getReference();
                for(BeaconFoundEvent bfe:foundBeaconEventCopy){
                    newDBRef.child("observations").child(bfe.getBeaconFound().getId2().toString()+":"+bfe.getBeaconFound().getId3().toString())
                            .child(Long.toString(bfe.getLastTime())).child("latitude").setValue(bfe.getLastLat());
                    newDBRef.child("observations").child(bfe.getBeaconFound().getId2().toString()+":"+bfe.getBeaconFound().getId3().toString())
                            .child(Long.toString(bfe.getLastTime())).child("longitude").setValue(bfe.getLastLong());
                    newDBRef.child("observations").child(bfe.getBeaconFound().getId2().toString()+":"+bfe.getBeaconFound().getId3().toString())
                            .child(Long.toString(bfe.getLastTime())).child("message").setValue(bfe.getBeaconNickname());
                }
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

}
