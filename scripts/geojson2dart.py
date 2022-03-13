import json


def polygon_center(polygon: list) -> tuple[float, float]:
    lats = [i[0] for i in polygon]
    lngs = [i[1] for i in polygon]

    center_lat = min(lats) + ((max(lats) - min(lats)) / 2)
    center_lng = min(lngs) + ((max(lngs) - min(lngs)) / 2)

    return center_lat, center_lng


def test_polygon_center():
    bungalow = [
        [48.179139, 11.5538915],
        [48.1791103, 11.5538916],
        [48.1791104, 11.5539494],
        [48.1791391, 11.5539493],
        [48.179139, 11.5538915],
    ]

    print(polygon_center(bungalow))


start_str = """
import 'package:latlong2/latlong.dart';

class Building {
  String label;
  List<LatLng> points;
  Building({required this.label, required this.points});
}

List map_data = [
"""
end_str = "];"


def geojson2dart():
    with open('map_data.dart', 'w') as out_file:
        out_file.write(start_str)
        with open('map_data/olydorf.geojson', 'r') as file:
            data = json.load(file)
            for building in data['features']:
                if "addr:unit" in building['properties']:

                    coords = building['geometry']['coordinates'][0]
                    if len(coords) == 5:
                        building_start_str = f"""
                        Building(
                            label: "{building['properties']["addr:unit"]}",
                            points: [
                        """
                        out_file.write(building_start_str)

                        for i in range(4):
                            out_file.write(
                                f"LatLng({coords[i][1]}, {coords[i][0]}),")

                        out_file.write("],),")
        out_file.write(end_str)


if __name__ == "__main__":
    geojson2dart()
