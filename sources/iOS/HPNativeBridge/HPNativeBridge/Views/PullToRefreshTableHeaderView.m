//
//  PullToRefreshTableHeaderView.m
//  Socle
//
//  Created by Vincent Ganneau on 24/11/11.
//  Copyright (c) 2011 Haploid. All rights reserved.
//

#import "PullToRefreshTableHeaderView.h"


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark IMPLEMENTATION
#pragma mark - 
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@implementation PullToRefreshTableHeaderView

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark PROPERTIES
#pragma mark - 
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@synthesize loadingHeight, progressView, arrowImageView, lastUpdatedLabel, statusLabel, state;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark METHODS
#pragma mark - 
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//*******************
// SET LAST UPDATED *
//*******************
/*!
 @method		- (void)setLastUpdated:(NSString *)lastUpdated
 @abstract		Sets the last updated text.
 @param         lastUpdated The last updated text to set.
 */
- (void)setLastUpdated:(NSString *)lastUpdated {
    self.lastUpdatedLabel.text = lastUpdated;
}

//************
// SET STATE *
//************
/*!
 @method		- (void)setState:(RefreshState)newState
 @abstract		Sets the refresh state.
 @param         newState    The refresh state to set.
 */
- (void)setState:(RefreshState)newState {
    switch (newState) {
        case RefreshStateNormal:
            if (state == RefreshStatePulling) {
                [UIView beginAnimations:nil context:nil];
                [UIView setAnimationDuration:0.2];
                self.arrowImageView.transform = CGAffineTransformIdentity;
                [UIView commitAnimations];
            }
            self.statusLabel.text = [self textForState:newState];
            [self.progressView stopAnimating];
            self.arrowImageView.hidden = NO;
            self.arrowImageView.transform = CGAffineTransformIdentity;
            break;
            
        case RefreshStatePulling:
            self.statusLabel.text = [self textForState:newState];
            [UIView beginAnimations:nil context:nil];
            [UIView setAnimationDuration:0.2];
            self.arrowImageView.transform = CGAffineTransformMakeRotation(M_PI);
            [UIView commitAnimations];
            break;
            
        case RefreshStateLoading:
            self.statusLabel.text = [self textForState:newState];
            [self.progressView startAnimating];
            self.arrowImageView.hidden = YES;
            break;
            
        default:
            break;
    }
    
    state = newState;
}

//************
// SET TEXT FOR STATE *
//************
/*!
 @method		- (NSString *)textForState:(RefreshState)newState
 @abstract		Sets the text for the status label depending on the newState given
 @param         newState The new state applied to the pullToRefreshTableHeaderView
 @return        a NSString containing the string to display for the given mode.
 @discussion    This method may be overriden in subclasses.
 */
- (NSString *)textForState:(RefreshState)newState {
    switch (newState) {
        case RefreshStateNormal:
            return NSLocalizedString(@"Tirez pour rafraîchir...", nil);
            break;
        case RefreshStatePulling:
            return NSLocalizedString(@"Relâchez pour actualiser...", nil);
            break;
        case RefreshStateLoading:
            return NSLocalizedString(@"Chargement...", nil);
            break;
        default:
            break;
    }
    return @"";
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
- (void)dealloc {
   // [progressView release]; progressView = nil;
    //[arrowImageView release]; arrowImageView = nil;
    //[lastUpdatedLabel release]; lastUpdatedLabel = nil;
    //[statusLabel release]; statusLabel = nil;
    //[super dealloc];
}

@end
