//
//  HPHybridViewController.m
//  HPHybridCatalog
//
//  Created by Diane on 11/04/13.
//  Copyright (c) 2013 Haploid. All rights reserved.
//

#import "HPHybridViewController.h"

#define addValues      @"addValues"

@implementation HPHybridViewController
NSOperationQueue * queue;

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
    
    queue = [[NSOperationQueue alloc] init];
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
    if ([event isEqualToString:addValues]) {
        NSInteger intValue = [[[data objectForKey:kJSValues] objectAtIndex:0] intValue];
        intValue += [[[data objectForKey:kJSValues] objectAtIndex:1] intValue];
        NSString * value =[NSString stringWithFormat:@"%i",intValue];
        NSDictionary * result = [[NSDictionary alloc] initWithObjectsAndKeys:  value, @"result", nil];
        if (callback
            && callback.length > 0) {
            [self sendCallback:callback withData:result];
        }
        return YES;
    }
    return NO;
}

- (BOOL)onUnhandledCallback:(NSString *)callback withData:(NSDictionary *)data
{
    if ([callback isEqualToString:addValues]) {
        NSString * value = [NSString stringWithFormat:@"result is : %@",[data objectForKey:kJSResult]];
        UIAlertView * alertView = [[UIAlertView alloc] initWithTitle:@"Result" message:value delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alertView performSelectorOnMainThread:@selector(show) withObject:nil waitUntilDone:YES];
        
        return YES;
    }
    
    return NO;
}



///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark INTERACTIONS TO SEND TO JS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


- (IBAction)DoSomeMaths:(id)sender{
    NSArray * value = [NSArray arrayWithObjects:[NSNumber numberWithInt:1],[NSNumber numberWithInt:3], nil];
    NSDictionary * data = [NSDictionary dictionaryWithObjectsAndKeys: value, kJSValues, nil];
    [self sendEvent:addValues withData:data andCallback:addValues];
}

@end
