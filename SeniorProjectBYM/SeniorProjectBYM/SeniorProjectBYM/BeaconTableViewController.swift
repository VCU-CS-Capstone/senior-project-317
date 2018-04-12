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
    let appDelegate = UIApplication.shared.delegate as! AppDelegate
    
    override func viewDidLoad() {
        super.viewDidLoad()

        navigationItem.leftBarButtonItem = editButtonItem
       
        if let savedBeacons = loadBeacons(){
            beacons += savedBeacons
            self.tableView.reloadData()
        }
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
        cell.name.text = String(beacon.name!)
        
        
        
        let cellColor = String(beacon.color!)
        
        switch cellColor {
        case "red":
            cell.backgroundColor = UIColor.red
        case "blue":
            cell.backgroundColor = UIColor.blue
        case "yellow":
            cell.backgroundColor = UIColor.yellow
        case "gray":
            cell.backgroundColor = UIColor.gray
        case "green":
            cell.backgroundColor = UIColor.green
        case "black":
            cell.backgroundColor = UIColor.black
            cell.name.textColor = UIColor.white
        default:           print("Color is white")
        }
        
        return cell
    }
    
    @IBAction func unwindToBeaconList(sender: UIStoryboardSegue) {
        if let sourceViewController = sender.source as? AddBeaconViewController, let beacon = sourceViewController.beacon{
            saveBeacons()
            let newIndexPath = IndexPath(row: beacons.count, section: 0)
            beacons.append(beacon)
            tableView.insertRows(at: [newIndexPath], with: .automatic)
        } else if let sourceViewController = sender.source as? DisplayBeaconViewController, let beaconArray = sourceViewController.beacons  as? [Beacon], let beacon = sourceViewController.beacon{
            if beaconArray.count == self.beacons.count{
                tableView.reloadData()
                return
            }
            print("Recieved callback")
            let deletedIndexPath = IndexPath(arrayLiteral: self.beacons.index(of: beacon)!)
            let path = IndexPath(row: self.beacons.index(of: beacon)!, section: 0)
            self.beacons = beaconArray
            print(path)
            tableView.deleteRows(at: [path], with: .fade)
            //tableView.reloadData()
            
            /*
            let newIndexPath = IndexPath(row: beacons.count, section: 0)
            tableView.insertRows(at: [newIndexPath], with: .automatic)
            */
            print(beacons)
        } else {
            self.beacons = self.appDelegate.beaconsSaved
            tableView.reloadData()
        }
        let adbeacons = self.appDelegate.beaconsSaved
        print("AppDelegate Beacon Count")
        print(adbeacons.count)
        saveBeacons()
        print(beacons)
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
            displayBeaconViewController.beacons = self.beacons
            
        default:
            fatalError("Unexpected Segue Identifier; \(String(describing: segue.identifier))")
        }
    }
 
    private func saveBeacons() {
        let isSuccessfulSave = NSKeyedArchiver.archiveRootObject(beacons, toFile: Beacon.ArchiveURL.path)
        print(Beacon.ArchiveURL.path)
        print("loadBeacons:")
        if isSuccessfulSave {
            os_log("Beacons successfully saved.", log: OSLog.default, type: .debug)
        } else {
            os_log("Failed to save beacons...", log: OSLog.default, type: .error)
        }
        
        let lBeacons = loadBeacons()
        print("lBeacons Count")
        print(lBeacons?.count)
    }
    
    private func loadBeacons() -> [Beacon]? {
        print(Beacon.ArchiveURL.path)
        let loadedBeacons = NSKeyedUnarchiver.unarchiveObject(withFile: Beacon.ArchiveURL.path) as? [Beacon]
        print("Beacon return amount")
        print(loadedBeacons?.count)
        return loadedBeacons
    }
}
