/**
 *
 * CobaltViewController.h
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

#import <UIKit/UIKit.h>

#import <JavaScriptCore/JavaScriptCore.h>

#import "CobaltToast.h"

#import "FontAwesome.h"

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark JAVASCRIPT KEYS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//COBALT VERSION
#define IOSCurrentVersion                   @"0.4.1"

// GENERAL
#define kJSAction                           @"action"
#define kJSCallback                         @"callback"
#define kJSData                             @"data"
#define kJSPage                             @"page"
#define kJSTexts                            @"texts"
#define kJSType                             @"type"
#define KJSVersion                          @"version"
#define kJSValue                            @"value"
#define kJSValues                           @"values"
#define kJSResult                           @"result"

// CALLBACK
#define JSTypeCallBack                      @"callback"
#define JSCallbackSimpleAcquitment          @"callbackSimpleAcquitment"

// COBALT IS READY
#define JSTypeCobaltIsReady                 @"cobaltIsReady"

// EVENT
#define JSTypeEvent                         @"event"
#define kJSEvent                            @"event"

// LOG
#define JSTypeLog                           @"log"

// NAVIGATION
#define JSTypeNavigation                    @"navigation"
#define JSActionNavigationPush              @"push"
#define JSActionNavigationPop               @"pop"
#define JSActionNavigationModal             @"modal"
#define JSActionNavigationDismiss           @"dismiss"
#define kJSActionNavigationReplace          @"replace"
#define kJSNavigationController             @"controller"
#define JSNavigationControllerDefault       @"default"

#define kJSAnimated                         @"animated"

//LIFE CYCLE
#define JSEventOnAppStarted                 @"onAppStarted"
#define JSEventOnAppForeground              @"onAppForeground"
#define JSEventOnAppBackground              @"onAppBackground"
#define JSEventOnPageShown                  @"onPageShown"

// PULL TO REFRESH
#define JSEventPullToRefresh                @"pullToRefresh"
#define JSCallbackPullToRefreshDidRefresh   @"pullToRefreshDidRefresh"

// INFINITE SCROLL
#define JSEventInfiniteScroll               @"infiniteScroll"
#define JSCallbackInfiniteScrollDidRefresh  @"infiniteScrollDidRefresh"

// UI
#define kJSTypeUI                           @"ui"
#define kJSUIControl                        @"control"
#define kJSTypeImage                        @"image"


// PULL TO REFRESH

#define JSControlpullToRefresh              @"pullToRefresh"

// ALERT
#define JSControlAlert                      @"alert"
#define kJSAlertTitle                       @"title"
#define kJSAlertMessage                     @"message"
#define kJSAlertButtons                     @"buttons"
#define kJSAlertButtonIndex                 @"index"

// TOAST
#define JSControlToast                      @"toast"

//BARS
// TODO: uncomment for Bars
/*
#define JSControlBars                       @"bars"
#define kJSButton                           @"button"
#define kJSTitleBar                         @"title"
#define kJSVisibility                       @"visibility"
#define kJSTop                              @"top"
#define kJSBottom                           @"bottom"
*/

// WEB LAYER
#define JSTypeWebLayer                      @"webLayer"
#define JSActionWebLayerShow                @"show"
#define JSActionWebLayerDismiss             @"dismiss"
#define kJSWebLayerFadeDuration             @"fadeDuration"
#define JSEventWebLayerOnDismiss            @"onWebLayerDismissed"
#define kJSIsWebLayer                       @"isWebLayer"

//INTENT
#define kJSTypeIntent                        @"intent"
#define kJSActionOpenExternalUrl             @"openExternalUrl"
#define kJSUrl                               @"url"

// HTML
#define defaultHtmlPage                     @"index.html"

//PLUGIN
#define kJSTypePlugin                       @"plugin"
#define kJSPluginName                       @"name"

//NOTIFS
#define kOnAppStarted                       @"onAppStarted"
#define kOnAppForegroundNotification        @"onAppForegroundNotification"
#define kOnAppBackgroundNotification        @"onAppBackgroundNotification"

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark PROTOCOL
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@protocol CobaltDelegate <NSObject>

@optional
- (void)onCobaltIsReady;

@required
- (BOOL)onUnhandledMessage:(NSDictionary *)message;
- (BOOL)onUnhandledEvent:(NSString *)event withData:(NSDictionary *)data andCallback:(NSString *)callback;
- (BOOL)onUnhandledCallback:(NSString *)callback withData:(NSDictionary *)data;

@end

@protocol CobaltViewControllerJS <JSExport>

- (BOOL)onCobaltMessage:(NSString *)message;

@end

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark INTERFACE
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

typedef enum {
    WEB_VIEW,
    WEB_LAYER
} WebViewType;

/*!
 @class			CobaltViewController
 @abstract		Base class for a webView controller that allows javascript/native dialogs
 */
@interface CobaltViewController : UITableViewController <UIAlertViewDelegate, UIScrollViewDelegate, UIWebViewDelegate, CobaltToastDelegate, CobaltViewControllerJS>
{
    // Javascript queues
    NSOperationQueue * toJavaScriptOperationQueue;
    NSOperationQueue * fromJavaScriptOperationQueue;
    
    // TODO: uncomment for Bars
    /*
    UIColor * _navigationBarTintColor;
    UIColor * _toolbarTintColor;
    */
    
@private
    
    id<CobaltDelegate> _delegate;
    int _alertViewCounter;
    float _lastWebviewContentOffset;
	BOOL _isLoadingMore;
    BOOL _isRefreshing;
    
    NSAttributedString * _ptrRefreshText;
    NSAttributedString * _ptrRefreshingText;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark PROPERTIES
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*!
 @property		webView
 @abstract		the webView displaying content
 */
@property (strong, nonatomic) IBOutlet UIWebView * webView;

/*!
 @property		activityIndicator
 @abstract		an activity indicator shown- while the webView is loading
 */
@property (strong, nonatomic) UIActivityIndicatorView * activityIndicator;

/*!
 @property		pageName
 @abstract		the name of the HTML file with the content to display in the webview
 @discussion    the file must be located at ressourcePath
 */
@property (strong, nonatomic) NSString * pageName;

@property (strong, nonatomic) UIWebView * webLayer;

/*!
 @property		isPullToRefreshEnabled
 @abstract		allows or not the pullToRefresh functionality
 */
@property BOOL isPullToRefreshEnabled;

/*!
 @property		isInfiniteScrollEnabled
 @abstract		allows or not the infinite scroll functionality
 */
@property BOOL isInfiniteScrollEnabled;

/*!
 @property		infiniteScrollOffset
 @abstract		offset to trigger infinite scroll
 */
@property int infiniteScrollOffset;

// TODO: uncomment for Bars
/*!
 @property		hasToolBar
 @abstract		allows or not the toolbar display
 */
//@property BOOL hasToolBar;

/*!
 @property		barsConfiguration
 @abstract		bars configuration as defined in cobalt.conf
 */
//@property NSMutableDictionary * barsConfiguration;


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark COBALT METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/*!
 @method		- (void)setDelegate:(id)delegate
 @abstract		this method sets the delegate which responds to CobaltDelegate protocol
 */
- (void)setDelegate:(id<CobaltDelegate>)delegate;

/*!
 @method		-(void) customView
 @abstract		a method to custom the webView
 @discussion    must be subclassed in subclasses
 */
- (void)customWebView;

/*!
 @method		- (void)loadPage:(NSString *)page inWebView:(UIWebView *)mWebView
 @abstract		this method loads the page in ressourcePath in the Web view.
 @param         mWebView : Web view to load the page into
 @param         page: the page file
 */
- (void)loadPage:(NSString *)page inWebView:(UIWebView *)mWebView;


/*!
 @method		- (void) sendMessage:(NSDictionary *) message;
 @abstract		this method sends an message who was an Object format in an NSDictionary
 @param         messageId : the messageId given by a former JS call. It's all events aren't send with sendCallback and sendEvent.
 @discussion    This method should NOT be overridden in subclasses.
 */
- (void) sendMessage:(NSDictionary *) message;

- (void) sendMessageToWebLayer:(NSDictionary *) message;

/*!
 @method		-(void) sendCallback:(NSString *)callbackId withData:(NSObject *)data
 @abstract		this methods sends a callback with the givent callbackId and the object as parameter of the methods which is called in JS
 @param         callbackId : the callbackID given by a former JS call, so that JS calls the appropriate method
 @param         object : the object to send to the JS method which corresponds to the callbackId given
 @discussion    This method should NOT be overridden in subclasses.
 */
- (void)sendCallback:(NSString *)callback withData:(NSObject *)data;

/*!
 @method		- (void)sendEvent:(NSString *)event withData:(NSObject *)data andCallback:(NSString *)callback
 @abstract		this method sends an event with a data object and an optional callback
 @param         event: event fired
 @param         data: data object to send to JS
 @param         callback: the callback JS should calls when message is treated
 @discussion    This method should NOT be overridden in subclasses.
 */
- (void)sendEvent:(NSString *)event withData:(NSObject *)data andCallback:(NSString *)callback;

- (void)sendCallbackToWebLayer:(NSString *)callback withData:(NSObject *)data;

- (void)sendEventToWebLayer:(NSString *)event withData:(NSObject *)data andCallback:(NSString *)callback;

/*!
 @method        - (BOOL)handleDictionarySentByJavaScript:(NSDictionary *)message
 @abstract      Catches the message sent by the WebView as JSON used to fire native methods (allows interactions from the WebView to the native)
 @param         message: the JSON sent by the WebView
 @result        Returns YES if the message has been catched by the native, NO otherwise.
 @discussion    This method SHOULD NOT be overridden in subclasses.
 */
- (BOOL)handleDictionarySentByJavaScript:(NSDictionary *)message;

/*!
 @method		- (void)sendACK
 @abstract		Sends an ACK event as soon as a JS message is received
 @discussion    This is the default acquitment way. More complex acquitment methods may be implemented but on iOS, every call received by JS should send at least this acquitment.
 */
- (void)sendACK;

/*!
 @method		+ (UIViewController *)getViewControllerForController:(NSString *)controller andPage:(NSString *)page;
 @abstract		Returns an allocated and initialized view controller from its id in cobalt configuration file and HTML page
 @param         controller: view controller id
 @param         page: HTML page
 */
+ (UIViewController *)getViewControllerForController:(NSString *)controller andPage:(NSString *)page;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark PULL TO REFRESH METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*!
 @method		- (void)refresh
 @abstract		Tells the webview to be refresh its content.
 */
- (void)refresh;

/*!
 @method		- (void)refreshWebView
 @abstract		Sends event to refresh Web view content.
 */
- (void)refreshWebView;


/*!
 @method		- (void)customizeRefreshControlWithAttributedText:(NSAttributedString *)attributedText andTintColor: (UIColor *)tintColor
 @abstract		customize native pull to refresh control
 */
- (void)customizeRefreshControlWithAttributedRefreshText:(NSAttributedString *)attributedRefreshText andAttributedRefreshText:(NSAttributedString *)attributedRefreshingText andTintColor: (UIColor *)tintColor;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark INFINITE SCROLL METHOS
#pragma mark -
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*!
 @method		- (void)loadMoreItems
 @abstract		Tells the webview to be load more datas
 */
- (void)loadMoreItems;

/*!
 @method		-(void) loadMoreContentInWebview
 @abstract		Starts loading more content in webview
 */
- (void)loadMoreContentInWebview;

@end


