//
//  HPToast.h
//  HPNativeBridge
//
//  Created by Diane on 26/04/13.
//  Copyright (c) 2013 Haploid. All rights reserved.
//

#import "iToast.h"

@class HPToast;
@protocol HPToastDelegateProtocol <NSObject>

-(void) HPToastwillShow:(HPToast *)toast;
-(void) HPToastwillHide:(HPToast *)toast;

@end

@interface HPToast : iToast

@property (nonatomic,retain) id<HPToastDelegateProtocol> delegate;

+ (HPToast *) makeText:(NSString *) text;

@end
