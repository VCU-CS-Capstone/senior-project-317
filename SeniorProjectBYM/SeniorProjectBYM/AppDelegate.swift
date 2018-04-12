//
//  AppDelegate.swift
//  SeniorProjectBYM
//
//  Created by Jordan Mays-Rowland on 10/3/17.
//  Copyright © 2017 BYM. All rights reserved.
//

import UIKit

<<<<<<< HEAD
import Firebase
import GoogleSignIn
import GoogleMaps
import CoreLocation

=======
>>>>>>> fbeb639557f46cf9083026b67e1ed0ba6223199f
@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate, CLLocationManagerDelegate{

    var window: UIWindow?
    var beaconsSaved : [Beacon] = []
    var beaconsSeen : [Beacon] = []
    var locationManager : CLLocationManager!
    var ref: DatabaseReference!
    var timer = Timer()

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
<<<<<<< HEAD
        UIApplication.shared.setMinimumBackgroundFetchInterval(1)
        //UIApplication.shared.setMinimumBackgroundFetchInterval(5)
        FirebaseApp.configure()
        GIDSignIn.sharedInstance().clientID = FirebaseApp.app()?.options.clientID
        GMSServices.provideAPIKey("AIzaSyDHhfgFeJAY_fr77rrUHsM3zSg_A4VYbgM")
        
        locationManager = CLLocationManager.init()
        locationManager.delegate = self
        locationManager.requestAlwaysAuthorization()
        
        ref = Database.database().reference()
        
        startScanningForBeaconRegion(beaconRegion: getBeaconRegion())
        
         timer = Timer.scheduledTimer(timeInterval: 5, target: self, selector: #selector(self.backgroundAction), userInfo: nil, repeats: true)
=======
>>>>>>> fbeb639557f46cf9083026b67e1ed0ba6223199f
        
        return true
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
     */
    
    @objc func backgroundAction() {
        locationManager.requestLocation()
        print("backgroundAction Called")
    }
    
    func applicationWillResignActive(_ application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
    }

    func applicationDidEnterBackground(_ application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
        
        startScanningForBeaconRegion(beaconRegion: getBeaconRegion())
        
        print("Hello from the background")
        
        
        //timer = Timer.scheduledTimer(timeInterval: 5, target: self, selector: #selector(self.backgroundAction), userInfo: nil, repeats: true)
    }

    func applicationWillEnterForeground(_ application: UIApplication) {
        // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
    }

    func applicationDidBecomeActive(_ application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    }

    func applicationWillTerminate(_ application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    }
    
}

