//
//  HPZoomHybridViewController.m
//  HPHybridCatalog
//
//  Created by Diane on 17/04/13.
//  Copyright (c) 2013 Haploid. All rights reserved.
//

#import "HPZoomHybridViewController.h"

#define kDefaultTextZoomLevel @"textSizeZoomLevel"
#define defaultTextZoomLevel 10

@interface HPZoomHybridViewController ()

@end

@implementation HPZoomHybridViewController
@synthesize textSizeCurrentZoomLevel,textSizeMaxZoomLevel,textSizeMinZoomLevel,zoomInButton,zoomOutButton;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self.navigationController setNavigationBarHidden:YES];
    
    //load content in webView
    [self loadContentInWebView:self.webView FromFileNamed:self.pageName atPath:RESSOURCE_PATH withRessourcesAtPath:RESSOURCE_PATH];

    self.textSizeMaxZoomLevel = [NSNumber numberWithInt:20];
    self.textSizeMinZoomLevel = [NSNumber numberWithInt:5];
    NSUserDefaults *standardUserDefaults = [NSUserDefaults standardUserDefaults];
    NSInteger currentSize = [standardUserDefaults integerForKey:kDefaultTextZoomLevel];
    currentSize =  (currentSize && currentSize >= self.textSizeMinZoomLevel.integerValue && self.textSizeMaxZoomLevel.integerValue >= currentSize) ? currentSize : defaultTextZoomLevel;
    self.textSizeCurrentZoomLevel =[NSNumber numberWithInteger:currentSize];
    [self setZoomLevelInWebView];
}



- (IBAction)onZoomInButton:(id)sender {
    BOOL enabled = YES;
    
    self.textSizeCurrentZoomLevel = [NSNumber numberWithInt:([textSizeCurrentZoomLevel intValue]+1)];
    if([textSizeCurrentZoomLevel intValue] >= [textSizeMaxZoomLevel intValue])
        enabled = NO;
    
    [self.zoomInButton setEnabled:enabled];
    [self.zoomOutButton setEnabled:YES];
    
    [self setZoomLevelInWebView];
    [self saveZoomLevelToUserDefaults:self.textSizeCurrentZoomLevel];
}


- (IBAction)onZoomOutButton:(id)sender {
    BOOL enabled = YES;
    
    self. textSizeCurrentZoomLevel = [NSNumber numberWithInt:([self.textSizeCurrentZoomLevel intValue]-1)];
    if([textSizeCurrentZoomLevel intValue] <= [textSizeMinZoomLevel intValue])
        enabled = NO;
    
    [self.zoomOutButton setEnabled:enabled];
    [self.zoomInButton setEnabled:YES];
    
    
    [self setZoomLevelInWebView];
    [self saveZoomLevelToUserDefaults:self.textSizeCurrentZoomLevel];
}

-(void) setZoomLevelInWebView
{
    NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:
                          JSTypeEvent, kJSType,
                          JSNameSetZoom, kJSName,
                          self.textSizeCurrentZoomLevel,kJSValue,
                          nil];
    [self executeScriptInWebView:self.webView WithDictionary:dict];
}


-(void)saveZoomLevelToUserDefaults:(NSNumber*)zoomLevel
{
    NSUserDefaults *standardUserDefaults = [NSUserDefaults standardUserDefaults];
    
    if (standardUserDefaults) {
        [standardUserDefaults setInteger:[zoomLevel integerValue] forKey:kDefaultTextZoomLevel];
        [standardUserDefaults synchronize];
    }
}




- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}



@end
