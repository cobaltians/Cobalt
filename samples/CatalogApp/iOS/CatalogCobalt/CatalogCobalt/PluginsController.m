//
//  PluginsController.m
//  CatalogCobalt
//
//  Created by Haploid on 28/10/14.
//  Copyright (c) 2014 Haploid. All rights reserved.
//

#import "PluginsController.h"

@implementation PluginsController

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark WEBSERVICES STORAGE DELEGATE METHODS
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


- (id)processData:(id)data withParameters:(NSDictionary *)parameters {
    if(parameters) {
        NSString * ext = [parameters objectForKey: @"ext"];
        if(ext && [data isKindOfClass: [NSDictionary class]]) {
            NSMutableDictionary * dataToReturn = [(NSDictionary *)data mutableCopy];
            NSMutableDictionary * responseData = [[dataToReturn objectForKey: @"responseData"] mutableCopy];
            if(responseData)
            {
                NSArray * results = [responseData objectForKey: @"results"];
                NSMutableArray * resultsMutableCopy = [results mutableCopy];
                
                if(results) {
                    
                    for (NSDictionary * result in results)
                    {
                        NSString * url = [result objectForKey: @"unescapedUrl"];
                        if(![url hasSuffix: ext]) {
                            [resultsMutableCopy removeObject: result];
                        }
                    }
                    
                    [responseData setObject: resultsMutableCopy forKey: @"results"];
                    [dataToReturn setObject: responseData forKey: @"responseData"];
                    
                    return dataToReturn;
                }
                
                
            }
        }
    }
    
    return data;
}


@end
