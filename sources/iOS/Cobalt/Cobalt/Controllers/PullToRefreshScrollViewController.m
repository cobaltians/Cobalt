/**
 *
 * PullToRefreshScrollViewController.m
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

#import "PullToRefreshScrollViewController.h"

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark IMPLEMENTATION
#pragma mark - 
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@implementation PullToRefreshScrollViewController

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark PROPERTIES
#pragma mark - 
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@synthesize pullToRefreshTableHeaderView;
@synthesize scrollView;
@synthesize _refreshing;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark VIEW LIFECYCLE
#pragma mark - 
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//sérieux tu sais pas ce que cette fonction fait ?
- (void) viewDidLoad {
    [super viewDidLoad];
    
    self.pullToRefreshTableHeaderView.state = RefreshStateNormal;
    self.pullToRefreshTableHeaderView.loadingHeight = 60.0;
    self.pullToRefreshTableHeaderView.frame = CGRectMake(0.0, -self.scrollView.bounds.size.height, self.scrollView.bounds.size.width, self.scrollView.bounds.size.height);
    
}


//******************
// VIEW DID UNLOAD *
//******************
/*!
 @method		- (void)viewDidUnload
 @abstract		Called when the controller’s view is released from memory.
 */
- (void)viewDidUnload {
    [super viewDidUnload];
    self.pullToRefreshTableHeaderView = nil;
}

//*******************
// VIEW WILL APPEAR *
//*******************
/*!
 @method		- (void)viewWillAppear:(BOOL)animated
 @abstract		Notifies the view controller that its view is about to be become visible.
 @param         animated    If YES, the view is being added to the window using an animation.
 */
- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
  
    // Add pull to refresh table header view
    /*self.pullToRefreshTableHeaderView.state = RefreshStateNormal;
    self.pullToRefreshTableHeaderView.loadingHeight = 60.0;
    self.pullToRefreshTableHeaderView.frame = CGRectMake(0.0, -self.scrollView.bounds.size.height, self.scrollView.bounds.size.width, self.scrollView.bounds.size.height);*/
    [self.scrollView addSubview:self.pullToRefreshTableHeaderView];
  
    self.scrollView.contentSize = CGSizeMake(self.scrollView.frame.size.width, self.scrollView.frame.size.height+1);
}

- (void)willRotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration {
  [self.pullToRefreshTableHeaderView setHidden: YES];
  [super willRotateToInterfaceOrientation: toInterfaceOrientation duration: duration];
}

- (void)didRotateFromInterfaceOrientation:(UIInterfaceOrientation)fromInterfaceOrientation {
  [self.pullToRefreshTableHeaderView setHidden: NO];
  self.pullToRefreshTableHeaderView.frame = CGRectMake(0.0, -self.scrollView.bounds.size.height, self.scrollView.bounds.size.width, self.scrollView.bounds.size.height);
  //self.scrollView.contentSize = CGSizeMake(self.view.frame.size.width, self.view.frame.size.height+1);
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
    if (_scrollView.isDragging) {
        if (self.pullToRefreshTableHeaderView.state == RefreshStatePulling && _scrollView.contentOffset.y > -65.0 && scrollView.contentOffset.y < 0.0 && !_refreshing) {
            self.pullToRefreshTableHeaderView.state = RefreshStateNormal;
        }
        else if (self.pullToRefreshTableHeaderView.state == RefreshStateNormal && _scrollView.contentOffset.y < -65.0 && !_refreshing) {
            self.pullToRefreshTableHeaderView.state = RefreshStatePulling;
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
    if (_scrollView.contentOffset.y <= -65.0 && !_refreshing) {
        [self refresh];
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
 @abstract		Tells the table view to be refresh its content.
 */
- (void)refresh {
    _refreshing = YES;
    self.pullToRefreshTableHeaderView.state = RefreshStateLoading;
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationDuration:0.2];
    self.scrollView.contentInset = UIEdgeInsetsMake(self.pullToRefreshTableHeaderView.loadingHeight, 0.0f, 0.0f, 0.0f);
    [UIView commitAnimations];
    [self refreshTableViewDataSource];
}

//******************************
// LOAD TABLE VIEW DATA SOURCE *
//******************************
/*!
 @method		- (void)loadTableViewDataSource
 @abstract		Simulates loading of the table view data source.
 */
- (void)loadTableViewDataSource {
  [self didRefresh];
}

//*********************************
// REFRESH TABLE VIEW DATA SOURCE *
//*********************************
/*!
 @method		- (void)refreshTableViewDataSource
 @abstract		Starts refreshing the table view data source.
 */
- (void)refreshTableViewDataSource {
    /*
     This is the default fake implementation.
     */
    [self loadTableViewDataSource];
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
    self.scrollView.contentInset = UIEdgeInsetsMake(0.0f, 0.0f, 0.0f, 0.0f);
	[UIView commitAnimations];
#warning TODO REFRESH
  //[self.tableView reloadData];
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
#pragma mark MEMORY MANAGEMENT
#pragma mark - 
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//**********
// DEALLOC *
//**********
/*!
 @method		- (void)dealloc
 @abstract		Deallocates the memory occupied by the receiver.
 */

@end
