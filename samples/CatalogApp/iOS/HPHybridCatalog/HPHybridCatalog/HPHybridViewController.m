//
//  HPHybridViewController.m
//  HPHybridCatalog
//
//  Created by Diane on 11/04/13.
//  Copyright (c) 2013 Haploid. All rights reserved.
//

#import "HPHybridViewController.h"

#define JSNameTestCallback      @"nameTestCallback"
#define JSNameTestCallbackAsync @"nameTestCallbackAsync"

@implementation HPHybridViewController

@synthesize messageToSendToWebTextField;

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
    if ([event isEqualToString:JSNameTestCallback]) {
        NSString * value = [data objectForKey:kJSValue];
        [messageToSendToWebTextField performSelectorOnMainThread:@selector(setText:) withObject:value waitUntilDone:YES];
        
        if (callback
            && callback.length > 0) {
            [self changeNameForWebCallBack:callback withValue:value];
        }
        
        return YES;
    }
    else if([event isEqualToString:JSNameTestCallbackAsync]) {
        NSString * value = [data objectForKey:kJSValue];
        [messageToSendToWebTextField performSelectorOnMainThread:@selector(setText:) withObject:value waitUntilDone:YES];
        
        if (callback
            && callback.length > 0) {
            [self changeNameForWebCallBackAsync:callback withValue:value];
        }
        
        return YES;
    }
    
    return NO;
}

- (BOOL)onUnhandledCallback:(NSString *)callback withData:(NSDictionary *)data
{
    if ([callback isEqualToString:JSNameTestCallback]) {
        NSString * value = [data objectForKey:kJSValue];
        UIAlertView * alertView = [[UIAlertView alloc] initWithTitle:@"Toast JS callback" message:value delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alertView performSelectorOnMainThread:@selector(show) withObject:nil waitUntilDone:YES];
        
        return YES;
    }
    else if ([callback isEqualToString:JSNameTestCallbackAsync]) {
        NSString * value = [data objectForKey:kJSValue];
        UIAlertView * alertView = [[UIAlertView alloc] initWithTitle:@"Toast JS callback Async" message:value delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alertView performSelectorOnMainThread:@selector(show) withObject:nil waitUntilDone:YES];
        
        return YES;
    }
    
    return NO;
}


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark METHODS TO EXECUTE FROM JS CALLS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

- (void)changeNameForWebCallBackAsync:(NSString *)callBackID withValue:(NSString *)value
{
    NSString * nValue = [NSString stringWithFormat:@"Je m'appelle %@", value];
    
    NSURL * url = [NSURL URLWithString:@"http://google.fr"];
    NSURLRequest * request = [NSURLRequest requestWithURL:url];
    
    [NSURLConnection sendAsynchronousRequest:request queue:queue completionHandler:^(NSURLResponse * response, NSData * data, NSError * error) {
        if (error) {
            NSLog(@"Connection failed! Error - %@ %@",  [error localizedDescription],
                                                        [[error userInfo] objectForKey:NSURLErrorFailingURLStringErrorKey]);
        }
        
        if (data) {
            NSString * string = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
            if (! string) {
                 string = [[NSString alloc] initWithData:data encoding:NSISOLatin1StringEncoding];
            }
            //string = @"google loaded!";
            NSDictionary * data = [NSDictionary dictionaryWithObjectsAndKeys:[NSString stringWithFormat:@"%@ && %@", nValue, string], kJSValue, nil];
            [self sendCallback:callBackID withData:data];
         }
    }];
}


- (void)changeNameForWebCallBack:(NSString *)callBackID withValue:(NSString *)value
{
    NSDictionary * data = [NSDictionary dictionaryWithObjectsAndKeys:[NSString stringWithFormat:@"Je m'appelle %@", value], kJSValue, nil];
    
    [self sendCallback:callBackID withData:data];
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark INTERACTIONS TO SEND TO JS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

- (IBAction)testCallback:(id)sender {
    NSArray * value = [NSArray arrayWithObjects:[NSNumber numberWithInt:51],[NSNumber numberWithFloat:42], nil];
    NSDictionary * data = [NSDictionary dictionaryWithObjectsAndKeys: value, kJSValue, nil];
    [self sendEvent:JSNameTestCallback withData:data andCallback:JSNameTestCallback];
}

- (IBAction)testCallbackAsync:(id)sender {
    NSArray * value = [NSArray arrayWithObjects:@"Bonjour", @"Guillaume", nil];
    NSDictionary * data = [NSDictionary dictionaryWithObjectsAndKeys: value, kJSValue, nil];
    [self sendEvent:JSNameTestCallbackAsync withData:data andCallback:JSNameTestCallbackAsync];
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark TEXTFIELD METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    
    NSDictionary * data = [NSDictionary dictionaryWithObjectsAndKeys:textField.text, kJSValue, nil];
    [self sendEvent:@"logThis" withData:data andCallback:nil];
    
    return YES;
}

@end
