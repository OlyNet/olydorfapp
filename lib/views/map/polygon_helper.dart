import 'package:flutter/material.dart';

bool isPointInRect(Offset point, List<Offset> points) {
  double maxX =
      points.reduce((curr, next) => curr.dx > next.dx ? curr : next).dx;
  double minX =
      points.reduce((curr, next) => curr.dx < next.dx ? curr : next).dx;

  double maxY =
      points.reduce((curr, next) => curr.dy > next.dy ? curr : next).dy;
  double minY =
      points.reduce((curr, next) => curr.dy < next.dy ? curr : next).dy;
  return (point.dx > minX &&
      point.dx < maxX &&
      point.dy > minY &&
      point.dy < maxY);
}
