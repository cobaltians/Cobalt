//
//  HPPtrIsViewController.h
//  HPNativeBridge
//
//  Created by Diane on 25/04/13.
//  Copyright (c) 2013 Haploid. All rights reserved.
//

#import "HPNativeBridgeViewController.h"
#import "PullToRefreshTableHeaderView.h"

@interface HPPtrIsViewController : HPNativeBridgeViewController <UIScrollViewDelegate> {
    PullToRefreshTableHeaderView *pullToRefreshTableHeaderView;
@private
    
	BOOL _isLoadingMore;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark PROPERTIES
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*!
 @property		pullToRefreshTableHeaderView
 @abstract		The pull to refresh table header view.
 */
@property (nonatomic, strong) IBOutlet PullToRefreshTableHeaderView *pullToRefreshTableHeaderView;

/*!
 @property		isPullToRefreshActive
 @abstract		allows or not the pullToRefresh functionality
 */
@property BOOL isPullToRefreshActive;

/*!
 @property		isInfiniteScrollActive
 @abstract		allows or not the infinite scroll functionality
 */
@property BOOL isInfiniteScrollActive;
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*!
 @method		- (void)refresh
 @abstract		Tells the webview to be refresh its content.
 */
- (void)refresh;

/*!
 @method		- (void)refreshWebViewDataSource
 @abstract		Starts refreshing the web view data source.
 */
- (void)refreshWebViewDataSource;

/*!
 @method		- (void)didRefresh
 @abstract		Tells the web view it has been refreshed.
 */
- (void)didRefresh;

/*!
 @method		- (void)stopPullToRefreshRefreshing
 @abstract		stop the pullToRefresh from its refreshing state.
 @discussion	call this method when you want to reset the pullToRefresh state.
 */
-(void) stopPullToRefreshRefreshing;

/*!
 @method		- (void)loadMoreItems
 @abstract		Tells the webview to be load more datas
 */
- (void)loadMoreItems;

/*!
 @method		-(void) loadMoreContentInWebview
 @abstract		Starts loading more content in webview
 */
-(void) loadMoreContentInWebview;

/*!
 @method		-(void)moreItemsLoaded
 @abstract		Tells the web view more items have been loaded
 */
-(void)moreItemsLoaded;

/*!
 @method		- (void)stopInfiniteScrollRefreshing
 @abstract		stop the pullToRefresh from its refreshing state.
 @discussion	call this method when you want to reset the infinite scroll state.
 */
-(void) stopInfiniteScrollRefreshing;


@end