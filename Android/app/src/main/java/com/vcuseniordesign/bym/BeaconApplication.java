package com.vcuseniordesign.bym;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.app.Application;
import android.content.Intent;

import org.altbeacon.beacon.Beacon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;


public class BeaconApplication extends Application implements Application.ActivityLifecycleCallbacks {
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

    public ArrayList<Beacon> getSavedBeaconsFromFile(){
        ArrayList<Beacon> acquiredSavedBeacons = new ArrayList<Beacon>();
        try{
            Log.d("LoadBeacons","We are about to load savedBeacons");
            BufferedReader input = new BufferedReader(new InputStreamReader(
                    openFileInput("myBeacons.txt")));
            String curBeaconRow;
            while((curBeaconRow = input.readLine())!=null){
                Log.d("LoadBeacons",curBeaconRow+" is the current line (savedBeacons) \n");
                String[] beaconIds;
                beaconIds = curBeaconRow.split(",");
                Beacon beaconToAdd = new Beacon.Builder().setId1(beaconIds[0]).setId2(beaconIds[1]).setId3(beaconIds[2]).build();
                acquiredSavedBeacons.add(beaconToAdd);
            }
            Log.d("LoadBeacons","We have loaded the savedBeacons");
        }catch(FileNotFoundException fnf){

        }catch(Exception e){}
        return acquiredSavedBeacons;
    }

    public ArrayList<BeaconSaved> getSavedBeaconInfoFromFile(){
        ArrayList<BeaconSaved> acquiredSavedBeaconInfo = new ArrayList<BeaconSaved>();
        try{
            Log.d("LoadBeacons","We are about to load savedBeaconsInfo");
            BufferedReader input = new BufferedReader(new InputStreamReader(
                    openFileInput("myBeacons.txt")));
            String curBeaconRow;
            while((curBeaconRow = input.readLine())!=null){
                Log.d("LoadBeacons",curBeaconRow+" is the current line (savedBeaconsInfo) \n");
                String[] beaconIds;
                beaconIds = curBeaconRow.split(",");
                Beacon beaconToAdd = new Beacon.Builder().setId1(beaconIds[0]).setId2(beaconIds[1]).setId3(beaconIds[2]).build();
                acquiredSavedBeaconInfo.add(new BeaconSaved(beaconToAdd,beaconIds[3]));
            }
            Log.d("LoadBeacons","We have loaded the savedBeaconsInfo");
        }catch(FileNotFoundException fnf){

        }catch(Exception e){}
        return acquiredSavedBeaconInfo;
    }

    public ArrayList<BeaconFoundEvent> getFoundBeaconsFromFile(){
        ArrayList<BeaconFoundEvent> acquiredSavedBFE = new ArrayList<BeaconFoundEvent>();
        try{
            BufferedReader input = new BufferedReader(new InputStreamReader(
                    openFileInput("myBFE.txt")));
            String curBeaconRow;
            while((curBeaconRow = input.readLine())!=null){
                String[] bfeInfo;
                bfeInfo = curBeaconRow.split(",");
                Beacon beaconToAdd = new Beacon.Builder().setId1(bfeInfo[0]).setId2(bfeInfo[1]).setId3(bfeInfo[2]).build();
                BeaconFoundEvent bfeToAdd = new BeaconFoundEvent(beaconToAdd,Double.parseDouble(bfeInfo[3]),Double.parseDouble(bfeInfo[4]),Long.parseLong(bfeInfo[5]));
                acquiredSavedBFE.add(bfeToAdd);
            }
        }catch(FileNotFoundException fnf){

        }catch(Exception e){}
        return acquiredSavedBFE;
    }


    @Override
    public void onCreate(){
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
        Intent beaconScanningService = new Intent(getApplicationContext(),BeaconTracker.class);
        startService(beaconScanningService);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        setSavedBeacons(getSavedBeaconsFromFile());
        setSavedBeaconsInfo(getSavedBeaconInfoFromFile());
        setFoundBeaconEvents(getFoundBeaconsFromFile());
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
