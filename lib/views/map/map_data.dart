import 'package:latlong2/latlong.dart';

class Building {
  String label;
  List<LatLng> points;
  Building({required this.label, required this.points});
}

List map_data = [
  Building(
    label: "B13",
    points: [
      LatLng(48.179139, 11.5538915),
      LatLng(48.1791103, 11.5538916),
      LatLng(48.1791104, 11.5539494),
      LatLng(48.1791391, 11.5539493),
    ],
  ),
];
