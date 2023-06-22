#ifndef FLUTTER_PLUGIN_WIDGET_RECORDER_PLUGIN_H_
#define FLUTTER_PLUGIN_WIDGET_RECORDER_PLUGIN_H_

#include <flutter/method_channel.h>
#include <flutter/plugin_registrar_windows.h>

#include <memory>

namespace widget_recorder {

class WidgetRecorderPlugin : public flutter::Plugin {
 public:
  static void RegisterWithRegistrar(flutter::PluginRegistrarWindows *registrar);

  WidgetRecorderPlugin();

  virtual ~WidgetRecorderPlugin();

  // Disallow copy and assign.
  WidgetRecorderPlugin(const WidgetRecorderPlugin&) = delete;
  WidgetRecorderPlugin& operator=(const WidgetRecorderPlugin&) = delete;

  // Called when a method is called on this plugin's channel from Dart.
  void HandleMethodCall(
      const flutter::MethodCall<flutter::EncodableValue> &method_call,
      std::unique_ptr<flutter::MethodResult<flutter::EncodableValue>> result);
};

}  // namespace widget_recorder

#endif  // FLUTTER_PLUGIN_WIDGET_RECORDER_PLUGIN_H_
