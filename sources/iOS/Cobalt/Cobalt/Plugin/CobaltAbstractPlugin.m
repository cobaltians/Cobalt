//
//  CobaltAbstractPlugin.m
//  Cobalt
//
//  Created by Haploid on 23/07/14.
//  Copyright (c) 2014 Haploid. All rights reserved.
//

#import "CobaltAbstractPlugin.h"
#import "Cobalt.h"

@implementation CobaltAbstractPlugin

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark SINGLETON
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

static CobaltAbstractPlugin * instance = nil;

//******************
// SHARED INSTANCE *
//******************
/*!
 @method		+ (WebServices *)sharedInstance
 @abstract		Returns the singleton instance of web services.
 @result		The singleton instance of web services.
 */
+ (CobaltAbstractPlugin *)sharedInstanceWithCobaltViewController: (CobaltViewController *)viewController {
	@synchronized(self) {
		if (instance == nil) {
			instance = [[self alloc] init];
		}
        
        [instance.viewControllersArray addObject: [NSValue valueWithNonretainedObject: viewController]];
	}
	return instance;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark INITIALISATION
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//********
// INIT  *
//********
//
// Description :
//
- (id)init{
	if (self = [super init]) {
        _viewControllersArray = [[NSMutableArray alloc] init];
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(viewControllerDeallocated:) name:viewControllerDeallocatedNotification object:nil];
    }
	return self;
}

- (void)onMessageFromCobaltController:(CobaltViewController *)viewController andData: (NSDictionary *)data {
}

- (void)viewControllerDeallocated:(NSNotification *)notification {
    CobaltViewController * viewController = [notification object];
    
    [instance.viewControllersArray removeObject: [NSValue valueWithNonretainedObject: viewController]];
}

- (void) dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self name:viewControllerDeallocatedNotification object:nil];
}

@end
