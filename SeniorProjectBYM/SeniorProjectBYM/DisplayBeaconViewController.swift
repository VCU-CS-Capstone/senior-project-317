//
//  DisplayBeaconViewController.swift
//  SeniorProjectBYM
//
//  Created by Jordan Mays-Rowland on 10/25/17.
//  Copyright © 2017 BYM. All rights reserved.
//

import UIKit
import CoreLocation
import MapKit

class DisplayBeaconViewController: UIViewController, CLLocationManagerDelegate {
    
    @IBOutlet weak var name: UILabel!
    @IBOutlet weak var map: MKMapView!
    @IBOutlet weak var ifNotSeen: UILabel!
    
    
    var beacon : Beacon!
    var locationManager : CLLocationManager!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        locationManager = CLLocationManager.init()
        locationManager.delegate = self
        locationManager.requestWhenInUseAuthorization()
        
        name.text = beacon.name
        
        let viewColor = String(beacon.color!)
        
        switch viewColor {
        case "red":
            self.view.backgroundColor = UIColor.red
        case "blue":
            self.view.backgroundColor = UIColor.blue
        case "yellow":
            self.view.backgroundColor = UIColor.yellow
        case "gray":
            self.view.backgroundColor = UIColor.gray
        case "green":
            self.view.backgroundColor = UIColor.green
        case "black":
            self.view.backgroundColor = UIColor.black
            name.textColor = UIColor.white
            ifNotSeen.textColor = UIColor.white
        default:           print("Color is white")
        }
        
        loadMap()
        //startScanningForBeaconRegion(beaconRegion: getBeaconRegion())
    }
    
    func loadMap() {
        if beacon.latitude != nil && beacon.longitude != nil {
            let beaconCoordinates = CLLocationCoordinate2DMake(beacon.latitude!, beacon.longitude!)
            let mapSpan = MKCoordinateSpanMake(0.002, 0.002)
            let mapRegion = MKCoordinateRegionMake(beaconCoordinates, mapSpan)
            
            map.setRegion(mapRegion, animated: true)
            
            let beaconAnnotation = MKPointAnnotation()
            beaconAnnotation.coordinate = beaconCoordinates
            beaconAnnotation.title = beacon.name
            
            map.addAnnotation(beaconAnnotation)
        } else {
            ifNotSeen.text = "Beacon has not yet been seen"
        }
    }
    /*
    func getBeaconRegion() -> CLBeaconRegion {
        let beaconRegion = CLBeaconRegion.init(proximityUUID: UUID.init(uuidString: "D0D3FA86-CA76-45EC-9BD9-6AF4F6016926")!, major: CLBeaconMajorValue(beacon.major), minor: CLBeaconMinorValue(beacon.minor), identifier: "com.BYM.SeniorProjectBYM.myRegion")
        return beaconRegion
    }
    
    func startScanningForBeaconRegion(beaconRegion: CLBeaconRegion) {
        print(beaconRegion)
        locationManager.startMonitoring(for: beaconRegion)
        locationManager.startRangingBeacons(in: beaconRegion)
    }

    func locationManager(_ manager: CLLocationManager, didRangeBeacons beacons: [CLBeacon], in region: CLBeaconRegion) {
        
        if beacons.count > 0 {
            found.text = "Yes"
            locationManager.requestLocation()
        } else {
            found.text = "No"
        }
        print("Ranging")
    }

    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        let locValue:CLLocationCoordinate2D = manager.location!.coordinate
        beacon.latitude = locValue.latitude
        beacon.longitude = locValue.longitude
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    */

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        
    }
    */

}
