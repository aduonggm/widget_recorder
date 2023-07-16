package com.widget.recorder.duongnv.widget_recorder

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.Lifecycle
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
    private val imagesStack = arrayListOf<Bitmap>()
    private val removeImage = arrayListOf<Bitmap>()

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context = flutterPluginBinding.applicationContext
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "widget_recorder")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: Result) {

        when (call.method) {
            "getPlatformVersion" -> result.success("Android ${android.os.Build.VERSION.RELEASE}")
            "start_record" -> startRecord(result, call)
            "stop_record" -> stopRecord(result)
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
                when (event) {
                    Lifecycle.Event.ON_PAUSE -> viewRecorder.onPause()
                    Lifecycle.Event.ON_RESUME -> viewRecorder.onResume()
                    Lifecycle.Event.ON_DESTROY -> viewRecorder.onDestroy()
                    else -> {}
                }
                Log.e("Activity state: ", event.toString())
            })
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onDetachedFromActivity() {}
    override fun onDetachedFromActivityForConfigChanges() {}
    override fun onReattachedToActivityForConfigChanges(p0: ActivityPluginBinding) {}

    private fun startRecord(result: Result, call: MethodCall) {
        imagesStack.forEach {
            it.recycle()
        }
        imagesStack.clear()

        takeCapture()
        val width = call.argument<Int>("width") as Int
        val height = call.argument<Int>("height") as Int
        val frameRate = call.argument<Int>("frame_rate") as Int
        val enableRecordSoundFromMic = call.argument<Boolean>("enable_sound") ?: false
        Handler(Looper.getMainLooper()).postDelayed({
            val filePath =
                viewRecorder.startRecord(width, height, frameRate, enableRecordSoundFromMic)
            result.success(filePath)
        }, 50)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        return false
    }

    override fun getBitmap(): Bitmap? {
        Log.d("duongnv", "success:  capture getbm  ${imagesStack.size}")

        takeCapture()
        val image = if (imagesStack.isEmpty()) null
        else if (imagesStack.size == 1) imagesStack.first()
        else {
            val first = imagesStack.first()
            imagesStack.remove(first)
            first
        }
        println("run to get bitma  ${imagesStack.size}")
        image?.let {
            removeImage.add(image)
            if (removeImage.size > 2 && imagesStack.size > 1) {
                val bm = removeImage.first()
                removeImage.remove(bm)
                println("run to remove bm ${removeImage.size}")
                bm.recycle()
            }
        }
        return image
    }

    private fun stopRecord(result: Result) {
        viewRecorder.stopRecord()
        removeImage.forEach {
            it.recycle()
        }
        removeImage.clear()
        result.success(true)
    }

    private fun takeCapture() {
        val timeStart = System.currentTimeMillis()
        channel.invokeMethod("capture", null, object : Result {
            override fun success(result: Any?) {
                val imageData = result as ByteArray?
                imageData?.let {
                    val bmp = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                    Log.d("duongnv", "ç")
                    imagesStack.add(bmp)
                }

                println("on success  get bitmap  ${System.currentTimeMillis() - timeStart}")
            }

            override fun error(errorCode: String, errorMessage: String?, errorDetails: Any?) {
                println("on success  get bitmap  error   $errorCode   $errorMessage  $errorDetails")

            }

            override fun notImplemented() {
            }

        })
    }

}
