import 'dart:ui';

import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'widget_recorder_method_channel.dart';

abstract class WidgetRecorderPlatform extends PlatformInterface {
  /// Constructs a WidgetRecorderPlatform.
  WidgetRecorderPlatform() : super(token: _token);

  static final Object _token = Object();

  static WidgetRecorderPlatform _instance = MethodChannelWidgetRecorder();

  /// The default instance of [WidgetRecorderPlatform] to use.
  ///
  /// Defaults to [MethodChannelWidgetRecorder].
  static WidgetRecorderPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [WidgetRecorderPlatform] when
  /// they register themselves.
  static set instance(WidgetRecorderPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<String?> startRecord(int videoWidth, int videoHeight, int frameRate);

  Future<bool> stopRecord();
}
