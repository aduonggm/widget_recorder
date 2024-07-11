import 'dart:ui' as ui show ImageByteFormat;

import 'package:flutter/foundation.dart';
import 'package:widget_recorder/widget_recorder.dart';

import 'frame.dart';

class Exporter {
  final List<Frame> _frames = [];
  final WidgetRecorder widgetRecorder = WidgetRecorder();

  List<Frame> get frames => _frames;

  Future onNewFrame(Frame frame) async {
    print("on receive new frame     ");
    _frames.add(frame);
    ByteData? byteData = await frame.image.toByteData(format: ui.ImageByteFormat.png);
    Uint8List? uint8List = byteData?.buffer.asUint8List();
    print("on receive new frame  (uint8List != null):${(uint8List != null)}    ");
    if (uint8List != null) {
      widgetRecorder.sendFrame(uint8List, _frames.length);
    }
  }

  void clear() {
    _frames.clear();
  }

  bool get hasFrames => _frames.isNotEmpty;

  Future<List<RawFrame>?> exportFrames() async {
    if (_frames.isEmpty) {
      return null;
    }
    final bytesImages = <RawFrame>[];
    for (final frame in _frames) {
      final bytesImage = await frame.image.toByteData(format: ui.ImageByteFormat.png);
      if (bytesImage != null) {
        bytesImages.add(RawFrame(16, bytesImage));
      } else {
        print('Skipped frame while enconding');
      }
    }
    return bytesImages;
  }

// Future<List<int>?> exportGif() async {
//   final frames = await exportFrames();
//   if (frames == null) {
//     return null;
//   }
//   return compute(_exportGif, frames);
// }

// static Future<List<int>?> _exportGif(List<RawFrame> frames) async {
//   final animation = image.Animation();
//   animation.backgroundColor = Colors.transparent.value;
//   for (final frame in frames) {
//     final iAsBytes = frame.image.buffer.asUint8List();
//     final decodedImage = image.decodePng(iAsBytes);
//
//     if (decodedImage == null) {
//       print('Skipped frame while enconding');
//       continue;
//     }
//     decodedImage.duration = frame.durationInMillis;
//     animation.addFrame(decodedImage);
//   }
//   return image.encodeGifAnimation(animation);
// }
}

class RawFrame {
  RawFrame(this.durationInMillis, this.image);

  final int durationInMillis;
  final ByteData image;
}
