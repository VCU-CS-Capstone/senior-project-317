//
//  Beacon.swift
//  SeniorProjectBYM
//
//  Created by Jordan Mays-Rowland on 10/24/17.
//  Copyright © 2017 BYM. All rights reserved.
//

import UIKit
import os.log

class Beacon: NSObject, NSCoding {
    
    var name: String?
    var major: Int
    var minor: Int
    var latitude: Double?
    var longitude: Double?
    var color: String?
    
    init(major: Int, minor: Int){
        self.major = major
        self.minor = minor
        color = "white"
    }
    
    init(name: String?, major: Int, minor: Int){
        self.name = name
        self.major = major
        self.minor = minor
        color = "white"
    }
    
    init(name: String?, major: Int, minor: Int, latitude: Double?, longitude: Double?, color: String?) {
        self.name = name
        self.major = major
        self.minor = minor
        self.latitude = latitude
        self.longitude = longitude
        self.color = color
    }
    
    override func isEqual(_ object: Any?) -> Bool {
        if let otherBeacon = object as? Beacon {
            if self.major == otherBeacon.major && self.minor == otherBeacon.minor {
                return true
            } else {
                return false
            }
        } else {
            return false
        }
    }
    
    //Mark: NSCoding
    struct PropertyKey {
        static let name = "name"
        static let major = "major"
        static let minor = "minor"
        /*
        static let latitude = "latitude"
        static let longitude = "longitude"
        static let color = "color"
        */
    }
    
    //Mark: Archiving Path
    static let DocumentsDirectory = FileManager().urls(for: .documentDirectory, in: .userDomainMask).first!
    static let ArchiveURL = DocumentsDirectory.appendingPathComponent("beacons")
    
    func encode(with aCoder: NSCoder) {
        aCoder.encode(name, forKey: PropertyKey.name)
        aCoder.encode(major, forKey: PropertyKey.major)
        aCoder.encode(minor, forKey: PropertyKey.minor)
        /*
        aCoder.encode(latitude, forKey: PropertyKey.latitude)
        aCoder.encode(longitude, forKey: PropertyKey.longitude)
        aCoder.encode(color, forKey: PropertyKey.color)
         */
    }
    
    required convenience init?(coder aDecoder: NSCoder) {
        guard let name = aDecoder.decodeObject(forKey: PropertyKey.name) as? String else {
            os_log("Unable to decode the name for Beacon object", log: OSLog.default, type: .debug)
            return nil
        }
        let major = aDecoder.decodeInteger(forKey: PropertyKey.major)
        let minor = aDecoder.decodeInteger(forKey: PropertyKey.minor)
        /*
         guard let major = aDecoder.decodeObject(forKey: PropertyKey.major) as? Int else {
            os_log("Unable to decode the major for Beacon object", log: OSLog.default, type: .debug)
            return nil
        }
        guard let minor = aDecoder.decodeObject(forKey: PropertyKey.minor) as? Int else {
            os_log("Unable to decode the minor for Beacon object", log: OSLog.default, type: .debug)
            return nil
        }
         */
        /*
        let latitude = aDecoder.decodeDouble(forKey: PropertyKey.latitude)
        let longitude = aDecoder.decodeDouble(forKey: PropertyKey.longitude)
        let color = aDecoder.decodeObject(forKey: PropertyKey.color) as? String
        */
        self.init(name: name, major: major, minor: minor)
        
    }
}
