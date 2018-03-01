package com.vcuseniordesign.bym;
import android.content.Context;
import android.util.Log;
import android.app.Application;
import android.content.Intent;

import org.altbeacon.beacon.Beacon;

import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;


public class BeaconApplication extends Application {
    private ArrayList<Beacon> savedBeacons = new ArrayList<Beacon>();
    private ArrayList<Beacon> foundBeacons = new ArrayList<Beacon>();
    private ArrayList<BeaconFoundEvent> foundBeaconEvents = new ArrayList<BeaconFoundEvent>();
    private ArrayList<BeaconSaved> savedBeaconsInfo = new ArrayList<BeaconSaved>();

    public ArrayList<Beacon> getSavedBeacons(){
        return savedBeacons;
    }
    public ArrayList<Beacon> getFoundBeacons(){
        return foundBeacons;
    }
    public ArrayList<BeaconFoundEvent> getFoundBeaconEvents(){return foundBeaconEvents;}
    public ArrayList<BeaconSaved> getSavedBeaconsInfo(){return savedBeaconsInfo;}

    public void setSavedBeacons(ArrayList<Beacon> newList){savedBeacons=newList;}
    public void setFoundBeacons(ArrayList<Beacon> newList){foundBeacons=newList;}
    public void setFoundBeaconEvents(ArrayList<BeaconFoundEvent> newList){foundBeaconEvents=newList;}
    public void setSavedBeaconsInfo(ArrayList<BeaconSaved> newList){savedBeaconsInfo=newList;}

    public void storeSavedBeaconsInFile(){
        try{
            FileOutputStream fileOutput = this.openFileOutput("myBeacons.txt", Context.MODE_PRIVATE);
            String curBeaconInfo;

            ArrayList<Beacon> savedBeaconCopy = (ArrayList<Beacon>) savedBeacons.clone();
            ArrayList<BeaconSaved> beaconInfoCopy = (ArrayList<BeaconSaved>) savedBeaconsInfo.clone();

            for(Beacon curBeacon:savedBeaconCopy){

                BeaconSaved currentBeaconInfo = null;

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

    public void storeFoundBeaconsInFile(){
        try{

            FileOutputStream fileOutput = this.openFileOutput("myBFE.txt", Context.MODE_PRIVATE);
            String curBeaconInfo;
            ArrayList<BeaconFoundEvent> savedBFEListCopy=(ArrayList<BeaconFoundEvent>)foundBeaconEvents.clone();

            for(BeaconFoundEvent curBFE:savedBFEListCopy){
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


    @Override
    public void onCreate(){
        super.onCreate();
        Intent beaconScanningService = new Intent(getApplicationContext(),BeaconTracker.class);
        startService(beaconScanningService);
    }

}
