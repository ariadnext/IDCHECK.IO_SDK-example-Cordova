#import "IdcheckioSdk.h"
#import <Foundation/Foundation.h>
#import "YOUR_PROJECT_NAME-Swift.h"

@implementation IdcheckioSdk

NSString* callback;

- (void) preload:(CDVInvokedUrlCommand*)command{
    BOOL extractData = command.arguments[0];
    [Idcheckio.shared preloadWithExtractData:extractData];
}

- (void) activate:(CDVInvokedUrlCommand*)command{
    NSString* licenceFileName = command.arguments[0];
    BOOL extractData = command.arguments[1];
    callback = command.callbackId;
    [Idcheckio.shared activateWithLicenseFilename:licenceFileName extractData:extractData onComplete:^(NSException* error){
        if(error == nil){
            [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:callback];
        } else {
            [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error reason]] callbackId:callback];
        }
    }];
}

- (void) start:(CDVInvokedUrlCommand*)command{
    NSData *data = [command.arguments[0] dataUsingEncoding:NSUTF8StringEncoding];
    id json = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
    SDKParams* params = [self getParamsFromJson:json];
    callback = command.callbackId;
    Idcheckio.shared.delegate = self;

    NSError* error;
    [Idcheckio.shared setParams:params error:&error];
    if(error != nil){
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]] callbackId:callback];
    }

    dispatch_async(dispatch_get_main_queue(), ^{
        UIViewController *sdkViewController = [[UIViewController alloc] init];
        IdcheckioView *cameraView = [[IdcheckioView alloc] init];

        cameraView.translatesAutoresizingMaskIntoConstraints = false;
        sdkViewController.view.frame = [self viewController].view.frame;
        [sdkViewController.view addSubview:cameraView];
        sdkViewController.view.backgroundColor = UIColor.blackColor;
        [[cameraView.leadingAnchor constraintEqualToAnchor:sdkViewController.view.leadingAnchor] setActive:true];
        [[cameraView.trailingAnchor constraintEqualToAnchor:sdkViewController.view.trailingAnchor] setActive:true];
        [[cameraView.topAnchor constraintEqualToAnchor:sdkViewController.view.topAnchor] setActive:true];
        [[cameraView.bottomAnchor constraintEqualToAnchor:sdkViewController.view.bottomAnchor] setActive:true];

        [self.viewController presentViewController:sdkViewController animated:true completion:^{
            [Idcheckio.shared startWith:cameraView completion:^(NSError *error) {
                if(error != nil){
                    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]] callbackId:callback];
                }
            }];
        }];
    });
}

- (CISContext*) getCisContextFromJson:(id) json{
    CISContext* cisContext = [[CISContext alloc] init];
    for(NSString* key in [json allKeys]){
        if([key isEqualToString:folderUid]){
            [cisContext setFolderUid:[json objectForKey:key]];
        } else if([key isEqualToString:referenceTaskUid]){
            [cisContext setReferenceTaskUid:[json objectForKey:key]];
        } else if([key isEqualToString:referenceDocUid]){
            [cisContext setReferenceDocUid:[json objectForKey:key]];
        }
    }
    return cisContext;
}

- (SDKParams*) getParamsFromJson:(id) json{
    SDKParams* params = [[SDKParams alloc] init];
    for(NSString* key in [json allKeys]){
        if([key isEqualToString:DocumentType]){
            [params setDocumentType:[json objectForKey:key]];
        } else if ([key isEqualToString:ConfirmType]) {
            [params setConfirmType:[json objectForKey:key]];
        } else if ([key isEqualToString:Side1Extraction]) {
            id jsonSide1 = [json objectForKey:key];
            Extraction* extractSide1 = [[Extraction alloc] init];
            [extractSide1 setFace:[jsonSide1 objectForKey:FaceDetection]];
            [extractSide1 setCodeline:[jsonSide1 objectForKey:DataRequirement]];
            [params setSide1Extraction:extractSide1];
        } else if ([key isEqualToString:Side2Extraction]) {
            id jsonSide2 = [json objectForKey:key];
            Extraction* extractSide2 = [[Extraction alloc] init];
            [extractSide2 setFace:[jsonSide2 objectForKey:FaceDetection]];
            [extractSide2 setCodeline:[jsonSide2 objectForKey:DataRequirement]];
            [params setSide2Extraction:extractSide2];
        } else if ([key isEqualToString:ScanBothSides]) {
            [params setScanBothSides:[json objectForKey:key]];
        } else if ([key isEqualToString:UseHD]) {
            [params setUseHD:[self getBooleanFromString:[json objectForKey:key]]];
        } else if ([key isEqualToString:ExtraParams]) {
            id extraParams = [json objectForKey:key];
            for(NSString* extraKey in [extraParams allKeys]){
                if([extraKey isEqualToString:Language]){
                    Idcheckio.shared.extraParameters.language = [extraParams objectForKey:extraKey];
                } else if([extraKey isEqualToString:ManualButtonTimer]){
                    Idcheckio.shared.extraParameters.manualButtonTimer = [extraParams doubleForKey:extraKey];
                } else if([extraKey isEqualToString:MaxPictureFilesize]){
                    [Idcheckio.shared.extraParameters setMaxPictureFilesize:[extraParams integerForKey:extraKey]];
                } else if([extraKey isEqualToString:FeedbackLevel]){
                    [Idcheckio.shared.extraParameters setFeedbackLevel:[extraParams objectForKey:extraKey]];
                } else if([extraKey isEqualToString:Token]){
                    [Idcheckio.shared.extraParameters setToken:[extraParams objectForKey:extraKey]];
                } else if([extraKey isEqualToString:ConfirmAbort]){
                    Idcheckio.shared.extraParameters.confirmAbort = [[extraParams objectForKey:extraKey] boolValue];
                } else if([extraKey isEqualToString:AdjustCrop]){
                    Idcheckio.shared.extraParameters.adjustCrop = [[extraParams objectForKey:extraKey] boolValue];
                }
            }
        }
    }
    return params;
}

- (void) startOnline:(CDVInvokedUrlCommand*)command{
    NSData *data = [command.arguments[0] dataUsingEncoding:NSUTF8StringEncoding];
    id json = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
    SDKParams* params = [self getParamsFromJson:json];
    NSString* licenceFileName = command.arguments[1];
    NSData *cisData = [command.arguments[2] dataUsingEncoding:NSUTF8StringEncoding];
    id cisJson = [NSJSONSerialization JSONObjectWithData:cisData options:0 error:nil];
    CISContext* cisContext = [self getCisContextFromJson:cisJson];
    callback = command.callbackId;
    Idcheckio.shared.delegate = self;

    NSError* error;
    [Idcheckio.shared setParams:params error:&error];
    if(error != nil){
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]] callbackId:callback];
    }

    dispatch_async(dispatch_get_main_queue(), ^{
        UIViewController *sdkViewController = [[UIViewController alloc] init];
        IdcheckioView *cameraView = [[IdcheckioView alloc] init];

        cameraView.translatesAutoresizingMaskIntoConstraints = false;
        sdkViewController.view.frame = [self viewController].view.frame;
        [sdkViewController.view addSubview:cameraView];
        sdkViewController.view.backgroundColor = UIColor.blackColor;
        [[cameraView.leadingAnchor constraintEqualToAnchor:sdkViewController.view.leadingAnchor] setActive:true];
        [[cameraView.trailingAnchor constraintEqualToAnchor:sdkViewController.view.trailingAnchor] setActive:true];
        [[cameraView.topAnchor constraintEqualToAnchor:sdkViewController.view.topAnchor] setActive:true];
        [[cameraView.bottomAnchor constraintEqualToAnchor:sdkViewController.view.bottomAnchor] setActive:true];

        [self.viewController presentViewController:sdkViewController animated:true completion:^{
            [Idcheckio.shared startOnlineWith:cameraView licenseFilename:licenceFileName cisContext:cisContext completion:^(NSError *error) {
                if(error != nil){
                    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]] callbackId:callback];
                }
            }];
        }];
    });

}

- (BOOL) getBooleanFromString:(NSString*)string {
    if ([string isEqualToString:@"false"] || [string isEqualToString:@"NO"]){
        return NO;
    }
    return YES;
}

- (void)idcheckioDidSendEventWithInteraction:(enum IdcheckioInteraction)interaction msg:(IdcheckioMsg * _Nullable)msg {}

- (void)idcheckioFinishedWithResult:(IdcheckioResult * _Nullable)result error:(NSError * _Nullable)error {
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.viewController dismissViewControllerAnimated:true completion:^{}];
    });
    if(result != nil){
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:[IdcheckioObjcUtil resultToJSON:result]] callbackId:callback];
    } else if(error != nil){
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]] callbackId:callback];
    }
}

@end
