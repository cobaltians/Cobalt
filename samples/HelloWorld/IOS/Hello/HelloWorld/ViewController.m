//
//  ViewController.m
//  HelloWorld
//
//  Created by Sébastien Vitard on 20/04/2015.
//  Copyright (c) 2015 Cobaltians. All rights reserved.
//

#import "ViewController.h"

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    // Do any additional setup after loading the view, typically from a nib.
    [self setDelegate:self];
    [self.navigationController setNavigationBarHidden:YES];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark COBALT DELEGATE METHODS
#pragma mark -
/////////////////////////////////////////////////////////////////////////////////////////////////////////

- (BOOL)onUnhandledMessage:(NSDictionary *)message {
    return NO;
}

- (BOOL)onUnhandledEvent:(NSString *)event
                withData:(NSDictionary *)data
             andCallback:(NSString *)callback {
    return NO;
}

- (BOOL)onUnhandledCallback:(NSString *)callback
                   withData:(NSDictionary *)data {
    return NO;
}

@end
