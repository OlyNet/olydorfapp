// from https://github.com/molteo-engineering-team/point_in_polygon/blob/main/lib/point_in_polygon.dart

import 'package:flutter/material.dart';

bool isOffsetInPolygon(Offset point, List<Offset> vertices) {
  int intersectCount = 0;
  for (int i = 0; i < vertices.length; i += 1) {
    final Offset vertB =
        i == vertices.length - 1 ? vertices[0] : vertices[i + 1];
    if (rayCastIntersect(point, vertices[i], vertB)) {
      intersectCount += 1;
    }
  }
  return (intersectCount % 2) == 1;
}

bool rayCastIntersect(Offset point, Offset vertA, Offset vertB) {
  final double aY = vertA.dy;
  final double bY = vertB.dy;
  final double aX = vertA.dx;
  final double bX = vertB.dx;
  final double pY = point.dy;
  final double pX = point.dx;

  if ((aY > pY && bY > pY) || (aY < pY && bY < pY) || (aX < pX && bX < pX)) {
    return false;
  }
  final double m = (aY - bY) / (aX - bX);
  final double b = ((aX * -1) * m) + aY;
  final double x = (pY - b) / m;
  return x > pX;
}
