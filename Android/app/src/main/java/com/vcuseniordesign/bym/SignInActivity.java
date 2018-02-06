package com.vcuseniordesign.bym;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.altbeacon.beacon.Beacon;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Code based on tutorial at https://developers.google.com/identity/sign-in/android/sign-in
 *
 * Presents initial screen to user to sign-in using a Google account.
 *
 * onStart() provides silent sign-in if user has stored credentials.
 *
 * onClick() responds to Google sign-in button. Calls signIn() method.
 * onActivityResult() responds to asynchronous activity which signIn() launched.
 * UI is updated by updateUI() based on result.
 *
 * Still need to implement sign-out on home screen, and deal with cases of no connection.
 */
public class SignInActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = "SignInActivity"; //used in LOG calls
    private static final int RC_SIGN_IN = 9001; //Code to send to google API for *sign-in* requests.

    private GoogleApiClient mGoogleApiClient;
    private TextView mStatusTextView;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        // Views
        mStatusTextView = (TextView) findViewById(R.id.sign_in_status);

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        ((BeaconApplication)getApplication()).setSavedBeacons(getSavedBeaconsFromFile());
        ((BeaconApplication)getApplication()).setFoundBeaconEvents(getFoundBeaconsFromFile());

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
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

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideProgressDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            mStatusTextView.setText(R.string.signed_in);


            Intent intent = new Intent(this, TagInfo.class);
            startActivity(intent);

        } else {
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            mStatusTextView.setText(R.string.not_signed_in);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
}