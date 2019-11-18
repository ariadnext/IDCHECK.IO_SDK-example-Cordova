#ifndef IdcheckioSdk_h
#define IdcheckioSdk_h

#import <Cordova/CDVPlugin.h>
#import <IDCheckIOSDK/IDCheckIOSDK-Swift.h>
#import "IdcheckioKeys.h"

@class IdcheckioObjcUtil;

@interface IdcheckioSdk : CDVPlugin <IdcheckioDelegate>

- (void) preload:(CDVInvokedUrlCommand*)command;

- (void) activate:(CDVInvokedUrlCommand*)command;

- (void) start:(CDVInvokedUrlCommand*)command;

- (void) startOnline:(CDVInvokedUrlCommand*)command;

@end

#endif
