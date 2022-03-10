import 'package:appwrite/appwrite.dart';
import 'package:appwrite/models.dart';
import 'package:olydorf/models/user_model.dart';

// User Data stored in DB
class UserData {
  final Client client;
  late Database database;
  late Storage storage;
  late Account account;

  UserData(this.client) {
    account = Account(client);
    storage = Storage(client);
    database = Database(client);
  }

  Future<AppUser?> getCurrentUser() async {
    try {
      final user = await account.get();
      final data = await database.getDocument(
          collectionId: 'users', documentId: user.$id);
      return AppUser.fromMap(data.data);
    } catch (_) {}
  }
}
