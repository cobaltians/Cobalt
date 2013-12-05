//
//  HPHybridViewController.m
//  HPHybridCatalog
//
//  Created by Diane on 11/04/13.
//  Copyright (c) 2013 Haploid. All rights reserved.
//

#import "HPHybridViewController.h"

#define JSNameTestCallback @"nameTestCallback"
#define JSNameTestCallbackAsync @"nameTestCallbackAsync"

@interface HPHybridViewController ()

@end

@implementation HPHybridViewController

@synthesize messageToSendToWebTextField;

NSOperationQueue *queue;
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark LIFE CYCLE
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
    
    queue = [[NSOperationQueue alloc] init];
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
                if([name isEqualToString:JSNameTestCallback])
                {
                    NSString *value = [dict objectForKey:kJSValue];
                    self.messageToSendToWebTextField.text = value;
                    
                    NSString *callbackID = [NSString stringWithFormat:@"%@",[dict objectForKey:kJSCallbackID]];
                    if(callbackID && callbackID.length >0)
                    {
                        [self changeNameForWebCallBack:callbackID withValue:value];
                    }
                    return YES;
                }
                else if([name isEqualToString:JSNameTestCallbackAsync])
                {
                    NSString *value = [dict objectForKey:kJSValue];
                    self.messageToSendToWebTextField.text = value;
                    
                    NSString *callbackID = [NSString stringWithFormat:@"%@",[dict objectForKey:kJSCallbackID]];
                    if(callbackID && callbackID.length >0)
                    {
                        [self changeNameForWebCallBackAsync:callbackID withValue:value];
                    }
                    return YES;
                }
            }
        }
        else if([type isEqualToString:JSTypeCallBack])
        {
            NSString *callbackID = [dict objectForKey:kJSCallbackID];
            if(callbackID && callbackID.length >0 && [callbackID isKindOfClass:[NSString class]])
            {
                if([callbackID isEqualToString:JSNameTestCallback])
                {
                    NSString *value = [dict objectForKey:kJSParams];
                    UIAlertView *a = [[UIAlertView alloc] initWithTitle:@"Toast JS callback" message:value delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
                    [a performSelectorOnMainThread:@selector(show) withObject:nil waitUntilDone:YES];
                    return YES;
                }
                else if([callbackID isEqualToString:JSNameTestCallbackAsync])
                {
                    NSString *value = [dict objectForKey:kJSParams];
                    UIAlertView *a = [[UIAlertView alloc] initWithTitle:@"Toast JS callback Async" message:value delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
                    [a performSelectorOnMainThread:@selector(show) withObject:nil waitUntilDone:YES];
                    return YES;
                }
            }
            
        }
    }
    return [super handleDictionarySentByJavaScript:dict];
}


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark METHODS TO EXECUTE FROM JS CALLS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

-(void) changeNameForWebCallBackAsync:(NSString *)callBackID withValue:(NSString *)value
{
    NSString *nValue = [NSString stringWithFormat:@"Je m'appelle %@",value];
    
    NSURL *url = [NSURL URLWithString:@"http://google.fr"];
    NSURLRequest *request = [NSURLRequest requestWithURL:url];
    
    [NSURLConnection sendAsynchronousRequest:request queue:queue completionHandler:^(NSURLResponse* response, NSData* data, NSError* error)
     {
         if(error)
         {
             NSLog(@"Connection failed ! Error - %@ %@",[error localizedDescription],[[error userInfo] objectForKey:NSURLErrorFailingURLStringErrorKey]);
         }
         
         if(data)
         {
             NSString *s = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
             if(!s)
                 s = [[NSString alloc] initWithData:data encoding:NSISOLatin1StringEncoding];
             
             //s = @"google loaded!";
             [self sendCallbackResponseWithID:callBackID andObject:[NSString stringWithFormat:@"%@ && %@",nValue,s]];
         }
     }];
    
}


-(void) changeNameForWebCallBack:(NSString *)callBackID withValue:(NSString *)value
{
    NSString *nValue = [NSString stringWithFormat:@"Je m'appelle %@",value];
    
    [self sendCallbackResponseWithID:callBackID andObject:nValue];
}


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark INTERACTIONS TO SEND TO JS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



- (IBAction)testCallback:(id)sender {
    NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:
                          JSTypeEvent, kJSType,
                          JSNameTestCallback, kJSName,
                          [NSArray arrayWithObjects:[NSNumber numberWithInt:51],[NSNumber numberWithFloat:42], nil],kJSValue,
                          JSNameTestCallback, kJSCallbackID,
                          
                          nil];
    [self executeScriptInWebView:self.webView WithDictionary:dict];
}

- (IBAction)testCallbackAsync:(id)sender {
    NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:
                          JSTypeEvent, kJSType,
                          JSNameTestCallbackAsync, kJSName,
                          [NSArray arrayWithObjects:@"Bonjour",@"Guillaume", nil],kJSValue,
                          JSNameTestCallbackAsync, kJSCallbackID,
                          
                          nil];
    [self executeScriptInWebView:self.webView WithDictionary:dict];
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark TEXTFIELD METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    NSString *text = textField.text;
    NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:
                          JSTypeLog, kJSType,
                          text,kJSValue,
                          nil];
    [self executeScriptInWebView:self.webView WithDictionary:dict];
    
    return YES;
}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark MEMORY MANAGEMENT
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
