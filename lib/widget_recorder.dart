import 'dart:ui';

import 'package:widget_recorder/widget_recorder_method_channel.dart';

import 'widget_recorder_platform_interface.dart';

class WidgetRecorder {
  Future<String?> getPlatformVersion() {
    return WidgetRecorderPlatform.instance.getPlatformVersion();
  }

  Future<String?> startRecord(int width, int height, int frameRate) {
    return WidgetRecorderPlatform.instance.startRecord(width, height, frameRate);
  }

  void setImageCapture(Image? Function() getImage) {
    (WidgetRecorderPlatform.instance as MethodChannelWidgetRecorder).set(getImage);
  }

  stopRecord() {
    WidgetRecorderPlatform.instance.stopRecord();
  }
}
