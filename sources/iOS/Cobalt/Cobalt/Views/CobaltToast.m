/**
 *
 * CobaltToast.m
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

#import "CobaltToast.h"

@implementation CobaltToast

+ (CobaltToast *)makeText:(NSString *)text
{
    CobaltToast * toast = [[CobaltToast alloc] initWithText:text];
    [toast setDuration:iToastDurationNormal];
    return toast;
}

- (id)initWithText:(NSString *)pText
{
	if (self = [super init]) {
		text = pText;
	}
	
	return self;
}

- (void)show:(iToastType)type
{
    if (self.delegate) {
        [self.delegate toastWillShow:self];
    }
    
    [super show:type];
    
    [view setUserInteractionEnabled:NO];
}

- (void)hideToast:(NSTimer *)pTimer
{
    if (self.delegate) {
        [self.delegate toastWillHide:self];
    }
    
    [super hideToast:pTimer];
}

@end
