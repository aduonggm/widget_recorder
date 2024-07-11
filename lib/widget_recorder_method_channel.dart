import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'widget_recorder_platform_interface.dart';

/// An implementation of [WidgetRecorderPlatform] that uses method channels.
class MethodChannelWidgetRecorder extends WidgetRecorderPlatform {
  /// The method channel used to interact with the native platform.

  @visibleForTesting
  final methodChannel = const MethodChannel('widget_recorder');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<String?> startRecord(int videoWidth, int videoHeight, int frameRate) async {
    try {
      final result = await methodChannel.invokeMethod("start_record", {
        "width": videoWidth,
        "height": videoHeight,
        "frame_rate": frameRate,
      });
      if (result is String) return result;
      throw Exception("please implement function in Android");
    } catch (e) {
      rethrow;
    }
  }

  @override
  Future<bool> stopRecord() async {
    methodChannel.invokeMethod("stop_record");
    return true;
  }

  @override
  Future<bool> sendFrame(Uint8List imageData, int count) async {
    final result = await methodChannel.invokeMethod("send_frame",imageData);

    return false;
  }
}
