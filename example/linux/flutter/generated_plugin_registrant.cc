//
//  Generated file. Do not edit.
//

// clang-format off

#include "generated_plugin_registrant.h"

#include <widget_recorder/widget_recorder_plugin.h>

void fl_register_plugins(FlPluginRegistry* registry) {
  g_autoptr(FlPluginRegistrar) widget_recorder_registrar =
      fl_plugin_registry_get_registrar_for_plugin(registry, "WidgetRecorderPlugin");
  widget_recorder_plugin_register_with_registrar(widget_recorder_registrar);
}
