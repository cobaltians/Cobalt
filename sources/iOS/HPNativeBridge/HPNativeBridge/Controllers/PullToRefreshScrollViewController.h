//
//  PullToRefreshScrollViewController.h
//
//  Copyright (c) 2012 Haploid. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "PullToRefreshTableHeaderView.h"

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark INTERFACE
#pragma mark - 
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*!
 @class			PullToRefreshTableViewController
 @abstract		Base class for pull to refresh table view controllers.
 */
@interface PullToRefreshScrollViewController : UIViewController <UIScrollViewDelegate> {
    PullToRefreshTableHeaderView *pullToRefreshTableHeaderView;
	BOOL _refreshing;
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
 @property		scrollView
 @abstract		The scrollView
 */
@property (nonatomic, strong) IBOutlet UIScrollView * scrollView;

/*!
 @property		refreshing
 @abstract		The Boolean to know if PTR is refreshing
 */
@property (atomic) BOOL _refreshing;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark METHODS
#pragma mark - 
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*!
 @method		- (void)refresh
 @abstract		Tells the table view to be refresh its content.
 */
- (void)refresh;

/*!
 @method		- (void)refreshTableViewDataSource
 @abstract		Starts refreshing the table view data source.
 */
- (void)refreshTableViewDataSource;

/*!
 @method		- (void)didRefresh
 @abstract		Tells the table view it has been refreshed.
 */
- (void)didRefresh;

@end
