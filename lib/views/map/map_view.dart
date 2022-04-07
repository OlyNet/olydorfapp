import 'package:flutter/material.dart';
import 'package:flutter_map/flutter_map.dart';
import 'package:flutter_speed_dial/flutter_speed_dial.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:latlong2/latlong.dart';
import 'package:olydorf/api/profile_helper.dart';
import 'package:olydorf/models/user_model.dart';
import 'package:olydorf/views/map/custom_polygon_plugin.dart';
import 'package:olydorf/views/map/map_data.dart';

import 'custom_polygon_options.dart';

class MapView extends StatefulHookConsumerWidget {
  const MapView({Key? key}) : super(key: key);

  @override
  _MapViewState createState() => _MapViewState();
}

enum MapType {
  none,
  osm,
  earth,
}

class _MapViewState extends ConsumerState<MapView> {
  late final MapController _mapController;

  MapType currentBaseMap = MapType.none;

  @override
  void initState() {
    super.initState();
    _mapController = MapController();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(toolbarHeight: 0),
      body: FlutterMap(
        mapController: _mapController,
        options: MapOptions(
          center: LatLng(48.17926, 11.55215),
          zoom: 18,
          plugins: [CustomPolygonPlugin()],
        ),
        layers: [
          if (currentBaseMap == MapType.osm) ...[
            TileLayerOptions(
              maxNativeZoom: 18,
              urlTemplate: "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
              subdomains: ['a', 'b', 'c'],
            ),
          ] else if (currentBaseMap == MapType.earth) ...[
            TileLayerOptions(
              maxNativeZoom: 18,
              urlTemplate:
                  "https://services.arcgisonline.com/arcgis/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}",
            ),
          ] else ...[
            TileLayerOptions(urlTemplate: ""),
          ],
          CustomPolygonLayerOptions(
            polygons: [
              for (Building building in map_data) ...[
                _customPolygon(context, building)
              ]
            ],
            polygonCulling: true,
          ),
        ],
      ),
      floatingActionButtonLocation: FloatingActionButtonLocation.startFloat,
      floatingActionButton:
          SpeedDial(icon: Icons.layers, switchLabelPosition: true, children: [
        SpeedDialChild(
          child: const Text("none"),
          onTap: () {
            changeBaseMap(MapType.none);
          },
        ),
        SpeedDialChild(
          child: const Text("map"),
          onTap: () {
            changeBaseMap(MapType.osm);
          },
        ),
        SpeedDialChild(
          child: const Text("earth"),
          onTap: () {
            changeBaseMap(MapType.earth);
          },
        ),
      ]),
    );
  }

  void changeBaseMap(MapType mapType) {
    setState(() {
      currentBaseMap = mapType;
    });
  }

  CustomPolygon _customPolygon(BuildContext context, Building building) {
    return CustomPolygon(
      label: building.label,
      labelStyle: TextStyle(
          color: currentBaseMap == MapType.earth ? Colors.white : Colors.black),
      onTap: () async {
        AppUser? user = await ProfileHelper.getBungalow(ref, building.label);
        showDialog(
            context: context,
            builder: (_) => AlertDialog(
                  title: Text(building.label),
                  content:
                      Column(children: [Text(user != null ? user.name : "")]),
                ));
      },
      points: building.points,
      color: currentBaseMap == MapType.earth ? Colors.white12 : Colors.black12,
      borderColor:
          currentBaseMap == MapType.earth ? Colors.white : Colors.black,
      borderStrokeWidth: 1,
    );
  }
}
