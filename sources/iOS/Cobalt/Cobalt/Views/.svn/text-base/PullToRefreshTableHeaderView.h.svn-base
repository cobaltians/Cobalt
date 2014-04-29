//
//  PullToRefreshTableHeaderView.h
//  Socle
//
//  Created by Vincent Ganneau on 24/11/11.
//  Copyright (c) 2011 Haploid. All rights reserved.
//

#import <UIKit/UIKit.h>

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark ENUMERATE
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

typedef enum {
    RefreshStateNormal = 0,
    RefreshStatePulling,
    RefreshStateLoading
} RefreshState;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark INTERFACE
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*!
 @class			PullToRefreshTableHeaderView
 @abstract		Class for header of table view that pull to refresh.
 */
@interface PullToRefreshTableHeaderView : UIView {
    
    CGFloat loadingHeight;
    UIActivityIndicatorView *progressView;
    UIImageView *arrowImageView;
    UILabel *lastUpdatedLabel;
    UILabel *statusLabel;
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
@property (nonatomic, retain) IBOutlet UIActivityIndicatorView *progressView;

/*!
 @property		arrowImageView
 @abstract		The arrow image view.
 */
@property (nonatomic, retain) IBOutlet UIImageView *arrowImageView;

/*!
 @property		lastUpdatedLabel
 @abstract		The last updated label.
 */
@property (nonatomic, retain) IBOutlet UILabel *lastUpdatedLabel;

/*!
 @property		statusLabel
 @abstract		The status label.
 */
@property (nonatomic, retain) IBOutlet UILabel *statusLabel;

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
