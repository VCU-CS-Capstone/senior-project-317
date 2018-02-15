package com.vcuseniordesign.bym;

import android.Manifest;
import android.annotation.TargetApi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

import com.google.firebase.database.*;

public class TagInfo extends AppCompatActivity /*implements BeaconConsumer */{
    TagInfo curScreen= this;
    TextView deviceInfo;
    ListView deviceList;
    private BeaconManager beaconManager;
    private ArrayList<Beacon> beaconList = new ArrayList<Beacon>();
    //private ArrayList<Beacon> savedBeacons = new ArrayList<Beacon>();
    //private ArrayList<BeaconFoundEvent> foundBeaconList = new ArrayList<BeaconFoundEvent>();
    homeBeaconButtonAdapter deviceListAdapter;
    private LocationManager phoneLocationManager;
    double curLat = 0;
    double curLong = 0;
    private BroadcastReceiver updateReceiver;

    @Override
    @TargetApi(23)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_info);

        //deleteBeaconFile();
        //((BeaconApplication)getApplication()).getSavedBeacons().clear();
        /*


        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.bind(this);
        beaconManager.getBeaconParsers().add(new BeaconParser("CorrectParse").setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.setForegroundScanPeriod(3100);
        beaconManager.setForegroundBetweenScanPeriod(1000);
        beaconManager.setBackgroundScanPeriod(5100);
        beaconManager.setBackgroundBetweenScanPeriod(2000);*/
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

        /*
        if(getIntent().getSerializableExtra("savedBeaconList")!=null){
            ArrayList<Beacon> updatedSaveList = new ArrayList<Beacon>((ArrayList<Beacon>) getIntent().getSerializableExtra("savedBeaconList"));
            for(Beacon curBeaconToAdd:getSavedBeaconsFromFile()){
                if(!updatedSaveList.contains(curBeaconToAdd)){
                    updatedSaveList.add(curBeaconToAdd);
                }
            }
            savedBeacons=updatedSaveList;
        }else{
            savedBeacons=getSavedBeaconsFromFile();
        }*/



        //Permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check 
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                    }
                });
                builder.show();
            }
            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
                    }
                });
                builder.show();
            }
            if (this.checkSelfPermission(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs Bluetooth enabled");
                builder.setMessage("Please turn on bluetooth so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 3);
                    }
                });
                builder.show();
            }
        }
        //end Perms
        /*
        for(Beacon curBeacon: savedBeacons){
            foundBeaconList.add(new BeaconFoundEvent(curBeacon,curLat,curLong,System.currentTimeMillis()));
        }*/

        if(getIntent().getSerializableExtra("beaconToUnpair")!=null){
            BeaconFoundEvent beaconToUnpair = (BeaconFoundEvent) getIntent().getSerializableExtra("beaconToUnpair");
            Beacon beaconObjToCheck=beaconToUnpair.getBeaconFound();
            for(Beacon b: ((BeaconApplication)getApplication()).getSavedBeacons()){
                if (b.equals(beaconObjToCheck)){
                    ((BeaconApplication)getApplication()).getSavedBeacons().remove(b);
                }
            }
            for(BeaconFoundEvent bfe:((BeaconApplication)getApplication()).getFoundBeaconEvents()){
                if(bfe.getBeaconFound().equals(beaconObjToCheck)){
                    ((BeaconApplication)getApplication()).getFoundBeaconEvents().remove(bfe);
                }
            }
        }
        deviceList = (ListView) findViewById(R.id.deviceInfoList);
        deviceListAdapter = new homeBeaconButtonAdapter(this, android.R.layout.simple_list_item_1, beaconList);
        if (((BeaconApplication)getApplication()).getSavedBeacons().size() == 0){ deviceListAdapter.add(null);
        }else{
            ArrayList<Beacon> savedBeaconCopy = (ArrayList<Beacon>) ((BeaconApplication)getApplication()).getSavedBeacons().clone();
            for(Beacon curBeacon:savedBeaconCopy){
                beaconList.add(curBeacon);
                deviceListAdapter.add(curBeacon);
            }
        }

        deviceList.setAdapter(deviceListAdapter);
        deviceInfo = (TextView) findViewById(R.id.deviceInfoText);
        deviceInfo.setVisibility(View.GONE);
        deviceInfo.setFocusable(false);
        deviceInfo.setFocusableInTouchMode(false);
        deviceInfo.setClickable(false);

        final Button addButton = (Button) findViewById(R.id.addNewDeviceButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launchAllDeviceList();
            }
        });
        final Button allMapButton = (Button) findViewById(R.id.AllMapButton);
        allMapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launchAllMap();
            }
        });
        allMapButton.setVisibility(View.GONE);
        final Button heatmapButton = (Button) findViewById(R.id.heatButton);
        heatmapButton.setVisibility(View.GONE);
        heatmapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launchHeatMap();
            }
        });
        final Button button = (Button) findViewById(R.id.settings_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launchSettingsActivity();
            }
        });
        final Button clearButton = (Button) findViewById(R.id.clearSavedButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(curScreen);
                builder.setMessage("Are you sure you want to delete beacon(s)?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteBeaconFile();
                                deviceInfo.append("Saved Beacons Removed");
                                ((BeaconApplication)getApplication()).getSavedBeacons().clear();
                                deviceListAdapter.clear();
                                deviceListAdapter.add(null);
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


        updateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getStringExtra("UpdateIntent")!=null) {
                    deviceListAdapter.clear();
                    ArrayList<Beacon> savedBeaconCopy = (ArrayList<Beacon>) ((BeaconApplication)getApplication()).getSavedBeacons().clone();
                    for (Beacon b : savedBeaconCopy) {
                        deviceListAdapter.add(b);
                    }
                }
            }
        };

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent.getSerializableExtra("beaconToUnpair")!=null){
            BeaconFoundEvent beaconToUnpair = (BeaconFoundEvent) intent.getSerializableExtra("beaconToUnpair");
            Beacon beaconObjToCheck=beaconToUnpair.getBeaconFound();
            ArrayList<Beacon> savedBeaconsCopy = (ArrayList<Beacon>)((BeaconApplication)getApplication()).getSavedBeacons().clone();
            for(Beacon b: savedBeaconsCopy){
                if (b.equals(beaconObjToCheck)){
                    savedBeaconsCopy.remove(b);
                }
            }
            ((BeaconApplication)getApplication()).setSavedBeacons(savedBeaconsCopy);
            ArrayList<BeaconFoundEvent> savedBFECopy = (ArrayList<BeaconFoundEvent>) ((BeaconApplication) getApplication()).getFoundBeaconEvents().clone();
            ArrayList<BeaconFoundEvent> savedBFECopyReturn = (ArrayList<BeaconFoundEvent>) savedBFECopy.clone();
            Log.d("UnpairingTag","About to remove BFE of Tag");
            int testCount=0;
            Log.d("UnpairingTag","Number of BFEs to check "+savedBFECopy.size());
            for(BeaconFoundEvent bfeTemp:savedBFECopy){
                Log.d("UnpairingTag","Start of BFE Iteration: "+testCount+ " on: "+bfeTemp.getBeaconFound().getId2()+":"+bfeTemp.getBeaconFound().getId3());
                if(bfeTemp.getBeaconFound().equals(beaconObjToCheck)){
                    savedBFECopyReturn.remove(bfeTemp);
                }
                testCount++;
            }
            ((BeaconApplication)getApplication()).setFoundBeaconEvents(savedBFECopy);
        }
        deviceList = (ListView) findViewById(R.id.deviceInfoList);
        deviceListAdapter = new homeBeaconButtonAdapter(this, android.R.layout.simple_list_item_1, beaconList);
        if (((BeaconApplication)getApplication()).getSavedBeacons().size() == 0){ deviceListAdapter.add(null);
        }else{
            ArrayList<Beacon> savedBeaconCopy = (ArrayList<Beacon>) ((BeaconApplication)getApplication()).getSavedBeacons().clone();
            for(Beacon curBeacon:savedBeaconCopy){
                beaconList.add(curBeacon);
                deviceListAdapter.add(curBeacon);
            }
        }

        deviceList.setAdapter(deviceListAdapter);
    }

    @Override
    public void onBackPressed() {
        Log.d("BackButtonTest","We are minimizing the App");
        Intent goHome = new Intent(Intent.ACTION_MAIN);
        goHome.addCategory(Intent.CATEGORY_HOME);
        goHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(goHome);
        //super.onBackPressed();
    }

    public void onStop(){
        super.onStop();
        storeSavedBeaconsInFile(((BeaconApplication)getApplication()).getSavedBeacons());
        storeFoundBeaconsInFile(((BeaconApplication)getApplication()).getFoundBeaconEvents());
        //Intent stopBeaconService = new Intent(this, BeaconTracker.class);
        //stopService(stopBeaconService);
    }

    /*
    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {

            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, org.altbeacon.beacon.Region region) {
                try {
                    if (beacons.size() > 0) {
                        for (Beacon b : beacons) {
                            final Beacon curB = b;
                            if (((BeaconApplication)getApplication()).getSavedBeacons().size() != 0) {
                                if (!beaconList.contains(b)&&((BeaconApplication)getApplication()).getSavedBeacons().contains(b)) {
                                    beaconList.add(b);
                                    try{
                                        BeaconFoundEvent newEvent = new BeaconFoundEvent(b,curLat, curLong,System.currentTimeMillis());
                                        ((BeaconApplication)getApplication()).getFoundBeaconEvents().add(newEvent);}catch(SecurityException e){}
                                    final int deviceCount = beaconList.size() - 1;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            deviceInfo.append("Added" + "\n");
                                            try {
                                                deviceListAdapter.add(curB);
                                                deviceListAdapter.add("Device " + deviceCount + "\n" +"Latitude: " +(phoneLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude())
                                                        + " Longitude: "+ (phoneLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude())+ "\n Current Time: "+ System.currentTimeMillis());
                                            } catch (SecurityException e) {
                                            }
                                        }
                                    });

                                    //deviceInfo.append(b.getId1().toString()+" \n"+"TimeDetected" + System.currentTimeMillis()+" \n");
                                } else {
                                    if (((BeaconApplication)getApplication()).getSavedBeacons().contains(b)){
                                        BeaconFoundEvent beaconEventToUpdate=((BeaconApplication)getApplication()).getFoundBeaconEvents().get(beaconList.indexOf(b));
                                        try {
                                            beaconEventToUpdate.setLastLat(curLat);
                                            beaconEventToUpdate.setLastLong(curLong);
                                            beaconEventToUpdate.setLastTime(System.currentTimeMillis());
                                        }catch(SecurityException e){}
                                        final int curElement = beaconList.indexOf(b);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    deviceListAdapter.remove(deviceListAdapter.getItem(curElement));
                                                    deviceListAdapter.insert(curB, curElement);
                                                    deviceListAdapter.insert("Device " + Integer.toString(curElement) + "\n" + "Latitude: " + (phoneLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude())
                                                            + " Longitude: " + (phoneLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude()) + "\n Current Time: " + System.currentTimeMillis(), curElement);

                                                } catch (SecurityException e) {
                                                }
                                            }
                                        });
                                    }
                                    //deviceInfo.append(b.getId1().toString()+" \n"+"TimeDetected" + System.currentTimeMillis()+" \n");
                                }
                            } else {

                            }
                            //end of single beacon stuff
                        }
                        //end of all beacons per scan
                    }
                } catch (Exception ex) {
                    Log.e("SeniorDesign", "Error was thrown: " + ex.getMessage());
                }
            }
        });
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            //Log.e(TAG_BEACON_ACTIVITY, "Error was thrown: " + e.getMessage());
        }
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }
                    });
                    builder.show();
                }
                return;
            }
            case 2: {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Functionality limited");
                builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                    }
                });
                builder.show();
            }
            case 3: {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Functionality limited");
                builder.setMessage("Since Bluetooth access has not been granted, this app will not be able to discover beacons when in the background.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                    }
                });
                builder.show();
            }
        }
    }

    private void deleteBeaconFile(){
        try{
            File fileToDelete  = new File(this.getFilesDir(), "myBeacons.txt");
            fileToDelete.delete();
            fileToDelete  = new File(this.getFilesDir(), "myBFE.txt");
            fileToDelete.delete();


        }catch(Exception e){}
    }

    private void storeSavedBeaconsInFile(ArrayList<Beacon> savedBeaconList){
        try{
            FileOutputStream fileOutput = this.openFileOutput("myBeacons.txt", Context.MODE_PRIVATE);
            String curBeaconInfo;
            for(Beacon curBeacon:savedBeaconList){

                BeaconSaved currentBeaconInfo = null;
                ArrayList<BeaconSaved> beaconInfoCopy = (ArrayList<BeaconSaved>)((BeaconApplication) getApplication()).getSavedBeaconsInfo().clone();
                for(BeaconSaved curBeaconInfoToCheck:beaconInfoCopy){
                    if(curBeaconInfoToCheck.getCurBeacon().equals(curBeacon)){
                        currentBeaconInfo=curBeaconInfoToCheck;
                    }
                }


                curBeaconInfo = curBeacon.getId1()+","
                        +curBeacon.getId2()+","
                        +curBeacon.getId3()+","
                        +currentBeaconInfo.getBeaconName()+","
                        +"\n";
                fileOutput.write(curBeaconInfo.getBytes());
            }
            fileOutput.close();
        }catch(SecurityException se){}
        catch (Exception e){}
    }

    private void storeFoundBeaconsInFile(ArrayList<BeaconFoundEvent> savedBFEList){
        try{
            FileOutputStream fileOutput = this.openFileOutput("myBFE.txt", Context.MODE_PRIVATE);
            String curBeaconInfo;
            for(BeaconFoundEvent curBFE:savedBFEList){
                    Beacon curBeacon = curBFE.getBeaconFound();
                    curBeaconInfo = curBeacon.getId1()+","
                            +curBeacon.getId2()+","
                            +curBeacon.getId3()+","
                            +curBFE.getLastLat()+","
                            +curBFE.getLastLong()+","
                            +curBFE.getLastTime()
                            +"\n";
                fileOutput.write(curBeaconInfo.getBytes());
            }
            fileOutput.close();
        }catch(SecurityException se){}
        catch (Exception e){}
    }

    private ArrayList<Beacon> getSavedBeaconsFromFile(){
        ArrayList<Beacon> acquiredSavedBeacons = new ArrayList<Beacon>();
        try{
            BufferedReader input = new BufferedReader(new InputStreamReader(
                    openFileInput("myBeacons.txt")));
            String curBeaconRow;
            while((curBeaconRow = input.readLine())!=null){
                String[] beaconIds;
                beaconIds = curBeaconRow.split(",");
                Beacon beaconToAdd = new Beacon.Builder().setId1(beaconIds[0]).setId2(beaconIds[1]).setId3(beaconIds[2]).build();
                acquiredSavedBeacons.add(beaconToAdd);
            }
        }catch(FileNotFoundException fnf){

        }catch(Exception e){}
        return acquiredSavedBeacons;
    }

    private void launchSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    private void launchAllDeviceList(){
        Intent intent = new Intent(this, AvailableBeaconScreen.class);
        intent.putExtra("savedBeaconList",((BeaconApplication)getApplication()).getSavedBeacons());
        startActivity(intent);
    }

    public void launchMoreBeaconInfo(Beacon b){
        Log.d("LaunchingMoreBeacon","Current beacon is "+b.getId1()+" : "+b.getId2()+" : "+b.getId3());
        BeaconFoundEvent eventToExplore= new BeaconFoundEvent();
        ArrayList<BeaconFoundEvent> curBeaconList = (ArrayList<BeaconFoundEvent>)((BeaconApplication)getApplication()).getFoundBeaconEvents().clone();
        for(BeaconFoundEvent curEvent:curBeaconList){
            Log.d("LaunchingMoreBeacon","Current beacon is "+b.getId1()+" : "+b.getId2()+" : "+b.getId3());
            Log.d("LaunchingMoreBeacon","Beacon to find is "+curEvent.getBeaconFound().getId1()+" : "+curEvent.getBeaconFound().getId2()+" : "+curEvent.getBeaconFound().getId3());
            if(b.equals(curEvent.getBeaconFound())){
                eventToExplore=curEvent;
            }
        }
        Intent intent = new Intent(this, BeaconInfoScreen.class);
        intent.putExtra("beaconInfo",eventToExplore);
        startActivity(intent);
    }

    private void launchAllMap(){
        Intent intent = new Intent(this,AllMap.class);
        startActivity(intent);
    }
    private void launchHeatMap(){
        Intent intent = new Intent(this,heatMapActivity.class);
        startActivity(intent);
    }

}

