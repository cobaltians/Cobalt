//
//  HPToast.h
//  Cobalt
//
//  Created by Diane on 26/04/13.
//  Copyright (c) 2013 Haploid. All rights reserved.
//

#import "iToast.h"

@class CobaltToast;
@protocol HPToastDelegateProtocol <NSObject>

-(void) HPToastwillShow:(CobaltToast *)toast;
-(void) HPToastwillHide:(CobaltToast *)toast;

@end

@interface CobaltToast : iToast

@property (nonatomic,retain) id<HPToastDelegateProtocol> delegate;

+ (CobaltToast *) makeText:(NSString *) text;

@end
