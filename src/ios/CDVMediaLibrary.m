/*
 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at
 
 http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.
 */

#import <Cordova/CDV.h>
#import <Cordova/CDVViewController.h>

#import "ALAssetsLibrary+CustomPhotoAlbum.h"

#import "CDVMediaLibrary.h"

@interface CDVViewController (MediaLibrary)
@end

@implementation CDVViewController (MediaLibrary)
@end

@implementation CDVMediaLibrary

@synthesize library;
@synthesize callbackId;

- (void)pluginInitialize
{
    BOOL isiOS7 = (IsAtLeastiOSVersion(@"7.0"));
}

- (CDVPlugin*)initWithWebView:(UIWebView*)theWebView
{
	self = [super initWithWebView:theWebView];

	if (self) {
	}

	return self;
}

- (void) savePicture:(CDVInvokedUrlCommand*)command
{
	self.library = [[ALAssetsLibrary alloc] init];
	self.callbackId = command.callbackId;

	// NSData* imageData = [NSData dataFromBase64String:[command.arguments objectAtIndex:0]];
	/*
	NSURL *imageUri = [NSURL URLWithString:[command.arguments objectAtIndex:0]];
	NSData *imageData = [NSData dataWithContentsOfURL:imageUri];
	*/
	NSString* imageUri = [command.arguments objectAtIndex:0];
	NSString* albumName = [command.arguments objectAtIndex:1];
	NSLog(@"albumName: %@",albumName);

	// UIImage* image = [[[UIImage alloc] initWithData:imageData cache:NO] autorelease];
	UIImage* image = [[[UIImage alloc] initWithContentsOfFile:imageUri cache:NO] autorelease];

	[self.library saveImage:image toAlbum:albumName withCompletionBlock:^(NSError *error) {
		if (error != nil) {
			// Show error message...
			NSLog(@"ERROR: %@",error);

			CDVPluginResult* result = [CDVPluginResult resultWithStatus: CDVCommandStatus_ERROR messageAsString:error.description];
			[self.webView stringByEvaluatingJavaScriptFromString:[result toErrorCallbackString: self.callbackId]];
		} else {
			// Show message image successfully saved
			NSLog(@"Picture saved!");

			CDVPluginResult* result = [CDVPluginResult resultWithStatus: CDVCommandStatus_OK messageAsString:@"Picture saved"];
			[self.webView stringByEvaluatingJavaScriptFromString:[result toSuccessCallbackString: self.callbackId]];
		}
	}];
}

- (void) dealloc
{
	self.library = nil;

	[callbackId release];
	[super dealloc];
}

@end
