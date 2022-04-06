class AppUser {
  final String id;
  final String email;
  final String name;
  final String address;

  AppUser({
    required this.id,
    required this.email,
    required this.name,
    this.address = "",
  });

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'email': email,
      'name': name,
      'address': address,
    };
  }

  factory AppUser.fromMap(Map<String, dynamic> map) {
    return AppUser(
        id: map['id'],
        name: map['name'],
        email: map['email'],
        address: map['address'] ?? "");
  }
}
