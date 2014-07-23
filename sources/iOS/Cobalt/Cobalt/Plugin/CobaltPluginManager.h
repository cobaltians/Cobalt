//
//  CobaltPluginManager.h
//  Cobalt
//
//  Created by Haploid on 23/07/14.
//  Copyright (c) 2014 Haploid. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Cobalt.h"

@interface CobaltPluginManager : NSObject

@property (nonatomic, retain) NSMutableDictionary * pluginsDictionary;


+ (CobaltPluginManager *)sharedInstance;
- (BOOL)onMessageFromCobaltViewController:(CobaltViewController *)viewController andData: (NSDictionary *)data;

@end
