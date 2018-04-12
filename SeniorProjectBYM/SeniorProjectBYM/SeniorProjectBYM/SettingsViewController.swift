//
//  SettingsViewController.swift
//  SeniorProjectBYM
//
//  Created by Justin Kyle Yirka on 10/30/17.
//  Copyright © 2017 BYM. All rights reserved.
//

import UIKit

import Firebase
import GoogleSignIn

class SettingsViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    @IBAction func signOut(_ sender: UIButton) {
        //GIDSignIn.sharedInstance().signOut()
        print("Passed")
        performSegue(withIdentifier: "signedOutSegue", sender: self)
        print("Second")
    }
    

}
