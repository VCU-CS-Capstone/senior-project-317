package com.vcuseniordesign.bym;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;

public class AvailableBeaconScreen extends AppCompatActivity /*implements BeaconConsumer*/ {

    private BeaconManager beaconManager;
    private ArrayList<Beacon> beaconList = new ArrayList<Beacon>();
    //private ArrayList<Beacon> savedBeacons = new ArrayList<Beacon>();
    BeaconButtonAdapter deviceListAdapter;
    private LocationManager phoneLocationManager;
    private ListView availableDevices;
    private TextView availableDeviceDebug;
    private BroadcastReceiver updateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_beacon_screen);

        availableDeviceDebug = (TextView) findViewById(R.id.availableDeviceDebug);

        availableDevices = (ListView) findViewById(R.id.availableDevicesList);
        phoneLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        deviceListAdapter = new BeaconButtonAdapter(this, android.R.layout.simple_list_item_1, beaconList);
        availableDevices.setAdapter(deviceListAdapter);

        ArrayList<Beacon> foundBeaconCopy = (ArrayList<Beacon>) ((BeaconApplication)getApplication()).getFoundBeacons().clone();
        ArrayList<Beacon> savedBeaconCopy = (ArrayList<Beacon>) ((BeaconApplication)getApplication()).getSavedBeacons().clone();
        for (Beacon b : foundBeaconCopy) {
            if (!savedBeaconCopy.contains(b)) {
                beaconList.add(b);
                deviceListAdapter.add(b);
            }
        }

        if(getIntent().getSerializableExtra("savedBeaconList")!=null){
        //savedBeacons = (ArrayList<Beacon>) getIntent().getSerializableExtra("savedBeaconList");
        }
        updateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getStringExtra("UpdateIntent")!=null) {
                    Log.d("BaconUpdateReceiverABS","WE ARE UPDATING THE AVAILABLE BEACON LIST");
                    deviceListAdapter.clear();
                    ArrayList<Beacon> foundBeaconCopy = (ArrayList<Beacon>) ((BeaconApplication)getApplication()).getFoundBeacons().clone();
                    ArrayList<Beacon> savedBeaconCopy = (ArrayList<Beacon>) ((BeaconApplication)getApplication()).getSavedBeacons().clone();
                    for (Beacon b : foundBeaconCopy) {
                        final Beacon curB=b;
                        if(!savedBeaconCopy.contains(curB)){
                        beaconList.add(curB);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        deviceListAdapter.add(curB);
                                        availableDeviceDebug.append("\n Adding");

                                    } catch (SecurityException e) {
                                    }
                                }
                            });
                        }
                    }
                }
            }
        };
        registerReceiver(updateReceiver,new IntentFilter("com.vcuseniordesign.bym"));
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
                            final Beacon curB=b;
                                if (!beaconList.contains(b)&&!((BeaconApplication)getApplication()).getSavedBeacons().contains(b)&&b.getId1().toString().equals("d0d3fa86-ca76-45ec-9bd9-6af4f6016926")) {
                                    beaconList.add(b);
                                    final int deviceCount = beaconList.size() - 1;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                deviceListAdapter.add(curB);
                                                availableDeviceDebug.append("\n Adding");

                                            } catch (SecurityException e) {
                                            }
                                        }
                                    });

                                    //deviceInfo.append(b.getId1().toString()+" \n"+"TimeDetected" + System.currentTimeMillis()+" \n");
                                } else {
                                    if (!((BeaconApplication)getApplication()).getSavedBeacons().contains(b)&&b.getId1().toString().equals("d0d3fa86-ca76-45ec-9bd9-6af4f6016926")){
                                        final int curElement = beaconList.indexOf(b);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                deviceListAdapter.remove(deviceListAdapter.getItem(curElement));
                                                deviceListAdapter.insert(curB, curElement);
                                                availableDeviceDebug.append("\n Updating");

                                            } catch (SecurityException e) {
                                            }
                                        }
                                    });
                                    }
                                    //deviceInfo.append(b.getId1().toString()+" \n"+"TimeDetected" + System.currentTimeMillis()+" \n");
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

    public void launchHomeDeviceList(Beacon beaconToAdd){
        ((BeaconApplication)getApplication()).getSavedBeacons().add(beaconToAdd);
        Intent intent = new Intent(this, TagInfo.class);
        //intent.putExtra("savedBeaconList",savedBeacons);
        startActivity(intent);
    }
}
