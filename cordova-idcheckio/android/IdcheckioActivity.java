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
import com.ariadnext.idcheckio.sdk.component.IdcheckioView;
import com.ariadnext.idcheckio.sdk.interfaces.ErrorMsg;
import com.ariadnext.idcheckio.sdk.interfaces.IdcheckioError;
import com.ariadnext.idcheckio.sdk.interfaces.IdcheckioInteraction;
import com.ariadnext.idcheckio.sdk.interfaces.IdcheckioInteractionInterface;
import com.ariadnext.idcheckio.sdk.interfaces.cis.CISType;
import com.ariadnext.idcheckio.sdk.interfaces.result.IdcheckioResult;
import com.ariadnext.idcheckio.sdk.utils.ExtensionUtilsKt;

import org.json.JSONException;
import org.json.JSONObject;

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
        try{
            IdcheckioView.Builder idcheckioView = new IdcheckioView.Builder()
                    .listener(this);
            Intent intent = getIntent();
            String params = intent.getStringExtra("PARAMS");
            String cisContext = intent.getStringExtra("CIS");
            String action = intent.getStringExtra("ACTION");
            ParameterUtils.parseParameters(idcheckioView, params);
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
                    idcheckio.startOnline(new CISContext(cis.optString("folderUid"),
                            cis.optString("referenceTaskUid"),
                            cis.optString("referenceDocUid"),
                            cisType
                    ));
                    break;
                default:
                    break;
            }
        } catch (JSONException ex) {
            Intent exIntent = new Intent();
            exIntent.putExtra("ERROR_MSG", ExtensionUtilsKt.toJson(new ErrorMsg(IdcheckioError.INCOMPATIBLE_PARAMETERS, null, ex.getMessage())));
            this.setResult(RESULT_CANCELED, exIntent);
            this.finish();
        } catch (IllegalArgumentException ex) {
            Log.e("IdcheckioActivity", "Failed to parse parameters", ex);
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
                Intent errorIntent = new Intent();
                if (data != null) {
                    errorIntent.putExtra("ERROR_MSG", ExtensionUtilsKt.toJson((ErrorMsg) data));
                }
                this.setResult(RESULT_CANCELED, errorIntent);
                this.finish();
                break;
            default:
                break;
        }
    }
}
