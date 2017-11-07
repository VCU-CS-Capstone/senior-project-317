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
    
    init(major: Int, minor: Int){
        self.major = major
        self.minor = minor
    }
    
    init(name: String?, major: Int, minor: Int, latitude: Double?, longitude: Double?) {
        self.name = name
        self.major = major
        self.minor = minor
        self.latitude = latitude
        self.longitude = longitude
    }
    
    struct PropertyKey {
        static let name = "name"
        static let major = "major"
        static let minor = "minor"
        static let latitude = "latitude"
        static let longitude = "longitude"
    }
    
    static let DocumentsDirectory = FileManager().urls(for: .documentDirectory, in: .userDomainMask).first!
    static let ArchiveURL = DocumentsDirectory.appendingPathComponent("beacons")
    
    func encode(with aCoder: NSCoder) {
        aCoder.encode(name, forKey: PropertyKey.name)
        aCoder.encode(major, forKey: PropertyKey.major)
        aCoder.encode(minor, forKey: PropertyKey.minor)
        aCoder.encode(latitude, forKey: PropertyKey.latitude)
        aCoder.encode(longitude, forKey: PropertyKey.longitude)
    }
    
    required convenience init?(coder aDecoder: NSCoder) {
        let name = aDecoder.decodeObject(forKey: PropertyKey.name) as? String
        guard let major = aDecoder.decodeObject(forKey: PropertyKey.major) as? Int else {
            os_log("Unable to decode the major for Beacon object", log: OSLog.default, type: .debug)
            return nil
        }
        guard let minor = aDecoder.decodeObject(forKey: PropertyKey.minor) as? Int else {
            os_log("Unable to decode the minor for Beacon object", log: OSLog.default, type: .debug)
            return nil
        }
        let latitude = aDecoder.decodeDouble(forKey: PropertyKey.latitude)
        let longitude = aDecoder.decodeDouble(forKey: PropertyKey.longitude)
        
        self.init(name: name, major: major, minor: minor, latitude: latitude, longitude: longitude)
        
    }
}
