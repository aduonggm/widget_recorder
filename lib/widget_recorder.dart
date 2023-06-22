
import 'widget_recorder_platform_interface.dart';

class WidgetRecorder {

  Future<String?> getPlatformVersion() {
    return WidgetRecorderPlatform.instance.getPlatformVersion();
  }
  
  Future<bool> startRecord(int width, int height){
    return WidgetRecorderPlatform.instance.startRecord(width, height);
  }
}
