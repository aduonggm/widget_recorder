import 'dart:typed_data';

import 'widget_recorder_platform_interface.dart';

class WidgetRecorder {
  Future<String?> getPlatformVersion() {
    return WidgetRecorderPlatform.instance.getPlatformVersion();
  }

  Future<String?> startRecord(int width, int height, int frameRate) {
    return WidgetRecorderPlatform.instance.startRecord(width, height, frameRate);
  }

  void stopRecord() {
    WidgetRecorderPlatform.instance.stopRecord();
  }

  void sendFrame(Uint8List imageData, int count) {
    WidgetRecorderPlatform.instance.sendFrame(imageData, count);
  }
}
