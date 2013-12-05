//
//  HPSimpleHybridViewController.m
//  HPHybridCatalog
//
//  Created by Diane on 17/04/13.
//  Copyright (c) 2013 Haploid. All rights reserved.
//

#import "HPSimpleHybridViewController.h"

@interface HPSimpleHybridViewController ()

@end

@implementation HPSimpleHybridViewController

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

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark WEBVIEW METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

-(BOOL)handleDictionarySentByJavaScript:(NSDictionary *)dict
{
    NSString *type = [dict objectForKey:kJSType];
    if(type && type.length >0 && [type isKindOfClass:[NSString class]])
    {
        if([type isEqualToString:JSTypeEvent])
        {
            NSString *name = [dict objectForKey:kJSName];
            if(name && [name isKindOfClass:[NSString class]] && name.length >0)
            {
                if([name isEqualToString:@"getBigData"])
                {
                    NSNumber *value = [dict objectForKey:kJSValue];
                    
                    NSString *callbackID = [NSString stringWithFormat:@"%@",[dict objectForKey:kJSCallbackID]];
                    NSArray *users = [self generateBigData:value];
                    if(callbackID && callbackID.length >0)
                    {
                        [self sendCallbackResponseWithID:callbackID andObject:users];
                    }
                    return YES;
                }
            }
        }
    }
    return [super handleDictionarySentByJavaScript:dict];

}

-(NSArray *)generateBigData:(NSNumber *)dataSize
{
    int i = 0;
    NSMutableArray *users = [NSMutableArray array];
    for(i = 0 ; i < dataSize.intValue ; i++)
    {
        NSString *name = i%2 == 0 ? @"Ploppy" : @"Snoopy";
        NSString *imageName = @"img/ic_launcher.png";
        NSNumber *age = i <= 100 ? [NSNumber numberWithInt:i] : [NSNumber numberWithFloat:i/100.0];
        NSDictionary *user = [[NSDictionary alloc] initWithObjectsAndKeys:name,@"username",age,@"userage",imageName,@"userimage", nil];
        
        [users addObject:user];
    }
    
    return users;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
