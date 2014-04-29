//
//  HPToastAlertViewController.h
//  HPHybridCatalog
//
//  Created by Diane on 23/04/13.
//  Copyright (c) 2013 Haploid. All rights reserved.
//

#import "CobaltViewController.h"

@interface HPToastAlertViewController : CobaltViewController<CobaltDelegate>

@property (strong, nonatomic) IBOutlet UILabel * messageLabel;

@end
