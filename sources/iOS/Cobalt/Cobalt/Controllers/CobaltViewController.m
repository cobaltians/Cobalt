/**
 *
 * CobaltViewController.m
 * Cobalt
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Cobaltians
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

#import "CobaltViewController.h"
#import "iToast.h"
#import "Cobalt.h"

#define haploidSpecialJSKey                 @"h@ploid#k&y"

// CONFIGURATION FILE
#define confFileName                        @"cobalt.conf"
#define kIosController                      @"iosController"
#define kIosNibName                         @"iosNibName"
#define kPullToRefreshEnabled               @"pullToRefresh"
#define kInfiniteScrollEnabled              @"infiniteScroll"

@interface CobaltViewController ()

@end

@implementation CobaltViewController

@synthesize webView,activityIndicator,pageName,popUpWebview, pullToRefreshTableHeaderView,isPullToRefreshActive;

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
    
    if(!self.pageName || self.pageName.length == 0)
    {
        self.pageName = defaultHtmlPage;
    }
    
    // Add pull to refresh table header view
    if(self.isPullToRefreshActive)
    {
        [self customPullToRefreshDefaultView];
        
        self.pullToRefreshTableHeaderView.state = RefreshStateNormal;
        self.pullToRefreshTableHeaderView.loadingHeight = self.pullToRefreshTableHeaderView.frame.size.height;
        self.pullToRefreshTableHeaderView.frame = CGRectMake(0.0, -self.webView.bounds.size.height, self.webView.bounds.size.width, self.webView.bounds.size.height);
        
        if(self.pullToRefreshTableHeaderView)
        {
            [self.webView.scrollView addSubview:self.pullToRefreshTableHeaderView];
        }
#if DEBUG_COBALT
        else
        {
            NSLog(@"WARNING : no pullToRefreshTableHeaderView set !");
        }
#endif
        [self.webView.scrollView setDelegate:self];
    }

    [self loadContentInWebView:self.webView FromFileNamed:self.pageName atPath:[self ressourcePath] withRessourcesAtPath:[self ressourcePath]];
}

- (void)viewDidUnload {
    self.webView.delegate = nil;
    [self setWebView:nil];
    [self setActivityIndicator:nil];
    toJavaScriptOperationQueue = nil;
    self.pullToRefreshTableHeaderView = nil;
    [self.webView.scrollView setDelegate:nil];
    [super viewDidUnload];
}

- (void)willRotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration {
    if(self.pullToRefreshTableHeaderView && self.pullToRefreshTableHeaderView.superview)
    {
        [self.pullToRefreshTableHeaderView setHidden: YES];
    }
    [super willRotateToInterfaceOrientation: toInterfaceOrientation duration: duration];
}

- (void)didRotateFromInterfaceOrientation:(UIInterfaceOrientation)fromInterfaceOrientation {
    if(self.pullToRefreshTableHeaderView && self.pullToRefreshTableHeaderView.superview)
    {
        [self.pullToRefreshTableHeaderView setHidden: NO];
        self.pullToRefreshTableHeaderView.frame = CGRectMake(0.0, -self.webView.bounds.size.height, self.webView.bounds.size.width, self.webView.bounds.size.height);
        //self.scrollView.contentSize = CGSizeMake(self.view.frame.size.width, self.view.frame.size.height+1);
    }
    [super didRotateFromInterfaceOrientation: fromInterfaceOrientation];
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


-(void) customWebView
{
    /*
     This is the default implementation.
     */
}

-(void) customPullToRefreshDefaultView
{
    if(!self.pullToRefreshTableHeaderView)
    {
        CGRect frame = CGRectMake(0.0, 0.0, self.webView.bounds.size.width,60.0);
        self.pullToRefreshTableHeaderView = [[PullToRefreshTableHeaderView alloc] initWithFrame:frame];
        [self.pullToRefreshTableHeaderView setAutoresizingMask:UIViewAutoresizingFlexibleRightMargin|UIViewAutoresizingFlexibleBottomMargin];
        
        self.pullToRefreshTableHeaderView.statusLabel = [[UILabel alloc] initWithFrame:frame];
        [self.pullToRefreshTableHeaderView.statusLabel setTextAlignment:NSTextAlignmentCenter];
        [self.pullToRefreshTableHeaderView.statusLabel setTextColor:[UIColor blackColor]];
        [self.pullToRefreshTableHeaderView.statusLabel setBackgroundColor:[UIColor clearColor]];
        [self.pullToRefreshTableHeaderView.statusLabel setAutoresizingMask:UIViewAutoresizingFlexibleTopMargin|UIViewAutoresizingFlexibleRightMargin|UIViewAutoresizingFlexibleLeftMargin];
        [self.pullToRefreshTableHeaderView addSubview:self.pullToRefreshTableHeaderView.statusLabel];
        
        
        self.pullToRefreshTableHeaderView.progressView = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
        [self.pullToRefreshTableHeaderView.progressView setAutoresizingMask:UIViewAutoresizingFlexibleTopMargin];
        [self.pullToRefreshTableHeaderView.progressView setColor:[UIColor blackColor]];
        [self.pullToRefreshTableHeaderView.progressView setFrame:CGRectMake(0, 0, 60, 60)];
        [self.pullToRefreshTableHeaderView addSubview:self.pullToRefreshTableHeaderView.progressView];
    }
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

#if DEBUG_COBALT
    if (stringFromFileAtURL == nil) {
        NSLog(@"Error reading file at %@\n%@",url, [error localizedFailureReason]);
    }
#endif
    
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
                              callbackId, kJSCallback,
                              object, kJSData,
                              nil];
        
        // pourquoi executer ça dans la main thread ??? (d'autant plus que tu y es déjà !!)
        //[self performSelectorOnMainThread:@selector(executeScriptInWebViewWithDictionary:) withObject:dict waitUntilDone:YES];
        [self executeScriptInWebView:self.webView WithDictionary:dict];
    }
#if DEBUG_COBALT
    else NSLog(@"ERROR : web callbackID invalid (null or empty)");
#endif
    
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
#if DEBUG_COBALT
                NSLog(@"JS LOGS : %@",value);
#endif
                
                return YES;
            }
        }
        //EVENT
        else if([type isEqualToString:JSTypeEvent])
        {
            NSString *name = [dict objectForKey:kJSEvent];
            //EVENT = TOAST
            if(name && [name isKindOfClass:[NSString class]] &&name.length >0 && [name isEqualToString:JSControlToast])
            {
                NSString *value = [dict objectForKey:kJSValue];
                CobaltToast *t = (CobaltToast *)[[CobaltToast makeText:value] setGravity:iToastGravityBottom];
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
            NSString *navType = [dict objectForKey:kJSAction];
            //PUSH
            if(navType && [navType isKindOfClass:[NSString class]] && navType.length >0)
            {
                if([navType isEqualToString:JSActionNavigationPush])
                {
                    NSString *vcId = [dict objectForKey:kJSNavigationController] ? [dict objectForKey:kJSNavigationController] : @"";
                    NSString *pageNamed = [dict objectForKey:kJSPage] ? [dict objectForKey:kJSPage] : @"";
                    NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:vcId, kJSNavigationController,
                                                                                    pageNamed, kJSPage,
                                                                                    nil];
                    [self performSelectorOnMainThread:@selector(pushWebViewControllerWithDict:) withObject:dict waitUntilDone:YES];
                    
                    return YES;
                }
                //POP
                else if(navType && navType.length >0 && [navType isEqualToString:JSActionNavigationPop])
                {
                    [self performSelectorOnMainThread:@selector(popWebViewController) withObject:nil waitUntilDone:YES];
                    
                    return YES;
                }
                //MODALE
                else if(navType && navType.length >0 && [navType isEqualToString:JSActionNavigationModale])
                {
                    NSString *vcId = [dict objectForKey:kJSNavigationController] ? [dict objectForKey:kJSNavigationController] : @"";
                    NSString *pageNamed = [dict objectForKey:kJSPage] ? [dict objectForKey:kJSPage] : @"";
                    NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:vcId, kJSNavigationController,
                                                                                    pageNamed, kJSPage,
                                                                                    nil];
                    [self performSelectorOnMainThread:@selector(presentWebViewControllerWithDict:) withObject:dict waitUntilDone:YES];
                    
                    return YES;
                }
                //DISMISS
                else if(navType && navType.length >0 && [navType isEqualToString:JSActionNavigationDismiss])
                {
                    [self performSelectorOnMainThread:@selector(dismissWebViewController) withObject:nil waitUntilDone:YES];
                    return YES;
                }
            }
        }
        //ALERT
        else if([type isEqualToString:JSControlAlert])
        {
            [self showAlertWithDict:dict];
            return YES;
            
        }
        //WEB ALERT
        else if([type isEqualToString:JSTypeWebLayer])
        {
            NSString *name = [dict objectForKey:kJSAction];
            if(name && [name isKindOfClass:[NSString class]] && name.length >0)
            {
                if([name isEqualToString:JSActionWebLayerShow])
                {
                    [self performSelectorOnMainThread:@selector(showPopUpWebviewWithDict:) withObject:dict waitUntilDone:YES];
                    return YES;
                }
                else if([name isEqualToString:JSActionWebLayerDismiss])
                {
                    [self performSelectorOnMainThread:@selector(dismissPopUpWebviewWithDict:) withObject:dict waitUntilDone:YES];
                    return YES;
                }
            }
        }
        // CALLBACKS
        else if([type isEqualToString:JSTypeCallBack])
        {
            NSString *name = [dict objectForKey:kJSCallback];
            if(name && [name isKindOfClass:[NSString class]] && [name isEqualToString:JSCallbackPullToRefreshDidRefresh])
            {
                [self performSelectorOnMainThread:@selector(didRefresh) withObject:nil waitUntilDone:YES];
                return YES;
            }
            else if (name && [name isKindOfClass:[NSString class]] && [name isEqualToString:JSCallbackInfiniteScrollDidRefresh])
            {
                [self performSelectorOnMainThread:@selector(moreItemsHaveBeenLoaded) withObject:nil waitUntilDone:YES];
                return YES;
            }
        }
        //JS READY EVENT
        else if([type isEqualToString:JSTypeCobaltIsReady])
        {
            [toJavaScriptOperationQueue setSuspended:NO];
#if DEBUG_COBALT
            NSLog(@"Received NativeBridgeIsReady");
#endif
        }
    }
#if DEBUG_COBALT
    else NSLog(@"ERROR : type is undefined. Impossible to handle JavaScriptEvent !");
#endif
    return NO;
}

- (NSString *)ressourcePath
{
    return [NSString stringWithFormat:@"%@%@",[[NSBundle mainBundle] resourcePath],@"/www/"];
}


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark NAVIGATION METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

-(void) pushWebViewControllerWithDict:(NSDictionary *)dict
{
    NSString *viewControllerId = [dict objectForKey:kJSNavigationController] ? [dict objectForKey:kJSNavigationController] : @"";
    NSString *pageNameToShow = [dict objectForKey:kJSPage]  ? [dict objectForKey:kJSPage] : @"";
    CobaltViewController *viewControllerToPush = [self getControllerFromId:viewControllerId];
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
    NSString *viewControllerId = [dict objectForKey:kJSNavigationController];
    NSString *pageNameToShow = [dict objectForKey:kJSPage];
    CobaltViewController *viewControllerToPresent = [self getControllerFromId:viewControllerId];
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
#if DEBUG_COBALT
    else NSLog(@"Error : no controller is presented");
#endif
}


-(CobaltViewController *) getControllerFromId:(NSString *) viewControllerId
{
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
                className = [[confDictionary objectForKey:viewControllerId] objectForKey:kIosController];
                nibName = [[confDictionary objectForKey:viewControllerId] objectForKey:kIosNibName];
                pullToRefreshActive = [[[confDictionary objectForKey:viewControllerId] objectForKey:kPullToRefreshEnabled] boolValue];
                infiniteScrollActive = [[[confDictionary objectForKey:viewControllerId] objectForKey:kInfiniteScrollEnabled] boolValue];
            }
            
            if(!className)
            {
#if DEBUG_COBALT
                NSLog(@"WARNING : className for ID %@ not found. Look for default class ID",viewControllerId);
#endif
                className = [[confDictionary objectForKey:JSNavigationControllerDefault] objectForKey:kIosController];
                nibName = [[confDictionary objectForKey:JSNavigationControllerDefault] objectForKey:kIosNibName];
                pullToRefreshActive = [[[confDictionary objectForKey:JSNavigationControllerDefault] objectForKey:kPullToRefreshEnabled] boolValue];
                infiniteScrollActive = [[[confDictionary objectForKey:viewControllerId] objectForKey:kInfiniteScrollEnabled] boolValue];
                
            }
            
            //if nibName is not defined in conf file -> use same as className !
            if(!nibName && className)
            {
                nibName = className;
            }
        }
        else
        {
#if DEBUG_COBALT
            NSLog(@"ERROR : Syntax error in Conf file");
#endif
            return nil;
        }
        
    }
    else
    {
#if DEBUG_COBALT
        NSLog(@"ERROR : Conf file may be missing or not at the root of ressources folder.");
#endif
        return nil;
    }
    
    if([self isValidControllerWithClassName:className andNibName:nibName])
    {
        if([NSClassFromString(className) isSubclassOfClass:[CobaltViewController class]])
        {
            CobaltViewController *c = [[NSClassFromString(className) alloc] initWithNibName:nibName bundle:[NSBundle mainBundle]];
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
#if DEBUG_COBALT
        NSLog(@"ERROR : no view Controller named %@ was found for given ID %@. Nothing will happen...",className,viewControllerId);
#endif
        return nil;
    }
}


-(BOOL) isValidControllerWithClassName:(NSString *)className andNibName:(NSString *)nibName
{
    BOOL b1 = (className && NSClassFromString(className) && [NSClassFromString(className) isSubclassOfClass:[CobaltViewController class]]);
    BOOL b2 = (nibName && nibName.length > 0 && [[NSBundle mainBundle] pathForResource:nibName ofType:@"nib"] != nil);
    
    if(!b1)
    {
        if(!className || (className && !NSClassFromString(className)))
        {
#if DEBUG_COBALT
            NSLog(@"ERROR : classNotFound %@",className);
#endif
        }
        else if(className && NSClassFromString(className) && ![NSClassFromString(className) isSubclassOfClass:[CobaltViewController class]])
        {
#if DEBUG_COBALT
            NSLog(@"ERROR : impossible to show %@ for it does not inherit from CobaltViewController",className);
#endif
        }
    }
    else if(!b2)
    {
#if DEBUG_COBALT
        NSLog(@"ERROR : nibName %@ does not exist !",nibName);
#endif
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
                NSString *callbackId = [NSString stringWithFormat:@"%@",[dict objectForKey:kJSCallback]];
                if(callbackId && callbackId.length > 0)
                {
                    [alertCallbacks setObject:callbackId forKey:[NSString stringWithFormat:@"%d",alertId.intValue]];
                }
                else
                {
#if DEBUG_COBALT
                    NSLog(@"WARNING : invalid callback name for alertView with webCallback (null or empty)");
#endif
                }
            }
            else if(![receiverType isEqualToString:JSAlertCallbackReceiverNative])
            {
#if DEBUG_COBALT
                NSLog(@"WARNING : invalid callback receiver for alertView : %@",receiverType);
#endif
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
        
        NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:[NSNumber numberWithInteger:alertViewId], kJSAlertID,
                                                                        [NSNumber numberWithInteger:buttonIndex], kJSAlertButtonIndex,
                                                                        nil];
        
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
    
    popupPageName = ([dict objectForKey:kJSPage]  && [[dict objectForKey:kJSPage] isKindOfClass:[NSString class]])? [dict objectForKey:kJSPage] : @"";
    NSNumber *fadeDuration = ([dict objectForKey:kJSWebLayerFadeDuration]  && [[dict objectForKey:kJSWebLayerFadeDuration] isKindOfClass:[NSNumber class]])? [dict objectForKey:kJSWebLayerFadeDuration] : [NSNumber numberWithFloat:0.3];
    
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
    NSNumber *fadeDuration = ([dict objectForKey:kJSWebLayerFadeDuration]  && [[dict objectForKey:kJSWebLayerFadeDuration] isKindOfClass:[NSNumber class]])? [dict objectForKey:kJSWebLayerFadeDuration] : [NSNumber numberWithFloat:0.3];
    
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
   NSDictionary *d = [NSDictionary dictionaryWithObjectsAndKeys:JSTypeEvent, kJSType,
                                                                JSEventWebLayerOnDismiss, kJSEvent,
                                                                filename, kJSValue,
                                                                nil];
    [self executeScriptInWebView:self.webView WithDictionary:d];
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark WEBVIEW DELEGATE METHODS
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
    NSDictionary * dict = [NSDictionary dictionaryWithObjectsAndKeys:   JSTypeCallBack, kJSType,
                                                                        JSCallbackSimpleAcquitment, kJSCallback,
                                                                        nil];
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
#pragma mark HPTOAST DELEGATE METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

-(void) toastwillShow:(CobaltToast *)toast
{
    toastIsShown = YES;
#if DEBUG_COBALT
    NSLog(@"show");
#endif
}

-(void) toastwillHide:(CobaltToast *)toast
{
    toastIsShown = NO;
    if(toastsToShow.count > 0)
    {
        CobaltToast *t = [toastsToShow objectAtIndex:0];
        [t performSelectorOnMainThread:@selector(show) withObject:nil waitUntilDone:YES];
        [toastsToShow removeObjectAtIndex:0];
    }
    
}


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark SCROLL VIEW DELEGATE METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//*************
// DID SCROLL *
//*************
/*!
 @method        - (void)scrollViewDidScroll:(UIScrollView *)scrollView
 @abstract      Tells the delegate when the user scrolls the content view within the receiver.
 @param         scrollView  The scroll-view object in which the scrolling occurred.
 */
- (void)scrollViewDidScroll:(UIScrollView *)_scrollView {
    if(self.isPullToRefreshActive && self.pullToRefreshTableHeaderView && self.pullToRefreshTableHeaderView.superview)
    {
        if (_scrollView.isDragging) {
            if (self.pullToRefreshTableHeaderView.state == RefreshStatePulling && _scrollView.contentOffset.y > -65.0 && self.webView.scrollView.contentOffset.y < 0.0 && !_isRefreshing) {
                self.pullToRefreshTableHeaderView.state = RefreshStateNormal;
            }
            else if (self.pullToRefreshTableHeaderView.state == RefreshStateNormal && _scrollView.contentOffset.y < -65.0 && !_isRefreshing) {
                self.pullToRefreshTableHeaderView.state = RefreshStatePulling;
            }
        }
    }
}

//*******************
// DID END DRAGGING *
//*******************
/*!
 @method        - (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate
 @abstract      Tells the delegate when dragging ended in the scroll view.
 @param         scrollView  The scroll-view object that finished scrolling the content view.
 @param         decelerate  YES if the scrolling movement will continue, but decelerate, after a touch-up gesture during a dragging operation.
 */
- (void)scrollViewDidEndDragging:(UIScrollView *)_scrollView willDecelerate:(BOOL)decelerate {
    if(self.isPullToRefreshActive && self.pullToRefreshTableHeaderView && self.pullToRefreshTableHeaderView.superview)
    {
        if (_scrollView.contentOffset.y <= -65.0 && !_isRefreshing) {
            [self refresh];
        }
    }
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark PULL TO REFRESH METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//**********
// REFRESH *
//**********
/*!
 @method		- (void)refresh
 @abstract		Tells the web view to refresh its content.
 */
- (void)refresh {
    if(self.isPullToRefreshActive)
    {
        _isRefreshing = YES;
        self.pullToRefreshTableHeaderView.state = RefreshStateLoading;
        [UIView beginAnimations:nil context:nil];
        [UIView setAnimationDuration:0.2];
        self.webView.scrollView.contentInset = UIEdgeInsetsMake(self.pullToRefreshTableHeaderView.loadingHeight, 0.0f, 0.0f, 0.0f);
        [UIView commitAnimations];
        
        [self refreshWebViewDataSource];
    }
}

//******************************
// LOAD TABLE VIEW DATA SOURCE *
//******************************
/*!
 @method		- (void)loadTableViewDataSource
 @abstract		Simulates loading of the table view data source.
 */
- (void)loadWebViewDataSource {
    [self didRefresh];
}

//*********************************
// REFRESH TABLE VIEW DATA SOURCE *
//*********************************
/*!
 @method		- (void)refreshTableViewDataSource
 @abstract		Starts refreshing the table view data source.
 */
- (void)refreshWebViewDataSource {
    NSDictionary *d = [NSDictionary dictionaryWithObjectsAndKeys:   JSTypeEvent, kJSType,
                                                                    JSEventPullToRefresh, kJSEvent,
                                                                    JSCallbackPullToRefreshDidRefresh, kJSCallback,
                                                                    nil];
    [self executeScriptInWebView:self.webView WithDictionary:d];
}

//**************
// DID REFRESH *
//**************
/*!
 @method		- (void)didRefresh
 @abstract		Tells the table view it has been refreshed.
 */
- (void)didRefresh {
    _isRefreshing = NO;
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationDuration:0.3];
    [UIView setAnimationDelegate:self];
    [UIView setAnimationDidStopSelector:@selector(didStop)];
    self.webView.scrollView.contentInset = UIEdgeInsetsMake(0.0f, 0.0f, 0.0f, 0.0f);
	[UIView commitAnimations];
}

//***********
// DID STOP *
//***********
/*!
 @method		- (void)didStop
 @abstract		Tells the table view the refresh animation has stopped.
 */
- (void)didStop {
    self.pullToRefreshTableHeaderView.state = RefreshStateNormal;
}

-(void) stopPullToRefreshRefreshing
{
    if(self.isPullToRefreshActive && _isRefreshing)
    {
        [self didRefresh];
        NSDictionary *d = [NSDictionary dictionaryWithObjectsAndKeys:   JSTypeEvent, kJSType,
                                                                        JSPullToRefreshCancelled, kJSName,
                                                                        nil];
        [self executeScriptInWebView:self.webView WithDictionary:d];
    }
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark INFINITE SCROLL METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

- (void)loadMoreItems
{
    _isLoadingMore = YES;
    
    [self loadMoreContentInWebview];
}

-(void) loadMoreContentInWebview
{
    NSDictionary *d = [NSDictionary dictionaryWithObjectsAndKeys:   JSTypeEvent, kJSType,
                                                                    JSEventInfiniteScroll, kJSEvent,
                                                                    JSCallbackInfiniteScrollDidRefresh, kJSCallback,
                                                                    nil];
    [self executeScriptInWebView:self.webView WithDictionary:d];
}

-(void)moreItemsHaveBeenLoaded
{
    _isLoadingMore = NO;
    [self moreItemsLoaded];
}

-(void)moreItemsLoaded
{
    /*
     * default implementation
     */
}

-(void) stopInfiniteScrollRefreshing
{
    if(self.isInfiniteScrollActive && _isLoadingMore)
    {
        _isLoadingMore = NO;
        NSDictionary *d = [NSDictionary dictionaryWithObjectsAndKeys:JSTypeEvent,kJSType,JSInfiniteScrollCancelled,kJSName,nil];
        [self executeScriptInWebView:self.webView WithDictionary:d];
    }
}


@end


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark IMPLEMENTATION
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@implementation PullToRefreshTableHeaderView

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark PROPERTIES
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@synthesize loadingHeight, progressView, arrowImageView, lastUpdatedLabel, statusLabel, state;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//*******************
// SET LAST UPDATED *
//*******************
/*!
 @method		- (void)setLastUpdated:(NSString *)lastUpdated
 @abstract		Sets the last updated text.
 @param         lastUpdated The last updated text to set.
 */
- (void)setLastUpdated:(NSString *)lastUpdated {
    self.lastUpdatedLabel.text = lastUpdated;
}

//************
// SET STATE *
//************
/*!
 @method		- (void)setState:(RefreshState)newState
 @abstract		Sets the refresh state.
 @param         newState    The refresh state to set.
 */
- (void)setState:(RefreshState)newState {
    switch (newState) {
        case RefreshStateNormal:
            if (state == RefreshStatePulling) {
                [UIView beginAnimations:nil context:nil];
                [UIView setAnimationDuration:0.2];
                self.arrowImageView.transform = CGAffineTransformIdentity;
                [UIView commitAnimations];
            }
            self.statusLabel.text = [self textForState:newState];
            [self.progressView stopAnimating];
            self.arrowImageView.hidden = NO;
            self.arrowImageView.transform = CGAffineTransformIdentity;
            break;
            
        case RefreshStatePulling:
            self.statusLabel.text = [self textForState:newState];
            [UIView beginAnimations:nil context:nil];
            [UIView setAnimationDuration:0.2];
            self.arrowImageView.transform = CGAffineTransformMakeRotation(M_PI);
            [UIView commitAnimations];
            break;
            
        case RefreshStateLoading:
            self.statusLabel.text = [self textForState:newState];
            [self.progressView startAnimating];
            self.arrowImageView.hidden = YES;
            break;
            
        default:
            break;
    }
    
    state = newState;
}

//************
// SET TEXT FOR STATE *
//************
/*!
 @method		- (NSString *)textForState:(RefreshState)newState
 @abstract		Sets the text for the status label depending on the newState given
 @param         newState The new state applied to the pullToRefreshTableHeaderView
 @return        a NSString containing the string to display for the given mode.
 @discussion    This method may be overriden in subclasses.
 */
- (NSString *)textForState:(RefreshState)newState {
    switch (newState) {
        case RefreshStateNormal:
            return NSLocalizedString(@"Tirez pour rafraîchir...", nil);
            break;
        case RefreshStatePulling:
            return NSLocalizedString(@"Relâchez pour actualiser...", nil);
            break;
        case RefreshStateLoading:
            return NSLocalizedString(@"Chargement...", nil);
            break;
        default:
            break;
    }
    return @"";
}

@end
