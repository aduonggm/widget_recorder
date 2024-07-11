// In order to *not* need this ignore, consider extracting the "web" version
// of your plugin as a separate package, instead of inlining it in the same
// package as the core of your plugin.
// ignore: avoid_web_libraries_in_flutter
import 'dart:html' as html show window;
import 'dart:typed_data';
import 'dart:ui';

import 'package:flutter_web_plugins/flutter_web_plugins.dart';

import 'widget_recorder_platform_interface.dart';

/// A web implementation of the WidgetRecorderPlatform of the WidgetRecorder plugin.
class WidgetRecorderWeb extends WidgetRecorderPlatform {
  /// Constructs a WidgetRecorderWeb
  WidgetRecorderWeb();

  static void registerWith(Registrar registrar) {
    WidgetRecorderPlatform.instance = WidgetRecorderWeb();
  }

  /// Returns a [String] containing the version of the platform.
  @override
  Future<String?> getPlatformVersion() async {
    final version = html.window.navigator.userAgent;
    return version;
  }

  @override
  Future<String?> startRecord(int videoWidth, int videoHeight, int frameRate) async => null;

  @override
  void setFunctionGetImage(Image? Function() getImage) {
    // TODO: implement setFunctionGetImage
  }

  @override
  Future<bool> stopRecord() {
    // TODO: implement stopRecord
    throw UnimplementedError();
  }

  @override
  Future<bool> sendFrame(Uint8List imageData, int count) {
    // TODO: implement sendFrame
    throw UnimplementedError();
  }
}
