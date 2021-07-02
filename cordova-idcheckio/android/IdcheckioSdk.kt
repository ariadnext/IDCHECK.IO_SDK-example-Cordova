package com.ariadnext.idcheckio

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.ariadnext.idcheckio.sdk.bean.OnlineContext
import com.ariadnext.idcheckio.sdk.bean.SdkEnvironment
import com.ariadnext.idcheckio.sdk.component.Idcheckio
import com.ariadnext.idcheckio.sdk.component.IdcheckioView
import com.ariadnext.idcheckio.sdk.interfaces.*
import com.ariadnext.idcheckio.sdk.interfaces.result.IdcheckioResult
import com.ariadnext.idcheckio.sdk.utils.toJson
import org.apache.cordova.CallbackContext
import org.apache.cordova.CordovaInterface
import org.apache.cordova.CordovaPlugin
import org.apache.cordova.CordovaWebView
import org.json.JSONArray
import org.json.JSONException
import java.io.FileNotFoundException

class IdcheckioSdk : CordovaPlugin(), IdcheckioCallback, IdcheckioInteractionInterface {
    private var cordovaInterface: CordovaInterface? = null
    private var callbackContext: CallbackContext? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent) {
        when (requestCode) {
            START_REQUEST, START_ONLINE_REQUEST -> if (resultCode == Activity.RESULT_OK) {
                val result = intent.extras!!.getString("IDCHECKIO_RESULT", "{}")
                callbackContext!!.success(result)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                val error = intent.extras!!.getString("ERROR_MSG", "{}")
                callbackContext!!.error(error)
            }
        }
    }

    override fun initialize(cordova: CordovaInterface, webView: CordovaWebView) {
        super.initialize(cordova, webView)
        cordovaInterface = cordova
    }

    private fun analyze(params: String, side1ToUpload: String?, side2ToUpload: String?, isOnline: Boolean, onlineContextJson : String?) {
        try {
            val onlineContext = OnlineContext.createFrom(onlineContextJson?.takeIf { it.isNotEmpty() && it != "null" } ?: "{}")
            val builder = IdcheckioView.Builder()
            ParameterUtils.parseParameters(builder, params)
            val side1Uri: Uri = if (side1ToUpload != null && side1ToUpload.isNotEmpty()) {
                Uri.parse(side1ToUpload)
            } else {
                callbackContext!!.error("SIDE_1_PARSING_ERROR")
                return
            }
            var side2Uri: Uri? = null
            if (side2ToUpload != null && side2ToUpload.isNotEmpty()) {
                side2Uri = Uri.parse(side2ToUpload)
            }
            //Start in a custom thread
            Idcheckio.analyze(cordovaInterface!!.activity, this, builder.captureParams(), side1Uri, side2Uri, isOnline, onlineContext)
        } catch (ex: JSONException) {
            callbackContext!!.error(ErrorMsg(IdcheckioError.INCOMPATIBLE_PARAMETERS, null, ex.message).toJson())
        } catch (ex: FileNotFoundException) {
            callbackContext!!.error(ErrorMsg(IdcheckioError.UNSUPPORTED_FILE_EXTENSION, null, ex.message).toJson())
        }
    }

    override fun execute(action: String, rawArgs: String, callbackContext: CallbackContext): Boolean {
        try {
            this.callbackContext = callbackContext
            val array = JSONArray(rawArgs)
            when (action) {
                PRELOAD -> {
                    Idcheckio.preload(cordovaInterface!!.activity, array.getBoolean(0))
                    this.callbackContext!!.success()
                }
                ACTIVATE -> {
                    val environment = array.getString(3)
                    Idcheckio.activate(array.getString(0), this, cordovaInterface!!.activity, array.getBoolean(1), array.getBoolean(2),
                        SdkEnvironment.valueOf(environment))
                }
                ANALYZE -> analyze(array[0].toString(), array.getString(1), array.getString(2), array.getBoolean(3), array[4].toString())
                START -> {
                    val intent = Intent(cordovaInterface!!.activity, IdcheckioActivity::class.java)
                    intent.putExtra("PARAMS", array[0].toString())
                    intent.putExtra("ACTION", START)
                    cordovaInterface!!.startActivityForResult(this, intent, START_REQUEST)
                }
                START_ONLINE -> {
                    val intentOnline = Intent(cordovaInterface!!.activity, IdcheckioActivity::class.java)
                    intentOnline.putExtra("PARAMS", array[0].toString())
                    intentOnline.putExtra("ONLINE", array[1].toString())
                    intentOnline.putExtra("ACTION", START_ONLINE)
                    cordovaInterface?.startActivityForResult(this, intentOnline, START_ONLINE_REQUEST)
                }
                else -> return false
            }
        } catch (ex: JSONException) {
            this.callbackContext!!.error(ex.message)
            return true
        }
        return true
    }

    override fun onInitEnd(success: Boolean, error: ErrorMsg?) {
        if (success) {
            callbackContext?.success()
        } else {
            callbackContext?.error(error?.toJson())
        }
    }

    override fun onIdcheckioInteraction(interaction: IdcheckioInteraction, data: Any?) {
        when (interaction) {
            IdcheckioInteraction.RESULT -> callbackContext?.success((data as IdcheckioResult).toJson())
            IdcheckioInteraction.ERROR -> callbackContext?.error((data as ErrorMsg).toJson())
            else -> { }
        }
    }

    companion object {
        private const val PRELOAD = "preload"
        private const val ACTIVATE = "activate"
        private const val START = "start"
        private const val ANALYZE = "analyze"
        private const val START_ONLINE = "startOnline"
        private const val START_REQUEST = 5
        private const val START_ONLINE_REQUEST = 6
    }
}