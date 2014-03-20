//
//  ImageManager.m
//  Cobalt
//
//  Created by Haploid on 20/03/14.
//  Copyright (c) 2014 Haploid. All rights reserved.
//

#import "ImageManager.h"

@implementation ImageManager

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark SINGLETON
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

static ImageManager * instance = nil;

//******************
// SHARED INSTANCE *
//******************
/*!
 @method		+ (WebServices *)sharedInstance
 @abstract		Returns the singleton instance of web services.
 @result		The singleton instance of web services.
 */
+ (ImageManager *)sharedInstance {
	@synchronized(self) {
		if (instance == nil) {
			instance = [[self alloc] init];
		}
	}
	return instance;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark INITIALISATION
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//********
// INIT  *
//********
//
// Description :
//
- (id)init{
	if (self = [super init]) {
        _imagesArray = [[NSMutableDictionary alloc] init];
        _identifierIndex = 0;
    }
	return self;
}


-(UIImage *) imageForIdentifier: (NSNumber *) identifier {
    NSObject * object = [_imagesArray objectForKey: identifier];
    
    if([object isKindOfClass: [UIImage class]]) {
        return (UIImage *)object;
    }
    
    return nil;
}

-(ALAsset *)assetForIdentifier: (NSNumber *) identifier {
    NSObject * object = [_imagesArray objectForKey: identifier];
    
    if([object isKindOfClass: [ALAsset class]]) {
        return (ALAsset *)object;
    }
    
    return nil;
}

- (NSNumber *)saveImage: (UIImage *)imageToSave {
    _identifierIndex ++;
    
    NSNumber * identifier = [NSNumber numberWithInt: _identifierIndex];
    
    [_imagesArray setObject: imageToSave forKey: identifier];
    
    return identifier;
}

- (NSNumber *)saveAsset: (ALAsset *)assetToSave {
    _identifierIndex ++;
    
    NSNumber * identifier = [NSNumber numberWithInt: _identifierIndex];
    
    [_imagesArray setObject: assetToSave forKey: identifier];
    
    return identifier;
}

@end
