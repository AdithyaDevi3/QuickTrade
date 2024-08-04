//
//  SecondViewController.swift
//  t
//
//  Created by Adithya Devi on 8/4/24.
//

import UIKit

class SecondViewController: UIViewController {

    
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }
    
    @IBAction func SecondPressed(_ sender: Any) {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let secondController = storyboard.instantiateViewController(withIdentifier: "Second_Page")
        
        secondController.loadViewIfNeeded()
        secondController.view.backgroundColor = .systemBlue
        
        self.present(secondController, animated: true, completion: nil)
    }
    
    
    @IBAction func doneButtonTapped(_ sender: Any){
        dismiss(animated: true, completion: nil)
    }
    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}
