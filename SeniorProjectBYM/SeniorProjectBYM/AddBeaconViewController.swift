//
//  AddBeaconViewController.swift
//  SeniorProjectBYM
//
//  Created by Jordan Mays-Rowland on 10/25/17.
//  Copyright © 2017 BYM. All rights reserved.
//

import UIKit
import os.log

class AddBeaconViewController: UIViewController {

    
    @IBOutlet weak var name: UITextField!
    @IBOutlet weak var major: UITextField!
    @IBOutlet weak var minor: UITextField!
    @IBOutlet weak var saveButton: UIBarButtonItem!
    
    var beacon: Beacon?
    
    override func viewDidLoad() {
        super.viewDidLoad()

        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    
    // MARK: - Navigation
    
    @IBAction func cancel(_ sender: UIBarButtonItem) {
        dismiss(animated: true, completion: nil)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        super.prepare(for: segue, sender: sender)
        
        guard let button = sender as? UIBarButtonItem, button === saveButton else {
            os_log("The save button was not pressed, cancelling", log: OSLog.default, type: .debug)
            return
        }
        
        let maj = Int(major.text!)
        let min = Int(minor.text!)
        
        beacon = Beacon(major: maj!, minor: min!)
    }
    
    /*
    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
