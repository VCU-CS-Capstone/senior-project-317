package com.vcuseniordesign.bym;

import org.altbeacon.beacon.Beacon;

import java.io.Serializable;

public class BeaconFoundEvent implements Serializable {
    String beaconNickname = "empty";
    Beacon beaconFound;
    double lastLat;
    double lastLong;
    long lastTime;
    BeaconFoundEvent(){
        beaconNickname="empty";
        beaconFound=null;
        lastLat=0;
        lastLong=0;
        lastTime=0;
    }
    BeaconFoundEvent(Beacon b,double latitude,double longitude,long timeFound){
        beaconNickname="empty";
        beaconFound=b;
        lastLat=latitude;
        lastLong=longitude;
        lastTime=timeFound;
    }

    public String toString(){
        return beaconNickname+"\t"+beaconFound.getId2()+"\t"+beaconFound.getId3()+"\t"+lastLat+"\t"+lastLong+"\t"+lastTime;

    }

    public String getBeaconNickname(){return beaconNickname;}
    public Beacon getBeaconFound(){return beaconFound;}
    public double getLastLat(){return lastLat;}
    public double getLastLong(){return lastLong;}
    public long getLastTime(){return lastTime;}

    public void setBeaconNickname(String newName){beaconNickname=newName;}
    public void setBeaconFound(Beacon newBeacon){beaconFound=newBeacon;}
    public void setLastLat(double newLat){lastLat=newLat;}
    public void setLastLong(double newLong){lastLong=newLong;}
    public void setLastTime(long newTime){lastTime=newTime;}
    public void setEventFromLine(String readLine){
        String[] foundBeaconAttributes=readLine.split("\t");
        beaconNickname=foundBeaconAttributes[0];
        beaconFound=new Beacon.Builder().setId1("d0d3fa86-ca76-45ec-9bd9-6af4f6016926").setId2(foundBeaconAttributes[1]).setId3(foundBeaconAttributes[2]).build();

    }

}
