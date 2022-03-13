import 'package:flutter/material.dart';
import 'package:flutter_map/flutter_map.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:latlong2/latlong.dart';
import 'package:olydorf/views/map/custom_polygon_plugin.dart';
import 'package:olydorf/views/map/map_data.dart';

import 'custom_polygon_options.dart';

class MapView extends HookConsumerWidget {
  const MapView({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return Center(
      child: FlutterMap(
        options: MapOptions(
            center: LatLng(48.17926, 11.55215),
            zoom: 18,
            plugins: [CustomPolygonPlugin()]),
        layers: [
          TileLayerOptions(
            urlTemplate: "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
            subdomains: ['a', 'b', 'c'],
          ),
          CustomPolygonLayerOptions(polygons: [
            for (Building building in map_data) ...[
              _customPolygon(context, building)
            ]
          ]),
        ],
      ),
    );
  }

  CustomPolygon _customPolygon(BuildContext context, Building building) {
    return CustomPolygon(
      label: building.label,
      onTap: () {
        showDialog(
            context: context,
            builder: (_) => AlertDialog(
                  title: Text(building.label),
                ));
      },
      points: building.points,
      color: Colors.black12,
      borderColor: Colors.black,
      borderStrokeWidth: 1,
    );
  }
}
