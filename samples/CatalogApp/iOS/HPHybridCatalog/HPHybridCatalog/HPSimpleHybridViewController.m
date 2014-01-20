//
//  HPSimpleHybridViewController.m
//  HPHybridCatalog
//
//  Created by Diane on 17/04/13.
//  Copyright (c) 2013 Haploid. All rights reserved.
//

#import "HPSimpleHybridViewController.h"

@implementation HPSimpleHybridViewController

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
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

- (NSArray *)generateBigData:(NSNumber *)dataSize
{
    NSMutableArray * users = [NSMutableArray array];
    for (int i = 0 ; i < dataSize.intValue ; i++) {
        NSString * name = i%2 == 0 ? @"Ploppy" : @"Snoopy";
        NSString * imageName = @"img/ic_launcher.png";
        NSNumber * age = i <= 100 ? [NSNumber numberWithInt:i] : [NSNumber numberWithFloat:i/100.0];
        NSDictionary * user = [[NSDictionary alloc] initWithObjectsAndKeys: name, @"username",
                                                                            age, @"userage",
                                                                            imageName, @"userimage", nil];
        [users addObject:user];
    }
    
    return users;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark WEBVIEW METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

+ (NSString *)ressourcePath
{
    return RESSOURCE_PATH;
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
    if ([event isEqualToString:@"getBigData"]) {
        NSNumber * value = [data objectForKey:kJSValue];
        
        NSArray * users = [self generateBigData:value];
        
        if (callback
            && callback.length >0) {
            [self sendCallback:callback withData:users];
        }
        
        return YES;
    }
    
    return NO;
}

- (BOOL)onUnhandledCallback:(NSString *)callback withData:(NSDictionary *)data
{
    return NO;
}

@end
