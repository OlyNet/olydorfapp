// from https://github.com/fleaflet/flutter_map/blob/master/lib/src/layer/polygon_layer.dart

import 'dart:math';
import 'dart:ui';

import 'package:flutter/widgets.dart';
import 'package:flutter_map/plugin_api.dart';
import 'package:latlong2/latlong.dart' hide Path;
import 'package:olydorf/views/map/polygon_helper.dart'; // conflict with Path from UI

class CustomPolygonLayerOptions extends LayerOptions {
  final List<CustomPolygon> polygons;
  final bool polygonCulling;

  /// screen space culling of polygons based on bounding box
  CustomPolygonLayerOptions({
    Key? key,
    this.polygons = const [],
    this.polygonCulling = false,
  }) : super(key: key) {
    if (polygonCulling) {
      for (var polygon in polygons) {
        polygon.boundingBox = LatLngBounds.fromPoints(polygon.points);
      }
    }
  }
}

class CustomPolygon {
  final List<LatLng> points;
  final List<Offset> offsets = [];
  final List<List<LatLng>>? holePointsList;
  final List<List<Offset>>? holeOffsetsList;
  final Color color;
  final double borderStrokeWidth;
  final Color borderColor;
  final bool disableHolesBorder;
  final bool isDotted;
  final String? label;
  final TextStyle labelStyle;
  late final LatLngBounds boundingBox;
  final void Function()? onTap;

  CustomPolygon({
    required this.points,
    this.holePointsList,
    this.color = const Color(0xFF00FF00),
    this.borderStrokeWidth = 0.0,
    this.borderColor = const Color(0xFFFFFF00),
    this.disableHolesBorder = false,
    this.isDotted = false,
    this.label,
    this.labelStyle = const TextStyle(),
    this.onTap,
  }) : holeOffsetsList = null == holePointsList || holePointsList.isEmpty
            ? null
            : List.generate(holePointsList.length, (_) => []);
}

class CustomPolygonLayerWidget extends StatelessWidget {
  final CustomPolygonLayerOptions options;
  const CustomPolygonLayerWidget({Key? key, required this.options})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    final mapState = MapState.maybeOf(context)!;
    return CustomPolygonLayer(options, mapState, mapState.onMoved);
  }
}

class CustomPolygonLayer extends StatelessWidget {
  final CustomPolygonLayerOptions polygonOpts;
  final MapState map;
  final Stream<void>? stream;

  CustomPolygonLayer(this.polygonOpts, this.map, this.stream)
      : super(key: polygonOpts.key);

  @override
  Widget build(BuildContext context) {
    return LayoutBuilder(
      builder: (BuildContext context, BoxConstraints bc) {
        final size = Size(bc.maxWidth, bc.maxHeight);
        return _build(context, size);
      },
    );
  }

  Widget _build(BuildContext context, Size size) {
    return StreamBuilder(
      stream: stream, // a Stream<void> or null
      builder: (BuildContext context, _) {
        var polygons = <Widget>[];

        for (var polygon in polygonOpts.polygons) {
          polygon.offsets.clear();

          if (null != polygon.holeOffsetsList) {
            for (var offsets in polygon.holeOffsetsList!) {
              offsets.clear();
            }
          }

          if (polygonOpts.polygonCulling &&
              !polygon.boundingBox.isOverlapping(map.bounds)) {
            // skip this polygon as it's offscreen
            continue;
          }

          _fillOffsets(polygon.offsets, polygon.points);

          if (null != polygon.holePointsList) {
            for (var i = 0, len = polygon.holePointsList!.length;
                i < len;
                ++i) {
              _fillOffsets(
                  polygon.holeOffsetsList![i], polygon.holePointsList![i]);
            }
          }

          polygons.add(
            GestureDetector(
              onTap: polygon.onTap ?? () {},
              child: CustomPaint(
                painter: CustomPolygonPainter(polygon),
                size: size,
              ),
            ),
          );
        }

        return Stack(
          children: polygons,
        );
      },
    );
  }

  void _fillOffsets(final List<Offset> offsets, final List<LatLng> points) {
    for (var i = 0, len = points.length; i < len; ++i) {
      var point = points[i];

      var pos = map.project(point);
      pos = pos.multiplyBy(map.getZoomScale(map.zoom, map.zoom)) -
          map.getPixelOrigin();
      offsets.add(Offset(pos.x.toDouble(), pos.y.toDouble()));
      if (i > 0) {
        offsets.add(Offset(pos.x.toDouble(), pos.y.toDouble()));
      }
    }
  }
}

class CustomPolygonPainter extends CustomPainter {
  final CustomPolygon polygonOpt;

  CustomPolygonPainter(this.polygonOpt);

  @override
  bool? hitTest(Offset position) {
    return isPointInRect(position, polygonOpt.offsets);
  }

  @override
  void paint(Canvas canvas, Size size) {
    if (polygonOpt.offsets.isEmpty) {
      return;
    }
    final rect = Offset.zero & size;
    _paintPolygon(canvas, rect);
  }

  void _paintBorder(Canvas canvas) {
    if (polygonOpt.borderStrokeWidth > 0.0) {
      var borderRadius = (polygonOpt.borderStrokeWidth / 2);

      final borderPaint = Paint()
        ..color = polygonOpt.borderColor
        ..strokeWidth = polygonOpt.borderStrokeWidth;

      if (polygonOpt.isDotted) {
        var spacing = polygonOpt.borderStrokeWidth * 1.5;
        _paintDottedLine(
            canvas, polygonOpt.offsets, borderRadius, spacing, borderPaint);

        if (!polygonOpt.disableHolesBorder &&
            null != polygonOpt.holeOffsetsList) {
          for (var offsets in polygonOpt.holeOffsetsList!) {
            _paintDottedLine(
                canvas, offsets, borderRadius, spacing, borderPaint);
          }
        }
      } else {
        _paintLine(canvas, polygonOpt.offsets, borderRadius, borderPaint);

        if (!polygonOpt.disableHolesBorder &&
            null != polygonOpt.holeOffsetsList) {
          for (var offsets in polygonOpt.holeOffsetsList!) {
            _paintLine(canvas, offsets, borderRadius, borderPaint);
          }
        }
      }
    }
  }

  void _paintDottedLine(Canvas canvas, List<Offset> offsets, double radius,
      double stepLength, Paint paint) {
    var startDistance = 0.0;
    for (var i = 0; i < offsets.length - 1; i++) {
      var o0 = offsets[i];
      var o1 = offsets[i + 1];
      var totalDistance = _dist(o0, o1);
      var distance = startDistance;
      while (distance < totalDistance) {
        var f1 = distance / totalDistance;
        var f0 = 1.0 - f1;
        var offset = Offset(o0.dx * f0 + o1.dx * f1, o0.dy * f0 + o1.dy * f1);
        canvas.drawCircle(offset, radius, paint);
        distance += stepLength;
      }
      startDistance = distance < totalDistance
          ? stepLength - (totalDistance - distance)
          : distance - totalDistance;
    }
    canvas.drawCircle(offsets.last, radius, paint);
  }

  void _paintLine(
      Canvas canvas, List<Offset> offsets, double radius, Paint paint) {
    canvas.drawPoints(PointMode.lines, [...offsets, offsets[0]], paint);
    for (var offset in offsets) {
      canvas.drawCircle(offset, radius, paint);
    }
  }

  void _paintPolygon(Canvas canvas, Rect rect) {
    final paint = Paint();

    if (null != polygonOpt.holeOffsetsList) {
      canvas.saveLayer(rect, paint);
      paint.style = PaintingStyle.fill;

      for (var offsets in polygonOpt.holeOffsetsList!) {
        var path = Path();
        path.addPolygon(offsets, true);
        canvas.drawPath(path, paint);
      }

      paint
        ..color = polygonOpt.color
        ..blendMode = BlendMode.srcOut;
    } else {
      canvas.clipRect(rect);
      paint
        ..style = PaintingStyle.fill
        ..color = polygonOpt.color;
    }

    var path = Path();
    path.addPolygon(polygonOpt.offsets, true);
    canvas.drawPath(path, paint);

    _paintBorder(canvas);

    if (polygonOpt.label != null) {
      _paintLabel(canvas);
    }

    if (polygonOpt.holeOffsetsList != null) {
      canvas.restore();
    }
  }

  void _paintLabel(Canvas canvas) {
    double maxX = polygonOpt.offsets
        .reduce((curr, next) => curr.dx > next.dx ? curr : next)
        .dx;
    double minX = polygonOpt.offsets
        .reduce((curr, next) => curr.dx < next.dx ? curr : next)
        .dx;
    double maxWidth = maxX - minX;
    double maxY = polygonOpt.offsets
        .reduce((curr, next) => curr.dy > next.dy ? curr : next)
        .dy;
    double minY = polygonOpt.offsets
        .reduce((curr, next) => curr.dy < next.dy ? curr : next)
        .dy;

    final textSpan = TextSpan(
        text: polygonOpt.label,
        style: polygonOpt.labelStyle
            .copyWith(fontSize: (maxWidth) / polygonOpt.label!.length));
    final textPainter = TextPainter(
      text: textSpan,
      textAlign: TextAlign.center,
      textDirection: TextDirection.ltr,
    );
    textPainter.layout(minWidth: 0, maxWidth: maxWidth);

    // calculate center of polygon - text width and height
    double centerX = minX + (maxWidth - textPainter.width) / 2;
    double centerY = minY + (maxY - minY - textPainter.height) / 2;

    textPainter.paint(
      canvas,
      Offset(centerX, centerY),
    );
  }

  @override
  bool shouldRepaint(CustomPolygonPainter oldDelegate) => false;

  double _dist(Offset v, Offset w) {
    return sqrt(_dist2(v, w));
  }

  double _dist2(Offset v, Offset w) {
    return _sqr(v.dx - w.dx) + _sqr(v.dy - w.dy);
  }

  double _sqr(double x) {
    return x * x;
  }
}
