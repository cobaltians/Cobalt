//
//  CustomizedPulltoRefreshViewController.m
//  CatalogCobalt
//
//  Created by Haploid on 29/07/14.
//  Copyright (c) 2014 Haploid. All rights reserved.
//

#import "CustomizedPulltoRefreshViewController.h"

@interface CustomizedPulltoRefreshViewController ()

@end

@implementation CustomizedPulltoRefreshViewController

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
    // Do any additional setup after loading the view.
    
    [self customizeRefreshControlWithAttributedText: [[NSAttributedString alloc] initWithString:@"customized text"] andTintColor: [UIColor redColor]];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
