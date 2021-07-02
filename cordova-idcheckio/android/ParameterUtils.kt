package com.ariadnext.idcheckio

import com.ariadnext.idcheckio.sdk.bean.*
import com.ariadnext.idcheckio.sdk.component.IdcheckioView
import com.ariadnext.idcheckio.sdk.interfaces.cis.CISType
import org.json.JSONException
import org.json.JSONObject

object ParameterUtils {
    @Throws(JSONException::class)
    fun parseParameters(idcheckioView: IdcheckioView.Builder, params: String) {
        val jsonParams = JSONObject(params)
        val keys = jsonParams.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = jsonParams.getString(key)
            when (key) {
                "docType" -> idcheckioView.docType(DocumentType.valueOf(value))
                "orientation" -> idcheckioView.orientation(Orientation.valueOf(value))
                "confirmationType" -> idcheckioView.confirmType(ConfirmationType.valueOf(value))
                "useHd" -> idcheckioView.useHd(value.toBoolean())
                "integrityCheck" -> {
                    val integrityCheck = JSONObject(value)
                    idcheckioView.integrityCheck(IntegrityCheck(readEmrtd = integrityCheck.getString("readEmrtd").toBoolean()))
                }
                "scanBothSides" -> idcheckioView.scanBothSides(Forceable.valueOf(value))
                "sideOneExtraction" -> {
                    val side1 = JSONObject(value)
                    idcheckioView.sideOneExtraction(Extraction(
                        DataRequirement.valueOf(side1.getString("codeline")), FaceDetection.valueOf(side1.getString("faceDetection")))
                    )
                }
                "sideTwoExtraction" -> {
                    val side2 = JSONObject(value)
                    idcheckioView.sideTwoExtraction(Extraction(
                        DataRequirement.valueOf(side2.getString("codeline")), FaceDetection.valueOf(side2.getString("faceDetection")))
                    )
                }
                "language" -> idcheckioView.language(Language.valueOf(value))
                "manualButtonTimer" -> idcheckioView.manualButtonTimer(value.toInt())
                "feedbackLevel" -> idcheckioView.feedbackLevel(FeedbackLevel.valueOf(value))
                "adjustCrop" -> idcheckioView.adjustCrop(value.toBoolean())
                "maxPictureFilesize" -> idcheckioView.maxPictureFilesize(FileSize.valueOf(value))
                "token" -> idcheckioView.token(value)
                "confirmAbort" -> idcheckioView.confirmAbort(value.toBoolean())
                "onlineConfig" -> {
                    val onlineJson = JSONObject(value)
                    val onlineConfig = OnlineConfig()
                    if(!onlineJson.isNull("isReferenceDocument")){
                        onlineConfig.isReferenceDocument = onlineJson.getString("isReferenceDocument").toBoolean()
                    }
                    if(!onlineJson.isNull("checkType")){
                        onlineConfig.checkType = CheckType.valueOf(onlineJson.getString("checkType"))
                    }
                    if(!onlineJson.isNull("cisType")){
                        onlineConfig.cisType = CISType.valueOf(onlineJson.getString("cisType"))
                    }
                    if(!onlineJson.isNull("folderUid")){
                        onlineConfig.folderUid = onlineJson.getString("folderUid")
                    }
                    if(!onlineJson.isNull("biometricConsent")){
                        onlineConfig.biometricConsent = onlineJson.getString("biometricConsent").toBoolean()
                    }
                    if(!onlineJson.isNull("enableManualAnalysis")){
                        onlineConfig.enableManualAnalysis = onlineJson.getString("enableManualAnalysis").toBoolean()
                    }
                    idcheckioView.onlineConfig(onlineConfig)
                }
                else -> { }
            }
        }
    }
}