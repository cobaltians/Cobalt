//
//  CallbacksController.h
//  HPHybridCatalog
//
//  Created by Diane on 11/04/13.
//  Copyright (c) 2013 Haploid. All rights reserved.
//

#import "CobaltViewController.h"

@interface CallbacksController : CobaltViewController<CobaltDelegate>

@property (nonatomic,retain) NSDictionary* dataAuto;
- (IBAction)DoSomeMaths:(id)sender;
- (IBAction)AutoTest:(id)sender;
@end
