package com.widget.recorder.duongnv.widget_recorder

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
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
    PluginRegistry.ActivityResultListener {
    private lateinit var context: Context
    private lateinit var activity: Activity
    private lateinit var channel: MethodChannel
    private lateinit var viewRecorder: ViewRecorderDemoActivity
    private val imagesStack = arrayListOf<Bitmap>()

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
            "send_frame" -> frameProcess(call, result)
            else -> throw Exception("not implement function   ${call.method}")
        }
    }

    private fun frameProcess(call: MethodCall, result: Result) {
        val imageData = call.arguments as ByteArray
        imageData.let {
            val bmp = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
            imagesStack.add(bmp)
            viewRecorder.drawFrame(bmp)
        }
        result.success(true)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        viewRecorder = ViewRecorderDemoActivity(activity)
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
        imagesStack.forEach { it.recycle() }
        imagesStack.clear()

        val width = call.argument<Int>("width") as Int
        val height = call.argument<Int>("height") as Int
        val enableRecordSoundFromMic = call.argument<Boolean>("enable_sound") ?: false

        val filePath = viewRecorder.startRecord(width, height, enableRecordSoundFromMic)
        result.success(filePath)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        return false
    }

    private fun stopRecord(result: Result) {
        viewRecorder.stopRecord()
        result.success(true)
    }

}
