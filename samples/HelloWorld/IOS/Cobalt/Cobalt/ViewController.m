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
    self.pageName=@"index.html";
    
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
