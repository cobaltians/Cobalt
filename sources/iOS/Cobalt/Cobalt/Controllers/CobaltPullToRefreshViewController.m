/**
 *
 * CobaltPullToRefreshViewController.m
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

#import "CobaltPullToRefreshViewController.h"

#define JSPullToRefreshRefresh              @"pullToRefreshRefresh"
#define JSPullToRefreshDidRefresh           @"pullToRefreshDidRefresh"

@interface CobaltPullToRefreshViewController ()

@end

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark IMPLEMENTATION
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@implementation CobaltPullToRefreshViewController

@synthesize pullToRefreshTableHeaderView,isPullToRefreshActive;



///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark VIEW LIFECYCLE
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
    PullToRefreshTableHeaderView *h = [[PullToRefreshTableHeaderView alloc] initWithFrame:CGRectMake(0, 0, 10, 10)];

    // Do any additional setup after loading the view from its nib.
#warning INSANE ! Mandatory to create a "useless" PullToRefreshTableHeaderView to allow xib file to create a PullToRefreshTableHeaderView instead of a default UIView (even if it is well set in Xib...
    [self customPullToRefreshDefaultView];
    
    self.pullToRefreshTableHeaderView.state = RefreshStateNormal;
    self.pullToRefreshTableHeaderView.loadingHeight = self.pullToRefreshTableHeaderView.frame.size.height;
    self.pullToRefreshTableHeaderView.frame = CGRectMake(0.0, -self.webView.bounds.size.height, self.webView.bounds.size.width, self.webView.bounds.size.height);
    
    // Add pull to refresh table header view
    if(self.isPullToRefreshActive)
    {
        if(self.pullToRefreshTableHeaderView)
        {
            [self.webView.scrollView addSubview:self.pullToRefreshTableHeaderView];
            //self.scrollView.contentSize = CGSizeMake(self.scrollView.frame.size.width, self.scrollView.frame.size.height+1);
        }
#if DEBUG_COBALT
        else
        {
            NSLog(@"WARNING : no pullToRefreshTableHeaderView set !");
        }
#endif
    }
    
    [self.webView.scrollView setDelegate:self];
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

- (void)viewDidUnload {
    [super viewDidUnload];
    self.pullToRefreshTableHeaderView = nil;
    [self.webView.scrollView setDelegate:nil];
}


- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void) viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
#if DEBUG_COBALT
    NSLog(@"pullToRefresh enabled = %d",self.isPullToRefreshActive);
#endif
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
#pragma mark SCROLL VIEW DELEGATE
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
            if (self.pullToRefreshTableHeaderView.state == RefreshStatePulling && _scrollView.contentOffset.y > -65.0 && self.webView.scrollView.contentOffset.y < 0.0 && !_refreshing) {
                self.pullToRefreshTableHeaderView.state = RefreshStateNormal;
            }
            else if (self.pullToRefreshTableHeaderView.state == RefreshStateNormal && _scrollView.contentOffset.y < -65.0 && !_refreshing) {
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
        if (_scrollView.contentOffset.y <= -65.0 && !_refreshing) {
            [self refresh];
        }
    }
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark METHODS
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
        _refreshing = YES;
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
    NSDictionary *d = [NSDictionary dictionaryWithObjectsAndKeys:JSTypeEvent,kJSType,JSPullToRefreshRefresh,kJSName, JSPullToRefreshDidRefresh,kJSCallbackID,nil];
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
    _refreshing = NO;
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

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark NATIVE BRIDGE METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

-(BOOL)handleDictionarySentByJavaScript:(NSDictionary *)dict
{
    NSString *type = [dict objectForKey:kJSType];
    if(type && [type isKindOfClass:[NSString class]] && type.length > 0)
    {
        if([type isEqualToString:JSTypeCallBack])
        {
            NSString *name = [dict objectForKey:kJSCallbackID];
            if(name && [name isKindOfClass:[NSString class]] && [name isEqualToString:JSPullToRefreshDidRefresh])
            {
                [self performSelectorOnMainThread:@selector(didRefresh) withObject:nil waitUntilDone:YES];
                return YES;
            }
        }
    }
    
    return [super handleDictionarySentByJavaScript:dict];
}


@end

