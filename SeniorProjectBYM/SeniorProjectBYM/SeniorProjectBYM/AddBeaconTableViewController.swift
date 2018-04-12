//
//  AddBeaconTableViewController.swift
//  SeniorProjectBYM
//
//  Created by Jordan Mays-Rowland on 10/30/17.
//  Copyright © 2017 BYM. All rights reserved.
//

import UIKit
import CoreLocation
import Firebase

class AddBeaconTableViewController: UITableViewController {

    //var locationManager : CLLocationManager!
    var beacons : Array<Beacon> = []
    var beacon: Beacon?
    var ref: DatabaseReference!
    var timer : Timer?
    
    @IBOutlet weak var cancelButton: UIBarButtonItem!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        ref = Database.database().reference()
        
        self.backgroundAction()
        timer = Timer.scheduledTimer(timeInterval: 5, target: self, selector: #selector(self.backgroundAction), userInfo: nil, repeats: true)

    }
    
    @objc func backgroundAction() {
        let appDelegate = UIApplication.shared.delegate as! AppDelegate
        self.beacons = appDelegate.beaconsSeen
        //var unownedBeacons : Array<Beacon> = []
        let lastQuery = self.ref.child("claimedBeacons").queryOrderedByKey()
        
        lastQuery.observeSingleEvent(of: .value, with: {(snapshot) in
            print("Pulling Information")
            
            let claimedFIR = snapshot.value as! NSDictionary
            let claimedBeacons = claimedFIR.allKeys
            
            for claimedBeacon in claimedBeacons{
                let beacArray = String(describing: claimedBeacon).components(separatedBy: ":")
                let beacMajor = Int(beacArray[0])
                print(beacMajor)
                let beacMinor = Int(beacArray[1])
                print(beacMinor)
                let beac = Beacon(major: beacMajor!, minor: beacMinor!)
                if self.beacons.contains(beac){
                    print("Made it to contains")
                    self.beacons.remove(at: self.beacons.index(of: beac)!)
                }
                print(claimedBeacon)
            }
            
            print("In Background")
            print("Beacons to add " + String(self.beacons.count))
            self.tableView.reloadData()
        })
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
        
        self.timer!.invalidate()
        self.timer = nil
        
        super.prepare(for: segue, sender: sender)
        
        //guard let addBeaconViewController = segue.destination as? AddBeaconViewController else {
          //  fatalError("Unexpected destination: \(segue.destination)")
        //}
        
        guard let button = sender as? UIBarButtonItem, button === cancelButton else {
            
            guard let selectedBeaconCell = sender as? AddBeaconTableViewCell else {
                fatalError("Unexpected sender: \(String(describing: sender))")
            }
            guard let indexPath = tableView.indexPath(for: selectedBeaconCell) else{
                fatalError("The selected cell is not being displayed by the table")
            }
            self.beacon = beacons[indexPath.row]
            
            return
        }
        
        //let selectedBeacon = beacons[indexPath.row]
        //addBeaconViewController.beacon = selectedBeacon
    }
    

}
