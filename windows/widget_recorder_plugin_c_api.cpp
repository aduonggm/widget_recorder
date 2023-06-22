#include "include/widget_recorder/widget_recorder_plugin_c_api.h"

#include <flutter/plugin_registrar_windows.h>

#include "widget_recorder_plugin.h"

void WidgetRecorderPluginCApiRegisterWithRegistrar(
    FlutterDesktopPluginRegistrarRef registrar) {
  widget_recorder::WidgetRecorderPlugin::RegisterWithRegistrar(
      flutter::PluginRegistrarManager::GetInstance()
          ->GetRegistrar<flutter::PluginRegistrarWindows>(registrar));
}
