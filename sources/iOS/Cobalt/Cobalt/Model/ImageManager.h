//
//  ImageManager.h
//  Cobalt
//
//  Created by Haploid on 20/03/14.
//  Copyright (c) 2014 Haploid. All rights reserved.
//

#import <AssetsLibrary/AssetsLibrary.h>
#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h> 

@interface ImageManager : NSObject {
    int _identifierIndex;
    NSMutableDictionary * _imagesArray;
}

+ (ImageManager *)sharedInstance;

- (UIImage *) imageForIdentifier: (NSNumber *) identifier;
- (ALAsset *) assetForIdentifier: (NSNumber *) identifier;
- (NSNumber *)saveImage: (UIImage *)imageToSave;
- (NSNumber *)saveAsset: (ALAsset *)assetToSave;

@end
