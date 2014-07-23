//
//  CobaltPluginManager.m
//  Cobalt
//
//  Created by Haploid on 23/07/14.
//  Copyright (c) 2014 Haploid. All rights reserved.
//

#import "CobaltPluginManager.h"
#import "CobaltAbstractPlugin.h"

//to be script generated
#import "CobaltLocationPlugin.h"

@implementation CobaltPluginManager


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark SINGLETON
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

static CobaltPluginManager * instance = nil;

//******************
// SHARED INSTANCE *
//******************
/*!
 @method		+ (WebServices *)sharedInstance
 @abstract		Returns the singleton instance of web services.
 @result		The singleton instance of web services.
 */
+ (CobaltPluginManager *)sharedInstance {
	@synchronized(self) {
		if (instance == nil) {
			instance = [[self alloc] init];
		}
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
        _pluginsDictionary = [[NSMutableDictionary alloc] init];
        
        //to be script generated
        [_pluginsDictionary setObject: [CobaltLocationPlugin class]  forKey: @"location"];
    }
	return self;
}

- (BOOL)onMessageFromCobaltViewController:(CobaltViewController *)viewController andData: (NSDictionary *)data {
    NSString * pluginName = [data objectForKey: kJSPluginName];
    
    if([pluginName isKindOfClass: [NSString class]]) {
        Class class = [_pluginsDictionary objectForKey: pluginName];
        if(class)
        {
            CobaltAbstractPlugin * plugin = [class sharedInstanceWithCobaltViewController: viewController];
            [plugin onMessageFromWebview: viewController andData: data];
            return YES;
        }
    }
    
    return NO;
}


@end
