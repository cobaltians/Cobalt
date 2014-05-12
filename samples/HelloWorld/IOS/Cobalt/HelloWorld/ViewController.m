//
//  ViewController.m
//  Cobalt
//
//  Created by Julien Gambier on 20/12/2013.
//  Copyright (c) 2013 Julien Gambier. All rights reserved.
//

#import "ViewController.h"

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    // Do any additional setup after loading the view from its nib.
    [self setDelegate:self];
    [self.navigationController setNavigationBarHidden:YES];
}

@end
