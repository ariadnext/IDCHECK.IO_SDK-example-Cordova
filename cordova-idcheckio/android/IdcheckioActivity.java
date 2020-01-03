package com.ariadnext.idcheckio;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.ariadnext.idcheckio.sdk.bean.CISContext;
import com.ariadnext.idcheckio.sdk.bean.ConfirmationType;
import com.ariadnext.idcheckio.sdk.bean.DataRequirement;
import com.ariadnext.idcheckio.sdk.bean.DocumentType;
import com.ariadnext.idcheckio.sdk.bean.Extraction;
import com.ariadnext.idcheckio.sdk.bean.FaceDetection;
import com.ariadnext.idcheckio.sdk.bean.FeedbackLevel;
import com.ariadnext.idcheckio.sdk.bean.FileSize;
import com.ariadnext.idcheckio.sdk.bean.Forceable;
import com.ariadnext.idcheckio.sdk.bean.Language;
import com.ariadnext.idcheckio.sdk.bean.Orientation;
import com.ariadnext.idcheckio.sdk.component.IdcheckioView;
import com.ariadnext.idcheckio.sdk.interfaces.ErrorMsg;
import com.ariadnext.idcheckio.sdk.interfaces.IdcheckioInteraction;
import com.ariadnext.idcheckio.sdk.interfaces.IdcheckioInteractionInterface;
import com.ariadnext.idcheckio.sdk.interfaces.cis.CISType;
import com.ariadnext.idcheckio.sdk.interfaces.result.IdcheckioResult;
import com.ariadnext.idcheckio.sdk.utils.ExtensionUtilsKt;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class IdcheckioActivity extends FragmentActivity implements IdcheckioInteractionInterface {
    private final static int CONTAINER_ID = 1234801625;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        FrameLayout rootLayout = new FrameLayout(this);
        rootLayout.setId(CONTAINER_ID);
        setContentView(rootLayout);
        IdcheckioView.Builder idcheckioView = new IdcheckioView.Builder()
                .listener(this);
        Intent intent = getIntent();
        String params = intent.getStringExtra("PARAMS");
        String licenceFileName = intent.getStringExtra("LICENCE");
        String cisContext = intent.getStringExtra("CIS");
        boolean disableImei = intent.getBooleanExtra("IMEI", true);
        String action = intent.getStringExtra("ACTION");
        try {
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
                                default:
                                    break;
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
            IdcheckioView idcheckio = idcheckioView.build();
            getSupportFragmentManager().beginTransaction().replace(CONTAINER_ID, idcheckio).commit();
            switch (action) {
                case "start":
                    idcheckio.start();
                    break;
                case "startOnline":
                    JSONObject cis = new JSONObject(cisContext);
                    String cisTypeString = cis.optString("cisType");
                    CISType cisType = null;
                    if (!cisTypeString.isEmpty()) {
                        cisType = CISType.valueOf(cisTypeString);
                    }
                    idcheckio.startOnline(licenceFileName,
                            new CISContext(cis.optString("folderUid"),
                                    cis.optString("referenceTaskUid"),
                                    cis.optString("referenceDocUid"),
                                    cisType
                            ), disableImei);
                    break;
                default:
                    break;
            }
        } catch (JSONException ex) {
            Intent exIntent = new Intent();
            exIntent.putExtra("IDCHECKIO_ERROR_TYPE", "WRONG_PARAMETERS");
            exIntent.putExtra("IDCHECKIO_ERROR_MESSAGE", ex.getMessage());
            this.setResult(RESULT_CANCELED, exIntent);
            this.finish();
        } catch (IllegalArgumentException ex) {
            Log.e("IdceckioActivity", "Failed to parse parameters", ex);
        }
    }

    @Override
    public void onIdcheckioInteraction(@NonNull IdcheckioInteraction interaction, @Nullable Object data) {
        switch (interaction) {
            case RESULT:
                Intent resultIntent = new Intent();
                resultIntent.putExtra("IDCHECKIO_RESULT", ExtensionUtilsKt.toJson((IdcheckioResult) data));
                this.setResult(RESULT_OK, resultIntent);
                this.finish();
                break;
            case ERROR:
                ErrorMsg errorMsg = (ErrorMsg) data;
                Intent errorIntent = new Intent();
                if (errorMsg != null) {
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
