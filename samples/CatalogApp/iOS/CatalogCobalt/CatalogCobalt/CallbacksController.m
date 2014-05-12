//
//  CallbacksController.m
//  HPHybridCatalog
//
//  Created by Diane on 11/04/13.
//  Copyright (c) 2013 Haploid. All rights reserved.
//

#import "CallbacksController.h"

#define addValues       @"addValues"
#define echo            @"echo"

@implementation CallbacksController
@synthesize dataAuto;
int i = 1;
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
    
    dataAuto = [NSDictionary dictionaryWithObjectsAndKeys:
                @"quotes : it's working \"great\"", @"1",
                @"url &eactue;é&12;\n3#23:%20'\\u0020hop", @"2",
                @"{ obj_representation : \"test\"}", @"3",
                @"emoji \ue415 \\ue415 u{1f604}", @"4",
                @"https://cob.s3.amazonaws.com/abcd.jpg?AWSAccessKeyId=1&Expires=1401263985&Signature=xbE%2B49MCgE7/WTKqnvwQ3f4zYmg%3D", @"5",
                nil];
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
        if (callback && callback.length > 0) {
            [self sendCallback:callback withData:result];
        }
        return YES;
    }else if ([event isEqualToString:echo]) {
        if (callback && callback.length > 0) {
            [self sendCallback:callback withData:data];
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
    }else if ([callback isEqualToString:echo]) {
        bool allEquals = true;
        if (i<[dataAuto count]) {
            NSLog(@"iOS Native : %@ vs %@",data ,[dataAuto objectForKey:[NSString stringWithFormat:@"%i",i]]);
            if ([[dataAuto objectForKey:[NSString stringWithFormat:@"%i",i]]isEqualToString:[NSString stringWithFormat:@"%@",data]]) {
                NSLog(@"iOS Native : Success");
            }else{
                allEquals = false;
                NSLog(@"iOS Native : Error");
            }
            i++;
            [self AutoTest:nil];
        }else {
            i=1;
            if (allEquals) {
                UIAlertView * alertView = [[UIAlertView alloc] initWithTitle:@"Success" message:@"All test passed ! No error" delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
                //[alertView performSelectorOnMainThread:@selector(show) withObject:nil waitUntilDone:YES];
                [alertView show];
            }else if (!allEquals){
                UIAlertView * alertView = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Some tests failed !s check logs." delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
                [alertView performSelectorOnMainThread:@selector(show) withObject:nil waitUntilDone:YES];
            }

        }
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
- (IBAction)AutoTest:(id)sender {
    NSLog(@"passer");
    NSLog(@"iOS Native : %@",[dataAuto objectForKey:[NSString stringWithFormat:@"%i",i]]);
    if ([dataAuto objectForKey:[NSString stringWithFormat:@"%i",i]]!=nil) {
        [self sendEvent:echo withData:[dataAuto objectForKey:[NSString stringWithFormat:@"%i",i]] andCallback:echo];
    }
    
}

@end
