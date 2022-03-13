import 'package:flutter/material.dart';
import 'package:flutter_map/flutter_map.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:latlong2/latlong.dart';

class MapView extends StatefulHookConsumerWidget {
  MapView({Key? key}) : super(key: key);

  @override
  _MapViewState createState() => _MapViewState();
}

class _MapViewState extends ConsumerState<MapView> {
  MapController? _mapController;

  @override
  void initState() {
    super.initState();
    _mapController = MapController();
  }

  @override
  Widget build(BuildContext context) {
    return Center(
      child: FlutterMap(
        options: MapOptions(
          center: LatLng(48.17926, 11.55215),
          zoom: 18,
        ),
        mapController: _mapController,
        layers: [
          TileLayerOptions(
            urlTemplate: "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
            subdomains: ['a', 'b', 'c'],
          ),
          PolygonLayerOptions(polygons: [
            Polygon(
                points: [
                  LatLng(48.179139, 11.5538915),
                  LatLng(48.1791103, 11.5538916),
                  LatLng(48.1791104, 11.5539494),
                  LatLng(48.1791391, 11.5539493),
                  LatLng(48.179139, 11.5538915),
                ],
                color: Colors.black12,
                borderColor: Colors.black,
                borderStrokeWidth: 1)
          ]),
          MarkerLayerOptions(
            markers: [
              Marker(
                width: 25,
                height: 20,
                point: LatLng(48.1791247, 11.55392045),
                builder: (ctx) => TextButton(
                  style: ButtonStyle(
                      // fixedSize: MaterialStateProperty<Size>(Size(25,10)),
                      shape: MaterialStateProperty.all<RoundedRectangleBorder>(
                        const RoundedRectangleBorder(
                          borderRadius: BorderRadius.zero,
                          side: BorderSide(color: Colors.red),
                        ),
                      ),
                      padding: MaterialStateProperty.all<EdgeInsets>(
                          EdgeInsets.zero)),
                  onPressed: (() {
                    showDialog(
                        context: ctx,
                        builder: (_) => const AlertDialog(
                              title: Text("B13"),
                            ));
                  }),
                  child: const Text(
                    'B13',
                    style: TextStyle(fontSize: 5),
                  ),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}
