import 'package:appwrite/models.dart';

class AppUser {
  final String id;
  final String email;
  final String name;
  final String address;
  final List<Team> teams;

  AppUser({
    required this.id,
    required this.email,
    required this.name,
    this.address = "",
    this.teams = const [],
  });

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'email': email,
      'name': name,
      'address': address,
    };
  }

  factory AppUser.fromMap(Map<String, dynamic> map, [List<Team>? teams]) {
    return AppUser(
        id: map['id'],
        name: map['name'],
        email: map['email'],
        address: map['address'] ?? "",
        teams: teams ?? []);
  }
}
