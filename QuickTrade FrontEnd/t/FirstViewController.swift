//
//  ViewController.swift
//  t
//
//  Created by Adithya Devi on 8/4/24.
//

import UIKit

class FirstViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
    }

    @IBAction func HomePressed(_ sender: Any) {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let secondController = storyboard.instantiateViewController(withIdentifier: "Home_Screen")
        
        secondController.loadViewIfNeeded()
        secondController.view.backgroundColor = .systemRed
        
        self.present(secondController, animated: true, completion: nil)
    }
   
    @IBAction func ThirdPressed(_ sender: Any) {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let secondController = storyboard.instantiateViewController(withIdentifier: "Third_Page")
        
        secondController.loadViewIfNeeded()
        secondController.view.backgroundColor = .systemBrown
        
        self.present(secondController, animated: true, completion: nil)
    }
    
    
    
    
}

