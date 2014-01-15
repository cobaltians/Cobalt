//
//  HPZoomHybridViewController.h
//  HPHybridCatalog
//
//  Created by Diane on 17/04/13.
//  Copyright (c) 2013 Haploid. All rights reserved.
//

#import "CobaltViewController.h"

@interface HPZoomHybridViewController : CobaltViewController

@property (strong, nonatomic) IBOutlet UIButton *zoomOutButton;
@property (strong, nonatomic) IBOutlet UIButton *zoomInButton;


@property (strong,nonatomic) NSNumber *textSizeCurrentZoomLevel;
@property (strong,nonatomic) NSNumber *textSizeMaxZoomLevel;
@property (strong,nonatomic) NSNumber *textSizeMinZoomLevel;

/*!
 @method		- (IBAction)onZoomInButton:(id)sender
 @abstract		Processes the action when the zoom in button is clicked.
 @param         sender  The object posting the action.
 */
- (IBAction)onZoomOutButton:(id)sender;

/*!
 @method		- (IBAction)onZoomOutButton:(id)sender
 @abstract		Processes the action when the zoom out button is clicked.
 @param         sender  The object posting the action.
 */
- (IBAction)onZoomInButton:(id)sender;

@end
