package com.vcuseniordesign.bym;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;


public class BeaconButtonAdapter extends ArrayAdapter<Beacon> {
    Context curContext;
    AvailableBeaconScreen curAvailableBeaconScreen;
    ArrayList<Beacon> listItems;
    public BeaconButtonAdapter(Context context, int textViewResourceID, ArrayList<Beacon> listOfItems){
        super(context,textViewResourceID);
        listItems = listOfItems;
        curContext=context;
        curAvailableBeaconScreen=(AvailableBeaconScreen)context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;

        if(v==null){
            LayoutInflater vi;
            vi = LayoutInflater.from(curContext);
            v=vi.inflate(R.layout.beacon_list_button,null);
        }
        final Beacon curBeacon = getItem(position);

        if(curBeacon != null){
            final Button mainButton = (Button) v.findViewById(R.id.MainButton);

            if(mainButton!=null){
                mainButton.setText(curBeacon.getId2().toString()+"\n"+System.currentTimeMillis());
                mainButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        curAvailableBeaconScreen.launchHomeDeviceList(curBeacon);
                    }
                });
            }

        }
        if(curBeacon==null){
            final Button mainButton = (Button) v.findViewById(R.id.MainButton);
            if(mainButton!=null){
                mainButton.setText("I am null");
                mainButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        //do stuff
                    }
                });
            }
        }

        return v;
    }
}
