//
//  HPNativeBridgeViewController.m
//  HPNativeBridge
//
//  Created by Diane on 11/04/13.
//  Copyright (c) 2013 Haploid. All rights reserved.
//

#import "HPNativeBridgeViewController.h"
#import "iToast.h"
#import "HPNativeBridge.h"

#import "HPNativeBridgePullToRefreshViewController.h"
#import "HPPtrIsViewController.h"

#define haploidSpecialJSKey @"h@ploid#k&y"

//Conf File
#define confFileName            @"nativeBridge.conf"
#define kIosClassName           @"iosClassName"
#define kIosNibName             @"iosNibName"
#define kPullToRefreshActive    @"pullToRefresh"
#define kInfiniteScrollActive   @"infiniteScroll"


#define JSOnWebAlertDismissed @"onWebAlertDismissed"

@interface HPNativeBridgeViewController ()

@end

@implementation HPNativeBridgeViewController

@synthesize webView,activityIndicator,pageName,popUpWebview;

NSMutableDictionary *alertCallbacks;

NSMutableArray *toastsToShow;
BOOL toastIsShown;

NSString *popupPageName;

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
    [self customWebView];
    [self.webView setDelegate:self];
    
    toJavaScriptOperationQueue = [[NSOperationQueue alloc] init] ;
    [toJavaScriptOperationQueue setSuspended:YES];
    
    fromJavaScriptOperationQueue = [[NSOperationQueue alloc] init] ;
    [activityIndicator setHidesWhenStopped:YES];
    
    alertCallbacks = [[NSMutableDictionary alloc] init];
    toastsToShow = [[NSMutableArray alloc] init];
    
    [self.activityIndicator startAnimating];
    
    if([self.webView respondsToSelector:@selector(setKeyboardDisplayRequiresUserAction:)])
        [self.webView setKeyboardDisplayRequiresUserAction:NO];
    /*
     //if a pageName is already defined -> display it in the webview !
     //PB WITH PREFIX...
     if(self.pageName && self.pageName.length > 0)
     {
     //load content in webView
     NSString *contentToDisplay = [self getStringFromFileNamed:self.pageName atPath:RESSOURCE_PATH];
     [self.webView loadHTMLString:contentToDisplay baseURL:[NSURL URLWithString:RESSOURCE_PATH]];
     }
     //*/
    
    if(!self.pageName || self.pageName.length == 0)
    {
        self.pageName = @"index.html";
    }
    [self loadContentInWebView:self.webView FromFileNamed:self.pageName atPath:[self ressourcePath] withRessourcesAtPath:[self ressourcePath]];
}

- (void)viewDidUnload {
    self.webView.delegate = nil;
    [self setWebView:nil];
    [self setActivityIndicator:nil];
    toJavaScriptOperationQueue = nil;
    [super viewDidUnload];
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


-(NSString *)ressourcePath {
    return RESSOURCE_PATH;
}

-(void) customWebView
{
    /*
     This is the default implementation.
     */
}

-(void)loadContentInWebView:(UIWebView *)mWebView FromFileNamed:(NSString *)filename atPath:(NSString *)path withRessourcesAtPath:(NSString *)pathOfRessources
{
    /*
     NSString *contentToDisplay = [self getStringFromFileNamed:filename atPath:path];
     NSURL *u = (pathOfRessources) ? [NSURL fileURLWithPath:pathOfRessources] : nil;
     [self.webView loadHTMLString:contentToDisplay baseURL:u];
     //*/
    //*
    NSURL *baseURL = [NSURL fileURLWithPath:[path stringByAppendingPathComponent:filename]];
    NSURLRequest *request = [NSURLRequest requestWithURL:baseURL];
    [mWebView loadRequest:request];
    //*/
}


-(NSString *)getStringFromFileNamed:(NSString *)filename atPath:(NSString *)path
{
    NSURL *url = [NSURL fileURLWithPath:[NSString stringWithFormat:@"%@%@",path,filename] isDirectory:NO];
    NSError *error;
    NSString *stringFromFileAtURL = [[NSString alloc] initWithContentsOfURL:url encoding:NSUTF8StringEncoding error:&error];
    if (stringFromFileAtURL == nil) {
        NSLog(@"Error reading file at %@\n%@",url, [error localizedFailureReason]);
    }
    
    return stringFromFileAtURL;
}

-(id) parseJSONFromString:(NSString *) jsonString
{
    NSError *e;
    NSData* data = [jsonString dataUsingEncoding:NSUTF8StringEncoding];
    if(data)
    {
        return [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&e];
    }
    else return nil;
}

-(void) executeScriptInWebView:(UIWebView *)mWebView WithDictionary:(NSDictionary *)dict
{
    [toJavaScriptOperationQueue addOperationWithBlock:^{
        if([NSJSONSerialization isValidJSONObject:dict])
        {
            NSError *e;
            NSString *jsonMsg =[[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:dict options:0 error:&e] encoding:NSUTF8StringEncoding];
            
            //ensure there is no raw newline in any part of the json string.
            jsonMsg = [[jsonMsg componentsSeparatedByCharactersInSet:[NSCharacterSet newlineCharacterSet]] componentsJoinedByString:@""];

            NSString *javaScriptCommand = [NSString stringWithFormat:@"nativeBridge.execute(%@);",jsonMsg];
            
            // TDOD - pourquoi attendre la fin de l'exécution dans le main thread sachant que tu es dans un thread ???
            [mWebView performSelectorOnMainThread:@selector(stringByEvaluatingJavaScriptFromString:) withObject:javaScriptCommand waitUntilDone:NO];
        }
    }];
}

-(void) sendCallbackResponseWithID:(NSString *)callbackId andObject:(NSObject *)object
{
    if(callbackId && callbackId.length > 0)
    {
        NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:
                              JSTypeCallBack, kJSType,
                              callbackId, kJSCallbackID,
                              object,kJSParams,
                              nil];
        
        // pourquoi executer ça dans la main thread ??? (d'autant plus que tu y es déjà !!)
        //[self performSelectorOnMainThread:@selector(executeScriptInWebViewWithDictionary:) withObject:dict waitUntilDone:YES];
        [self executeScriptInWebView:self.webView WithDictionary:dict];
    }
    else NSLog(@"ERROR : web callbackID invalid (null or empty)");
    
}


-(BOOL)handleDictionarySentByJavaScript:(NSDictionary *)dict
{
    NSString *type = [dict objectForKey:kJSType];
    if(type && [type isKindOfClass:[NSString class]] && type.length > 0)
    {
        //LOG
        if([type isEqualToString:JSTypeLog])
        {
            NSString *value = [dict objectForKey:kJSValue];
            if(value && [value isKindOfClass:[NSString class]] && value.length >0)
            {
                NSLog(@"JS LOGS : %@",value);
                
                return YES;
            }
        }
        //EVENT
        else if([type isEqualToString:JSTypeEvent])
        {
            NSString *name = [dict objectForKey:kJSName];
            //EVENT = TOAST
            if(name && [name isKindOfClass:[NSString class]] &&name.length >0 && [name isEqualToString:JSNameToast])
            {
                NSString *value = [dict objectForKey:kJSValue];
                HPToast *t = (HPToast *)[[HPToast makeText:value] setGravity:iToastGravityBottom];
                [t setDelegate:self];
                if(toastIsShown)
                {
                    [toastsToShow addObject:t];
                }
                else
                {
                    [t performSelectorOnMainThread:@selector(show) withObject:nil waitUntilDone:YES];
                }
                return YES;
            }
        }
        //NAVIGATE
        else if([type isEqualToString:JSTypeNavigation])
        {
            NSString *navType = [dict objectForKey:kJSNavigationType];
            //PUSH
            if(navType && [navType isKindOfClass:[NSString class]] && navType.length >0)
            {
                if([navType isEqualToString:JSNavigationTypePush])
                {
                    NSString *vcId = [dict objectForKey:kJSNavigationClassId] ? [dict objectForKey:kJSNavigationClassId] : @"";
                    NSString *pageNamed = [dict objectForKey:kJSNavigationPageName] ? [dict objectForKey:kJSNavigationPageName] : @"";
                    NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:vcId,kJSNavigationClassId,pageNamed,kJSNavigationPageName, nil];
                    [self performSelectorOnMainThread:@selector(pushWebViewControllerWithDict:) withObject:dict waitUntilDone:YES];
                    
                    return YES;
                }
                //POP
                else if(navType && navType.length >0 && [navType isEqualToString:JSNavigationTypePop])
                {
                    [self performSelectorOnMainThread:@selector(popWebViewController) withObject:nil waitUntilDone:YES];
                    
                    return YES;
                }
                //MODALE
                else if(navType && navType.length >0 && [navType isEqualToString:JSNavigationTypeModale])
                {
                    NSString *vcId = [dict objectForKey:kJSNavigationClassId] ? [dict objectForKey:kJSNavigationClassId] : @"";
                    NSString *pageNamed = [dict objectForKey:kJSNavigationPageName] ? [dict objectForKey:kJSNavigationPageName] : @"";
                    NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:vcId,kJSNavigationClassId,pageNamed,kJSNavigationPageName, nil];
                    [self performSelectorOnMainThread:@selector(presentWebViewControllerWithDict:) withObject:dict waitUntilDone:YES];
                    
                    return YES;
                }
                //DISMISS
                else if(navType && navType.length >0 && [navType isEqualToString:JSNavigationTypeDismiss])
                {
                    [self performSelectorOnMainThread:@selector(dismissWebViewController) withObject:nil waitUntilDone:YES];
                    return YES;
                }
            }
        }
        //ALERT
        else if([type isEqualToString:JSTypeAlert])
        {
            [self showAlertWithDict:dict];
            return YES;
            
        }
        //WEB ALERT
        else if([type isEqualToString:JSTypeWebAlert])
        {
            NSString *name = [dict objectForKey:kJSName];
            if(name && [name isKindOfClass:[NSString class]] && name.length >0)
            {
                if([name isEqualToString:JSWebAlertShow])
                {
                    [self performSelectorOnMainThread:@selector(showPopUpWebviewWithDict:) withObject:dict waitUntilDone:YES];
                    return YES;
                }
                else if([name isEqualToString:JSWebAlertDismiss])
                {
                    [self performSelectorOnMainThread:@selector(dismissPopUpWebviewWithDict:) withObject:dict waitUntilDone:YES];
                    return YES;
                }
            }
        }
        //JS READY EVENT
        else if([type isEqualToString:JSNativeBridgeIsReady])
        {
            [toJavaScriptOperationQueue setSuspended:NO];
            NSLog(@"Received NativeBridgeIsReady");
        }
    }
    else NSLog(@"ERROR : type is undefined. Impossible to handle JavaScriptEvent !");
    return NO;
}


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark NAVIGATION METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

-(void) pushWebViewControllerWithDict:(NSDictionary *)dict
{
    NSString *viewControllerId = [dict objectForKey:kJSNavigationClassId] ? [dict objectForKey:kJSNavigationClassId] : @"";
    NSString *pageNameToShow = [dict objectForKey:kJSNavigationPageName]  ? [dict objectForKey:kJSNavigationPageName] : @"";
    HPNativeBridgeViewController *viewControllerToPush = [self getControllerFromId:viewControllerId];
    if(viewControllerToPush)
    {
        
        //set ressourcePath and fileName
        //viewControllerToPush.ressourcePath = self.ressourcePath;
        viewControllerToPush.pageName = pageNameToShow;
        
        
        //push the corresponding viewController
        [self.navigationController pushViewController:viewControllerToPush animated:YES];
    }
}


-(void) popWebViewController
{
    [self.navigationController popViewControllerAnimated:YES];
}


-(void) presentWebViewControllerWithDict:(NSDictionary *)dict
{
    NSString *viewControllerId = [dict objectForKey:kJSNavigationClassId];
    NSString *pageNameToShow = [dict objectForKey:kJSNavigationPageName];
    HPNativeBridgeViewController *viewControllerToPresent = [self getControllerFromId:viewControllerId];
    if(viewControllerToPresent)
    {
        //set ressourcePath and fileName
        //viewControllerToPresent.ressourcePath = self.ressourcePath;
        viewControllerToPresent.pageName = pageNameToShow;
        
        //present the corresponding viewController
        [self presentViewController:[[UINavigationController alloc] initWithRootViewController:viewControllerToPresent] animated:YES completion:nil];
    }
}


-(void) dismissWebViewController
{
    if(self.presentingViewController)
        [self.presentingViewController dismissViewControllerAnimated:YES completion:nil];
    else NSLog(@"Error : no controller is presented");
}


#warning UPDATE WITH STORYBOARDS  => [[UIStoryboard storyboardWithName:@"TT" bundle:nil] instantiateViewControllerWithIdentifier:@"TT"];
-(HPNativeBridgeViewController *) getControllerFromId:(NSString *) viewControllerId
{
#warning ERROR... -> impossible to preload the webview from here since RESSOURCE_PATH (defined in this prefix) is not the same as RESSOURCE_PATH (defined in app's prefix) and this prefix is used here... :/
    NSString *confToParse = [self getStringFromFileNamed:confFileName atPath: [self ressourcePath]];
    NSString *className,*nibName;
    BOOL pullToRefreshActive,infiniteScrollActive;
    
    if(confToParse && confToParse.length > 0)
    {
        NSDictionary *confDictionary = [self parseJSONFromString:confToParse];
        
        if(confDictionary && ![confDictionary isKindOfClass:[NSNull class]])
        {
            if(viewControllerId && ![viewControllerId isKindOfClass:[NSNull class]])
            {
                className = [[confDictionary objectForKey:viewControllerId] objectForKey:kIosClassName];
                nibName = [[confDictionary objectForKey:viewControllerId] objectForKey:kIosNibName];
                pullToRefreshActive = [[[confDictionary objectForKey:viewControllerId] objectForKey:kPullToRefreshActive] boolValue];
                infiniteScrollActive = [[[confDictionary objectForKey:viewControllerId] objectForKey:kInfiniteScrollActive] boolValue];
            }
            
            if(!className)
            {
                NSLog(@"WARNING : className for ID %@ not found. Look for default class ID",viewControllerId);
                className = [[confDictionary objectForKey:JSNavigationDefaultClassId] objectForKey:kIosClassName];
                nibName = [[confDictionary objectForKey:JSNavigationDefaultClassId] objectForKey:kIosNibName];
                pullToRefreshActive = [[[confDictionary objectForKey:JSNavigationDefaultClassId] objectForKey:kPullToRefreshActive] boolValue];
                infiniteScrollActive = [[[confDictionary objectForKey:viewControllerId] objectForKey:kInfiniteScrollActive] boolValue];
                
            }
            
            //if nibName is not defined in conf file -> use same as className !
            if(!nibName && className)
            {
                nibName = className;
            }
        }
        else
        {
            NSLog(@"ERROR : Syntax error in Conf file");
            return nil;
        }
        
    }
    else
    {
        NSLog(@"ERROR : Conf file may be missing or not at the root of RESSOURCE_PATH folder.");
        return nil;
    }
    
    if([self isValidControllerWithClassName:className andNibName:nibName])
    {
        if([NSClassFromString(className) isSubclassOfClass:[HPNativeBridgePullToRefreshViewController class]])
        {
            HPNativeBridgePullToRefreshViewController *c = [[NSClassFromString(className) alloc] initWithNibName:nibName bundle:[NSBundle mainBundle]];
            if (pullToRefreshActive) {
                c.isPullToRefreshActive = YES;
            }
            else c.isPullToRefreshActive = NO;
            return c;
        }
        else if([NSClassFromString(className) isSubclassOfClass:[HPPtrIsViewController class]])
        {
            HPPtrIsViewController *c = [[NSClassFromString(className) alloc] initWithNibName:nibName bundle:[NSBundle mainBundle]];
            if (pullToRefreshActive) {
                c.isPullToRefreshActive = YES;
            }
            else c.isPullToRefreshActive = NO;
            if (infiniteScrollActive) {
                c.isInfiniteScrollActive = YES;
            }
            else c.isInfiniteScrollActive = NO;
            return c;
        }
        
        return [[NSClassFromString(className) alloc] initWithNibName:nibName bundle:[NSBundle mainBundle]];
    }
    else
    {
        NSLog(@"ERROR : no view Controller named %@ was found for given ID %@. Nothing will happen...",className,viewControllerId);
        return nil;
    }
}


-(BOOL) isValidControllerWithClassName:(NSString *)className andNibName:(NSString *)nibName
{
    BOOL b1 = (className && NSClassFromString(className) && [NSClassFromString(className) isSubclassOfClass:[HPNativeBridgeViewController class]]);
    BOOL b2 = (nibName && nibName.length > 0 && [[NSBundle mainBundle] pathForResource:nibName ofType:@"nib"] != nil);
    
    if(!b1)
    {
        if(!className || (className && !NSClassFromString(className)))
        {
            NSLog(@"ERROR : classNotFound %@",className);
        }
        else if(className && NSClassFromString(className) && ![NSClassFromString(className) isSubclassOfClass:[HPNativeBridgeViewController class]])
        {
            NSLog(@"ERROR : impossible to show %@ for it does not inherit from HPNativeBridgeViewController",className);
        }
    }
    else if(!b2)
    {
        NSLog(@"ERROR : nibName %@ does not exist !",nibName);
    }
    
    return b1&&b2;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark ALERTS METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
-(void) showAlertWithDict:(NSDictionary *)dict
{
    if(dict)
    {
        NSString *title = ([dict objectForKey:kJSAlertTitle]  && [[dict objectForKey:kJSAlertTitle] isKindOfClass:[NSString class]])? [dict objectForKey:kJSAlertTitle] : @"";
        NSString *message = ([dict objectForKey:kJSAlertMessage]  && [[dict objectForKey:kJSAlertMessage] isKindOfClass:[NSString class]])? [dict objectForKey:kJSAlertMessage] : @"";
        NSArray *buttons = ([dict objectForKey:kJSAlertButtons] && [[dict objectForKey:kJSAlertButtons] isKindOfClass:[NSArray class]])? [dict objectForKey:kJSAlertButtons] : [NSArray array];
        NSString *receiverType = ([dict objectForKey:kJSAlertCallbackReceiver] && [[dict objectForKey:kJSAlertCallbackReceiver] isKindOfClass:[NSString class]])? [dict objectForKey:kJSAlertCallbackReceiver] : @"";
        
        UIAlertView *a;
        //CREATE ALERTVIEW WITH DELEGATE SET
        if(receiverType && receiverType.length > 0)
        {
            
            if(buttons && buttons.count >=1)
            {
                a = [[UIAlertView alloc] initWithTitle:title message:message delegate:self cancelButtonTitle:[buttons objectAtIndex:0] otherButtonTitles:nil];
            }
            else
            {
                a = [[UIAlertView alloc] initWithTitle:title message:message delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
            }
            
            NSNumber *alertId = [dict objectForKey:kJSAlertID] ? [dict objectForKey:kJSAlertID] : [NSNumber numberWithInteger:-1];
            [a setTag:alertId.integerValue];
            
            //save the callbackId to send it later to web
            if([receiverType isEqualToString:JSAlertCallbackReceiverWeb])
            {
                NSString *callbackId = [NSString stringWithFormat:@"%@",[dict objectForKey:kJSCallbackID]];
                if(callbackId && callbackId.length > 0)
                {
                    [alertCallbacks setObject:callbackId forKey:[NSString stringWithFormat:@"%d",alertId.intValue]];
                }
                else
                {
                    NSLog(@"WARNING : invalid callback name for alertView with webCallback (null or empty)");
                }
            }
            else if(![receiverType isEqualToString:JSAlertCallbackReceiverNative])
            {
                NSLog(@"WARNING : invalid callback receiver for alertView : %@",receiverType);
            }
            
        }
        //NO CALLBACK TO CALL ON CLICK ON BUTTONS
        else
        {
            if(buttons && buttons.count >=1)
            {
                a = [[UIAlertView alloc] initWithTitle:title message:message delegate:nil cancelButtonTitle:[buttons objectAtIndex:0] otherButtonTitles:nil];
            }
            else
            {
                a = [[UIAlertView alloc] initWithTitle:title message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
            }
        }
        
        //Add buttons
        int i = 0;
        for(i = 1 ; i < buttons.count ; i++)
        {
            [a addButtonWithTitle:[buttons objectAtIndex:i]];
        }
        [a performSelectorOnMainThread:@selector(show) withObject:nil waitUntilDone:YES];
    }
}

-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    NSInteger alertViewId = alertView.tag;
    
    if([alertCallbacks objectForKey:[NSString stringWithFormat:@"%d",alertViewId]] && [[alertCallbacks objectForKey:[NSString stringWithFormat:@"%d",alertViewId]] isKindOfClass:[NSString class]])
    {
        NSString *callbackId = [alertCallbacks objectForKey:[NSString stringWithFormat:@"%d",alertViewId]];
        
        NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:[NSNumber numberWithInteger:alertViewId],kJSAlertID,[NSNumber numberWithInteger:buttonIndex],kJSAlertButtonIndex, nil];
        
        [self sendCallbackResponseWithID:callbackId andObject:dict];
        
    }
    else
    {
        [self alertView:alertView WithTag:alertViewId clickedButtonAtIndex:buttonIndex];
    }
}

-(void) alertView:(UIAlertView *)alertView WithTag:(NSInteger)tag clickedButtonAtIndex:(NSInteger)buttonIndex
{
    /*
     * This is default implementation
     */
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark POP UP WEBVIEW
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

-(void) showPopUpWebviewWithDict:(NSDictionary *)dict
{
    if(self.popUpWebview && self.popUpWebview.superview)
    {
        [self.popUpWebview removeFromSuperview];
        [self.popUpWebview setDelegate:nil];
        self.popUpWebview = nil;
    }
    
    popupPageName = ([dict objectForKey:kJSWebAlertPageName]  && [[dict objectForKey:kJSWebAlertPageName] isKindOfClass:[NSString class]])? [dict objectForKey:kJSWebAlertPageName] : @"";
    NSNumber *fadeDuration = ([dict objectForKey:kJSWebAlertfadeDuration]  && [[dict objectForKey:kJSWebAlertfadeDuration] isKindOfClass:[NSNumber class]])? [dict objectForKey:kJSWebAlertfadeDuration] : [NSNumber numberWithFloat:0.3];
    
    self.popUpWebview = [[UIWebView alloc] initWithFrame:self.view.frame];
    
    [self.popUpWebview setDelegate:self];
    [self.popUpWebview setBackgroundColor:[UIColor clearColor]];
    [self.popUpWebview.scrollView setBounces:NO];
    if([self.popUpWebview respondsToSelector:@selector(setKeyboardDisplayRequiresUserAction:)])
        [self.popUpWebview setKeyboardDisplayRequiresUserAction:NO];
    
    [self loadContentInWebView:self.popUpWebview FromFileNamed:popupPageName atPath:[self ressourcePath] withRessourcesAtPath: [self ressourcePath]];
    self.popUpWebview.opaque = NO;
    [self.popUpWebview setAutoresizingMask:UIViewAutoresizingFlexibleWidth|UIViewAutoresizingFlexibleHeight];
    [self.popUpWebview setAlpha:0.0];
    [self.view addSubview:self.popUpWebview];
    [UIView animateWithDuration:fadeDuration.floatValue animations:^{
        [self.popUpWebview setAlpha:1.0];
    } completion:nil];
}


-(void) dismissPopUpWebviewWithDict:(NSDictionary *)dict
{
    NSNumber *fadeDuration = ([dict objectForKey:kJSWebAlertfadeDuration]  && [[dict objectForKey:kJSWebAlertfadeDuration] isKindOfClass:[NSNumber class]])? [dict objectForKey:kJSWebAlertfadeDuration] : [NSNumber numberWithFloat:0.3];
    
    [UIView animateWithDuration:fadeDuration.floatValue animations:^{
        [self.popUpWebview setAlpha:0.0];
    } completion:^(BOOL finished) {
        [self.popUpWebview removeFromSuperview];
        [self.popUpWebview setDelegate:nil];
        self.popUpWebview = nil;

        [self onWebPopupDismissed:popupPageName];
        popupPageName = nil;
    }];
}

-(void)onWebPopupDismissed:(NSString *)filename
{
   NSDictionary *d = [NSDictionary dictionaryWithObjectsAndKeys:JSTypeEvent,kJSType,JSOnWebAlertDismissed,kJSName,filename,kJSValue, nil];
    [self executeScriptInWebView:self.webView WithDictionary:d];
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark WEBVIEW DELEGATE
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType {
    
    NSString *requestString = [[[request URL] absoluteString] stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    //if requestString contains haploidSpecialJSKey -> the JSON received is extracted.
    NSRange r = [requestString rangeOfString:haploidSpecialJSKey];
    if(r.location  != NSNotFound)
    {
        NSString *jsonString = [requestString substringFromIndex:r.location+haploidSpecialJSKey.length];
        NSDictionary *jsonObj = [self parseJSONFromString:jsonString];
        
        [fromJavaScriptOperationQueue addOperationWithBlock:^{
            [self handleDictionarySentByJavaScript:jsonObj];
        }];
        
        [self sendSimpleAcquitmentToJS];
        
        return NO;
    }
    
    [self.activityIndicator startAnimating];
    
    // stop queues until we load the webview
    [toJavaScriptOperationQueue setSuspended:YES];
    //[fromJavaScriptOperationQueue setSuspended:YES];
    
    // Return YES to make sure regular navigation works as expected.
    return YES;
}


- (void)webViewDidFinishLoad:(UIWebView *)webView
{
    // start queues
    [toJavaScriptOperationQueue setSuspended:NO];
    //[fromJavaScriptOperationQueue setSuspended:NO];
    
    [self.activityIndicator stopAnimating];
}


-(void) sendSimpleAcquitmentToJS
{
    NSDictionary * dict = [NSDictionary dictionaryWithObjectsAndKeys:JSTypeCallBack,kJSType,JSCallbackSimpleAcquitment,kJSCallbackID, nil];
    [self executeScriptInWebView:self.webView WithDictionary:dict];
    
    if(self.popUpWebview)
        [self executeScriptInWebView:self.popUpWebview WithDictionary:dict];
}


/*why not to call callBacks automatically, knowing their name (would avoid a switch on the callback names...
 
 -(void) performSelectorNamed:(NSString *)methodName withDict:(NSDictionary *)dict
 {
 SEL s = NSSelectorFromString(methodName);
 if([self respondsToSelector:s])
 [self performSelector:s withObject:dict];
 }
 //*/

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark HPTOAST DELEGATE
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

-(void) HPToastwillShow:(HPToast *)toast
{
    toastIsShown = YES;
    NSLog(@"show");
}

-(void) HPToastwillHide:(HPToast *)toast
{
    toastIsShown = NO;
    if(toastsToShow.count > 0)
    {
        HPToast *t = [toastsToShow objectAtIndex:0];
        [t performSelectorOnMainThread:@selector(show) withObject:nil waitUntilDone:YES];
        [toastsToShow removeObjectAtIndex:0];
    }
    
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
