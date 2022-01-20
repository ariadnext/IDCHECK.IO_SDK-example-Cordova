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
    BOOL disableAudioForLiveness = command.arguments[2];
    NSString* sdkEnvironment = [command.arguments[3] lowercaseString];
    callback = command.callbackId;
    [Idcheckio.shared activateWithLicenseFilename:licenceFileName extractData:extractData disableAudioForLiveness:disableAudioForLiveness sdkEnvironment:sdkEnvironment onComplete:^(NSException* error){
        if(error == nil){
            [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:callback];
        } else {
            [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error reason]] callbackId:callback];
        }
    }];
}

- (void) start:(CDVInvokedUrlCommand*)command{
    SDKParams* params = [self getParamsFromJson:command.arguments[0]];
    callback = command.callbackId;
    Idcheckio.shared.delegate = self;

    NSError* error;
    [Idcheckio.shared setParams:params error:&error];
    if(error != nil){
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]] callbackId:callback];
    }

    dispatch_async(dispatch_get_main_queue(), ^{
        IdcheckioViewController *idcheckioViewController = [[IdcheckioViewController alloc] init];
        idcheckioViewController.modalPresentationStyle = UIModalPresentationFullScreen;
        idcheckioViewController.isOnlineSession = false;
        idcheckioViewController.startCompletion = ^(NSError *error) {
            if(error != nil){
                dispatch_async(dispatch_get_main_queue(), ^{
                    [[[UIApplication sharedApplication] keyWindow].rootViewController dismissViewControllerAnimated:true completion:^{}];
                });
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]] callbackId:callback];
            }
        };
        [idcheckioViewController setResultCompletion:^(IdcheckioResult *result, NSError *error) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [[[UIApplication sharedApplication] keyWindow].rootViewController dismissViewControllerAnimated:true completion:^{}];
            });
            if(result != nil){
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:[IdcheckioObjcUtil resultToJSON:result]] callbackId:callback];
            } else if(error != nil){
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]] callbackId:callback];
            }
        }];
        [[[UIApplication sharedApplication] keyWindow].rootViewController presentViewController:idcheckioViewController animated:true completion:nil];
    });
}

- (SDKParams*) getParamsFromJson:(NSDictionary*)params {
    SDKParams* sdkParams = [[SDKParams alloc] init];
    for(NSString* key in [params allKeys]){
        if([key isEqualToString:docTypeKey]){
            [sdkParams setDocumentType:[params objectForKey:key]];
        } else if ([key isEqualToString:confirmationTypeKey]) {
            [sdkParams setConfirmType:[params objectForKey:key]];
        } else if ([key isEqualToString:sideOneExtractionKey]) {
            id paramsSide1 = [params objectForKey:key];
            Extraction* extractSide1 = [[Extraction alloc] init];
            [extractSide1 setFace:[paramsSide1 objectForKey:faceDetectionKey]];
            [extractSide1 setCodeline:[paramsSide1 objectForKey:codelineKey]];
            [sdkParams setSide1Extraction:extractSide1];
        } else if ([key isEqualToString:sideTwoExtractionKey]) {
            id paramsSide2 = [params objectForKey:key];
            Extraction* extractSide2 = [[Extraction alloc] init];
            [extractSide2 setFace:[paramsSide2 objectForKey:faceDetectionKey]];
            [extractSide2 setCodeline:[paramsSide2 objectForKey:codelineKey]];
            [sdkParams setSide2Extraction:extractSide2];
        } else if ([key isEqualToString:integrityCheckKey]) {
            id paramIntegrityCheck = [params objectForKey:key];
            IntegrityCheck *integrity = [[IntegrityCheck alloc] init];
            integrity.readEmrtd = [paramIntegrityCheck objectForKey:readEmrtdKey];
            [sdkParams setIntegrityCheck: integrity];
        } else if ([key isEqualToString:onlineConfigKey]){
            id onlineConfigParams = [params objectForKey:key];
            OnlineConfig *onlineConfig = [sdkParams onlineConfig];
            onlineConfig.isReferenceDocument = [[onlineConfigParams objectForKey:isReferenceDocumentKey] boolValue];
            [onlineConfig setCheckType:[onlineConfigParams objectForKey:checkTypeKey]];
            if ([onlineConfigParams objectForKey:cisTypeKey] != [NSNull null]) {
                [onlineConfig setCisType:[onlineConfigParams objectForKey:cisTypeKey]];
            }
            if ([onlineConfigParams objectForKey:folderUidKey] != [NSNull null]) {
                onlineConfig.folderUid = [onlineConfigParams objectForKey:folderUidKey];
            }
            if ([onlineConfigParams objectForKey:biometricConsentKey] != [NSNull null]) {
                [onlineConfig setBiometricConsentWithBiometricConsent:[[onlineConfigParams objectForKey:biometricConsentKey] boolValue]];
            }
            onlineConfig.enableManualAnalysis = [[onlineConfigParams objectForKey:enableManualAnalysisKey] boolValue];
        } else if ([key isEqualToString:scanBothSidesKey]) {
            [sdkParams setScanBothSides:[params objectForKey:key]];
        } else if ([key isEqualToString:useHdKey]) {
            [sdkParams setUseHD: [[params objectForKey:key] boolValue]];
        } else if([key isEqualToString:languageKey]){
            [Idcheckio.shared.extraParameters setLanguage:[params objectForKey:key]];
        } else if([key isEqualToString:manualButtonTimerKey]){
            Idcheckio.shared.extraParameters.manualButtonTimer = [[params objectForKey:key] doubleValue];
        } else if([key isEqualToString:maxPictureFilesizeKey]){
            [Idcheckio.shared.extraParameters setMaxPictureFilesize:[params objectForKey:key]];
        } else if([key isEqualToString:feedbackLevelKey]){
            [Idcheckio.shared.extraParameters setFeedbackLevel:[params objectForKey:key]];
        } else if([key isEqualToString:confirmAbortKey]){
            Idcheckio.shared.extraParameters.confirmAbort = [[params objectForKey:key] boolValue];
        } else if([key isEqualToString:adjustCropKey]){
            Idcheckio.shared.extraParameters.adjustCrop = [[params objectForKey:key] boolValue];
        } else if([key isEqualToString:sdkEnvironmentKey]){
            Idcheckio.shared.extraParameters.sdkEnvironment = [params objectForKey:key];
        }
    }
    return sdkParams;
}

- (void) startOnline:(CDVInvokedUrlCommand*)command{
    NSDictionary* json = command.arguments[0];
    SDKParams* params = [self getParamsFromJson:json];
    OnlineContext* onlineContext = [self getOnlineContextFromJson:command.arguments[1]];
    callback = command.callbackId;

    NSError* error;
    [Idcheckio.shared setParams:params error:&error];
    if(error != nil){
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]] callbackId:callback];
    }

    dispatch_async(dispatch_get_main_queue(), ^{
            IdcheckioViewController *idcheckioViewController = [[IdcheckioViewController alloc] init];
            idcheckioViewController.modalPresentationStyle = UIModalPresentationFullScreen;
            idcheckioViewController.isOnlineSession = true;
            idcheckioViewController.onlineContext = onlineContext;
            idcheckioViewController.startCompletion = ^(NSError *error) {
                if(error != nil){
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [[[UIApplication sharedApplication] keyWindow].rootViewController dismissViewControllerAnimated:true completion:^{}];
                    });
                    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]] callbackId:callback];
                }
            };
            [idcheckioViewController setResultCompletion:^(IdcheckioResult *result, NSError *error) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [[[UIApplication sharedApplication] keyWindow].rootViewController dismissViewControllerAnimated:true completion:^{}];
                });
                if(result != nil){
                    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:[IdcheckioObjcUtil resultToJSON:result]] callbackId:callback];
                } else if(error != nil){
                    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]] callbackId:callback];
                }
            }];
            [[[UIApplication sharedApplication] keyWindow].rootViewController presentViewController:idcheckioViewController animated:true completion:nil];
        });

}

- (void) analyze:(CDVInvokedUrlCommand*)command {
    SDKParams* params = [self getParamsFromJson:command.arguments[0]];
    NSString* side1 = command.arguments[1];
    NSString* side2 = command.arguments[2];
    BOOL online = command.arguments[3];
    NSError* error;
    [Idcheckio.shared setParams:params error:&error];
    if(error != nil){
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]] callbackId:callback];
    }
    callback = command.callbackId;
    Idcheckio.shared.delegate = self;
    OnlineContext* onlineContext = [self getOnlineContextFromJson:command.arguments[4]];
    NSURL *url1 = [NSURL URLWithString:[side1 stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLQueryAllowedCharacterSet]]];
    NSURL *url2 = [NSURL URLWithString:[side2 stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLQueryAllowedCharacterSet]]];
    UIImage *side1Image = [[UIImage alloc] initWithData:[NSData dataWithContentsOfURL:url1]];
    UIImage *side2Image = [[UIImage alloc] initWithData:[NSData dataWithContentsOfURL:url2]];
    [Idcheckio.shared analyzeWithSide1Image:side1Image side2Image:side2Image online:online onlineContext:onlineContext];
}

- (BOOL) getBooleanFromString:(NSString*)string {
    if ([string isEqualToString:@"false"] || [string isEqualToString:@"NO"]){
        return NO;
    }
    return YES;
}

- (OnlineContext*) getOnlineContextFromJson:(NSDictionary*)cis{
    if ([cis isEqual:[NSNull null]]) {
        return nil;
    }
    NSError * err;
    NSData * jsonData = [NSJSONSerialization  dataWithJSONObject:cis options:0 error:&err];
    NSString * myString = [[NSString alloc] initWithData:jsonData   encoding:NSUTF8StringEncoding];
    if (err == nil) {
        return [OnlineContext fromJson:myString];
    }
    return nil;
}

- (void)idcheckioDidSendEventWithInteraction:(enum IdcheckioInteraction)interaction msg:(IdcheckioMsg * _Nullable)msg {}

- (void)idcheckioFinishedWithResult:(IdcheckioResult * _Nullable)result error:(NSError * _Nullable)error {
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.viewController dismissViewControllerAnimated:true completion:^{
            if(result != nil){
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:[IdcheckioObjcUtil resultToJSON:result]] callbackId:callback];
            } else if(error != nil){
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]] callbackId:callback];
            }
        }];
    });
}

@end
