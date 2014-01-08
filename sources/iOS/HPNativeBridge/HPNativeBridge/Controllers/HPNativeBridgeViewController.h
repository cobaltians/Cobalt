//
//  HPNativeBridgeViewController.h
//  HPNativeBridge
//
//  Created by Diane on 11/04/13.
//  Copyright (c) 2013 Haploid. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "AbstractViewController.h"
#import "HPToast.h"

#define kJSType             @"type"
#define JSTypeEvent         @"typeEvent"
#define JSNativeBridgeIsReady @"nativeBridgeIsReady"
#define JSTypeReady         @"nativeBridgeIsReady"
#define JSTypeLog           @"typeLog"
#define JSTypeAlert         @"typeAlert"
#define JSTypeWebAlert      @"typeWebAlert"
#define JSTypeCallBack      @"typeCallback"
#define JSTypeNavigation    @"typeNavigation"

//EVENTS
#define kJSName             @"name"
#define JSNameToast         @"nameToast"
#define JSNameSetZoom       @"nameSetZoom"//todo : remove, nothing to do in nativeBridge

#define kJSValue            @"value"

//CALLBACKS
#define kJSCallbackID       @"callbackID"
#define JSCallbackSimpleAcquitment @"callbackSimpleAcquitment"

#define kJSParams @"params"

//NAVIGATION
#define kJSNavigationType             @"navigationType"
#define JSNavigationTypePush          @"push"
#define JSNavigationTypePop           @"pop"
#define JSNavigationTypeDismiss       @"dismiss"
#define JSNavigationTypeModale        @"modale"

#define kJSNavigationPageName         @"navigationPageName"

#define kJSNavigationClassId         @"navigationClassId"
#define JSNavigationDefaultClassId   @"default"

//ALERT
#define kJSAlertTitle               @"alertTitle"
#define kJSAlertMessage             @"alertMessage"
#define kJSAlertButtons             @"alertButtons"
#define kJSAlertCallbackReceiver    @"alertReceiver"
#define kJSAlertID                  @"alertId"
#define kJSAlertButtonIndex         @"index"

#define JSAlertCallbackReceiverWeb         @"web"
#define JSAlertCallbackReceiverNative      @"native"

//WEBALERT
#define JSWebAlertShow                 @"show"
#define JSWebAlertDismiss              @"dismiss"
#define kJSWebAlertPageName             @"pageName"
#define kJSWebAlertfadeDuration         @"fadeDuration"

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark INTERFACE
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*!
 @class			HPNativeBridgeViewController
 @abstract		Base class for a webView controller that allows javascript/native dialogs
 */
@interface HPNativeBridgeViewController : AbstractViewController <UIWebViewDelegate,UIAlertViewDelegate,HPToastDelegateProtocol>

{
    NSOperationQueue *toJavaScriptOperationQueue;
    NSOperationQueue *fromJavaScriptOperationQueue;
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
@property (strong, nonatomic) IBOutlet UIWebView *webView;

/*!
 @property		activityIndicator
 @abstract		an activity indicator shown- while the webView is loading
 */
@property (strong, nonatomic) IBOutlet UIActivityIndicatorView *activityIndicator;

/*!
 @property		pageName
 @abstract		the name of the HTML file with the content to display in the webview
 @discussion    the file must be located at ressourcePath
 */
@property (strong, nonatomic) NSString *pageName;

@property (strong,nonatomic) UIWebView *popUpWebview;
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/*!
 @method		-(NSString *)ressourcePath
 @abstract		this method returns the ressource path
 @return        a string representing the ressource path to be used in the webview
 @discussion    must be subclassed in subclasses
 */
-(NSString *)ressourcePath;

/*!
 @method		-(void) customView
 @abstract		a method to custom the webView
 */
-(void) customWebView;

/*!
 @method		-(void)loadContentInWebView:(UIWebView *)mWebView FromFileNamed:(NSString *)filename atPath:(NSString *)path withRessourcesAtPath:(NSString *)pathOfRessources

 @abstract		this method loads the content of the File named filename at path path in the webview with the ressources that can be found at pathOfRessources.
 @param         mWebView : the webview where to load the file
 @param         filename : the name of the file
 @param         path : the path where to find this file
 @param         pathOfRessources : the path where to find the ressources (css and js files)
 */
-(void)loadContentInWebView:(UIWebView *)mWebView FromFileNamed:(NSString *)filename atPath:(NSString *)path withRessourcesAtPath:(NSString *)pathOfRessources
;

/*!
 @method		-(NSString *)getStringFromFileNamed:(NSString *)filename andPath:(NSString *)path
 @abstract		this method returns the content of the File named filename at path path.
 @param         filename : the name of the file
 @param         path : the path where to find this file
 @return        the content of the specified file or nil if an error occured
 */
-(NSString *)getStringFromFileNamed:(NSString *)filename atPath:(NSString *)path;

/*!
 @method		-(void) executeScriptInWebView:(UIWebView *)mWebView WithDictionary:(NSDictionary *)dict
 @abstract		this method sends a JSON to the webView to execute a script (allows interactions from the native to the webView)
 @param         mWebView : the webview where the script is due to be executed
 @param         dict : a NSDictionary that contains the necessary informations to execute the script
 @discussion    the webView MUST have a function "nativeBridge.execute(%@);" that receives the JSON (representing dict) as parameter
 @discussion    This method should NOT be overridden in subclasses.
 */
-(void) executeScriptInWebView:(UIWebView *)mWebView WithDictionary:(NSDictionary *)dict;

/*!
 @method		-(void) sendCallbackResponseWithID:(NSString *)callbackId andObject:(NSObject *)object
 @abstract		this methods sends a callback with the givent callbackId and the object as parameter of the methods which is called in JS
 @param         callbackId : the callbackID given by a former JS call, so that JS calls the appropriate method
 @param         object : the object to send to the JS method which corresponds to the callbackId given
 @discussion    This method should NOT be overridden in subclasses.
 */
-(void) sendCallbackResponseWithID:(NSString *)callbackId andObject:(NSObject *)object;

/*!
 @method		-(void)handleDictionarySentByJavaScript:(NSDictionary *)dict
 @abstract		this method gets a JSON from the webView to use so as to fire native methods (allows interactions from the webView to the native)
 @param        dict : the NSDictionary given by the webView to handle with
 @result        BOOL : if YES, the action to do by the controller has been completed. Otherwise, returns NO.
 @discussion    This method MUST be overridden in subclasses.
 */
-(BOOL)handleDictionarySentByJavaScript:(NSDictionary *)dict;

/*!
 @method		-(void) sendAcquitmentToJS
 @abstract		sends a simple acquitment to JS as soon as a JS action is received
 @discussion    This is the default acquitment way. More complex acquitment methods may be implemented but on iOS, every call received by JavaScript should send at least this acquitment.
 */
-(void) sendSimpleAcquitmentToJS;

/*!
 @method		-(void) alertView:(UIAlertView *)alertView WithTag:(NSInteger) tag clickedButtonAtIndex:(NSInteger)buttonIndex
 @abstract		this method is executed when an alertView has been clicked and a custom behaviour should be implemented in the native class.
 @param         alertView : the alertView containing the button
 @param         tag : the tag of the alertView
 @param         buttonIndex : The index of the button that was clicked. The button indices start at 0.
 @discussion    this method should be overriden in subclasses if alertViews are necessary. 
 @discussion    It is called in the delegate method -(void) alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex;
 @discussion    This delegate method -(void) alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex; SHOULD NOT be overriden.
 */
-(void) alertView:(UIAlertView *)alertView WithTag:(NSInteger)tag clickedButtonAtIndex:(NSInteger)buttonIndex;

@end
