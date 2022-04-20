import 'dart:typed_data';

class Event {
  late String name;
  String? description;
  String? location;
  DateTime? date;
  String? imgId;
  Uint8List? image;

  Event({
    required this.name,
    this.description,
    this.location,
    this.date,
    this.imgId,
    this.image,
  });

  Map<String, dynamic> toMap() {
    return {
      'name': name,
      'description': description,
      'location': location,
      'date': date?.toIso8601String(),
      'imgId': imgId,
    };
  }

  factory Event.fromMap(Map<String, dynamic> map) {
    return Event(
      name: map['name'],
      description: map['description'],
      location: map['location'],
      date: map['date'] != null ? DateTime.parse(map['date']) : null,
      imgId: map['imgId'],
    );
  }
}
