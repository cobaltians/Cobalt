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
int i = 0;
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
    //[self.navigationController setNavigationBarHidden:YES];
    
    dataAuto = @[@"quotes : it's working \"great\"",
               @"url &eactue;é&12;\n3#23:%20'\\u0020hop",
               @"{ obj_representation : \"test\"}",
               @"emoji \ue415 \\ue415 u{1f604}",
               @"https://cob.s3.amazonaws.com/abcd.jpg?AWSAccessKeyId=1&Expires=1401263985&Signature=xbE%2B49MCgE7/WTKqnvwQ3f4zYmg%3D"] ;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark COBALT DELEGATE METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

- (BOOL)onUnhandledEvent:(NSString *)event withData:(NSDictionary *)data andCallback:(NSString *)callback
{
    if ([event isEqualToString:addValues]) {
        NSInteger intValue = [[[data objectForKey:kJSValues] objectAtIndex:0] intValue];
        intValue += [[[data objectForKey:kJSValues] objectAtIndex:1] intValue];
        NSString * value =[NSString stringWithFormat:@"%li",(long)intValue];
        NSDictionary * result = [[NSDictionary alloc] initWithObjectsAndKeys:  value, @"result", nil];
        [self sendCallback:callback withData:result];
        
        return YES;
    }else if ([event isEqualToString:echo]) {
            [self sendCallback:callback withData:data];
        return YES;
    }  
    if ([event isEqualToString:@"testEmoji"]) {
        NSString * emojiString = [data objectForKey:@"str"];
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        [defaults setObject:emojiString forKey:@"emoji"];
        [defaults synchronize];
        NSString * emojiStringSend = [defaults objectForKey:@"emoji"];
        NSDictionary *emojiDictionary = @{@"result" : emojiStringSend};
        [self sendCallback:callback withData:emojiDictionary];
        return YES;
    }
    return NO;
}

- (BOOL)onUnhandledCallback:(NSString *)callback withData:(NSDictionary *)data
{
    if ([callback isEqualToString:addValues]) {
        NSString * value = [NSString stringWithFormat:@"result is : %@",[data objectForKey:kJSResult]];
        
        UIAlertView * alertView = [[UIAlertView alloc] initWithTitle:@"Result" message:value delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alertView show];
        
        return YES;
    }else if ([callback isEqualToString:echo]) {
        bool allEquals = true;
        NSLog(@"iOS Native : %@ vs %@",data ,[dataAuto objectAtIndex:i]);
        if (![[dataAuto objectAtIndex:i] isEqualToString:[NSString stringWithFormat:@"%@",data]]) {
            allEquals = false;
            NSLog(@"iOS Native : Error");
        }else{
            NSLog(@"iOS Native : Success");
        }
        i++;
        if (i>=[dataAuto count])
        {
            i=0;
            if (allEquals) {
                UIAlertView * alertView = [[UIAlertView alloc] initWithTitle:@"Success" message:@"All test passed ! No error" delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
                [alertView show];
            }else if (!allEquals){
                UIAlertView * alertView = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Some tests failed !s check logs." delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
                [alertView show];
            }
        }else{
            [self AutoTest:nil];
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
    NSLog(@"iOS Native : %@",[dataAuto objectAtIndex:i]);
    if ([dataAuto objectAtIndex:i]!=nil) {
        [self sendEvent:echo withData:[dataAuto objectAtIndex:i] andCallback:echo];
    }
    
}

@end
