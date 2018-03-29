package com.vcuseniordesign.bym;

import android.Manifest;
import android.annotation.TargetApi;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
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

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.*;

public class TagInfo extends AppCompatActivity /*implements BeaconConsumer */{
    TagInfo curScreen= this;
    TextView deviceInfo;
    ListView deviceList;
    ProgressBar loadingCircle;
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


        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(curScreen);
                builder.setMessage("Please turn on Bluetooth")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableBtIntent, 1);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                android.app.AlertDialog.Builder NotEnabledBuilder = new android.app.AlertDialog.Builder(curScreen);
                                NotEnabledBuilder.setMessage("Bluetooth not enabled. App will not function. Please restart with bluetooth enabled.")
                                        .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                NotEnabledBuilder.create().show();
                            }
                        });
                builder.create().show();
            }
        }

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(curScreen);
            dialog.setMessage("Please turn on location services.");
            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent enableLocationIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(enableLocationIntent, 1);
                    //get gps
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    android.app.AlertDialog.Builder NotEnabledBuilder = new android.app.AlertDialog.Builder(curScreen);
                    NotEnabledBuilder.setMessage("Location Services not enabled. App will not function. Please restart with Location Services enabled.")
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    NotEnabledBuilder.create().show();
                }
            });
            dialog.show();
        }

        /*
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
        */

        /*
        for(Beacon curBeacon: savedBeacons){
            foundBeaconList.add(new BeaconFoundEvent(curBeacon,curLat,curLong,System.currentTimeMillis()));
        }*/

        if(getIntent().getSerializableExtra("beaconToUnpair")!=null){
            BeaconFoundEvent beaconToUnpair = (BeaconFoundEvent) getIntent().getSerializableExtra("beaconToUnpair");
            Beacon beaconObjToCheck=beaconToUnpair.getBeaconFound();
            ArrayList<Beacon> savedBeaconCopy = (ArrayList<Beacon>) ((BeaconApplication)getApplication()).getSavedBeacons().clone();
            for(Beacon b: savedBeaconCopy){
                if (b.equals(beaconObjToCheck)){
                    ((BeaconApplication)getApplication()).getSavedBeacons().remove(b);
                }
            }
            ArrayList<BeaconFoundEvent> savedBFECopy = (ArrayList<BeaconFoundEvent>) ((BeaconApplication)getApplication()).getFoundBeaconEvents().clone();
            for(BeaconFoundEvent bfe:savedBFECopy){
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

        loadingCircle = (ProgressBar)findViewById(R.id.LoadingCircle);

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

                                final FirebaseDatabase db = FirebaseDatabase.getInstance();
                                final DatabaseReference newDBRef = db.getReference();

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

                                ArrayList<Beacon> savedBeaconCopy = (ArrayList<Beacon>) ((BeaconApplication)getApplication()).getSavedBeacons().clone();
                                for(Beacon curBeacon:savedBeaconCopy){
                                        newDBRef.child("claimedBeacons").child(curBeacon.getId2()+":"+curBeacon.getId3()).removeValue();
                                }

                                ((BeaconApplication)getApplication()).getSavedBeacons().clear();
                                ((BeaconApplication)getApplication()).getSavedBeaconsInfo().clear();
                                deviceListAdapter.clear();
                                deviceListAdapter.add(null);
                                db.goOffline();
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
        //((BeaconApplication)getApplication()).storeSavedBeaconsInFile();
        //storeSavedBeaconsInFile(((BeaconApplication)getApplication()).getSavedBeacons());
        ((BeaconApplication)getApplication()).storeFoundBeaconsInFile();
        //storeFoundBeaconsInFile(((BeaconApplication)getApplication()).getFoundBeaconEvents());
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

    /*
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
    }*/

    /*
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
    }*/

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
        Log.d("LaunchingMoreBeacon","Current beacon is "+b.getId2()+":"+b.getId3());
        BeaconFoundEvent eventToExplore= null;
        ArrayList<BeaconFoundEvent> curBeaconList = (ArrayList<BeaconFoundEvent>)((BeaconApplication)getApplication()).getFoundBeaconEvents().clone();
        for(BeaconFoundEvent curEvent:curBeaconList){
            Log.d("LaunchingMoreBeacon","Beacon to find is "+curEvent.getBeaconFound().getId2()+":"+curEvent.getBeaconFound().getId3());
            if(b.equals(curEvent.getBeaconFound())){
                eventToExplore=curEvent;
            }
        }
        if(eventToExplore==null){
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

            Query lastQuery = newDBRef.child("observations").child(b.getId2().toString()+":"+b.getId3().toString()).orderByKey().limitToLast(1);


            ValueEventListener getMostRecentValue = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot mostRecentEvent:dataSnapshot.getChildren()) {
                        double newLat = (double) (mostRecentEvent.child("latitude").getValue());
                        double newLong = (double) (mostRecentEvent.child("longitude").getValue());
                        long curTime = Long.parseLong(mostRecentEvent.getKey());
                        String combinedIds=mostRecentEvent.getRef().getParent().getKey();
                        String[] splitIds=combinedIds.split(":");
                        Beacon beaconToAdd=new Beacon.Builder().setId1("d0d3fa86-ca76-45ec-9bd9-6af4f6016926").setId2(splitIds[0]).setId3(splitIds[1]).build();
                        BeaconFoundEvent eventToExplore=new BeaconFoundEvent(beaconToAdd,newLat,newLong,curTime);
                        launchMoreInfoFromDB(eventToExplore);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
            lastQuery.addListenerForSingleValueEvent(getMostRecentValue);
            loadingCircle.setVisibility(View.VISIBLE);
            db.goOffline();

        }else {


            Intent intent = new Intent(this, BeaconInfoScreen.class);
            intent.putExtra("beaconInfo", eventToExplore);
            startActivity(intent);
        }
    }

    private void launchAllMap(){
        Intent intent = new Intent(this,AllMap.class);
        startActivity(intent);
    }
    private void launchHeatMap(){
        Intent intent = new Intent(this,heatMapActivity.class);
        startActivity(intent);
    }

    public void launchMoreInfoFromDB(BeaconFoundEvent beaconInfoToExplore){
        loadingCircle.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(this, BeaconInfoScreen.class);
        intent.putExtra("beaconInfo",beaconInfoToExplore);
        startActivity(intent);
    }

}

