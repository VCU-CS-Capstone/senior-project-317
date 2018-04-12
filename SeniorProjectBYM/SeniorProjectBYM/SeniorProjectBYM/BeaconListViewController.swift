//
//  BeaconListViewController.swift
//  SeniorProjectBYM
//
//  Created by Jordan Mays-Rowland on 12/15/17.
//  Copyright © 2017 BYM. All rights reserved.
//

import UIKit
import CoreLocation
import Firebase


class BeaconListViewController: UIViewController, CLLocationManagerDelegate {

    var beaconsSeen : [Beacon] = []
    var locationManager : CLLocationManager!
    var ref: DatabaseReference!
    var timer = Timer()
    
    override func viewDidLoad() {
        super.viewDidLoad()

       // timer = Timer.scheduledTimer(timeInterval: 5, target: self, selector: #selector(self.backgroundAction), userInfo: nil, repeats: true)
        /*
        locationManager = CLLocationManager.init()
        locationManager.delegate = self
        locationManager.requestAlwaysAuthorization()
        
        ref = Database.database().reference()
        
        startScanningForBeaconRegion(beaconRegion: getBeaconRegion())
        */
    }
    /*
    func getBeaconRegion() -> CLBeaconRegion {
        let beaconRegion = CLBeaconRegion.init(proximityUUID: UUID.init(uuidString: "D0D3FA86-CA76-45EC-9BD9-6AF4F6016926")!, identifier: "com.BYM.SeniorProjectBYM.myRegion")
        return beaconRegion
    }
    
    func startScanningForBeaconRegion(beaconRegion: CLBeaconRegion) {
        print(beaconRegion)
        locationManager.startMonitoring(for: beaconRegion)
        locationManager.startRangingBeacons(in: beaconRegion)
    }
    
    func locationManager(_ manager: CLLocationManager, didRangeBeacons beacons: [CLBeacon], in region: CLBeaconRegion) {
        
        if !(beacons.isEmpty) {
            //beaconsSeen : [Beacon] = []
            //print(beacons.count)
            for var i in (0..<beacons.count) {
                let beaconMajor = Int(truncating: beacons[i].major)
                let beaconMinor = Int(truncating: beacons[i].minor)
                let foundBeacon = Beacon(major: beaconMajor, minor: beaconMinor)
                //print("\(foundBeacon.major) : \(foundBeacon.minor)")
                //print(i)
                
                if beaconsSeen.contains(foundBeacon){
                    //print("Contains -> \(foundBeacon.major) : \(foundBeacon.minor)")
                    i += 1
                    continue
                }
                
                beaconsSeen.append(foundBeacon)
                i += 1
            }
            //print(beaconsSeen.count)
            //locationManager.requestLocation()
        }
        
        //print("Ranging")
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        print("Requesting location")
        print("When requesting location  " + String(beaconsSeen.count))
        let locValue:CLLocationCoordinate2D = manager.location!.coordinate
        let timeStamp = String(Int(NSDate().timeIntervalSince1970 * 1000))
        //self.ref.child("heatmap").child(timeStamp).child("latitude").setValue(locValue.latitude)
        //self.ref.child("heatmap").child(timeStamp).child("longitude").setValue(locValue.longitude)
        for beacon in beaconsSeen {
            
            let beaconName = String(beacon.major) + ":" + String(beacon.minor)
            
            self.ref.child("observations").child(beaconName).child(timeStamp).child("latitude").setValue(locValue.latitude)
            self.ref.child("observations").child(beaconName).child(timeStamp).child("longitude").setValue(locValue.longitude)
            self.ref.child("observations").child(beaconName).child(timeStamp).child("message").setValue("empty")
        }
        beaconsSeen.removeAll()
        /*
        let timeStamp = String(Int(NSDate().timeIntervalSince1970 * 1000))
        self.ref.child("seniorprojectbym").child("observations").child("locations").child(timeStamp).child("latitude").setValue(locValue.latitude)
        self.ref.child("seniorprojectbym").child("observations").child("locations").child(timeStamp).child("longitude").setValue(locValue.longitude)
 */
    }
    
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print("error:: \(error.localizedDescription)")
    }
    /*
    func getBeaconsFound() -> Array<Beacon>{
        print("Sending over size " + String(beaconsSeen.count))
        return beaconsSeen
    }
    */*/
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
/*
    @objc func backgroundAction() {
        locationManager.requestLocation()
        print("In Background")
    }
 
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */
*/
}
