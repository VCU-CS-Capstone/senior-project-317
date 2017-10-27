//
//  TrackViewController.swift
//  SeniorProjectBYM
//
//  Created by Jordan Mays-Rowland on 10/12/17.
//  Copyright © 2017 BYM. All rights reserved.
//

import UIKit
import CoreLocation

class TrackViewController: UIViewController, CLLocationManagerDelegate {

    @IBOutlet weak var iBeaconFoundLabel: UILabel!
    @IBOutlet weak var proximityUUIDLabel: UILabel!
    @IBOutlet weak var majorLabel: UILabel!
    @IBOutlet weak var minorLabel: UILabel!
    @IBOutlet weak var accuracyLabel: UILabel!
    @IBOutlet weak var distanceLabel: UILabel!
    @IBOutlet weak var rssiLabel: UILabel!
    
    var locationManager : CLLocationManager!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        locationManager = CLLocationManager.init()
        locationManager.delegate = self
        locationManager.requestWhenInUseAuthorization()
        startScanningForBeaconRegion(beaconRegion: getBeaconRegion())
    }
    
    func getBeaconRegion() -> CLBeaconRegion {
        let beaconRegion = CLBeaconRegion.init(proximityUUID: UUID.init(uuidString: "D0D3FA86-CA76-45EC-9BD9-6AF4F6016926")!,
                                               identifier: "com.BYM.SeniorProjectBYM.myRegion")
        return beaconRegion
    }
    
    func startScanningForBeaconRegion(beaconRegion: CLBeaconRegion) {
        print(beaconRegion)
        locationManager.startMonitoring(for: beaconRegion)
        locationManager.startRangingBeacons(in: beaconRegion)
    }
    
    // Delegate Methods
    func locationManager(_ manager: CLLocationManager, didRangeBeacons beacons: [CLBeacon], in region: CLBeaconRegion) {
        let beacon = beacons.last
        
        if beacons.count > 0 {
            iBeaconFoundLabel.text = "Yes"
            proximityUUIDLabel.text = beacon?.proximityUUID.uuidString
            majorLabel.text = beacon?.major.stringValue
            minorLabel.text = beacon?.minor.stringValue
            accuracyLabel.text = String(describing: beacon?.accuracy)
            if beacon?.proximity == CLProximity.unknown {
                distanceLabel.text = "Unknown Proximity"
            } else if beacon?.proximity == CLProximity.immediate {
                distanceLabel.text = "Immediate Proximity"
            } else if beacon?.proximity == CLProximity.near {
                distanceLabel.text = "Near Proximity"
            } else if beacon?.proximity == CLProximity.far {
                distanceLabel.text = "Far Proximity"
            }
            rssiLabel.text = String(describing: beacon?.rssi)
        } else {
            iBeaconFoundLabel.text = "No"
            proximityUUIDLabel.text = ""
            majorLabel.text = ""
            minorLabel.text = ""
            accuracyLabel.text = ""
            distanceLabel.text = ""
            rssiLabel.text = ""
        }
        
        print("Ranging")
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
