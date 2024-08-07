//
//  ViewController.swift
//  t
//
//  Created by Adithya Devi on 8/4/24.
//

import UIKit

class FirstViewController: UIViewController {

    @IBOutlet weak var leading: NSLayoutConstraint!
    @IBOutlet weak var trailing: NSLayoutConstraint!
    
    @IBOutlet weak var segmentControlOutlet: UISegmentedControl!
    var menuOut = false
    
    private var skillLevel = ""
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
  
    
    @IBAction func menuTapped(_ sender: Any) {
        if menuOut == false{
            leading.constant = 150
            trailing.constant = -150
            menuOut = true
        }
        else{
            leading.constant = 0
            trailing.constant = 0
            menuOut = false
        }
        UIView.animate(withDuration: 0.2, delay: 0.0, options: .curveEaseIn, animations: {
            self.view.layoutIfNeeded()
        }){( animationComplete) in
            print("The animation is complete")
        }
    }
    @IBAction func HomePressed(_ sender: Any) {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let secondController = storyboard.instantiateViewController(withIdentifier: "Home_Screen")
        
        secondController.loadViewIfNeeded()
        secondController.view.backgroundColor = .systemRed
    }
    
    @IBAction func SearchPressed(_ sender: Any) {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let secondController = storyboard.instantiateViewController(withIdentifier: "Second_Page")
        
        secondController.loadViewIfNeeded()
        secondController.view.backgroundColor = .systemBlue
    }
    
    
    
    @IBAction func AnalyticsPressed(_ sender: Any) {
            let storyboard = UIStoryboard(name: "Main", bundle: nil)
            let secondController = storyboard.instantiateViewController(withIdentifier: "Third_Page")
    
            secondController.loadViewIfNeeded()
            secondController.view.backgroundColor = .systemBrown
    
//            self.present(secondController, animated: true, completion: nil)
    }
    
    @IBAction func SelectKnowledgeLevel(_ sender: UISegmentedControl) {
  
            switch segmentControlOutlet.selectedSegmentIndex {
            case 0:
                skillLevel = "Beginner"
            case 1:
                skillLevel = "Intermediate"
            case 2:
                skillLevel = "Advanced"
            default:
                skillLevel = "Beginner"
            }
        }
    
    
}

