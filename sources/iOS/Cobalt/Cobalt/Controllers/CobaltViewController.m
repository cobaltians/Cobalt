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

#define haploidSpecialJSKey     @"h@ploid#k&y"

// CONFIGURATION FILE
#define confFileName            @"cobalt.conf"
#define kIosController          @"iosController"
#define kIosNibName             @"iosNibName"
#define kPullToRefreshEnabled   @"pullToRefresh"
#define kInfiniteScrollEnabled  @"infiniteScroll"

@implementation CobaltViewController

@synthesize activityIndicator,
            isInfiniteScrollActive,
            isPullToRefreshActive,
            pageName,
            popUpWebview,
            pullToRefreshTableHeaderView,
            webView;

NSMutableDictionary * alertCallbacks;

NSMutableArray * toastsToShow;
BOOL toastIsShown;

NSString * popupPageName;

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    // Do any additional setup after loading the view from its nib.
    [self customWebView];
    [webView setDelegate:self];
    
    toJavaScriptOperationQueue = [[NSOperationQueue alloc] init] ;
    [toJavaScriptOperationQueue setSuspended:YES];
    
    fromJavaScriptOperationQueue = [[NSOperationQueue alloc] init] ;
    
    alertCallbacks = [[NSMutableDictionary alloc] init];
    toastsToShow = [[NSMutableArray alloc] init];
    
    [activityIndicator startAnimating];
    
    if ([webView respondsToSelector:@selector(setKeyboardDisplayRequiresUserAction:)]) {
        [webView setKeyboardDisplayRequiresUserAction:NO];
    }
    
    if (! pageName
        || pageName.length == 0) {
        pageName = defaultHtmlPage;
    }
    
    // Add pull-to-refresh table header view
    if (isPullToRefreshActive) {
        [self customPullToRefreshDefaultView];
        
        pullToRefreshTableHeaderView.state = RefreshStateNormal;
        pullToRefreshTableHeaderView.loadingHeight = pullToRefreshTableHeaderView.frame.size.height;
        pullToRefreshTableHeaderView.frame = CGRectMake(0.0, -self.webView.bounds.size.height,
                                                        self.webView.bounds.size.width, self.webView.bounds.size.height);
        
        if(pullToRefreshTableHeaderView) {
            [webView.scrollView addSubview:pullToRefreshTableHeaderView];
        }
#if DEBUG_COBALT
        else {
            NSLog(@"WARNING: no pullToRefreshTableHeaderView set!");
        }
#endif
        [webView.scrollView setDelegate:self];
    }

    [self loadContentInWebView:webView FromFileNamed:pageName atPath:[CobaltViewController ressourcePath] withRessourcesAtPath:[CobaltViewController ressourcePath]];
}

- (void)viewDidUnload
{
    pullToRefreshTableHeaderView = nil;
    
    [webView.scrollView setDelegate:nil];
    webView.delegate = nil;
    [self setWebView:nil];
    
    [self setActivityIndicator:nil];
    
    toJavaScriptOperationQueue = nil;
    
    [super viewDidUnload];
}

- (void)willRotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration
{
    if (pullToRefreshTableHeaderView
        && pullToRefreshTableHeaderView.superview) {
        [pullToRefreshTableHeaderView setHidden: YES];
    }
    
    [super willRotateToInterfaceOrientation:toInterfaceOrientation duration:duration];
}

- (void)didRotateFromInterfaceOrientation:(UIInterfaceOrientation)fromInterfaceOrientation
{
    if (pullToRefreshTableHeaderView
        && pullToRefreshTableHeaderView.superview) {
        [pullToRefreshTableHeaderView setHidden: NO];
        pullToRefreshTableHeaderView.frame = CGRectMake(0.0, -self.webView.bounds.size.height,
                                                        self.webView.bounds.size.width, self.webView.bounds.size.height);
    }
    
    [super didRotateFromInterfaceOrientation:fromInterfaceOrientation];
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


- (void)customWebView
{
    
}

-(void) customPullToRefreshDefaultView
{
    if (! pullToRefreshTableHeaderView) {
        CGRect frame = CGRectMake(0.0, 0.0,
                                  webView.bounds.size.width, 60.0);
        pullToRefreshTableHeaderView = [[PullToRefreshTableHeaderView alloc] initWithFrame:frame];
        [pullToRefreshTableHeaderView setAutoresizingMask:UIViewAutoresizingFlexibleRightMargin|UIViewAutoresizingFlexibleBottomMargin];
        
        pullToRefreshTableHeaderView.statusLabel = [[UILabel alloc] initWithFrame:frame];
        [pullToRefreshTableHeaderView.statusLabel setTextAlignment:NSTextAlignmentCenter];
        [pullToRefreshTableHeaderView.statusLabel setTextColor:[UIColor blackColor]];
        [pullToRefreshTableHeaderView.statusLabel setBackgroundColor:[UIColor clearColor]];
        [pullToRefreshTableHeaderView.statusLabel setAutoresizingMask:UIViewAutoresizingFlexibleTopMargin|UIViewAutoresizingFlexibleRightMargin|UIViewAutoresizingFlexibleLeftMargin];
        [pullToRefreshTableHeaderView addSubview:pullToRefreshTableHeaderView.statusLabel];
        
        
        pullToRefreshTableHeaderView.progressView = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
        [pullToRefreshTableHeaderView.progressView setAutoresizingMask:UIViewAutoresizingFlexibleTopMargin];
        [pullToRefreshTableHeaderView.progressView setColor:[UIColor blackColor]];
        [pullToRefreshTableHeaderView.progressView setFrame:CGRectMake(0, 0,
                                                                       60, 60)];
        [pullToRefreshTableHeaderView addSubview:pullToRefreshTableHeaderView.progressView];
    }
}

- (void)loadContentInWebView:(UIWebView *)mWebView FromFileNamed:(NSString *)filename atPath:(NSString *)path withRessourcesAtPath:(NSString *)pathOfRessources
{
    NSURL * baseURL = [NSURL fileURLWithPath:[path stringByAppendingPathComponent:filename]];
    NSURLRequest * request = [NSURLRequest requestWithURL:baseURL];
    [mWebView loadRequest:request];
}

- (NSDictionary *)getConfigurationForController:(NSString *)controller
{
    NSDictionary * configuration = [self getConfiguration];
    if (configuration) {
        if (controller) {
            NSDictionary * controllerConfiguration = [configuration objectForKey:controller];
            
            if (controllerConfiguration
                && [controllerConfiguration isKindOfClass:[NSDictionary class]]) {
                return controllerConfiguration;
            }
#if DEBUG_COBALT
            else {

                NSLog(@"getConfigurationForController: no configuration found for %@ controller.\n\
                      Trying to return default controller configuration", controller);
#endif
            }
        }
        
        NSDictionary * defaultControllerConfiguration = [configuration objectForKey:JSNavigationControllerDefault];
        
        if (defaultControllerConfiguration
            && [defaultControllerConfiguration isKindOfClass:[NSDictionary class]]) {
            return defaultControllerConfiguration;
        }
        else {
#if DEBUG_COBALT
            NSLog(@"getConfigurationForController: no configuration found for default controller");
#endif
        }
    }
    
    return nil;
}

- (NSDictionary *)getConfiguration
{
    NSString * configuration = [CobaltViewController stringWithContentsOfFile:[NSString stringWithFormat:@"%@%@", [CobaltViewController ressourcePath], confFileName]];
    if (configuration) {
        return [CobaltViewController JSONObjectWithString:configuration];
    }
    else {
        return nil;
    }
}

+ (NSString *)stringWithContentsOfFile:(NSString *)path
{
    if (path != nil) {
        NSURL * url = [NSURL fileURLWithPath:path isDirectory:NO];
        NSError * error;
        NSString * content = [[NSString alloc] initWithContentsOfURL:url encoding:NSUTF8StringEncoding error:&error];

    #if DEBUG_COBALT
        if (! content) {
            NSLog(@"stringWithContentsOfFile: Error while reading file at %@\n%@", url, [error localizedFailureReason]);
        }
    #endif
        
        return content;
    }
    else {
#if DEBUG_COBALT
        NSLog(@"stringWithContentsOfFile: path is nil");
#endif
        return nil;
    }
}

+ (id)JSONObjectWithString:(NSString *)string
{
    NSError * error;
    NSData * data = [string dataUsingEncoding:NSUTF8StringEncoding];
    
    id dictionary = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&error];
    
    #if DEBUG_COBALT
    if (! dictionary) {
        NSLog(@"JSONObjectWithString: Error while reading JSON %@\n%@", string, [error localizedFailureReason]);
    }
    #endif
    
    return dictionary;
}

- (void)executeScriptInWebView:(UIWebView *)mWebView WithDictionary:(NSDictionary *)dict
{
    [toJavaScriptOperationQueue addOperationWithBlock:^{
        if ([NSJSONSerialization isValidJSONObject:dict]) {
            NSError * error;
            NSString * jsonMsg =[[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:dict options:0 error:&error] encoding:NSUTF8StringEncoding];
            
            //ensure there is no raw newline in any part of the json string.
            jsonMsg = [[jsonMsg componentsSeparatedByCharactersInSet:[NSCharacterSet newlineCharacterSet]] componentsJoinedByString:@""];

            NSString * javaScriptCommand = [NSString stringWithFormat:@"nativeBridge.execute(%@);", jsonMsg];
            
            // TDOD - pourquoi attendre la fin de l'exécution dans le main thread sachant que tu es dans un thread ???
            [mWebView performSelectorOnMainThread:@selector(stringByEvaluatingJavaScriptFromString:) withObject:javaScriptCommand waitUntilDone:NO];
        }
    }];
}

- (void)sendCallbackResponseWithID:(NSString *)callbackId andObject:(NSObject *)object
{
    if(callbackId
       && callbackId.length > 0) {
        NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:JSTypeCallBack, kJSType,
                                                                        callbackId, kJSCallback,
                                                                        object, kJSData,
                                                                        nil];
        
        // pourquoi executer ça dans la main thread ??? (d'autant plus que tu y es déjà !!)
        //[self performSelectorOnMainThread:@selector(executeScriptInWebViewWithDictionary:) withObject:dict waitUntilDone:YES];
        [self executeScriptInWebView:webView WithDictionary:dict];
    }
#if DEBUG_COBALT
    else {
        NSLog(@"ERROR : web callbackID invalid (null or empty)");
    }
#endif
}

- (BOOL)handleDictionarySentByJavaScript:(NSDictionary *)dict
{
    NSString * type = [dict objectForKey:kJSType];
    
    if (type
        && [type isKindOfClass:[NSString class]]) {
        
        // CALLBACK
        if ([type isEqualToString:JSTypeCallBack]) {
            NSString * callback = [dict objectForKey:kJSCallback];
            NSDictionary * data = [dict objectForKey:kJSData];
            
            if (callback
                && [callback isKindOfClass:[NSString class]]) {
                if ([callback isEqualToString:JSCallbackPullToRefreshDidRefresh]) {
                    [self performSelectorOnMainThread:@selector(pullToRefreshDidRefresh) withObject:nil waitUntilDone:YES];
                }
                else if ([callback isEqualToString:JSCallbackInfiniteScrollDidRefresh]) {
                    [self performSelectorOnMainThread:@selector(infiniteScrollDidRefresh) withObject:nil waitUntilDone:YES];
                }
                else {
#if DEBUG_COBALT
                    NSLog(@"handleDictionarySentByJavaScript: unhandled callback %@", [dict description]);
#endif
                    // TODO: add onUnhandledCallback method
                    // return [self onUnhandledCallback:callback withData:data];
                    return NO;
                }
                
                
            }
#if DEBUG_COBALT
            else {
                NSLog(@"handleDictionarySentByJavaScript: callback field missing or not a string (message: %@)", [dict description]);
            }
#endif
        }
        
        // COBALT IS READY
        else if ([type isEqualToString:JSTypeCobaltIsReady]) {
            [toJavaScriptOperationQueue setSuspended:NO];
#if DEBUG_COBALT
            NSLog(@"handleDictionarySentByJavaScript: CobaltIsReady!");
#endif
        }
        
        // EVENT
        else if ([type isEqualToString:JSTypeEvent]) {
            NSString * event = [dict objectForKey:kJSEvent];
            NSDictionary * data = [dict objectForKey:kJSData];
            NSString * callback = [dict objectForKey:kJSCallback];
            
            if (event &&
                [event isKindOfClass:[NSString class]]) {
#if DEBUG_COBALT
                NSLog(@"handleDictionarySentByJavaScript: unhandled event %@", [dict description]);
#endif
                // TODO: add onUnhandledEvent method
                //return [self onUnhandledEvent:event withData:data andCallback:callback];
                return NO;
            }
#if DEBUG_COBALT
            else {
                NSLog(@"handleDictionarySentByJavaScript: event field missing or not a string (message: %@)", [dict description]);
            }
#endif
        }
        
        // LOG
        else if ([type isEqualToString:JSTypeLog]) {
            NSString * text = [dict objectForKey:kJSValue];
            if (text
                && [text isKindOfClass:[NSString class]]) {
                NSLog(@"JS LOG: %@", text);
            }
        }
        
        // NAVIGATION
        else if([type isEqualToString:JSTypeNavigation]) {
            NSString * action = [dict objectForKey:kJSAction];
            
            if (action
                && [action isKindOfClass:[NSString class]]) {
                // PUSH
                if ([action isEqualToString:JSActionNavigationPush]) {
                    NSDictionary * data = [dict objectForKey:kJSData];
                    if (data
                        && [data isKindOfClass:[NSDictionary class]]) {
                        [self performSelectorOnMainThread:@selector(pushViewControllerWithData:) withObject:data waitUntilDone:YES];
                    }
#if DEBUG_COBALT
                    else {
                        NSLog(@"handleDictionarySentByJavaScript: data field missing or not an object (message: %@)", [dict description]);
                    }
#endif
                }
                //POP
                else if ([action isEqualToString:JSActionNavigationPop]) {
                    [self performSelectorOnMainThread:@selector(popViewController) withObject:nil waitUntilDone:YES];
                }
                //MODALE
                else if ([action isEqualToString:JSActionNavigationModale]) {
                    NSDictionary * data = [dict objectForKey:kJSData];
                    if (data
                        && [data isKindOfClass:[NSDictionary class]]) {
                            [self performSelectorOnMainThread:@selector(presentViewControllerWithData:) withObject:data waitUntilDone:YES];
                    }
#if DEBUG_COBALT
                    else {
                        NSLog(@"handleDictionarySentByJavaScript: data field missing or not an object (message: %@)", [dict description]);
                    }
#endif
                }
                //DISMISS
                else if ([action isEqualToString:JSActionNavigationDismiss]) {
                    [self performSelectorOnMainThread:@selector(dismissViewController) withObject:nil waitUntilDone:YES];
                }
                else {
#if DEBUG_COBALT
                    NSLog(@"handleDictionarySentByJavaScript: unhandled navigation %@", [dict description]);
#endif
                    // TODO: add onUnhandledMessage method
                    //return [self onUnhandledMessage:dict];
                    return NO;
                }
                
            }
#if DEBUG_COBALT
            else {
                NSLog(@"handleDictionarySentByJavaScript: action field missing or not a string (message: %@)", [dict description]);
            }
#endif
        }
        
        // TODO: FROM HERE
        
        // UI
        else if ([type isEqualToString:JSTypeUI]) {
            NSString * control = [dict objectForKey:kJSUIControl];
            NSDictionary * data = [dict objectForKey:kJSData];
            NSString * callback = [dict objectForKey:kJSCallback];
            
            if (control
                && [control isKindOfClass:[NSString class]]) {
                
                // TOAST
                if ([control isEqualToString:JSControlToast]) {
                    if (data
                        && [data isKindOfClass:[NSDictionary class]]) {
                        NSString * message = [data objectForKey:kJSAlertMessage];
                        if (message
                            && [message isKindOfClass: [NSString class]]) {
                            CobaltToast * toast = (CobaltToast *)[[CobaltToast makeText:message] setGravity:iToastGravityBottom];
                            [toast setDelegate:self];
                            if (toastIsShown) {
                                [toastsToShow addObject:toast];
                            }
                            else {
                                [toast performSelectorOnMainThread:@selector(show) withObject:nil waitUntilDone:YES];
                            }
                        }
#if DEBUG_COBALT
                        else {
                            NSLog(@"handleDictionarySentByJavaScript: message field missing or not a string (message: %@)", [dict description]);
                        }
#endif
                    }
#if DEBUG_COBALT
                    else {
                        NSLog(@"handleDictionarySentByJavaScript: data field missing or not an object (message: %@)", [dict description]);
                    }
#endif
                }
                
                // ALERT
                else if([control isEqualToString:JSControlAlert]) {
                    [self showAlertWithDict:dict];
                }
                
                else {
#if DEBUG_COBALT
                    NSLog(@"handleDictionarySentByJavaScript: unhandled message %@", [dict description]);
#endif
                    // TODO: add onUnhandledMessage method
                    //return [self onUnhandledMessage:dict];
                    return NO;
                }
            }
#if DEBUG_COBALT
            else {
                NSLog(@"handleDictionarySentByJavaScript: control field missing or not a string (message: %@)", [dict description]);
            }
#endif
        }
        
        // WEB LAYER
        else if ([type isEqualToString:JSTypeWebLayer]) {
            NSString * action = [dict objectForKey:kJSAction];
            if (action
               && [action isKindOfClass:[NSString class]]) {
                
                // SHOW
                if([action isEqualToString:JSActionWebLayerShow]) {
                    [self performSelectorOnMainThread:@selector(showPopUpWebviewWithDict:) withObject:dict waitUntilDone:YES];
                }
                
                // DISMISS
                else if([action isEqualToString:JSActionWebLayerDismiss]) {
                    [self performSelectorOnMainThread:@selector(dismissPopUpWebviewWithDict:) withObject:dict waitUntilDone:YES];
                }
            }
#if DEBUG_COBALT
            else {
                NSLog(@"handleDictionarySentByJavaScript: action field missing or not a string (message: %@)", [dict description]);

            }
#endif
        }
        
        else {
#if DEBUG_COBALT
            NSLog(@"handleDictionarySentByJavaScript: unhandled message %@", [dict description]);
#endif
            // TODO: add onUnhandledMessage method
            //return [self onUnhandledMessage:dict];
            return NO;
        }
    }
    else {
#if DEBUG_COBALT
        NSLog(@"handleDictionarySentByJavaScript: unhandled message %@", [dict description]);
#endif
        // TODO: add onUnhandledMessage method
        //return [self onUnhandledMessage:dict];
        return NO;
    }
    
    return YES;
}

+ (NSString *)ressourcePath
{
    return [NSString stringWithFormat:@"%@%@",[[NSBundle mainBundle] resourcePath], @"/www/"];
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark NAVIGATION METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

- (void)pushViewControllerWithData:(NSDictionary *)data
{
    NSString * page = [data objectForKey:kJSPage];
    NSString * controller = [data objectForKey:kJSNavigationController];
    
    if (page
        && [page isKindOfClass:[NSString class]]) {
        UIViewController * viewController = [self getViewControllerForController:controller];
        if (viewController) {
            if ([[viewController class] isSubclassOfClass:[CobaltViewController class]]) {
                // Sets page
                ((CobaltViewController *)viewController).pageName = page;
            }
            
            // Push corresponding viewController
            [self.navigationController pushViewController:viewController animated:YES];
        }
    }
#if DEBUG_COBALT
    else {
        NSLog(@"handleDictionarySentByJavaScript: page field missing or not a string (data: %@)", [data description]);
    }
#endif
}


- (void)popViewController
{
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)presentViewControllerWithData:(NSDictionary *)data
{
    NSString * page = [data objectForKey:kJSPage];
    NSString * controller = [data objectForKey:kJSNavigationController];
    if (page
        && [page isKindOfClass:[NSString class]]) {
        UIViewController * viewController = [self getViewControllerForController:controller];
        if (viewController) {
            if ([[viewController class] isSubclassOfClass:[CobaltViewController class]]) {
                // Sets page
                ((CobaltViewController *)viewController).pageName = page;
            }
            
            [self presentViewController:[[UINavigationController alloc] initWithRootViewController:viewController] animated:YES completion:nil];
        }
    }
#if DEBUG_COBALT
    else {
        NSLog(@"handleDictionarySentByJavaScript: page field missing or not a string (message: %@)", [data description]);
    }
#endif
}

- (void)dismissViewController
{
    if (self.presentingViewController) {
        [self.presentingViewController dismissViewControllerAnimated:YES completion:nil];
    }
#if DEBUG_COBALT
    else {
        NSLog(@"dismissViewController: current controller was not presented");
    }
#endif
}

- (UIViewController *)getViewControllerForController:(NSString *)controller
{
    NSDictionary * configuration = [self getConfigurationForController:controller];
    
    if (configuration) {
        NSString * class = [configuration objectForKey:kIosController];
        NSString * nib = [configuration objectForKey:kIosNibName];
        BOOL pullToRefreshEnabled = [[configuration objectForKey:kPullToRefreshEnabled] boolValue];
        BOOL infiniteScrollEnabled = [[configuration objectForKey:kInfiniteScrollEnabled] boolValue];
        
        if (! class) {
#if DEBUG_COBALT
            NSLog(@"getCobaltViewControllerForController: no class found for %@ controller", controller);
#endif
            return nil;
        }
        
        // If nib not defined in configuration file, use same as class!
        if(! nib) {
            nib = class;
        }
        
        if ([CobaltViewController isValidViewControllerWithClass:class andNib:nib]) {
            if ([NSClassFromString(class) isSubclassOfClass:[CobaltViewController class]]) {
                CobaltViewController * viewController = [[NSClassFromString(class) alloc] initWithNibName:nib bundle:[NSBundle mainBundle]];
                viewController.isPullToRefreshActive = pullToRefreshEnabled;
                viewController.isInfiniteScrollActive = infiniteScrollEnabled;
                return viewController;
            }
            else {
                return [[NSClassFromString(class) alloc] initWithNibName:nib bundle:[NSBundle mainBundle]];
            }
        }
    }
    
    return nil;
}

+ (BOOL)isValidViewControllerWithClass:(NSString *)class andNib:(NSString *)nib
{
    BOOL isValidClass = NSClassFromString(class) != nil;
    BOOL isValidNib = (nib.length > 0
                       && ! [[NSBundle mainBundle] pathForResource:nib ofType:@"nib"]);
    
#if DEBUG_COBALT
    if (! isValidClass) {
        NSLog(@"isValidViewControllerWithClass: class %@ not found", class);
    }
    
    if(! isValidNib) {
        NSLog(@"isValidViewControllerWithClass: %@ nib does not exist!", nib);
    }
#endif
    
    return isValidClass && isValidNib;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark ALERTS METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
- (void)showAlertWithDict:(NSDictionary *)dict
{
    if (dict) {
        NSString * title = ([dict objectForKey:kJSAlertTitle] && [[dict objectForKey:kJSAlertTitle] isKindOfClass:[NSString class]]) ? [dict objectForKey:kJSAlertTitle] : @"";
        NSString * message = ([dict objectForKey:kJSAlertMessage] && [[dict objectForKey:kJSAlertMessage] isKindOfClass:[NSString class]])? [dict objectForKey:kJSAlertMessage] : @"";
        NSArray * buttons = ([dict objectForKey:kJSAlertButtons] && [[dict objectForKey:kJSAlertButtons] isKindOfClass:[NSArray class]]) ? [dict objectForKey:kJSAlertButtons] : [NSArray array];
        NSString * receiverType = ([dict objectForKey:kJSAlertCallbackReceiver] && [[dict objectForKey:kJSAlertCallbackReceiver] isKindOfClass:[NSString class]]) ? [dict objectForKey:kJSAlertCallbackReceiver] : @"";
        
        UIAlertView * alertView;
        //CREATE ALERTVIEW WITH DELEGATE SET
        if (receiverType
            && receiverType.length > 0) {
            if (buttons
                && buttons.count >=1) {
                alertView = [[UIAlertView alloc] initWithTitle:title message:message delegate:self cancelButtonTitle:[buttons objectAtIndex:0] otherButtonTitles:nil];
            }
            else {
                alertView = [[UIAlertView alloc] initWithTitle:title message:message delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
            }
            
            NSNumber * alertId = [dict objectForKey:kJSAlertID] ? [dict objectForKey:kJSAlertID] : [NSNumber numberWithInteger:-1];
            [alertView setTag:alertId.integerValue];
            
            //save the callbackId to send it later to web
            if ([receiverType isEqualToString:JSAlertCallbackReceiverWeb]) {
                NSString * callbackId = [NSString stringWithFormat:@"%@", [dict objectForKey:kJSCallback]];
                if (callbackId
                    && callbackId.length > 0) {
                    [alertCallbacks setObject:callbackId forKey:[NSString stringWithFormat:@"%d",alertId.intValue]];
                }
                else {
#if DEBUG_COBALT
                    NSLog(@"WARNING: invalid callback name for alertView with webCallback (null or empty)");
#endif
                }
            }
            else if(! [receiverType isEqualToString:JSAlertCallbackReceiverNative]) {
#if DEBUG_COBALT
                NSLog(@"WARNING: invalid callback receiver for alertView: %@", receiverType);
#endif
            }
        }
        //NO CALLBACK TO CALL ON CLICK ON BUTTONS
        else {
            if (buttons
                && buttons.count >=1) {
                alertView = [[UIAlertView alloc] initWithTitle:title message:message delegate:nil cancelButtonTitle:[buttons objectAtIndex:0] otherButtonTitles:nil];
            }
            else {
                alertView = [[UIAlertView alloc] initWithTitle:title message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
            }
        }
        
        //Add buttons
        int i = 0;
        for (i = 1 ; i < buttons.count ; i++) {
            [alertView addButtonWithTitle:[buttons objectAtIndex:i]];
        }
        
        [alertView performSelectorOnMainThread:@selector(show) withObject:nil waitUntilDone:YES];
    }
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    NSInteger alertViewId = alertView.tag;
    
    if ([alertCallbacks objectForKey:[NSString stringWithFormat:@"%d",alertViewId]]
        && [[alertCallbacks objectForKey:[NSString stringWithFormat:@"%d",alertViewId]] isKindOfClass:[NSString class]]) {
        NSString * callbackId = [alertCallbacks objectForKey:[NSString stringWithFormat:@"%d",alertViewId]];
        NSDictionary * dict = [NSDictionary dictionaryWithObjectsAndKeys:[NSNumber numberWithInteger:alertViewId], kJSAlertID,
                                                                            [NSNumber numberWithInteger:buttonIndex], kJSAlertButtonIndex,
                                                                            nil];
        [self sendCallbackResponseWithID:callbackId andObject:dict];
    }
    else {
        [self alertView:alertView WithTag:alertViewId clickedButtonAtIndex:buttonIndex];
    }
}

- (void)alertView:(UIAlertView *)alertView WithTag:(NSInteger)tag clickedButtonAtIndex:(NSInteger)buttonIndex
{
    
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark POP UP WEBVIEW
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

- (void)showPopUpWebviewWithDict:(NSDictionary *)dict
{
    if (popUpWebview
        && popUpWebview.superview) {
        [popUpWebview removeFromSuperview];
        [popUpWebview setDelegate:nil];
        popUpWebview = nil;
    }
    
    popupPageName = ([dict objectForKey:kJSPage] && [[dict objectForKey:kJSPage] isKindOfClass:[NSString class]]) ? [dict objectForKey:kJSPage] : @"";
    NSNumber * fadeDuration = ([dict objectForKey:kJSWebLayerFadeDuration] && [[dict objectForKey:kJSWebLayerFadeDuration] isKindOfClass:[NSNumber class]]) ? [dict objectForKey:kJSWebLayerFadeDuration] : [NSNumber numberWithFloat:0.3];
    
    popUpWebview = [[UIWebView alloc] initWithFrame:self.view.frame];
    
    [popUpWebview setDelegate:self];
    [popUpWebview setBackgroundColor:[UIColor clearColor]];
    [popUpWebview.scrollView setBounces:NO];
    if ([popUpWebview respondsToSelector:@selector(setKeyboardDisplayRequiresUserAction:)]) {
        [popUpWebview setKeyboardDisplayRequiresUserAction:NO];
    }
    
    [self loadContentInWebView:popUpWebview FromFileNamed:popupPageName atPath:[CobaltViewController ressourcePath] withRessourcesAtPath: [CobaltViewController ressourcePath]];
    popUpWebview.opaque = NO;
    [popUpWebview setAutoresizingMask:UIViewAutoresizingFlexibleWidth|UIViewAutoresizingFlexibleHeight];
    [popUpWebview setAlpha:0.0];
    [self.view addSubview:popUpWebview];
    [UIView animateWithDuration:fadeDuration.floatValue animations:^{
        [popUpWebview setAlpha:1.0];
    } completion:nil];
}


- (void)dismissPopUpWebviewWithDict:(NSDictionary *)dict
{
    NSNumber * fadeDuration = ([dict objectForKey:kJSWebLayerFadeDuration] && [[dict objectForKey:kJSWebLayerFadeDuration] isKindOfClass:[NSNumber class]]) ? [dict objectForKey:kJSWebLayerFadeDuration] : [NSNumber numberWithFloat:0.3];
    
    [UIView animateWithDuration:fadeDuration.floatValue animations:^{
        [popUpWebview setAlpha:0.0];
    } completion:^(BOOL finished) {
        [popUpWebview removeFromSuperview];
        [popUpWebview setDelegate:nil];
        popUpWebview = nil;

        [self onWebPopupDismissed:popupPageName];
        popupPageName = nil;
    }];
}

- (void)onWebPopupDismissed:(NSString *)filename
{
   NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:JSTypeEvent, kJSType,
                                                                    JSEventWebLayerOnDismiss, kJSEvent,
                                                                    filename, kJSValue,
                                                                    nil];
    [self executeScriptInWebView:webView WithDictionary:dict];
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark WEBVIEW DELEGATE METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType
{
    NSString * requestString = [[[request URL] absoluteString] stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    //if requestString contains haploidSpecialJSKey -> the JSON received is extracted.
    NSRange range = [requestString rangeOfString:haploidSpecialJSKey];
    if (range.location != NSNotFound) {
        NSString * jsonString = [requestString substringFromIndex:range.location + haploidSpecialJSKey.length];
        NSDictionary * jsonObj = [CobaltViewController JSONObjectWithString:jsonString];
        
        [fromJavaScriptOperationQueue addOperationWithBlock:^{
            [self handleDictionarySentByJavaScript:jsonObj];
        }];
        
        [self sendSimpleAcquitmentToJS];
        
        return NO;
    }
    
    [activityIndicator startAnimating];
    
    // stop queues until we load the webview
    [toJavaScriptOperationQueue setSuspended:YES];
    
    // Return YES to make sure regular navigation works as expected.
    return YES;
}


- (void)webViewDidFinishLoad:(UIWebView *)webView
{
    // start queue
    [toJavaScriptOperationQueue setSuspended:NO];
    
    [activityIndicator stopAnimating];
}


- (void)sendSimpleAcquitmentToJS
{
    NSDictionary * dict = [NSDictionary dictionaryWithObjectsAndKeys:   JSTypeCallBack, kJSType,
                                                                        JSCallbackSimpleAcquitment, kJSCallback,
                                                                        nil];
    [self executeScriptInWebView:webView WithDictionary:dict];
    
    if (popUpWebview) {
        [self executeScriptInWebView:popUpWebview WithDictionary:dict];
    }
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark COBALT TOAST DELEGATE METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

- (void)toastwillShow:(CobaltToast *)toast
{
    toastIsShown = YES;
#if DEBUG_COBALT
    NSLog(@"show");
#endif
}

- (void)toastwillHide:(CobaltToast *)toast
{
    toastIsShown = NO;
    if (toastsToShow.count > 0) {
        CobaltToast * toast = [toastsToShow objectAtIndex:0];
        [toast performSelectorOnMainThread:@selector(show) withObject:nil waitUntilDone:YES];
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
- (void)scrollViewDidScroll:(UIScrollView *)_scrollView
{
    if (isPullToRefreshActive
        && pullToRefreshTableHeaderView
        && pullToRefreshTableHeaderView.superview) {
        if (_scrollView.isDragging) {
            if (pullToRefreshTableHeaderView.state == RefreshStatePulling
                && _scrollView.contentOffset.y > -65.0
                && webView.scrollView.contentOffset.y < 0.0
                && ! _isRefreshing) {
                pullToRefreshTableHeaderView.state = RefreshStateNormal;
            }
            else if (pullToRefreshTableHeaderView.state == RefreshStateNormal
                     && _scrollView.contentOffset.y < -65.0
                     && ! _isRefreshing) {
                pullToRefreshTableHeaderView.state = RefreshStatePulling;
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
- (void)scrollViewDidEndDragging:(UIScrollView *)_scrollView willDecelerate:(BOOL)decelerate
{
    if (isPullToRefreshActive
        && pullToRefreshTableHeaderView
        && pullToRefreshTableHeaderView.superview) {
        if (_scrollView.contentOffset.y <= -65.0
            && ! _isRefreshing) {
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
    if (isPullToRefreshActive) {
        _isRefreshing = YES;
        pullToRefreshTableHeaderView.state = RefreshStateLoading;
        
        [UIView beginAnimations:nil context:nil];
        [UIView setAnimationDuration:0.2];
        webView.scrollView.contentInset = UIEdgeInsetsMake(pullToRefreshTableHeaderView.loadingHeight, 0.0f,
                                                           0.0f, 0.0f);
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
- (void)loadWebViewDataSource
{
    [self pullToRefreshDidRefresh];
}

//*********************************
// REFRESH TABLE VIEW DATA SOURCE *
//*********************************
/*!
 @method		- (void)refreshTableViewDataSource
 @abstract		Starts refreshing the table view data source.
 */
- (void)refreshWebViewDataSource
{
    NSDictionary * dict = [NSDictionary dictionaryWithObjectsAndKeys:   JSTypeEvent, kJSType,
                                                                        JSEventPullToRefresh, kJSEvent,
                                                                        JSCallbackPullToRefreshDidRefresh, kJSCallback,
                                                                        nil];
    [self executeScriptInWebView:webView WithDictionary:dict];
}

//**************
// DID REFRESH *
//**************
/*!
 @method		- (void)pullToRefreshDidRefresh
 @abstract		Tells the table view it has been refreshed.
 */
- (void)pullToRefreshDidRefresh {
    _isRefreshing = NO;
    
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationDuration:0.3];
    [UIView setAnimationDelegate:self];
    [UIView setAnimationDidStopSelector:@selector(didStop)];
    webView.scrollView.contentInset = UIEdgeInsetsMake(0.0f, 0.0f,
                                                       0.0f, 0.0f);
	[UIView commitAnimations];
}

//***********
// DID STOP *
//***********
/*!
 @method		- (void)didStop
 @abstract		Tells the table view the refresh animation has stopped.
 */
- (void)didStop
{
    pullToRefreshTableHeaderView.state = RefreshStateNormal;
}

- (void)stopPullToRefreshRefreshing
{
    if (isPullToRefreshActive
        && _isRefreshing) {
        [self pullToRefreshDidRefresh];
        NSDictionary * dict = [NSDictionary dictionaryWithObjectsAndKeys:   JSTypeEvent, kJSType,
                                                                            JSPullToRefreshCancelled, kJSName,
                                                                            nil];
        [self executeScriptInWebView:self.webView WithDictionary:dict];
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

- (void)loadMoreContentInWebview
{
    NSDictionary * dict = [NSDictionary dictionaryWithObjectsAndKeys:   JSTypeEvent, kJSType,
                                                                        JSEventInfiniteScroll, kJSEvent,
                                                                        JSCallbackInfiniteScrollDidRefresh, kJSCallback,
                                                                        nil];
    [self executeScriptInWebView:self.webView WithDictionary:dict];
}

- (void)infiniteScrollDidRefresh
{
    _isLoadingMore = NO;
    
    [self moreItemsLoaded];
}

- (void)moreItemsLoaded
{

}

- (void)stopInfiniteScrollRefreshing
{
    if (isInfiniteScrollActive
        && _isLoadingMore) {
        _isLoadingMore = NO;
        NSDictionary * dict = [NSDictionary dictionaryWithObjectsAndKeys:   JSTypeEvent, kJSType,
                                                                            JSInfiniteScrollCancelled, kJSName,
                                                                            nil];
        [self executeScriptInWebView:webView WithDictionary:dict];
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

@synthesize arrowImageView,
            lastUpdatedLabel,
            loadingHeight,
            progressView,
            state,
            statusLabel;

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
- (void)setLastUpdated:(NSString *)lastUpdated
{
    lastUpdatedLabel.text = lastUpdated;
}

//************
// SET STATE *
//************
/*!
 @method		- (void)setState:(RefreshState)newState
 @abstract		Sets the refresh state.
 @param         newState    The refresh state to set.
 */
- (void)setState:(RefreshState)newState
{
    switch (newState) {
        case RefreshStateNormal:
            if (state == RefreshStatePulling) {
                [UIView beginAnimations:nil context:nil];
                [UIView setAnimationDuration:0.2];
                arrowImageView.transform = CGAffineTransformIdentity;
                [UIView commitAnimations];
            }
            statusLabel.text = [self textForState:newState];
            [progressView stopAnimating];
            arrowImageView.hidden = NO;
            arrowImageView.transform = CGAffineTransformIdentity;
            break;
        case RefreshStatePulling:
            statusLabel.text = [self textForState:newState];
            [UIView beginAnimations:nil context:nil];
            [UIView setAnimationDuration:0.2];
            arrowImageView.transform = CGAffineTransformMakeRotation(M_PI);
            [UIView commitAnimations];
            break;
        case RefreshStateLoading:
            statusLabel.text = [self textForState:newState];
            [progressView startAnimating];
            arrowImageView.hidden = YES;
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
- (NSString *)textForState:(RefreshState)newState
{
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
