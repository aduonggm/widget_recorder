import 'dart:ui' as ui show Image;

import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/scheduler.dart';
import 'package:widget_recorder/widget_recorder.dart';

class ScreenRecorderController {
  ScreenRecorderController({
    this.pixelRatio = 1,
    this.frameRate = 30,
    SchedulerBinding? binding,
  })  : _containerKey = GlobalKey(),
        _widgetRecorderPlugin = WidgetRecorder() {
    _widgetRecorderPlugin.setImageCapture(capture);
  }

  final GlobalKey _containerKey;
  final WidgetRecorder _widgetRecorderPlugin;

  /// The pixelRatio describes the scale between the logical pixels and the size
  /// of the output image. Specifying 1.0 will give you a 1:1 mapping between
  /// logical pixels and the output pixels in the image. The default is a pixel
  /// ration of 3 and a value below 1 is not recommended.
  ///
  /// See [RenderRepaintBoundary](https://api.flutter.dev/flutter/rendering/RenderRepaintBoundary/toImage.html)
  /// for the underlying implementation.
  final double pixelRatio;

  final int frameRate;

  int skipped = 0;

  bool _record = false;

  void start() async {
    // only start a video, if no recording is in progress
    if (_record == true) {
      return;
    }
    final context = _containerKey.currentContext;
    if (context != null) {
      final size = MediaQuery.sizeOf(context);
      final success = await _widgetRecorderPlugin.startRecord(size.width.toInt(), size.height.toInt(), frameRate);
      print("start record success  $success");
    }
    _record = true;
  }

  void stop() async {
    final success = await _widgetRecorderPlugin.stopRecord();

    _record = false;
  }

  ui.Image? capture() {
    var startTime = DateTime.now().millisecondsSinceEpoch;
    print("start capture   $startTime }");
    final renderObject = _containerKey.currentContext!.findRenderObject() as RenderRepaintBoundary;
    print("start capture   ${DateTime.now().millisecondsSinceEpoch - startTime}");

    return renderObject.toImageSync(pixelRatio: pixelRatio);
  }
}

class ScreenRecorder extends StatelessWidget {
  ScreenRecorder({
    Key? key,
    required this.child,
    required this.controller,
    required this.width,
    required this.height,
    this.background = Colors.white,
  })  : assert(background.alpha == 255, 'background color is not allowed to be transparent'),
        super(key: key);

  /// The child which should be recorded.
  final Widget child;

  /// This controller starts and stops the recording.
  final ScreenRecorderController controller;

  /// Width of the recording.
  /// This should not change during recording as it could lead to
  /// undefined behavior.
  final double width;

  /// Height of the recording
  /// This should not change during recording as it could lead to
  /// undefined behavior.
  final double height;

  /// The background color of the recording.
  /// Transparency is currently not supported.
  final Color background;

  @override
  Widget build(BuildContext context) {
    return RepaintBoundary(
      key: controller._containerKey,
      child: Container(
        width: width,
        height: height,
        color: background,
        alignment: Alignment.center,
        child: child,
      ),
    );
  }
}
