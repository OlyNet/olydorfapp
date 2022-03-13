

def polygon_center(polygon: list) -> tuple[float, float]:
    lats = [i[0] for i in polygon]
    lngs = [i[1] for i in polygon]

    center_lat = min(lats) + ((max(lats) - min(lats)) / 2)
    center_lng = min(lngs) + ((max(lngs) - min(lngs)) / 2)

    return center_lat, center_lng


if __name__ == "__main__":
    bungalow = [
        [48.179139, 11.5538915],
        [48.1791103, 11.5538916],
        [48.1791104, 11.5539494],
        [48.1791391, 11.5539493],
        [48.179139, 11.5538915],
    ]

    print(polygon_center(bungalow))
