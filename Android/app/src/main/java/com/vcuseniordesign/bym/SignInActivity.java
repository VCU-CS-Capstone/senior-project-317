package com.vcuseniordesign.bym;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.Manifest;

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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
        for(String appFile:fileList()) {
            File tempFile = new File(getFilesDir().getAbsoluteFile()+"/"+appFile);
            Log.d("LoadBeacons",appFile+ " Exists: "+tempFile.exists());
            Log.d("LoadBeacons",appFile+" is this long: "+Long.toString(tempFile.length()));
            Log.d("LoadBeacons","Can read: "+tempFile.canRead()+" Can write: "+tempFile.canWrite() );
        }
        ((BeaconApplication)getApplication()).setSavedBeacons(((BeaconApplication)getApplication()).getSavedBeaconsFromFile());
        ((BeaconApplication)getApplication()).setFoundBeaconEvents(((BeaconApplication)getApplication()).getFoundBeaconsFromFile());
        ((BeaconApplication)getApplication()).setSavedBeaconsInfo(((BeaconApplication)getApplication()).getSavedBeaconInfoFromFile());

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