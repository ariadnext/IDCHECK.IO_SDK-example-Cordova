package com.ariadnext.idcheckio;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.ariadnext.idcheckio.sdk.component.Idcheckio;
import com.ariadnext.idcheckio.sdk.interfaces.ErrorMsg;
import com.ariadnext.idcheckio.sdk.interfaces.IdcheckioCallback;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

public class IdcheckioSdk extends CordovaPlugin implements IdcheckioCallback {
    private CordovaInterface cordovaInterface;
    private CallbackContext callbackContext;
    private final static String PRELOAD = "preload";
    private final static String ACTIVATE = "activate";
    private final static String START = "start";
    private final static String START_ONLINE = "startOnline";
    private final static int START_REQUEST = 5;
    private final static int START_ONLINE_REQUEST = 6;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode){
            case START_REQUEST:
            case START_ONLINE_REQUEST:
                if(resultCode == Activity.RESULT_OK){
                    String result = intent.getExtras().getString("IDCHECKIO_RESULT", "{}");
                    this.callbackContext.success(result);
                } else if(resultCode == Activity.RESULT_CANCELED){
                    String resultType = "";
                    String resultErrorCode = "";
                    String resultMessage = "";
                    if(intent != null) {
                        if (intent.hasExtra("IDCHECKIO_ERROR_TYPE") && intent.getExtras().get("IDCHECKIO_ERROR_TYPE") != null)
                            resultType = intent.getExtras().get("IDCHECKIO_ERROR_TYPE").toString();
                        if (intent.hasExtra("IDCHECKIO_ERROR_CODE") && intent.getExtras().get("IDCHECKIO_ERROR_CODE") != null)
                            resultErrorCode = intent.getExtras().get("IDCHECKIO_ERROR_CODE").toString();
                        if (intent.hasExtra("IDCHECKIO_ERROR_MESSAGE") && intent.getExtras().get("IDCHECKIO_ERROR_MESSAGE") != null)
                            resultMessage = intent.getExtras().getString("IDCHECKIO_ERROR_MESSAGE", "");
                    }
                    this.callbackContext.error("{\"type\":\"" + resultType + "\"," +
                            "\"code\":\"" + resultErrorCode + "\"," +
                            "\"message\":\"" + resultMessage + "\"}");
                }
                break;
        }
    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        this.cordovaInterface = cordova;
    }

    @Override
    public boolean execute(String action, String rawArgs, CallbackContext callbackContext) {
        try {
            this.callbackContext = callbackContext;
            JSONArray array = new JSONArray(rawArgs);
            switch (action){
                case PRELOAD :
                    Idcheckio.INSTANCE.preload(this.cordovaInterface.getActivity(), array.getBoolean(0));
                    this.callbackContext.success();
                    break;
                case ACTIVATE :
                    Idcheckio.activate(array.getString(0), this, this.cordovaInterface.getActivity(),
                            array.getBoolean(1), array.getBoolean(2));
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
                    intentOnline.putExtra("LICENCE", array.getString(1));
                    intentOnline.putExtra("CIS", array.get(2).toString());
                    intentOnline.putExtra("IMEI", array.getBoolean(3));
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
            this.callbackContext.error(
                    (errorMsg != null && errorMsg.getMessage() != null)?
                            errorMsg.getMessage() : "");
        }
    }
}
