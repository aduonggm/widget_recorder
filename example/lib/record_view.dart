import 'dart:async';
import 'package:flutter/material.dart';
import 'package:widget_recorder/screen_recorder.dart';

import 'samp_animation.dart';

class RecordWidget extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({Key? key, required this.title}) : super(key: key);

  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  bool _recording = false;
  ScreenRecorderController controller = ScreenRecorderController();
  int time = 0;
  late Timer? timer;

  void _initTimer() {
    timer = Timer.periodic(const Duration(seconds: 1), (timer) {
      setState(() {
        time++;
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Column(
        children: [
          Expanded(
            child: ScreenRecorder(
                controller: controller,
                child: Column(
                  children: [
                    const SizedBox(
                      height: 10,
                    ),
                    Text(
                      time.toString(),
                      style: const TextStyle(fontSize: 30, fontWeight: FontWeight.bold, color: Colors.red),
                    ),
                    const Expanded(
                      child: SampleAnimation(),
                    ),
                  ],
                )),
          ),
          TextButton(
              onPressed: () {
                if (_recording) {
                  timer?.cancel();
                  timer = null;
                  time = 0;
                  controller.stop();
                } else {
                  _initTimer();
                  controller.start();
                }
                setState(() {
                  _recording = !_recording;
                });
              },
              child: Text(_recording ? 'stop' : "start"))

        ],
      ),
    );
  }
}
