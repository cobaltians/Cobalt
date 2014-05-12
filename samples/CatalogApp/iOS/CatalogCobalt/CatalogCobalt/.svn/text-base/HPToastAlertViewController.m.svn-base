//
//  HPToastAlertViewController.m
//  HPHybridCatalog
//
//  Created by Diane on 23/04/13.
//  Copyright (c) 2013 Haploid. All rights reserved.
//

#import "HPToastAlertViewController.h"
#import "iToast.h"

@interface HPToastAlertViewController ()

@end

@implementation HPToastAlertViewController
@synthesize messageLabel;

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
}


-(void) alertView:(UIAlertView *)alertView WithTag:(NSInteger)tag clickedButtonAtIndex:(NSInteger)buttonIndex
{
    [[iToast makeText:[NSString stringWithFormat:@"button index = %d, alertId = %d",buttonIndex,tag]] show];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewDidUnload {
    [self setMessageLabel:nil];
    [super viewDidUnload];
}
@end
