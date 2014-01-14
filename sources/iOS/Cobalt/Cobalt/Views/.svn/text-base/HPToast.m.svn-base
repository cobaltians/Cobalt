//
//  HPToast.m
//  HPNativeBridge
//
//  Created by Diane on 26/04/13.
//  Copyright (c) 2013 Haploid. All rights reserved.
//

#import "HPToast.h"

@implementation HPToast


- (void) hideToast:(NSTimer*)theTimer{

    if(self.delegate)
        [self.delegate HPToastwillHide:self];
    
        [super hideToast:theTimer];
}


-(void) show:(iToastType)type
{
    if(self.delegate)
        [self.delegate HPToastwillShow:self];
    
    [super show:type];
    [view setUserInteractionEnabled:NO];
}

+ (HPToast *) makeText:(NSString *) tex
{
    HPToast *t = [[HPToast alloc] initWithText:tex];
    [t setDuration:iToastDurationNormal];
    return t;
}

- (id) initWithText:(NSString *) tex{
	if (self = [super init]) {
		text = tex;
	}
	
	return self;
}

@end
