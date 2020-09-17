package com.ariadnext.idcheckio;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.Nullable;

import com.ariadnext.idcheckio.sdk.bean.CISContext;
import com.ariadnext.idcheckio.sdk.bean.SdkEnvironment;
import com.ariadnext.idcheckio.sdk.component.Idcheckio;
import com.ariadnext.idcheckio.sdk.component.IdcheckioView;
import com.ariadnext.idcheckio.sdk.interfaces.ErrorMsg;
import com.ariadnext.idcheckio.sdk.interfaces.IdcheckioCallback;
import com.ariadnext.idcheckio.sdk.interfaces.IdcheckioError;
import com.ariadnext.idcheckio.sdk.interfaces.IdcheckioInteraction;
import com.ariadnext.idcheckio.sdk.interfaces.IdcheckioInteractionInterface;
import com.ariadnext.idcheckio.sdk.interfaces.cis.CISType;
import com.ariadnext.idcheckio.sdk.interfaces.result.IdcheckioResult;
import com.ariadnext.idcheckio.sdk.utils.EnumUtils;
import com.ariadnext.idcheckio.sdk.utils.ExtensionUtilsKt;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class IdcheckioSdk extends CordovaPlugin implements IdcheckioCallback, IdcheckioInteractionInterface {
    private CordovaInterface cordovaInterface;
    private CallbackContext callbackContext;
    private static final String PRELOAD = "preload";
    private static final String ACTIVATE = "activate";
    private static final String START = "start";
    private static final String ANALYZE = "analyze";
    private static final String START_ONLINE = "startOnline";
    private static final int START_REQUEST = 5;
    private static final int START_ONLINE_REQUEST = 6;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode){
            case START_REQUEST:
            case START_ONLINE_REQUEST:
                if(resultCode == Activity.RESULT_OK){
                    String result = intent.getExtras().getString("IDCHECKIO_RESULT", "{}");
                    this.callbackContext.success(result);
                } else if(resultCode == Activity.RESULT_CANCELED){
                    String error = intent.getExtras().getString("ERROR_MSG", "{}");
                    this.callbackContext.error(error);
                }
                break;
        }
    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        this.cordovaInterface = cordova;
    }

    private void analyze(String params, String side1ToUpload, String side2ToUpload, Boolean isOnline, String cisContext){
        try {
            Uri side1Uri;
            Uri side2Uri = null;
            IdcheckioView.Builder builder = new IdcheckioView.Builder();
            ParameterUtils.parseParameters(builder, params);
            if(side1ToUpload != null && !side1ToUpload.isEmpty()){
                side1Uri = Uri.parse(side1ToUpload);
            } else {
                this.callbackContext.error("SIDE_1_PARSING_ERROR");
                return;
            }
            if(side2ToUpload != null && !side2ToUpload.isEmpty()){
                side2Uri = Uri.parse(side2ToUpload);
            }
            JSONObject cis = new JSONObject(cisContext);
            String cisTypeString = cis.optString("cisType");
            CISType cisType = null;
            if (!cisTypeString.isEmpty()) {
                cisType = CISType.valueOf(cisTypeString);
            }
            //Start in a custom thread
            Idcheckio.analyze(this.cordovaInterface.getActivity(),
                    builder.captureParams(),
                    side1Uri,
                    side2Uri,
                    this,
                    isOnline,
                    new CISContext(cis.optString("folderUid"),
                            cis.optString("referenceTaskUid"),
                            cis.optString("referenceDocUid"),
                            cisType,
                            cis.optBoolean("biometricConsent")));
        } catch (JSONException ex){
            this.callbackContext.error(ExtensionUtilsKt.toJson(new ErrorMsg(IdcheckioError.INCOMPATIBLE_PARAMETERS, null, ex.getMessage())));
        }
    }

    @Override
    public boolean execute(String action, String rawArgs, CallbackContext callbackContext) {
        try {
            this.callbackContext = callbackContext;
            JSONArray array = new JSONArray(rawArgs);
            switch (action){
                case PRELOAD :
                    Idcheckio.preload(this.cordovaInterface.getActivity(), array.getBoolean(0));
                    this.callbackContext.success();
                    break;
                case ACTIVATE :
                    String environment = array.getString(4);
                    if(!EnumUtils.isValidEnum(SdkEnvironment.class, environment)){
                        callbackContext.error("Wrong SdkEnvironment value.");
                    }
                    Idcheckio.activate(array.getString(0), this, this.cordovaInterface.getActivity(),
                            array.getBoolean(1), array.getBoolean(2), array.getBoolean(3), SdkEnvironment.valueOf(environment));
                    break;
                case ANALYZE :
                    this.analyze(array.get(0).toString(), array.getString(1), array.getString(2), array.getBoolean(3), array.get(4).toString());
                    break;
                case START :
                    Intent intent = new Intent(this.cordovaInterface.getActivity(), IdcheckioActivity.class);
                    intent.putExtra("PARAMS", array.get(0).toString());
                    intent.putExtra("ACTION", START);
                    this.cordovaInterface.startActivityForResult(this, intent, START_REQUEST);
                    break;
                case START_ONLINE :
                    Intent intentOnline = new Intent(this.cordovaInterface.getActivity(), IdcheckioActivity.class);
                    intentOnline.putExtra("PARAMS", array.get(0).toString());
                    intentOnline.putExtra("CIS", array.get(1).toString());
                    intentOnline.putExtra("ACTION", START_ONLINE);
                    this.cordovaInterface.startActivityForResult(this, intentOnline, START_ONLINE_REQUEST);
                    break;
                default:
                    return false;
            }
        } catch (JSONException ex){
            this.callbackContext.error(ex.getMessage());
            return true;
        }
        return true;
    }

    @Override
    public void onInitEnd(boolean success, @Nullable ErrorMsg errorMsg) {
        if(success){
            this.callbackContext.success();
        } else {
            this.callbackContext.error(ExtensionUtilsKt.toJson(errorMsg));
        }
    }

    @Override
    public void onIdcheckioInteraction(IdcheckioInteraction idcheckioInteraction, Object data) {
        switch (idcheckioInteraction) {
            case RESULT:
                this.callbackContext.success(ExtensionUtilsKt.toJson((IdcheckioResult) data));
                break;
            case ERROR:
                this.callbackContext.error(ExtensionUtilsKt.toJson((ErrorMsg) data));
                break;
            default:
                break;
        }
    }
}
