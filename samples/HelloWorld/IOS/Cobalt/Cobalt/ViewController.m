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

    	// Do any additional setup after loading the view, typically from a nib.
    self.isPullToRefreshActive=YES;
<<<<<<< HEAD
    // Do any additional setup after loading the view from its nib.
    [self.navigationController setNavigationBarHidden:YES];
    
    
=======
    self.pageName=@"index.html";
>>>>>>> 0.3
    
    [super viewDidLoad];

}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (NSString *)ressourcePath
{
    return [NSString stringWithFormat:@"%@%@",[[NSBundle mainBundle] resourcePath],@"/www/"];
}

@end
