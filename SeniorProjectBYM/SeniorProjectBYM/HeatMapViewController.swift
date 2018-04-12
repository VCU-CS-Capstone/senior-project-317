//
//  HeatMapViewController.swift
//  SeniorProjectBYM
//
//  Created by Jordan Mays-Rowland on 1/25/18.
//  Copyright © 2018 BYM. All rights reserved.
//

import UIKit
import GoogleMaps

class HeatMapViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        let camera = GMSCameraPosition.camera(withLatitude: 37.5488, longitude: -77.4527, zoom: 15.5)
        let mapView = GMSMapView.map(withFrame: CGRect.zero, camera: camera)
        view = mapView
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
