import 'package:flutter/material.dart';
import 'package:flutter_map/plugin_api.dart';
import 'package:olydorf/views/map/custom_polygon_options.dart';

class CustomPolygonPlugin extends MapPlugin {
  @override
  Widget createLayer(
      LayerOptions options, MapState mapState, Stream<void> stream) {
    return CustomPolygonLayer(
        options as CustomPolygonLayerOptions, mapState, stream);
  }

  @override
  bool supportsLayer(LayerOptions options) {
    return options is CustomPolygonLayerOptions;
  }
}
