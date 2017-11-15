//
//  AddBeaconViewController.swift
//  SeniorProjectBYM
//
//  Created by Jordan Mays-Rowland on 10/25/17.
//  Copyright © 2017 BYM. All rights reserved.
//

import UIKit
import os.log

class AddBeaconViewController: UIViewController, UITextFieldDelegate {

    
    @IBOutlet weak var name: UITextField!
    @IBOutlet weak var major: UITextField!
    @IBOutlet weak var minor: UITextField!
    @IBOutlet weak var saveButton: UIBarButtonItem!
    
    var beacon: Beacon?
    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.name.delegate = self
        self.major.delegate = self
        self.minor.delegate = self
        
        self.navigationItem.rightBarButtonItem?.isEnabled = false
        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        
        if checkInput() {
            self.navigationItem.rightBarButtonItem?.isEnabled = true
        } else {
            self.navigationItem.rightBarButtonItem?.isEnabled = false
        }
        
        if textField == name {
            return true
        }
        
        let allowedCharacters = CharacterSet.decimalDigits
        let characterSet = CharacterSet(charactersIn: string)
        
        if allowedCharacters.isSuperset(of: characterSet) {
           // print("=================================")
            if !(textField.text?.isEmpty)! {
                var text = textField.text
                text?.append(string)
                if Int(text!)! < 0 || Int(text!)! > 65535 {
                    //print("==============Made it===============")
                    return false
                }
            }
        }
        return allowedCharacters.isSuperset(of: characterSet)
    }
    
    func checkInput() -> Bool {
        if (name.text?.isEmpty)! || (major.text?.isEmpty)! || (minor.text?.isEmpty)!{
            return false
        }
        return true
    }
    // MARK: - Navigation
    

    @IBAction func unwindToBeaconList(sender: UIStoryboardSegue) {
        if let sourceTableViewController = sender.source as? AddBeaconTableViewController, let beacon = sourceTableViewController.beacon{
            major.text = String(beacon.major)
            minor.text = String(beacon.minor)
        }
    }
    
    @IBAction func cancel(_ sender: UIBarButtonItem) {
        dismiss(animated: true, completion: nil)
    }

    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        super.prepare(for: segue, sender: sender)
        
        guard let button = sender as? UIBarButtonItem, button === saveButton else {
            os_log("The save button was not pressed, cancelling", log: OSLog.default, type: .debug)
            return
        }
        
        let nam = name.text
        let maj = Int(major.text!)
        let min = Int(minor.text!)
        
        beacon = Beacon(name: nam, major: maj!, minor: min!, latitude: nil, longitude: nil, color: "white")
    }
    
    /*
    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
