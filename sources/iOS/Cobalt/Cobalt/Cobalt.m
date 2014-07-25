/**
 *
 * Cobalt.m
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

#import "Cobalt.h"

@implementation Cobalt

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark RESOURCE PATH
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

static NSString * sResourcePath = nil;

+ (void)setResourcePath:(NSString *)resourcePath
{
    sResourcePath = resourcePath;
}

+ (NSString *)resourcePath
{
    if (sResourcePath != nil) {
        return sResourcePath;
    }
    else {
        return [NSString stringWithFormat:@"%@%@",[[NSBundle mainBundle] resourcePath], @"/www/"];
    }
}

+ (NSDictionary *)getControllersConfiguration
{
    //TODO not load this file each time
    NSString * configuration = [Cobalt stringWithContentsOfFile:[NSString stringWithFormat:@"%@%@", [Cobalt resourcePath], confFileName]];
    
    if (configuration) {
        return [[Cobalt JSONObjectWithString:configuration] objectForKey: kControllers];
    }
    else {
        return nil;
    }
}

+ (NSDictionary *)getPluginsConfiguration
{
    //TODO not load this file each time
    NSString * configuration = [Cobalt stringWithContentsOfFile:[NSString stringWithFormat:@"%@%@", [Cobalt resourcePath], confFileName]];
    
    if (configuration) {
        return [[Cobalt JSONObjectWithString:configuration] objectForKey: kPlugins];
    }
    else {
        return nil;
    }
}

+ (NSString *)stringWithContentsOfFile:(NSString *)path
{
    if (path != nil) {
        NSURL * url = [NSURL fileURLWithPath:path isDirectory:NO];
        NSError * error;
        NSString * content = [[NSString alloc] initWithContentsOfURL:url encoding:NSUTF8StringEncoding error:&error];
        
#if DEBUG_COBALT
        if (! content) {
            NSLog(@"stringWithContentsOfFile: Error while reading file at %@\n%@", url, [error localizedFailureReason]);
        }
#endif
        
        return content;
    }
    else {
#if DEBUG_COBALT
        NSLog(@"stringWithContentsOfFile: path is nil");
#endif
        return nil;
    }
}

+ (id)JSONObjectWithString:(NSString *)string
{
    NSError * error;
    NSData * data = [string dataUsingEncoding:NSUTF8StringEncoding];
    
    id dictionary = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&error];
    
#if DEBUG_COBALT
    if (! dictionary) {
        NSLog(@"JSONObjectWithString: Error while reading JSON %@\n%@", string, [error localizedFailureReason]);
    }
#endif
    
    return dictionary;
}

@end
