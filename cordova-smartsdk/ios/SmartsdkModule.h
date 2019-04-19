#ifndef SmartsdkModule_h
#define SmartsdkModule_h

#import <Cordova/CDVPlugin.h>

@interface SmartsdkModule : CDVPlugin

- (void)init:(CDVInvokedUrlCommand*)command;

- (void)capture:(CDVInvokedUrlCommand*)command;

@end

#endif /* SmartsdkModule_h */
