/**
 *
 * HPPtrIsViewController.h
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

@interface HPPtrIsViewController : CobaltViewController <UIScrollViewDelegate> {
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