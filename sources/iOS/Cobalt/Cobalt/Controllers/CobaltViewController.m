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

#import "Cobalt.h"
#import "iToast.h"

#import "CobaltPluginManager.h"

@interface CobaltViewController ()
/*!
 @method		+(void) executeScriptInWebView:(UIWebView *)mWebView withDictionary:(NSDictionary *)dict
 @abstract		this method sends a JSON to the webView to execute a script (allows interactions from the native to the webView)
 @param         mWebView : the webview where the script is due to be executed
 @param         dict : a NSDictionary that contains the necessary informations to execute the script
 @discussion    the webView MUST have a function "nativeBridge.execute(%@);" that receives the JSON (representing dict) as parameter
 @discussion    This method should NOT be overridden in subclasses.
 */
- (void)executeScriptInWebView:(UIWebView *)mWebView withDictionary:(NSDictionary *)dict;
@end

@implementation CobaltViewController
@synthesize activityIndicator,
            isInfiniteScrollEnabled,
            isPullToRefreshEnabled,
            pageName,
            webLayer,
            webView;

NSMutableDictionary * alertCallbacks;

NSMutableArray * toastsToShow;
BOOL toastIsShown;

NSString * webLayerPage;



- (void)viewDidLoad
{
    [super viewDidLoad];
    
    // Do any additional setup after loading the view from its nib.
    [self customWebView];
    [webView setDelegate:self];
    
    toJavaScriptOperationQueue = [[NSOperationQueue alloc] init] ;
    [toJavaScriptOperationQueue setSuspended:YES];
    
    fromJavaScriptOperationQueue = [[NSOperationQueue alloc] init] ;
    
    _alertViewCounter = 0;
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
    if (isPullToRefreshEnabled) {
        UIRefreshControl *refresh = [[UIRefreshControl alloc] init];
        
        [refresh addTarget:self action:@selector(refresh) forControlEvents:UIControlEventValueChanged];
        
        self.refreshControl = refresh;

        [self customizeRefreshControlWithAttributedRefreshText: [[NSAttributedString alloc] initWithString:@"Tirer pour mettre à jour"] andAttributedRefreshText: [[NSAttributedString alloc] initWithString:@"Chargement en cours"] andTintColor: [UIColor grayColor]];
    }
    
    [webView.scrollView setDelegate:self];
    //[self.tableView setScrollEnabled: NO];
    
    [self loadPage:pageName inWebView:webView];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
}

- (void)dealloc
{
    toJavaScriptOperationQueue = nil;
    fromJavaScriptOperationQueue = nil;
    _delegate = nil;
    webView = nil;
    activityIndicator = nil;
    pageName = nil;
    webLayer = nil;
    
    [[NSNotificationCenter defaultCenter] postNotificationName: viewControllerDeallocatedNotification  object: self];
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

- (void)setDelegate:(id)delegate
{
    if (delegate) {
        _delegate = delegate;
    }
}

- (void)customWebView
{
    
}

- (void)loadPage:(NSString *)page inWebView:(UIWebView *)mWebView
{
    NSURL * fileURL = [NSURL fileURLWithPath:[[Cobalt resourcePath] stringByAppendingPathComponent:page]];
    NSURLRequest * requestURL = [NSURLRequest requestWithURL:fileURL];
    [mWebView loadRequest:requestURL];
}

+ (NSDictionary *)getConfigurationForController:(NSString *)controller
{
    NSDictionary * configuration = [Cobalt getControllersConfiguration];
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
            }
#endif
        }
        
        NSDictionary * defaultControllerConfiguration = [configuration objectForKey:JSNavigationControllerDefault];
        
        if (defaultControllerConfiguration
            && [defaultControllerConfiguration isKindOfClass:[NSDictionary class]]) {
            return defaultControllerConfiguration;
        }
#if DEBUG_COBALT
        else {
            NSLog(@"getConfigurationForController: no configuration found for default controller");
        }
#endif
    }
    
    return nil;
}

- (void)executeScriptInWebView:(UIWebView *)mWebView withDictionary:(NSDictionary *)dict
{
    [toJavaScriptOperationQueue addOperationWithBlock:^{
        if ([NSJSONSerialization isValidJSONObject:dict]) {
            NSError * error;
            NSString * message =[[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:dict options:0 error:&error] encoding:NSUTF8StringEncoding];
            
            if (message) {
                // Ensures there is no raw newLine in message.
                message = [[message componentsSeparatedByCharactersInSet:[NSCharacterSet newlineCharacterSet]] componentsJoinedByString:@""];
                
                NSString * script = [NSString stringWithFormat:@"cobalt.execute(%@);", message];
                
                [mWebView performSelectorOnMainThread:@selector(stringByEvaluatingJavaScriptFromString:) withObject:script waitUntilDone:NO];
            }
#if DEBUG_COBALT
            else {
                NSLog(@"executeScriptInWebView: Error while generating JSON %@\n%@", [dict description], [error localizedFailureReason]);
            }
#endif
        }
    }];
}

- (void)sendCallback:(NSString *)callback withData:(NSObject *)data
{
    if (callback
        && callback.length > 0) {
        NSDictionary * dict = [NSDictionary dictionaryWithObjectsAndKeys:   JSTypeCallBack, kJSType,
                                                                            callback, kJSCallback,
                                                                            data, kJSData,
                                                                            nil];
        [self executeScriptInWebView:webView withDictionary:dict];
    }
#if DEBUG_COBALT
    else {
        NSLog(@"sendCallback: invalid callback (null or empty)");
    }
#endif
}

- (void)sendEvent:(NSString *)event withData:(NSObject *)data andCallback:(NSString *)callback
{
    if (event
        && event.length > 0) {
        NSMutableDictionary * dict = [NSMutableDictionary dictionaryWithObjectsAndKeys: JSTypeEvent, kJSType,
                                                                                        event, kJSEvent,
                                                                                        nil];
        if (data) {
            [dict setObject:data forKey:kJSData];
        }
        if (callback) {
            [dict setObject:callback forKey:kJSCallback];
        }
        
        [self executeScriptInWebView:webView withDictionary:dict];
    }
#if DEBUG_COBALT
    else {
        NSLog(@"sendEvent: invalid event (null or empty)");
    }
#endif
}

- (void)sendCallbackToWebLayer:(NSString *)callback withData:(NSObject *)data
{
    if (callback
        && callback.length > 0) {
        NSDictionary * dict = [NSDictionary dictionaryWithObjectsAndKeys:   JSTypeCallBack, kJSType,
                               callback, kJSCallback,
                               data, kJSData,
                               nil];
        [self executeScriptInWebView:webLayer withDictionary:dict];
    }
#if DEBUG_COBALT
    else {
        NSLog(@"sendCallbackToWebLayer: invalid callback (null or empty)");
    }
#endif
}

- (void)sendEventToWebLayer:(NSString *)event withData:(NSObject *)data andCallback:(NSString *)callback
{
    if (event
        && event.length > 0) {
        NSMutableDictionary * dict = [NSMutableDictionary dictionaryWithObjectsAndKeys: JSTypeEvent, kJSType,
                                      event, kJSEvent,
                                      nil];
        if (data) {
            [dict setObject:data forKey:kJSData];
        }
        if (callback) {
            [dict setObject:callback forKey:kJSCallback];
        }
        
        [self executeScriptInWebView:webLayer withDictionary:dict];
    }
#if DEBUG_COBALT
    else {
        NSLog(@"sendEventToWebLayer: invalid event (null or empty)");
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
                    [self.refreshControl endRefreshing];
                    self.refreshControl.attributedTitle = _ptrRefreshText;
                    _isRefreshing = NO;
                }
                else if ([callback isEqualToString:JSCallbackInfiniteScrollDidRefresh]) {
                    [self onInfiniteScrollDidRefresh];
                }
                else {
#if DEBUG_COBALT
                    NSLog(@"handleDictionarySentByJavaScript: unhandled callback %@", [dict description]);
#endif
                    if (_delegate != nil
                        && [_delegate respondsToSelector:@selector(onUnhandledCallback:withData:)]) {
                        return [_delegate onUnhandledCallback:callback withData:data];
                    }
                    else {
                        return NO;
                    }
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
            if (_delegate != nil
                && [_delegate respondsToSelector:@selector(onCobaltIsReady)]) {
                [_delegate onCobaltIsReady];
            }
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
                if (_delegate != nil
                    && [_delegate respondsToSelector:@selector(onUnhandledEvent:withData:andCallback:)]) {
                    return [_delegate onUnhandledEvent:event withData:data andCallback:callback];
                }
                else {
                    return NO;
                }
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
                        [self pushViewControllerWithData:data];
                    }
#if DEBUG_COBALT
                    else {
                        NSLog(@"handleDictionarySentByJavaScript: data field missing or not an object (message: %@)", [dict description]);
                    }
#endif
                }
                //POP
                else if ([action isEqualToString:JSActionNavigationPop]) {
                    [self popViewController];
                }
                //MODAL
                else if ([action isEqualToString:JSActionNavigationModal]) {
                    NSDictionary * data = [dict objectForKey:kJSData];
                    if (data
                        && [data isKindOfClass:[NSDictionary class]]) {
                            [self presentViewControllerWithData:data];
                    }
#if DEBUG_COBALT
                    else {
                        NSLog(@"handleDictionarySentByJavaScript: data field missing or not an object (message: %@)", [dict description]);
                    }
#endif
                }
                //DISMISS
                else if ([action isEqualToString:JSActionNavigationDismiss]) {
                    [self dismissViewController];
                }
                else {
#if DEBUG_COBALT
                    NSLog(@"handleDictionarySentByJavaScript: unhandled navigation %@", [dict description]);
#endif
                    if (_delegate != nil
                        && [_delegate respondsToSelector:@selector(onUnhandledMessage:)]) {
                        return [_delegate onUnhandledMessage:dict];
                    }
                    else {
                        return NO;
                    }
                }
                
            }
#if DEBUG_COBALT
            else {
                NSLog(@"handleDictionarySentByJavaScript: action field missing or not a string (message: %@)", [dict description]);
            }
#endif
        }
        
        // UI
        else if ([type isEqualToString:kJSTypeUI]) {
            NSString * control = [dict objectForKey:kJSUIControl];
            NSDictionary * data = [dict objectForKey:kJSData];
            
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
                                [toast show];
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
                    [self showAlert:dict];
                }
                
                else {
#if DEBUG_COBALT
                    NSLog(@"handleDictionarySentByJavaScript: unhandled message %@", [dict description]);
#endif
                    if (_delegate != nil
                        && [_delegate respondsToSelector:@selector(onUnhandledMessage:)]) {
                        return [_delegate onUnhandledMessage:dict];
                    }
                    else {
                        return NO;
                    }
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
            NSDictionary * data = [dict objectForKey:kJSData];
            
            if (action
                && [action isKindOfClass:[NSString class]]) {
                
                // SHOW
                if ([action isEqualToString:JSActionWebLayerShow]) {
                    if (data
                        && [data isKindOfClass:[NSDictionary class]]) {
                        [self showWebLayer:data];
                    }
#if DEBUG_COBALT
                    else {
                        NSLog(@"handleDictionarySentByJavaScript: data field missing or not an object (message: %@)", [dict description]);
                        
                    }
#endif
                }
                
                // DISMISS
                else if([action isEqualToString:JSActionWebLayerDismiss]) {
                    [self dismissWebLayer:data];
                }
            }
#if DEBUG_COBALT
            else {
                NSLog(@"handleDictionarySentByJavaScript: action field missing or not a string (message: %@)", [dict description]);

            }
#endif
        }
        // INTENT
        else if ([type isEqualToString:kJSTypeIntent]) {
            NSString * action = [dict objectForKey:kJSAction];
            NSDictionary * data = [dict objectForKey:kJSData];
            
            if (action
                && [action isKindOfClass:[NSString class]]) {
                
                // OPEN EXTERNAL URL
                if ([action isEqualToString: kJSActionOpenExternalUrl]) {
                    NSString * url = [data objectForKey: kJSUrl];
                    if([url isKindOfClass: [NSString class]]) {
                        [[UIApplication sharedApplication] openURL: [NSURL URLWithString: url]];
                    }
                }
            }
        }
        // PLUGIN
        else if ([type isEqualToString: kJSTypePlugin]) {
            [[CobaltPluginManager sharedInstance] onMessageFromCobaltViewController: self andData: dict];
        }
        else {
#if DEBUG_COBALT
            NSLog(@"handleDictionarySentByJavaScript: unhandled message %@", [dict description]);
#endif
            if (_delegate != nil
                && [_delegate respondsToSelector:@selector(onUnhandledMessage:)]) {
                return [_delegate onUnhandledMessage:dict];
            }
            else {
                return NO;
            }
        }
    }
    else {
#if DEBUG_COBALT
        NSLog(@"handleDictionarySentByJavaScript: unhandled message %@", [dict description]);
#endif
        if (_delegate != nil
            && [_delegate respondsToSelector:@selector(onUnhandledMessage:)]) {
            return [_delegate onUnhandledMessage:dict];
        }
        else {
            return NO;
        }
    }
    
    return YES;
}

- (void) sendMessage:(NSDictionary *) message {
    //default implementation does nothing
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
        UIViewController * viewController = [CobaltViewController getViewControllerForController:controller andPage:page];
        if (viewController) {
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
        UIViewController * viewController = [CobaltViewController getViewControllerForController:controller andPage:page];
        if (viewController) {
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

+ (UIViewController *)getViewControllerForController:(NSString *)controller andPage:(NSString *)page
{
    UIViewController * viewController = [CobaltViewController getViewControllerForController:controller];
    
    if (viewController
        && [[viewController class] isSubclassOfClass:[CobaltViewController class]]) {
        // Sets page
        ((CobaltViewController *)viewController).pageName = page;
    }
    
    return viewController;
}

+ (UIViewController *)getViewControllerForController:(NSString *)controller
{
    NSDictionary * configuration = [CobaltViewController getConfigurationForController:controller];
    
    if (configuration) {
        NSString * class = [configuration objectForKey: kIos];
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
        
        //if nib file does no exists, use default one i.e. CobaltViewController.xib
        if([[NSBundle mainBundle] pathForResource:nib ofType:@"nib"] == nil)
        {
            nib = @"CobaltViewController";
        }
        
        if ([CobaltViewController isValidViewControllerWithClass:class andNib:nib]) {
            if ([NSClassFromString(class) isSubclassOfClass:[CobaltViewController class]]) {
                CobaltViewController * viewController = [[NSClassFromString(class) alloc] initWithNibName:nib bundle:[NSBundle mainBundle]];
                viewController.isPullToRefreshEnabled = pullToRefreshEnabled;
                viewController.isInfiniteScrollEnabled = infiniteScrollEnabled;
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
                       && [[NSBundle mainBundle] pathForResource:nib ofType:@"nib"] != nil);
    
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
- (void)showAlert:(NSDictionary *)dict
{
    NSDictionary * data = [dict objectForKey:kJSData];
    NSString * callback = [dict objectForKey:kJSCallback];
    
    if (data
        && [data isKindOfClass:[NSDictionary class]]) {
        NSString * title = ([data objectForKey:kJSAlertTitle] && [[data objectForKey:kJSAlertTitle] isKindOfClass:[NSString class]]) ? [data objectForKey:kJSAlertTitle] : @"";
        NSString * message = ([data objectForKey:kJSAlertMessage] && [[data objectForKey:kJSAlertMessage] isKindOfClass:[NSString class]]) ? [data objectForKey:kJSAlertMessage] : @"";
        NSArray * buttons = ([data objectForKey:kJSAlertButtons] && [[data objectForKey:kJSAlertButtons] isKindOfClass:[NSArray class]]) ? [data objectForKey:kJSAlertButtons] : [NSArray array];
        
        UIAlertView * alertView;
        id delegate = (callback && [callback isKindOfClass:[NSString class]]) ? self : nil;
        
        if (! buttons.count) {
            alertView = [[UIAlertView alloc] initWithTitle:title message:message delegate:delegate cancelButtonTitle:@"OK" otherButtonTitles:nil];
        }
        else {
            alertView = [[UIAlertView alloc] initWithTitle:title message:message delegate:delegate cancelButtonTitle:[buttons objectAtIndex:0] otherButtonTitles:nil];
            
            // Add other buttons
            for (int i = 1 ; i < buttons.count ; i++) {
                [alertView addButtonWithTitle:[buttons objectAtIndex:i]];
            }
        }
        
        if (delegate) {
            alertView.tag = ++_alertViewCounter;
            [alertCallbacks setObject:callback forKey:[NSString stringWithFormat:@"%ld", (long)alertView.tag]];
        }
        
        [alertView show];
    }
#if DEBUG_COBALT
    else {
        NSLog(@"showAlert: data field missing or not an object (message: %@)", [dict description]);
    }
#endif
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    NSString * callback = [alertCallbacks objectForKey:[NSString stringWithFormat:@"%ld", (long)alertView.tag]];
    
    if (callback
        && [callback isKindOfClass:[NSString class]]) {
        NSDictionary * data = [NSDictionary dictionaryWithObjectsAndKeys:   [NSNumber numberWithInteger:buttonIndex], kJSAlertButtonIndex,
                                                                            nil];
        [self sendCallback:callback withData:data];
    }
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark WEB LAYER
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

- (void)showWebLayer:(NSDictionary *)data
{
    if (webLayer
        && webLayer.superview) {
        [webLayer removeFromSuperview];
        [webLayer setDelegate:nil];
        webLayer = nil;
    }
    
    webLayerPage = [data objectForKey:kJSPage];
    NSNumber * fadeDuration = ([data objectForKey:kJSWebLayerFadeDuration] && [[data objectForKey:kJSWebLayerFadeDuration] isKindOfClass:[NSNumber class]]) ? [data objectForKey:kJSWebLayerFadeDuration] : [NSNumber numberWithFloat:0.3];
    
    if (webLayerPage) {
        webLayer = [[UIWebView alloc] initWithFrame:self.view.bounds];
        [webLayer setDelegate:self];
        [webLayer setAlpha:0.0];
        [webLayer setAutoresizingMask:UIViewAutoresizingFlexibleWidth|UIViewAutoresizingFlexibleHeight];
        [webLayer setBackgroundColor:[UIColor clearColor]];
        [webLayer setOpaque:NO];
        [webLayer.scrollView setBounces:NO];
        
        if ([webLayer respondsToSelector:@selector(setKeyboardDisplayRequiresUserAction:)]) {
            [webLayer setKeyboardDisplayRequiresUserAction:NO];
        }
        
        [self loadPage:webLayerPage inWebView:webLayer];
        
        [self.view addSubview:webLayer];
        [UIView animateWithDuration:fadeDuration.floatValue animations:^{
            [webLayer setAlpha:1.0];
        } completion:nil];
    }
#if DEBUG_COBALT
    else {
        NSLog(@"showWebLayer: page field missing or not a string (data: %@)", data);
    }
#endif
}

// TODO: like Android code, implement getDataForDismiss
- (void)dismissWebLayer:(NSDictionary *)data
{
    // Guillaume told me that having a customizable fadeDuration is a bad idea. So, it's a fixed fadeDuration...
    // REMEMBER, So if Guillaume tell me the opposite, it owe me a chocolate croissant :)
    NSNumber * fadeDuration = [NSNumber numberWithFloat:0.3];
    //NSNumber * fadeDuration = (dict && [dict objectForKey:kJSWebLayerFadeDuration] && [[dict objectForKey:kJSWebLayerFadeDuration] isKindOfClass:[NSNumber class]]) ? [dict objectForKey:kJSWebLayerFadeDuration] : [NSNumber numberWithFloat:0.3];
    
    [UIView animateWithDuration:fadeDuration.floatValue animations:^{
        [webLayer setAlpha:0.0];
    } completion:^(BOOL finished) {
        [webLayer removeFromSuperview];
        [webLayer setDelegate:nil];
        webLayer = nil;

        [self onWebLayerDismissed:webLayerPage withData:data];
        webLayerPage = nil;
    }];
}

- (void)onWebLayerDismissed:(NSString *)page withData:(NSDictionary *)dict
{
    NSDictionary * data = [NSDictionary dictionaryWithObjectsAndKeys:   page, kJSPage,
                                                                        dict, kJSData,
                                                                        nil];
    [self sendEvent:JSEventWebLayerOnDismiss withData:data andCallback:nil];
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark WEBVIEW DELEGATE METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType
{
    NSString * requestURL = [[[request URL] absoluteString] stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    
    // if requestURL contains haploidSpecialJSKey, extracts the JSON received.
    NSRange range = [requestURL rangeOfString:haploidSpecialJSKey];
    if (range.location != NSNotFound) {
        NSString * json = [requestURL substringFromIndex:range.location + haploidSpecialJSKey.length];
        NSDictionary * jsonObj = [Cobalt JSONObjectWithString:json];
        
        [fromJavaScriptOperationQueue addOperationWithBlock:^{
            dispatch_sync(dispatch_get_main_queue(), ^{
                [self handleDictionarySentByJavaScript:jsonObj];
            });
        }];
        
        [self sendACK];
        
        return NO;
    }
    
    [activityIndicator startAnimating];
    
    // Stops queues until Web view is loaded
    [toJavaScriptOperationQueue setSuspended:YES];
    
    // Returns YES to ensure regular navigation working as expected.
    return YES;
}


- (void)webViewDidFinishLoad:(UIWebView *)webView
{
    // start queue
    [toJavaScriptOperationQueue setSuspended:NO];
    
    [activityIndicator stopAnimating];
}


- (void)sendACK
{
    [self sendCallback:JSCallbackSimpleAcquitment withData:nil];
    
    if (webLayer) {
        NSDictionary * dict = [NSDictionary dictionaryWithObjectsAndKeys:   JSTypeCallBack, kJSType,
                                                                            JSCallbackSimpleAcquitment, kJSCallback,
                                                                            nil];
        [self executeScriptInWebView:webLayer withDictionary:dict];
    }
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark COBALT TOAST DELEGATE METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

- (void)toastWillShow:(CobaltToast *)toast
{
    toastIsShown = YES;
#if DEBUG_COBALT
    NSLog(@"toastWillShow");
#endif
}

- (void)toastWillHide:(CobaltToast *)toast
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
    if (_scrollView.isDragging) {
        if (isInfiniteScrollEnabled) {
            if (webView.scrollView.contentOffset.y > (_scrollView.contentSize.height - _scrollView.frame.size.height)
               && !_isLoadingMore){
                [self loadMoreItems];
            }
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
    if (isPullToRefreshEnabled) {
        _isRefreshing = YES;
        
        self.refreshControl.attributedTitle = _ptrRefreshingText;
        
        [self refreshWebView];
    }
}

//*******************
// REFRESH WEB VIEW *
//*******************
/*!
 @method		- (void)refreshWebView
 @abstract		Sends event to refresh Web view content.
 */
- (void)refreshWebView
{
    [self sendEvent:JSEventPullToRefresh withData:nil andCallback:JSCallbackPullToRefreshDidRefresh];
}


/*!
 @method		- (void)customizeRefreshControlWithAttributedRefreshText:(NSAttributedString *)attributedRefreshText andAttributedRefreshText:(NSAttributedString *)attributedRefreshingText andTintColor: (UIColor *)tintColor;
 @abstract		customize native pull to refresh control
 */
- (void)customizeRefreshControlWithAttributedRefreshText:(NSAttributedString *)attributedRefreshText andAttributedRefreshText:(NSAttributedString *)attributedRefreshingText andTintColor: (UIColor *)tintColor {
    _ptrRefreshText = attributedRefreshText;
    _ptrRefreshingText = attributedRefreshingText;
    
    self.refreshControl.attributedTitle = attributedRefreshText;
    self.refreshControl.tintColor = tintColor;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark INFINITE SCROLL METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// TODO: How can IS works with these methods? O_o
- (void)loadMoreItems
{
    _isLoadingMore = YES;
    
    [self loadMoreContentInWebview];
}

- (void)loadMoreContentInWebview
{
    [self sendEvent:JSEventInfiniteScroll withData:nil andCallback:JSCallbackInfiniteScrollDidRefresh];
}

- (void)onInfiniteScrollDidRefresh
{
    _isLoadingMore = NO;
}

@end
