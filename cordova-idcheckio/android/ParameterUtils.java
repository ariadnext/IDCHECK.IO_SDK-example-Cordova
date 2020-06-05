package com.ariadnext.idcheckio;

import com.ariadnext.idcheckio.sdk.bean.ConfirmationType;
import com.ariadnext.idcheckio.sdk.bean.DataRequirement;
import com.ariadnext.idcheckio.sdk.bean.DocumentType;
import com.ariadnext.idcheckio.sdk.bean.EnumExtraParameters;
import com.ariadnext.idcheckio.sdk.bean.Extraction;
import com.ariadnext.idcheckio.sdk.bean.FaceDetection;
import com.ariadnext.idcheckio.sdk.bean.FeedbackLevel;
import com.ariadnext.idcheckio.sdk.bean.FileSize;
import com.ariadnext.idcheckio.sdk.bean.Forceable;
import com.ariadnext.idcheckio.sdk.bean.Language;
import com.ariadnext.idcheckio.sdk.bean.Orientation;
import com.ariadnext.idcheckio.sdk.component.IdcheckioView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class ParameterUtils {

    public static void parseParameters(IdcheckioView.Builder idcheckioView, String params) throws JSONException{
        JSONObject jsonParams = new JSONObject(params);
        Iterator<String> keys = jsonParams.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = jsonParams.getString(key);
            switch (key) {
                case "DocumentType":
                    idcheckioView.docType(DocumentType.valueOf(value));
                    break;
                case "Orientation":
                    idcheckioView.orientation(Orientation.valueOf(value));
                    break;
                case "ConfirmType":
                    idcheckioView.confirmType(ConfirmationType.valueOf(value));
                    break;
                case "UseHd":
                    idcheckioView.useHd(Boolean.parseBoolean(value));
                    break;
                case "ScanBothSides":
                    idcheckioView.scanBothSides(Forceable.valueOf(value));
                    break;
                case "Side1Extraction":
                    JSONObject side1 = new JSONObject(value);
                    idcheckioView.sideOneExtraction(new Extraction(DataRequirement.valueOf(side1.getString("DataRequirement")),
                            FaceDetection.valueOf(side1.getString("FaceDetection"))));
                    break;
                case "Side2Extraction":
                    JSONObject side2 = new JSONObject(value);
                    idcheckioView.sideTwoExtraction(new Extraction(DataRequirement.valueOf(side2.getString("DataRequirement")),
                            FaceDetection.valueOf(side2.getString("FaceDetection"))));
                    break;
                case "ExtraParams":
                    JSONObject extraParams = new JSONObject(value);
                    Iterator<String> extraKeys = extraParams.keys();
                    while (extraKeys.hasNext()) {
                        String extraKey = extraKeys.next();
                        switch (extraKey) {
                            case "AdjustCrop":
                                idcheckioView.adjustCrop(Boolean.parseBoolean(extraParams.getString(extraKey)));
                                break;
                            case "Language":
                                idcheckioView.language(Language.valueOf(extraParams.getString(extraKey)));
                                break;
                            case "ManualButtonTimer":
                                idcheckioView.manualButtonTimer(Integer.parseInt(extraParams.getString(extraKey)));
                                break;
                            case "MaxPictureFilesize":
                                idcheckioView.maxPictureFilesize(FileSize.valueOf(extraParams.getString(extraKey)));
                                break;
                            case "FeedbackLevel":
                                idcheckioView.feedbackLevel(FeedbackLevel.valueOf(extraParams.getString(extraKey)));
                                break;
                            case "Token":
                                idcheckioView.token(extraParams.getString(extraKey));
                                break;
                            case "ConfirmAbort":
                                idcheckioView.confirmAbort(Boolean.parseBoolean(extraParams.getString(extraKey)));
                                break;
                            case "SdkEnvironment":
                                idcheckioView.extraParameter(EnumExtraParameters.SDK_ENVIRONMENT, extraParams.getString(extraKey));
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
    }
}
