//
//  CobaltPullToRefreshViewController.h
//  Cobalt
//
//  Created by Diane on 24/04/13.
//  Copyright (c) 2013 Haploid. All rights reserved.
//

#import "CobaltViewController.h"
#import "PullToRefreshTableHeaderView.h"


@interface CobaltPullToRefreshViewController : CobaltViewController <UIScrollViewDelegate> {
    PullToRefreshTableHeaderView *pullToRefreshTableHeaderView;
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


@end
