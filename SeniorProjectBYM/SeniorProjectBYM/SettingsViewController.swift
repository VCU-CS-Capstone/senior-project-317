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
<<<<<<< HEAD
        //GIDSignIn.sharedInstance().signOut()
        print("Passed")
=======
        print("SIGN OUT BUTTON")
        
        let firebaseAuth = Auth.auth()
        do {
            try firebaseAuth.signOut()
        } catch let signOutError as NSError {
            print ("Error signing out: %@", signOutError)
        }
        
        GIDSignIn.sharedInstance().signOut()
        
>>>>>>> fbeb639557f46cf9083026b67e1ed0ba6223199f
        performSegue(withIdentifier: "signedOutSegue", sender: self)
        print("Second")
    }
    

}
