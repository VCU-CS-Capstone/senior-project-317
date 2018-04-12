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
import Firebase
import os.log

class DisplayBeaconViewController: UIViewController, CLLocationManagerDelegate {
    
    @IBOutlet weak var name: UILabel!
    @IBOutlet weak var majorLabel: UILabel!
    @IBOutlet weak var minorLabel: UILabel!
    @IBOutlet weak var map: MKMapView!
    @IBOutlet weak var ifNotSeen: UILabel!
    //@IBOutlet weak var deleteBeacon: UIBarButtonItem!
    
    var beacon : Beacon!
    var beacons = [Beacon]()
    var ref: DatabaseReference!
    var timer : Timer?
    let appDelegate = UIApplication.shared.delegate as! AppDelegate
    
    //var locationManager : CLLocationManager!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        ref = Database.database().reference()
        /*
        locationManager = CLLocationManager.init()
        locationManager.delegate = self
        locationManager.requestAlwaysAuthorization()
        */
        name.text = beacon.name
        majorLabel.text = String(beacon.major)
        minorLabel.text = String(beacon.minor)
        
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
        timer = Timer.scheduledTimer(timeInterval: 5, target: self, selector: #selector(self.backgroundAction), userInfo: nil, repeats: true)

        //startScanningForBeaconRegion(beaconRegion: getBeaconRegion())
    }
    
    func loadMap() {
       
        let beaconName = String(beacon.major) + ":" + String(beacon.minor)
        
        let lastQuery = self.ref.child("observations").child(beaconName).queryOrderedByKey().queryLimited(toLast: 1)
        
        var test = 0
        
        lastQuery.observeSingleEvent(of: .value, with:{(snapshot) in
            print("Pulling information")
            
            var lat = 0.0
            var long = 0.0
            var tim = 0
            let dateFormatter = DateFormatter()
            var message = ""
            
            let beaconFIR = snapshot.value as! NSDictionary
            let keys = beaconFIR.allKeys
            
            for time in keys{
                let recent = beaconFIR[time] as! NSDictionary
                tim = Int(String(describing: time))!
                lat = Double(String(describing: recent["latitude"]!))!
                long = Double(String(describing: recent["longitude"]!))!
                message = String(describing: recent["message"]!)
            }
            
            let time = Double(tim) * 0.001
            let date = NSDate(timeIntervalSince1970: TimeInterval(time))
            
            dateFormatter.dateFormat = "HH:mm:ss EEEE, MMMM dd, yyyy"
            //dateFormatter.dateStyle = CFDateFormatterStyle.fullStyle
            let convertedDate = dateFormatter.string(from: date as Date)
            self.ifNotSeen.text = convertedDate
            print(convertedDate)
            let beaconCoordinates = CLLocationCoordinate2DMake(lat, long)
            let mapSpan = MKCoordinateSpanMake(0.002, 0.002)
            let mapRegion = MKCoordinateRegionMake(beaconCoordinates, mapSpan)
            self.map.setRegion(mapRegion, animated: true)
            let beaconAnnotation = MKPointAnnotation()
            beaconAnnotation.coordinate = beaconCoordinates
            
            if(message != "empty"){
                beaconAnnotation.title = message
            }
            self.map.removeAnnotations(self.map.annotations)
            self.map.addAnnotation(beaconAnnotation)
            
            test = 1
        })

        
        //if beacon.latitude != nil && beacon.longitude != nil {
        if test == 0 {
            print("Query failed")
            let beaconCoordinates = CLLocationCoordinate2DMake(0, 0)
            let mapSpan = MKCoordinateSpanMake(0.002, 0.002)
            let mapRegion = MKCoordinateRegionMake(beaconCoordinates, mapSpan)
            
            map.setRegion(mapRegion, animated: true)
            
            let beaconAnnotation = MKPointAnnotation()
            beaconAnnotation.coordinate = beaconCoordinates
            
            map.addAnnotation(beaconAnnotation)
            
            //ifNotSeen.text = "Beacon has not yet been seen"
        }
    }
    
    @IBOutlet var deletePop: UIView!
    
    @IBOutlet weak var deleteBeac: UIButton!
    
    @IBAction func deleteBeacon(_ sender: Any) {
        self.view.addSubview(deletePop)
        deletePop.center = self.view.center
    }
    
    @IBAction func cancelDelete(_ sender: Any) {
        self.deletePop.removeFromSuperview()
    }
    
    @IBOutlet var editPop: UIView!
    
    @IBAction func editBeacon(_ sender: Any) {
        self.view.addSubview(editPop)
        self.newName.text = ""
        self.newName.addTarget(self, action: #selector(textField(_:)), for: .editingChanged)
        self.saveEnable.isEnabled = false
        editPop.center = self.view.center
    }
    
    @IBOutlet weak var newName: UITextField!
    
    @IBOutlet weak var saveEnable: UIButton!
    
    @IBAction func saveEdit(_ sender: Any) {
        self.beacon.name = newName.text
        self.name.text = newName.text
        let beaconIndex = self.beacons.index(of: self.beacon)
        self.beacons[beaconIndex!] = self.beacon
        let adbeaconIndex = self.appDelegate.beaconsSaved.index(of: self.beacon)
        self.appDelegate.beaconsSaved[adbeaconIndex!] = self.beacon
        self.editPop.removeFromSuperview()
    }
    
    @IBAction func cancelEdit(_ sender: Any) {
        self.editPop.removeFromSuperview()
    }
    
    @objc func textField(_ textField: UITextField) -> Bool {
        
        if checkInput() {
            self.saveEnable.isEnabled = true
        } else {
            self.saveEnable.isEnabled = false
        }
        
        return true
    }
    
    func checkInput() -> Bool {
        if (newName.text?.isEmpty)!{
            return false
        }
        return true
    }
    
    @objc func backgroundAction() {
        loadMap()
        print("Reloading Map")
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
       
        self.timer!.invalidate()
        self.timer = Timer()
        
        super.prepare(for: segue, sender: sender)
        
        guard let button = sender as? UIButton, button === deleteBeac else {
            os_log("Returning to Beacon List", log: OSLog.default, type: .debug)
            return
        }
        
        self.appDelegate.beaconsSaved = self.appDelegate.beaconsSaved.filter {$0 != beacon}
        
        beacons = beacons.filter {$0 != beacon}
        print(beacons)
        print("Deleted beacon")
        
        let beaconData = String(self.beacon.major) + ":" + String(self.beacon.minor)
        self.ref.child("claimedBeacons").child(beaconData).removeValue()
    }
}
