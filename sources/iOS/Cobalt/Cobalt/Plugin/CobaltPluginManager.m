//
//  CobaltPluginManager.m
//  Cobalt
//
//  Created by Haploid on 23/07/14.
//  Copyright (c) 2014 Haploid. All rights reserved.
//

#import "CobaltPluginManager.h"
#import "CobaltAbstractPlugin.h"

@implementation CobaltPluginManager


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark SINGLETON
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

static CobaltPluginManager * cobaltPluginManagerInstance = nil;

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
		if (cobaltPluginManagerInstance == nil) {
			cobaltPluginManagerInstance = [[self alloc] init];
		}
	}
	return cobaltPluginManagerInstance;
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
        _pluginsDictionary = [Cobalt getPluginsConfiguration];
        //_pluginsDictionary = [[NSMutableDictionary alloc] initWithContentsOfFile: [[NSBundle mainBundle] pathForResource:@"plugins" ofType:@"plist"]];
    }
	return self;
}

- (BOOL)onMessageFromCobaltViewController:(CobaltViewController *)viewController andData: (NSDictionary *)data {
    NSString * pluginName = [data objectForKey: kJSPluginName];
    BOOL isFromWebLayer = [[[data objectForKey:kJSData] objectForKey: kJSIsWebLayer] boolValue];
    
    if([pluginName isKindOfClass: [NSString class]]) {
        NSString * className = [[_pluginsDictionary objectForKey: pluginName] objectForKey: kIos];
        Class class = NSClassFromString(className);
        if(class)
        {
            CobaltAbstractPlugin * plugin = [class sharedInstanceWithCobaltViewController: viewController];
            if (!isFromWebLayer)
                [plugin onMessageFromCobaltController: viewController andData: data];
            else
                [plugin onMessageFromWebLayerWithCobaltController: viewController andData: data];
            
            return YES;
        }
        else {
            #if DEBUG_COBALT
            NSLog(@"\n***********\n%@ class not found\n***********\n", className);
            #endif
        }
    }
    
    return NO;
}


@end
