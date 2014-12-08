//
//  CobaltAbstractPlugin.m
//  Cobalt
//
//  Created by Haploid on 23/07/14.
//  Copyright (c) 2014 Haploid. All rights reserved.
//

#import "CobaltAbstractPlugin.h"
#import "Cobalt.h"
#import <objc/runtime.h>

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark SINGLETON
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

static CobaltAbstractPlugin * cobaltPluginInstance;

@implementation CobaltAbstractPlugin

//******************
// SHARED INSTANCE *
//******************
/*!
 @method		+ (WebServices *)sharedInstance
 @abstract		Returns the singleton instance of web services.
 @result		The singleton instance of web services.
 */
+ (CobaltAbstractPlugin *)sharedInstanceWithCobaltViewController: (CobaltViewController *)viewController {
    CobaltAbstractPlugin * instance = (CobaltAbstractPlugin *)objc_getAssociatedObject(self, &cobaltPluginInstance);
    if( !instance ){
        instance = [[self alloc] init];
        
        objc_setAssociatedObject(self, &cobaltPluginInstance, instance, OBJC_ASSOCIATION_RETAIN);
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

- (void)onMessageFromWebLayerWithCobaltController:(CobaltViewController *)viewController andData: (NSDictionary *)data {

}


- (void)viewControllerDeallocated:(NSNotification *)notification {
    CobaltViewController * viewController = [notification object];
    
    [cobaltPluginInstance.viewControllersArray removeObject: [NSValue valueWithNonretainedObject: viewController]];
}

- (void) dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self name:viewControllerDeallocatedNotification object:nil];
}

@end
