package com.ariadnext.sdk;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ariadnext.android.smartsdk.bean.enums.AXTSdkParameters;
import com.ariadnext.android.smartsdk.enums.EnumExtraParameter;
import com.ariadnext.android.smartsdk.exception.CaptureApiException;
import com.ariadnext.android.smartsdk.interfaces.AXTCaptureInterface;
import com.ariadnext.android.smartsdk.interfaces.bean.AXTDataExtractionRequirement;
import com.ariadnext.android.smartsdk.interfaces.bean.AXTDocument;
import com.ariadnext.android.smartsdk.interfaces.bean.AXTDocumentType;
import com.ariadnext.android.smartsdk.interfaces.bean.AXTSdkInit;
import com.ariadnext.android.smartsdk.interfaces.bean.AXTSdkParams;
import com.ariadnext.android.smartsdk.interfaces.AXTCaptureInterfaceCallback;
import com.ariadnext.android.smartsdk.interfaces.bean.AXTSdkResult;
import com.ariadnext.android.smartsdk.utils.AXTStringUtils;
import com.ariadnext.android.smartsdk.utils.EnumUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SmartsdkModule extends CordovaPlugin implements AXTCaptureInterfaceCallback{
    private final static String TAG = "IdcheckIO";
    private enum Action {INIT, CAPTURE};
    private boolean isActivated = false;
    private CordovaInterface cordova;
    private CallbackContext callbackContext;
    private final static int SDK_REQUEST_CODE = 10;

    private static final String EXTRACT_DATA = "ExtractData";
    private static final String USE_HD = "UseHD";
    private static final String USE_FRONT_CAMERA = "UseFrontCamera";
    private static final String READ_RFID = "ReadRfid";
    private static final String DISPLAY_RESULT = "DisplayResult";
    private static final String SCAN_BOTH_SIDE = "ScanBothSide";
    private static final String DOC_TYPE = "DocType";
    private static final String DATA_EXTRACTION_REQUIREMENT = "ExtractRequirement";
    private static final String EXTRA_PARAMETERS = "ExtraParameters";

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "Receive result from Activity");
        if (requestCode == SDK_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    final AXTSdkResult result = AXTCaptureInterface.INSTANCE.getResultImageFromCapture(data);
                    String json_cropped = mapper.writeValueAsString(result.getMapImageCropped());
                    String json_source = mapper.writeValueAsString(result.getMapImageSource());
                    String json_face = mapper.writeValueAsString(result.getMapImageFace());
                    String json_document = mapper.writeValueAsString(result.getMapDocument());
                    String json_result = "{\"mapImageSource\":"+json_source+",\"mapImageCropped\":"+json_cropped+",\"mapImageFace\":"+json_face+",\"mapDocument\":"+json_document+"}";
                    Log.d(TAG, json_result);
                    this.callbackContext.success(json_result);
                } catch (final CaptureApiException ex) {
                    switch (ex.getCodeType()) {
                        case LICENSE_SDK_ERROR:
                            callbackContext.error("LICENSE_SDK_ERROR");
                            break;
                        default:
                            callbackContext.error(ex.getMessage());
                            break;
                    }
                } catch (JsonProcessingException e) {
                    Log.e(TAG, "JSON_PARSING_ERROR");
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        this.cordova = cordova;
        super.initialize(cordova, webView);
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        super.onRequestPermissionResult(requestCode, permissions, grantResults);
    }

    private void initSmartSdk(){
        Log.d(TAG, "Starting SmartSdk init...");
        try {
            AXTSdkInit sdkInit = new AXTSdkInit("licence");
            sdkInit.setUseImeiForActivation(false);
            AXTCaptureInterface.INSTANCE.initCaptureSdk(this.cordova.getActivity(), sdkInit, this);
        } catch (CaptureApiException e) {
            callbackContext.error("INIT_FAILED : " + e.getMessage());
        } catch (UnsatisfiedLinkError u){
            callbackContext.error("ARCH_NOT_SUPPORTED");
        }
    }

    private void capture(Map<String, Object> args){
        Log.i(TAG, "Start IDCHECK.IO SDK capture with arguments : " + args);
        if(this.isActivated){
            try{
                final Intent smartSdk = AXTCaptureInterface.INSTANCE.getIntentCapture(this.cordova.getActivity(), this.getSdkParams(args));
                this.cordova.startActivityForResult(this, smartSdk, SDK_REQUEST_CODE);
            } catch (CaptureApiException e) {
                callbackContext.error("START_FAILED");
                Log.e(TAG, e.getMessage());
            }
        } else {
            callbackContext.error("SDK_NOT_INITIALIZED");
        }
    }

    @Override
    public boolean execute(String action, String rawArgs, CallbackContext callbackContext) throws JSONException {
      Log.d(TAG, "Receive action from Cordova => " + action);
      action = action.toUpperCase();
        try{
            this.callbackContext = callbackContext;
            JSONArray array = new JSONArray(rawArgs);
            switch(Action.valueOf(action)){
                case INIT:
                    this.initSmartSdk();
                    break;
                case CAPTURE:
                    Map<String,Object> result = new ObjectMapper().readValue(array.getString(0), HashMap.class);
                    this.capture(result);
                    break;
                default:
                    break;
            }
        } catch (IllegalArgumentException e){
            callbackContext.error("INVALID_ACTION");
        } catch (IOException ioe) {
            callbackContext.error("JSON_PARSING_ERROR");
        }
        return true;
    }

    private AXTSdkParams getSdkParams(Map<String, Object> map){
        AXTSdkParams params = new AXTSdkParams();
        if(map.containsKey(EXTRACT_DATA)){
            params.addParameters(AXTSdkParameters.EXTRACT_DATA, Boolean.parseBoolean((String) map.get(EXTRACT_DATA)));
        }
        if(map.containsKey(USE_HD)) {
            params.addParameters(AXTSdkParameters.USE_HD, Boolean.parseBoolean((String) map.get(USE_HD)));
        }
        if(map.containsKey(USE_FRONT_CAMERA)){
            params.addParameters(AXTSdkParameters.USE_FRONT_CAMERA, Boolean.parseBoolean((String) map.get(USE_FRONT_CAMERA)));
        }
        if(map.containsKey(READ_RFID)){
            params.addParameters(AXTSdkParameters.READ_RFID, Boolean.parseBoolean((String) map.get(READ_RFID)));
        }
        if(map.containsKey(DISPLAY_RESULT)){
            params.addParameters(AXTSdkParameters.DISPLAY_CAPTURE, Boolean.parseBoolean((String) map.get(DISPLAY_RESULT)));
        }
        if(map.containsKey(SCAN_BOTH_SIDE)){
            params.addParameters(AXTSdkParameters.SCAN_RECTO_VERSO, Boolean.parseBoolean((String) map.get(SCAN_BOTH_SIDE)));
        }
        if(map.containsKey(DOC_TYPE)){
            params.setDocType(AXTDocumentType.valueOf((String) map.get(DOC_TYPE)));
        }
        if(map.containsKey(DATA_EXTRACTION_REQUIREMENT)){
            params.addParameters(AXTSdkParameters.DATA_EXTRACTION_REQUIREMENT, AXTDataExtractionRequirement.valueOf((String) map.get(DATA_EXTRACTION_REQUIREMENT)));
        }
        if(map.containsKey(EXTRA_PARAMETERS)) {
            for(Map.Entry<String, Object> extraparam : ((Map<String, Object>)map.get(EXTRA_PARAMETERS)).entrySet()) {
                if (EnumUtils.isValidEnum(EnumExtraParameter.class, extraparam.getKey())) {
                    params.addExtraParameters(EnumExtraParameter.valueOf(extraparam.getKey()), extraparam.getValue());
                }
            }
        }
        return params;
    }
    
    public boolean isActivated() {
        return isActivated;
    }

    @Override
    public void onInitSuccess() {
        Log.i(TAG, "IDCHECK.IO SDK initialization : success");
        this.isActivated = true;
        this.callbackContext.success("INIT_SUCCESS");
    }

    @Override
    public void onInitError() {
        Log.i(TAG, "IDCHECK.IO SDK initialization : error");
        this.isActivated = false;
        this.callbackContext.error("INIT_FAILED");
    }
}
