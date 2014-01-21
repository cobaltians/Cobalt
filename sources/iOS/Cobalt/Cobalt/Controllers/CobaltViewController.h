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

#import "CobaltToast.h"

@class PullToRefreshTableHeaderView;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark JAVASCRIPT KEYS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// GENERAL
#define kJSAction                           @"action"
#define kJSCallback                         @"callback"
#define kJSData                             @"data"
#define kJSPage                             @"page"
#define kJSType                             @"type"
#define kJSValue                            @"value"

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
#define JSActionNavigationModale            @"modale"
#define JSActionNavigationDismiss           @"dismiss"
#define kJSNavigationController             @"controller"
#define JSNavigationControllerDefault       @"default"

// PULL TO REFRESH
#define JSEventPullToRefresh                @"pullToRefresh"
#define JSCallbackPullToRefreshDidRefresh   @"pullToRefreshDidRefresh"

// INFINITE SCROLL
#define JSEventInfiniteScroll               @"infiniteScroll"
#define JSCallbackInfiniteScrollDidRefresh  @"infiniteScrollDidRefresh"

// UI
#define JSTypeUI                            @"ui"
#define kJSUIControl                        @"control"

// ALERT
#define JSControlAlert                      @"alert"
#define kJSAlertTitle                       @"title"
#define kJSAlertMessage                     @"message"
#define kJSAlertButtons                     @"buttons"
#define kJSAlertButtonIndex                 @"index"

// TOAST
#define JSControlToast                      @"toast"

// WEB LAYER
#define JSTypeWebLayer                      @"webLayer"
#define JSActionWebLayerShow                @"show"
#define JSActionWebLayerDismiss             @"dismiss"
#define kJSWebLayerFadeDuration             @"fadeDuration"
#define JSEventWebLayerOnDismiss            @"onWebLayerDismissed"

// HTML
#define defaultHtmlPage                     @"index.html"

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark PROTOCOL
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@protocol CobaltDelegate <NSObject>

- (BOOL)onUnhandledMessage:(NSDictionary *)message;
- (BOOL)onUnhandledEvent:(NSString *)event withData:(NSDictionary *)data andCallback:(NSString *)callback;
- (BOOL)onUnhandledCallback:(NSString *)callback withData:(NSDictionary *)data;

@end

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark INTERFACE
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/*!
 @class			CobaltViewController
 @abstract		Base class for a webView controller that allows javascript/native dialogs
 */
@interface CobaltViewController : UIViewController <UIAlertViewDelegate, UIScrollViewDelegate, UIWebViewDelegate, CobaltToastDelegate>
{
    // Javascript queues
    NSOperationQueue * toJavaScriptOperationQueue;
    NSOperationQueue * fromJavaScriptOperationQueue;
    
    // UI components
    PullToRefreshTableHeaderView * pullToRefreshTableHeaderView;
    
@private
    
    id<CobaltDelegate> _delegate;
    int _alertViewCounter;
	BOOL _isLoadingMore;
    BOOL _isRefreshing;
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
@property (strong, nonatomic) UIWebView * webView;

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
 @property		pullToRefreshTableHeaderView
 @abstract		The pull to refresh table header view.
 */
@property (nonatomic, strong) PullToRefreshTableHeaderView * pullToRefreshTableHeaderView;

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
 @method		+ (NSString *)stringWithContentsOfFile:(NSString *)path
 @abstract		this method returns the content of the file at path path.
 @param         path: the path where to find this file
 @return        the content of the specified file or nil if an error occured
 */
+ (NSString *)stringWithContentsOfFile:(NSString *)path;

/*!
 @method		-(void) executeScriptInWebView:(UIWebView *)mWebView withDictionary:(NSDictionary *)dict
 @abstract		this method sends a JSON to the webView to execute a script (allows interactions from the native to the webView)
 @param         mWebView : the webview where the script is due to be executed
 @param         dict : a NSDictionary that contains the necessary informations to execute the script
 @discussion    the webView MUST have a function "nativeBridge.execute(%@);" that receives the JSON (representing dict) as parameter
 @discussion    This method should NOT be overridden in subclasses.
 */
- (void)executeScriptInWebView:(UIWebView *)mWebView withDictionary:(NSDictionary *)dict;

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

/*!
 @method		-(void)handleDictionarySentByJavaScript:(NSDictionary *)dict
 @abstract		this method gets a JSON from the webView to use so as to fire native methods (allows interactions from the webView to the native)
 @param        dict : the NSDictionary given by the webView to handle with
 @result        BOOL : if YES, the action to do by the controller has been completed. Otherwise, returns NO.
 @discussion    This method MUST be overridden in subclasses.
 */
- (BOOL)handleDictionarySentByJavaScript:(NSDictionary *)dict;

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
 @method		- (void)onPullToRefreshDidRefresh
 @abstract		Tells the web view it has been refreshed.
 */
- (void)onPullToRefreshDidRefresh;

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

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark INTERFACE
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

typedef enum {
    RefreshStateNormal = 0,
    RefreshStatePulling,
    RefreshStateLoading
} RefreshState;

/*!
 @class			PullToRefreshTableHeaderView
 @abstract		Class for header of table view that pull to refresh.
 */
@interface PullToRefreshTableHeaderView : UIView {
    CGFloat loadingHeight;
    UIActivityIndicatorView * progressView;
    UIImageView * arrowImageView;
    UILabel * lastUpdatedLabel;
    UILabel * statusLabel;
    RefreshState state;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark PROPERTIES
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*!
 @property		loadingHeight
 @abstract		The view heigh when in loading state.
 */
@property (nonatomic, assign) CGFloat loadingHeight;

/*!
 @property		progressView
 @abstract		The progress view.
 */
@property (nonatomic, retain) UIActivityIndicatorView * progressView;

/*!
 @property		arrowImageView
 @abstract		The arrow image view.
 */
@property (nonatomic, retain) UIImageView * arrowImageView;

/*!
 @property		lastUpdatedLabel
 @abstract		The last updated label.
 */
@property (nonatomic, retain)  UILabel * lastUpdatedLabel;

/*!
 @property		statusLabel
 @abstract		The status label.
 */
@property (nonatomic, retain) UILabel * statusLabel;

/*!
 @property		state
 @abstract		The refresh state.
 */
@property (nonatomic, assign) RefreshState state;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*!
 @method		- (void)setLastUpdated:(NSString *)lastUpdated
 @abstract		Sets the last updated text.
 @param         lastUpdated The last updated text to set.
 */
- (void)setLastUpdated:(NSString *)lastUpdated;

/*!
 @method		- (NSString *)textForState:(RefreshState)newState
 @abstract		Sets the text for the status label depending on the newState given
 @param         newState The new state applied to the pullToRefreshTableHeaderView
 @return        a NSString containing the string to display for the given mode.
 @discussion    This method may be overriden in subclasses.
 */
- (NSString *)textForState:(RefreshState)newState;
@end


