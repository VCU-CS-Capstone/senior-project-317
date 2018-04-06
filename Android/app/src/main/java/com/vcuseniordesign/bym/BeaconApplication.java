package com.vcuseniordesign.bym;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.app.Application;
import android.content.Intent;

import org.altbeacon.beacon.Beacon;

import java.io.File;
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
            Log.d("WriteFile",Environment.getRootDirectory().getAbsolutePath()+"/"+"SeniorDesignBYM");

            /*File dir = new File(Environment.getRootDirectory().getPath()+"/"+"SeniorDesignBYM");
            try{
                if(dir.mkdir()) {
                    Log.d("WriteFile","Directory Created");
                } else {
                    Log.d("WriteFile","Directory not created");
                }
            }catch(Exception e){
                Log.d("WriteFile",e.getMessage());
            }

            final File newDir = new File(Environment.getRootDirectory().getPath()+"/"+"SeniorDesignBYM"+"/"+"myBeaconTest.txt");
            //Log.d("WriteFile",getFilesDir().getAbsolutePath());
            FileOutputStream fileOutput = new FileOutputStream(newDir);*/
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
            Log.d("WriteFile","We finished writing SavedBeacons to the file");
        }catch(SecurityException se){Log.d("WriteFile","There was a security failure writing saved Beacons");}
        catch (Exception e){Log.d("WriteFile","There was a failure writing saved Beacons: \n"+e.getMessage());}
    }

    public void storeFoundBeaconsInFile(){
        try{
            //Log.d("WriteFile",Environment.getDataDirectory().getPath());
            //final File newDir = new File(Environment.getRootDirectory().getPath()+"/"+"myBFE.txt");
            //Log.d("WriteFile",getFilesDir().getAbsolutePath());
            //FileOutputStream fileOutput = new FileOutputStream(newDir);
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
            Log.d("WriteFile","We finished writing FoundBeacons to the file");
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
