package com.vcuseniordesign.bym;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.altbeacon.beacon.Beacon;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *  Sign-in implemented using this tutorial: https://firebase.google.com/docs/auth/android/firebaseui
 */
public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SignInActivity"; //used in LOG calls

    private static final int RC_SIGN_IN = 123; //can be anything


    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_sign_in);

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        ((BeaconApplication)getApplication()).setSavedBeacons(getSavedBeaconsFromFile());
        ((BeaconApplication)getApplication()).setFoundBeaconEvents(getFoundBeaconsFromFile());
        ((BeaconApplication)getApplication()).setSavedBeaconsInfo(getSavedBeaconInfoFromFile());

        ArrayList<Beacon> savedBeaconCopy = (ArrayList<Beacon>)((BeaconApplication) getApplication()).getSavedBeacons().clone();
        ArrayList<BeaconSaved> beaconInfoCopy = (ArrayList<BeaconSaved>)((BeaconApplication) getApplication()).getSavedBeaconsInfo().clone();
        for(BeaconSaved curBeaconSaved: beaconInfoCopy){
            Log.d("LoadBeacons","Current BeaconSaved is: "+curBeaconSaved.getCurBeacon().getId2()+":"+curBeaconSaved.getCurBeacon().getId3());
        }
        for(Beacon curBeacon: savedBeaconCopy){
            Log.d("LoadBeacons","Current Beacon is: "+curBeacon.getId2()+":"+curBeacon.getId3());
        }

        signIn();
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

    private ArrayList<BeaconSaved> getSavedBeaconInfoFromFile(){
        ArrayList<BeaconSaved> acquiredSavedBeaconInfo = new ArrayList<BeaconSaved>();
        try{
            BufferedReader input = new BufferedReader(new InputStreamReader(
                    openFileInput("myBeacons.txt")));
            String curBeaconRow;
            while((curBeaconRow = input.readLine())!=null){
                String[] beaconIds;
                beaconIds = curBeaconRow.split(",");
                Beacon beaconToAdd = new Beacon.Builder().setId1(beaconIds[0]).setId2(beaconIds[1]).setId3(beaconIds[2]).build();
                acquiredSavedBeaconInfo.add(new BeaconSaved(beaconToAdd,beaconIds[3]));
            }
        }catch(FileNotFoundException fnf){

        }catch(Exception e){}
        return acquiredSavedBeaconInfo;
    }

    private ArrayList<BeaconFoundEvent> getFoundBeaconsFromFile(){
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

    private void signIn() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build() );

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                updateUI(true);
            } else {
                // Sign in failed, check response for error code
                updateUI(false);
            }
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.INVISIBLE);
            ((TextView)findViewById(R.id.sign_in_status)).setText(R.string.signed_in);

            Intent intent = new Intent(this, TagInfo.class);
            startActivity(intent);

        } else {
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);

            TextView statusTextView = findViewById(R.id.sign_in_status);

            if (isOffline()) {
                statusTextView.setVisibility(View.VISIBLE);
                statusTextView.setText(R.string.no_connection);
            } else {
                statusTextView.setVisibility(View.VISIBLE);
                statusTextView.setText(R.string.not_signed_in);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    /**
     * See: https://github.com/firebase/FirebaseUI-Android/blob/master/auth/src/main/java/com/firebase/ui/auth/KickoffActivity.java
     * Check if there is an active or soon-to-be-active network connection.
     *
     * @return true if there is no network connection, false otherwise.
     */
    private boolean isOffline() {
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        return !(manager != null
                && manager.getActiveNetworkInfo() != null
                && manager.getActiveNetworkInfo().isConnectedOrConnecting());
    }
}