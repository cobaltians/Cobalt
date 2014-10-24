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

////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark GET POST REQUEST
#pragma mark -
////////////////////////////////////////////////////////////////////////////////////

@interface CobaltButton : UIButton

@property (nonatomic, retain) NSString * iconName;
@end

@implementation CobaltButton
@end

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


- (id) init {
    if(self = [super init]) {
        _navigationBarTintColor = [[UINavigationBar appearance] barTintColor];
        _toolbarTintColor = [[UIToolbar appearance] barTintColor];
    }
    
    return self;
}

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

        [self customizeRefreshControlWithAttributedRefreshText: [[NSAttributedString alloc] initWithString:@"Pull to refresh"] andAttributedRefreshText: [[NSAttributedString alloc] initWithString:@"Refreshing"] andTintColor: [UIColor grayColor]];
    }
    
    [webView.scrollView setDelegate:self];
    
    if(!self.isPullToRefreshEnabled)
        [self.tableView setScrollEnabled: NO];
    
    [self loadPage:pageName inWebView:webView];
    
    if([JSContext class]) {
        JSContext *context = [self.webView valueForKeyPath:@"documentView.webView.mainFrame.javaScriptContext"];
    
        //[context setExceptionHandler:^(JSContext *context, JSValue *value) {
        //    NSLog(@"%@", value);
        //}];
    
        // register CobaltViewController class
        context[@"cobaltViewController"] = self;
    }
    
    [self configureBars];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onAppBackground:) name: kOnAppBackgroundNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onAppForeground:) name:kOnAppForegroundNotification object:nil];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    self.navigationController.navigationBar.barTintColor = _navigationBarTintColor;
    self.navigationController.toolbar.barTintColor = _toolbarTintColor;
    
    self.navigationController.toolbarHidden = !self.hasToolBar;
    
    [self sendEvent: JSEventOnPageShown withData:nil andCallback:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onPageShown:) name:kOnPageShownNotification object:nil];
}


- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear: animated];
    
    [[NSNotificationCenter defaultCenter] removeObserver:self name:kOnPageShownNotification object:nil];
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
    
    [[NSNotificationCenter defaultCenter] removeObserver:self name:kOnAppBackgroundNotification object:nil];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:kOnAppForegroundNotification object:nil];
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark NOTIFICATIONS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

- (void)onAppBackground:(NSNotification *)notification {
    [self sendEvent: JSEventOnAppBackground withData: nil andCallback: nil];
}

- (void)onAppForeground:(NSNotification *)notification {
    [self sendEvent: JSEventOnAppForeground withData: nil andCallback: nil];
}

- (void)onPageShown:(NSNotification *)notification {
    [self sendEvent: JSEventOnPageShown withData: nil andCallback: nil];
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

- (BOOL)_handleDictionarySentByJavaScript:(NSString *)json {
    NSDictionary * jsonObj = [Cobalt JSONObjectWithString:json];
    return [self handleDictionarySentByJavaScript: jsonObj];
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
            NSString * versionWeb = [dict objectForKey:KJSVersion];
            if (![IOSCurrentVersion isEqualToString:versionWeb]) {
                NSLog(@"Warning : Cobalt version mismatch : iOS Cobalt version is %@ but Web Cobalt version is %@. You should fix this.",IOSCurrentVersion, versionWeb);
            }else{
                NSLog(@"handleDictionarySentByJavaScript: CobaltIsReady, version %@",versionWeb);
            }
            
            
#endif
        }
        // EVENT
        else if ([type isEqualToString:JSTypeEvent]) {
            NSString * event = [dict objectForKey:kJSEvent];
            NSDictionary * data = [dict objectForKey:kJSData];
            NSString * callback = [dict objectForKey:kJSCallback];
            
            if (event &&
                [event isKindOfClass:[NSString class]]) {
                if (_delegate != nil
                    && [_delegate respondsToSelector:@selector(onUnhandledEvent:withData:andCallback:)]) {
                    BOOL toReturn = [_delegate onUnhandledEvent:event withData:data andCallback:callback];
                    if(!toReturn) {
#if DEBUG_COBALT
                        NSLog(@"handleDictionarySentByJavaScript: unhandled event %@", [dict description]);
#endif
                    }
                    
                    return toReturn;
                }
                else {
#if DEBUG_COBALT
                    NSLog(@"handleDictionarySentByJavaScript: unhandled event %@", [dict description]);
#endif
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
                    NSDictionary * controllersConfigration = [Cobalt getControllersConfiguration];
                    NSDictionary * data = [dict objectForKey:kJSData];
                    
                    if (data
                        && [data isKindOfClass:[NSDictionary class]]) {
                        NSString * controllerKey = [data objectForKey: kJSNavigationController];
                        NSString * controllerPage = [data objectForKey: kJSPage];
                        
                        NSDictionary * controllerConfiguration = [controllersConfigration objectForKey: controllerKey];
                        NSString * controllerClassName = [controllerConfiguration objectForKey: kIos];
                        
                        for(UIViewController * viewController in self.navigationController.viewControllers) {
                            if([viewController isKindOfClass: [CobaltViewController class]]) {
                                if([viewController isKindOfClass: NSClassFromString(controllerClassName)] && [((CobaltViewController *)viewController).pageName isEqualToString: controllerPage]) {
                                    [self.navigationController popToViewController: viewController animated: YES];
                                    break;
                                }
                            }
                        }
                        
#if DEBUG_COBALT
                        NSLog(@"pop: controller/page not found");
#endif
                    }
                    else
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
                //REPLACE
                else if ([action isEqualToString:kJSActionNavigationReplace]) {
                    [self dismissViewController];
                    NSDictionary * data = [dict objectForKey:kJSData];
                    if (data
                        && [data isKindOfClass:[NSDictionary class]]) {
                        [self replaceViewControllerWithData:data];
                    }
#if DEBUG_COBALT
                    else {
                        NSLog(@"handleDictionarySentByJavaScript: data field missing or not an object (message: %@)", [dict description]);
                    }
#endif

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
                
                // BARS
                else if ([control isEqualToString: JSControlBars]) {
                    if (data
                        && [data isKindOfClass:[NSDictionary class]]) {
                        NSString * action = [data objectForKey: kJSAction];
                        NSString * button = [data objectForKey: kJSButton];
                        
                        if (action
                            && [action isKindOfClass: [NSString class]]) {
                            
                            
                            if([action isEqualToString: @"showButton"]) {
                                if (button
                                    && [button isKindOfClass: [NSString class]]) {
                                    
                                    NSMutableArray * barActionsArray = [self.barsConfiguration objectForKey: kBarActions];
                                    for(NSMutableDictionary * barAction in barActionsArray) {
                                        if([[barAction objectForKey: kBarActionName] isEqualToString: button]) {
                                            [barAction setObject: @"true" forKey: kBarActionVisible];
                                            break;
                                        }
                                    }
                                    
                                    [self configureBars];
                                }
                            } else if([action isEqualToString: @"hideButton"]) {
                                if (button
                                    && [button isKindOfClass: [NSString class]]) {
                                    NSMutableArray * barActionsArray = [self.barsConfiguration objectForKey: kBarActions];
                                    for(NSMutableDictionary * barAction in barActionsArray) {
                                        if([[barAction objectForKey: kBarActionName] isEqualToString: button]) {
                                            [barAction setObject: @"false" forKey: kBarActionVisible];
                                            break;
                                        }
                                    }
                                    
                                    [self configureBars];
                                }
                            }
                            else if([action isEqualToString: @"hide"]) {
                                [self.navigationController setToolbarHidden: YES animated: YES];
                                [self.navigationController setNavigationBarHidden: YES animated: YES];
                                
                            } else if([action isEqualToString: @"show"]) {
                                [self.navigationController setToolbarHidden: NO animated: YES];
                                [self.navigationController setNavigationBarHidden: NO animated: YES];
                                
                            } else if([action isEqualToString: @"setTexts"]) {
                                NSDictionary * texts = [data objectForKey: kJSTexts];
                                for(NSString * key in [texts allKeys]) {
                                    if([key isEqualToString: kJSTitleBar])
                                    {
                                        [self.navigationItem setTitle: [texts objectForKey: kJSTitleBar]];
                                    } else {
                                        NSMutableArray * barActionsArray = [self.barsConfiguration objectForKey: kBarActions];
                                        for(NSMutableDictionary * barAction in barActionsArray) {
                                            if([[barAction objectForKey: kBarActionName] isEqualToString: key]) {
                                                [barAction setObject: [texts objectForKey: key] forKey: kBarActionTitle];
                                                break;
                                            }
                                        }
                                        
                                        [self configureBars];
                                    }
                                }
                            } else if ([action isEqualToString: @"setVisibility"]) {
                                NSDictionary * visibilities = [data objectForKey: kJSVisibility];
                                BOOL topVisible = [[visibilities objectForKey: kJSTop] boolValue];
                                BOOL bottomVisible = [[visibilities objectForKey: kJSBottom] boolValue];
                                
                                [self.navigationController setNavigationBarHidden: !topVisible animated:YES];
                                [self.navigationController setToolbarHidden: !bottomVisible animated:YES];
                            }
                            
                            
                        }
#if DEBUG_COBALT
                        else {
                            NSLog(@"handleDictionarySentByJavaScript: action field missing or not a string (message: %@)", [dict description]);
                        }
#endif
                    }
#if DEBUG_COBALT
                    else {
                        NSLog(@"handleDictionarySentByJavaScript: data field missing or not an object (message: %@)", [dict description]);
                    }
#endif
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
    if (message != nil) [self executeScriptInWebView:webView withDictionary:message];
#if DEBUG_COBALT
    else NSLog(@"sendMessage: message is nil!");
#endif
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark NAVIGATION METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

- (void)replaceViewControllerWithData:(NSDictionary *)data
{
    NSString * page = [data objectForKey:kJSPage];
    NSString * controller = [data objectForKey:kJSNavigationController];
    BOOL animated = [[data objectForKey: kJSAnimated] boolValue];
    
    if (page
        && [page isKindOfClass:[NSString class]]) {
        UIViewController * viewController = [CobaltViewController getViewControllerForController:controller andPage:page];
        if (viewController) {
            // replace current view with corresponding viewController
            NSMutableArray * viewControllers = [NSMutableArray arrayWithArray: [self.navigationController viewControllers]];
            [viewControllers replaceObjectAtIndex: (viewControllers.count - 1) withObject: viewController];
            [self.navigationController setViewControllers: viewControllers animated: animated];
        }
    }
#if DEBUG_COBALT
    else {
        NSLog(@"handleDictionarySentByJavaScript: page field missing or not a string (data: %@)", [data description]);
    }
#endif
}

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
        NSMutableDictionary * barsDictionary = [NSMutableDictionary dictionaryWithDictionary: [configuration objectForKey: kBars]];
        
        NSDictionary * barActionsArray = [barsDictionary objectForKey: kBarActions];
        NSMutableArray * mutableBarActionsArray = [NSMutableArray arrayWithCapacity: barActionsArray.count];
        for(NSDictionary * barActionDictionary in barActionsArray) {
            [mutableBarActionsArray addObject: [NSMutableDictionary dictionaryWithDictionary: barActionDictionary]];
        }
        
        [barsDictionary setObject: mutableBarActionsArray forKey: kBarActions];
        
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
                viewController.barsConfiguration = barsDictionary;
                
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
#pragma mark BARS METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


//TODO put this code (2 following functions) in a UIColor category
void SKScanHexColor(NSString * hexString, float * red, float * green, float * blue, float * alpha) {
    NSString *cleanString = [hexString stringByReplacingOccurrencesOfString:@"#" withString:@""];
    if([cleanString length] == 3) {
        cleanString = [NSString stringWithFormat:@"%@%@%@%@%@%@",
                       [cleanString substringWithRange:NSMakeRange(0, 1)],[cleanString substringWithRange:NSMakeRange(0, 1)],
                       [cleanString substringWithRange:NSMakeRange(1, 1)],[cleanString substringWithRange:NSMakeRange(1, 1)],
                       [cleanString substringWithRange:NSMakeRange(2, 1)],[cleanString substringWithRange:NSMakeRange(2, 1)]];
    }
    if([cleanString length] == 6) {
        cleanString = [cleanString stringByAppendingString:@"ff"];
    }
    
    unsigned int baseValue;
    [[NSScanner scannerWithString:cleanString] scanHexInt:&baseValue];
    
    if (red) { *red = ((baseValue >> 24) & 0xFF)/255.0f; }
    if (green) { *green = ((baseValue >> 16) & 0xFF)/255.0f; }
    if (blue) { *blue = ((baseValue >> 8) & 0xFF)/255.0f; }
    if (alpha) { *alpha = ((baseValue >> 0) & 0xFF)/255.0f; }
}

UIColor * SKColorFromHexString(NSString * hexString) {
    float red, green, blue, alpha;
    SKScanHexColor(hexString, &red, &green, &blue, &alpha);
    
    return [UIColor colorWithRed:red green:green blue:blue alpha:alpha];
}

- (void)configureBars {
    
    NSDictionary * visibilities = [self.barsConfiguration objectForKey: kJSVisibility];
    BOOL topVisible = [[visibilities objectForKey: kJSTop] boolValue] || ![visibilities objectForKey: kJSTop];
    BOOL bottomVisible = [[visibilities objectForKey: kJSBottom] boolValue];
    
    [self.navigationController setNavigationBarHidden: !topVisible animated: NO];
    self.hasToolBar = bottomVisible;
    
    NSString * backgroundColorString = [self.barsConfiguration objectForKey: kBarBackgroundColor];
    
    if(backgroundColorString.length > 0) {
        UIColor * backgroundColor = SKColorFromHexString(backgroundColorString);
    
        _navigationBarTintColor = backgroundColor;
        _toolbarTintColor = backgroundColor;
    }
    
    NSArray * barsAction = [self.barsConfiguration objectForKey: kBarActions];
    
    NSMutableArray * topLeftButtonsArray = [NSMutableArray arrayWithCapacity: 5];
    NSMutableArray * topRightButtonsArray = [NSMutableArray arrayWithCapacity: 5];
    NSMutableArray * bottomLeftButtonsArray = [NSMutableArray arrayWithCapacity: 5];
    NSMutableArray * bottomRightButtonsArray = [NSMutableArray arrayWithCapacity: 5];
    
    for(NSDictionary * barActionConfiguration in barsAction) {
        if([[barActionConfiguration objectForKey: kBarActionVisible] isEqualToString: @"false"])
            continue;
        
        CobaltButton *barButtonAction =  [[CobaltButton alloc] initWithFrame:CGRectMake(0.0, 0.0, 22.0, 22.0)];
        barButtonAction.iconName = [barActionConfiguration objectForKey: kBarActionName];
        
        NSString * barButtonImageName = [barActionConfiguration objectForKey: kBarActionIcon];
        UIImage * barButtonImage = nil;
        
        
        if([barButtonImageName hasPrefix: @"fa-"]) {
            barButtonImage = [UIImage imageWithIcon:barButtonImageName backgroundColor:[UIColor clearColor] iconColor:[UIColor whiteColor] iconScale:[[UIScreen mainScreen] scale] andSize: barButtonAction.frame.size];
        } else {
            barButtonImage = [UIImage imageNamed: barButtonImageName];
        }
        
        if(barButtonImage) {
            [barButtonAction setImage: barButtonImage forState:UIControlStateNormal];
        }
        else
        {
            NSString * barButtonTitle = [barActionConfiguration objectForKey: kBarActionTitle];
            [barButtonAction setTitle: barButtonTitle forState: UIControlStateNormal];
            [barButtonAction sizeToFit];
        }
        
        UIBarButtonItem * barButtonItemAction = [[UIBarButtonItem alloc] initWithCustomView:barButtonAction];
        
        NSString * barActionPosition = [barActionConfiguration objectForKey: kBarActionPosition];
        
        if([barActionPosition isEqualToString: @"topLeft"]) {
            [topLeftButtonsArray addObject: barButtonItemAction];
        } else if([barActionPosition isEqualToString: @"topRight"]) {
            [topRightButtonsArray addObject: barButtonItemAction];
        } else if([barActionPosition isEqualToString: @"bottomLeft"]) {
            [bottomLeftButtonsArray addObject: barButtonItemAction];
        } else if([barActionPosition isEqualToString: @"bottomRight"]) {
            [bottomRightButtonsArray addObject: barButtonItemAction];
        }
        
        [barButtonAction addTarget:self action:@selector(onBarButtonItem:) forControlEvents:UIControlEventTouchUpInside];
    }
    
    self.navigationItem.leftItemsSupplementBackButton = YES;
    
    [self.navigationItem setLeftBarButtonItems: topLeftButtonsArray animated: YES];
    [self.navigationItem setRightBarButtonItems: topRightButtonsArray animated: YES];
    
    NSMutableArray * bottomButtonsArray = [NSMutableArray arrayWithCapacity: bottomLeftButtonsArray.count + bottomRightButtonsArray.count + 1];
    UIBarButtonItem *flexibleSpace = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:nil action:nil];

    [bottomButtonsArray addObjectsFromArray: bottomLeftButtonsArray];
    [bottomButtonsArray addObject: flexibleSpace];
    [bottomButtonsArray addObjectsFromArray: bottomRightButtonsArray];
    
    [self setToolbarItems: bottomButtonsArray animated: YES];
}

- (void)onBarButtonItem: (CobaltButton *)button {
    NSDictionary * data = @{ @"type" : @"ui", @"control" : @"bars", @"data" : @{ @"action" : @"buttonPressed", @"button" : button.iconName }};
    
    [self sendMessage: data];
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
        
        NSString *currSysVer = [[UIDevice currentDevice] systemVersion];
        if ([currSysVer compare: @"8.0" options:NSNumericSearch] != NSOrderedAscending) {
            UIAlertController *alertController = [UIAlertController
                                                  alertControllerWithTitle:title
                                                  message:message
                                                  preferredStyle:UIAlertControllerStyleAlert];
            if (! buttons.count) {
                UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleCancel handler:^(UIAlertAction *action){}];
                [alertController addAction:cancelAction];
            }else{
                
                UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:[buttons objectAtIndex:0] style:UIAlertActionStyleCancel handler:^(UIAlertAction *action){}];
                for (int i = 1 ; i < buttons.count ; i++) {
                    UIAlertAction *otherAction = [UIAlertAction actionWithTitle:[buttons objectAtIndex:i] style:UIAlertActionStyleDefault handler:^(UIAlertAction *action)
                                               {
                                                   if (callback
                                                       && [callback isKindOfClass:[NSString class]]) {
                                                       NSDictionary * data = [NSDictionary dictionaryWithObjectsAndKeys:[NSNumber numberWithInteger:i],
                                                                              kJSAlertButtonIndex,
                                                                              nil];
                                                       [self sendCallback:callback withData:data];
                                                   }
                                               }];
                    [alertController addAction:otherAction];
                }
                [alertController addAction:cancelAction];
            }
            [self presentViewController:alertController animated:YES completion:nil];
            
        }else{
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
