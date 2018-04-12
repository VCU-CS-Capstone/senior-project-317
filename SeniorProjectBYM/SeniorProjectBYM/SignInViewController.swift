//
//  SignInViewController.swift
//  SeniorProjectBYM
//
//  Created by Jordan Mays-Rowland on 10/16/17.
//  Copyright © 2017 BYM. All rights reserved.
//

import UIKit

import Firebase
import GoogleSignIn

// We sign-in to Firebase *through* google sign-in.
// See https://firebase.google.com/docs/auth/ios/google-signin#3_authenticate_with_firebase
// There is a difference. We choose not to use the Firebase pre-built UI.... because documentation sucked

class SignInViewController: UIViewController, GIDSignInUIDelegate, GIDSignInDelegate {

    override func viewDidLoad() {
        super.viewDidLoad()

        FirebaseApp.configure()
        GIDSignIn.sharedInstance().clientID = FirebaseApp.app()?.options.clientID
        
        GIDSignIn.sharedInstance().delegate = self
        GIDSignIn.sharedInstance().uiDelegate = self
        
        // Uncomment to automatically sign in the user.
        GIDSignIn.sharedInstance().signInSilently()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    @available(iOS 9.0, *)
    func application(_ application: UIApplication, open url: URL, options: [UIApplicationOpenURLOptionsKey : Any]) -> Bool {
        return GIDSignIn.sharedInstance().handle(url,
                                                 sourceApplication:options[UIApplicationOpenURLOptionsKey.sourceApplication] as? String,
                                                 annotation: [:])
    }
    func application(_ application: UIApplication, open url: URL, sourceApplication: String?, annotation: Any) -> Bool {
        // Necessary to run on iOS 8 or older
        return GIDSignIn.sharedInstance().handle(url,
                                                 sourceApplication: sourceApplication,
                                                 annotation: annotation)
    }
    
    func sign(_ signIn: GIDSignIn!, didSignInFor user: GIDGoogleUser!, withError error: Error?) {
        if (error == nil) {
            guard let authentication = user.authentication else { return }
            let credential = GoogleAuthProvider.credential(withIDToken: authentication.idToken,
                                                           accessToken: authentication.accessToken)
            
            Auth.auth().signIn(with: credential) { (user, error) in
                if let error = error {
                    // handle Firebase Sign-in error
                    print("ERROR")
                } else {
                    // User has signed in and Firebase has authenaticated
                    self.performSegue(withIdentifier: "signedInSegue", sender: self)
                }
            }
        } else {
            // Handle sign-in error
        }
    }

}
