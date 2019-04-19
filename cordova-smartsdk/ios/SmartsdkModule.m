
#import "SmartsdkModule.h"
#import "SmartsdkKeys.h"
#import <Foundation/Foundation.h>
#import <SmartsdkKit/AXTSdkInit.h>
#import <SmartsdkKit/AXTSdkResult.h>
#import <SmartsdkKit/AXTDocumentIdentity.h>
#import <SmartsdkKit/AXTDocumentCreditCard.h>
#import <SmartsdkKit/AXTDocumentRegistrationVehicle.h>
#import <SmartsdkKit/AXTCaptureInterface.h>
#import <Cordova/CDVPlugin.h>

@implementation SmartsdkModule

NSString* callback;

- (void)init:(CDVInvokedUrlCommand*)command {
    NSLog(@"Initilization started");
    AXTSdkInit* sdkInit = [AXTSdkInit new];
    [sdkInit setLicenseFilename:@"licence"];
    sdkInit.timeoutActivation = 20;
    
    [[AXTCaptureInterface captureInterfaceInstance] initCaptureSdk:sdkInit withCompletion:^(NSArray *result, NSException *error) {
        if (error == nil) {
            NSLog(@"IDCHECK.IO SDK initialization : success");
            [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"INIT_SUCCESS"] callbackId:command.callbackId];
        } else {
            NSLog(@"IDCHECK.IO SDK initialization : error");
            [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"INIT_FAILED"] callbackId:command.callbackId];
        }
    }];
}

- (void)capture:(CDVInvokedUrlCommand*)command {
    NSLog(@"Start IDCHECK.IO SDK capture");
    callback = command.callbackId;
    
    NSData *data = [command.arguments[0] dataUsingEncoding:NSUTF8StringEncoding];
    id json = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
    
    AXTSdkParams* sdkParams = [[AXTSdkParams alloc]init];
    NSMutableDictionary* parameters = [[NSMutableDictionary alloc]init];
    [parameters setObject:@([self getBooleanFromString:[json objectForKey:PARAMS_USE_HD]]) forKey:AXTSdkParameters(USE_HD)];
    [parameters setObject:@([self getBooleanFromString:[json objectForKey:PARAMS_USE_FRONT_CAMERA]]) forKey:AXTSdkParameters(USE_FRONT_CAMERA)];
    [parameters setObject:@([self getBooleanFromString:[json objectForKey:PARAMS_EXTRACT_DATA]]) forKey:AXTSdkParameters(EXTRACT_DATA)];
    [parameters setObject:@([self getBooleanFromString:[json objectForKey:PARAMS_SCAN_BOTH_SIDE]]) forKey:AXTSdkParameters(SCAN_BOTH_SIDE)];
    [parameters setObject:@([self getBooleanFromString:[json objectForKey:PARAMS_DISPLAY_RESULT]]) forKey:AXTSdkParameters(DISPLAY_RESULT)];
    [parameters setObject:AXTDataExtractionRequirement([self getDataExtractionRequirementFromString:[json objectForKey:PARAMS_DATA_EXTRACTION_REQUIREMENT]]) forKey:AXTSdkParameters(DATA_EXTRACTION_REQUIREMENT)];
    [sdkParams setDoctype:[self getDocTypeFromString:[json objectForKey:DOCTYPE]]];
    [sdkParams setParameters:parameters];
    
    if ([json objectForKey:EXTRA_PARAMETERS] != nil) {
        NSMutableDictionary* extras = [[NSMutableDictionary alloc]init];
        
        for (NSString* key in [[json objectForKey:EXTRA_PARAMETERS] allKeys]) {
            if ([[[json objectForKey:EXTRA_PARAMETERS] objectForKey:key] isKindOfClass:[NSNumber class]]) {
                NSString* value = [[json objectForKey:EXTRA_PARAMETERS] objectForKey:key];
                [extras setObject:@([value intValue]) forKey:key];
            } else if ([[[json objectForKey:EXTRA_PARAMETERS] objectForKey:key] isKindOfClass:[NSString class]]) {
                NSString* value = [[json objectForKey:EXTRA_PARAMETERS] objectForKey:key];
                if ([[[json objectForKey:EXTRA_PARAMETERS] objectForKey:key] isEqualToString:@"false"] ) {
                    [extras setObject:@(NO) forKey:key];
                } else if ([[[json objectForKey:EXTRA_PARAMETERS] objectForKey:key] isEqualToString:@"true"]) {
                    [extras setObject:@(YES) forKey:key];
                } else {
                    [extras setObject:value forKey:key];
                }
            }
        }
        [sdkParams setExtraParameters:extras];
    }
    
    UIViewController* controller = [[AXTCaptureInterface captureInterfaceInstance] getViewControllerCaptureSdk:sdkParams];
    
    if (controller != nil) {
        UIViewController *rootViewController = [[[[UIApplication sharedApplication] delegate] window] rootViewController];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(getResultFromSmartcrop:) name:SMARTSDK_RESULT object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(smartCropCancelled) name:SMARTSDK_CANCELLED object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(smartCropCrash:) name:SMARTSDK_CRASH object:nil];
        dispatch_async( dispatch_get_main_queue(), ^{
            [rootViewController presentViewController:controller animated:NO completion:nil];
        });
    } else {
        NSLog(@"Wait the initialization end, please...");
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR] callbackId:command.callbackId];
    }
}

- (Boolean) getBooleanFromString:(NSString*)string {
    if ([string isEqualToString:@"false"]){
        return NO;
    }
    return YES;
}

- (AXTDocumentType) getDocTypeFromString:(NSString *)type {
    AXTDocumentType result;
    if ([type isEqualToString:@"ID"]){
        result = ID;
    } else if ([type isEqualToString:@"DL_USA"]){
        result = DL_USA;
    } else if ([type isEqualToString:@"A4"]){
        result = A4;
    } else if ([type isEqualToString:@"MRZ"]){
        result = MRZ;
    } else if ([type isEqualToString:@"VEHICLE_REGISTRATION"]){
        result = VEHICLE_REGISTRATION;
    } else if ([type isEqualToString:@"SELFIE"]){
        result = SELFIE;
    } else if ([type isEqualToString:@"A4_PORTRAIT"]){
        result = A4_PORTRAIT;
    } else if ([type isEqualToString:@"A4_LANDSCAPE"]){
        result = A4_LANDSCAPE;
    } else if ([type isEqualToString:@"CHEQUE"]){
        result = CHEQUE;
    } else if ([type isEqualToString:@"OLD_DL_FR"]){
        result = OLD_DL_FR;
    } else if ([type isEqualToString:@"ANY"]){
        result = ANY;
    } else if ([type isEqualToString:@"CREDIT_CARD"]){
        result = CREDIT_CARD;
    } else if ([type isEqualToString:@"PHOTO"]){
        result = PHOTO;
    } else {
        result = DISABLED;
    }
    return result;
}

- (AXTDataExtractionRequirement) getDataExtractionRequirementFromString:(NSString*)string {
    AXTDataExtractionRequirement result = NONE;
    if ([string isEqualToString:@"MRZ_FOUND"]){
        result = MRZ_FOUND;
    } else if ([string isEqualToString:@"MRZ_VALID"]){
        result = MRZ_VALID;
    }
    return result;
}

- (NSString*)getJSONFromResult:(AXTSdkResult*)result {
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    
    NSMutableDictionary *crop = [NSMutableDictionary dictionary];
    for (NSString* key in [result mapImageCropped]) {
        NSMutableDictionary *fieldCropped = [NSMutableDictionary dictionary];
        AXTImageResult *image = [[result mapImageCropped] objectForKey:key];
        [fieldCropped setObject:[image imagePath] forKey:IMAGE_URI];
        [crop setObject:fieldCropped forKey:key];
    }
    [dict setObject:crop forKey:MAP_IMAGE_CROPPED];
    
    NSMutableDictionary *source = [NSMutableDictionary dictionary];
    for (NSString* key in [result mapImageSource]) {
        NSMutableDictionary *fieldSource = [NSMutableDictionary dictionary];
        AXTImageResult *image = [[result mapImageSource] objectForKey:key];
        [fieldSource setObject:[image imagePath] forKey:IMAGE_URI];
        [source setObject:fieldSource forKey:key];
    }
    [dict setObject:source forKey:MAP_IMAGE_SOURCE];
    
    NSMutableDictionary *faces = [NSMutableDictionary dictionary];
    for (NSString* key in [result mapImageFace]) {
        NSMutableDictionary *fieldFace = [NSMutableDictionary dictionary];
        AXTImageResult *image = [[result mapImageFace] objectForKey:key];
        [fieldFace setObject:[image imagePath] forKey:IMAGE_URI];
        [faces setObject:fieldFace forKey:key];
    }
    [dict setObject:faces forKey:MAP_IMAGE_FACE];
    
    NSMutableDictionary *documents = [NSMutableDictionary dictionary];
    for(NSString* key in [result mapDocument]){
        NSMutableDictionary *docfields = [NSMutableDictionary dictionary];
        NSMutableDictionary *fields = [NSMutableDictionary dictionary];
        if ([key isEqualToString:IDENTITY_DOCUMENT]){
            AXTDocumentIdentity* doc = [[result mapDocument] objectForKey:key];
            if ([[doc fields] valueForKey:CODELINE] != nil) {
                [fields setObject:[[doc fields] valueForKey:CODELINE] forKey:CODELINE];
            }
            if ([[doc fields] valueForKey:EMIT_DATE] != nil) {
                [fields setObject:[[doc fields] valueForKey:EMIT_DATE] forKey:EMIT_DATE];
            }
            if ([[doc fields] valueForKey:EMIT_COUNTRY] != nil) {
                [fields setObject:[[doc fields] valueForKey:EMIT_COUNTRY] forKey:EMIT_COUNTRY];
            }
            if ([[doc fields] valueForKey:DOCUMENT_NUMBER] != nil) {
                [fields setObject:[[doc fields] valueForKey:DOCUMENT_NUMBER] forKey:DOCUMENT_NUMBER];
            }
            if ([[doc fields] valueForKey:LAST_NAMES] != nil) {
                [fields setObject:[[doc fields] valueForKey:LAST_NAMES] forKey:LAST_NAMES];
            }
            if ([[doc fields] valueForKey:FIRST_NAMES] != nil) {
                [fields setObject:[[doc fields] valueForKey:FIRST_NAMES] forKey:FIRST_NAMES];
            }
            if ([[doc fields] valueForKey:GENDER] != nil) {
                [fields setObject:[[doc fields] valueForKey:GENDER] forKey:GENDER];
            }
            if ([[doc fields] valueForKey:BIRTH_DATE] != nil) {
                [fields setObject:[[doc fields] valueForKey:BIRTH_DATE] forKey:BIRTH_DATE];
            }
            if ([[doc fields] valueForKey:NATIONALITY] != nil) {
                [fields setObject:[[doc fields] valueForKey:NATIONALITY] forKey:NATIONALITY];
            }
            if ([[doc fields] valueForKey:PERSONAL_NUMBER] != nil) {
                [fields setObject:[[doc fields] valueForKey:PERSONAL_NUMBER] forKey:PERSONAL_NUMBER];
            }
            
            AXTDocumentValidityResult validity = [doc documentValidity];
            if (validity == VALID) {
                [docfields setObject:VALIDITY_VALID forKey:DOCUMENT_VALIDITY];
            } else if (validity == INVALID) {
                [docfields setObject:VALIDITY_INVALID forKey:DOCUMENT_VALIDITY];
            } else {
                [docfields setObject:VALIDITY_CONTROL_NOT_AVAILABLE forKey:DOCUMENT_VALIDITY];
            }
            
            if ([doc documentType] != nil) {
                [docfields setObject:[doc documentType] forKey:DOCUMENT_TYPE];
            }
        } else if ([key isEqualToString:CREDIT_CARD_DOCUMENT]){
            AXTDocumentCreditCard* doc = [[result mapDocument] objectForKey:key];
            if ([[doc fields] valueForKey:CODELINE] != nil) {
                [fields setObject:[[doc fields] valueForKey:CODELINE] forKey:CODELINE];
            }
            if ([[doc fields] valueForKey:EXPIRATION_MONTH] != nil) {
                [fields setObject:[[doc fields] valueForKey:EXPIRATION_MONTH] forKey:EXPIRATION_MONTH];
            }
            if ([[doc fields] valueForKey:EXPIRATION_YEAR] != nil) {
                [fields setObject:[[doc fields] valueForKey:EXPIRATION_YEAR] forKey:EXPIRATION_YEAR];
            }
            if ([[doc fields] valueForKey:PAYMENT_CARD_NUMBER] != nil) {
                [fields setObject:[[doc fields] valueForKey:PAYMENT_CARD_NUMBER] forKey:PAYMENT_CARD_NUMBER];
            }
            if ([doc documentValidity] != nil) {
                AXTDocumentValidityResult validity = [doc documentValidity];
                if (validity == VALID) {
                    [docfields setObject:VALIDITY_VALID forKey:DOCUMENT_VALIDITY];
                } else if (validity == INVALID) {
                    [docfields setObject:VALIDITY_INVALID forKey:DOCUMENT_VALIDITY];
                } else {
                    [docfields setObject:VALIDITY_CONTROL_NOT_AVAILABLE forKey:DOCUMENT_VALIDITY];
                }
            }
            if ([doc documentType] != nil) {
                [docfields setObject:[doc documentType] forKey:DOCUMENT_TYPE];
            }
        } else if ([key isEqualToString:REGISTRATION_VEHICLE_DOCUMENT]){
            AXTDocumentRegistrationVehicle* doc = [[result mapDocument] objectForKey:key];
            if ([[doc fields] valueForKey:CODELINE] != nil) {
                [fields setObject:[[doc fields] valueForKey:CODELINE] forKey:CODELINE];
            }
            if ([[doc fields] valueForKey:VEHICLE_NUMBER] != nil) {
                [fields setObject:[[doc fields] valueForKey:VEHICLE_NUMBER] forKey:VEHICLE_NUMBER];
            }
            if ([[doc fields] valueForKey:FIRST_REGISTRATION_DATE] != nil) {
                [fields setObject:[[doc fields] valueForKey:FIRST_REGISTRATION_DATE] forKey:FIRST_REGISTRATION_DATE];
            }
            if ([[doc fields] valueForKey:MODEL_NAME] != nil) {
                [fields setObject:[[doc fields] valueForKey:MODEL_NAME] forKey:MODEL_NAME];
            }
            if ([[doc fields] valueForKey:REGISTRATION_NUMBER] != nil) {
                [fields setObject:[[doc fields] valueForKey:REGISTRATION_NUMBER] forKey:REGISTRATION_NUMBER];
            }
            if ([[doc fields] valueForKey:MAKE_NAME] != nil) {
                [fields setObject:[[doc fields] valueForKey:MAKE_NAME] forKey:MAKE_NAME];
            }
            if ([doc documentValidity] != nil) {
                AXTDocumentValidityResult validity = [doc documentValidity];
                if (validity == VALID) {
                    [docfields setObject:VALIDITY_VALID forKey:DOCUMENT_VALIDITY];
                } else if (validity == INVALID) {
                    [docfields setObject:VALIDITY_INVALID forKey:DOCUMENT_VALIDITY];
                } else {
                    [docfields setObject:VALIDITY_CONTROL_NOT_AVAILABLE forKey:DOCUMENT_VALIDITY];
                }
            }
            if ([doc documentType] != nil) {
                [docfields setObject:[doc documentType] forKey:DOCUMENT_TYPE];
            }
        } else {
            AXTDocumentAbstract* doc = [[result mapDocument] objectForKey:key];
            if ([doc documentValidity] != nil) {
                AXTDocumentValidityResult validity = [doc documentValidity];
                if (validity == VALID) {
                    [docfields setObject:VALIDITY_VALID forKey:DOCUMENT_VALIDITY];
                } else if (validity == INVALID) {
                    [docfields setObject:VALIDITY_INVALID forKey:DOCUMENT_VALIDITY];
                } else {
                    [docfields setObject:VALIDITY_CONTROL_NOT_AVAILABLE forKey:DOCUMENT_VALIDITY];
                }
            }
            if ([doc documentType] != nil) {
                [docfields setObject:[doc documentType] forKey:DOCUMENT_TYPE];
            }
        }
        [docfields setObject:fields forKey:FIELDS];
        [documents setObject:docfields forKey:key];
    }
    [dict setObject:documents forKey:MAP_DOCUMENT];
    
    NSData * jsonData = [NSJSONSerialization dataWithJSONObject:dict options:0 error:nil];
    NSString * myString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    
    return myString;
    
}

- (void) getResultFromSmartcrop:(NSNotification *)paramNotification{
    UIViewController *rootViewController = [[[[UIApplication sharedApplication] delegate] window] rootViewController];
    dispatch_async( dispatch_get_main_queue(), ^{
        [rootViewController dismissViewControllerAnimated:NO completion:nil];
    });
    AXTSdkResult* result = [[paramNotification userInfo] valueForKey:SMARTSDK_RESULT_PARAM];
    
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:[self getJSONFromResult:result]] callbackId:callback];
}

-(void) smartCropCancelled{
    UIViewController *rootViewController = [[[[UIApplication sharedApplication] delegate] window] rootViewController];
    dispatch_async( dispatch_get_main_queue(), ^{
        [rootViewController dismissViewControllerAnimated:NO completion:nil];
    });
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"CANCEL_BY_USER"] callbackId:callback];
}

-(void) smartCropCrash:(NSNotification *)paramNotification{
    NSException* exception = [[paramNotification userInfo] valueForKey:SMARTSDK_EXCEPTION];
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[exception reason]] callbackId:callback];
}

@end
