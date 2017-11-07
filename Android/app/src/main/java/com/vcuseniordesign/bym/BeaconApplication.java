package com.vcuseniordesign.bym;

import android.app.Application;
import android.content.Intent;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;


public class BeaconApplication extends Application {
    private ArrayList<Beacon> savedBeacons = new ArrayList<Beacon>();
    private ArrayList<Beacon> foundBeacons = new ArrayList<Beacon>();
    private ArrayList<BeaconFoundEvent> foundBeaconEvents = new ArrayList<BeaconFoundEvent>();

    public ArrayList<Beacon> getSavedBeacons(){
        return savedBeacons;
    }
    public ArrayList<Beacon> getFoundBeacons(){
        return foundBeacons;
    }
    public ArrayList<BeaconFoundEvent> getFoundBeaconEvents(){return foundBeaconEvents;}

    public void setSavedBeacons(ArrayList<Beacon> newList){savedBeacons=newList;}
    public void setFoundBeacons(ArrayList<Beacon> newList){foundBeacons=newList;}
    public void setFoundBeaconEvents(ArrayList<BeaconFoundEvent> newList){foundBeaconEvents=newList;}

    @Override
    public void onCreate(){
        super.onCreate();
        Intent beaconScanningService = new Intent(getApplicationContext(),BeaconTracker.class);
        startService(beaconScanningService);
    }

}
