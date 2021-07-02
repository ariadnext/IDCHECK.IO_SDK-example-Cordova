package com.ariadnext.idcheckio

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import com.ariadnext.idcheckio.sdk.bean.OnlineContext
import com.ariadnext.idcheckio.sdk.component.IdcheckioView
import com.ariadnext.idcheckio.sdk.interfaces.ErrorMsg
import com.ariadnext.idcheckio.sdk.interfaces.IdcheckioError
import com.ariadnext.idcheckio.sdk.interfaces.IdcheckioInteraction
import com.ariadnext.idcheckio.sdk.interfaces.IdcheckioInteractionInterface
import com.ariadnext.idcheckio.sdk.interfaces.result.IdcheckioResult
import com.ariadnext.idcheckio.sdk.utils.toJson
import org.json.JSONException

class IdcheckioActivity : FragmentActivity(), IdcheckioInteractionInterface {
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        this.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val rootLayout = FrameLayout(this)
        rootLayout.id = CONTAINER_ID
        setContentView(rootLayout)
        try {
            val idcheckioView = IdcheckioView.Builder()
                .listener(this)
            val intent = intent
            val params = intent!!.getStringExtra("PARAMS") ?: "{}"
            val onlineContext = if(intent.hasExtra("ONLINE")) {
                OnlineContext.createFrom(intent.getStringExtra("ONLINE").takeIf { it.isNotEmpty() && it != "null" } ?: "{}")
            } else {
                null
            }
            val action = intent.getStringExtra("ACTION")
            ParameterUtils.parseParameters(idcheckioView, params)
            val idcheckio: IdcheckioView = idcheckioView.build()
            supportFragmentManager.beginTransaction().replace(CONTAINER_ID, idcheckio).commit()
            when (action) {
                "start" -> idcheckio.start()
                "startOnline" -> idcheckio.startOnline(onlineContext)
                else -> { /* Should not happen */ }
            }
        } catch (ex: JSONException) {
            val exIntent = Intent()
            exIntent.putExtra("ERROR_MSG", ErrorMsg(IdcheckioError.INCOMPATIBLE_PARAMETERS, null, ex.message).toJson())
            this.setResult(RESULT_CANCELED, exIntent)
            finish()
        } catch (ex: IllegalArgumentException) {
            Log.e("IdcheckioActivity", "Failed to parse parameters", ex)
        }
    }

    override fun onIdcheckioInteraction(interaction: IdcheckioInteraction, data: Any?) {
        when (interaction) {
            IdcheckioInteraction.RESULT -> {
                val resultIntent = Intent()
                resultIntent.putExtra("IDCHECKIO_RESULT", (data as IdcheckioResult).toJson())
                this.setResult(RESULT_OK, resultIntent)
                finish()
            }
            IdcheckioInteraction.ERROR -> {
                val errorIntent = Intent()
                if (data != null) {
                    errorIntent.putExtra("ERROR_MSG", (data as ErrorMsg).toJson())
                }
                this.setResult(RESULT_CANCELED, errorIntent)
                finish()
            }
            else -> {
            }
        }
    }

    companion object {
        private const val CONTAINER_ID = 1234801625
    }
}