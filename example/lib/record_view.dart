import 'dart:typed_data';
import 'dart:io';

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
  MyHomePage({Key? key, required this.title}) : super(key: key);

  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  bool _recording = false;
  bool _exporting = false;
  ScreenRecorderController controller = ScreenRecorderController();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            if (_exporting)
              Center(child: CircularProgressIndicator())
            else ...[
              ScreenRecorder(
                height: 200,
                width: 200,
                controller: controller,
                child: SampleAnimation(),
              ),
              if (!_recording && !_exporting)
                Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: ElevatedButton(
                    onPressed: () {
                      controller.start();
                      setState(() {
                        _recording = true;
                      });
                    },
                    child: Text('Start'),
                  ),
                ),
              if (_recording && !_exporting)
                Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: ElevatedButton(
                    onPressed: () {
                      controller.stop();
                      setState(() {
                        _recording = false;
                      });
                    },
                    child: Text('Stop'),
                  ),
                ),

            ]
          ],
        ),
      ),
    );
  }

}
