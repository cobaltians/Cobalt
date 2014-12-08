//
//  CobaltAbstractPlugin.h
//  Cobalt
//
//  Created by Haploid on 23/07/14.
//  Copyright (c) 2014 Haploid. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "CobaltViewController.h"

@interface CobaltAbstractPlugin : NSObject

@property (nonatomic, retain) NSMutableArray * viewControllersArray;

+ (CobaltAbstractPlugin *)sharedInstanceWithCobaltViewController: (CobaltViewController *)viewController;
- (void)onMessageFromCobaltController:(CobaltViewController *)viewController andData: (NSDictionary *)data;
- (void)onMessageFromWebLayerWithCobaltController:(CobaltViewController *)viewController andData: (NSDictionary *)data;
@end
