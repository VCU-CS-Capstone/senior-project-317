//
//  AddBeaconTableViewController.swift
//  SeniorProjectBYM
//
//  Created by Jordan Mays-Rowland on 10/30/17.
//  Copyright © 2017 BYM. All rights reserved.
//

import UIKit
import CoreLocation

class AddBeaconTableViewController: UITableViewController {

    var locationManager : CLLocationManager!
    var beacons = [Beacon]()
    var beacon: Beacon?
    
    override func viewDidLoad() {
        super.viewDidLoad()

        locationManager = CLLocationManager.init()
        locationManager.delegate = self as? CLLocationManagerDelegate
        locationManager.requestWhenInUseAuthorization()
        startScanningForBeaconRegion(beaconRegion: getBeaconRegion())
        
        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false

        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem
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
        
        if beacons.count > 0 {
            for var i in (0..<beacons.count) {
                let beacon = beacons[i]
                self.beacons[i] = Beacon(major: Int(beacon.major), minor: Int(beacon.minor))
                i += 1
            }
            self.tableView.reloadData()
        } else {
        }
        
        print("Ranging")
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return beacons.count
    }

    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cellIdentifier = "AddBeaconTableViewCell"
        
        guard let cell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier, for: indexPath) as? AddBeaconTableViewCell else {
            fatalError("The dequeued cell is not an instance of AddBeaconTableViewCell")
        }
        
        let beacon = beacons[indexPath.row]
        cell.major.text = String(beacon.major)
        cell.minor.text = String(beacon.minor)

        return cell
    }
    

    /*
    // Override to support conditional editing of the table view.
    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the specified item to be editable.
        return true
    }
    */

    /*
    // Override to support editing the table view.
    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            // Delete the row from the data source
            tableView.deleteRows(at: [indexPath], with: .fade)
        } else if editingStyle == .insert {
            // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
        }    
    }
    */

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

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        guard let addBeaconViewController = segue.destination as? AddBeaconViewController else {
            fatalError("Unexpected destination: \(segue.destination)")
        }
        guard let selectedBeaconCell = sender as? AddBeaconTableViewCell else {
            fatalError("Unexpected sender: \(String(describing: sender))")
        }
        guard let indexPath = tableView.indexPath(for: selectedBeaconCell) else{
            fatalError("The selected cell is not being displayed by the table")
        }

        let selectedBeacon = beacons[indexPath.row]
        addBeaconViewController.beacon = selectedBeacon
    }
    

}
