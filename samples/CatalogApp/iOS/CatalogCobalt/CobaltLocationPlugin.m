//
//  CobaltLocationPlugin.m
//  Cobalt
//
//  Created by Haploid on 23/07/14.
//  Copyright (c) 2014 Haploid. All rights reserved.
//

#import "CobaltLocationPlugin.h"

@implementation CobaltLocationPlugin

- (id)init{
	if (self = [super init]) {
        _locationManager = [[CLLocationManager alloc] init];
        _locationManager.distanceFilter = kCLDistanceFilterNone; // whenever we move
        _locationManager.desiredAccuracy = kCLLocationAccuracyHundredMeters; // 100 m
        _locationManager.delegate = self;
        
        [_locationManager startUpdatingLocation];
    }
	return self;
}

- (void)onMessageFromCobaltController:(CobaltViewController *)viewController andData: (NSDictionary *)data {
    _callback = [data objectForKey: kJSCallback];
    _viewController = viewController;
    
    _sendToWeb = YES;
    
    if(_locationManager.location) {
        [self sendLocationToWeb: _locationManager.location];
    }
}

-(void)locationManager:(CLLocationManager *)manager didUpdateToLocation:(CLLocation *)newLocation fromLocation:(CLLocation *)oldLocation {
    [self sendLocationToWeb: newLocation];
}

-(void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error {
    [self sendLocationToWeb: nil];
}


- (void)sendLocationToWeb: (CLLocation *) location {
    if(!_sendToWeb)
        return;
    
    _sendToWeb = NO;
    
    NSDictionary * locationDict = nil;
    
    if(location)
        locationDict = @{ LONGITUDE : [NSNumber numberWithDouble: location.coordinate.longitude], LATITUDE: [NSNumber numberWithDouble: location.coordinate.latitude]};
    
    [_viewController sendCallback: _callback withData: locationDict];
    //[_locationManager stopUpdatingLocation];
}

@end
