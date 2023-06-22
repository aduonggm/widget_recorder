package com.widget.recorder.duongnv.widget_recorder

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LifecycleEventObserver
import com.widget.recorder.duongnv.widget_recorder.recorder.ViewRecorder
import com.widget.recorder.duongnv.widget_recorder.recorder.ViewRecorderDemoActivity
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.embedding.engine.plugins.lifecycle.HiddenLifecycleReference
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry


/** WidgetRecorderPlugin */
class WidgetRecorderPlugin : FlutterPlugin, MethodCallHandler, ActivityAware,
    PluginRegistry.ActivityResultListener, ViewRecorder.IBitmap {
    private lateinit var context: Context
    private lateinit var activity: Activity
    private lateinit var channel: MethodChannel
    private lateinit var viewRecorder: ViewRecorderDemoActivity

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context = flutterPluginBinding.applicationContext
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "widget_recorder")
        channel.setMethodCallHandler(this)


    }

    override fun onMethodCall(call: MethodCall, result: Result) {

        when (call.method) {
            "getPlatformVersion" -> result.success("Android ${android.os.Build.VERSION.RELEASE}")
            "start_record" -> startRecord(result, call)
            else -> throw Exception("not implement function   ${call.method}")
        }
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        viewRecorder = ViewRecorderDemoActivity(activity, this)
        binding.addActivityResultListener(this)
        (binding.lifecycle as HiddenLifecycleReference)
            .lifecycle
            .addObserver(LifecycleEventObserver { _, event ->
                Log.e("Activity state: ", event.toString())
            })

        Handler(Looper.getMainLooper()).postDelayed({
            getBitmap()
        }, 2000)

    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onDetachedFromActivity() {}
    override fun onDetachedFromActivityForConfigChanges() {}
    override fun onReattachedToActivityForConfigChanges(p0: ActivityPluginBinding) {}

    private fun startRecord(result: Result, call: MethodCall) {
        val width = call.argument<Int>("width") as Int
        val height = call.argument<Int>("height") as Int
        println(" size view is   $width   $height")
        result.success(true)
        getBitmap()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        return false
    }

    override fun getBitmap(): Bitmap? {
        println("run to get bitma")
        channel.invokeMethod("capture", null, object : MethodChannel.Result {
            override fun success(result: Any?) {

                val imageData = result as ByteArray
                val bmp = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                println("on success  get bitmap  $result")
            }

            override fun error(errorCode: String, errorMessage: String?, errorDetails: Any?) {
                println("on success  get bitmap  error")

            }

            override fun notImplemented() {
                TODO("Not yet implemented")
            }

        })
        return null
    }

}
