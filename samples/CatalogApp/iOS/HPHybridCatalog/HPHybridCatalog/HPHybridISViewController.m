//
//  HPHybridISViewController.m
//  HPHybridCatalog
//
//  Created by Diane on 25/04/13.
//  Copyright (c) 2013 Haploid. All rights reserved.
//

#import "HPHybridISViewController.h"

@interface HPHybridISViewController ()

@end

@implementation HPHybridISViewController

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
    
    [self loadContentInWebView:self.webView FromFileNamed:self.pageName atPath:RESSOURCE_PATH withRessourcesAtPath:RESSOURCE_PATH];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
