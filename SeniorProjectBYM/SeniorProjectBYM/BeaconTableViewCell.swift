//
//  BeaconTableViewCell.swift
//  SeniorProjectBYM
//
//  Created by Jordan Mays-Rowland on 10/24/17.
//  Copyright © 2017 BYM. All rights reserved.
//

import UIKit

class BeaconTableViewCell: UITableViewCell {
    

    @IBOutlet weak var name: UILabel!
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
