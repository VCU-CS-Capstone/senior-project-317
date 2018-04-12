package com.vcuseniordesign.bym;

import org.altbeacon.beacon.Beacon;


public class BeaconSaved {
    private Beacon curBeacon=null;
    private String beaconName=null;


    public BeaconSaved(Beacon givenBeacon, String givenName){
        curBeacon=givenBeacon;
        beaconName=givenName;
    }

    public Beacon getCurBeacon(){return curBeacon;}
    public String getBeaconName(){return beaconName;}
    public void setCurBeacon(Beacon newBeacon){curBeacon=newBeacon;}
    public void setBeaconName(String newName){beaconName=newName;}

}
