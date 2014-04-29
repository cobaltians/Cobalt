//
//  HPZoomHybridViewController.m
//  HPHybridCatalog
//
//  Created by Diane on 17/04/13.
//  Copyright (c) 2013 Haploid. All rights reserved.
//

#import "HPZoomHybridViewController.h"

#define JSNameSetZoom           @"JSNameSetZoom"
#define kDefaultTextZoomLevel   @"textSizeZoomLevel"
#define defaultTextZoomLevel    10

@implementation HPZoomHybridViewController

@synthesize textSizeCurrentZoomLevel,
            textSizeMaxZoomLevel,
            textSizeMinZoomLevel,
            zoomInButton,
            zoomOutButton;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark LIFE CYCLE
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self setDelegate:self];
    [self.navigationController setNavigationBarHidden:YES];

    textSizeMaxZoomLevel = [NSNumber numberWithInt:20];
    textSizeMinZoomLevel = [NSNumber numberWithInt:5];
    NSUserDefaults * standardUserDefaults = [NSUserDefaults standardUserDefaults];
    NSInteger currentSize = [standardUserDefaults integerForKey:kDefaultTextZoomLevel];
    currentSize = (currentSize && currentSize >= textSizeMinZoomLevel.integerValue && textSizeMaxZoomLevel.integerValue >= currentSize) ? currentSize : defaultTextZoomLevel;
    textSizeCurrentZoomLevel =[NSNumber numberWithInteger:currentSize];
    [self setZoomLevelInWebView];
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark COBALT DELEGATE METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

- (BOOL)onUnhandledMessage:(NSDictionary *)message
{
    return NO;
}

- (BOOL)onUnhandledEvent:(NSString *)event withData:(NSDictionary *)data andCallback:(NSString *)callback
{
    return NO;
}

- (BOOL)onUnhandledCallback:(NSString *)callback withData:(NSDictionary *)data
{
    return NO;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

- (IBAction)onZoomInButton:(id)sender
{
    BOOL enabled = YES;
    
    textSizeCurrentZoomLevel = [NSNumber numberWithInt:([textSizeCurrentZoomLevel intValue] + 1)];
    if ([textSizeCurrentZoomLevel intValue] >= [textSizeMaxZoomLevel intValue]) {
        enabled = NO;
    }
    
    [zoomInButton setEnabled:enabled];
    [zoomOutButton setEnabled:YES];
    
    [self setZoomLevelInWebView];
    [self saveZoomLevelToUserDefaults:textSizeCurrentZoomLevel];
}

- (IBAction)onZoomOutButton:(id)sender
{
    BOOL enabled = YES;
    
    textSizeCurrentZoomLevel = [NSNumber numberWithInt:([textSizeCurrentZoomLevel intValue] - 1)];
    
    if ([textSizeCurrentZoomLevel intValue] <= [textSizeMinZoomLevel intValue]) {
        enabled = NO;
    }
    
    [zoomOutButton setEnabled:enabled];
    [zoomInButton setEnabled:YES];
    
    [self setZoomLevelInWebView];
    [self saveZoomLevelToUserDefaults:textSizeCurrentZoomLevel];
}

- (void)setZoomLevelInWebView
{
    [self sendEvent:JSNameSetZoom withData:[NSDictionary dictionaryWithObjectsAndKeys:textSizeCurrentZoomLevel, kJSValue, nil] andCallback:nil];
}


- (void)saveZoomLevelToUserDefaults:(NSNumber *)zoomLevel
{
    NSUserDefaults * standardUserDefaults = [NSUserDefaults standardUserDefaults];
    
    if (standardUserDefaults) {
        [standardUserDefaults setInteger:[zoomLevel integerValue] forKey:kDefaultTextZoomLevel];
        [standardUserDefaults synchronize];
    }
}

@end
