//
//  BeaconTableViewController.swift
//  SeniorProjectBYM
//
//  Created by Jordan Mays-Rowland on 10/25/17.
//  Copyright © 2017 BYM. All rights reserved.
//

import UIKit
import os.log
import CoreLocation

class BeaconTableViewController: UITableViewController, CLLocationManagerDelegate {

    var beacons = [Beacon]()
    var beaconsSeen = [Beacon]()
    var locationManager : CLLocationManager!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        navigationItem.leftBarButtonItem = editButtonItem
        
        locationManager = CLLocationManager.init()
        locationManager.delegate = self
        locationManager.requestWhenInUseAuthorization()
        
        if let savedBeacons = loadBeacons(){
            if savedBeacons.count > 0 {
                print(beacons.count)
                beacons += savedBeacons
            } else {
                loadSampleBeacons()
            }
        } else {
            loadSampleBeacons()
        }
    }

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
            for var i in (0..<beacons.count) {
                let beacon = beacons[i]
                self.beaconsSeen[i] = Beacon(major: Int(beacon.major), minor: Int(beacon.minor))
                i += 1
            }
            locationManager.requestLocation()
        }
        
        print("Ranging")
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        let locValue:CLLocationCoordinate2D = manager.location!.coordinate
        for beacon in beaconsSeen {
            for var i in (0..<beacons.count) {
                if beacon.major == beacons[i].major && beacon.minor == beacons[i].minor {
                    beacons[i].latitude = locValue.latitude
                    beacons[i].longitude = locValue.longitude
                }
                i += 1
            }
        }
        saveBeacons()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return beacons.count
    }

    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cellIdentifier = "BeaconTableViewCell"
        
        guard let cell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier, for: indexPath) as? BeaconTableViewCell else {
            fatalError("The dequeued cell is not an instance of BeaconTableViewCell")
        }

        let beacon = beacons[indexPath.row]
        cell.major.text = String(beacon.major)
        cell.minor.text = String(beacon.minor)

        return cell
    }
    
    @IBAction func unwindToBeaconList(sender: UIStoryboardSegue) {
        if let sourceViewController = sender.source as? AddBeaconViewController, let beacon = sourceViewController.beacon{
            let newIndexPath = IndexPath(row: beacons.count, section: 0)
            beacons.append(beacon)
            tableView.insertRows(at: [newIndexPath], with: .automatic)
        }
        saveBeacons()
    }

    
    // Override to support conditional editing of the table view.
    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the specified item to be editable.
        return true
    }
    

    
    // Override to support editing the table view.
    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            // Delete the row from the data source
            beacons.remove(at: indexPath.row)
            saveBeacons()
            tableView.deleteRows(at: [indexPath], with: .fade)
        } else if editingStyle == .insert {
            // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
        }    
    }
    

    /*
    // Override to support rearranging the table view.
    override func tableView(_ tableView: UITableView, moveRowAt fromIndexPath: IndexPath, to: IndexPath) {

    }
    */

    /*
    // Override to support conditional rearranging of the table view.
    override func tableView(_ tableView: UITableView, canMoveRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the item to be re-orderable.
        return true
    }
    */

    
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        
        super.prepare(for: segue, sender: sender)
        
        
        switch(segue.identifier ?? "") {
            
        case "AddBeacon":
            os_log("Adding a new beacon.", log: OSLog.default, type: .debug)
            
        case "ShowDetail":

            guard let displayBeaconViewController = segue.destination as? DisplayBeaconViewController else {
                fatalError("Unexpected destination: \(segue.destination)")
            }
            guard let selectedBeaconCell = sender as? BeaconTableViewCell else {
                fatalError("Unexpected sender: \(String(describing: sender))")
            }
            guard let indexPath = tableView.indexPath(for: selectedBeaconCell) else{
                fatalError("The selected cell is not being displayed by the table")
            }
        
            let selectedBeacon = beacons[indexPath.row]
            displayBeaconViewController.beacon = selectedBeacon
            
        default:
            fatalError("Unexpected Segue Identifier; \(String(describing: segue.identifier))")
        }
    }
 

    private func loadSampleBeacons(){
        let beacon1 = Beacon(major: 4438, minor: 18145)
        beacon1.name = "lemon"
        beacon1.latitude = 37.545034
        beacon1.longitude = -77.448309
        let beacon2 = Beacon(major: 7767, minor: 5360)
        beacon2.name = "chair"
        let beacon3 = Beacon(major: 43751, minor: 58033)
        beacon3.name = "car"
        
        beacons += [beacon1, beacon2, beacon3]
    }
    
    private func saveBeacons() {
        let isSuccessfulSave = NSKeyedArchiver.archiveRootObject(beacons, toFile: Beacon.ArchiveURL.path)
        
        if isSuccessfulSave {
            os_log("Beacons successfully saved.", log: OSLog.default, type: .debug)
        } else {
            os_log("Failed to save beacons...", log: OSLog.default, type: .error)
        }
    }
    
    private func loadBeacons() -> [Beacon]? {
        return NSKeyedUnarchiver.unarchiveObject(withFile: Beacon.ArchiveURL.path) as? [Beacon]
    }
}
