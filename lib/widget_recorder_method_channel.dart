import 'dart:ui' as ui;

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'widget_recorder_platform_interface.dart';

/// An implementation of [WidgetRecorderPlatform] that uses method channels.
class MethodChannelWidgetRecorder extends WidgetRecorderPlatform {
  /// The method channel used to interact with the native platform.

  static late final ui.Image? Function() getImage;
   void set( ui.Image? Function() getImages){
     getImage = getImages;
  }
  @visibleForTesting
  final methodChannel = const MethodChannel('widget_recorder')
    ..setMethodCallHandler((call) async {
      print("run to method handler ");
      switch (call.method) {
        case "capture":
          final image = getImage();

          ByteData? byteData = await image?.toByteData(format: ui.ImageByteFormat.png);
          Uint8List? uint8List = byteData?.buffer.asUint8List();



          return uint8List;

        default:
          return " not implement ";
      }
    });

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<bool> startRecord(int videoWidth, int videoHeight) async {
    try {
      final result = await methodChannel.invokeMethod("start_record", {
        "width": videoWidth,
        "height": videoHeight,
      });
      if (result is bool) return result;
      throw Exception("please implement function in Android");
    } catch (e) {
      rethrow;
    }
  }


}
