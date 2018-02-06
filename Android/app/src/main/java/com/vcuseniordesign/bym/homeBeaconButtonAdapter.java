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


public class homeBeaconButtonAdapter extends ArrayAdapter<Beacon> {
    Context curContext;
    ArrayList<Beacon> listItems;
    TagInfo curTagScreen;
    public homeBeaconButtonAdapter(Context context, int textViewResourceID, ArrayList<Beacon> listOfItems){
        super(context,textViewResourceID);
        listItems = listOfItems;
        curContext=context;
        curTagScreen=(TagInfo) context;
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
                mainButton.setText("Major: " +curBeacon.getId2().toString()+"\n"+"Minor: "+curBeacon.getId3());
                mainButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        curTagScreen.launchMoreBeaconInfo(curBeacon);
                    }
                });
            }

        }
        if(curBeacon==null){
            final Button mainButton = (Button) v.findViewById(R.id.MainButton);
            if(mainButton!=null){
                mainButton.setText("No paired devices");
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
