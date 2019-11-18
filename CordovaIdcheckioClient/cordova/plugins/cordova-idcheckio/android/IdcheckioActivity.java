package com.ariadnext.idcheckio;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ariadnext.idcheckio.sdk.bean.CISContext;
import com.ariadnext.idcheckio.sdk.bean.ConfirmationType;
import com.ariadnext.idcheckio.sdk.bean.DataRequirement;
import com.ariadnext.idcheckio.sdk.bean.DocumentType;
import com.ariadnext.idcheckio.sdk.bean.EnumExtraParameters;
import com.ariadnext.idcheckio.sdk.bean.EnumParameters;
import com.ariadnext.idcheckio.sdk.bean.Extraction;
import com.ariadnext.idcheckio.sdk.bean.FaceDetection;
import com.ariadnext.idcheckio.sdk.bean.Forceable;
import com.ariadnext.idcheckio.sdk.bean.Orientation;
import com.ariadnext.idcheckio.sdk.component.IdcheckioView;
import com.ariadnext.idcheckio.sdk.interfaces.ErrorMsg;
import com.ariadnext.idcheckio.sdk.interfaces.IdcheckioInteraction;
import com.ariadnext.idcheckio.sdk.interfaces.IdcheckioInteractionInterface;
import com.ariadnext.idcheckio.sdk.interfaces.result.IdcheckioResult;
import com.ariadnext.idcheckio.sdk.utils.ExtensionUtilsKt;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class IdcheckioActivity extends Activity implements IdcheckioInteractionInterface {

    @Override
    protected void onCreate(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        RelativeLayout rootLayout = new RelativeLayout(this);
        setContentView(rootLayout);
        IdcheckioView idcheckioView = new IdcheckioView(this);
        rootLayout.addView(idcheckioView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        idcheckioView.setInteractionListener(this);
        Intent intent = getIntent();
        String params = intent.getStringExtra("PARAMS");
        String licenceFileName = intent.getStringExtra("LICENCE");
        String cisContext = intent.getStringExtra("CIS");
        boolean disableImei = intent.getBooleanExtra("IMEI", true);
        String action = intent.getStringExtra("ACTION");
        try {
            JSONObject jsonParams = new JSONObject(params);
            Iterator<String> keys = jsonParams.keys();
            while(keys.hasNext()) {
                String key = keys.next();
                String value = jsonParams.getString(key);
                switch (key) {
                    case "DocumentType" :
                        idcheckioView.setDocumentType(DocumentType.valueOf(value));
                        break;
                    case "Orientation" :
                        if(Orientation.LANDSCAPE.toString().equals(value)){
                            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        } else {
                            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        }
                        idcheckioView.addParameters(EnumParameters.ORIENTATION, Orientation.valueOf(value));
                        break;
                    case "ConfirmType" :
                        idcheckioView.addParameters(EnumParameters.CONFIRM_TYPE, ConfirmationType.valueOf(value));
                        break;
                    case "UseHd" :
                        idcheckioView.addParameters(EnumParameters.USE_HD, Boolean.parseBoolean(value));
                        break;
                    case "ScanBothSides" :
                        idcheckioView.addParameters(EnumParameters.SCAN_BOTH_SIDES, Forceable.valueOf(value));
                        break;
                    case "Side1Extraction" :
                        JSONObject side1 = new JSONObject(value);
                        idcheckioView.addParameters(EnumParameters.SIDE_1_EXTRACTION,
                                new Extraction(DataRequirement.valueOf(side1.getString("DataRequirement")),
                                        FaceDetection.valueOf(side1.getString("FaceDetection"))));
                        break;
                    case "Side2Extraction" :
                        JSONObject side2 = new JSONObject(value);
                        idcheckioView.addParameters(EnumParameters.SIDE_2_EXTRACTION,
                                new Extraction(DataRequirement.valueOf(side2.getString("DataRequirement")),
                                        FaceDetection.valueOf(side2.getString("FaceDetection"))));
                        break;
                    case "ExtraParams" :
                        JSONObject extraParams = new JSONObject(value);
                        Iterator<String> extraKeys = extraParams.keys();
                        while(extraKeys.hasNext()) {
                            String extraKey = extraKeys.next();
                            switch (extraKey){
                                case "AdjustCrop" :
                                    idcheckioView.addExtraParameters(EnumExtraParameters.ADJUST_CROP, extraParams.getString(extraKey));
                                    break;
                                case "Language" :
                                    idcheckioView.addExtraParameters(EnumExtraParameters.LANGUAGE, extraParams.getString(extraKey));
                                    break;
                                case "ManualButtonTimer" :
                                    idcheckioView.addExtraParameters(EnumExtraParameters.MANUAL_BUTTON_TIMER, extraParams.getString(extraKey));
                                    break;
                                case "MaxPictureFilesize" :
                                    idcheckioView.addExtraParameters(EnumExtraParameters.MAX_PICTURE_FILESIZE, extraParams.getString(extraKey));
                                    break;
                                case "FeedbackLevel" :
                                    idcheckioView.addExtraParameters(EnumExtraParameters.FEEDBACK_LEVEL, extraParams.getString(extraKey));
                                    break;
                                case "Token" :
                                    idcheckioView.addExtraParameters(EnumExtraParameters.TOKEN, extraParams.getString(extraKey));
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
            switch(action){
                case "start" :
                    idcheckioView.start();
                    break;
                case "startOnline" :
                    JSONObject cis = new JSONObject(cisContext);
                    idcheckioView.startOnline(licenceFileName, this,
                            new CISContext(cis.optString("folderUid"),
                                    cis.optString("referenceTaskUid"),
                                    cis.optString("referenceDocUid"),
                                    cis.optString("cisType")
                            ), disableImei);
                    break;
                default:
                    break;
            }
        } catch (JSONException ex){
            Intent exIntent = new Intent();
            exIntent.putExtra("IDCHECKIO_ERROR_TYPE", "WRONG_PARAMETERS");
            exIntent.putExtra("IDCHECKIO_ERROR_MESSAGE", ex.getMessage());
            this.setResult(RESULT_CANCELED, exIntent);
            this.finish();
        }
    }

    @Override
    public void onIdcheckioInteraction(@NonNull IdcheckioInteraction interaction, @Nullable Object data) {
        switch (interaction){
            case RESULT:
                Intent resultIntent = new Intent();
                resultIntent.putExtra("IDCHECKIO_RESULT", ExtensionUtilsKt.toJson((IdcheckioResult) data));
                this.setResult(RESULT_OK, resultIntent);
                this.finish();
                break;
            case ERROR:
                ErrorMsg errorMsg = (ErrorMsg) data;
                Intent errorIntent = new Intent();
                if(errorMsg != null) {
                    errorIntent.putExtra("IDCHECKIO_ERROR_TYPE", errorMsg.getType());
                    errorIntent.putExtra("IDCHECKIO_ERROR_CODE", errorMsg.getCode());
                    errorIntent.putExtra("IDCHECKIO_ERROR_MESSAGE", errorMsg.getMessage());
                }
                this.setResult(RESULT_CANCELED, errorIntent);
                this.finish();
                break;
            default:
                break;
        }
    }
}
