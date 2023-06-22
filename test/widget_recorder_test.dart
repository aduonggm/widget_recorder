import 'package:flutter_test/flutter_test.dart';
import 'package:widget_recorder/widget_recorder.dart';
import 'package:widget_recorder/widget_recorder_platform_interface.dart';
import 'package:widget_recorder/widget_recorder_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockWidgetRecorderPlatform with MockPlatformInterfaceMixin implements WidgetRecorderPlatform {
  @override
  Future<String?> getPlatformVersion() => Future.value('42');

  @override
  Future<bool> startRecord(int videoWidth, int videoHeight) async => false;
}

void main() {
  final WidgetRecorderPlatform initialPlatform = WidgetRecorderPlatform.instance;

  test('$MethodChannelWidgetRecorder is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelWidgetRecorder>());
  });

  test('getPlatformVersion', () async {
    WidgetRecorder widgetRecorderPlugin = WidgetRecorder();
    MockWidgetRecorderPlatform fakePlatform = MockWidgetRecorderPlatform();
    WidgetRecorderPlatform.instance = fakePlatform;

    expect(await widgetRecorderPlugin.getPlatformVersion(), '42');
  });
}
