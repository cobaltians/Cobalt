//
//  HPHybridViewController.h
//  HPHybridCatalog
//
//  Created by Diane on 11/04/13.
//  Copyright (c) 2013 Haploid. All rights reserved.
//

#import "CobaltViewController.h"

@interface HPHybridViewController : CobaltViewController<CobaltDelegate>

@property (strong, nonatomic) IBOutlet UITextField * messageToSendToWebTextField;

- (IBAction)testCallback:(id)sender;
- (IBAction)testCallbackAsync:(id)sender;

@end
